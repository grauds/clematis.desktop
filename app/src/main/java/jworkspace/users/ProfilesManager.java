package jworkspace.users;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.Font;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;

/**
 * This class can add, rename, delete or list user profiles.
 * There can be only one instance of manager in workspace.
 */
@SuppressWarnings("unused")
class ProfilesManager implements Comparator {

    private static final String DEFAULT_USER_NAME = "root";

    private static final Logger LOG = LoggerFactory.getLogger(ProfilesManager.class);

    private static final String PROFILES_MANAGER_PROFILE_NULL = "ProfilesManager.profile.null";

    private static final String USERS = "users";

    private static final String PROFILE_DAT = "profile.dat";

    private Profile currentProfile = null;

    ProfilesManager() {
        super();
    }

    /**
     * Adds previously constructed profile to database.
     */
    public void add(Profile profile) throws ProfileOperationException, IOException {

        if (profile == null) {
            throw new ProfileOperationException(LangResource.getString(PROFILES_MANAGER_PROFILE_NULL));
        }
        if (getProfilesList().contains(profile.getUserName())) {
            throw new ProfileOperationException(LangResource.getString("ProfilesManager.profile.alreadyExists"));
        }

        saveProfile(profile);
    }

    /**
     * Compare profiles routine.
     */
    public int compare(Object obj1, Object obj2) {

        if (obj1 instanceof Profile && obj2 instanceof Profile) {
            return (((Profile) obj1).getUserName()).compareTo(((Profile) obj2).getUserName());
        }
        return 0;
    }

    /**
     * Delete profile.
     */
    public void delete(Profile profile, String password) throws ProfileOperationException {

        if (profile == null) {
            throw new ProfileOperationException(LangResource.getString(PROFILES_MANAGER_PROFILE_NULL));
        }
        if (!profile.checkPassword(password)) {
            throw new ProfileOperationException(LangResource.getString("ProfilesManager.passwd.check.failed"));
        }

        File file = new File(getProfileFolder(profile.getUserName()));
        KiwiUtils.deleteTree(file);
    }

    /**
     * Returns admin profile
     */
    public Profile getAdminProfile() throws ProfileOperationException {
        try {
            return loadProfile(DEFAULT_USER_NAME);
        } catch (IOException e) {
            throw new ProfileOperationException(e.getMessage());
        }
    }

    /**
     * Returns current profile
     */
    Profile getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Sets current profile by its name.
     */
    public void setCurrentProfile(String name) throws ProfileOperationException {

        if (name == null || currentProfile != null && currentProfile.getUserName().equals(name)) {
            return;
        }

        if (currentProfile != null && !currentProfile.getUserName().equals(name)) {
            saveCurrentProfile();

        }

        currentProfile = getProfile(name);
    }

    /**
     * Sets current profile.
     */
    void setCurrentProfile(Profile profile) {

        if (profile == null || currentProfile != null && currentProfile.equals(profile)) {
            return;
        }

        if (currentProfile != null && !currentProfile.equals(profile)) {
            saveCurrentProfile();
        }

        currentProfile = profile;
    }

    /**
     * Returns current profiles folder path relative to
     * "user.dir" system property.
     *
     * @return java.lang.String
     */
    String getCurrentProfileFolder() {
        return USERS + File.separator + currentProfile.getUserName();
    }

    /**
     * Returns profile by its name.
     */
    Profile getProfile(String name) throws ProfileOperationException {
        if (name == null) {
            throw new ProfileOperationException(LangResource.getString("ProfilesManager.name.null"));
        }
        try {
            return loadProfile(name);
        } catch (IOException e) {
            throw new ProfileOperationException(e.getMessage());
        }
    }

    /**
     * Returns profiles folder path relative to
     * "user.dir" system property.
     *
     * @param profileName java.lang.String
     * @return java.lang.String
     */
    String getProfileFolder(String profileName) {
        return USERS + File.separator + profileName;
    }

    /**
     * Returns sorted list of profiles.
     * This actually traverses /users directory.
     */
    Vector getProfilesList() {

        Vector<String> list = new Vector<>();

        File file = new File(System.getProperty("user.dir") + File.separator + USERS);
        File[] dirs = file.listFiles();

        if (dirs != null) {
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    list.addElement(dir.getName());
                }
            }
        }

        Collections.sort(list);
        return list;
    }

    /**
     * Loads profile from disk
     *
     * @param userName java.lang.String
     * @return jworkspace.users.Profile
     */
    private Profile loadProfile(String userName) throws IOException {

        Profile profile = new Profile();
        String path = Workspace.getBasePath() + getProfileFolder(userName);

        if (!Files.exists(Paths.get(path))) {
            Files.createDirectories(Paths.get(path));
        }

        try (FileInputStream ifile = new FileInputStream(path + File.separator + PROFILE_DAT);
             DataInputStream di = new DataInputStream(ifile)) {

            profile.load(di);

        } catch (FileNotFoundException e) {
            /*
             * It seems that we have loaded profile for the first time.
             */
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_change.png"));

            StringBuilder message = new StringBuilder();

            message.append("<html><font color=\"black\" >");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message1"));
            message.append(" <b>").append(userName).append("</b>.<br>");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message2"));
            message.append(".<br><i>");
            message.append(LangResource.getString("ProfileEngine.newProfileAdded.message3"));
            message.append(" ");
            message.append("<b>").append(userName).append("</b>");
            message.append(".");
            message.append("</i></font></html>");

            JLabel lmess = new JLabel(message.toString());
            lmess.setFont(lmess.getFont().deriveFont(Font.PLAIN));

            JOptionPane.showMessageDialog(new Frame(),
                lmess,
                LangResource.getString("ProfileEngine.newProfileAdded.title"),
                JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (IOException e) {
            LOG.error(LangResource.getString("ProfilesManager.profile.load.failed"), e);
        }

        if (profile.getUserName() == null || profile.getUserName().trim().equals("")) {
            profile.setUserName(userName);
        }
        return profile;
    }

    /**
     * Saves current profile on disk.
     */
    void saveCurrentProfile() {

        String fileName = getProfileFolder(currentProfile.getUserName());

        File file = new File(fileName);

        try {
            if (!file.exists()) {
                FileUtils.forceMkdir(file);
            }
            saveProfile(fileName);
        } catch (IOException e) {
            LOG.error(LangResource.getString("ProfilesManager.profile.save.failed"), e);
        }
    }

    /**
     * Saves profile on disk.
     *
     * @param profile to save
     */
    private void saveProfile(Profile profile) throws IOException {

        String fileName = getProfileFolder(profile.getUserName());

        File file = new File(fileName);

        if (!file.exists()) {
            FileUtils.forceMkdir(file);
        }

        saveProfile(fileName);
    }

    private void saveProfile(String fileName) throws IOException {

        FileOutputStream ofile = new FileOutputStream(fileName + File.separator + PROFILE_DAT);
        DataOutputStream os = new DataOutputStream(ofile);
        currentProfile.save(os);
        ofile.close();
    }
}