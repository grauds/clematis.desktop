package jworkspace.runtime.downloader.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService {

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void start(DownloadTask task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
