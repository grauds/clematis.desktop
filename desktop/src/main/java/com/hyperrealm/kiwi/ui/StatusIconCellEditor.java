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

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTree;

/**
 * A cell editor that complements
 * {@link com.hyperrealm.kiwi.ui.StatusIconCellRenderer} which allows the
 * selection of a state by displaying the state icons in a JComboBox. The
 * editor assumes that the values being edited are objects of type
 * <code>Integer</code>.
 *
 * @author Mark Lindner
 * @since Kiwi 2.4.1
 */

public class StatusIconCellEditor extends DefaultCellEditor {

    private JComboBox combo;

    /**
     * Construct a new <code>StatusIconCellEditor</code>.
     *
     * @param icons The set of icons that represent the various states. The array
     *              must contain at least one icon.
     * @throws IllegalArgumentException If <code>icons</code> is
     *                                  <code>null</code> or a zero-length array.
     */

    public StatusIconCellEditor(Icon[] icons) {
        super(new JComboBox(icons));

        if (icons.length < 1) {
            throw (new IllegalArgumentException("Cannot be null or an empty array."));
        }

        combo = (JComboBox) getComponent();
    }

    /**
     * Get the value currently in the cell editor.
     *
     * @return The current value, as a <code>Integer</code>.
     */

    public Object getCellEditorValue() {
        return combo.getSelectedIndex();
    }

    /* Prepare the editor for a value. */

    private Component prepareEditor(Object value) {
        if (value.getClass().getSuperclass() == Integer.class) {
            combo.setSelectedIndex((Integer) value);
        }

        return combo;
    }

    /**
     * Get an editor for a JTable.
     */

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        return prepareEditor(value);
    }

    /**
     * Get an editor for a JTree.
     */

    public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                boolean isSelected,
                                                boolean expanded, boolean leaf,
                                                int row) {
        return prepareEditor(value);
    }
}
