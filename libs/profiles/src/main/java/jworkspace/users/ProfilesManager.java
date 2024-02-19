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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.KiwiUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.ProfileOperationException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * This class can add, rename, delete or list user profiles.
 * @author Anton Troshin
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public class ProfilesManager implements Comparator {

    private static final String DEFAULT_USER_NAME = "root";

    private static final Logger LOG = LoggerFactory.getLogger(ProfilesManager.class);

    private static final String PROFILES_MANAGER_PROFILE_NULL = "ProfilesManager.profile.null";

    private static final String USERS = "users";

    private Path basePath;

    private Profile currentProfile = null;

    private final Profile defaultProfile = new Profile("default", "", "", "", "");

    ProfilesManager(Path basePath) {
        super();
        this.basePath = basePath;
    }

    /**
     * Adds previously constructed profile to database.
     */
    public void add(Profile profile) throws ProfileOperationException, IOException {

        if (profile == null) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILES_MANAGER_PROFILE_NULL));
        }
        if (getProfilesList().contains(profile.getUserName())) {
            throw new ProfileOperationException(
                WorkspaceResourceAnchor.getString("ProfilesManager.profile.alreadyExists"));
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
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILES_MANAGER_PROFILE_NULL));
        }
        if (!profile.checkPassword(password)) {
            throw new ProfileOperationException(
                WorkspaceResourceAnchor.getString("ProfilesManager.passwd.check.failed"));
        }

        File file = profile.getProfilePath(getBasePath()).toFile();
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

    void clearCurrentProfile() {
        currentProfile = null;
    }

    /**
     * Returns current profiles folder path relative to "user.home" system property and ensures it exists
     *
     * @return java.lang.String
     */
    Path ensureCurrentProfilePath(Path basePath) throws IOException {
        return getCurrentProfile().ensureProfilePath(basePath);
    }

    /**
     * Returns current profiles folder path relative to "user.home" system property.
     *
     * @return java.lang.String
     */
    Path getCurrentProfilePath(Path basePath) {
        return getCurrentProfile().getProfilePath(basePath);
    }

    /**
     * Returns profile by its name.
     */
    Profile loadProfile(String name) throws ProfileOperationException {
        if (name == null) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString("ProfilesManager.name.null"));
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
    List<String> getProfilesList() {

        List<String> list = new ArrayList<>();

        File file = Paths.get(getBasePath().toString(), USERS).toFile();
        File[] dirs = file.listFiles();

        if (dirs != null) {
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    list.add(dir.getName());
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
    private Profile readProfile(String userName) throws IOException {

        Profile profile = new Profile();
        profile.setUserName(userName);
        profile.load(getBasePath());
        return profile;
    }

    /**
     * Saves current profile on disk.
     */
    void saveCurrentProfile() throws IOException {
        saveProfile(getCurrentProfile());
    }

    /**
     * Saves profile on disk.
     *
     * @param profile to save
     */
    private void saveProfile(Profile profile) throws IOException {

        if (profile == null) {
            return;
        }

        profile.save(getBasePath());
    }

    Profile getCurrentProfile() {
        if (currentProfile == null) {
            return defaultProfile;
        } else {
            return currentProfile;
        }
    }

    Profile getDefaultProfile() {
        return defaultProfile;
    }
}