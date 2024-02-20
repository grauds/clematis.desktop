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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_ROW_HEIGHT;

import com.hyperrealm.kiwi.ui.model.tree.KTreeModel;
import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeAdapter;
import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeTableAdapter;

/* This class incorporates ideas from Sun's TreeTable example code, as
 * well as from the Texas Instruments TreeTable implementation, which is
 * also built around the Sun example.
 */

/**
 * A combination tree/table component. This class is an extension of
 * <code>KTable</code> in which the first column presents information
 * in a hierarchical form in the same manner as a <code>JTree</code>.
 * <p>
 * See {@link com.hyperrealm.kiwi.ui.FilesystemTableView com.hyperrealm.kiwi.ui.FilesystemTableView}
 * for an example usage of this component.
 * <p>
 * <p><center>
 * <img src="snapshot/FilesystemTableView.gif"><br>
 * <i>An example KTreeTable.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KTreeTable extends KTable {

    private TreeTableCellRenderer treeRenderer;

    private boolean rootVisible = false;

    /**
     * Construct a new <code>KTreeTable</code>.
     */

    public KTreeTable() {
        super();

        setRowHeight(DEFAULT_ROW_HEIGHT);
        getTableHeader().setReorderingAllowed(false);
        setAutoCreateColumnsFromModel(true);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        sizeColumnsToFit(AUTO_RESIZE_ALL_COLUMNS);

    }

    /**
     * Overridden to prevent callers from enabling sortable mode.
     */

    public final void setSortable(boolean flag) {
        // ignore
    }

    /**
     * Overridden to prevent callers from enabling editable mode.
     */

    public final void setEditable(boolean editable) {
        // ignore
    }

    /**
     * Set the tree model for this component.
     *
     * @param model The new tree model.
     */

    public final void setTreeModel(KTreeModel model) {
        // create the renderer and editor, and set the models

        treeRenderer = new TreeTableCellRenderer(model);
        treeRenderer.setRowHeight(getRowHeight());
        KTreeModelTreeAdapter treeModel
            = new KTreeModelTreeAdapter(treeRenderer);
        treeRenderer.setActualModel(treeModel);
        treeModel.setTreeModel(model);
        treeRenderer.setRootVisible(rootVisible);

        KTreeModelTreeTableAdapter tableModel = new KTreeModelTreeTableAdapter(treeRenderer);
        tableModel.setTreeModel(model);
        super.setModel(tableModel);

        // Force the JTable and JTree to share their row selection models.

        TreeTableSelectionModel treeTableSelModel = new TreeTableSelectionModel();
        treeRenderer.setSelectionModel(treeTableSelModel);
        setSelectionModel(treeTableSelModel.getListSelectionModel());

        // Make the tree and table row heights the same.

        treeRenderer.setRowHeight(getRowHeight());

        // Install the tree editor renderer and editor.

        TreeTableCellEditor treeEditor = new TreeTableCellEditor();

        TableColumnModel cmodel = getColumnModel();
        TableColumn tc = cmodel.getColumn(0);
        if (tc != null) {
            tc.setCellRenderer(treeRenderer);
            tc.setCellEditor(treeEditor);
        }

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
    }

    /**
     * Get the tree path for the currently selected node, or the first selected
     * node if more than one is selected.
     *
     * @return The tree path to the selected node.
     */

    public final TreePath getSelectionPath() {
        if (treeRenderer == null) {
            return (null);
        } else {
            return (treeRenderer.getSelectionPath());
        }
    }

    /**
     * Set the selection to the specified tree path.
     *
     * @param path The tree path to the node to select.
     */

    public final void setSelectionPath(TreePath path) {
        if (treeRenderer != null) {
            treeRenderer.setSelectionPath(path);
        }
    }

    /**
     * Get the tree paths for the currently selected nodes.
     *
     * @return An array of tree paths to the selected nodes.
     */

    public final TreePath[] getSelectionPaths() {
        if (treeRenderer == null) {
            return (null);
        } else {
            return (treeRenderer.getSelectionPaths());
        }
    }

    /**
     * Set the selection to the specified tree paths.
     *
     * @param paths The tree paths to the nodes to select.
     */

    public final void setSelectionPaths(TreePath[] paths) {
        if (treeRenderer != null) {
            treeRenderer.setSelectionPaths(paths);
        }
    }

    /**
     * Determine if the node at the given tree path is currently selected.
     *
     * @param path The path to the node.
     * @return <code>true</code> if the node is selected, <code>false</code>
     * otherwise.
     */

    public final boolean isPathSelected(TreePath path) {
        if (treeRenderer == null) {
            return (false);
        } else {
            return (treeRenderer.isPathSelected(path));
        }
    }

    /**
     * Overridden to ensure that tree and table model row heights are the same.
     */

    public final void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (treeRenderer != null) {
            treeRenderer.setRowHeight(rowHeight);
        }
    }

    /*
     */

    public final boolean isCellEditable(int row, int column) {
        return (column == 0);
    }

    /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to
     * paint the editor. The UI currently uses different techniques to
     * paint the renderers and editors and overriding setBounds() below
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     */

    /**
     * Get the row number that is currently being edited.
     *
     * @return The row that is being edited, or -1 if no edit is in progress.
     */

    public final int getEditingRow() {
        return ((editingColumn == 0) ? -1 : editingRow);
    }

    /**
     * Get the selected item.
     *
     * @return The item that is currently selected in the tree, or the first
     * selected item if more than one node is selected.
     */

    public final Object getSelectedItem() {
        int row = getSelectedRow();

        if (row < 0) {
            return (null);
        }

        return (getValueAt(row, 0));
    }

    /**
     * Get the selected items.
     *
     * @return An array of items that are currently selected in the tree.
     */

    public final Object[] getSelectedItems() {
        int[] rows = getSelectedRows();

        Object[] items = new Object[rows.length];
        for (int i = 0; i < rows.length; i++) {
            items[i] = getValueAt(rows[i], 0);
        }

        return (items);
    }

    /**
     * Specify whether the root node should be visible or not.
     *
     * @param flag <code>true</code> if the root node should be visible in the
     *             tree table, <code>false</code> if not.
     */

    public final void setRootVisible(boolean flag) {
        this.rootVisible = flag;

        if (treeRenderer != null) {
            treeRenderer.setRootVisible(flag);
        }
    }

    /**
     * Get the tree path for the node at the given row.
     *
     * @param row The visible row in the tree table.
     * @return A tree path to the node at that row.
     */

    public final TreePath getPathForRow(int row) {
        return (treeRenderer.getPathForRow(row));
    }

    /*
     */

    /**
     * Expand the specified row in the tree.
     *
     * @param row The row number.
     */

    public void expandRow(int row) {
        treeRenderer.expandRow(row);
    }

    /*
     */

    private class TreeTableCellRenderer extends JTree
        implements TableCellRenderer {
        protected int visibleRow;
        private KTreeModelTreeCellRenderer renderer;

        TreeTableCellRenderer(KTreeModel model) {
            renderer = new KTreeModelTreeCellRenderer();
            renderer.setHighlightBackground(getSelectionBackground());
            renderer.setHighlightForeground(getSelectionForeground());
            renderer.setModel(model);
            setRootVisible(false);
        }

        public void setActualModel(TreeModel model) {
            super.setModel(model);
            setCellRenderer(renderer);
        }

        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, KTreeTable.this.getHeight());
        }

        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            visibleRow = row;
            return (this);
        }
    }

    /*
     */

    private class TreeTableCellEditor extends AbstractCellEditor
        implements TableCellEditor {
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int r,
                                                     int c) {
            return (treeRenderer);
        }

        // Somewhat hackish. In order to get the tree nodes to expand/collapse
        // in response to mouse clicks in the cells, we need to install the JTree
        // as a cell editor. But once any node is expanded, the table goes into
        // "editing" mode, and highlighting stops working. Therefore we have to
        // pre-empt that by returning false here, so that editing doesn't actually
        // start...but we still need to forward the event to the tree so that it
        // can expand/collapse as necessary.

        public boolean isCellEditable(EventObject evt) {
            if (evt instanceof MouseEvent) {
                MouseEvent mevt = (MouseEvent) evt;

                int col = 0;

                MouseEvent newEvt = new MouseEvent(treeRenderer, mevt.getID(),
                    mevt.getWhen(), mevt.getModifiers(),
                    mevt.getX() - getCellRect(0, col, true).x,
                    mevt.getY(), mevt.getClickCount(),
                    mevt.isPopupTrigger());

                treeRenderer.dispatchEvent(newEvt);
            }

            return (false);
        }

        public Object getCellEditorValue() {
            return (null);
        }
    }

    private class TreeTableSelectionModel extends DefaultTreeSelectionModel
        implements ListSelectionListener {
        private boolean updating = false;

        TreeTableSelectionModel() {
            getListSelectionModel().addListSelectionListener(this);
        }

        ListSelectionModel getListSelectionModel() {
            return (listSelectionModel);
        }

        public void resetRowSelection() {
            if (updating) {
                return;
            }

            updating = true;
            super.resetRowSelection();
            updating = false;
        }

        public void valueChanged(ListSelectionEvent evt) {
            if (updating) {
                return;
            }

            updating = true;

            int minRow = evt.getFirstIndex();
            int maxRow = evt.getLastIndex();

            for (int i = minRow; i <= maxRow; i++) {
                TreePath treePath = treeRenderer.getPathForRow(i);

                if (listSelectionModel.isSelectedIndex(i)) {
                    treeRenderer.addSelectionPath(treePath);
                } else {
                    treeRenderer.removeSelectionPath(treePath);
                }
            }

            updating = false;
        }
    }

}
