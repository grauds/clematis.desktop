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

/**
 * Workspace local users validation. This class only checks for writable home directory
 * and redirects the validation to users profile engine
 *
 * @author Anton Troshin
 */
public class LoginValidator {

    private final ProfilesManager profilesManager;

    public LoginValidator(ProfilesManager profilesManager) {
        this.profilesManager = profilesManager;
    }

    /**
     * Validates user name candidate and tries to log it in with profiles manager
     */
    public boolean validate(String name, String password) {
        if (name != null && !name.trim().isEmpty()) {
            try {
                File file = File.createTempFile(name + "_check", "tmp");
                if (file.delete() && profilesManager != null) {
                    profilesManager.login(name, password);
                }
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }
}