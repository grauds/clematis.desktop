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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Troshin
 */
public class WorkspaceResourceAnchor {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceResourceAnchor.class);
    private static ResourceBundle resources = null;

    private WorkspaceResourceAnchor() {}

    public static void printAvailableLocales() {

        LOG.info("Available locales:");
        Locale[] list = DateFormat.getAvailableLocales();
        for (Locale locale : list) {
            LOG.info(locale.getLanguage() + "   " + locale.getCountry());
        }
    }

    public static String getString(String id) {

        if (resources == null) {
            resources = ResourceBundle.getBundle("i18n/strings");
        }

        String message = "null";
        try {
            message = resources.getString(id);
        } catch (MissingResourceException ex) {
            LOG.warn("Cannot find resource:" + id);
        }

        return message;
    }
}