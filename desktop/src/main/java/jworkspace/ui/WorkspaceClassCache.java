package jworkspace.ui;

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

import com.hyperrealm.kiwi.ui.KFileChooser;
import com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog;
import jworkspace.LangResource;
import jworkspace.ui.widgets.FilePreviewer;
import jworkspace.ui.widgets.WorkspaceFileFilter;
import jworkspace.ui.widgets.WorkspaceFileView;
import jworkspace.ui.action.UISwitchListener;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Workspace CACHE. This cashe is simple
 * mean for storing dynamically loaded
 * classes.
 */
public final class WorkspaceClassCache
{
// common cache
    private static Hashtable cache = new Hashtable();
// file dialogs
    private static JFileChooser fileChooser = null;
    private static FilePreviewer previewer = null;
    private static JFileChooser iconChooser = null;
    private static KFileChooserDialog dirChooser = null;

    /**
     * Default public constructor
     */
    public WorkspaceClassCache()
    {
        super();
    }

    /**
     * This method allows to choose archive of jar or zip type.
     */
    public static File chooseArchive(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseArchive"),
                 new String[]{"jar", "zip"},
                 LangResource.getString("Cache.Archives")
                ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose archive of jar or zip type plus
     * directory.
     */
    public static File chooseArchiveOrDir(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseArchiveOrDir"),
                 new String[]{"jar", "zip"},
                 LangResource.getString("Cache.Archives")
                ).setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return (getFileChooser().getSelectedFile());
    }

    /**
     * Use this method to choose color.
     */
    public static Color chooseColor(Frame parent, String title, Color initialColor)
    {
        return JColorChooser.showDialog(parent, title, initialColor);
    }

    /**
     * This method allows to choose directory.
     */
    public static File chooseDirectory(Frame parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseDirectory"),
                 null, null
                ).setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose directory inside of specified root.
     */
    public static File chooseDirectory(Frame parent, File root)
    {
        dirChooser = new KFileChooserDialog
                (parent,
                 LangResource.getString("Cache.chooseDirectory"),
                        KFileChooser.OPEN_DIALOG);

        UIManager.addPropertyChangeListener(new UISwitchListener(dirChooser));
        dirChooser.setFileSelectionMode(KFileChooser.DIRECTORIES_ONLY);
        dirChooser.setVisible(true);

        return (dirChooser.getSelectedFile());
    }

    /**
     * This method allows to choose any file.
     */
    public static File chooseFile(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseFile"),
                 null, null
                ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method allows to choose html file.
     */
    public static File chooseHTMLFile(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseHTML"),
                 new String[]{"html", "htm", "shtml"},
                 LangResource.getString("Cache.hypertext")
                ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);
        return getFileChooser().getSelectedFile();
    }

    /**
     * This method allows to choose html file or directory.
     */
    public static File chooseHTMLFileOrDir(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.chooseHTMLOrDirectory"),
                 new String[]{"html", "htm", "shtml"},
                 LangResource.getString("Cache.hypertext")
                ).setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (getFileChooser().showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return getFileChooser().getSelectedFile();
    }

    /**
     * This method allows to choose icon.
     */
    public static Image chooseImage(Component parent)
    {
        if (getIconChooser().showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return (null);

        File imf = (getIconChooser().getSelectedFile());

        if (imf != null)
        {
            ImageIcon test_cover;
            try {
                test_cover = new ImageIcon(Imaging.getBufferedImage(imf));
                if (test_cover.getIconHeight() != -1 && test_cover.getIconWidth() != -1)
                {
                    return test_cover.getImage();
                }
            } catch (ImageReadException | IOException e) {
                 //
            }
        }
        return null;
    }

    /**
     * This method allows to choose icon.
     */
    public static Image chooseImage(Component parent, String path)
    {
        if (getIconChooser(path == null ? " " : path).showOpenDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        File imf = (getIconChooser().getSelectedFile());

        if (imf != null)
        {
            ImageIcon test_cover;
            try {
                test_cover = new ImageIcon(Imaging.getBufferedImage(imf));
                if (test_cover.getIconHeight() != -1 && test_cover.getIconWidth() != -1)
                {
                    return test_cover.getImage();
                }
            } catch (ImageReadException | IOException e) {
                //
            }
        }
        return null;
    }

    /**
     * This method allows to choose file to save.
     */
    public static File chooseSaveFile(Component parent)
    {
        getFileChooser
                (LangResource.getString("Cache.saveAs"),
                 null, null
                ).setFileSelectionMode(JFileChooser.FILES_ONLY);

        getFileChooser().setCurrentDirectory
                (new File(System.getProperty("user.home")));

        if (getFileChooser().showSaveDialog(parent)
                != JFileChooser.APPROVE_OPTION)
            return (null);

        return (getFileChooser().getSelectedFile());
    }

    /**
     * This method is called once to create filechoosers.
     */
    public static void createFileChoosers()
    {
        getFileChooser();
        getIconChooser();
    }

    /**
     * This method is called once to reset filechoosers.
     */
    public static void resetFileChoosers()
    {
        fileChooser = null;
        iconChooser = null;
    }

    /**
     * Get methos of class cache
     */
    public static Object get(Object key)
    {
        return cache.get(key);
    }

    /**
     * Returns archive chooser
     */
    public static JFileChooser getArchiveChooser()
    {
        return WorkspaceClassCache.getFileChooser
                (LangResource.getString("Cache.chooseArchive"),
                 new String[]{"jar", "zip"},
                 LangResource.getString("Cache.Archives"));
    }

    /**
     * Returns file chooser
     */
    public static JFileChooser getFileChooser(String title,
                                              String[] ext, String description)
    {
        if (fileChooser == null)
        {
            fileChooser = new JFileChooser(title);
            fileChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(fileChooser));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        fileChooser.setDialogTitle(title);
        fileChooser.resetChoosableFileFilters();
        if (ext != null && description != null)
            fileChooser.addChoosableFileFilter(new WorkspaceFileFilter(ext, description));
        return fileChooser;
    }

    /**
     * Get file chooser with default parameters
     */
    public static JFileChooser getFileChooser()
    {
        if (fileChooser == null)
        {
            fileChooser = new JFileChooser();
            fileChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(fileChooser));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        return fileChooser;
    }

    /**
     * Get html files chooser
     */
    public static JFileChooser getHtmlChooser()
    {
        return WorkspaceClassCache.getFileChooser
                (LangResource.getString("Cache.selectHTML"),
                 new String[]{"html", "shtml", "htm"},
                 LangResource.getString("Cache.hypertext"));
    }

    /**
     * Get icon chooser
     */
    public static JFileChooser getIconChooser()
    {
        if (iconChooser == null)
        {
            iconChooser = new JFileChooser(LangResource.getString("Cache.chooseIcon"));
            previewer = new FilePreviewer(iconChooser);
            iconChooser.setAccessory(previewer);
            WorkspaceFileFilter ff = new WorkspaceFileFilter(new String[]{"gif",
                                                                          "jpg", "jpeg", "png", "ico", "bmp", "psd",
                                                                          "tga", "cur", "tiff", "xpm"},
                                                             LangResource.getString("Cache.allFormats"));

            ff.setExtensionListInDescription(false);
            iconChooser.addChoosableFileFilter(ff);
            iconChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(iconChooser));
            iconChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        return iconChooser;
    }

    /**
     * Get icon chooser
     */
    public static JFileChooser getIconChooser(String path)
    {
        if (iconChooser == null)
        {
            iconChooser = new JFileChooser(LangResource.getString("Cache.chooseIcon"));
            previewer = new FilePreviewer(iconChooser);
            iconChooser.setAccessory(previewer);
            WorkspaceFileFilter ff = new WorkspaceFileFilter(new String[]{"gif",
                                                                          "jpg", "jpeg", "png", "ico", "bmp", "psd",
                                                                          "tga", "cur", "tiff", "xpm"},
                                                             LangResource.getString("Cache.allFormats"));
            ff.setExtensionListInDescription(false);
            iconChooser.addChoosableFileFilter(ff);
            iconChooser.setFileView(new WorkspaceFileView());
            UIManager.addPropertyChangeListener(new UISwitchListener(iconChooser));
        }
        File file = new File(path);
        if (file.exists())
        {
            iconChooser.setSelectedFile(file);
            iconChooser.setCurrentDirectory(file);
        }
        else
        {
            iconChooser.setSelectedFile(null);
            previewer.reset();
        }
        return iconChooser;
    }

    /**
     * Put method of class cache
     */
    public static void put(Object key, Object value)
    {
        cache.put(key, value);
    }

    /**
     * Update UI of choosers.
     * (BUG #211610) - should update all components in cache.
     */
    public static void updateUI()
    {
        fileChooser.updateUI();
        iconChooser.updateUI();
    }
}