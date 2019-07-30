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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
 */

import org.eldos.MsgConnect.MCBase.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Date;

import jworkspace.kernel.Workspace;
import jworkspace.network.UserGroup;
import jworkspace.network.user.group.GroupAgent;
import com.sun.media.jsdt.*;
/**
 *  User message handler uses User API to answer requests
 *  sent via Java Workspace message queue from GUI shells.
 *  This handler also answers back to client, if any callback
 *  is required. This is the functionality of message queue.
 */
public class UserMessageHandler extends MCMessageHandler
                                implements MCHandleMessageListener
{
    /**
     * Reference to the instance of user manager
     */
    User user = null;
    /**
     * Constructor takes an instance of user manager
     * as parameter.
     * @param user
     */
    public UserMessageHandler(User user)
    {
       super();
       if (user == null)
       {
         throw new IllegalArgumentException("User is null");
       }
       this.user = user;
       /**
        * This class will listen for incoming messages
        */
       this.addMessageListener(this);
       /**
        * This message range is in use by network
        * user plugin.
        */
       setMsgCodeHigh(1900);
       setMsgCodeLow(1800);
    }
    /**
     * Handler for incoming messages. Dispatch message,
     * select one of its functions and answer back to
     * GUI with a reply.
     * @param e handle message event
     * @return true, if message is handled
     */
    public boolean handleMessage(MCHandleMessageEvent e)
    {
        MCMessage incomingMessage = e.getMessage();
        int messageCode = incomingMessage.getMsgCode();
        switch(messageCode)
        {
            /**
             * Connect to server
             */
            case 1800:
                cmd_connect();
                break;
            /**
             * Connect to server as user
             */
            case 1801:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_connect_as( name );
                }
                break;
            /**
             * Connect to server as user with password
             */
            case 1802:
                {
                    /**
                     * If incoming message data is not null, try to parse
                     * user name and password from incoming parameters.
                     */
                    if ( incomingMessage.getData() != null)
                    {
                        String name_pwd = new String(incomingMessage.getData());
                        StringTokenizer st = new StringTokenizer(name_pwd, ":");
                        if (st.countTokens() == 2)
                        {
                            String name = st.nextToken();
                            String pwd = st.nextToken();
                            cmd_connect_as( name, pwd );
                        }
                        else if (st.countTokens() == 1)
                        {
                            String name = st.nextToken();
                            cmd_connect_as( name );
                        }
                        else
                        {
                            user.sendFeedback("Incorrect parameter in request for connection");
                        }
                    }
                }
                break;
            /**
             * Disconnect from server or leave group
             */
            case 1803:
                {
                    byte[] bytes = incomingMessage.getData();
                    if ( bytes == null )
                    {
                        cmd_disconnect();
                    }
                    else
                    {
                        String name = new String(incomingMessage.getData());
                        cmd_grp_leave( name );
                    }
                }
                break;
            /**
             * Create group
             */
            case 1804:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_grp_create(name);
                }
                break;
            /**
             * Join group
             */
            case 1805:
                {
                    String name = new String(incomingMessage.getData());
                    if (cmd_grp_join( name ))
                    {
                        incomingMessage.setResult( 0 );
                    }
                    else
                    {
                        incomingMessage.setResult( -1 );
                    }
                }
                break;
            /**
             * Join recent group
             */
            case 1806:
                if (cmd_grp_join_recent())
                {
                    incomingMessage.setResult( 0 );
                }
                else
                {
                    incomingMessage.setResult( -1 );
                }
                break;
            /**
             * List groups
             */
            case 1807:
                cmd_grp_list();
                break;
            /**
             * Print group users
             */
            case 1808:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_grp_print(name);
                }
                break;
            /**
             * Remove group
             */
            case 1809:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_grp_remove(name);
                }
                break;
             /**
              * User info
              */
             case 1810:
                cmd_usr_info();
                break;
             /**
              * Set user name
              */
             case 1811:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_set_alt_info( name );
                }
                break;
            /**
             * Set alternative user information
             */
            case 1812:
                {
                    String name_pwd = new String(incomingMessage.getData());
                    StringTokenizer st = new StringTokenizer(name_pwd, ":");
                    if (st.countTokens() == 2)
                    {
                        String name = st.nextToken();
                        String pwd = st.nextToken();
                        cmd_set_alt_info(name, pwd);
                    }
                    else
                    {
                        user.sendFeedback("Incorrect parameter in request for alt info");
                    }
                }
                break;
              /**
               * Request for users of groups management channel
               */
              case 1813:
                {
                   try
                   {
                      if (user.getUserInfo().getGroupManagementChannel() != null)
                      {
                       Workspace.fireEvent(new Integer(2003), "online",
                              user.getUserInfo().getGroupManagementChannel().listClientNames());
                      }
                   }
                   catch(Exception ex)
                   {
                       user.sendFeedback("Cannot send list of server users");
                       ex.printStackTrace();
                   }
                }
                break;
              /**
               * If user manager online or offline
               */
              case 1814:
                {
                    if (user.getUserInfo().isOnline())
                    {
                        Workspace.fireEvent(new Integer(2001), "online", new Boolean(true));
                    }
                    else
                    {
                        Workspace.fireEvent(new Integer(2001), "offline", new Boolean(false));
                    }
                }
                break;
              /**
                * Set active group
                */
              case 1815:
                {
                    String name = new String(incomingMessage.getData());
                    cmd_grp_set_active(name);
                }
                break;
               /**
                * Set text to currently active group
                */
              case 1816:
                {
                    byte[] bytes = incomingMessage.getData();
                    if ( bytes != null )
                    {
                        String text = new String(bytes);
                        cmd_grp_send_text(text);
                    }
                }
                break;

            default:
                return false;
        }
        return true;
    }
   /**
    * Command section. Connect.
    */
    public void cmd_connect()
    {
        user.joinServer(false);
    }
    /**
     * Command section. Connect as different user,
     * but the same password.
     */
    public void cmd_connect_as(String name)
    {
        if ( name == null )
        {
            user.sendFeedback("Alternative name is not supplied");
            return;
        }
        if ( name.trim().equals("") )
        {
            user.sendFeedback("Alternative name is empty");
            return;
        }
        user.joinServer(true);
    }
    /**
     * Command section. Connect.
     */
    public void cmd_connect_as(String name, String password)
    {
        user.joinServerAs( name, password );
    }
    /**
     * Disconnect from server
     */
    public void cmd_disconnect()
    {
      if (!user.getUserInfo().isOnline())
      {
        return;
      }
      try
      {
        user.save();
        user.reset();
      }
      catch(IOException ex)
      {
         user.sendFeedback(ex.toString());
      }
    }
    /**
     * Join specified group
     */
    public boolean cmd_grp_join(String name)
    {
        UserGroup ug = user.findGroup(name);

        if (ug != null)
        {
            return user.joinGroup(ug);
        }
        else
        {
            user.sendFeedback("> Group " + name + " is not found on server");
        }
        return false;
    }
    /**
     * Print information about current user
     */
    public void cmd_usr_info()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("******* Current user information *******");
        sb.append("\n");
        sb.append("User nickname: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_USER_NAME));
        sb.append("\n");
        sb.append("Alt user nickname: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_ALT_USER_NAME));
        sb.append("\n");
        sb.append("UID: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_USER_ID));
        sb.append("\n");
        sb.append("Last active group: " +
                    UserInfo.getPreferences().
                    getString(UserConstants.CK_LAST_ACTIVE_GROUP));
        sb.append("\n");
        sb.append("Server name: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_SERVER_NAME));
        sb.append("\n");
        sb.append("Server port: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_SERVER_PORT));
        sb.append("\n");
        sb.append("Server type: " +
                    UserInfo.getPreferences().getString(UserConstants.
                                                                CK_SERVER_TYPE));
        sb.append("\n");
        sb.append("*******  *******");
        sb.append("\n");

        user.sendFeedback(sb.toString());
    }
    /**
     * Join the most recent group
     */
    public boolean cmd_grp_join_recent()
    {
        return user.joinRecentGroup();
    }
    /**
     * Disconnect from group.
     */
    public void cmd_grp_leave(String name)
    {
        UserGroup ug = user.findGroup(name);

        if (ug != null)
        {
            user.leaveGroup(ug);
        }
        else
        {
            user.sendFeedback("> Group " + name + " is not found on server");
        }
    }
    /**
     * List existing groups
     */
    public void cmd_grp_list()
    {
        user.requestGroupsFromServer();

        Hashtable groups = user.getUserInfo().getGroups();
        if (groups == null)
        {
            user.sendFeedback("There is no groups on server");
            return;
        }
        Enumeration i = groups.elements();
        StringBuffer sb = new StringBuffer();
        sb.append("List of server groups:");

        while (i.hasMoreElements())
        {
            UserGroup ug = (UserGroup)i.nextElement();

            if (   user.getUserInfo().getActiveGroup() != null
                    &&
                    ug.hasUser( DefaultAgent.getAgentName() )
                    &&
                    user.getUserInfo().getActiveGroup().getName().equals(ug.getName()))
            {
               sb.append("\n");
               sb.append("* (A) " + ug.getName());
            }
            else if (ug.hasUser(DefaultAgent.getAgentName()))
            {
               sb.append("\n");
               sb.append("* " + ug.getName());
            }
            else
            {
               sb.append("\n");
               sb.append(ug.getName());
            }
        }
        user.sendFeedback(sb.toString());
    }
    /**
     * Create users group
     */
    public void cmd_grp_create(String name)
    {
        UserGroup ug = new UserGroup();
        ug.setName(name);
        ug.setCreationDate(new Date());
        ug.setMainUser(DefaultAgent.getAgentName());

        if (!user.isGroupExists(name))
        {
            user.createUsersGroup(ug);
        }
    }

    /**
     * Delete group
     */
    public void cmd_grp_remove(String name)
    {
        UserGroup ug = user.findGroup(name);

        if (ug != null)
        {
            user.removeUsersGroup(ug);
         }
        else
        {
            user.sendFeedback("> Group " + name + " is not found on server");
        }
    }

    /**
     * Set active group. User should be connected
     * to that group first.
     */
    public void cmd_grp_set_active(String name)
    {
        UserGroup ug = user.findGroup(name);
        if (ug.hasUser(DefaultAgent.getAgentName()))
        {
            user.getUserInfo().setActiveGroup(ug);
            user.sendFeedback("> Group " + name + " is now selected for activity");
        }
        else
        {
            user.sendFeedback("You should join group " + name + " to make it active");
        }
    }
    /**
     * Sends text to currently active group
     * @param text to send to currently active group
     */
    public void cmd_grp_send_text(String text)
    {
       UserGroup active = user.getUserInfo().getActiveGroup();
       if (active != null)
       {
           user.sendToGroup(text, UserConstants.PLAIN_TEXT, active);
           user.sendFeedback("Send to active group " + active.getName());
       }
       else
       {
           user.sendToAllUsers(text, UserConstants.PLAIN_TEXT);
       }
    }
    /**
     * Help on commands
     */
    public void cmd_help() { }
    /**
     * Print group participants
     * @param name of required group
     */
    public void cmd_grp_print(String name)
    {
        /**
         * Find users group
         */
        UserGroup ug = user.findGroup(name);

        if (ug != null)
        {
            /**
             * Find user group agent
             */
            GroupAgent agent = user.getGroupAgent(name);
            if (agent == null)
            {
                return;
            }

            URLString url = null;
            Session session = null;
            Channel groupChannel = null;
            String clientNames[] = null;
            try
            {
                url = ug.getSessionURL();
                session = SessionFactory.createSession(agent, url, false);
                groupChannel = session.createChannel(agent,
                              ug.getUsersGroupChannelID(), true, true, false);
                clientNames = groupChannel.listClientNames();
            }
            catch (JSDTException ex) {  }

            if (clientNames.length == 0)
            {
               user.sendFeedback("There is no users in group " + ug.getName());
            }
            else
            {
               StringBuffer sb = new StringBuffer();
               sb.append("Users of " + ug.getName() + " group:");
               sb.append("\n");

               for (int i = 0; i < clientNames.length; i++)
               {
                  sb.append(clientNames[i]);
                  sb.append("\n");
               }
               user.sendFeedback(sb.toString());
            }
        }
        else
        {
             user.sendFeedback("> Group " + name + " is not found on server");
        }
    }
    /**
     * Set alternative user name and password
     */
    public void cmd_set_alt_info(String userName, String passwd)
    {
        user.setAltCredentials(userName, passwd);
    }
    /**
     * Set alternative user name
     */
    public void cmd_set_alt_info(String userName)
    {
        user.setAltCredentials(userName);
    }
}