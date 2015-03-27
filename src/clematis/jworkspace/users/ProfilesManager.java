package jworkspace.users;

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

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.util.WorkspaceError;
import jworkspace.util.sort.QuickSort;
import jworkspace.util.sort.Sorter;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Vector;

/**
 * This class can add, rename, delete or list user profiles.
 * There can be only one instance of manager in workspace.
 */

/**
 * Change log:
 * 22.11.01 - ProfilesManager is no more serializable,
 * it maintains a list of users as a folder list in
 * "users" directory.
 */
class ProfilesManager implements Sorter
{
    private Profile currentProfile = null;
    public static final String DEFAULT_USER_NAME = "root";

    protected ProfilesManager() throws ProfileOperationException
    {
        super();
    }

    /**
     * Adds previously constructed profile to database.
     */
    public void add(Profile profile) throws ProfileOperationException, IOException
    {
        if (profile == null)
        {
            throw new ProfileOperationException
                    (LangResource.getString("ProfilesManager.profile.null"));
        }
        if (getProfilesList().contains(profile.getUserName()))
        {
            throw new ProfileOperationException
                    (LangResource.getString("ProfilesManager.profile.alreadyExists"));
        }

        saveProfile(profile);
    }

    /**
     * Compare profiles routine.
     */
    public int compare(Object obj1, Object obj2)
    {
        if (obj1 instanceof Profile &&
                obj2 instanceof Profile)
        {
            return -(((Profile) obj1).getUserName()).
                    compareTo(((Profile) obj2).getUserName());
        }
        return 0;
    }

    /**
     * Delete profile.
     */
    public void delete(Profile profile, String password)
            throws ProfileOperationException
    {
        if (profile == null)
        {
            throw new ProfileOperationException
                    (LangResource.getString("ProfilesManager.profile.null"));
        }
        if (!profile.checkPassword(password))
        {
            throw new ProfileOperationException
                    (LangResource.getString("ProfilesManager.passwd.check.failed"));
        }

        File file = new File(getProfileFolder(profile.getUserName()));
        kiwi.util.KiwiUtils.deleteTree(file);
    }

    /**
     * Returns admin profile
     */
    public Profile getAdminProfile() throws ProfileOperationException
    {
        Profile admin = loadProfile( DEFAULT_USER_NAME );
        if (admin == null)
        {
            admin = Profile.create(ProfilesManager.DEFAULT_USER_NAME, "", "", "", "");
        }
        return admin;
    }

    /**
     * Returns current profile
     */
    public Profile getCurrentProfile()
    {
        return currentProfile;
    }

    /**
     * Returns current profiles folder path relative to
     * "user.dir" system property.
     * @return java.lang.String
     */
    public String getCurrentProfileFolder()
    {
        return "users" + File.separator + currentProfile.getUserName();
    }

    /**
     * Returns profile by its name.
     */
    public Profile getProfile(String name) throws ProfileOperationException
    {
        if (name == null)
        {
            throw new ProfileOperationException
                    (LangResource.getString("ProfilesManager.name.null"));
        }
        return loadProfile(name);
    }

    /**
     * Returns profiles folder path relative to
     * "user.dir" system property.
     * @return java.lang.String
     * @param profileName java.lang.String
     */
    public String getProfileFolder(String profileName)
    {
        return "users" + File.separator + profileName;
    }

    /**
     *  Returns sorted list of profiles.
     *  This actually traverses /users directory.
     */
    public Vector getProfilesList()
    {
        Vector list = new Vector();

        File file = new File(System.getProperty("user.dir") + File.separator + "users");
        File[] dirs = file.listFiles();

        for (int i = 0; i < dirs.length; i++)
            if (dirs[i].isDirectory())
                list.addElement(dirs[i].getName());

        (new QuickSort(this)).sort(list);

        return list;
    }

    /**
     * Loads profile from disk
     * @return jworkspace.users.Profile
     * @param userName java.lang.String
     */
    protected Profile loadProfile(String userName)
    {
        Profile profile = new Profile();
        try
        {
            FileInputStream ifile = new FileInputStream(getProfileFolder(userName) +
                                                        File.separator + "profile.dat");
            DataInputStream di = new DataInputStream(ifile);
            profile.load(di);
            ifile.close();
        }
        catch (FileNotFoundException e)
        {
            /**
             * It seems that we have loaded profile for the first time.
             */
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                                           getImage("user_change.png"));
            StringBuffer message = new StringBuffer();
            message.append("<html><font color=\"black\" >");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message1"));
            message.append(" ");
            message.append("<b>" + userName + "</b>");
            message.append(".");
            message.append("<br>");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message2"));
            message.append(".");
            message.append("<br><i>");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message3"));
            message.append(" ");
            message.append("<b>" + userName + "</b>");
            message.append(".");
            message.append("</i></font></html>");

            JLabel lmess = new JLabel(message.toString());
            lmess.setFont(lmess.getFont().deriveFont(Font.PLAIN));

            JOptionPane.showMessageDialog( new Frame(),
                                          lmess,
                                          LangResource.getString("ProfileEngine.newProfileAdded.title"),
                                          JOptionPane.INFORMATION_MESSAGE, icon);
        }
        catch (IOException e)
        {
            WorkspaceError.exception
                    (LangResource.getString("ProfilesManager.profile.load.failed"), e);
        }

        if (profile.getUserName() == null || profile.getUserName().trim().equals(""))
        {
            profile.setUserName(userName);
        }
        return profile;
    }

    /**
     * Saves current profile on disk.
     */
    public void saveCurrentProfile()
    {
        String fileName = getProfileFolder(currentProfile.getUserName());

        File file = new File(fileName);

        if (!file.exists())
            file.mkdirs();

        try
        {
            FileOutputStream ofile = new FileOutputStream(fileName
                                                          + File.separator + "profile.dat");
            DataOutputStream os = new DataOutputStream(ofile);
            currentProfile.save(os);
            ofile.close();
        }
        catch (IOException e)
        {
            WorkspaceError.exception
                    (LangResource.getString("ProfilesManager.profile.save.failed"), e);
        }
    }

    /**
     * Saves profile on disk.
     * @param profile to save
     */
    protected void saveProfile(Profile profile) throws IOException
    {
        String fileName = getProfileFolder(profile.getUserName());

        File file = new File(fileName);

        if (!file.exists())
            file.mkdirs();

        FileOutputStream ofile = new FileOutputStream(fileName
                                                      + File.separator + "profile.dat");
        DataOutputStream os = new DataOutputStream(ofile);
        currentProfile.save(os);
        ofile.close();
    }

    /**
     * Sets current profile by its name.
     */
    public void setCurrentProfile(String name) throws ProfileOperationException
    {
        if (name == null)
            return;

        if (currentProfile != null && currentProfile.getUserName().equals(name))
        {
            return;
        }
        else if (currentProfile != null && !currentProfile.getUserName().equals(name))
        {
            saveCurrentProfile();
            currentProfile = getProfile(name);
            return;
        }
        else if (currentProfile == null)
        {
            currentProfile = getProfile(name);
        }
    }

    /**
     * Sets current profile.
     */
    public void setCurrentProfile(Profile profile)
    {
        if (profile == null)
            return;

        if (currentProfile != null && currentProfile.equals(profile))
        {
            return;
        }
        else if (currentProfile != null && !currentProfile.equals(profile))
        {
            saveCurrentProfile();
            currentProfile = profile;
            return;
        }
        else if (currentProfile == null)
        {
            currentProfile = profile;
        }
    }
}