package jworkspace.runtime.downloader.service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;

@Data
public class DownloadItem {

    private String url;
    private String fileName;
    private volatile long totalBytes = -1;
    private volatile long downloadedBytes = 0;
    private volatile long speedBytesPerSec = 0;
    private volatile DownloadStatus status = DownloadStatus.QUEUED;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final long createdAt = System.currentTimeMillis();
    private double speedKb;
    private long etaSeconds;
    private long lastTime;
    private long lastBytes;
    private int progress;

    private String expectedChecksum;
    private String actualChecksum;

    private final List<String> logs = new CopyOnWriteArrayList<>();

    public DownloadItem() {
        addLog("Task created.");
    }

    public DownloadItem(String url, String fileName) {
        this();
        this.url = url;
        this.fileName = fileName;
    }

    public DownloadItem(String url, DownloadStatus status, int progress, int etaSeconds) {
        this(url, url.substring(url.lastIndexOf('/') + 1));
        this.status = status;
        this.progress = progress;
        this.etaSeconds = etaSeconds;
    }

    public void addLog(String message) {
        logs.add(time() + " " + message);
    }

    public List<String> getLogs() {
        return List.copyOf(logs);
    }

    private String time() {
        return LocalTime.now().withNano(0).toString();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public int getProgressPercent() {
        if (totalBytes <= 0) {
            return 0;
        }
        return (int) (downloadedBytes * 100 / totalBytes);
    }

    public long getEtaSeconds() {
        if (speedBytesPerSec <= 0 || totalBytes <= 0) {
            return -1;
        }
        long remaining = totalBytes - downloadedBytes;
        return remaining / speedBytesPerSec;
    }

    public boolean isFinished() {
        return status == DownloadStatus.COMPLETED
            || status == DownloadStatus.FAILED
            || status == DownloadStatus.CANCELED;
    }
}
