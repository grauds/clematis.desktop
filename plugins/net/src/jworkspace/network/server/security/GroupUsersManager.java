package jworkspace.network.server.security;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
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
   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
 */
import java.io.File;
/**
 * Authentication of group users
 */
public class GroupUsersManager extends AbstractUsersManager
{
   /**
    * The name of the group, this class managers.
    */
    protected String groupName = null;
    /**
     * Constructor receives a name of managed group
     * @param groupName name of managed group
     */
    public GroupUsersManager(String groupName)
    {
      super();
      this.groupName = groupName;
      load();
    }
    /**
     * This method returns path to a user database file on server.
     * @return path to users database file
     */
    public StringBuffer getUsersDBPath()
    {
        StringBuffer path = new StringBuffer();

        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append("groups");
        path.append(File.separator);
        path.append(groupName);

        return path;
    }
}
