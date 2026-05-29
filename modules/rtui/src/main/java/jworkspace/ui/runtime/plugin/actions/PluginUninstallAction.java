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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.AbstractAction;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.dialog.KMessageDialog;
import com.hyperrealm.kiwi.ui.dialog.KQuestionDialog;

import static jworkspace.runtime.plugin.WorkspacePluginLocator.PLUGIN_DELETED;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.runtime.plugin.reports.PluginReport;

public class PluginUninstallAction extends AbstractAction {

    private final Plugin p;
    private final PluginReport pluginReport;

    public PluginUninstallAction(Plugin p, PluginReport pluginReport) {
        this.p = p;
        this.pluginReport = pluginReport;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
        KQuestionDialog questionDialog = new KQuestionDialog(
            parent
        ) {
            @Override
            protected boolean accept() {
                KMessageDialog messageDialog = new KMessageDialog(parent);
                try {
                    if (WorkspacePluginLocator.uninstallPlugin(Path.of(p.getJarFile()))) {
                        messageDialog.setMessage("Deleted successfully.");
                        PluginUninstallAction.this.p.getProperties().put(
                            PLUGIN_DELETED, true
                        );
                        pluginReport.repaint();
                    } else {
                        messageDialog.setMessage(String.format("File %s doesn't exist.", p.getJarFile()));
                    }
                } catch (IOException e) {
                    messageDialog.setMessage(
                        String.format("Error deleting the file: %s. %s", p.getJarFile(), e.getMessage())
                    );
                }
                messageDialog.setVisible(true);
                return true;
            }
        };
        questionDialog.setMessage(String.format("Are you sure to uninstall %s?", p));
        questionDialog.setVisible(true);
    }
}
