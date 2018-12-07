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
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.hyperrealm.kiwi.ui.model.TableSorter;
import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * An extension of <code>JTable</code> that fixes a number of blatant bugs
 * and limitations in that component.
 *
 * <p><center>
 * <img src="snapshot/KTable.gif"><br>
 * <i>An example sortable KTable.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class KTable extends JTable {

    private TableSorter sorter;

    private boolean sortable = false, editable = true;

    private TableModel realModel = null;

    private Icon iSort, iRsort;

    private boolean internalSelectionChange = false;

    /**
     * Construct a new <code>KTable</code>.
     */

    public KTable() {
        iSort = KiwiUtils.getResourceManager().getIcon("sort_down.png");
        iRsort = KiwiUtils.getResourceManager().getIcon("sort_up.png");

        setAutoCreateColumnsFromModel(false);
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setShowGrid(false);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setSelectionModel(new SelectionModel());
        setTableHeader(new TableHeader(getColumnModel()));
    }

    /**
     * This method is overridden to intercept
     * <code>TableModelEvents</code>.  These events cause the selection
     * to be cleared as a side-effect; this situation is sometimes of
     * interest to application code, and may be tested via a call to
     * <code>isSelectionClearing()</code>.
     */

    public void tableChanged(TableModelEvent event) {
        internalSelectionChange = true;
        super.tableChanged(event);
        internalSelectionChange = false;
    }

    /**
     * Determine if the selection on this table is clearing as a result of a
     * <code>TableModelEvent</code>.
     *
     * @since Kiwi 2.0
     */

    public boolean isSelectionClearing() {
        return (internalSelectionChange);
    }

    /**
     *
     */

    public void createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel();

        prepareHeader();
    }

    /**
     * Get the row translation for a given row in the table. If sorting is
     * turned on, the visual row may not be the same as the row in the underlying
     * data model; this method obtains the model row corresponding to the
     * specified visual row, whether sorting is turned on or not.
     *
     * @param row The visual row.
     * @return The corresponding row in the data model.
     */

    public int getRowTranslation(int row) {
        return (sortable ? sorter.getRowTranslation(row) : row);
    }

    /**
     * Set the data model for this table.
     *
     * @param model The new model.
     */

    public void setModel(TableModel model) {
        realModel = model;
        if (sortable) {
            sorter = new TableSorter(realModel);
            super.setModel(sorter);
            sorter.registerTableHeaderListener(this);
        } else {
            super.setModel(realModel);
        }
    }

    /**
     * Overridden to customize table cell header renderers.
     */

    public void setColumnModel(TableColumnModel cmodel) {
        super.setColumnModel(cmodel);
        prepareHeader();
    }

    /* Prepare the header to use the custom header renderer. */

    private void prepareHeader() {
        TableColumnModel cmodel = getColumnModel();
        HeaderRenderer headerRenderer = new HeaderRenderer();

        int cols = cmodel.getColumnCount();

        for (int i = 0; i < cols; i++) {
            TableColumn tc = cmodel.getColumn(i);
            tc.setHeaderRenderer(headerRenderer);
        }
    }

    /**
     * Enable or disable this table. A disabled table cannot be edited or
     * sorted, nor can the selection be changed, nor the columns reordered or
     * resized.
     *
     * @param enabled A flag specifying whether this table should be enabled
     *                or disabled.
     */

    public void setEnabled(boolean enabled) {
        Color fg = (enabled
            ? UIManager.getColor("Table.selectionForeground")
            : UIManager.getColor("Label.disabledForeground"));

        setForeground(fg);
        setSelectionForeground(fg);
        super.setEnabled(enabled);
        getTableHeader().setEnabled(enabled);

    }

    /**
     * Determine if the table is editable.
     *
     * @return <code>true</code> if the table is editable and <code>false</code>
     * otherwise.
     */

    public boolean isEditable() {
        return (editable);
    }

    /**
     * Set the editable state of this table.
     *
     * @param editable A flag specifying whether this table is editable.
     */

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Determine if the table is sortable.
     *
     * @return <code>true</code> if the table is sortable and <code>false</code>
     * otherwise.
     */

    public boolean isSortable() {
        return (sortable);
    }

    /**
     * Set the sortable state of this table.
     *
     * @param sortable A flag specifying whether this table is sortable.
     */

    public void setSortable(boolean sortable) {
        if (this.sortable == sortable) {
            return;
        }

        this.sortable = sortable;

        if (sortable) {
            if (realModel != null) {
                sorter = new TableSorter(realModel);
                super.setModel(sorter);
                sorter.registerTableHeaderListener(this);
            }
        } else {
            if (realModel != null) {
                super.setModel(realModel);
            }
            sorter = null;
        }

        prepareHeader();
    }

    /**
     * Determine if a cell is editable. Editability of a given cell is
     * ultimately determined by the table model, unless the table has been
     * made non-editable via a call to <code>setEditable()</code>, or if it has
     * been disabled via a call to <code>setEnabled()</code>.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return <code>true</code> if the cell at the specified coordinates is
     * editable, or <code>false</code> if it is not editable or if the table has
     * been made non-editable.
     * @see #setEditable
     */

    public boolean isCellEditable(int row, int col) {
        if (!isEnabled()) {
            return (false);
        }

        return (editable && getModel().isCellEditable(row, col));
    }

    /**
     * Scroll the table to ensure that a given row is visible.
     *
     * @param row The row that must be visible.
     */

    public void ensureRowIsVisible(int row) {
        Rectangle r = getCellRect(row, 0, false);

        int vh = getPreferredScrollableViewportSize().height;

        r.y += (vh / 2);
        scrollRectToVisible(r);

        r.y -= (vh - rowHeight);
        scrollRectToVisible(r);
    }

    /**
     * Stop any cell edit that is in progress on the table.
     */

    public void stopEditing() {
        int row = getEditingRow();
        int col = getEditingColumn();

        getCellEditor(row, col).stopCellEditing();
    }

    /**
     * Sort the table by the specified column. The method has no effect if the
     * table is not sortable.
     *
     * @since Kiwi 2.0
     */

    public void sortByColumn(int col) {
        if (sorter != null) {
            sorter.sortByColumn(col);
        }
    }

    /* A custom list selection model that honors disabled state by disallowing
     * changes to the current selection(s).
     */

    /**
     * Configure a column in the table with width attributes, a cell renderer,
     * and a cell editor. Columns should be configured <i>after</i> a model has
     * been set for the table.
     *
     * @param column   The column to configure.
     * @param width    The preferred width for the column.
     * @param minWidth The minimum width for the column.
     * @param maxWidth The maximum width for the column.
     * @param renderer The cell renderer for the column.
     * @param editor   The cell editor for the column.
     */

    public void configureColumn(int column, int width, int minWidth,
                                int maxWidth, TableCellRenderer renderer,
                                TableCellEditor editor) {
        configureColumn(column, width, minWidth, maxWidth);
        configureColumn(column, renderer, editor);
    }

    /* A custom header renderer that displays a sort icon in each column if the
     * table is sortable.
     */

    /**
     * Configure a column in the table with a cell renderer and cell
     * editor. Columns should be configured <i>after</i> a model has
     * been set for the table.
     *
     * @param column   The column to configure.
     * @param renderer The cell renderer for the column.
     * @param editor   The cell editor for the column.
     * @since Kiwi 2.0
     */

    public void configureColumn(int column, TableCellRenderer renderer,
                                TableCellEditor editor) {
        TableColumnModel cmodel = getColumnModel();
        TableColumn tc = cmodel.getColumn(column);
        if (tc != null) {
            tc.setCellRenderer(renderer);
            tc.setCellEditor(editor);
        }
    }

    /* A custom table header that honors disabled state by disallowing column
     * resizing and reordering.
     */

    /**
     * Configure a column in the table with width attributes. Columns
     * should be configured <i>after</i> a model has been set for the
     * table.
     *
     * @param column   The column to configure.
     * @param width    The preferred width for the column.
     * @param minWidth The minimum width for the column.
     * @param maxWidth The maximum width for the column.
     * @since Kiwi 2.0
     */

    public void configureColumn(int column, int width, int minWidth,
                                int maxWidth) {
        TableColumnModel cmodel = getColumnModel();
        TableColumn tc = cmodel.getColumn(column);
        if (tc != null) {
            tc.setPreferredWidth(width);
            tc.setMinWidth(minWidth);
            tc.setMaxWidth(maxWidth);
        }
    }

    private class SelectionModel extends DefaultListSelectionModel {
        public void clearSelection() {
            if (isEnabled()) {
                super.clearSelection();
            }
        }

        public void addSelectionInterval(int a, int b) {
            if (isEnabled()) {
                super.addSelectionInterval(a, b);
            }
        }

        public void insertIndexInterval(int a, int b, boolean before) {
            if (isEnabled()) {
                super.insertIndexInterval(a, b, before);
            }
        }

        public void removeIndexInterval(int a, int b) {
            if (isEnabled()) {
                super.removeIndexInterval(a, b);
            }
        }

        public void setSelectionInterval(int a, int b) {
            if (isEnabled()) {
                super.setSelectionInterval(a, b);
            }
        }

        public void setAnchorSelectionIndex(int a) {
            if (isEnabled()) {
                super.setAnchorSelectionIndex(a);
            }
        }

        public void setLeadSelectionIndex(int a) {
            if (isEnabled()) {
                super.setLeadSelectionIndex(a);
            }
        }
    }

    private class HeaderRenderer extends HeaderCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            if (isSortable()) {
                int sortCol = sorter.getSortedVisibleColumn();

                if (sortCol < 0) {
                    setIcon(null);
                } else {
                    if (sortCol != column) {
                        setIcon(null);
                    } else {
                        setIcon(sorter.isSortedAscending() ? iSort : iRsort);
                    }
                }
            } else {
                setIcon(null);
            }

            return (super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column));
        }
    }

    private class TableHeader extends JTableHeader {

        TableHeader(TableColumnModel model) {
            super(model);
        }

        public boolean getResizingAllowed() {
            return (isEnabled() && super.getResizingAllowed());
        }

        public boolean getReorderingAllowed() {
            return (isEnabled() && super.getReorderingAllowed());
        }
    }

}
