package jworkspace.ui.runtime.downloader;
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
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.ui.dialog.KMessageDialog;
import com.hyperrealm.kiwi.ui.dialog.KQuestionDialog;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadService;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.downloader.DownloadTask;
import jworkspace.runtime.downloader.IDownloadListener;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.plugins.ShellsLoader;

public class DownloadController implements IDownloadListener {

    private final DownloadTableModel model;
    private final DownloadService service;

    public DownloadController(DownloadTableModel model,
                              DownloadService service
    ) {
        this.model = model;
        this.service = service;
    }

    public void add(DownloadItem item) {
        this.model.addItem(item);
    }

    public void start(int row) {
        DownloadItem item = model.getItem(row);
        if (item.getStatus() == DownloadStatus.QUEUED) {
            service.start(new DownloadTask(
                item,
                this,
                Path.of(
                    ServiceLocator.getInstance().getProfilesManager().getCurrentProfilePath().toString(),
                    DownloadTask.DEFAULT_PATH
                ),
                row
            ));
        }
    }

    public void cancel(int row) {
        DownloadItem item = model.getItem(row);
        item.getCancelled().set(true);
        item.addLog("Download canceled by user.");
        item.setStatus(DownloadStatus.CANCELED);
        update(row);
    }

    public void remove(int row) throws IOException {
        DownloadItem item = model.getItem(row);
        Files.deleteIfExists(item.getCompletedFile());
        model.removeRow(row);
    }

    public void shutdown() {
        service.shutdown();
    }

    public void fireTableDataChanged() {
        this.model.fireTableDataChanged();
    }

    @Override
    public void update(int row) {
        this.model.updateRow(row);
    }

    @Override
    public void finished(int row) {
        DownloadItem item = model.getItem(row);
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();

        KQuestionDialog questionDialog = new KQuestionDialog(parent) {
            @Override
            protected boolean accept() {
                KMessageDialog messageDialog = new KMessageDialog(parent);
                try {
                    Plugin plugin = ServiceLocator.getInstance().getPluginLocator().loadPlugin(
                         item.getCompletedFile().toFile(), PluginDTO.PLUGIN_TYPE_ANY
                    );
                    Files.move(item.getCompletedFile(), Path.of(
                        ShellsLoader.SHELLS_DIRECTORY, item.getFileName()
                    ));
                    messageDialog.setMessage(
                        String.format("Plugin %s installed successfully.", plugin)
                    );
                } catch (IOException | PluginException e) {
                    messageDialog.setMessage(
                        String.format("Error installing the plugin: %s.", e.getMessage())
                    );
                }
                messageDialog.setVisible(true);
                return true;
            }
        };
        questionDialog.setMessage(String.format("Are you sure to install a plugin from %s?", item.getCompletedFile()));
        questionDialog.setVisible(true);
    }
}
