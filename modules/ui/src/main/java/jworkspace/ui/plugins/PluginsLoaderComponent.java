package jworkspace.ui.plugins;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.config.DesktopServiceLocator;

public class PluginsLoaderComponent {

    /**
     * Progress dialog for observing shells load.
     */
    private final ProgressDialog pr;
    /**
     * Plugins loader
     */
    private final ShellsLoader shellsLoader;

    public PluginsLoaderComponent() {
        pr = new ProgressDialog(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.loading"),
            true
        );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pr.setLocation(
            (screenSize.width - pr.getWidth()) / 2,
            (screenSize.height - pr.getHeight()) / 2
        );
        this.shellsLoader = new ShellsLoader(pr);
    }

    public void loadPlugins() {
        /*
         * Load plugins in a separate worker thread in order to update the swing thread
         */
        SwingUtilities.invokeLater(() -> pr.track(this.shellsLoader));
    }
}
