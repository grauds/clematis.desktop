package jworkspace.network.server;

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

import java.io.*;
import java.util.*;

import com.sun.media.jsdt.*;
import jworkspace.network.*;
import kiwi.util.*;

/**
 * This class holds data structures and fields for
 * server. All default data is used only if user
 * had not provided configuration file for server.
 */
public class ServerInfo extends SharedConstants
{
    /**
     * Default file name
     */
    private final static String PREFERENCES_FILE_NAME = "jsdtserver.cfg";
    /**
     * Group data file name
     */
    public final static String GROUPS_FILE_NAME = "group.xml";
    /**
     * Registered users file name
     */
    public final static String REGISTERED_USERS_FILE_NAME = "users.cfg";
    /**
     * Server preferences there above settings are actually stored.
     */
    private static Config preferences = null;
    /**
     * System session for group management
     */
    private Session groupManagementSession = null;
    /**
     * System channel for exchanging commands
     * and system messages.
     */
    private Channel groupManagementChannel = null;
    /**
     * Groups of users on this server
     */
    private Hashtable groups = new Hashtable();
    /**
     * ServerInfo constructor comment.
     */
    public ServerInfo()
    {
        super();
    }
    /**
     * Get group management channel
     */
    public Channel getGroupManagementChannel()
    {
        return groupManagementChannel;
    }
    /**
     * Get group management session
     */
    public Session getGroupManagementSession()
    {
        return groupManagementSession;
    }
    /**
     * Get all groups
     */
    public Hashtable getGroups()
    {
        if (groups == null)
        {
            groups = new Hashtable();
        }
        return groups;
    }
    /**
     * Return a set of preferences.
     */
    public static Config getPreferences()
    {
        if (preferences == null)
        {
            preferences = new Config("Local Java Workspace JSDT Server definition file\nThis server is started locally as a service");

            preferences.put(ServerInfo.CK_SERVER_NAME,
                            SharedConstants.DEFAULT_SERVER_NAME);

            preferences.put(ServerInfo.CK_SERVER_PORT,
                            new Integer(SharedConstants.DEFAULT_SERVER_PORT).toString());

            preferences.put(ServerInfo.CK_SERVER_TYPE,
                            SharedConstants.DEFAULT_SERVER_TYPE);

            preferences.put(ServerInfo.CK_REGISTRY_PORT,
                            SharedConstants.DEFAULT_REGISTRY_PORT);

            preferences.put(ServerInfo.CK_REGISTRY_TYPE,
                            SharedConstants.DEFAULT_REGISTRY_TYPE);

            preferences.put(ServerInfo.CK_MANAGEMENT_SESSION_NAME,
                            SharedConstants.DEFAULT_GROUP_MANAGEMENT_SESSION_NAME);

            preferences.put(ServerInfo.CK_MANAGEMENT_CHANNEL_NAME,
                            SharedConstants.DEFAULT_GROUP_MANAGEMENT_CHANNEL_NAME);

            preferences.put(ServerInfo.CK_BOT_NAME,
                            SharedConstants.DEFAULT_BOT_NAME);

            readPreferences();
        }
        return preferences;
    }
    /**
     * Read preferences from Properties object on disk.
     * Each call to this function results in a read
     * from disk rather than an internal lookup, i.e.
     * the values read from disk are not cached as a class member.
     * This eliminates data redundancy and ensures consistency.
     * The overhead is neglible as the file in question is essentially
     * plain text.
     * Algorithm:
     * 1. Construct new Properties object P
     * 2. Initialize P with default preferences
     * 3. Read user preferences from disk file
     * 4. IF read is successful THEN
     * 4.1. Store user preferences in P
     * 5. Return P
     * This approach will return a valid object even if the read fails
     * or in case of the application being used for the first
     * time.
     */
    public static void readPreferences()
    {
        /**
         * Java Workspace user path.
         */
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append(ServerInfo.PREFERENCES_FILE_NAME);

        File file = new File(path.toString());
        if (file.exists())
        {
            try
            {
                FileInputStream f = new FileInputStream(file);
                getPreferences().load(f);
                f.close();
            }
            catch (FileNotFoundException ffe)
            {
                // the exception can be handled by ignoring it since the default values in p will substitute for what should
                // have been read from file.
            }
            catch (IOException ie)
            {
            }
        }
    }
    /**
     * Sets group management channel
     */
    public void setGroupManagementChannel(Channel groupManagementChannel)
    {
        this.groupManagementChannel = groupManagementChannel;
    }
    /**
     * Sets group management session
     */
    public void setGroupManagementSession(Session groupManagementSession)
    {
        this.groupManagementSession = groupManagementSession;
    }
    /**
     * Sets all groups of users
     */
    public void setGroups(Hashtable groups)
    {
        this.groups = groups;
    }
    /**
     * Sets preferences for this server
     */
    public void setPreferences(Config preferences)
    {
        this.preferences = preferences;
    }
    /**
     * Write preferences to disk file.
     */
    public static void writePreferences() throws IOException
    {
        /**
         * Java Workspace user path.
         */
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.dir"));
        path.append(File.separator);
        path.append("config");
        path.append(File.separator);
        path.append("network");

        File file = new File(path.toString());

        if (!file.exists())
        {
            file.mkdirs();
        }
        path.append(File.separator);
        path.append(ServerInfo.PREFERENCES_FILE_NAME);

        FileOutputStream f = new FileOutputStream(path.toString());
        getPreferences().store
                (f, "Java Workspace Local JSDT Server Configuration File");
        f.close();
    }
}
