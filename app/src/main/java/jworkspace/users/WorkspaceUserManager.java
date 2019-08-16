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
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.Config;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IUserManager;
import jworkspace.kernel.Workspace;

/**
 * Profile engine is one of required by kernel.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public class WorkspaceUserManager implements IUserManager {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceUserManager.class);

    private static WorkspaceUserManager instance = null;

    private ProfilesManager profilesManager;

    private boolean userLogged = false;

    /**
     * Default public constructor.
     */
    private WorkspaceUserManager() {
        super();
    }

    public static synchronized WorkspaceUserManager getInstance() {
        if (instance == null) {
            instance = new WorkspaceUserManager();
            instance.profilesManager = new ProfilesManager(Workspace.getBasePath());
        }
        return instance;
    }

    /**
     * Returns parameter from current user profile
     */
    public Config getParameters() {
        return profilesManager.getCurrentProfile().getParameters();
    }

    /**
     * Returns user name from current user profile
     */
    public String getUserName() {
        return profilesManager.getCurrentProfile().getUserName();
    }

    /**
     * Get human readable name for installer
     */
    public String getName() {
        return "Java Workspace User Management v2.00";
    }

    /**
     * Returns user first name from current user profile
     */
    public String getUserFirstName() {
        return profilesManager.getCurrentProfile().getUserFirstName();
    }

    /**
     * Set user first name
     */
    public void setUserFirstName(String name) {
        profilesManager.getCurrentProfile().setUserFirstName(name);
    }

    /**
     * Returns user last name from current user profile
     */
    public String getUserLastName() {
        return profilesManager.getCurrentProfile().getUserLastName();
    }

    /**
     * Set user last name
     */
    public void setUserLastName(String name) {
        profilesManager.getCurrentProfile().setUserLastName(name);
    }

    /**
     * Returns user email from current user profile
     */
    public String getEmail() {
        return profilesManager.getCurrentProfile().getEmail();
    }

    /**
     * Set user mail
     */
    public void setEmail(String mail) {
        profilesManager.getCurrentProfile().setEmail(mail);
    }

    /**
     * Returns path to user folder from current user profile.
     */
    public Path ensureCurrentProfilePath(Path basePath) throws IOException {
        return profilesManager.ensureCurrentProfilePath(basePath);
    }

    /**
     * Add new profile
     */
    @SuppressWarnings("checkstyle:MagicNumber")
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
    public void reset() {
    }

    /**
     * Get path to specified user folder.
     */
    public Path ensureProfilePath(String name, Path basePath) throws IOException {
        return new Profile(name).ensureProfilePath(basePath);
    }

    /*** Returns all users in system
     */
    public Vector getUsersList() {
        return profilesManager.getProfilesList();
    }

    /**
     * Set password
     */
    public void setPassword(String oldPassword, String password, String confirmPassword)
        throws ProfileOperationException {

        profilesManager.getCurrentProfile().setPassword(oldPassword, password, confirmPassword);
    }

    /**
     * Check password
     */
    public boolean checkPassword(String password) {
        return profilesManager.getCurrentProfile().checkPassword(password);
    }

    /**
     * Return user description
     */
    public String getDescription() {
        return profilesManager.getCurrentProfile().getDescription();
    }

    /**
     * Set description for the current profile
     */
    public void setDescription(String desc) {
        profilesManager.getCurrentProfile().setDescription(desc);
    }

    /**
     */
    public void load() {
        WorkspaceUserManager.LOG.info("> Profiles manager is loaded");
    }

    /**
     * User login procedure.
     */
    public void login(String name, String password) throws ProfileOperationException {

        Profile profile = profilesManager.loadProfile(name);

        if (!profile.checkPassword(password)) {
            throw new ProfileOperationException(
                WorkspaceResourceAnchor.getString("UserProfileEngine.passwd.check.failed"));
        }

        profilesManager.setCurrentProfile(profile);
        userLogged = true;

        WorkspaceUserManager.LOG.info("> You are logged as " + getUserName());
    }

    /**
     * User logout procedure.
     */
    public void logout() {
        try {
            profilesManager.saveCurrentProfile();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        profilesManager.clearCurrentProfile();
        userLogged = false;
    }

    /**
     * Saves profiles manager and profiles on disk
     * via serialization support.
     */
    public void save() throws IOException {
        profilesManager.saveCurrentProfile();
    }

    /**
     * returns whether user is logged.
     */
    public boolean userLogged() {
        return userLogged;
    }

    /**
     * Set user name
     */
    public void setUserName(String name) {
        profilesManager.getCurrentProfile().setUserName(name);
        Workspace.getUi().update();
    }
}
