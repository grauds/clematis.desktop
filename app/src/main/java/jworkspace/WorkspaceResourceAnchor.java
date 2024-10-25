package jworkspace;

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

import java.text.DateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@Log
public class WorkspaceResourceAnchor {

    private static ResourceBundle resources = null;

    private WorkspaceResourceAnchor() {}

    public static void printAvailableLocales() {

        log.log(Level.INFO, "Available locales:");
        Locale[] list = DateFormat.getAvailableLocales();
        for (Locale locale : list) {
            log.log(Level.INFO, locale.getLanguage() + "   " + locale.getCountry());
        }
    }

    public static String getString(String id) {

        String message = id;
        try {
            if (resources == null) {
                resources = ResourceBundle.getBundle("i18n/strings");
            }
            message = resources.getString(id);
        } catch (MissingResourceException ex) {
            log.log(Level.INFO, "Cannot find resource:" + id);
        }

        return message;
    }
}