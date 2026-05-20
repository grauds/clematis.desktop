package jworkspace.ui.runtime.plugin.actions;


/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;

import com.hyperrealm.kiwi.plugin.Plugin;

import jworkspace.ui.runtime.plugin.PluginDownloadController;

public class PluginInstallAction extends AbstractAction {

    private final Plugin plugin;

    public PluginInstallAction(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.plugin != null && this.plugin.getJarFile() != null) {
            PluginDownloadController.installPlugin(
                Path.of(this.plugin.getJarFile()),
                this.plugin
            );
        }
    }
}
