package jworkspace.kernel;
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
import org.eldos.MsgConnect.MCBase.*;

import java.util.Hashtable;
import java.util.TimerTask;
/**
 * System workspace messenger. Makes use of Message Connect library
 * of version 1.06 and manages local and remote system message queues.
 * It also stores all generic message queues from plugins and etc.
 */
public final class Messenger
{
   /**
     * System private messenger
     */
    private static MCMessenger messenger = new MCMessenger();
    /**
     * System message queue
     */
    private static MCQueue queue = new MCQueue();
    /**
     * Additional message queues
     */
    private static Hashtable queues = new Hashtable();
    /**
     * Dispatch timer
     */
    private static java.util.Timer dispatchTimer = new java.util.Timer();
    /**
     * Timer task
     */
    static class DispatchTask extends TimerTask
    {
            public void run()
            {
                   try
                   {
                        messenger.dispatchMessages();
                   }
                   catch(Exception e)
                   {
                        e.printStackTrace();
                   }
            }
    }
    /**
     * Handle unhandled messages in message queue.
     */
    static class MessageUtilizer implements MCHandleMessageListener
    {
      public boolean handleMessage(MCHandleMessageEvent event)
      {
         return true;
      }
    }

    /**
     * Add message handler to the Workspace system message queue.
     * @param messageHandler the message handler to add
     */
    public static void addMessageHandler(MCMessageHandler messageHandler)
    {
        queue.getHandlers().add(messageHandler);
    }

    /**
     * Remove message handler from the Workspace system message queue.
     * @param messageHandler the message handler to remove
     */
    public static void removeMessageHandler(MCMessageHandler messageHandler)
    {
        queue.getHandlers().remove(messageHandler);
    }

    /**
     * This method adds a messemger to a transport object. Transport
     * can be either local or socket, to connect different copies of
     * Java Workspace on different computers.
     * @param transport new transport to associate with this messenger.
     */
    public static void addMessengerTransport(MCBaseTransport transport)
    {
        transport.setMessenger(messenger);
        transport.setActive(true);
    }
    /**
     * Add local message queue.
     * @return true, if there is no queue with the same name exists
     * and new queue is successfully added.
     */
    public static boolean addLocalMessageQueue(String name)
    {
        if (queues.containsKey(name))
        {
          return false;
        }
        MCQueue queue = new MCQueue("LOCAL:" + name);
        queue.setMessenger( getMessenger() );
        queue.addUnhandledMessageListener(new MessageUtilizer());
        queues.put( name, queue);
        return true;
    }
    /**
     * Get system messager
     * @return the messenger object
     */
    public static MCMessenger getMessenger()
    {
        return messenger;
    }
    /**
     * Get local message queue by name
     * @return a local queue
     */
    public static MCQueue getLocalMessageQueue(String name)
    {
        return (MCQueue) queues.get(name);
    }
    /**
     * Get system message queue
     * @return the message queue object
     */
    public static MCQueue getQueue()
    {
        return queue;
    }
    /**
     * Initialize system messenger.
     */
    static void init()
    {
      queue.setMessenger(getMessenger());
      queue.setQueueName("LOCAL:SYSTEM");
      queue.addUnhandledMessageListener(new MessageUtilizer());
      /**
       * Add local transport to the messager.
       */
      addMessengerTransport(new MCDirectTransport());
      dispatchTimer.schedule(new DispatchTask(), 100, 100);
    }
    /**
     * Destroy system messenger.
     */
    static void destroy()
    {
       messenger.destroy();
       dispatchTimer.cancel();
       dispatchTimer = null;
    }

    /**
     * Returns message credentials - user name and password,
     * both are taken from user manager engine.
     * @return message credentials
     */
     public static MCMessageCredentials getMessageCredentials()
     {
        String login = Workspace.getProfilesEngine().getUserName();
        String pwd = new String( Workspace.getProfilesEngine().getCipherPassword() );
        return new MCMessageCredentials(login, pwd);
     }
}
