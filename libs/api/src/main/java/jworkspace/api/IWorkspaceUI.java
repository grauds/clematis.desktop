package jworkspace.api;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2024 Anton Troshin

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

import java.awt.Frame;
import java.awt.datatransfer.Clipboard;

/**
 * Basic user interface for jWorkspace with main window, basic popup dialogs,
 * update data or request if data is modified
 */
public interface IWorkspaceUI extends IWorkspaceComponent {

    /**
     * Returns special clipboard for graphic interface.
     */
    Clipboard getClipboard();

    /**
     * Main frame for the application.
     */
    Frame getFrame();

    /**
     * Check whether this UI is modified.
     */
    boolean isModified();

    /**
     * Command to update the UI.
     */
    void update();

    /**
     * Show error to user
     *
     * @param usermsg message
     * @param ex exception
     */
    void showError(String usermsg, Throwable ex);

    /**
     * Show message to user
     *
     * @param usermsg message
     */
    void showMessage(String usermsg);
}