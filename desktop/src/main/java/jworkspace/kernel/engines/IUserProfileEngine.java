package jworkspace.kernel.engines;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin
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

import com.hyperrealm.kiwi.util.Config;

import javax.swing.*;
import java.util.Vector;

/**
 * User profile systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 */
public interface IUserProfileEngine
        extends IEngine
{
    /**
     * Get user-defined property from user profile.
     */
    public Config getParameters();

    /**
     * Get current user name.
     * @return java.lang.String
     */
    String getUserName();

    /**
     * Get path to current user folder.
     * @return java.lang.String
     */
    String getPath();

    /**
     * Get user first name
     */
    String getUserFirstName();

    /**
     * Get user last name
     */
    String getUserLastName();

    /**
     * Get user mail
     */
    String getEmail();

    /**
     * Add new Profile
     */
    void addProfile(String name);

    /**
     * Remove Profile
     */
    void removeProfile(String name, String password);

    /**
     * Get path to specified user folder.
     * @return java.lang.String
     */
    String getPath(String name);

    /**
     * Get users list.
     */
    Vector getUsersList();

    /**
     * Get description
     */
    String getDescription();

    /**
     * Login procedure.
     */
    void login(String name, String password) throws Exception;

    /**
     * Logout current user.
     */
    void logout() throws Exception;

    /**
     * Returns true, if there is user, logged
     * in workspace. There can be situation,
     * there will be no logged users, for example
     * during startup login.
     * @return boolean
     */
    boolean userLogged();

    /**
     * Returns login dialog for the system.
     * Login dialog should perform login security
     * functions.
     * @return login dialog for the system.
     */
    public JDialog getLoginDlg();

    /**
     * Set user name
     */
    boolean setUserName(String name);

    /**
     * Set user first name
     */
    void setUserFirstName(String name);

    /**
     * Set user last name
     */
    void setUserLastName(String name);

    /**
     * Set user mail
     */
    void setEmail(String mail);

    /**
     * Set description
     */
    void setDescription(String description);

    /**
     * Set password
     */
    void setPassword(String old_password, String password,
                     String confirm_password) throws Exception;

    /**
     * Check password
     */
    boolean checkPassword(String password) throws Exception;

    /**
     * Return encrypted password
     */
    byte[] getCipherPassword();
}