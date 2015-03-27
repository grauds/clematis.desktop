package jworkspace.network.server;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
   Authors may be contacted at:
   larsyde@diku.dk
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
 */

import java.io.*;
import java.util.*;

import com.sun.media.jsdt.*;
import com.sun.media.jsdt.event.*;
import jworkspace.kernel.*;
import jworkspace.network.*;
import jworkspace.network.datagram.*;
import jworkspace.network.server.security.*;
import jworkspace.network.server.group.GroupManager;
import jworkspace.network.server.group.GroupSessionListener;
import jworkspace.network.server.content.ContentManager;
import jworkspace.util.WorkspaceError;

import kiwi.util.plugin.*;
import kiwi.util.*;

/**
 * Collaboration group server. Contains one administrative
 * jsdt session and channel within, groups and they sessions and
 * channels, shared resources and tokens. This is also a
 * Java Workspace plugin and thence an entry point to start server
 * in Java Workspace container.
 */
public class Server implements IWorkspaceListener
{
    /**
     * Instance of group management server.
     */
    private ServerManager serverManager = null;
    /**
     * Instance of group management session listener
     */
    private GMSessionListener gml = null;
    /**
     * Instance of group management channel listener
     */
    private GMChannelListener gmchl = null;
    /**
     * Session info object
     */
    private ServerInfo serverInfo = null;
    /**
     * Group management listener is used to indicate the join and
     * leave of GM_MANAGEMENT_SESSION
     */
    public class GMSessionListener extends SessionAdaptor
    {
        /**
         * Public constructor
         */
        public GMSessionListener()
        {
            super();
        }

        /**
         * User joined GM session.
         */
        public void sessionJoined(SessionEvent se)
        {
            Workspace.getLogger().info("User " + se.getClientName() +
                               " joined group management session");
        }

        /**
         * User left session. Sends notification to all users
         * that current user has left server.
         */
        public void sessionLeft(SessionEvent se)
        {
            Workspace.getLogger().info("User " + se.getClientName() +
                               " left group management session");
        }
    }

    /**
     * Group management listener is used to indicate the join and
     * leave of GM_MANAGEMENT_CHANNEL
     */
    public class GMChannelListener extends ChannelAdaptor
    {
        /**
         * Public constructor
         */
        public GMChannelListener()
        {
            super();
        }

        /**
         * User joined GM channel.
         */
        public void channelJoined(ChannelEvent se)
        {
            Workspace.getLogger().info("User " + se.getClientName() +
                               " joined group management channel");
            /**
             * Send notification to others that user joined server.
             */
            DataPackage outgoingPackage = new DataPackage();
            outgoingPackage.setContents("User " + se.getClientName() +
                                        " joined channel");
            outgoingPackage.getProperties().setProperty
                    (DataPackage.DESCRIPTION, ServerInfo.CK_USER_JOINED_SERVER);

            try
            {
                getServerInfo().getGroupManagementChannel().sendToOthers
                        (getServerManager(), new Data(outgoingPackage));
            }
            catch (JSDTException ex)
            {
                ex.printStackTrace();
            }
        }

        /**
         * User left GM channel.
         */
        public void channelLeft(ChannelEvent se)
        {
            Workspace.getLogger().info("User " + se.getClientName() +
                               " left group management channel");
            if (se.getClientName().equals(ServerInfo.DEFAULT_BOT_NAME))
            {
                return;
            }
            /**
             * Send notification to others that user joined server.
             */
            DataPackage outgoingPackage = new DataPackage();
            outgoingPackage.setContents("User " + se.getClientName() +
                                        " left channel");
            outgoingPackage.getProperties().setProperty
                    (DataPackage.DESCRIPTION, ServerInfo.CK_USER_LEFT_SERVER);

            try
            {
                getServerInfo().getGroupManagementChannel().sendToOthers
                        (getServerManager(), new Data(outgoingPackage));
            }
            catch (JSDTException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Plugin public constructor
     */
    public Server(Plugin plugin)
    {
        super();
    }
 //****************************  Server life cycle *************************
    /**
     * Load session manager at server startup and create
     * group management session and channel.
     */
    public void load()
    {
        serverInfo = new ServerInfo();
        serverInfo.readPreferences();
        try
        {
            startRegistry();
        }
        catch (ServerOperationException e)
        {
            Workspace.getLogger().warning(e.getMessage());
            return;
        }
        catch (Error err)
        {
            WorkspaceError.exception("Unexpected network error. May be TCP/IP protocols are not installed on your PC", err);
            return;
        }
        create();
        readGroups();
    }

    public void reset() { }

    /**
     * Save groups, stop registry and shut down server.
     * @throws IOException
     */
    public void save() throws IOException
    {
        serverInfo.writePreferences();
        writeGroups();
        destroy();
        stopRegistry();
        serverInfo = null;
    }

    /**
     * This method actually saves data in plugins
     */
    public void dispose()
    {
        try
        {
            save();
        }
        catch (IOException ex)
        {
            Workspace.getLogger().warning(">Exception - Cannot save network server config");
        }
    }

    /**
     * Setup the group management session and channel and activate it.
     * The group managemenet session is a main session for the whole server.
     */
    public void create()
    {
        /**
         * Session and channel for the group management.
         */
        Session session = null;
        Channel channel = null;
        /**
         * Get server config
         */
        Config serverConfig = ServerInfo.getPreferences();
        try
        {
            /**
             * Create session url for group management server.
             */
            URLString url = URLString.createSessionURL
                    (
                            serverConfig.getProperty(ServerInfo.CK_SERVER_NAME),
                            Integer.parseInt(serverConfig.getProperty(ServerInfo.CK_SERVER_PORT)),
                            serverConfig.getProperty(ServerInfo.CK_SERVER_TYPE),
                            serverConfig.getProperty(ServerInfo.CK_MANAGEMENT_SESSION_NAME)
                    );
            /**
             * Created a managed session.
             */
            session = SessionFactory.createSession
                    (getServerManager(), url, true, new ServerUsersManager());
            /**
             * Create a data transfer channel within a server session.
             */
            channel = session.createChannel(getServerManager(),
                                            serverConfig.getProperty(ServerInfo.CK_MANAGEMENT_CHANNEL_NAME),
                                            true, true, true);
            /**
             * Group management server is a consumer for this channel.
             */
            channel.addConsumer(getServerManager(),
                                getServerManager());
            Workspace.getLogger().info("Group management server " +
                               serverManager.getName() + " started");
        }
        catch (JSDTException ex)
        {
            ex.printStackTrace();
            return;
        }
        /**
         * Install session listener and channel listener.
         */
        try
        {
            gml = new GMSessionListener();
            session.addSessionListener(gml);
            gmchl = new GMChannelListener();
            channel.addChannelListener(gmchl);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        /**
         * Register session and channel in server info object.
         */
        getServerInfo().setGroupManagementSession(session);
        getServerInfo().setGroupManagementChannel(channel);
    }

    /**
     * Destroy the group management.
     */
    public void destroy()
    {
        try
        {
            /**
             * Remove session and channel listeners.
             */
            getServerInfo().getGroupManagementSession().removeSessionListener(gml);
            gml = null;
            getServerInfo().getGroupManagementChannel().removeChannelListener(gmchl);
            gmchl = null;
            /**
             * Shut down session and channel
             */
            getServerInfo().getGroupManagementChannel().destroy
                    (getServerManager());
            getServerInfo().getGroupManagementSession().close(true);
            getServerInfo().setGroupManagementSession(null);
            getServerInfo().setGroupManagementChannel(null);
            serverManager = null;
        }
        catch (JSDTException jsdtex)
        {
            jsdtex.printStackTrace();
        }
    }
//**************************** Server Info ************************

    /**
     * Creates and returns an instance of group management
     * server, which has the same name as for machine.
     */
    public ServerManager getServerManager()
    {
        if (serverManager == null)
        {
            serverManager = new ServerManager(this);
            serverManager.setName(ServerInfo.getPreferences().getProperty(ServerInfo.CK_BOT_NAME));
        }
        return serverManager;
    }

    /**
     * Create and return new ServerInfo object
     */
    public ServerInfo getServerInfo()
    {
        if (serverInfo == null)
        {
            serverInfo = new ServerInfo();
        }
        return serverInfo;
    }

//****************************  Registry  *************************
    /**
     * Start JSDT Registry. Class ServerInfo ideally fits with
     * runtime parameters like registry port, connection type, etc.
     */
    public void startRegistry() throws ServerOperationException
    {
        /**
         * Get server config
         */
        Config serverConfig = ServerInfo.getPreferences();
        /**
         * Try to start registry according to server config
         */
        try
        {
            /**
             * Set the port of registry
             */
            RegistryFactory.registryPort = Integer.parseInt
                    (serverConfig.getProperty(ServerInfo.CK_REGISTRY_PORT));
            /**
             * Start registry on specified port
             */
            RegistryFactory.startRegistry
                    (serverConfig.getProperty(ServerInfo.CK_REGISTRY_TYPE),
                     Integer.parseInt(serverConfig.getProperty(ServerInfo.CK_REGISTRY_PORT)));
            /**
             * Check if our registry exists.
             */
            if (RegistryFactory.registryExists
                    (serverConfig.getProperty(ServerInfo.CK_REGISTRY_TYPE),
                     Integer.parseInt(serverConfig.getProperty(ServerInfo.CK_REGISTRY_PORT))))
            {
                Workspace.getLogger().info("Successfully started registry");
            }
        }
        catch (com.sun.media.jsdt.NoRegistryException nre)
        {
            Workspace.getLogger().warning("No registry exception " + nre.getMessage());
            nre.printStackTrace();
        }
        catch (com.sun.media.jsdt.RegistryExistsException ree)
        {
            Workspace.getLogger().warning("Registry already exists " + ree.getMessage());
            // if we are here, the second instance of registry is attempted to be started on
            // the same machine, throw Server Operation Exception
            throw new ServerOperationException("Another copy of Java Workspace Group Server is running on this machine");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Workspace.getLogger().warning(ex.toString());
        }
    }

    /**
     * Stops registry
     */
    public void stopRegistry()
    {
        /**
         * Get server config
         */
        Config serverConfig = ServerInfo.getPreferences();
        /**
         * Try to stop the registry.
         */
        try
        {
            RegistryFactory.stopRegistry
                    (serverConfig.getProperty(ServerInfo.CK_REGISTRY_TYPE),
                     Integer.parseInt(serverConfig.getProperty
                                      (ServerInfo.CK_REGISTRY_PORT)));
            Workspace.getLogger().info("Registry is stopped");
        }
        catch (Exception ex)
        {
            Workspace.getLogger().warning(ex.toString());
        }
    }
//***************************  Groups Management *****************************
    /**
     * Adds a group to server. The group should be already created.
     * After the group is added, the new list of groups is broadcasted.
     */
    public void addGroup(UserGroup usersGroup) throws ServerOperationException
    {
        /**
         * Check to see if user group already exists
         */
        if ( serverInfo.getGroups().get(usersGroup.getUsersGroupID()) == null )
        {
            serverInfo.getGroups().put(usersGroup.getUsersGroupID(), usersGroup);
            activateGroup(usersGroup);
            storeGroup(usersGroup);
        }
        else
        {
            throw new ServerOperationException("Group already exists");
        }
    }

   /**
     * Traverses the group directory (list of active groups)
     * to determine if any group matches the group name and
     * description parameters
     * @return boolean
     * @param groupName java.lang.String
     */
    public UserGroup findGroup(String groupName)
    {
        Hashtable groups = getServerInfo().getGroups();

        Enumeration i = groups.elements();
        while (i.hasMoreElements())
        {
            UserGroup ug = (UserGroup)i.nextElement();
            if (ug.getName().equals(groupName))
            {
                return ug;
            }
        }
        return null;
    }

    /**
     * Traverses the group directory (list of active groups)
     * to determine if any group matches the group url
     * @return boolean
     * @param url java.lang.String
     */
    public UserGroup findGroup(URLString url)
    {
        Hashtable groups = getServerInfo().getGroups();

        Enumeration i = groups.elements();
        while (i.hasMoreElements())
        {
            UserGroup ug = (UserGroup)i.nextElement();
            if (ug.getSessionURL().equals(url))
            {
                return ug;
            }
        }
        return null;
    }

    /**
     * Remove users group from the list of groups on server
     */
    public void removeGroup(UserGroup ug) throws ServerOperationException
    {
        if (ug.getUsers().size() != 0)
        {
            throw new ServerOperationException("Group contains users.");
        }
        else
        {
            deactivateGroup(ug);
            getServerInfo().getGroups().remove(ug.getUsersGroupID());
            wipeGroup(ug);
        }
    }

    /**
     * Activates given group. This means that group should have
     * its own group management session and channel.
     */
    public void activateGroup(UserGroup usersGroup) throws ServerOperationException
    {
        /**
         * Get server config
         */
        Config serverConfig = ServerInfo.getPreferences();
        /**
         * The group session
         */
        Session session = null;
        URLString url = null;
        /**
         * Create an instance of group management server
         */
        GroupManager gm = new GroupManager(new ContentManager(usersGroup));
        /**
         * Try to activate group
         */
        try
        {
            url = URLString.createSessionURL
                    (
                            serverConfig.getProperty(ServerInfo.CK_SERVER_NAME),
                            Integer.parseInt(serverConfig.getProperty(ServerInfo.CK_SERVER_PORT)),
                            serverConfig.getProperty(ServerInfo.CK_SERVER_TYPE),
                            usersGroup.getUsersGroupID()
                    );
            /**
             * Create group session with a given URL.
             */
            session = SessionFactory.createSession
                  (gm, url, true, new GroupUsersManager(usersGroup.getName()));
            /**
             * Add a session listener to a group session
             */
            session.addSessionListener
                    (new GroupSessionListener(usersGroup.getUsersGroupID(), this));
            /**
             * Create a data transfer channel within a group session.
             */
            Channel channel = session.createChannel
                    (gm, usersGroup.getUsersGroupChannelID(), true, true, true);

            channel.addConsumer(gm, gm);
            /**
             * Each group stores its url. Is it correct?
             */
            usersGroup.setSessionURL(url);
            Workspace.getLogger().info("Group " + url.toString() + " is activated");
        }
        catch (JSDTException ex)
        {
           throw new ServerOperationException("Cannot activate group", ex);
        }
    }

    /**
     * Shut down a group.
     */
    public void deactivateGroup(UserGroup usersGroup) throws ServerOperationException
    {
        try
        {
            URLString url = usersGroup.getSessionURL();
            Session session = SessionFactory.createSession(
                    getServerManager(), url, false);
            Channel channel = session.createChannel(
                    getServerManager(), usersGroup.getUsersGroupChannelID(),
                    true, true, false);
            channel.destroy(getServerManager());
            session.destroy(getServerManager());
        }
        catch (JSDTException ex)
        {
           throw new ServerOperationException("Cannot deactivate group", ex);
        }
    }

   /**
     * Read the groups directory from persistent storage, i.e. disk file
     */
    public void readGroups()
    {
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append("groups");
        path.append(File.separator);

        File file = new File(path.toString());
        if ( !file.exists() )
        {
           return;
        }
        // get list of groups as subdirectories
        String[] children = file.list();

        for ( int i = 0; i < children.length; i++)
        {
            // got complete path to group config file
            file = new File(path.toString() +
                            children[i] +
                            File.separator +
                            ServerInfo.GROUPS_FILE_NAME );

            // this check will pass only directories with valid files
            if ( !file.exists() )
            {
               continue;
            }

            try
            {
               FileInputStream fis = new FileInputStream(file);
               byte[] block = new byte[1024];
               StringBuffer sb = new StringBuffer();

               while (  fis.read( block ) != -1)
               {
                   sb.append( new String(block));
               }
               // read group in.
               String xml = sb.toString().trim();
               UserGroup ug = UserGroup.create(xml);
               getServerInfo().getGroups().put(ug.getUsersGroupID(), ug);
               activateGroup(ug);
               fis.close();
             }
             catch (Exception ex)
             {
               continue;
             }
         }
    }

    /**
     * Remove group data recursively from disk
     * if group is removed.
     */
    private void storeGroup(UserGroup ug)
    {
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append("groups");
        path.append(File.separator);
        path.append(ug.getName());
        path.append(File.separator);

        File file = new File(path.toString());
        if ( !file.exists() )
        {
           file.mkdirs();
        }

        path.append(ServerInfo.GROUPS_FILE_NAME);
        file = new File(path.toString());
        try
        {
          // write
          String content = ug.toXString();
          FileOutputStream fos = new FileOutputStream(file);

          fos.write( content.getBytes(), 0, content.length());

          fos.flush();
          fos.close();
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
          Workspace.getLogger().warning(ex.toString());
        }
    }

    /**
     * Remove group data recursively from disk
     * if group is removed.
     */
    private void wipeGroup(UserGroup ug)
    {
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append("groups");
        path.append(File.separator);
        path.append(ug.getName());
        path.append(File.separator);

        KiwiUtils.deleteTree( new File(path.toString()) );
    }

    /**
     * Write registered groups to persistent storage, i.e. disk file
     */
    public void writeGroups()
    {
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append("groups");
        path.append(File.separator);

        Collection values = getServerInfo().getGroups().values();
        Iterator iterator = values.iterator();

        while (iterator.hasNext())
        {
           // get user group and deactivate it
           UserGroup ug = (UserGroup)iterator.next();

           String groupName = ug.getName();
           // make group directory
           File file = new File(path.toString() + groupName + File.separator);
           if ( !file.exists() )
           {
               file.mkdirs();
           }

           file = new File(path.toString() + groupName + File.separator +
                           ServerInfo.GROUPS_FILE_NAME);

           try
           {
             // deactivate
             deactivateGroup(ug);
             // write
             String content = ug.toXString();
             FileOutputStream fos = new FileOutputStream(file);

             fos.write( content.getBytes(), 0, content.length());

             fos.flush();
             fos.close();
           }
           catch (IOException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
           catch (ServerOperationException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
        }
    }

    /**
     * Process event as Workspace Events listener
     */
    public void processEvent(Object event, Object lparam, Object rparam)
    {
        // do some advanced stuff here
    }

//*************************** Other stuff *****************************

    /**
     * Forcibly expel client from group management session
     * if it has not already left. This to ensure consistency when clients
     * terminate abruptly, i.e. do not exit properly.
     * @param userName java.lang.String
     */
    private void removeUser(String userName) throws ServerOperationException
    {
        try
        {
            String[] clientNames =
                    getServerInfo().getGroupManagementSession().listClientNames();

            for (int i = 0; i < clientNames.length; i++)
            {
                if (clientNames[i].equalsIgnoreCase(userName))
                {
                    Client tempClient = ClientFactory.lookupClient
                            (URLString.createClientURL
                             (ServerInfo.CK_SERVER_NAME,
                              Integer.parseInt(ServerInfo.CK_SERVER_PORT),
                              ServerInfo.CK_SERVER_TYPE, userName));
                    if (tempClient != null)
                    {
                        getServerInfo().getGroupManagementSession().leave(tempClient);
                        Workspace.getLogger().info("> Server reports " + userName
                                           + " has left. Ping timeout.");
                    }
                    break;
                }
            }
        }
        catch (JSDTException ex)
        {
           throw new ServerOperationException("Cannot remove user", ex);
        }
    }
}