package jworkspace.network.server.content;

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
import com.sun.media.jsdt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jworkspace.network.UserGroup;
/**
 * Content manager is a class for getting group resources from server
 * hard disk and converting it into ByteArrays objects within a group
 * session. This allows files to be shared between group users and accessed
 * in any time. Such files can also be modified by users and saved back on disk.
 * Users also can upload they resources to group and share them with over
 * group users. All editions could be synchronized with a help of Tokens.
 */
public class ContentManager
{
    /**
     * The name of group
     */
    private UserGroup group = null;
    /**
     * Public constructor
     */
    public ContentManager(UserGroup group)
    {
        super();
        this.group = group;
    }
    public UserGroup getGroup()
    {
        return group;
    }
    /**
     * Loads resource from the relative path within a given group and
     * makes it available at group session as byte array. If byte array
     * with given name already exists, does nothing.
     * @param client server-side group manager instance
     * @param path relative path to server resource
     */
    public ByteArray load(Client client, String path) throws JSDTException
    {
        /**
         * Get reference to a group session
         */
        Session session = SessionFactory.
                         createSession(client, group.getSessionURL(), false);
        /**
         * Result byte array
         */
        ByteArray barr = null;
        byte[] content = new byte[0];

        if (! session.byteArrayExists(path))
        {
            /**
             * Java Workspace user path.
             */
            StringBuffer dir = new StringBuffer();
            dir.append(System.getProperty("user.dir"));
            dir.append(File.separator);
            dir.append("config");
            dir.append(File.separator);
            dir.append("network");
            dir.append(File.separator);
            dir.append("groups");
            dir.append(File.separator);
            dir.append(getGroup().getName());
            /**
             * If path does not exists, just create directories
             * and return nothing.
             */
            File file = new File(dir.toString());
            if (!file.exists())
            {
                file.mkdirs();
                return barr;
            }
            /**
             * Here we have to deal with group resources. It can be any file
             * in any format, which we should read into array of bytes and
             * record in a ByteArray. The structure of disk directories is
             * tree, but we won't organize trees from ByteArray's. Otherwize,
             * ByteArray's names will be the relative paths.
             */
            dir.append(File.separator);
            file = new File(dir.toString() + path);

            try
            {
                FileInputStream fis = new FileInputStream(file);
                content = new byte[(int)file.length()];
                fis.read(content);
            }
            catch (IOException e)
            {
                return barr;
            }
           /**
            * Client creates and joins byte array, sets its value and leaves it.
            */
            barr = session.createByteArray(client, path, true);
            barr.setValue(client, content);
            barr.leave(client);
        }
        else
        {
            barr = session.createByteArray(client, path, false);
        }
        return barr;
  }
}