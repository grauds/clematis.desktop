package jworkspace.ui;
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

import java.io.IOException;

import jworkspace.ui.cpanel.CButton;

/**
 * User gui shell - a collection of user views.
 *
 * @author Anton Troshin
 */
public interface IShell {
    /**
     * Get all Control Panel buttons for this shell
     */
    CButton[] getButtons();

    /**
     * Load shell from disk
     */
    void load() throws IOException;

    /**
     * Reset the state of shell
     */
    void reset();

    /**
     * Save all settings to default path
     */
    void save() throws IOException;

    /**
     * Returns path for saving component data.
     */
    String getPath();

    /**
     * Sets path for saving component data.
     */
    void setPath(String path);
}
