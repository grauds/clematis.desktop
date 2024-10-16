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

import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.IShell;
import jworkspace.ui.api.IView;
import jworkspace.ui.cpanel.CButton;

/**
 * Default user gui shell.
 * @author Anton Troshin
 */
public abstract class DefaultShell implements IShell, ActionListener {
    /**
     * Show this window
     */
    private static final String NEW_VIEW = "NEW_VIEW";
    /**
     * Save path. Relative to user.home
     */
    private String path = "";

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(DefaultShell.NEW_VIEW)) {
            /*
             * Send message to workspace gui with request
             * to add to layout?
             */
            Map<String, Object> lparam = new HashMap<>();
            lparam.put("view", this);
            lparam.put("display", Boolean.TRUE);
            lparam.put("register", Boolean.FALSE);
            ServiceLocator
                .getInstance()
                .getEventsDispatcher()
                .fireEvent(WorkspaceGUI.WorkspaceViewListener.CODE, lparam, null);
        }
    }

    /**
     * This method returns new view object to install in multidesktop
     * system. This view will not be unique. The shell interface serves
     * as a view factory.
     *
     * @return instance of view
     */
    protected abstract IView getView();

    /**
     * Get all Control Panel buttons for this shell
     */
    public CButton[] getButtons() {
        return new CButton[0];
    }

    /**
     * Load shell from disk
     */
    public void load() {
    }

    /**
     * Reset the state of shell
     */
    public void reset() {
    }

    /**
     * Save all settings to default path
     */
    public void save() {
    }

    /**
     * Returns path for saving component data.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path for saving component data.
     */
    public void setPath(String path) {
        this.path = path;
    }
}
