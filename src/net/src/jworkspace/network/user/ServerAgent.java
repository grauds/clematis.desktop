package jworkspace.network.user;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/
import com.sun.media.jsdt.*;
import jworkspace.kernel.Messenger;
import jworkspace.kernel.Workspace;
import jworkspace.network.datagram.DataPackage;
import java.util.Hashtable;

public class ServerAgent extends DefaultAgent implements ChannelConsumer
{
    /**
     * User Manager
     */
    protected User user = null;
    /**
     * User agent constructor
     * @param um - user manager
     */
    public ServerAgent(User um)
    {
        if (um == null)
        {
            throw new IllegalArgumentException("User manager cannot be null");
        }
        this.user = um;
    }
    /**
     * Returns the name of queue of this agent
     * @return the name of queue of this agent
     */
    public String getQueueName()
    {
        return Messenger.getQueue().getQueueName();
    }
    /**
     * Return client URL for this client. This URL will be registered
     * in JSDT registry. The port of server client is one point greater,
     * than the port of server group management session.
     * @return url string for this client
     */
    public URLString getClientURL()
    {
      return URLString.createClientURL
           ( UserInfo.getPreferences().getString(UserInfo.CK_SERVER_NAME),
             UserInfo.getPreferences().getInt(UserInfo.CK_SERVER_PORT) + 1,
             UserInfo.getPreferences().getString(UserInfo.CK_SERVER_TYPE),
             getAgentName());
    }
    /**
     * Returns channel of this agent
     * @return group management channel
     */
    public Channel getChannel()
    {
        return user.getUserInfo().getGroupManagementChannel();
    }
    /**
     * Accept and parse incoming data.
     * The if-else structure maps message types to functions.
     */
    public void dataReceived(Data data)
    {
        DataPackage incomingPackage = null;
        try
        {
            incomingPackage = (DataPackage)data.getDataAsObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /**
         * Update group directory.
         */
        if (incomingPackage.getProperties().getProperty(DataPackage.DESCRIPTION).
                  equalsIgnoreCase(UserConstants.CK_GROUP_DIRECTORY))
        {
            user.updateGroupsList((Hashtable)incomingPackage.getContents());
            /**
             * User should be online while receiving
             * list of groups. If user is offline this means
             * that it is the final stage of connection process.
             */
            if ( ! user.getUserInfo().isOnline())
            {
                user.goOnline();
            }
        }
        /**
         * Got plain text message, server message or server error message.
         */
        else if (incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.PLAIN_TEXT) ||

                incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_SERVER_MESSAGE) ||

                incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_SERVER_ERROR))
        {
            /**
             * Forward text to GUI
             */
            user.sendFeedback(data.getSenderName() + ": " +
                             (String)incomingPackage.getContents());
        }
        /**
         * Another user joined or left server
         */
        else if ( incomingPackage.getProperties().getProperty
                 ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_USER_JOINED_SERVER)
                 ||
                  incomingPackage.getProperties().getProperty
                 ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_USER_LEFT_SERVER) )
        {
            /**
             * Forward text to GUI
             */
            if (incomingPackage.getContents() instanceof String)
            {
                user.sendFeedback((String)incomingPackage.getContents());
            }
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
    }
}
