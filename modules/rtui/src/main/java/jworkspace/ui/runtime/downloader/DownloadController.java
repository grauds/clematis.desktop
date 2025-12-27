package jworkspace.ui.runtime.downloader;

import jworkspace.runtime.downloader.service.DownloadItem;
import jworkspace.runtime.downloader.service.DownloadService;
import jworkspace.runtime.downloader.service.DownloadStatus;
import jworkspace.runtime.downloader.service.DownloadTask;
import jworkspace.runtime.downloader.service.IDownloadListener;

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
