package jworkspace.network.user;

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
import java.util.logging.Level;

import com.sun.media.jsdt.*;
import com.sun.media.jsdt.event.*;

import jworkspace.kernel.*;
import jworkspace.network.*;
import jworkspace.network.user.group.GroupAgent;
import jworkspace.network.user.group.GroupMessageHandler;
import jworkspace.network.datagram.*;
import jworkspace.util.*;

import kiwi.util.plugin.*;
import kiwi.util.ResourceLoader;
import kiwi.ui.dialog.LoginDialog;
import kiwi.event.DialogDismissListener;
import kiwi.event.DialogDismissEvent;

import org.eldos.MsgConnect.MCBase.MCQueue;
import org.eldos.MsgConnect.MCBase.MCMessage;
import org.eldos.MsgConnect.MCBase.EMCError;

import javax.swing.*;
/**
 * The main control class for the client application.
 * This class holds methods and control structures
 * shared by several client application components.
 * Note: User can connect only to one server at a time.
 */
public class User implements DialogDismissListener
{
    /**
     * Login dialog
     */
    protected static LoginDialog loginDlg = null;
    /**
     * User information
     */
    private UserInfo userInfo = null;
    /**
     * Group agents. Each agent instance is for
     * one group plus attached message queue to talk to GUI.
     */
    private Hashtable agents = new Hashtable();
    /**
     * Event handler for JSDT connections.
     * Ensures automatic notification in case of connection failure
     */
    public class ServerConnectionListener implements ConnectionListener
    {
        public void connectionFailed(ConnectionEvent ce)
        {
            Workspace.getLogger().log(Level.WARNING, "Connection failed", ce);
        }
    };
    /**
     * Empty public constructor.
     */
    public User()
    {
        super();
        /**
         * Attach message handler to workspace
         * system message queue
         */
       Messenger.addMessageHandler(new UserMessageHandler(this));
    }
    /**
     * Plugin public constructor
     */
    public User(Plugin plugin)
    {
        this();
    }
    /**
     * Creates once and returns instance of user info
     * specifically for this user.
     */
    public UserInfo getUserInfo()
    {
        if (userInfo == null)
        {
            userInfo = new UserInfo();
        }
        return userInfo;
    }
 //************** Set alternative credentials *********//
    /**
     * Set alternative credentials for this user
     * @param userName alt user name
     * @param passwd alt passwd
     */
    public void setAltCredentials(String userName, String passwd)
    {
        UserInfo.getPreferences().putString(UserConstants.CK_ALT_USER_NAME, userName);
        UserInfo.getPreferences().putString(UserConstants.CK_ALT_USER_PASSWORD, passwd);
    }
    /**
     * Set alternative credentials for this user
     * @param userName alt user name
     */
    public void setAltCredentials(String userName)
    {
        UserInfo.getPreferences().putString(UserConstants.CK_ALT_USER_NAME, userName);
    }
 //************** Create server agent ******************//
    /**
     * Create or return a cached copy of server agent
     * @return a server agent instance
     */
    public ServerAgent getServerAgent()
    {
      ServerAgent agent = null;
      if ( agents.containsKey(UserInfo.CK_MANAGEMENT_CHANNEL_NAME) )
      {
        agent = (ServerAgent) agents.get(UserInfo.CK_MANAGEMENT_CHANNEL_NAME);
      }
      else
      {
        agent = new ServerAgent( this );
        agents.put(UserInfo.CK_MANAGEMENT_CHANNEL_NAME, agent);
      }
      return agent;
    }
 //************** Server join and leave procedures ****//
    /**
     * Join server as a workspace user
     */
    public void joinServer()
    {
       joinServer(false);
    }
    /**
     * Join server with alternative credentials
     */
    public void joinServerAlt()
    {
       joinServerAs
               ( UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_NAME),
                 UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_PASSWORD) );
    }
    /**
     * Join server as....
     */
    public void joinServerAs(String name, String password)
    {
        if ( name == null || password == null )
        {
            sendFeedback("Alternative credentials are not supplied");
            return;
        }
        if ( name.trim().equals("") || password.trim().equals("") )
        {
            sendFeedback("Alternative credentials are empty");
            return;
        }
        /**
         * Tune group management server agent to use
         * alternative credentials.
         */
        setAltCredentials( name, password);
        /**
         * Join server
         */
        joinServer( true );
    }
    /**
     * Connect the client to the server via the group management session.
     */
    public void joinServer(boolean altCredentials)
    {
        /**
         * If user info does not contain any reference to a GM session,
         * it means our client is still offline. Online clients are prompted to
         * disconnect first.
         */
        if (getUserInfo().getGroupManagementSession() == null)
        {
            /**
             * Group Management
             */
            Session session;
            Channel channel;
            /**
             * Create agent for server group management session.
             */
            ServerAgent agent = getServerAgent();
            agent.setUseAltNick(altCredentials);
            try
            {
                /**
                 * Construct server URL
                 */
                RegistryFactory.registryPort = Integer.parseInt(UserInfo.
                                                                getPreferences().getProperty(UserInfo.CK_REGISTRY_PORT));

                URLString url = URLString.createSessionURL(
                        UserInfo.getPreferences().getProperty
                        (UserConstants.CK_SERVER_NAME),
                        Integer.parseInt((UserInfo.getPreferences().getProperty
                                          (UserConstants.CK_SERVER_PORT))),
                        UserInfo.getPreferences().getProperty
                        (UserConstants.CK_SERVER_TYPE),
                        UserInfo.getPreferences().getProperty
                        (UserConstants.CK_MANAGEMENT_SESSION_NAME));

                Workspace.getLogger().info("Attempting to join server: " + url.toString());
                /**
                 * If group management session exists, try to join it.
                 */
                if (SessionFactory.sessionExists(url))
                {
                    session = SessionFactory.createSession(agent, url, true);
                    channel = session.createChannel
                            (agent, UserInfo.getPreferences().
                             getProperty(UserConstants.CK_MANAGEMENT_CHANNEL_NAME),
                             true, true, true);

                    channel.addConsumer(agent, agent);
                    /**
                     * Set group management channel and session in
                     * user properties.
                     */
                    getUserInfo().setGroupManagementSession(session);
                    getUserInfo().setGroupManagementChannel(channel);
                    /**
                     * Successfully joined group management session and channel.
                     */
                    joinedServer();
                }
                else
                {
                    sendFeedback("Group management session " + url.toString() +
                                  " is not found");
                }
            }
            catch (NameInUseException ex)
            {
                sendFeedback("Cannot connect to server - name is in use");
                failedPermissionHandler();
            }
            catch (PermissionDeniedException ex)
            {
                sendFeedback("Cannot connect to server - permission denied");
                failedPermissionHandler();
            }
            catch (JSDTException ex)
            {
                sendFeedback("Cannot connect to server: " + ex.toString());
                WorkspaceError.exception("Cannot connect to server", ex);
            }
        }
    }
    /**
     * Post login procedures - the failure of login permissions.
     */
    void failedPermissionHandler()
    {
        String server_string = UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_NAME) +
                " on port " +
                UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_PORT) +
                " with " +
                UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_TYPE)
                + " connection type";
        String total = "Cannot connect to " + server_string;
        /**
         * Send feedback
         */
        sendFeedback(total);
        /**
         * Offer to repeat procedure
         */
        requestAltCredentials();
    }
    /**
     * Show login dialog to request alt credentials
     */
    void requestAltCredentials()
    {
       LoginDialog dlg = User.getLoginDlg();
       dlg.addDialogDismissListener( this );
       dlg.setVisible( true );
    }
    /**
     * Post login procedure: request groups from server.
     */
    void joinedServer()
    {
        /**
         * Send feedback
         */
        String feedback = "Requesting groups list from server";
        sendFeedback(feedback);
        /**
         * Request groups
         */
        requestGroupsFromServer();
    }
    /**
     * Request the directory of groups from the server.
     * Assumes that group management is up and running.
     * The directory will be received by the groupClient
     * data member.
     */
    void requestGroupsFromServer()
    {
        sendToServer(null, UserConstants.CK_GET_GROUPS_REQUEST);
    }
    /**
     * Update group directory with new user groups from server.
     */
    void updateGroupsList(Hashtable groups)
    {
        getUserInfo().setGroups(groups);

        if (getUserInfo().getActiveGroup() != null)
        {
            getUserInfo().setActiveGroup((UserGroup)groups.
                                          get(getUserInfo().getActiveGroup().
                                          getUsersGroupID()));
        }
    }
    /**
     * Groups succesfully received - the end of login procedure
     */
    void goOnline()
    {
        String server_string = UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_NAME) +
                " on port " +
                UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_PORT) +
                " with " +
                UserInfo.getPreferences().
                getString(UserInfo.CK_SERVER_TYPE)
                + " connection type";

        String total = "Welcome to " + server_string;
        /**
         * Send feedback
         */
        sendFeedback(total);
        /**
         * Set online status
         */
        getUserInfo().setOnline(true);
        /**
         * Notify all listeners that workspace is online
         */
        Workspace.fireEvent(new Integer(2002), "online", new Boolean(true));
    }
   /**
    * Leave the group management communication channel
    * and session. User is considered to be online still.
    */
    public void leaveServer()
    {
       ServerAgent agent = getServerAgent();
       try
       {
          if (getUserInfo().getGroupManagementChannel() != null)
          {
             getUserInfo().getGroupManagementChannel().leave( agent );
          }
          if (getUserInfo().getGroupManagementSession() != null)
          {
             getUserInfo().getGroupManagementSession().leave( agent );
          }
          getUserInfo().setGroupManagementSession(null);
          getUserInfo().setGroupManagementChannel(null);
       }
       catch (JSDTException ex)
       {
          sendFeedback("Leave group management exception: " + ex.toString());
       }
    }
// ************************ Server messaging ****************
    /**
     * Send message to server
     */
    void sendToServer(Object message, String description)
    {
        /**
         * Get user group agent
         */
        ServerAgent agent = getServerAgent();
        /**
         * If agent is null, return
         */
        if (agent == null) return;

        DataPackage outgoingPackage = new DataPackage();
        outgoingPackage.setContents(message);
        outgoingPackage.getProperties().
                setProperty(DataPackage.DESCRIPTION, description);
        try
        {
            /**
             * Send message to server side network processor.
             */
            if (getUserInfo().getGroupManagementChannel() != null)
            {
                getUserInfo().getGroupManagementChannel().
                        sendToClient(agent, UserInfo.getPreferences().
                                            getProperty(UserConstants.CK_BOT_NAME),
                                            new Data(outgoingPackage));
            }
            else
            {
                sendFeedback("> ERROR: Group management channel is null");
            }
        }
        catch (JSDTException ex)
        {
            sendFeedback("Send to server exception: " + ex.toString());
        }
    }
    /**
     * Send message to all other users except server
     */
    public void sendToAllUsers(String text, String description)
    {
        /**
         * Get user group agent
         */
        ServerAgent agent = getServerAgent();
        /**
         * If agent is null, we are already in this group.
         */
        if (agent == null) return;
        /**
         * Send notification that user joined server
         * to all other users
         */
        DataPackage outgoingPackage = new DataPackage();
        outgoingPackage.setContents(text);
        outgoingPackage.getProperties().setProperty(
                DataPackage.DESCRIPTION, description);

        try
        {
            userInfo.getGroupManagementChannel().sendToOthers(
                    agent,  new Data(outgoingPackage));
        }
        catch (JSDTException ex)
        {
            ex.printStackTrace();
        }
    }
// ************************ Groups join and leave ***********
    /**
     * Connect to the workgroup specified by the parameter, if possible.
     * User group user connected to, automatically becomes active.
     */
    public boolean joinGroup(URLString url)
    {
       UserGroup usersGroup = findGroup(url);
       if (usersGroup == null)
       {
          sendFeedback("> No group found with url " + url);
          sendFeedback("> Connect to server and download updated list of groups");
          return false;
       }
       return joinGroup(usersGroup);
    }
    /**
     * Connect to the workgroup specified by the argument, if possible.
     * User group user connected to, automatically becomes active.
     */
    public boolean joinGroup(UserGroup usersGroup)
    {
        if (usersGroup != null)
        {
            /**
             * Group session and channel
             */
            Session session;
            Channel channel;
            /**
             * Create user group agent
             */
            GroupAgent agent = createGroupAgent(usersGroup.getName());
            /**
             * If agent is null, we are already in this group.
             */
            if (agent == null) return true;
            try
            {
                RegistryFactory.registryPort = Integer.parseInt
                        (UserInfo.getPreferences().getProperty(UserInfo.CK_REGISTRY_PORT));
                URLString url = usersGroup.getSessionURL();

                sendFeedback("> Connecting to group url " + url);

                session = SessionFactory.createSession(agent, url, true);
                channel = session.createChannel(agent,
                           usersGroup.getUsersGroupChannelID(), true, true, true);
                /**
                 * Add consumer for channel
                 */
                channel.addConsumer(agent, agent);
                /**
                 * Bound agent to channel
                 */
                agent.setChannel(channel);
                /**
                 * Set new active group
                 */
                getUserInfo().setActiveGroup(usersGroup);
                getUserInfo().setOnline(true);
                /**
                 * Announce successful join
                 */
                String sender = DefaultAgent.getAgentName();
                String text = " joined " + usersGroup.getName();
                /**
                 * Send to console
                 */
                sendFeedback("You are" + text);
                /**
                 * Send to group users
                 */
                sendToGroup(sender + text, UserInfo.CK_USER_JOINED_GROUP, usersGroup);
                return true;
            }
            catch (JSDTException ex)
            {
                sendFeedback("> Cannot connect to group: " + ex.toString());
            }
        }
        return false;
    }
    /**
     * Connect to the most recently joined group,
     * if any, as specified in the preferences.
     */
    public boolean joinRecentGroup()
    {
        if (getUserInfo().getGroups() != null)
        {
            /**
             * Parse name of last active group from user preferences.
             */
            String last_active_group_id = UserInfo.getPreferences().
                    getProperty(UserConstants.CK_LAST_ACTIVE_GROUP);

            if (last_active_group_id == null)
            {
                sendFeedback("No last group available");
                return false;
            }
            /**
             * Get object user group from user preferences.
             */
            UserGroup usersGroup = (UserGroup)getUserInfo().getGroups().
                                            get(last_active_group_id);
            /**
             * Join last active group if goup exists.
             */
            if (usersGroup != null)
            {
                return joinGroup(usersGroup);
            }
            else
            {
                sendFeedback("Group " + last_active_group_id +
                              " is not found on server.");
            }
        }
        else
        {
            sendFeedback("No groups available");
        }
        return false;
    }
    /**
     * Leave a users group (passed as argument).
     */
    public void leaveGroup(UserGroup usersGroup)
    {
        if (usersGroup != null)
        {
            /**
             * Users group name
             */
            String groupName = usersGroup.getName();
            /**
             * Find agent
             */
            GroupAgent agent = getGroupAgent(groupName);
            /**
             * If agent is null, return
             */
            if ( agent == null ) return;
            /**
             * Obtain reference to the session that this client
             * currently subscribes to and leave it. This will automatically
             * expel the client from any objects subordinate to that session.
             */
            try
            {
                /**
                 * Announce group disconnection
                 */
                String sender = DefaultAgent.getAgentName();
                String text = " left " + usersGroup.getName();
                /**
                 * Send to all other users before
                 * this user actually leaves this group.
                 */
                sendToGroup(sender + text, UserInfo.CK_USER_LEFT_GROUP, usersGroup);
                /**
                 * Leave group
                 */
                URLString url = usersGroup.getSessionURL();
                Session session = SessionFactory.createSession(agent, url, false);
                session.leave(agent);
                /**
                 * Send to console
                 */
                sendFeedback("You are" + text);
                /**
                 * Desactivate group if it was active
                 */
                if ( getUserInfo().getActiveGroup().equals(usersGroup))
                {
                    getUserInfo().setActiveGroup( null );
                }
            }
            catch (JSDTException ex)
            {
                sendFeedback("Leave group exception: " + ex.toString());
            }
        }
    }
     /**
     * Leave all groups, this user is joined to.
     */
    public void leaveAllGroups()
    {
        if (getUserInfo().getGroups() == null)
        {
            return;
        }
        Enumeration gr = getUserInfo().getGroups().elements();
        while (gr.hasMoreElements())
        {
            UserGroup ug = (UserGroup)gr.nextElement();
            String groupName = ug.getName();
            GroupAgent agent = getGroupAgent(groupName);
            if ( agent != null && ug.hasUser(agent.getAgentName()))
            {
                leaveGroup(ug);
            }
        }
    }
//**************** Group agents *******************
    /**
     * Creates group agent instance for a group. Places agent in
     * hashtable under groupName key. Also creates a message queue
     * with the same name as group and adds group message handler.
     */
    public GroupAgent createGroupAgent(String messageQueueName)
    {
        if (agents.containsKey(messageQueueName))
        {
            return (GroupAgent) agents.get(messageQueueName);
        }
        /**
         * Create group agent
         */
        GroupAgent agent = new GroupAgent(this, messageQueueName);
        agents.put(messageQueueName, agent);
        /**
         * Create group message handler
         */
        GroupMessageHandler gmh = new GroupMessageHandler(agent);
        /**
         * Create message queue and add group message handler.
         */
        if ( Messenger.addLocalMessageQueue(messageQueueName) )
        {
           MCQueue queue = Messenger.getLocalMessageQueue(messageQueueName);
           queue.getHandlers().add(gmh);
        }
        else
        {
            return null;
        }
        return agent;
    }
    /**
     * Returns group management agent - the client
     * side processor of group channel.
     * @param groupName the name of group, which agent is required.
     */
    public GroupAgent getGroupAgent(String groupName)
    {
        return (GroupAgent) agents.get(groupName);
    }
    /**
     * Traverses the group directory (list of active groups)
     * to determine if any group matches the group name and
     * description parameters
     * @return boolean
     * @param groupName java.lang.String
     */
    public boolean isGroupExists(String groupName)
    {
        boolean exists = false;
        Hashtable groups = getUserInfo().getGroups();
        if (groups != null && groupName != null)
        {
            Enumeration i = groups.elements();
            while (i.hasMoreElements())
            {
                UserGroup ug = (UserGroup)i.nextElement();
                if (ug.getName().equals(groupName))
                {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
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
        Hashtable groups = getUserInfo().getGroups();
        if (groups != null && groupName != null)
        {
            Enumeration i = groups.elements();
            while (i.hasMoreElements())
            {
                UserGroup ug = (UserGroup)i.nextElement();
                if (ug.getName().equals(groupName))
                {
                    return ug;
                }
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
        Hashtable groups = getUserInfo().getGroups();

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
     * Places feedback to message queue. This message
     * should be processed by GUI clients. The feedback
     * message code is 4000.
     * @param feedback the feedback message
     */
    public void sendFeedback(String feedback)
    {
        MCMessage fm = new MCMessage();
        fm.setMsgCode(4000);
        fm.setData(feedback.getBytes());
        try
        {
            Messenger.getMessenger().postMessage("LOCAL:SYSTEM", fm,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            emcError.printStackTrace();
        }
    }
// ******************* Group send methods **************
    /**
     * Send message to currently active group
     */
    public void sendToGroup(String text, String description, UserGroup usersGroup)
    {
        try
        {
            if (usersGroup == null)
            {
                return;
            }
            URLString url = usersGroup.getSessionURL();
            if (url == null)
            {
                return;
            }
            /**
             * Get user group agent
             */
            GroupAgent agent = getGroupAgent(usersGroup.getName());
            /**
             * If agent is null, return
             */
            if (agent == null) return;

            Session session = SessionFactory.createSession(agent, url, false);
            if (session == null)
            {
                return;
            }
            Channel channel = session.createChannel(agent,
                                                    usersGroup.getUsersGroupChannelID(), true, true, false);
            if (channel == null)
            {
                return;
            }
            /**
             * Send text to other users in group
             */
            DataPackage outgoingPackage = new DataPackage();
            outgoingPackage.setContents(text);
            outgoingPackage.getProperties().setProperty(
                    DataPackage.DESCRIPTION, description);
            channel.sendToOthers(agent, new Data(outgoingPackage));
        }
        catch (JSDTException ex)
        {
            ex.printStackTrace();
        }
    }
    /**
     * Send command to currently active group
     */
    public void sendToGroup(Data command, UserGroup usersGroup)
    {
        try
        {
            if (usersGroup == null)
            {
                return;
            }
            URLString url = usersGroup.getSessionURL();
            if (url == null)
            {
                return;
            }
            /**
             * Get user group agent
             */
            GroupAgent agent = getGroupAgent(usersGroup.getName());
            /**
             * If agent is null, return
             */
            if (agent == null) return;

            Session session = SessionFactory.createSession(agent, url, false);
            if (session == null)
            {
                return;
            }
            Channel channel = session.createChannel(agent,
                                                    usersGroup.getUsersGroupChannelID(),
                                                    true, true, false);
            if (channel == null)
            {
                return;
            }
            /**
             * Send command to other users in group
             */
            channel.sendToOthers(agent, command);
        }
        catch (JSDTException ex)
        {
            ex.printStackTrace();
        }
    }
// ***************** Groups administration ***************
    /**
     * Submit a request to the server that a
     * group be created and added to the group directory.
     */
    public void createUsersGroup(UserGroup usersGroup)
    {
        sendToServer(usersGroup, UserConstants.CK_ADD_GROUP_REQUEST);
    }
    /**
     * Submit a request to the server that it
     * removes a specific group from the group directory.
     */
    public void removeUsersGroup(UserGroup usersGroup)
    {
        sendToServer(usersGroup, UserConstants.CK_REMOVE_GROUP_REQUEST);
    }
// ***************** Connection listener ****************
    /**
     * Test connection to group management and notify user if it isn't active.
     * @return boolean
     */
    public boolean verifyGroupManagementConnection()
    {
        if (getUserInfo().getGroupManagementSession() == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
//************************** Login dialog and listener event **
    /**
     * Returns console login dialog
     */
    public static LoginDialog getLoginDlg()
    {
        if (loginDlg == null)
        {
            ImageIcon icon = new ImageIcon
                    ( new ResourceLoader(User.class)
                           .getResourceAsImage("images/not_connected.png") );

            loginDlg = new LoginDialog( Workspace.getUI().getFrame(),
                                        "Network Login",
                                        "Please, provide your" +
                                        " network credentials to access server",
                                        icon, new UserLoginValidator())
            {
                public void dispose()
                {
                    destroy();
                    super.dispose();
                }
            };
            loginDlg.pack();
            loginDlg.centerDialog();
            loginDlg.setIcon(null);
        }
        loginDlg.setTexture(null);
        loginDlg.setOpaque(false);
        return loginDlg;
    }
    /**
     * Listen for own login dialog events.
     * @param event
     */
    public void dialogDismissed(DialogDismissEvent event)
    {
        if (!event.isCancelled())
        {
            SwingUtilities.invokeLater(new Thread()
            {
                public void run()
                {
                   joinServerAlt();
                }
            });
        }
    }
// ************************ User manage life cycle *********
    /**
     * Perform various initialization maneuvers at start-up
     */
    public void load() { }
    /**
     * Perform termination housekeeping such as updating preferences and
     * other sundry chores.
     */
    public void save() throws IOException
    {
        /**
         * Remember last active group.
         */
        if (getUserInfo().isOnline() && getUserInfo().getActiveGroup() != null)
        {
            Workspace.logException("> The last active group for this user is " +
                               getUserInfo().getActiveGroup().getName());
            UserInfo.getPreferences().
                    setProperty(UserConstants.CK_LAST_ACTIVE_GROUP,
                                getUserInfo().getActiveGroup().getUsersGroupID());
        }
        /**
         * Write preferences
         */
        getUserInfo().writePreferences();
    }
    /**
     * Reset user manager connections
     */
    public void reset()
    {
        if (getUserInfo().isOnline())
        {
            leaveAllGroups();
            leaveServer();
            getUserInfo().setOnline(false);
            sendFeedback("Disconnected from server.");
            Workspace.fireEvent(new Integer(2002), "offline", new Boolean(false));
        }
    }
    /**
     * This method actually saves data in plugins
     */
    public void dispose()
    {
        try
        {
            save();
            reset();
        }
        catch (IOException ex)
        {
            Workspace.logException(">Exception - Cannot save network client config");
        }
    }
}
