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
import java.nio.file.Path;

import javax.swing.AbstractAction;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.ui.dialog.KQuestionDialog;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.downloader.DownloadTask;
import jworkspace.runtime.plugin.PluginUpdateChecker;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.resources.TaskTrackerComponent;
import jworkspace.ui.runtime.plugin.PluginDownloadController;
import jworkspace.ui.runtime.plugin.reports.PluginReport;

public class PluginUpdateAction extends AbstractAction {

    private final Plugin plugin;
    private final PluginReport pluginReport;

    public PluginUpdateAction(Plugin plugin, PluginReport pluginReport) {
        this.plugin = plugin;
        this.pluginReport = pluginReport;
    }

    @SuppressWarnings("checkstyle:AnonInnerLength")
    @Override
    public void actionPerformed(ActionEvent e) {
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
        KQuestionDialog questionDialog = new KQuestionDialog(
            parent
        ) {
            @Override
            protected boolean accept() {
                DownloadItem downloadItem = new DownloadItem(
                    plugin.getUpdateVersion().url().toString(),
                    DownloadStatus.QUEUED, 0, -1
                );

                DownloadTask downloadTask = new DownloadTask(downloadItem);
                downloadTask.setPath(Path.of(
                    ServiceLocator.getInstance().getProfilesManager().getCurrentProfilePath().toString(),
                    DownloadTask.DEFAULT_PATH
                ));
                new TaskTrackerComponent(downloadTask, "Loading update").runAndTrack()
                    .thenRun(() -> {
                        Plugin updated = null;
                        try {
                            updated = ServiceLocator.getInstance().getPluginLocator().createPlugin(
                                downloadItem.getCompletedFile().toFile(), PluginDTO.PLUGIN_LEVEL_ANY
                            );

                        } catch (PluginException e) {
                            WorkspaceError.exception(String.format("Error loading the plugin: %s.", e.getMessage()), e);
                        }

                        try {
                            if (updated != null && updated.getJarFile() != null
                                && WorkspacePluginLocator.uninstallPlugin(Path.of(plugin.getJarFile()))
                            ) {
                                PluginDownloadController.installPlugin(
                                    Path.of(updated.getJarFile()),
                                    updated
                                );
                            }
                            plugin.setUpdateVersion(null);
                            plugin.getProperties().put(PluginUpdateChecker.PLUGIN_HAS_UPDATE, false);
                        } catch (Exception ex) {
                            WorkspaceError.exception(
                                String.format("Error uninstalling the plugin: %s.", ex.getMessage()), ex
                            );
                        }

                        pluginReport.repaint();
                    })
                    .exceptionally(e -> {
                        WorkspaceError.exception("Cannot update plugin", e);
                        return null;
                    });
                return true;
            }
        };
        questionDialog.setMessage(String.format("Are you sure to update %s?", plugin));
        questionDialog.setVisible(true);
    }


}
