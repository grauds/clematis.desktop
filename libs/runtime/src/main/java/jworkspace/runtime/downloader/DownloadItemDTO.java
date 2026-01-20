package jworkspace.runtime.downloader;

import java.util.List;

import lombok.Data;

/**
 * DTO for GSON serialization of DownloadItem.
 * Contains only simple types to avoid Java internal encapsulation issues.
 */
@Data
public class DownloadItemDTO {
    private String url;
    private String fileName;
    private long totalBytes;
    private long downloadedBytes;
    private String status;
    private int progress;
    private long etaSeconds;
    private String expectedChecksum;
    private String actualChecksum;
    private List<String> logs;
}