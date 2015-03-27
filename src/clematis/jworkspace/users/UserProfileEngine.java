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
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.kernel.engines.IUserProfileEngine;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceLoginValidator;
import jworkspace.util.WorkspaceError;
import kiwi.ui.dialog.LoginDialog;
import kiwi.util.Config;
import kiwi.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Profile engine is one of required by kernel.
 * This class implements interface
 * <code>jworkspace.kernel.engines.IUserProfileEngine</code>.
 */
public class UserProfileEngine implements IUserProfileEngine
{
    protected static ProfilesManager profilesManager = null;
    protected static boolean userLogged = false;
    /**
     * Login dialog
     */
    private static LoginDialog loginDlg = null;

    /**
     * Default public constructor.
     */
    public UserProfileEngine()
    {
        super();
    }

    /**
     * Returns parameter from current user profile
     */
    public Config getParameters()
    {
        return profilesManager.getCurrentProfile().getParameters();
    }

    /**
     * Returns user name from current user profile
     */
    public java.lang.String getUserName()
    {
        return profilesManager.getCurrentProfile().getUserName();
    }

    /**
     * Get human readable name for installer
     */
    public String getName()
    {
        return "Java Workspace User Management Engine (R) v0.90";
    }

    /**
     * Returns user first name from current user profile
     */
    public String getUserFirstName()
    {
        return profilesManager.getCurrentProfile().getUserFirstName();
    }

    /**
     * Returns user last name from current user profile
     */
    public String getUserLastName()
    {
        return profilesManager.getCurrentProfile().getUserLastName();
    }

    /**
     * Returns user email from current user profile
     */
    public String getEmail()
    {
        return profilesManager.getCurrentProfile().getEmail();
    }

    /**
     * Returns path to user folder from current user profile.
     */
    public java.lang.String getPath()
    {
        return profilesManager.getCurrentProfileFolder();
    }

    /**
     * Add new profile
     */
    public void addProfile(String name)
    {
        if (name != null)
        {
            try
            {
                Profile profile = Profile.create(name, "", "", "", "");
                profilesManager.add(profile);
            }
            catch (IOException ex)
            {
                Workspace.getLogger().warning(LangResource.getString("message#290") + name);
            }
            catch (ProfileOperationException ex)
            {
                Workspace.getLogger().warning(LangResource.getString("message#288") + name);
            }
        }
    }

    /**
     * Remove profile
     */
    public void removeProfile(String name, String password)
    {
        try
        {
            Profile profile = profilesManager.getProfile(name);
            profilesManager.delete(profile, password);
        }
        catch (ProfileOperationException ex)
        {
            Workspace.getLogger().warning(LangResource.getString("message#275") + name);
        }
    }

    /**
     * Reset this engine.
     */
    public void reset()
    {
    }

    /**
     * Get path to specified user folder.
     */
    public java.lang.String getPath(java.lang.String name)
    {
        return profilesManager.getProfileFolder(name);
    }

    /*** Returns all users in system
     */
    public java.util.Vector getUsersList()
    {
        return profilesManager.getProfilesList();
    }

    /**
     * Set password
     */
    public void setPassword(String old_password, String password,
                            String confirm_password)
            throws ProfileOperationException
    {
        profilesManager.getCurrentProfile().
                setPassword(old_password, password, confirm_password);
    }

    /**
     * Check password
     */
    public boolean checkPassword(String password)
    {
        return profilesManager.getCurrentProfile().checkPassword(password);
    }

    /**
     * Get encrypted password
     */
    public byte[] getCipherPassword()
    {
        return profilesManager.getCurrentProfile().getCipherPassword();
    }

    /**
     * Return user description
     */
    public String getDescription()
    {
        return profilesManager.getCurrentProfile().getDecription();
    }

    /**
     * Loads profiles manager and profiles from disk
     * via serialization support.
     */
    public void load() throws IOException
    {
        Workspace.getLogger().info(">" + "Loading profile");
        if (profilesManager != null)
        {
            Workspace.getLogger().info(">" + "Profiles manager is null");
            return;
        }
        try
        {
            profilesManager = new ProfilesManager();
        }
        catch (ProfileOperationException ex)
        {
            throw new IOException(ex.getMessage());
        }
        Workspace.getLogger().info(">" + "Profiles manager is loaded");
    }

    /**
     * User login procedure.
     */
    public void login(String name, String password) throws ProfileOperationException
    {
        Profile profile = profilesManager.getProfile(name);
        if (profile == null && !name.equals(ProfilesManager.DEFAULT_USER_NAME))
        {
            throw new ProfileOperationException
                    (LangResource.getString("UserProfileEngine.profile.null"));
        }
        /**
         * If user wants to login under localroot, but localroot
         * profile.dat is corrupted or absent, user profile enfine will
         * try to create default profile once again.
         */
        else if (profile == null && name.equals(ProfilesManager.DEFAULT_USER_NAME))
        {
            try
            {
                profile = Profile.create(ProfilesManager.DEFAULT_USER_NAME, "", "", "", "");
                profilesManager.add(profile);
            }
                    /**
                     * If localroot is already in managers database
                     */
            catch (ProfileOperationException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("UserProfileEngine.profile.ex"), ex);
            }
            catch (IOException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("UserProfileEngine.profile.ex"), ex);
            }
        }
        else if (!profile.checkPassword(password))
        {
            throw new ProfileOperationException
                    (LangResource.getString("UserProfileEngine.passwd.check.failed"));
        }

        profilesManager.setCurrentProfile(profile);
        userLogged = true;
        Workspace.getLogger().info(">" + "You are logged as" + " " + getUserName());
        /**
         * Change system user path
         */
        System.setProperty("user.home", System.getProperty("user.dir")
                                        + File.separator + getPath());
        Workspace.getLogger().info(">" + "Homepath" + " " + System.getProperty("user.home"));
    }

    /**
     * User logout procedure.
     */
    public void logout()
            throws ProfileOperationException
    {
        profilesManager.saveCurrentProfile();
        userLogged = false;
    }

    /**
     * Saves profiles manager and profiles on disk
     * via serialization support.
     */
    public void save()
    {
        profilesManager.saveCurrentProfile();
    }

    /**
     * returns whether user is logged.
     */
    public boolean userLogged()
    {
        return userLogged;
    }

    /**
     * Set user name
     */
    public boolean setUserName(String name)
    {
        if (profilesManager.getCurrentProfile().setUserName(name))
        {
            Workspace.getUI().update();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Set user first name
     */
    public void setUserFirstName(String name)
    {
        profilesManager.getCurrentProfile().setUserFirstName(name);
    }

    /**
     * Set user last name
     */
    public void setUserLastName(String name)
    {
        profilesManager.getCurrentProfile().setUserLastName(name);
    }

    /**
     * Set description for the current profile
     */
    public void setDescription(String desc)
    {
        profilesManager.getCurrentProfile().setDescription(desc);
    }

    /**
     * Set user mail
     */
    public void setEmail(String mail)
    {
        profilesManager.getCurrentProfile().setEmail(mail);
    }

    /**
     * Returns login dialog for this GUI system.
     */
    public javax.swing.JDialog getLoginDlg()
    {
        if (loginDlg == null)
        {
            loginDlg = new LoginDialog( new Frame(),
                                       LangResource.getString("LoginDlg.title"), "",
                                       new ImageIcon(
                                               new ResourceLoader(WorkspaceResourceAnchor.class)
                                               .getResourceAsImage("logo/LogoSmall.gif")),
                                       new WorkspaceLoginValidator())
            {
                protected boolean canCancel()
                {
                    Workspace.exit();
                    return false;
                }
            };
            loginDlg.setAcceptButtonText(LangResource.getString("LoginDlg.login.accept"));
            loginDlg.pack();
            loginDlg.centerDialog();
            loginDlg.setIcon(null);
            UIManager.addPropertyChangeListener(new UISwitchListener(loginDlg));
        }
        loginDlg.setTexture(null);
        loginDlg.setOpaque(false);
        loginDlg.getContentPane().setBackground(Color.white);
        return loginDlg;
    }
}
