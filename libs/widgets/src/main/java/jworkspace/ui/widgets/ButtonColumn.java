package jworkspace.ui.widgets;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A universal JTable button column that renders a button in a specific column,
 * handles clicks, hover cursor, and disabled state automatically.
 * <p>
 * Features:
 * <ul>
 *   <li>Hover cursor shows HAND over enabled buttons, default otherwise</li>
 *   <li>Buttons respect JTable's isCellEditable() method for enabling/disabling</li>
 *   <li>Clicking the button fires a BiConsumer&lt;row, column&gt;</li>
 *   <li>Fully reusable for any JTable column</li>
 *   <li>Hover listener installed once per table</li>
 * </ul>
 */
public class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor {

    /** Client property key to mark hover listener installation */
    private static final String UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED =
        "UniversalButtonColumnHoverInstalled";

    /** Button used for rendering (non-editing state) */
    protected final JButton renderButton;

    /** Button used for editing (active click state) */
    protected final JButton editButton;

    /** JTable this editor is attached to */
    private JTable table;

    /** Row currently being edited (-1 if none) */
    private int row = -1;

    /** Column currently being edited (-1 if none) */
    private int column = -1;

    /** Action fired on button click: accepts row and column */
    private final BiConsumer<Integer, Integer> action;

    /**
     * Creates a ButtonColumn for a specific text and action.
     *
     * @param text   the button text
     * @param action action to fire when button is clicked (row, column)
     */
    public ButtonColumn(String text, BiConsumer<Integer, Integer> action) {
        this.action = Objects.requireNonNull(action);

        // Renderer button: just for painting
        renderButton = new JButton(text);
        renderButton.setOpaque(true);

        // Editor button: interactive, triggers clicks
        editButton = new JButton(text);
        editButton.setOpaque(true);
        editButton.addActionListener(this::onClick);
    }

    /**
     * Handles button clicks in the editor.
     * Stops editing to allow JTable to repaint, then calls the action.
     */
    private void onClick(ActionEvent e) {
        fireEditingStopped(); // Stop editor to redraw table

        // Only fire action if table and valid row/column are set
        if (table != null && row >= 0 && column >= 0) {
            action.accept(row, column);
        }
    }

    // ---------------- TableCellRenderer ----------------

    /**
     * Returns the component used to render the cell.
     * Sets button enabled state and background, and installs hover cursor.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setupButtonForCell(table, renderButton, row, column, isSelected);
        installHoverCursor(table, column);
        return renderButton;
    }

    // ---------------- TableCellEditor ----------------

    /**
     * Returns the component used for editing the cell.
     * Sets up the button and keeps track of row/column being edited.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.column = column;

        setupButtonForCell(table, editButton, row, column, true);
        installHoverCursor(table, column);
        return editButton;
    }

    /**
     * Returns the editor value. Not used, returns null.
     */
    @Override
    public Object getCellEditorValue() {
        return null;
    }

    // ---------------- Internal helpers ----------------

    /**
     * Configures the button appearance for a specific cell.
     * Sets enabled state, background, and cursor.
     *
     * @param table    the JTable
     * @param button   the JButton to configure
     * @param row      cell row
     * @param column   cell column
     * @param selected whether the cell is selected
     */
    protected void setupButtonForCell(JTable table, JButton button,
                                    int row, int column, boolean selected) {
        boolean enabled = table.isCellEditable(row, column); // button enabled if cell is editable
        button.setEnabled(enabled);

        if (enabled) {
            // Highlight selected row with selection background
            button.setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            button.setBackground(table.getBackground());
            button.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Installs a mouse motion listener to change cursor to hand when hovering
     * over enabled buttons in the given column.
     * <p>
     * This is called once per table; repeated calls are ignored.
     *
     * @param table        the JTable
     * @param buttonColumn the column index of the button column (view index)
     */
    protected void installHoverCursor(JTable table, int buttonColumn) {
        // Already installed? skip
        if (table.getClientProperty(UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED) != null) {
            return;
        }
        table.putClientProperty(UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED, true);

        // Listen for mouse movement to set cursor
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int rowAtPoint = table.rowAtPoint(e.getPoint());
                int columnAtPoint = table.columnAtPoint(e.getPoint());

                if (rowAtPoint >= 0 && columnAtPoint >= 0) {
                    int modelCol = table.convertColumnIndexToModel(columnAtPoint);
                    boolean enabled = table.isCellEditable(rowAtPoint, modelCol);

                    if (columnAtPoint == buttonColumn && enabled) {
                        // Over enabled button → hand cursor
                        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
                // Default cursor otherwise
                table.setCursor(Cursor.getDefaultCursor());
            }
        });
    }
}
