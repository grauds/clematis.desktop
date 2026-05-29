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
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a single download task and its runtime state.
 *
 * <p>This class is designed to be thread-safe for concurrent updates coming
 * from a downloader thread and UI observers. Volatile fields are used for
 * frequently updated values, while {@link CopyOnWriteArrayList} is used
 * for immutable log snapshots.</p>
 */
@Data
public class DownloadItem implements Comparable<DownloadItem> {

    private final UUID id = UUID.randomUUID();

    /** Wrapping task to get/set logs from/to */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final DownloadTask task = new DownloadTask(this);

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
     * Creates a new download item and initializes its log.
     */
    public DownloadItem() {
        log("Task created.");
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
     * Adds a timestamped log entry to this download.
     *
     * @param message log message
     */
    public void log(String message) {
        getTask().log(LocalTime.now().withNano(0) + " " + message);
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
            log("Temporary file created: " + tempFile);
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

        Files.move(tempFile, targetFile, REPLACE_EXISTING);
        tempFile = null;

        log("Download finalized: " + completedFile);
    }

    /**
     * Deletes the temporary file if the download was canceled or failed.
     */
    public synchronized void cleanupTempFile() {
        try {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
                log("Temporary file deleted");
            }
        } catch (IOException ignored) {
            // Best-effort cleanup
        } finally {
            tempFile = null;
        }
    }
    /**
     * Converts this runtime item to a DTO for serialization.
     */
    public DownloadItemDTO toDTO() {
        DownloadItemDTO dto = new DownloadItemDTO();
        dto.setUrl(this.url);
        dto.setFileName(this.fileName);
        if (this.getCompletedFile() != null) {
            dto.setCompletedFile(this.getCompletedFile().toString());
        }
        dto.setTotalBytes(this.totalBytes);
        dto.setDownloadedBytes(this.downloadedBytes);
        dto.setStatus(this.status.name());
        dto.setProgress(this.progress);
        dto.setEtaSeconds(this.etaSeconds);
        dto.setExpectedChecksum(this.expectedChecksum);
        dto.setActualChecksum(this.actualChecksum);
        dto.setLogs(this.getTask().getLogs());
        return dto;
    }

    /**
     * Restores a runtime item from a DTO.
     */
    public static DownloadItem fromDTO(DownloadItemDTO dto) {
        DownloadItem item = new DownloadItem(dto.getUrl(), dto.getFileName());
        if (dto.getCompletedFile() != null) {
            item.setCompletedFile(Path.of(dto.getCompletedFile()));
        }
        item.setTotalBytes(dto.getTotalBytes());
        item.setDownloadedBytes(dto.getDownloadedBytes());
        item.setStatus(DownloadStatus.valueOf(dto.getStatus()));
        item.setProgress(dto.getProgress());
        item.setEtaSeconds(dto.getEtaSeconds());
        item.setExpectedChecksum(dto.getExpectedChecksum());
        item.setActualChecksum(dto.getActualChecksum());

        // Restore logs if they exist
        if (dto.getLogs() != null) {
            item.getTask().clearLogs(); // Clear default "Task created" log
            item.getTask().setLogs(dto.getLogs());
        }
        return item;
    }

    @Override
    public int compareTo(DownloadItem o) {
        return o.getId().compareTo(id);
    }
}

