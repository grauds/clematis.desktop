package jworkspace.network;

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

/**
 * An entity object containing constants shared by
 * network applications.
 */
public class SharedConstants
{
// GROUP MANAGEMENT SESSION
    /**
     * The name of session for group management.
     */
    public static final String DEFAULT_GROUP_MANAGEMENT_SESSION_NAME = "GM_SESSION";
    /**
     * The name of channel for group management
     */
    public static final String DEFAULT_GROUP_MANAGEMENT_CHANNEL_NAME = "GM_CHANNEL";
// SERVER
    /**
     * The default name of server, holding entire system
     */
    public static final String DEFAULT_SERVER_NAME = "localhost";
    /**
     * The default port of server, holding entire system
     */
    public static final String DEFAULT_SERVER_PORT = "4665";
    /**
     * The default type of connection of server, holding entire system
     */
    public static final String DEFAULT_SERVER_TYPE = "socket";
    /**
     * Default server manager client name
     */
    public static final String DEFAULT_BOT_NAME = "gmanager";
// REGISTRY
    /**
     * Registry port
     */
    public static final String DEFAULT_REGISTRY_PORT = "4655";
    /**
     * Registry type
     */
    public static final String DEFAULT_REGISTRY_TYPE = "socket";
// KEYS
    /**
     * Actual server name
     */
    public static final String CK_SERVER_NAME = "server.name";
    /**
     * Actual server port
     */
    public static final String CK_SERVER_PORT = "server.port";
    /**
     * Connection type
     */
    public static final String CK_SERVER_TYPE = "server.type";
    /**
     * Registry port
     */
    public static final String CK_REGISTRY_PORT = "registry.port";
    /**
     * Registry type
     */
    public static final String CK_REGISTRY_TYPE = "registry.type";
    /**
     * Server manager client name
     */
    public static final String CK_BOT_NAME = "bot.name";
    /**
     * Management session name
     */
    public static final String CK_MANAGEMENT_SESSION_NAME = "msession.name";
    /**
     * Management channel name
     */
    public static final String CK_MANAGEMENT_CHANNEL_NAME = "mchannel.name";
// network commands
    /**
     * Add group request
     */
    public static final String CK_ADD_GROUP_REQUEST = "group.add.request";
    /**
     * Get group directory
     */
    public static final String CK_GROUP_DIRECTORY = "group.directory";
    /**
     * Remove group request
     */
    public static final String CK_REMOVE_GROUP_REQUEST = "group.remove.request";
    /**
     * Get group list request
     */
    public static final String CK_GET_GROUPS_REQUEST = "groups.get.request";
    /**
     * User joined server
     */
    public static final String CK_USER_JOINED_SERVER = "user.joined.server";
    /**
     * User left server
     */
    public static final String CK_USER_LEFT_SERVER = "user.left.server";
    /**
     * User joined group
     */
    public static final String CK_USER_JOINED_GROUP = "user.joined.group";
    /**
     * User left group
     */
    public static final String CK_USER_LEFT_GROUP = "user.left.group";
    /**
     * Message
     */
    public static final String CK_SERVER_MESSAGE = "server.message";
    /**
     * Error message
     */
    public static final String CK_SERVER_ERROR = "server.error";
    /**
     * Load group resource
     */
    public static final String CK_LOAD_RESOURCE_REQUEST = "group.resource.load.request";
}
