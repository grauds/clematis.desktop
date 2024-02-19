/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui.model.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.Icon;

import com.hyperrealm.kiwi.ui.model.ModelProperties;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * An implementation of
 * {@link TreeDataSource TreeDataSource}
 * wherein tree nodes represent files in the local filesystem. The
 * <code>ignoreFiles</code> argument of some forms of the constructor allows
 * for the creation of directory-only data sources. These are useful for
 * driving a directory chooser, for example.
 *
 * @author Mark Lindner
 * @see java.io.File
 */
public class FilesystemDataSource implements TreeDataSource<Object> {

    private static final Icon FOLDER_OPEN_ICON = KiwiUtils.getResourceManager()
        .getIcon("folder_page.png");

    private static final Icon FOLDER_CLOSED_ICON = KiwiUtils.getResourceManager()
        .getIcon("folder.png");

    private static final Icon FOLDER_LOCKED_ICON = KiwiUtils.getResourceManager()
        .getIcon("folder_locked.png");

    private static final Icon DOCUMENT_ICON = KiwiUtils.getResourceManager()
        .getIcon("document_blank.png");

    private static final Icon COMPUTER_ICON = KiwiUtils.getResourceManager()
        .getIcon("computer.png");

    private static final String[] COLUMNS;

    private static final String[] EMPTY_LIST = new String[0];

    private static final String FILE_COLUMN, SIZE_COLUMN, DATE_COLUMN, TIME_COLUMN;

    private static final Class[] TYPES = new Class[]{
        String.class,
        String.class,
        String.class,
        String.class
    };

    private static final String ALL_FILESYSTEMS;

    private static final int KILOBYTE = 1024;

    static {
        LocaleManager lm = LocaleManager.getDefault();
        LocaleData loc = lm.getLocaleData("KiwiMisc");

        FILE_COLUMN = loc.getMessage("kiwi.column.file");
        SIZE_COLUMN = loc.getMessage("kiwi.column.size");
        DATE_COLUMN = loc.getMessage("kiwi.column.date");
        TIME_COLUMN = loc.getMessage("kiwi.column.time");
        ALL_FILESYSTEMS = loc.getMessage("kiwi.label.all_filesystems");

        COLUMNS = new String[]{FILE_COLUMN, SIZE_COLUMN, DATE_COLUMN, TIME_COLUMN};

    }

    private FileRoot root;

    private final Date date = new Date();

    private boolean ignoreFiles = false;

    private LocaleManager lm;

    /**
     * Construct a new <code>FilesystemDataSource</code> with roots
     * for all available filesystems.
     *
     * @since Kiwi 2.0
     */
    public FilesystemDataSource() {
        init(new FileRoot(), false);
    }

    /**
     * Construct a new <code>FilesystemDataSource</code> wiht the given roots.
     *
     * @param roots The filesystem roots for this datasource.
     * @since Kiwi 2.0
     */
    public FilesystemDataSource(File[] roots) {
        init(new FileRoot(roots), false);
    }

    /**
     * Construct a new <code>FilesystemDataSource</code> with roots
     * for all available filesystems.
     *
     * @param ignoreFiles A flag specifying whether ordinary files
     *                    (non-directories) should be ignored or displayed.
     */
    public FilesystemDataSource(boolean ignoreFiles) {
        init(new FileRoot(), ignoreFiles);
    }

    /**
     * Construct a new <code>FilesystemDataSource</code>.
     *
     * @param root        The root directory.
     * @param ignoreFiles A flag specifying whether ordinary files
     *                    (non-directories) should be ignored or displayed.
     * @throws IllegalArgumentException if <code>root</code> is not
     *                                  a directory.
     */
    public FilesystemDataSource(File root, boolean ignoreFiles) {
        if (root != null && !root.isDirectory()) {
            throw (new IllegalArgumentException("Root must be a directory!"));
        }

        init(new FileRoot(root), ignoreFiles);
    }

    /**
     * Construct a new <code>FilesystemDataSource</code>.
     *
     * @param roots       The root directories.
     * @param ignoreFiles A flag specifying whether ordinary files
     *                    (non-directories) should be ignored or displayed.
     * @throws IllegalArgumentException if <code>root</code> is not
     *                                  a directory.
     * @since Kiwi 2.0
     */
    public FilesystemDataSource(File[] roots, boolean ignoreFiles) {
        for (File file : roots) {
            if (!file.isDirectory()) {
                throw (new IllegalArgumentException("Roots must be directories!"));
            }
        }

        init(new FileRoot(roots), ignoreFiles);
    }

    private void init(FileRoot root, boolean ignoreFiles) {
        this.ignoreFiles = ignoreFiles;
        this.root = root;

        lm = LocaleManager.getDefault();
    }

    /**
     * Get the root object.
     *
     * @return The <code>FileRoot</code> "virtual root" object which is
     * the parent of the root files with which this data source was
     * created.
     */
    public Object getRoot() {
        return (root);
    }

    /**
     * Get the children of a given node.
     */
    public Object[] getChildren(Object node) {

        if (node.getClass() == FileRoot.class) {
            return (((FileRoot) node).getRoots());
        }

        File f = (File) node;
        String[] children = EMPTY_LIST;

        try {
            children = f.list();
        } catch (Exception ignored) {
        }

        if (children != null) {
            Arrays.sort(children);
        }

        return (makeNodes(f, children));
    }

    /**
     * Create an array of Files from an array of filenames
     */
    private File[] makeNodes(File parent, String[] list) {

        File f;
        ArrayList<File> v = new ArrayList<File>();

        if (list != null) {
            for (String name : list) {
                if (parent == null) {
                    f = new File(name);
                } else {
                    f = new File(parent, name);
                }

                if (ignoreFiles && !f.isDirectory()) {
                    continue;
                }
                v.add(f);
            }
        }

        File[] nodes = new File[v.size()];
        return (v.toArray(nodes));
    }

    /*
     */
    public String getLabel(Object node) {

        if (node.getClass() == FileRoot.class) {
            return (ALL_FILESYSTEMS);
        }

        File f = (File) node;
        return root.isRoot(f) ? f.getPath() : f.getName();
    }

    /*
     */

    public Icon getIcon(Object node, boolean isExpanded) {

        Icon ret;

        if (node.getClass() == FileRoot.class) {
            ret = COMPUTER_ICON;
        } else {

            File f = (File) node;

            if (f.isDirectory()) {
                ret = !f.canRead() ? FOLDER_LOCKED_ICON : (isExpanded ? FOLDER_OPEN_ICON : FOLDER_CLOSED_ICON);
            } else {
                ret = DOCUMENT_ICON;
            }
        }

        return ret;
    }

    /*
     */

    public boolean isExpandable(Object node) {
        if (node.getClass() == FileRoot.class) {
            return (true);
        }

        File f = (File) node;

        return (f.isDirectory() && f.canRead());
    }

    /**
     * Get the value for a given property.
     */
    public Object getValueForProperty(Object node, String property) {

        Object ret;

        if (node == null) {

            if (property.equals(ModelProperties.COLUMN_NAMES_PROPERTY)) {
                ret = COLUMNS;
            } else if (property.equals(ModelProperties.COLUMN_TYPES_PROPERTY)) {
                ret = TYPES;
            } else {
                ret = null;
            }

        } else if (!(node instanceof File f)) {
            ret = null;
        } else {

            if (property.equals(FILE_COLUMN)) {
                ret = f;
            } else if (property.equals(SIZE_COLUMN)) {

                long len = f.length();

                len = (len + KILOBYTE - 1) / KILOBYTE;
                ret = len < KILOBYTE
                    ? (lm.formatInteger(len, true) + " Kb")
                    : (lm.formatInteger((len + KILOBYTE - 1) / KILOBYTE, true) + " Mb");

            } else if (property.equals(DATE_COLUMN)) {

                date.setTime(f.lastModified());
                ret = lm.formatDate(date, lm.MEDIUM);

            } else if (property.equals(TIME_COLUMN)) {

                date.setTime(f.lastModified());
                ret = lm.formatTime(date, lm.SHORT);

            } else {

                ret = null;

            }
        }
        return ret;
    }

    /**
     * A virtual filesystem root. This object encapsulates the
     * collection of filesystem roots. (Some platforms, namely Windows, have
     * more than one root directory in the filesystem.)
     */
    public static final class FileRoot {

        private final File[] roots;

        FileRoot() {
            roots = File.listRoots();
        }

        FileRoot(File[] roots) {
            this.roots = roots;
        }

        FileRoot(File root) {
            if (root != null) {
                roots = new File[1];
                roots[0] = root;
            } else {
                roots = File.listRoots();
            }
        }

        boolean isRoot(File file) {
            for (File root1 : roots) {
                if (root1 == file) {
                    return (true);
                }
            }

            return (false);
        }

        /**
         * Get the list of roots.
         */

        File[] getRoots() {
            return (roots);
        }

        /**
         * Get the string representation of this object.
         *
         * @return The empty string.
         */

        public String toString() {
            return ("");
        }
    }

}
