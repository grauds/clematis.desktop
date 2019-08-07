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

import java.awt.Color;
import java.io.IOException;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.ui.dialog.LoginDialog;
import com.hyperrealm.kiwi.util.Config;

import jworkspace.LangResource;
import jworkspace.api.IUserProfileEngine;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceLoginValidator;

/**
 * Profile engine is one of required by kernel.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public class UserProfileEngine implements IUserProfileEngine {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileEngine.class);

    private static final String USER_HOME = "user.home";

    private ProfilesManager profilesManager = new ProfilesManager();

    private boolean userLogged = false;

    /**
     * Login dialog
     */
    private LoginDialog loginDlg = null;

    /**
     * Default public constructor.
     */
    public UserProfileEngine() {
        super();
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
        return "Java Workspace User Management Engine (R) v2.00";
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
    public String getCurrentProfileRelativePath() throws IOException {
        return profilesManager.getCurrentProfileRelativePath();
    }

    /**
     * Add new profile
     */
    public void addProfile(String name) {

        if (name != null) {
            try {
                Profile profile = Profile.create(name, "", "", "", "");
                profilesManager.add(profile);
            } catch (IOException ex) {
                UserProfileEngine.LOG.warn(LangResource.getString("message#290") + name);
            } catch (ProfileOperationException ex) {
                UserProfileEngine.LOG.warn(LangResource.getString("message#288") + name);
            }
        }
    }

    /**
     * Remove profile
     */
    public void removeProfile(String name, String password) {
        try {
            Profile profile = profilesManager.getProfile(name);
            profilesManager.delete(profile, password);
        } catch (ProfileOperationException ex) {
            UserProfileEngine.LOG.warn(LangResource.getString("message#275") + name);
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
    public String getCurrentProfileRelativePath(String name) {
        return profilesManager.getProfileFolder(name);
    }

    /*** Returns all users in system
     */
    public java.util.Vector getUsersList() {
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
     * Loads profiles manager and profiles from disk
     * via serialization support.
     */
    public void load() {
        UserProfileEngine.LOG.info("> Profiles manager is loaded");
    }

    /**
     * User login procedure.
     */
    public void login(String name, String password) throws ProfileOperationException {

        Profile profile = profilesManager.getProfile(name);

        if (!profile.checkPassword(password)) {
            throw new ProfileOperationException(LangResource.getString("UserProfileEngine.passwd.check.failed"));
        }

        profilesManager.setCurrentProfile(profile);
        userLogged = true;

        UserProfileEngine.LOG.info("> You are logged as " + getUserName());
    }

    /**
     * User logout procedure.
     */
    public void logout() {
        profilesManager.saveCurrentProfile();
        userLogged = false;
    }

    /**
     * Saves profiles manager and profiles on disk
     * via serialization support.
     */
    public void save() {
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

    /**
     * Returns login dialog for this UI system.
     */
    public JDialog getLoginDlg() {
        if (loginDlg == null) {
            loginDlg = new WorkspaceLoginValidator(Workspace.getUi().getFrame(),
                LangResource.getString("LoginDlg.title"), ""
            );
            loginDlg.setAcceptButtonText(LangResource.getString("LoginDlg.login.accept"));
            loginDlg.pack();
            loginDlg.setIcon(null);
        }
        loginDlg.setTexture(null);
        loginDlg.getContentPane().setBackground(Color.white);
        return loginDlg;
    }
}
