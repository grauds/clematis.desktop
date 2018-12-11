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
import java.io.IOException;
import java.util.Hashtable;

import jworkspace.kernel.Workspace;
import jworkspace.ui.IShell;
import jworkspace.ui.IView;
import jworkspace.ui.cpanel.CButton;

/**
 * Default user gui shell.
 */
public abstract class DefaultShell implements IShell, ActionListener {
    /**
     * Show this window
     */
    public final static String NEW_VIEW = "NEW_VIEW";
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
            /**
             * Send message to workspace gui with request
             * to add to layout?
             */
            Hashtable lparam = new Hashtable();
            lparam.put("view", getView());
            lparam.put("display", new Boolean(true));
            lparam.put("register", new Boolean(false));
            Workspace.fireEvent(new Integer(1000), lparam, null);
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
    public void load() throws IOException {
    }

    /**
     * Reset the state of shell
     */
    public void reset() {
    }

    /**
     * Save all settings to default path
     */
    public void save() throws IOException {
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
