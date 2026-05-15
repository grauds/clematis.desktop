package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hyperrealm.kiwi.ui.KFileChooser;
import com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog;

import lombok.extern.java.Log;

/**
 * Workspace cache for heavy UI components
 * @author Anton Troshin
 */

@Log
public final class ClassCache {

    private static final String JAR_EXTENSION = "jar";
    private static final String ZIP_EXTENSION = "zip";
    private static final String HTML_EXTENSION = "html";
    private static final String HTM_EXTENSION = "htm";
    private static final String SHTML_EXTENSION = "shtml";
    private static final String USER_HOME_PROPERTY = "user.home";

    private static final String CACHE_CHOOSE_ARCHIVE_TITLE = "Cache.chooseArchive";
    private static final String CACHE_CHOOSE_ARCHIVE_OR_DIR_TITLE = "Cache.chooseArchiveOrDir";
    private static final String CACHE_ARCHIVES_TITLE = "Cache.Archives";
    private static final String CACHE_CHOOSE_DIRECTORY_TITLE = "Cache.chooseDirectory";
    private static final String CACHE_CHOOSE_FILE_TITLE = "Cache.chooseFile";
    private static final String CACHE_CHOOSE_HTML_TITLE = "Cache.chooseHTML";
    private static final String CACHE_HYPERTEXT_TITLE = "Cache.hypertext";
    private static final String CACHE_CHOOSE_HTMLOR_DIRECTORY_TITLE = "Cache.chooseHTMLOrDirectory";
    private static final String CACHE_SAVE_AS_TITLE = "Cache.saveAs";

    private static FilePreviewer previewer;

    private ClassCache() {
        throw new AssertionError("No ClassCache instances!");
    }

    public static File chooseArchive(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_ARCHIVE_TITLE),
            ResourceAnchor.getString(CACHE_ARCHIVES_TITLE),
            JAR_EXTENSION, ZIP_EXTENSION
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static File chooseArchiveOrDir(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_ARCHIVE_OR_DIR_TITLE),
            ResourceAnchor.getString(CACHE_ARCHIVES_TITLE),
            JAR_EXTENSION, ZIP_EXTENSION
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static Color chooseColor(Frame parent, String title, Color initialColor) {
        return JColorChooser.showDialog(parent, title, initialColor);
    }

    public static File chooseDirectory(Frame parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_DIRECTORY_TITLE),
            null
        );
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static File chooseDirectory(Frame parent, File root) {
        var dirChooser = new KFileChooserDialog(
            parent,
            ResourceAnchor.getString(CACHE_CHOOSE_DIRECTORY_TITLE),
            KFileChooser.OPEN_DIALOG
        );
        dirChooser.setFileSelectionMode(KFileChooser.DIRECTORIES_ONLY);
        if (root != null) {
            dirChooser.setCurrentDirectory(root);
        }
        dirChooser.setVisible(true);
        return dirChooser.getSelectedFile();
    }

    public static File chooseFile(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_FILE_TITLE),
            null
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static File chooseHTMLFile(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_HTML_TITLE),
            ResourceAnchor.getString(CACHE_HYPERTEXT_TITLE),
            HTML_EXTENSION, HTM_EXTENSION, SHTML_EXTENSION
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static File chooseHTMLFileOrDir(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_HTMLOR_DIRECTORY_TITLE),
            ResourceAnchor.getString(CACHE_HYPERTEXT_TITLE),
            HTML_EXTENSION, HTM_EXTENSION, SHTML_EXTENSION
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return showOpenDialogAndGetFile(chooser, parent);
    }

    public static Image chooseImage(Component parent) {
        return chooseImage(parent, null);
    }

    @SuppressWarnings("checkstyle:NestedIfDepth")
    private static Image chooseImage(Component parent, String path) {
        var chooser = getIconChooserInstance();
        if (path != null && !path.isBlank()) {
            chooser.setCurrentDirectory(new File(path));
        }

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File imf = chooser.getSelectedFile();
            if (imf != null) {
                try {
                    BufferedImage icon = ImageIO.read(imf);
                    if (icon != null) {
                        var testCover = new ImageIcon(icon);
                        if (testCover.getIconHeight() != -1 && testCover.getIconWidth() != -1) {
                            return testCover.getImage();
                        }
                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, e, () -> "Failed to read image file: " + imf.getAbsolutePath());
                }
            }
        }
        return null;
    }

    public static File chooseSaveFile(Component parent) {
        var chooser = prepareFileChooser(
            ResourceAnchor.getString(CACHE_SAVE_AS_TITLE),
            null
        );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File(System.getProperty(USER_HOME_PROPERTY, ".")));

        return (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
            ? chooser.getSelectedFile()
            : null;
    }

    public static JFileChooser getFileChooserInstance() {
        return FileChooserHolder.INSTANCE;
    }

    public static JFileChooser getIconChooserInstance() {
        return IconChooserHolder.INSTANCE;
    }

    /**
     * Returns the global file chooser initialized with a title, specific extensions, and description.
     * Fully compatible with legacy callers while fixing the multi-filter accumulation bug.
     */
    public static JFileChooser getFileChooser(String title, String[] ext, String description) {
        // Safely bridge the legacy String[] array to the modern Java 25 Varargs pipeline
        return prepareFileChooser(title, description, ext);
    }

    /**
     * Returns the icon file chooser instance initialized to a specific directory path.
     * Safe for use with Java 25 Virtual Threads.
     */
    public static JFileChooser getIconChooser(String path) {
        JFileChooser chooser = IconChooserHolder.INSTANCE;

        // Clear out any old lingering file filters from prior transactions
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        if (previewer == null) {
            previewer = new FilePreviewer(chooser);
        }
        chooser.setAccessory(previewer);

        if (path != null && !path.isBlank()) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                chooser.setCurrentDirectory(dir);
            }
        }
        return chooser;
    }
    /**
     * Returns the icon file chooser instance.
     * Backward compatible signature used by external caller setups.
     */
    public static JFileChooser getIconChooser() {
        return IconChooserHolder.INSTANCE;
    }

    private static JFileChooser prepareFileChooser(String title, String description, String... extensions) {
        var chooser = getFileChooserInstance();
        chooser.setDialogTitle(title);

        // Wipe out ALL existing user-defined filters to stop multi-filter accumulation
        chooser.resetChoosableFileFilters();

        if (extensions != null && extensions.length > 0) {
            // Safe Fallback: Prevent the literal "null" string representation if description is absent
            String safeDescription = (description == null || description.isBlank())
                ? "Files (*." + String.join(", *.", extensions) + ")"
                : description;

            var filter = new FileNameExtensionFilter(safeDescription, extensions);

            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
        } else {
            // Fall back cleanly to accept all if no constraints provided
            chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        }
        return chooser;
    }

    private static File showOpenDialogAndGetFile(JFileChooser chooser, Component parent) {
        return (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
            ? chooser.getSelectedFile()
            : null;
    }

    public static void createFileChoosers() {
        // Trigger lazy-loading explicitly if required early
        Objects.requireNonNull(getFileChooserInstance());
        Objects.requireNonNull(getIconChooserInstance());
    }

    // Thread-safe Lazy Initialization Holders (Virtual Thread Safe / No Synchronized Locks)
    private static class FileChooserHolder {
        private static final JFileChooser INSTANCE = new JFileChooser();
    }

    private static class IconChooserHolder {
        private static final JFileChooser INSTANCE = new JFileChooser();
    }
}
