package jworkspace.runtime.downloader;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Date;

import jworkspace.runtime.AbstractTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Runnable task responsible for downloading a single {@link DownloadItem}.
 *
 * <p>This class performs a blocking network download and periodically updates
 * the associated {@link DownloadItem} with progress, speed, and status changes.
 * UI updates are delegated to {@link IDownloadListener} and executed on each
 * meaningful state change.</p>
 *
 * <p>Designed to be executed by a background thread or executor.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class DownloadTask extends AbstractTask {

    public static final String DEFAULT_PATH = ".downloads";

    /** Buffer size (8 KB) used for streaming data from the network */
    private static final int BUFFER_SIZE = 8192;
    /**
     * Download model holding runtime state
     */
    private DownloadItem item;
    /**
     * Callback listener for UI or observer updates
     */
    private IDownloadListener listener;
    /**
     * UI row index associated with this download
     */
    private int row;
    /**
     * Path to store the downloaded file
     */
    private Path path = Path.of(DEFAULT_PATH);

    public DownloadTask(DownloadItem item, IDownloadListener listener, int row) {
        this.item = item;
        this.listener = listener;
        this.row = row;
    }

    public DownloadTask(DownloadItem item, IDownloadListener listener, Path path, int row) {
        this(item, listener, row);
        this.path = path;
    }

    /**
     * Executes the download operation.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Marks the item as downloading</li>
     *   <li>Opens a connection to the target URL</li>
     *   <li>Reads data in chunks while updating progress and speed</li>
     *   <li>Handles cancellation requests</li>
     *   <li>Updates final status on completion or failure</li>
     * </ol>
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void run() {
        setStartTime(new Date());

        try {
            // Transition the item into the active downloading state
            item.setStatus(DownloadStatus.DOWNLOADING);
            item.addLog("Starting download: " + item.getUrl());
            listener.update(row);

            // Create URL connection from the item URL
            URL url = URI.create(item.getUrl()).toURL();
            URLConnection conn = url.openConnection();

            // Retrieve the expected content length (it may be -1 if unknown)
            long total = conn.getContentLengthLong();
            item.createTempFile();

            try (InputStream in = conn.getInputStream();
                 OutputStream out = item.openTempOutputStream()) {

                long downloaded = 0;

                // Used for calculating average download speed
                long start = System.currentTimeMillis();

                byte[] buffer = new byte[BUFFER_SIZE];
                int read;

                while ((read = in.read(buffer)) != -1) {

                    // Check for cancellation request
                    if (item.getCancelled().get()) {
                        item.cleanupTempFile();
                        item.setStatus(DownloadStatus.CANCELED);
                        item.addLog("Canceled.");
                        listener.update(row);
                        return;
                    }

                    out.write(buffer, 0, read);
                    // Update downloaded byte count
                    downloaded += read;
                    item.setDownloadedBytes(downloaded);
                    item.setTotalBytes(total);

                    // Calculate average speed in bytes per second
                    long elapsed = System.currentTimeMillis() - start;
                    if (elapsed > 0) {
                        item.setSpeedBytesPerSec(downloaded * 1000 / elapsed);
                    }

                    // Notify listener about progress changes
                    listener.update(row);
                }
            }

            // Download completed successfully
            item.completeDownload(path);
            item.setStatus(DownloadStatus.COMPLETED);
            item.addLog("Download completed.");
            listener.update(row);
            listener.finished(row);

        } catch (Exception e) {
            // Any exception is treated as a download failure
            item.cleanupTempFile();
            item.setStatus(DownloadStatus.FAILED);
            item.addLog("Download failed: " + e.getMessage());
            listener.update(row);
        }
    }

}

