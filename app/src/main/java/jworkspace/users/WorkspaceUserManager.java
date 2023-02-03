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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.Config;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IUserManager;
import jworkspace.kernel.Workspace;
import lombok.NonNull;

/**
 * Profile engine is one of required by kernel.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public class WorkspaceUserManager implements IUserManager {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceUserManager.class);

    private static WorkspaceUserManager instance = null;

    private final ProfilesManager profilesManager;

    /**
     * Default public constructor.
     */
    private WorkspaceUserManager(Path basePath) {
        super();
        this.profilesManager = new ProfilesManager(basePath);
    }

    public static synchronized WorkspaceUserManager getInstance() {
        if (instance == null) {
            instance = new WorkspaceUserManager(Workspace.getBasePath());
        }
        return instance;
    }

    /**
     * Returns parameter from current user profile
     */
    @Override
    public Config getParameters() {
        return profilesManager.getCurrentProfile().getParameters();
    }

    /**
     * Returns user name from current user profile
     */
    @Override
    public String getUserName() {
        return profilesManager.getCurrentProfile().getUserName();
    }

    /**
     * Get human readable name for installer
     */
    @Override
    public String getName() {
        return "Java Workspace User Management v2.00";
    }

    /**
     * Returns user first name from current user profile
     */
    @Override
    public String getUserFirstName() {
        return profilesManager.getCurrentProfile().getUserFirstName();
    }

    /**
     * Set user first name
     */
    @Override
    public void setUserFirstName(String name) {
        profilesManager.getCurrentProfile().setUserFirstName(name);
    }

    /**
     * Returns user last name from current user profile
     */
    @Override
    public String getUserLastName() {
        return profilesManager.getCurrentProfile().getUserLastName();
    }

    /**
     * Set user last name
     */
    @Override
    public void setUserLastName(String name) {
        profilesManager.getCurrentProfile().setUserLastName(name);
    }

    /**
     * Returns user email from current user profile
     */
    @Override
    public String getEmail() {
        return profilesManager.getCurrentProfile().getEmail();
    }

    /**
     * Set user mail
     */
    @Override
    public void setEmail(String mail) {
        profilesManager.getCurrentProfile().setEmail(mail);
    }

    /**
     * Returns path to user folder from current user profile.
     */
    @Override
    public Path ensureCurrentProfilePath(Path basePath) throws IOException {
        return profilesManager.ensureCurrentProfilePath(basePath);
    }

    @Override
    public Path getCurrentProfilePath(Path basePath) {
        return profilesManager.getCurrentProfilePath(basePath);
    }

    /**
     * Add new profile
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void addProfile(String name, String... fields) {

        if (name == null) {
            return;
        }

        try {
            Profile profile = new Profile(name);
            if (fields != null) {
                Iterator<String> it = Arrays.asList(fields).iterator();
                if (it.hasNext()) {
                    profile.setPassword(it.next());
                }
                if (it.hasNext()) {
                    profile.setUserFirstName(it.next());
                }
                if (it.hasNext()) {
                    profile.setUserLastName(it.next());
                }
                if (it.hasNext()) {
                    profile.setEmail(it.next());
                }
            }
            profilesManager.add(profile);
        } catch (IOException ex) {
            WorkspaceUserManager.LOG.warn(WorkspaceResourceAnchor.getString("message#290") + name, ex);
        } catch (ProfileOperationException ex) {
            WorkspaceUserManager.LOG.warn(WorkspaceResourceAnchor.getString("message#288") + name, ex);
        }
    }

    /**
     * Remove profile
     */
    @Override
    public void removeProfile(String name, String password) {
        try {
            Profile profile = profilesManager.loadProfile(name);
            profilesManager.delete(profile, password);
        } catch (ProfileOperationException ex) {
            WorkspaceUserManager.LOG.warn(WorkspaceResourceAnchor.getString("message#275") + name, ex);
        }
    }

    /**
     * Reset this engine.
     */
    @Override
    public void reset() {}

    /**
     * Get path to specified user folder.
     */
    @Override
    public Path ensureProfilePath(String name, Path basePath) throws IOException {
        return new Profile(name).ensureProfilePath(basePath);
    }

    /**
     *  Returns all users in system
     */
    @Override
    public List<String> getUsersList() {
        return profilesManager.getProfilesList();
    }

    /**
     * Set password
     */
    @Override
    public void setPassword(String oldPassword, String password, String confirmPassword)
        throws ProfileOperationException {

        profilesManager.getCurrentProfile().setPassword(oldPassword, password, confirmPassword);
    }

    /**
     * Check password
     */
    @Override
    public boolean checkPassword(String password) {
        return profilesManager.getCurrentProfile().checkPassword(password);
    }

    /**
     * Return user description
     */
    @Override
    public String getDescription() {
        return profilesManager.getCurrentProfile().getDescription();
    }

    /**
     * Set description for the current profile
     */
    @Override
    public void setDescription(String desc) {
        profilesManager.getCurrentProfile().setDescription(desc);
    }

    /**
     */
    @Override
    public void load() {
        WorkspaceUserManager.LOG.info("> Profiles manager is loaded");
    }

    /**
     * User login procedure.
     */
    @Override
    public void login(@NonNull String name, @NonNull String password) throws ProfileOperationException {

        login(Profile.create(name, password, "", "", ""));
    }

    @Override
    public void login(@NonNull Profile candidate) throws ProfileOperationException {

        Profile profile = profilesManager.loadProfile(candidate.getUserName());

        if (!profile.checkPassword(candidate)) {
            throw new ProfileOperationException("Login has failed");
        }

        profilesManager.setCurrentProfile(profile);
        WorkspaceUserManager.LOG.info("> You are logged as " + getUserName());
    }

    /**
     * User logout procedure.
     */
    @Override
    public void logout() {
        try {
            profilesManager.saveCurrentProfile();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        profilesManager.clearCurrentProfile();
    }

    /**
     * Saves profiles manager and profiles on disk
     * via serialization support.
     */
    @Override
    public void save() throws IOException {
        profilesManager.saveCurrentProfile();
    }

    /**
     * Is the user logged in
     */
    @Override
    public boolean userLogged() {
        return profilesManager.getCurrentProfile() != null && profilesManager.getCurrentProfile()
            != profilesManager.getDefaultProfile();
    }

    /**
     * Set username
     */
    @Override
    public void setUserName(String name) {
        profilesManager.getCurrentProfile().setUserName(name);
        Workspace.getUi().update();
    }
}
