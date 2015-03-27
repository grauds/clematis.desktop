package jworkspace.kernel;

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
   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
 */

import kiwi.ui.dialog.LoginValidator;

import java.io.File;
import java.io.IOException;

/**
 * Login Validator is a specific component
 * that is a bridge between Login dialog
 * and Workspace Profiles engine. It executes
 * validation of input parameters via profiles
 * engines methods.
 */
public class WorkspaceLoginValidator
        implements LoginValidator
{
    /**
     * Validates input parameters.
     */
    public boolean validate(String name, String password)
    {
        if (name == null || name.trim().equals(""))
        {
            Workspace.getLogger().warning(">" +
                                   "Login failure - the name provided is incorrect");
            return false;
        }
        try
        {
            File file = File.createTempFile(name + "_check", "tmp");
            file.delete();
        }
        catch (IOException ex)
        {
            Workspace.getLogger().warning(">" +
                                   "Login failure - the name provided is incorrect");
            return false;
        }
        try
        {
            Workspace.getProfilesEngine().login(name, password);
        }
        catch (Exception ex)
        {
            Workspace.getLogger().warning(">" + "Login failure" + ex.toString());
            return false;
        }
        return true;
    }
    public void validationCancelled() { }
}