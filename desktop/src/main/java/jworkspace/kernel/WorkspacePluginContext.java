package jworkspace.kernel;

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

import javax.swing.JOptionPane;

import com.hyperrealm.kiwi.util.plugin.PluginContext;

/**
 * Workspace plugin context defines shared workspace resources for use in plugins.
 *
 * @author Anton Troshin
 */
public class WorkspacePluginContext implements PluginContext {

    /**
     * Show plugin status in workspace ui
     *
     * @param status string
     */
    public void showStatus(String status) {
        // todo: not implemented yet
    }

    /**
     * Show message in workspace ui
     *
     * @param message to workspace user
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(Workspace.getUi().getFrame(), message);
    }

    /**
     * Show question in workspace ui
     *
     * @param question to workspace user
     * @return boolean answer
     */
    public boolean showQuestion(String question) {
        int result = JOptionPane.showConfirmDialog(Workspace.getUi().getFrame(),
            question);

        return result == JOptionPane.YES_OPTION;
    }
}