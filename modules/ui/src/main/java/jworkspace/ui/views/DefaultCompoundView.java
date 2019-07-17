package jworkspace.ui.views;

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

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import jworkspace.kernel.Workspace;
import jworkspace.ui.IShell;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.cpanel.CButton;

/**
 * Simple user gui shell and view. This shell does nothing, but appears as plain panel in desktop.
 * @author Anton Troshin
 */
public class DefaultCompoundView extends DefaultView
    implements IShell, ActionListener {

    /**
     * Are the buttons loaded?
     */
    private boolean buttonsLoaded = false;

    DefaultCompoundView() {
        super();
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(DefaultCompoundView.SHOW)) {
            /*
             * Send message to workspace gui with request
             * to add to layout?
             */
            Map<String, Object> lparam = new HashMap<>();
            lparam.put("view", this);
            lparam.put("display", Boolean.TRUE);
            lparam.put("register", isUnique());
            Workspace.fireEvent(WorkspaceGUI.WorkspaceViewListener.CODE, lparam, null);
        }
    }

    public CButton[] getButtons() {
        return null;
    }

    public void setButtonsLoaded(boolean buttonsLoaded) {
        this.buttonsLoaded = buttonsLoaded;
    }

    public boolean areButtonsLoaded() {
        return buttonsLoaded;
    }
}