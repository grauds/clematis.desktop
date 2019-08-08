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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

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

        File file = new File(profile.getProfilePath());
        if (KiwiUtils.deleteTree(file) == 0) {
            LOG.warn("No files were deleted for " + profile.getUserName());
        }
    }

    /**
     * Returns admin profile
     */
    public Profile loadAdminProfile() throws ProfileOperationException {
        try {
            return readProfile(DEFAULT_USER_NAME);
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

    void clearCurrentProfile() {
        currentProfile = null;
    }

    /**
     * Sets current profile by its name.
     */
    void setCurrentProfile(String name) throws ProfileOperationException, IOException {

        if (name == null || currentProfile != null && currentProfile.getUserName().equals(name)) {
            return;
        }

        if (currentProfile != null && !currentProfile.getUserName().equals(name)) {
            saveCurrentProfile();

        }

        currentProfile = loadProfile(name);
    }

    /**
     * Returns current profiles folder path relative to "user.home" system property.
     *
     * @return java.lang.String
     */
    String getCurrentProfileRelativePath() throws IOException {
        if (currentProfile != null) {
            return currentProfile.getProfileRelativeFolder();
        } else {
            throw new IOException("Current profile is null");
        }
    }

    /**
     * Returns profile by its name.
     */
    Profile loadProfile(String name) throws ProfileOperationException {
        if (name == null) {
            throw new ProfileOperationException(LangResource.getString("ProfilesManager.name.null"));
        }
        try {
            return readProfile(name);
        } catch (IOException e) {
            throw new ProfileOperationException(e.getMessage());
        }
    }

    /**
     * Returns sorted list of profiles.
     * This actually traverses /users directory.
     */
    Vector getProfilesList() {

        Vector<String> list = new Vector<>();

        File file = new File(Workspace.getBasePath() + USERS + File.separator);
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
    private static Profile readProfile(String userName) throws IOException {

        Profile profile = new Profile();
        profile.setUserName(userName);
        profile.load();
        return profile;
    }

    /**
     * Saves current profile on disk.
     */
    void saveCurrentProfile() throws IOException {
        saveProfile(currentProfile);
    }

    /**
     * Saves profile on disk.
     *
     * @param profile to save
     */
    void saveProfile(Profile profile) throws IOException {

        if (profile == null) {
            return;
        }

        profile.save();
    }
}