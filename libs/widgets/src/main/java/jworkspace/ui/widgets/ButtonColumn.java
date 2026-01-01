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


public class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor {

    private static final String UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED = "UniversalButtonColumnHoverInstalled";
    private final JButton renderButton;
    private final JButton editButton;
    private JTable table;
    private int row = -1;
    private int column = -1;
    private final BiConsumer<Integer, Integer> action; // row, column

    public ButtonColumn(String text, BiConsumer<Integer, Integer> action) {
        this.action = Objects.requireNonNull(action);

        renderButton = new JButton(text);
        renderButton.setOpaque(true);

        editButton = new JButton(text);
        editButton.setOpaque(true);
        editButton.addActionListener(this::onClick);
    }

    private void onClick(ActionEvent e) {
        fireEditingStopped(); // stop editing so table redraws
        if (table != null && row >= 0 && column >= 0) {
            action.accept(row, column);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column
    ) {
        setupButtonForCell(table, renderButton, row, column, isSelected);
        installHoverCursor(table, column);

        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column
    ) {
        this.table = table;
        this.row = row;
        this.column = column;

        setupButtonForCell(table, editButton, row, column, true);
        installHoverCursor(table, column);

        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    private void setupButtonForCell(JTable table, JButton button, int row, int column, boolean selected) {
        boolean enabled = table.isCellEditable(row, column);
        button.setEnabled(enabled);
        if (enabled) {
            button.setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            button.setBackground(table.getBackground());
            button.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void installHoverCursor(JTable table, int buttonColumn) {
        if (table.getClientProperty(UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED) != null) {
            return;
        }
        table.putClientProperty(UNIVERSAL_BUTTON_COLUMN_HOVER_INSTALLED, true);
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int rowAtPoint = table.rowAtPoint(e.getPoint());
                int columnAtPoint = table.columnAtPoint(e.getPoint());

                if (rowAtPoint >= 0 && columnAtPoint >= 0) {
                    int modelCol = table.convertColumnIndexToModel(columnAtPoint);
                    boolean enabled = table.isCellEditable(rowAtPoint, modelCol);
                    if (columnAtPoint == buttonColumn && enabled) {
                        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
                table.setCursor(Cursor.getDefaultCursor());
            }
        });
    }
}
