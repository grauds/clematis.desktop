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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
 */

import java.io.*;

import com.sun.media.jsdt.*;

import jworkspace.kernel.*;
import jworkspace.network.*;
import jworkspace.network.datagram.*;

/**
 * This class is an administration tool for server. It implements
 * Client interface, allowing direct calls of jsdt functions.
 * This manager can add or remove groups, return list of groups and etc.
 */
public class ServerManager implements Client, ChannelConsumer
{
    /**
     * The reference to a server instance
     */
    Server server = null;
    /**
     * The name of this client
     */
    String name;
    /**
     * Constructor of server manager
     * @param server
     */
    ServerManager(Server server)
    {
        if (server == null)
        {
            throw new IllegalArgumentException("Server manager cannot be null");
        }
        this.server = server;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Object authenticate(AuthenticationInfo ai)
    {
        return new Long(1236457514);
    }

    /**
     * This method is used for system messages handling.
     */
    public void dataReceived(Data data)
    {
        DataPackage incomingPackage = null;
        try
        {
            Object datapack = data.getDataAsObject();
            if (!(datapack instanceof DataPackage))
            {
                Workspace.getLogger().warning("Bad data");
                return;
            }
            incomingPackage = (DataPackage)datapack;
            /**
             * Deal with received data package as with system command.
             * First get command
             */
            String command = incomingPackage.getProperties().getProperty(DataPackage.DESCRIPTION);
            /**
             * Received command
             */
            Workspace.getLogger().info("> Server received command: " + command);
            /**
             * Request list of groups
             */
            if (command.equalsIgnoreCase(ServerInfo.CK_GET_GROUPS_REQUEST))
            {
                sendGroupsList(data.getSenderName());
            }
            /**
             * Add group request
             */
            else if (command.equalsIgnoreCase(ServerInfo.CK_ADD_GROUP_REQUEST))
            {
                try
                {
                    UserGroup ug = (UserGroup)incomingPackage.getContents();
                    String name = ug.getName();
                    server.addGroup((UserGroup)incomingPackage.getContents());
                    sendMessage("> Group " + name + " is added", data.getSenderName());
                    broadcastGroupList();
                }
                catch (ServerOperationException e)
                {
                    Workspace.getLogger().warning("> Group is not added: " + e.getMessage());
                    sendErrorMessage("> Group is not added: " + e.getMessage(), data.getSenderName());
                }
            }
            /**
             * Request to remove group
             */
            else if (command.equalsIgnoreCase(ServerInfo.CK_REMOVE_GROUP_REQUEST))
            {
                try
                {
                    UserGroup ug = (UserGroup)incomingPackage.getContents();
                    String name = ug.getName();
                    server.removeGroup((UserGroup)incomingPackage.getContents());
                    sendMessage("> Group " + name + " is removed", data.getSenderName());
                    broadcastGroupList();
                }
                catch (ServerOperationException e)
                {
                    Workspace.getLogger().warning("> Group is not removed: " + e.getMessage());
                    sendErrorMessage("> Group is not removed: " + e.getMessage(), data.getSenderName());
                }
            }
        }
        catch (StreamCorruptedException ex)
        {
            ex.printStackTrace();
            Workspace.getLogger().warning(ex.toString());
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
            Workspace.getLogger().warning(ex.toString());
        }
    }
    //********************** Communication with clients ************************

       /**
        * Send the list of groups to user.
        */
       public void sendGroupsList(String recipientName)
       {
           DataPackage outgoingPackage = new DataPackage();
           outgoingPackage.setContents(server.getServerInfo().getGroups());
           outgoingPackage.getProperties().setProperty
                   ( DataPackage.DESCRIPTION, ServerInfo.CK_GROUP_DIRECTORY );

           try
           {
               server.getServerInfo().getGroupManagementChannel().sendToClient
                       ( this, recipientName, new Data(outgoingPackage) );
               Workspace.getLogger().info("Sent group directory to " + recipientName);
           }
           catch (JSDTException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
       }

       /**
        * Send error message from server to specified user
        */
       public void sendErrorMessage(String message, String recipientName)
       {
           /**
            * Compose an error message
            */
           DataPackage outgoingPackage = new DataPackage();
           outgoingPackage.setContents(message);
           outgoingPackage.getProperties().setProperty(
                   DataPackage.DESCRIPTION, ServerInfo.CK_SERVER_ERROR);
           try
           {
               server.getServerInfo().getGroupManagementChannel().sendToClient
                       (this, recipientName, new Data(outgoingPackage));
           }
           catch (JSDTException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
       }

       /**
        * Send message from server to specified user
        */
       public void sendMessage(String message, String recipientName)
       {
           /**
            * Compose an error message
            */
           DataPackage outgoingPackage = new DataPackage();
           outgoingPackage.setContents(message);
           outgoingPackage.getProperties().setProperty(
                   DataPackage.DESCRIPTION, ServerInfo.CK_SERVER_MESSAGE);
           try
           {
               server.getServerInfo().getGroupManagementChannel().sendToClient
                       (this, recipientName, new Data(outgoingPackage));
           }
           catch (JSDTException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
       }

       /**
        * Send error message from server to all logged users
        */
       public void broadcastErrorMessage(String message)
       {
           /**
            * Compose an error message
            */
           DataPackage outgoingPackage = new DataPackage();

           outgoingPackage.setContents(message);
           outgoingPackage.getProperties().setProperty(
                   DataPackage.DESCRIPTION, ServerInfo.CK_SERVER_ERROR);
           try
           {
               server.getServerInfo().getGroupManagementChannel().
                       sendToOthers( this, new Data(outgoingPackage) );
           }
           catch (JSDTException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
       }

      /**
        * Broadcast a list of groups to all users of group management channel.
        */
       public void broadcastGroupList()
       {
           /**
            * Compose a package with a group list as a
            * content.
            */
           DataPackage outgoingPackage = new DataPackage();

           outgoingPackage.setContents(server.getServerInfo().getGroups());
           outgoingPackage.getProperties().
                   setProperty( DataPackage.DESCRIPTION, ServerInfo.CK_GROUP_DIRECTORY );
           /**
            * Try to send a composed package other users.
            */
           try
           {
               if (server.getServerInfo().getGroupManagementChannel() != null)
               {
                   server.getServerInfo().getGroupManagementChannel().
                           sendToOthers(this, new Data(outgoingPackage));
               }
           }
           catch (JSDTException ex)
           {
               ex.printStackTrace();
               Workspace.getLogger().warning(ex.toString());
           }
       }
}