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
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadService;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.downloader.DownloadTask;
import jworkspace.runtime.downloader.IDownloadListener;

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
        if (item.getStatus() != DownloadStatus.QUEUED) {
            return;
        }

        service.start(new DownloadTask(item, this, row));
    }

    public void start() {
        for (int i = 0; i < model.getRowCount(); i++) {
            DownloadItem item = model.getItem(i);
            if (item.getStatus() == DownloadStatus.QUEUED) {
                service.start(new DownloadTask(item, this, i));
            }
        }
    }

    public void cancel(int row) {
        DownloadItem item = model.getItem(row);
        item.getCancelled().set(true);
        item.addLog("Download canceled by user.");
        item.setStatus(DownloadStatus.CANCELED);
        update(row);
    }

    public void remove(int row) {
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
}
