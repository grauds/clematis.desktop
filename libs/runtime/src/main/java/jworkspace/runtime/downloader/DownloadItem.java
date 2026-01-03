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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;

/**
 * Represents a single download task and its runtime state.
 *
 * <p>This class is designed to be thread-safe for concurrent updates coming
 * from a downloader thread and UI observers. Volatile fields are used for
 * frequently updated values, while {@link CopyOnWriteArrayList} is used
 * for immutable log snapshots.</p>
 */
@Data
public class DownloadItem {

    /** Source URL of the download */
    private String url;

    /** Target file name on disk */
    private String fileName;

    /** Total size of the file in bytes, or -1 if unknown */
    private volatile long totalBytes = -1;

    /** Bytes downloaded */
    private volatile long downloadedBytes = 0;

    /** Temporary file where binary data is written during download */
    private volatile Path tempFile;

    /** Final file path after successful download */
    private volatile Path completedFile;

    /** Current download speed in bytes per second */
    private volatile long speedBytesPerSec = 0;

    /** Current download state */
    private volatile DownloadStatus status = DownloadStatus.QUEUED;

    /** Flag indicating that cancellation was requested */
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    /** Timestamp (epoch millis) when the task was created */
    private final long createdAt = System.currentTimeMillis();

    /** Cached speed in kilobytes per second (used by UI) */
    private double speedKb;

    /** Estimated time remaining in seconds */
    private long etaSeconds;

    /** Timestamp of the last speed calculation */
    private long lastTime;

    /** Downloaded byte count at the last speed calculation */
    private long lastBytes;

    /** Cached progress percentage (0–100) */
    private int progress;

    /** Expected checksum (e.g. SHA-256) provided by metadata */
    private String expectedChecksum;

    /** Actual checksum calculated after download completion */
    private String actualChecksum;

    /**
     * Thread-safe list of log messages associated with this download.
     * <p>
     * CopyOnWriteArrayList is used to allow safe iteration from the UI thread
     * without explicit synchronization.
     * </p>
     */
    private final List<String> logs = new CopyOnWriteArrayList<>();

    /** Listeners for live log updates */
    private final List<IDownloadLogListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Creates a new download item and initializes its log.
     */
    public DownloadItem() {
        addLog("Task created.");
    }

    /**
     * Creates a new download item with URL and target file name.
     *
     * @param url      source URL
     * @param fileName output file name
     */
    public DownloadItem(String url, String fileName) {
        this();
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * Creates a download item using the file name derived from the URL.
     * Typically used when restoring tasks from persisted state.
     *
     * @param url         source URL
     * @param status      current download status
     * @param progress    current progress percentage
     * @param etaSeconds  estimated time remaining in seconds
     */
    public DownloadItem(String url, DownloadStatus status, int progress, int etaSeconds) {
        this(url, url.substring(url.lastIndexOf('/') + 1));
        this.status = status;
        this.progress = progress;
        this.etaSeconds = etaSeconds;
    }

    /**
     * Returns an immutable snapshot of the log entries.
     *
     * @return list of log messages
     */
    public List<String> getLogs() {
        return List.copyOf(logs);
    }

    /** Add a listener for live log updates */
    public void addListener(IDownloadLogListener listener) {
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeListener(IDownloadLogListener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a timestamped log entry to this download.
     *
     * @param message log message
     */
    public void addLog(String message) {
        String line = LocalTime.now().withNano(0) + " " + message;
        logs.add(line);

        // push to all listeners
        for (IDownloadLogListener l : listeners) {
            l.log(line);
        }
    }

    /**
     * Returns the current local time without nanoseconds,
     * formatted as {@code HH:mm:ss}.
     */
    private String time() {
        return LocalTime.now().withNano(0).toString();
    }

    /**
     * Calculates the current progress percentage.
     *
     * @return progress from 0 to 100, or 0 if total size is unknown
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public int getProgressPercent() {
        if (totalBytes <= 0) {
            return 0;
        }
        return (int) (downloadedBytes * 100 / totalBytes);
    }

    /**
     * Calculates the estimated remaining time in seconds.
     *
     * @return ETA in seconds, or -1 if it cannot be calculated
     */
    public long getEtaSeconds() {
        if (speedBytesPerSec <= 0 || totalBytes <= 0) {
            return -1;
        }
        long remaining = totalBytes - downloadedBytes;
        return remaining / speedBytesPerSec;
    }

    /**
     * Indicates whether the download has reached a terminal state.
     *
     * @return {@code true} if completed, failed, or canceled
     */
    public boolean isFinished() {
        return status == DownloadStatus.COMPLETED
            || status == DownloadStatus.FAILED
            || status == DownloadStatus.CANCELED;
    }

    /**
     * Creates a temporary file for storing downloaded binary data.
     *
     * <p>The file is created lazily and should be written to using
     * {@link #openTempOutputStream()}.</p>
     *
     * @throws IOException if the temp file cannot be created
     */
    public synchronized void createTempFile() throws IOException {
        if (tempFile == null) {
            tempFile = Files.createTempFile("download-", ".part");
            addLog("Temporary file created: " + tempFile);
        }
    }

    /**
     * Opens an output stream to the temporary download file.
     *
     * @return output stream for writing binary data
     * @throws IOException if the file cannot be opened
     */
    public OutputStream openTempOutputStream() throws IOException {
        if (tempFile == null) {
            throw new IllegalStateException("Temporary file not created");
        }
        return Files.newOutputStream(tempFile);
    }

    /**
     * Finalizes the download by renaming the temporary file to its final name.
     *
     * <p>This method should be called only after a successful download.</p>
     *
     * @param targetDirectory directory where the file should be placed
     * @throws IOException if the file cannot be moved
     */
    public synchronized void completeDownload(Path targetDirectory) throws IOException {
        if (tempFile == null) {
            throw new IllegalStateException("No temporary file to finalize");
        }

        Path targetFile = Files.createDirectories(targetDirectory).resolve(fileName);
        completedFile = targetFile;

        Files.move(tempFile, targetFile);
        tempFile = null;

        addLog("Download finalized: " + completedFile);
    }

    /**
     * Deletes the temporary file if the download was canceled or failed.
     */
    public synchronized void cleanupTempFile() {
        try {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
                addLog("Temporary file deleted");
            }
        } catch (IOException ignored) {
            // Best-effort cleanup
        } finally {
            tempFile = null;
        }
    }

}

