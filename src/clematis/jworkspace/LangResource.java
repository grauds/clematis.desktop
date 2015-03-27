package jworkspace;

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

import jworkspace.kernel.Workspace;

import java.text.DateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LangResource
{
    static ResourceBundle resources = null;
    static Locale locale;

    public LangResource()
    {
    }

    public static void printAvailableLocales()
    {
        Workspace.getLogger().info("Available locales:");

        Locale list[] = DateFormat.getAvailableLocales();
        for (int i = 0; i < list.length; i++)
        {
          Workspace.getLogger().info(list[i].getLanguage() + "   " + list[i].getCountry());
        }
    }

    static public String getString(String id)
    {
        if (resources == null) resources = ResourceBundle.getBundle("strings");
        String message = null;
        try
        {
            message = resources.getString(id);
        }
        catch (MissingResourceException ex)
        {
            Workspace.getLogger().warning(ex.toString());
            Workspace.getLogger().warning("Cannot find resource" + " " + id);
        }
        if (message == null)
        {
            System.out.println("Cannot find string > " + id + " <");
            return id;
        }
        return message;
    }
}
