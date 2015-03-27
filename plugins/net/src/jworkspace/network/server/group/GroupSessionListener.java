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

import com.sun.media.jsdt.event.SessionAdaptor;

import com.sun.media.jsdt.event.*;
import jworkspace.kernel.*;
import jworkspace.network.*;
import jworkspace.network.server.*;
/**
 * This listener dynamically supports a list of active users in
 * users group. This list can be retrieved from the group at any time.
 * Listener also notifies about user join or left server.
 */
public class GroupSessionListener extends SessionAdaptor
{
    /**
     * The id of users group that this listener is created for.
     */
    protected String groupID = null;
    /**
     * An instance of server manager
     */
    protected Server server = null;

    public GroupSessionListener(String groupID, Server server)
    {
        this.groupID = groupID;
        this.server = server;
    }

    /**
     * User joined group session.
     */
    public void sessionJoined(SessionEvent se)
    {
        userJoinedGroup(se.getClientName(),
                        (UserGroup)server.getServerInfo().getGroups().get(groupID));
    }

    /**
     * User left group session.
     */
    public void sessionLeft(SessionEvent se)
    {
        userLeftGroup(se.getClientName(),
                      (UserGroup)server.getServerInfo().getGroups().get(groupID));
    }

    /**
     * This method is invoked each time user joins group
     */
    public void userJoinedGroup(String userName, UserGroup userGroup)
    {
        userGroup.getUsers().add(userName);
        server.getServerInfo().getGroups().put
                (userGroup.getUsersGroupID(), userGroup);
        server.getServerManager().broadcastGroupList();
        Workspace.getLogger().warning("User " + userName + " joined group " +
                           userGroup.getName());
    }

    /**
     * This method is invoked each time user joins group
     */
    public void userLeftGroup(String userName, UserGroup userGroup)
    {
        if (userGroup.hasUser(userName))
        {
            userGroup.getUsers().remove(userName);
            server.getServerInfo().getGroups().put
                    (userGroup.getUsersGroupID(), userGroup);
            server.getServerManager().broadcastGroupList();
            Workspace.getLogger().warning("User " + userName + " left group " +
                               userGroup.getName());
        }
    }
}