package jworkspace.network.user;

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

import java.io.*;
import java.util.*;

import com.sun.media.jsdt.*;
import jworkspace.kernel.*;
import jworkspace.network.*;
import kiwi.util.*;

/**
 * The main entity class for the client application.
 * This class holds data structures and fields that must be shared by
 * several client application components.
 */
public class UserInfo extends SharedConstants
{
    /**
     * A reference to group management session of
     * group, this user belongs to. Often used
     * to send system data to other participants of
     * group.
     */
    private Session groupManagementSession = null;
    /**
     * A reference to group management channel of
     * group, this user belongs to. Often used
     * to send system data to other participants of
     * group.
     */
    private Channel groupManagementChannel = null;
    /**
     * A list of all groups, registered on server
     */
    private Hashtable groups = null;
    /**
     * Active group is user currently selected for activity.
     */
    private UserGroup activeGroup = null;
    /**
     * Default file name
     */
    private final static String PREFERENCES_FILE_NAME = "jsdtuser.cfg";
    /**
     * Preferences of user.
     */
    private static Config preferences = null;
    /**
     * Is user online or offline.
     */
    private boolean online = false;
    /**
     * ClientInfo constructor comment.
     */
    public UserInfo()
    {
        super();
    }
    /**
     * Create unique client id as concatenation of default
     * name + system time in milliseconds + local host IP address or
     * default name + system time if the IP address is unavailable.
     */
    public static String createUniqueClientID()
    {
        String ID = DefaultAgent.getAgentName()
                + "/" + (System.currentTimeMillis()) + "/";
        try
        {
            ID = ID + java.net.InetAddress.getLocalHost().toString();
        }
        catch (java.net.UnknownHostException UHE)
        {
            UHE.printStackTrace();
        }
        return ID;
    }

    /**
     * Get active group.
     */
    public UserGroup getActiveGroup()
    {
        return activeGroup;
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
     * Get all groups this user aware of.
     */
    public Hashtable getGroups()
    {
        return groups;
    }

    /**
     * Is user in online mode
     */
    public boolean isOnline()
    {
        return online;
    }

    /**
     * Get user preferences
     */
    public static Config getPreferences()
    {
        if (preferences == null)
        {
            preferences = new Config("Java Workspace JSDT user definition file");
            /**
             * Default user name
             */
            preferences.put(UserConstants.CK_USER_NAME, Workspace.getProfilesEngine().getUserName());
            /**
             * Default server name
             */
            preferences.put(UserConstants.CK_SERVER_NAME, SharedConstants.DEFAULT_SERVER_NAME);
            /**
             * Default server port
             */
            preferences.put(UserConstants.CK_SERVER_PORT, SharedConstants.DEFAULT_SERVER_PORT);
            /**
             * Default server type
             */
            preferences.put(UserConstants.CK_SERVER_TYPE, SharedConstants.DEFAULT_SERVER_TYPE);
            /**
             * Default gm session
             */
            preferences.put(UserConstants.CK_MANAGEMENT_SESSION_NAME, SharedConstants.DEFAULT_GROUP_MANAGEMENT_SESSION_NAME);
            /**
             * Default gm channel
             */
            preferences.put(UserConstants.CK_MANAGEMENT_CHANNEL_NAME, SharedConstants.DEFAULT_GROUP_MANAGEMENT_CHANNEL_NAME);
            /**
             * Default registry port
             */
            preferences.put(UserConstants.CK_REGISTRY_PORT, SharedConstants.DEFAULT_REGISTRY_PORT);
            /**
             * Default registry type
             */
            preferences.put(UserConstants.CK_REGISTRY_TYPE, SharedConstants.DEFAULT_REGISTRY_TYPE);
            /**
             * Default bot name
             */
            preferences.put(UserConstants.CK_BOT_NAME, SharedConstants.DEFAULT_BOT_NAME);
            /**
             * Read preferences from disk.
             */
            readPreferences();
            /**
             * If the client has not yet been assigned a
             * unique identifier, do so now. The new ID will be saved to disk upon exit
             */
            if (preferences.get(UserConstants.CK_USER_ID) == null)
            {
                preferences.put(UserConstants.CK_USER_ID, createUniqueClientID());
            }
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
     * This approach will return a valid object even if the
     * read fails or in case of the application being used
     * for the first time.
     * Creation date: (25-06-00 13:54:55)
     */
    public static void readPreferences()
    {
        /**
         * Java Workspace user path.
         */
        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.home"));
        path.append(File.separator);
        path.append("network");
        path.append(File.separator);
        path.append(UserInfo.PREFERENCES_FILE_NAME);

        File file = new File(path.toString());
        if (file.exists())
        {
            try
            {
                FileInputStream f = new FileInputStream(path.toString());
                preferences.load(f);
                f.close();
            }
            catch (FileNotFoundException ffe)
            {
                /**
                 * 	The exception can be handled by ignoring it since the
                 *  default values in p will substitute for what should
                 *  have been read from file.
                 */
            }
            catch (IOException ie)
            {
            }
        }
    }

    /**
     * Set active work group
     */
    public void setActiveGroup(UserGroup activeGroup)
    {
        this.activeGroup = activeGroup;
    }

    /**
     * Set group management channel
     */
    public void setGroupManagementChannel(Channel groupManagementChannel)
    {
        this.groupManagementChannel = groupManagementChannel;
    }

    /**
     * Set group management session.
     */
    public void setGroupManagementSession(Session groupManagementSession)
    {
        this.groupManagementSession = groupManagementSession;
    }

    /**
     * Set groups from server this user connected to.
     */
    public void setGroups(Hashtable groups)
    {
        this.groups = groups;
    }

    /**
     * Set online mode of user
     */
    public void setOnline(boolean online)
    {
        this.online = online;
    }

    /**
     * Write preferences to disk file.
     */
    public static void writePreferences()
    {
        /**
         * Java Workspace user path.
         */
        if (preferences == null)
        {
            return;
        }

        StringBuffer path = new StringBuffer();
        path.append(System.getProperty("user.home"));
        path.append(File.separator);
        path.append("network");
        File file = new File(path.toString());

        if (!file.exists())
        {
            file.mkdirs();
        }
        path.append(File.separator);
        path.append(UserInfo.PREFERENCES_FILE_NAME);

        try
        {
            FileOutputStream f = new FileOutputStream(path.toString());
            preferences.store(f, "Java Workspace JSDT user preferences.");
            f.close();
        }
        catch (IOException ie)
        {
        }

        preferences = null;
    }
}