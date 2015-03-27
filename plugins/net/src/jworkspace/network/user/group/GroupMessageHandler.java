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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
 */

import org.eldos.MsgConnect.MCBase.*;

import jworkspace.network.datagram.DataPackage;
import jworkspace.network.user.UserInfo;
import com.sun.media.jsdt.Data;
import com.sun.media.jsdt.JSDTException;
/**
 *  User message handler uses User API to answer requests
 *  tp group sent via Java Workspace message queue from GUI shells.
 *  This handler also answers back to client, if any callback
 *  is required. This is the functionality of message queue.
 */
public class GroupMessageHandler extends MCMessageHandler
                                implements MCHandleMessageListener
{
    /**
     * Reference to a user group agent
     */
    GroupAgent agent = null;
    /**
     * Public constructor
     * @param agent
     */
    public GroupMessageHandler(GroupAgent agent)
    {
       this.agent = agent;
       /**
        * This class will listen for incoming messages
        */
       this.addMessageListener(this);
       /**
        * This message range is in use by network
        * user plugin.
        */
       setMsgCodeHigh(2000);
       setMsgCodeLow(1901);
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
              * Request for the list of group users
              */
             case 1909:
             {
                agent.sendGroupUsersList();
             }
             break;
            /**
             * Load group resource
             */
            case 1910:
                {
                    String path = new String(incomingMessage.getData());
                    cmd_grp_res_load(path);
                }
                break;
            default:
                return false;
        }
        return true;
    }
    /**
     * Send a load resource request
     * @param name of resource to load
     */
    public void cmd_grp_res_load(String name)
    {
        try
        {
           DataPackage outgoingPackage = new DataPackage();
           outgoingPackage.setContents(name);
           outgoingPackage.getProperties().setProperty
                  ( DataPackage.DESCRIPTION,
                    UserInfo.CK_LOAD_RESOURCE_REQUEST );
           agent.sendToGroup(new Data(outgoingPackage));
        }
        catch(JSDTException ex)
        {
           agent.sendFeedback(ex.toString());
        }
    }
}
