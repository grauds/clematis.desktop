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
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.ui.KFileChooser;
import com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog;

import jworkspace.ui.api.action.UISwitchListener;
import lombok.extern.java.Log;

/**
 * Workspace cache for heavy UI components
 * @author Anton Troshin
 */
@Log
public final class ClassCache {

    private static final String JAR_EXTENTION = "jar";
    private static final String ZIP_EXTENTION = "zip";
    private static final String CACHE_CHOOSE_ARCHIVE_TITLE = "Cache.chooseArchive";
    private static final String CACHE_CHOOSE_ARCHIVE_OR_DIR_TITLE = "Cache.chooseArchiveOrDir";
    private static final String CACHE_ARCHIVES_TITLE = "Cache.Archives";
    private static final String CACHE_CHOOSE_DIRECTORY_TITLE = "Cache.chooseDirectory";
    private static final String CACHE_CHOOSE_FILE_TITLE = "Cache.chooseFile";
    private static final String CACHE_CHOOSE_HTML_TITLE = "Cache.chooseHTML";
    private static final String HTML_EXTENTION = "html";
    private static final String HTM_EXTENTION = "htm";
    private static final String SHTML_EXTENTION = "shtml";
    private static final String CACHE_HYPERTEXT_TITLE = "Cache.hypertext";
    private static final String CACHE_CHOOSE_HTMLOR_DIRECTORY_TITLE = "Cache.chooseHTMLOrDirectory";
    private static final String USER_HOME_PROPERTY = "user.home";

    // common cache
    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    // file dialogs
    private static JFileChooser fileChooser = null;
    private static FilePreviewer previewer = null;
    private static JFileChooser iconChooser = null;

    /**
     * Default public constructor
     */
    private ClassCache() {
        super();
    }

    /**
     * This method allows to choose archive of jar or zip type.
     */
    public static File chooseArchive(Component parent) {
        getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_ARCHIVE_TITLE),
                new String[]{JAR_EXTENTION, ZIP_EXTENTION},
                ResourceAnchor.getString(CACHE_ARCHIVES_TITLE)
            ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose archive of jar or zip type plus
     * directory.
     */
    public static File chooseArchiveOrDir(Component parent) {
        getFileChooser(
            ResourceAnchor.getString(CACHE_CHOOSE_ARCHIVE_OR_DIR_TITLE),
            new String[]{JAR_EXTENTION, ZIP_EXTENTION},
            ResourceAnchor.getString(CACHE_ARCHIVES_TITLE)
        ).setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return (getFileChooser().getSelectedFile());
    }

    /**
     * Use this method to choose color.
     */
    public static Color chooseColor(Frame parent, String title, Color initialColor) {
        return JColorChooser.showDialog(parent, title, initialColor);
    }

    /**
     * This method allows to choose directory.
     */
    public static File chooseDirectory(Frame parent) {
        getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_DIRECTORY_TITLE),
                null, null
            ).setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose directory inside of specified root.
     */
    public static File chooseDirectory(Frame parent, File root) {
        KFileChooserDialog dirChooser = new KFileChooserDialog(parent,
            ResourceAnchor.getString(CACHE_CHOOSE_DIRECTORY_TITLE),
            KFileChooser.OPEN_DIALOG);

        UIManager.addPropertyChangeListener(new UISwitchListener(dirChooser));
        dirChooser.setFileSelectionMode(KFileChooser.DIRECTORIES_ONLY);
        dirChooser.setVisible(true);

        return (dirChooser.getSelectedFile());
    }

    /**
     * This method allows to choose any file.
     */
    public static File chooseFile(Component parent) {
        getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_FILE_TITLE),
                null, null
            ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose html file.
     */
    public static File chooseHTMLFile(Component parent) {
        getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_HTML_TITLE),
                new String[]{HTML_EXTENTION, HTM_EXTENTION, SHTML_EXTENTION},
                ResourceAnchor.getString(CACHE_HYPERTEXT_TITLE)
            ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }
        return getFileChooser().getSelectedFile();
    }

    /**
     * This method allows to choose html file or directory.
     */
    public static File chooseHTMLFileOrDir(Component parent) {
        getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_HTMLOR_DIRECTORY_TITLE),
                new String[]{HTML_EXTENTION, HTM_EXTENTION, SHTML_EXTENTION},
                ResourceAnchor.getString(CACHE_HYPERTEXT_TITLE)
            ).setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (getFileChooser().showOpenDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return getFileChooser().getSelectedFile();
    }

    /**
     * This method allows to choose icon.
     */
    public static Image chooseImage(Component parent) {
        return chooseImage(parent, null);
    }

    /**
     * This method allows to choose icon.
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    private static Image chooseImage(Component parent, String path) {
        if (getIconChooser(path).showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File imf = (getIconChooser().getSelectedFile());

            if (imf != null) {
                ImageIcon testCover;
                try {
                    BufferedImage icon = ImageIO.read(imf);
                    if (icon != null) {
                        testCover = new ImageIcon();
                        return (testCover.getIconHeight() != -1 && testCover.getIconWidth() != -1)
                            ? testCover.getImage() : null;
                    }
                } catch (IOException e) {
                    log.severe(e.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    /**
     * This method allows to choose file to save.
     */
    public static File chooseSaveFile(Component parent) {
        getFileChooser(ResourceAnchor.getString("Cache.saveAs"),
                null, null
            ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        getFileChooser().setCurrentDirectory(new File(System.getProperty(USER_HOME_PROPERTY)));

        if (getFileChooser().showSaveDialog(parent)
            != JFileChooser.APPROVE_OPTION) {
            return (null);
        }

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method is called once to create filechoosers.
     */
    public static void createFileChoosers() {
        getFileChooser();
        getIconChooser();
    }

    /**
     * This method is called once to reset filechoosers.
     */
    static void resetFileChoosers() {
        fileChooser = null;
        iconChooser = null;
    }

    /**
     * Get methos of class cache
     */
    public static Object get(Object key) {
        return OBJECT_MAP.get(key);
    }

    /**
     * Returns archive chooser
     */
    public static JFileChooser getArchiveChooser() {
        return ClassCache.getFileChooser(ResourceAnchor.getString(CACHE_CHOOSE_ARCHIVE_TITLE),
                new String[]{JAR_EXTENTION, ZIP_EXTENTION},
                ResourceAnchor.getString(CACHE_ARCHIVES_TITLE));
    }

    /**
     * Returns file chooser
     */
    public static JFileChooser getFileChooser(String title,
                                              String[] ext, String description) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(title);
            fileChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(fileChooser));
            fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME_PROPERTY)));
        }
        fileChooser.setDialogTitle(title);
        fileChooser.resetChoosableFileFilters();
        if (ext != null && description != null) {
            fileChooser.addChoosableFileFilter(new WorkspaceFileFilter(ext, description));
        }
        return fileChooser;
    }

    /**
     * Get file chooser with default parameters
     */
    private static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(fileChooser));
            fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME_PROPERTY)));
        }
        return fileChooser;
    }

    /**
     * Get html files chooser
     */
    public static JFileChooser getHtmlChooser() {
        return ClassCache.getFileChooser(ResourceAnchor.getString("Cache.selectHTML"),
                new String[]{HTML_EXTENTION, SHTML_EXTENTION, HTM_EXTENTION},
                ResourceAnchor.getString(CACHE_HYPERTEXT_TITLE));
    }

    /**
     * Get ICON chooser
     */
    public static JFileChooser getIconChooser() {
        return getIconChooser(null);
    }

    /**
     * Get ICON chooser
     */
    public static JFileChooser getIconChooser(String path) {
        if (iconChooser == null) {
            iconChooser = new JFileChooser(ResourceAnchor.getString("Cache.chooseIcon"));
            previewer = new FilePreviewer(iconChooser);
            iconChooser.setAccessory(previewer);
            WorkspaceFileFilter ff = new WorkspaceFileFilter(new String[]{"gif",
                "jpg", "jpeg", "png", "ico", "bmp", "psd",
                "tga", "cur", "tiff", "xpm"},
                ResourceAnchor.getString("Cache.allFormats"));
            ff.setExtensionListInDescription(false);
            iconChooser.addChoosableFileFilter(ff);
            iconChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(iconChooser));
        }
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                iconChooser.setSelectedFile(file);
                iconChooser.setCurrentDirectory(file);
            } else {
                iconChooser.setSelectedFile(null);
                previewer.reset();
            }
        }
        return iconChooser;
    }

    /**
     * Put method of class cache
     */
    public static void put(String key, Object value) {
        OBJECT_MAP.put(key, value);
    }

    /**
     * Update UI of choosers.
     * (BUG #211610) - should update all components in cache.
     */
    public static void updateUI() {
        fileChooser.updateUI();
        iconChooser.updateUI();
    }
}