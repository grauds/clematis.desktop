package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any LATER version.

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

import jworkspace.ui.api.dialog.StackTraceError;
import jworkspace.ui.config.DesktopServiceLocator;
import lombok.extern.java.Log;

/**
 * Gui error reporter for the end-user
 *
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Log
public class WorkspaceError extends StackTraceError {


    public static void exception(String usermsg, Throwable ex) {
        exception(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            usermsg,
            ex
        );
    }

    public static void msg(String usermsg) {
        msg("Error", usermsg);
    }

    public static void msg(String title, String usermsg) {
        msg(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),  title, usermsg);
    }

}
