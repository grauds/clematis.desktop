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

package com.hyperrealm.kiwi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Objects;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_ROW_HEIGHT;
import com.hyperrealm.kiwi.ui.model.datasource.FilesystemDataSource;
import com.hyperrealm.kiwi.ui.model.tree.DefaultKTreeModel;
import com.hyperrealm.kiwi.ui.model.tree.ExternalKTreeModel;
import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeAdapter;

/**
 * This class represents a filesystem tree component. It displays hierarchical
 * data (ultimately obtained from a <code>FilesystemDataSource</code>) in a
 * <code>JTree</code> component. The filesystem (or portion thereof) being
 * displayed by the component can be changed at any time.
 *
 * <p><center>
 * <img src="snapshot/FilesystemTreeView.gif"><br>
 * <i>An example FilesystemTreeView.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see FilesystemDataSource
 * @see javax.swing.JTree
 */

public class FilesystemTreeView extends KPanel {

    private final JTree tree;

    private final KTreeModelTreeAdapter adapter;

    private final boolean ignoreFiles;

    /**
     * Construct a new <code>FilesystemTreeView</code>. The tree initially has
     * no data model; use <code>setRoot()</code> to initialize the component.
     *
     * @see #setRoot
     */

    public FilesystemTreeView() {
        this(false);
    }

    /**
     * Construct a new <code>FilesystemTreeView</code>. The tree initially has
     * no data model; use <code>setRoot()</code> to initialize the component.
     *
     * @param ignoreFiles A flag specifying whether this list should ignore
     *                    files and only display directories.
     * @see #setRoot
     */

    public FilesystemTreeView(boolean ignoreFiles) {
        this.ignoreFiles = ignoreFiles;

        setLayout(new BorderLayout(0, 0));

        tree = new JTree();
        tree.setRowHeight(DEFAULT_ROW_HEIGHT);
        tree.setBackground(Color.white);
        adapter = new KTreeModelTreeAdapter(tree);
        tree.setModel(adapter);

        KScrollPane scrollPane = new KScrollPane(tree);
        add(CENTER_POSITION, scrollPane);

        setMultipleSelectionsAllowed(false);
    }

    /**
     * Specify whether multiple selections are allowed in this component.
     *
     * @param flag If <code>true</code>, multiple discontiguous selections will
     *             be allowed; otherwise only single selection is allowed (the default).
     */

    public void setMultipleSelectionsAllowed(boolean flag) {
        tree.getSelectionModel()
            .setSelectionMode(flag ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
                : TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    /**
     * Set the root of the filesystem to be displayed by this component. This
     * causes the component to be reset and repainted.
     *
     * @param root The root directory of the filesystem to display; may be
     *             <code>null</code> to display all available filesystems.
     */

    public void setRoot(File root) {
        FilesystemDataSource fds = new FilesystemDataSource(root, ignoreFiles);
        DefaultKTreeModel model = new ExternalKTreeModel(fds);
        adapter.setTreeModel(model);
        tree.setCellRenderer(new KTreeModelTreeCellRenderer(model));
        tree.setRootVisible(false);
        repaint();
    }

    /**
     * Get the currently selected item in the tree. If there is more than one
     * item selected in the tree, gets the first selected item.
     *
     * @return The <code>File</code> object for the currently selected item in
     * the tree, or <code>null</code> if there is no selection.
     * @see #getSelectedFiles
     */

    public File getSelectedFile() {
        TreePath path = tree.getSelectionPath();
        return ((path == null) ? null : fileForPath(path));
    }

    /**
     * Get the currently selected items in the tree.
     *
     * @return An array of <code>File</code> objects corresponding to the
     * currently selected items in the tree. If there is no selection, an empty
     * array is returned.
     * @see #getSelectedFile
     */

    public File[] getSelectedFiles() {
        TreePath[] paths = tree.getSelectionPaths();
        File[] f = new File[Objects.requireNonNull(paths).length];
        for (int i = 0; i < paths.length; i++) {
            f[i] = fileForPath(paths[i]);
        }

        return (f);
    }

    /**
     * Get the <code>File</code> object for a given path in the tree.
     *
     * @param path The <code>TreePath</code> of the item.
     * @return The <code>File</code> object at the end of the given path.
     */

    private File fileForPath(TreePath path) {
        Object node = path.getLastPathComponent();
        return ((node == null) ? null : (File) node);
    }

    /**
     * Get the <code>JTree</code> that is embedded in this component.
     */

    public final JTree getJTree() {
        return (tree);
    }

    /**
     * Set the component's opacity.
     */

    public void setOpaque(boolean flag) {
        super.setOpaque(flag);
        if (tree != null) {
            tree.setOpaque(flag);
        }
    }

    /**
     * Set the font for this component.
     *
     * @param font The new font.
     */

    public void setFont(Font font) {
        super.setFont(font);
        if (tree != null) {
            tree.setFont(font);
        }
    }

}
