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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.TaskExecutorService;
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.downloader.DownloadTask;
import jworkspace.runtime.downloader.IDownloadListener;
import jworkspace.ui.WorkspaceError;
import lombok.Getter;

@Getter
public abstract class DownloadController implements IDownloadListener {

    private final DownloadTableModel model;
    private final TaskExecutorService service;

    public DownloadController(DownloadTableModel model,
                              TaskExecutorService service
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
            DownloadTask task = item.getTask();
            task.setListener(this);
            task.setPath(Path.of(
                ServiceLocator.getInstance().getProfilesManager().getCurrentProfilePath().toString(),
                DownloadTask.DEFAULT_PATH
            ));
            service.start(task);
        }
    }

    public void cancel(int row) {
        DownloadItem item = model.getItem(row);
        item.getCancelled().set(true);
        item.addLog("Download canceled by user.");
        item.setStatus(DownloadStatus.CANCELED);
        update(item);
    }

    public void remove(int row) {
        DownloadItem item = model.getItem(row);
        try {
            Files.deleteIfExists(item.getCompletedFile());
        } catch (IOException e) {
            WorkspaceError.exception("Cannot delete downloaded file.", e);
        }
        model.removeRow(row);
    }

    public void shutdown() {
        service.shutdown();
    }

    public void fireTableDataChanged() {
        this.model.fireTableDataChanged();
    }

    @Override
    public void update(DownloadItem item) {
        int row = this.model.getItems().indexOf(item);
        this.model.updateRow(row);
    }

}
