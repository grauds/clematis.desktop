package jworkspace.ui.network;
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
import org.eldos.MsgConnect.MCBase.MCMessageHandler;
import org.eldos.MsgConnect.MCBase.MCHandleMessageListener;
import org.eldos.MsgConnect.MCBase.MCHandleMessageEvent;
import org.eldos.MsgConnect.MCBase.MCMessage;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Listener of feedback messages from user plugin
 */
public class NetworkMessageHandler extends MCMessageHandler
                                implements MCHandleMessageListener
{
    NetworkConsole console = null;
   /**
     * Constructor takes an instance of network
     * console as parameter.
     * @param console
     */
    public NetworkMessageHandler(NetworkConsole console)
    {
       super();
       if (console == null)
       {
         throw new IllegalArgumentException("Console is null");
       }
       this.console = console;
       /**
        * This class will listen for incoming messages
        */
       this.addMessageListener(this);
       /**
        * This message range is in use by feedback handlers
        */
       setMsgCodeHigh(4100);
       setMsgCodeLow(4000);
    }
    /**
     * Handle feedback messages
     * @param event feedback message
     * @return
     */
    public boolean handleMessage(MCHandleMessageEvent event)
    {
       MCMessage incomingMessage = event.getMessage();
       int messageCode = incomingMessage.getMsgCode();
       switch(messageCode)
       {
         /**
          * Receive text and forward it to console
          */
          case 4000:
               console.append( new String(incomingMessage.getData()) );
               break;
         /**
          * Get a list of group users
          */
          case 4002:
               {
                   byte[] data = incomingMessage.getData();
                   List names = new ArrayList();
                   if (data != null && data.length > 0)
                   {
                     String str = new String(data);
                     StringTokenizer st = new StringTokenizer(str,
                                    new String(new byte[] {0}));
                     while (st.hasMoreTokens())
                     {
                        names.add( st.nextToken() );
                     }
                     console.getServerUsersList().setListData(names.toArray());
                   }
               }
               break;
       }
       return false;
    }
}
