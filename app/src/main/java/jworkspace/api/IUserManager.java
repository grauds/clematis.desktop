package jworkspace.api;


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
import java.util.Vector;

import com.hyperrealm.kiwi.util.Config;

import jworkspace.users.Profile;
import jworkspace.users.ProfileOperationException;

/**
 * User profile systems of Java Workspace should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 *
 * @author Anton Troshin
 */
public interface IUserManager extends WorkspaceComponent {
    /**
     * Get user-defined property from user profile.
     */
    Config getParameters();

    /**
     * Get current user name.
     *
     * @return java.lang.String
     */
    String getUserName();

    /**
     * Get path to current user folder.
     *
     * @return java.lang.String
     */
    Path ensureCurrentProfilePath(Path basePath) throws IOException;

    /**
     * Get user first name
     */
    String getUserFirstName();

    /**
     * Set user first name
     */
    void setUserFirstName(String name);

    /**
     * Get user last name
     */
    String getUserLastName();

    /**
     * Set user last name
     */
    void setUserLastName(String name);

    /**
     * Get user mail
     */
    String getEmail();

    /**
     * Set user mail
     */
    void setEmail(String mail);

    /**
     * Add new Profile
     */
    void addProfile(String name, String... fields);

    /**
     * Remove Profile
     */
    void removeProfile(String name, String password);

    /**
     * Get path to specified user folder.
     *
     * @return java.lang.String
     */
    Path ensureProfilePath(String name, Path basePath) throws IOException;

    /**
     * Get users list.
     */
    Vector getUsersList();

    /**
     * Get description
     */
    String getDescription();

    /**
     * Set description
     */
    void setDescription(String description);

    /**
     * Login procedure.
     */
    void login(String name, String password) throws ProfileOperationException, IOException;

    /**
     * Login procedure.
     */
    void login(Profile profile) throws ProfileOperationException, IOException;

    /**
     * Logout current user.
     */
    void logout();

    /**
     * Returns true, if there is user, logged in workspace.
     *
     * @return boolean
     */
    boolean userLogged();

    /**
     * Set user name
     */
    void setUserName(String name);

    /**
     * Set password
     */
    void setPassword(String oldPassword, String password, String confirmPassword) throws Exception;

    /**
     * Check password
     */
    boolean checkPassword(String password) throws Exception;
}