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

package com.hyperrealm.kiwi.ui.model;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import com.hyperrealm.kiwi.event.KTreeModelEvent;
import com.hyperrealm.kiwi.event.KTreeModelListener;

/**
 * An adapter that allows a Kiwi <code>KTreeTable</code> to be used
 * with a <code>KTreeModel</code>. The adapter translates messages
 * and events between the <code>KTreeTable</code> and the
 * <code>KTreeModel</code> implementation. This class is for internal
 * use, and does not normally need to be instantiated by application
 * code.
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KTreeTable
 * @since Kiwi 2.0
 */

public class KTreeModelTreeTableAdapter implements TableModel, KTreeModelListener {

    private KTreeModel model = null;

    private final ArrayList<TableModelListener> listeners;

    private JTree jTree;

    private String[] columns;

    /**
     * Construct a new tree model table adapter.
     */

    public KTreeModelTreeTableAdapter(JTree tree) {
        jTree = tree;
        listeners = new ArrayList<>();

        tree.addTreeExpansionListener(new TreeExpansionListener() {

                public void treeExpanded(TreeExpansionEvent event) {
                    fireTableDataChanged();
                }

                public void treeCollapsed(TreeExpansionEvent event) {
                    fireTableDataChanged();
                }

            }
        );
    }

    /**
     * Get the tree model in use by this adapter. Returns the tree model
     * currently associated with this adapter (may be <code>null</code> if no
     * model has been set).
     *
     * @see #setTreeModel
     */

    public KTreeModel getTreeModel() {
        return (model);
    }

    /**
     * Set the tree model to be used by this adapter. The adapter adds itself
     * as a listener of the tree model. If a model was already set prior to this
     * call, it is replaced, and the adapter removes itself as a listener from
     * that model.
     *
     * @param model The model to set.
     * @see #getTreeModel
     */

    public void setTreeModel(KTreeModel model) {
        if (this.model != null) {
            this.model.removeTreeModelListener(this);
        }
        this.model = model;
        init();
    }

    /* initialization code */

    private void init() {
        if (model != null) {
            model.addTreeModelListener(this);

            int cols = model.getFieldCount();
            columns = new String[cols];
            for (int i = 0; i < cols; i++) {
                columns[i] = model.getFieldLabel(i);
            }
        } else {
            columns = null;
        }

        fireTableDataChanged();
    }

    /**
     * Add a table model listener. Adds a <code>TableModelListener</code> to
     * this adapter's list of table model listeners.
     *
     * @param listener The listener to add.
     * @see #removeTableModelListener
     */

    public void addTableModelListener(TableModelListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a table model listener. Removes a <code>TableModelListener</code>
     * from this adapter's list of table model listeners.
     *
     * @param listener The listener to remove.
     * @see #addTableModelListener
     */

    public void removeTableModelListener(TableModelListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Get the column count for this model. (Implementation of
     * <code>TableModel</code>.)
     */

    public int getColumnCount() {
        if (columns != null) {
            return (columns.length);
        } else {
            return (0);
        }
    }

    /**
     * Set the value at the given row and column in the table model.
     * (Implementation of <code>TableModel</code>.) In this implementation, this
     * method does nothing, since <code>TreeTable</code>s are currently not
     * editable.
     */

    public void setValueAt(Object obj, int row, int col) {
        // does nothing
    }

    /**
     * Get the value at the given row and column in the table model.
     * (Implementation of <code>TableModel</code>.)
     *
     * @param row The row.
     * @param col The column.
     */

    public Object getValueAt(int row, int col) {
        if (model == null) {
            return (null);
        }

        Object node = nodeForRow(row);

        return (model.getField(node, col));
    }


    /**
     * Determine if a given cell is editable. In this implementation, the method
     * always returns <code>false</code> because <code>TreeTable</code>s are
     * currently not editable.
     */

    public boolean isCellEditable(int row, int col) {
        return (col == 0);
    }

    /**
     * Get the row count for this table model. (Implementation of
     * <code>TableModel</code>.)
     */

    public int getRowCount() {
        return (jTree.getRowCount());
    }

    /**
     * Get the class type for the given column. In this implementation, this
     * method always returns <code>Object.class</code>. This may change in a
     * future implementation to reflect the actual data type.
     */

    public Class getColumnClass(int col) {
        return (Object.class);
    }

    /**
     * Get the name of a given column. (Implementation of
     * <code>TableModel</code>.)
     *
     * @param col The index of the column.
     * @return The name of the column.
     */

    public String getColumnName(int col) {
        if (columns == null) {
            return (null);
        }

        return (columns[col]);
    }

    /**
     * Notify listeners that the table data has changed.
     */

    protected void fireTableDataChanged() {
        TableModelEvent evt = null;

        synchronized (listeners) {
            for (TableModelListener listener : listeners) {
                if (evt == null) {
                    evt = new TableModelEvent(this);
                }
                listener.tableChanged(evt);
            }
        }
    }

    /**
     * Get the node at the specified row.
     *
     * @param row The row number
     * @return The object at the specified row.
     */

    protected Object nodeForRow(int row) {
        TreePath path = jTree.getPathForRow(row);
        return (path.getLastPathComponent());
    }

    /**
     * Invoked after a node has been added to the model.
     */

    public void nodesAdded(KTreeModelEvent evt) {
        fireTableDataChanged();
    }

    /**
     * Invoked after a node has been removed from the model.
     */

    public void nodesRemoved(KTreeModelEvent evt) {
        fireTableDataChanged();
    }

    /**
     * Invoked when nodes are changed in the model.
     */

    public void nodesChanged(KTreeModelEvent evt) {
        fireTableDataChanged();
    }

    /**
     * Invoked when the structure of a portion of the model is changed.
     */

    public void structureChanged(KTreeModelEvent evt) {
        fireTableDataChanged();
    }

    /**
     * Invoked when the structure of the entire model has changed.
     */

    public void dataChanged(KTreeModelEvent evt) {
        fireTableDataChanged();
    }

    /**
     * Dispose of the adapter. Causes the adapter to detach its listeners from
     * its associated <code>JTree</code> component, and then null out its
     * references to the <code>JTree</code> and to the associated
     * <code>KTreeModel</code>.
     */

    public void dispose() {
        if (model != null) {
            model.removeTreeModelListener(this);
        }

        model = null;
        jTree = null;
    }

}
