package jworkspace.network.user.group;

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
import com.sun.media.jsdt.*;
import com.sun.media.jsdt.event.ClientEvent;
import jworkspace.kernel.*;

import jworkspace.network.datagram.*;
import jworkspace.network.user.User;
import jworkspace.network.user.UserConstants;
import jworkspace.network.user.ServerAgent;

import org.eldos.MsgConnect.MCBase.MCMessage;
import org.eldos.MsgConnect.MCBase.EMCError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Client object to be used with group management sessions of users groups.
 * It acts together with server side network processor, allowing
 * to dispatch incoming messages by client and send appropriate answer.
 */
public class GroupAgent extends ServerAgent
{
    /**
     * The name of local message queue to speak with GUI shells and
     * other plugins within one Java Workspace process
     */
    String queueName = null;
    /**
     * Channel this agent is created for
     */
    Channel channel = null;
     /**
     * Group agent constructor
     * @param um - user manager
     * @param queueName - message queue to exchange data with GUI
     */
    public GroupAgent(User um, String queueName)
    {
        super(um);
        this.queueName = queueName;
    }
    /**
     * Group agent constructor
     * @param um - user manager
     * @param channel - jsdt channel to exchange data with server
     * @param queueName - message queue to exchange data with GUI
     */
    public GroupAgent(User um, Channel channel, String queueName)
    {
        super(um);
        this.channel = channel;
        this.queueName = queueName;
    }
    /**
     * Returns the name of queue of this agent
     * @return the name of queue of this agent
     */
    public String getQueueName()
    {
        return "LOCAL:" + queueName;
    }
    /**
     * Returns channel of this agent
     * @return group channel
     */
    public Channel getChannel()
    {
        return channel;
    }
    /**
     * Set channel for this group agent
     * @param channel
     */
    public void setChannel(Channel channel)
    {
        this.channel = channel;
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
         * Another user joined or left group
         */
        if (incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_USER_JOINED_GROUP)
            ||
            incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_USER_LEFT_GROUP))
        {
            /**
             * Forward text to GUI
             */
            if (incomingPackage.getContents() instanceof String)
            {
                sendFeedback((String)incomingPackage.getContents());
            }
            /**
             * Notify all ui that group users list is changed
             */

        }
        /**
         * Got plain text message or error message
         */
        else if (incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.PLAIN_TEXT)
                ||
                incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.CK_SERVER_ERROR))
        {
            /**
             * Forward text to GUI
             */
            sendFeedback(data.getSenderName() + ": " +
                             (String)incomingPackage.getContents());
        }
        /**
         * Got message with xml content
         */
        else if (incomingPackage.getProperties().getProperty
                ( DataPackage.DESCRIPTION).equalsIgnoreCase(UserConstants.XML))
        {
            sendContent(((String)incomingPackage.getContents()).getBytes());
        }
    }
    /**
     * Sends group users list
     */
    public void sendGroupUsersList()
    {
        try
        {
          String[] names = getChannel().listClientNames();
          ByteArrayOutputStream baos = new ByteArrayOutputStream();

          for (int i = 0; i < names.length; i++)
          {
              baos.write(names[i].getBytes());
              baos.write(0);
          }
  	      baos.close();

          MCMessage fm = new MCMessage();
          fm.setMsgCode(4002);
          fm.setData( baos.toByteArray() );
          Messenger.getMessenger().postMessage(getQueueName(), fm,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            emcError.printStackTrace();
        }
        catch (JSDTException e)
        {
            sendFeedback("Cannot get a list of users for group "
                          + getChannel().getName());
        }
        catch (IOException e)
        {
            sendFeedback("Cannot get a list of users for group "
                          + getChannel().getName());
        }
    }
    /**
    * Send command to the server-side group client
    */
    public void sendToGroup(Data command) throws JSDTException
    {
        if ( channel != null )
        {
            channel.sendToOthers(this, command);
        }
        else
        {
            sendFeedback("Group agent is not bound to group channel");
        }
    }
    /**
     * Places loaded content to message queue.
     * @param content the feedback message
     */
    public void sendContent(byte[] content)
    {
        MCMessage fm = new MCMessage();
        fm.setMsgCode(4001);
        fm.setData(content);
        try
        {
            Messenger.getMessenger().postMessage(getQueueName(), fm,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            emcError.printStackTrace();
        }
    }
    /**
     * Places feedback to message queue. This message
     * should be processed by GUI clients.
     * @param feedback the feedback message
     */
    public void sendFeedback(String feedback)
    {
        MCMessage fm = new MCMessage();
        fm.setMsgCode(4000);
        fm.setData(feedback.getBytes());
        try
        {
            Messenger.getMessenger().postMessage(getQueueName(), fm,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            emcError.printStackTrace();
        }
    }
    /**
     * Client is invited to join byte array. This may be a result
     * of requests to load server content, upload client content to server
     * or somebody else has created new content and invited this user
     * to byte array. Anyway, user should handle new content arrival.
     * @param event
     */
    public void byteArrayInvited(ClientEvent event)
    {
        /**
         * The name of byte array this client is invited to
         * join.
         */
        String byteArrayName = event.getResourceName();
        /**
         * Get reference to session containing our byte array
         */
        Session session = event.getSession();
        /**
         * Join and get contents of byte array
         */
        try
        {
            ByteArray byteArray = session.createByteArray(this, byteArrayName, true);
            byte[] value = byteArray.getValueAsBytes();
            sendContent(value);
        }
        catch (JSDTException e)
        {
            sendFeedback("Cannot load resource");
        }
    }
}