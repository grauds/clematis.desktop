package jworkspace.runtime.downloader.service;

public enum DownloadStatus {
    QUEUED,
    DOWNLOADING,
    VERIFYING,
    COMPLETED,
    FAILED,
    CANCELED
}