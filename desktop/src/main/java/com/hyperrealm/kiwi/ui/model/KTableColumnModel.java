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

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * A specialized table column model which allows columns to be selectively
 * shown or hidden, and which can auto-configure itself from a
 * {@link com.hyperrealm.kiwi.ui.model.DomainObjectFieldAdapter DomainObjectFieldAdapter}.
 * The model maintains two lists of columns. One list contains only the visible
 * columns, and the other contains a complete list of columns, both visible and
 * hidden.
 * <p>
 * This class is typically used in conjunction with the
 * {@link com.hyperrealm.kiwi.ui.TableColumnSelector TableColumnSelector} class to create a
 * configurable {@link javax.swing.JTable JTable}.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1
 */

public class KTableColumnModel extends DefaultTableColumnModel {
    private ArrayList<TableColumn> allColumns;

    /**
     * Construct a new, empty <code>KTableColumnModel</code>.
     */

    public KTableColumnModel() {
        super();

        allColumns = new ArrayList<TableColumn>();
    }

    /**
     * Construct a new <code>KTableColumnModel</code> with the given adapter.
     *
     * @param adapter The field adapter which will provide information about
     *                table columns and their properties.
     */

    public KTableColumnModel(DomainObjectFieldAdapter adapter) {
        this();

        int ct = adapter.getFieldCount();
        for (int i = 0; i < ct; i++) {
            TableColumn tcol = new TableColumn(i, adapter.getFieldPreferredWidth(i),
                adapter.getCellRenderer(i),
                adapter.getCellEditor(i));
            tcol.setHeaderValue(adapter.getFieldName(i));
            tcol.setMaxWidth(adapter.getFieldMaxWidth(i));
            tcol.setMinWidth(adapter.getFieldMinWidth(i));

            addColumn(tcol);
        }
    }

    /**
     * Overridden to add the column from the full column list.
     */

    public void addColumn(TableColumn column) {
        allColumns.add(column);
        super.addColumn(column);
    }

    /**
     * Overridden to remove the column from the full column list.
     */

    public void removeColumn(TableColumn column) {
        allColumns.remove(column);
        super.removeColumn(column);
    }

    /**
     * Set the visibility of a column.
     *
     * @param column  The column.
     * @param visible <b>true</b> if the column should be shown, <b>false</b>
     *                if it should be hidden.
     * @throws IllegalArgumentException If the column is not in this
     *                                  model.
     */

    public void setColumnVisible(TableColumn column, boolean visible) {
        if (!allColumns.contains(column)) {
            throw (new IllegalArgumentException("invalid column"));
        }

        if (visible && !tableColumns.contains(column)) {
            super.addColumn(column);
            int oldIndex = super.getColumnCount() - 1;

            int newIndex = 0;
            int n = allColumns.indexOf(column);
            for (int i = 0; i < n; ++i) {
                if (isColumnVisible(i)) {
                    newIndex++;
                }
            }

            super.moveColumn(oldIndex, newIndex);
        } else if (!visible && tableColumns.contains(column)) {
            super.removeColumn(column);
        }
    }

    /**
     * Set the visibility of a column.
     *
     * @param index   The index of the column.
     * @param visible <b>true</b> if the column should be shown, <b>false</b>
     *                if it should be hidden.
     */

    public void setColumnVisible(int index, boolean visible) {
        setColumnVisible(allColumns.get(index), visible);
    }

    /**
     * Determine if the column at the given index is visible or hidden.
     *
     * @param index The column index.
     * @return <b>true</b> if the column is visible, <b>false</b> otherwise.
     */

    public boolean isColumnVisible(int index) {
        TableColumn tcol = allColumns.get(index);

        return (tableColumns.contains(tcol));
    }

    /**
     * Get the true column count, including both visible and hidden columns.
     */

    public int getRealColumnCount() {
        return (allColumns.size());
    }

    /**
     * Get the table column at the given index in the complete column list,
     * which includes both visible and hidden columns.
     */

    public TableColumn getRealColumn(int index) {
        return (allColumns.get(index));
    }

}
