package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.extern.java.Log;

/**
 * International resources for runtime manager.
 *
 * @author Anton Troshin
 */
@Log
public class LangResource {

    private static final String I18N_RESOURCES = "i18n/rmstrings";

    private static ResourceBundle resources = null;

    private LangResource() {
    }

    static String getString(String id) {
        try {
            if (resources == null) {
                resources = ResourceBundle.getBundle(I18N_RESOURCES);
            }
        } catch (MissingResourceException ex) {
            resources = ResourceBundle.getBundle(I18N_RESOURCES, Locale.ENGLISH);
        }
        String message;
        try {
            message = resources.getString(id);
        } catch (MissingResourceException ex) {
            message = id;
            log.finest("Cannot find resource string " + id + " in file rmstrings");
        }
        return message;
    }
}
