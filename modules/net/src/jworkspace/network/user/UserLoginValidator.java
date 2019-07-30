package jworkspace.network.user;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
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

import kiwi.ui.dialog.*;

/**
 * This class gets network credentials and makes them to be sended to
 * group management session. This requires a distance call to be executed,
 * to after password and user name are provided, this dialog is disposed,
 * taking no care about if credentials provided are correct.
 */
class UserLoginValidator implements LoginValidator
{
    /**
     * Here comes a user name and plain, non encrypted password.
     * Password is then used as a key in DES algorythm to form encrypted
     * user name to send it to server. If server knows the plain password,
     * it can decrypt the message and find user name among users of server
     * group management session.
     */
    public boolean validate(String name, String password)
    {
        if (name.trim().equals(""))
        {
            return false;
        }
        UserInfo.getPreferences().putString(UserConstants.CK_ALT_USER_NAME, name);
        UserInfo.getPreferences().putString(UserConstants.CK_ALT_USER_PASSWORD, password);
        return true;
    }

    public void validationCancelled()
    {
    }
}