package jworkspace.network.server.group;

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

import jworkspace.kernel.*;
import com.sun.media.jsdt.*;

import jworkspace.network.datagram.DataPackage;
import jworkspace.network.server.ServerInfo;
import jworkspace.network.server.content.ContentManager;
import jworkspace.network.user.UserConstants;
import jworkspace.network.SharedConstants;

import java.io.StreamCorruptedException;
/**
 * This is a group requests broker. It acts as a server-side
 * group administrator, providing means to manage users,
 * resources (ByteArrays) and tokens.
 */
public class GroupManager implements Client, ChannelConsumer
{
    /**
     * Nested content manager
     */
    protected ContentManager contentManager = null;
    /**
     * Public constructor with a content manager as a parameter for the
     * group, this class managers.
     * @param contentManager group content manager
     */
    public GroupManager(ContentManager contentManager)
    {
        super();
        this.contentManager = contentManager;
    }

    public Object authenticate(AuthenticationInfo authenticationInfo)
    {
        return new Long(1245879614);
    }

    public String getName()
    {
        return SharedConstants.DEFAULT_BOT_NAME;
    }
    /**
     * Receives commands, sent across the group channel.
     * @param data
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
             * Get command from incoming package description.
             */
            String command = incomingPackage.getProperties().
                    getProperty(DataPackage.DESCRIPTION);
            /**
             * Received command
             */
            Workspace.getLogger().warning("> Group administration command: " + command);
            if (command.equalsIgnoreCase(ServerInfo.CK_LOAD_RESOURCE_REQUEST))
            {
                loadResource(incomingPackage, data.getChannel(), data.getSenderName());
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (StreamCorruptedException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Load file from server directories, parse it to object and
     * make a byte array with relative file path and contents of loaded file.
     * Sends reply with CK_LOAD_RESOURCE_REQUEST description to sender
     * if succeeded, and CK_SERVER_ERROR if failed.
     * @param incomingPackage incoming command
     * @param channel group channel
     * @param senderName the name of sender
     */
    public void loadResource(DataPackage incomingPackage, Channel channel,
                                                          String senderName )
    {
        /**
         * Get relative path to required resource
         */
        String path = (String) incomingPackage.getContents();
        try
        {
            ByteArray barr = contentManager.load(this, path);
            /**
             * Send document to client
             */
            if (barr != null)
            {
               DataPackage outgoingPackage = new DataPackage();
               outgoingPackage.setContents(barr.getValueAsString());
               outgoingPackage.getProperties().setProperty(
                   DataPackage.DESCRIPTION,
                   UserConstants.XML);
               channel.sendToClient(this, senderName,
                                    new Data(outgoingPackage));
            }
             /**
              * Send server error if IO exception has occured during
              * resource loading.
              */
             else
             {
               DataPackage outgoingPackage = new DataPackage();
               outgoingPackage.setContents("Cannot load resource " + path);
               outgoingPackage.getProperties().setProperty(
                   DataPackage.DESCRIPTION,
                   ServerInfo.CK_SERVER_ERROR);
               channel.sendToClient(this, senderName,
                                    new Data(outgoingPackage));
             }
        }
        catch (JSDTException e)
        {
            e.printStackTrace();
        }
    }
}
