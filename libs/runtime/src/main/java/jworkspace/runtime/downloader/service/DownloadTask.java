package jworkspace.runtime.downloader.service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public record DownloadTask(DownloadItem item, IDownloadListener listener, int row) implements Runnable {

    private static final int BUFFER_SIZE = 8192;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void run() {
        try {
            item.setStatus(DownloadStatus.DOWNLOADING);
            item.addLog("Starting download: " + item.getUrl());
            listener.update(row);

            URL url = new URL(item.getUrl());
            URLConnection conn = url.openConnection();
            long total = conn.getContentLengthLong();

            try (InputStream in = conn.getInputStream()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long downloaded = 0;
                long start = System.currentTimeMillis();

                int read;
                while ((read = in.read(buffer)) != -1) {
                    if (item.getCancelled().get()) {
                        item.addLog("Canceled.");
                        item.setStatus(DownloadStatus.CANCELED);
                        listener.update(row);
                        return;
                    }

                    downloaded += read;
                    item.setDownloadedBytes(downloaded);
                    item.setTotalBytes(total);

                    long elapsed = System.currentTimeMillis() - start;
                    if (elapsed > 0) {
                        item.setSpeedBytesPerSec(downloaded * 1000 / elapsed);
                    }

                    listener.update(row);
                }
            }

            item.setStatus(DownloadStatus.COMPLETED);
            item.addLog("Download completed.");
            listener.update(row);

        } catch (Exception e) {
            item.setStatus(DownloadStatus.FAILED);
            item.addLog("Download failed: " + e.getMessage());
            listener.update(row);
        }
    }
}

