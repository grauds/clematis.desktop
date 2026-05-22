package jworkspace.ui.util;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import lombok.extern.java.Log;

/**
 * Utility engine responsible for recursively searching filesystems,
 * system archives (JAR/ZIP), and Java Classloaders without any GUI bindings.
 */
@Log
public class ResourceScanner {

    /**
     * Scans a target path inside the Classloader environment recursively.
     */
    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:ReturnCount"})
    public void scanClasspathFolder(String targetDir, ResourceHandler handler) {
        ClassLoader classLoader = ResourceScanner.class.getClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        URL anchorUrl = classLoader.getResource(targetDir);
        if (anchorUrl == null) {
            anchorUrl = classLoader.getResource(targetDir + "/_index.txt");
        }

        if (anchorUrl == null) {
            log.warning("Could not automatically locate the '" + targetDir + "' classpath folder root.");
            return;
        }

        try {
            URI uri = anchorUrl.toURI();

            if (uri.toString().endsWith("_index.txt")) {
                if ("jar".equals(uri.getScheme())) {
                    try (FileSystem fs = getOrCreateFileSystem(uri)) {
                        scanPathRecursively(fs.getPath("/" + targetDir), handler);
                    }
                } else {
                    scanPathRecursively(Paths.get(uri).getParent(), handler);
                }
                return;
            }

            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fs = getOrCreateFileSystem(uri)) {
                    scanPathRecursively(fs.getPath("/" + targetDir), handler);
                }
            } else if ("file".equals(uri.getScheme())) {
                scanPathRecursively(Paths.get(uri), handler);
            }
        } catch (Exception ex) {
            log.warning("Failed processing classpath folder exploration: " + ex.getMessage());
        }
    }

    /**
     * Scans an absolute file reference. Automatically paths to either directory flat-walk
     * layouts or parses zip compressed filesystem nodes.
     */
    public void scanFile(File file, BiConsumer<Float, String> progressUpdate, ResourceHandler handler) {
        if (file.isDirectory()) {
            scanDirectory(file, progressUpdate, handler);
        } else {
            String path = file.getPath().toLowerCase();
            if (path.endsWith("jar") || path.endsWith("zip")) {
                scanArchive(file, progressUpdate, handler);
            }
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void scanDirectory(File directory, BiConsumer<Float, String> progressUpdate, ResourceHandler handler) {
        List<Path> allFiles = collectValidFiles(directory.toPath());
        if (allFiles.isEmpty()) {
            return;
        }

        float step = 100f / allFiles.size();
        float progress = 0f;

        for (Path file : allFiles) {
            processSinglePath(file, handler);
            progress += step;
            progressUpdate.accept(progress, file.getFileName().toString());
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void scanArchive(File file, BiConsumer<Float, String> progressUpdate, ResourceHandler handler) {
        try (FileSystem fs = FileSystems.newFileSystem(file.toPath(), (ClassLoader) null)) {
            List<Path> allFiles = collectValidFiles(fs.getPath("/"));
            if (allFiles.isEmpty()) {
                return;
            }

            float step = 100f / allFiles.size();
            float progress = 0f;

            for (Path entry : allFiles) {
                processSinglePath(entry, handler);
                progress += step;
                progressUpdate.accept(progress, entry.getFileName().toString());
            }
        } catch (IOException ex) {
            log.warning("Failed to open archive system for scan: " + ex.getMessage());
        }
    }

    private void scanPathRecursively(Path folderPath, ResourceHandler handler) {
        if (!Files.exists(folderPath)) {
            return;
        }
        List<Path> targetFiles = collectValidFiles(folderPath);
        for (Path file : targetFiles) {
            processSinglePath(file, handler);
        }
    }

    private List<Path> collectValidFiles(Path root) {
        List<Path> collected = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(Files::isRegularFile)
                .filter(this::isImageFile)
                .forEach(collected::add);
        } catch (IOException e) {
            log.warning("Error walking virtual directories: " + e.getMessage());
        }
        return collected;
    }

    private void processSinglePath(Path targetFile, ResourceHandler handler) {
        try (InputStream is = Files.newInputStream(targetFile)) {
            Path fileNameNode = targetFile.getFileName();
            String name = (fileNameNode != null) ? fileNameNode.toString() : "";
            handler.handle(is, name);
        } catch (IOException ex) {
            log.warning("Error handling resource asset stream processing: " + targetFile);
        }
    }

    private boolean isImageFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    private FileSystem getOrCreateFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }
    /**
     * Functional interface to receive found resource items during traversal.
     * Arguments supplied: InputStream to data, and the plain item file name.
     */
    @FunctionalInterface
    public interface ResourceHandler {
        void handle(InputStream is, String filename) throws IOException;
    }

}
