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

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.ui.model.ExternalKTreeModel;
import com.hyperrealm.kiwi.ui.model.FilesystemDataSource;
import com.hyperrealm.kiwi.ui.model.KTreeModel;

/**
 * This class represents a filesystem table component. It displays
 * hierarchical data (ultimately obtained from a
 * <code>FilesystemDataSource</code>) in a <code>KTreeTable</code> component.
 * The filesystem (or portion thereof) being displayed by the component can
 * be changed at any time.
 *
 * <p><center>
 * <img src="snapshot/FilesystemTableView.gif"><br>
 * <i>An example FilesystemTableView.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.model.FilesystemDataSource
 * @see com.hyperrealm.kiwi.ui.KTreeTable
 */

public class FilesystemTableView extends KPanel {

    private KTreeTable table;

    private boolean ignoreFiles;

    /**
     * Construct a new <code>FilesystemTableView</code>. The table initially has
     * no data model; use <code>setRoot()</code> to initialize the component.
     *
     * @see #setRoot
     */

    public FilesystemTableView() {
        this(false);
    }

    /**
     * Construct a new <code>FilesystemTableView</code>. The view initially has
     * no data model; use <code>setRoot()</code> to initialize one.
     *
     * @param ignoreFiles A flag specifying whether this table should ignore
     *                    files and only display directories.
     * @see #setRoot
     */

    public FilesystemTableView(boolean ignoreFiles) {
        this.ignoreFiles = ignoreFiles;

        table = new KTreeTable();
        table.setRowHeight(ComponentDialog.DEFAULT_ROW_HEIGHT);
        table.setBackground(Color.white);

        KScrollPane scrollPane = new KScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants
            .VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new GridLayout(1, 0));
        add(scrollPane);

        setMultipleSelectionsAllowed(false);
    }

    /**
     * Specify whether multiple selections are allowed in this component.
     *
     * @param flag If <code>true</code>, multiple discontiguous selections will
     *             be allowed; otherwise only single selection is allowed (the default).
     */

    public void setMultipleSelectionsAllowed(boolean flag) {
        table.getSelectionModel()
            .setSelectionMode(flag ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                : ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Set the root of the filesystem to be displayed by this component. This
     * causes the component to be reset and repainted.
     *
     * @param root The root directory of the filesystem to display. May be
     *             <code>null</code>, indicating that all available filesystems should be
     *             displayed.
     */
    @SuppressWarnings("all")
    public void setRoot(File root) {
        FilesystemDataSource fds = new FilesystemDataSource(root, ignoreFiles);
        KTreeModel model = new ExternalKTreeModel(fds);

        table.setTreeModel(model);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        table.configureColumn(0, 100, 100, 1000);
        table.configureColumn(1, 70, 70, 70, rightRenderer, null);
        table.configureColumn(2, 100, 100, 100, rightRenderer, null);
        table.configureColumn(3, 70, 70, 70, rightRenderer, null);

        repaint();
    }

    /**
     * Get the currently selected item in the table. If there is more than one
     * item selected in the table, gets the last or most recently selected item.
     *
     * @return The <code>File</code> object for the currently selected item in
     * the table, or <code>null</code> if there is no selection.
     * @see #getSelectedFiles
     */

    public File getSelectedFile() {
        return ((File) table.getSelectedItem());
    }

    /**
     * Get the currently selected items in the table.
     *
     * @return An array of <code>File</code> objects corresponding to the
     * currently selected items in the table. If there is no selection, an empty
     * array is returned.
     * @see #getSelectedFile
     */

    public File[] getSelectedFiles() {
        return ((File[]) table.getSelectedItems());
    }

    /**
     * Get the <code>KTreeTable</code> component for this component.
     */

    public KTreeTable getTable() {
        return (table);
    }

}
