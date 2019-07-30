package jworkspace.ui.installer;
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
import java.util.*;

import jworkspace.kernel.*;
/**
 * International resources for runtime manager.
 */
public class LangResource
{
  static ResourceBundle resources = null;
  static Locale locale;
  static public String getString(String id)
  {
    /**
     * Try to find a bundle
     */
    try
    {
      if(resources == null) resources =
               ResourceBundle.getBundle ("i18n/istrings");
    }
    /**
     * If bundle is not found, use english strings
     */
    catch(MissingResourceException ex)
    {
      resources = ResourceBundle.getBundle ("i18n/istrings", Locale.ENGLISH);
    }
    String message = null;
    try
    {
      message = resources.getString (id);
    }
    catch(MissingResourceException ex)
    {
      message = id;
      Workspace.logException("Cannot find resource string " + id +
                " in file istrings");
    }
    return message;
  }
}
