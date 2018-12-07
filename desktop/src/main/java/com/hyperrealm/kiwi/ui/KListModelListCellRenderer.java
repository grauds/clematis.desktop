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

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.hyperrealm.kiwi.ui.model.KListModel;

/**
 * An implementation of <code>ListCellRenderer</code> for use with
 * <code>JList</code>s that are connected to a <code>KListModel</code> via
 * a <code>KListModelListAdapter</code>. This cell renderer consults the list
 * model for a cell's rendering information, such as its label and icon.
 *
 * @param <T>
 * @author Mark Lindner
 * @see javax.swing.JList
 * @see com.hyperrealm.kiwi.ui.model.KListModel
 * @see com.hyperrealm.kiwi.ui.model.KListModelAdapter
 */

public class KListModelListCellRenderer<T> extends AbstractCellRenderer<T> {

    private KListModel<T> model = null;

    private JLabel label;

    /**
     * Construct a new <code>ModelListCellRenderer</code>.
     */

    public KListModelListCellRenderer() {
        label = new JLabel();
    }


    /**
     * Construct a new <code>ModelListCellRenderer</code>.
     *
     * @param model The list model that will be used with this renderer.
     */

    public KListModelListCellRenderer(KListModel<T> model) {
        this();
        setModel(model);
    }

    @Override
    protected JComponent getCellRenderer(JComponent component, T value, int row, int column) {

        if ((model == null) || (value == null)) {
            label.setIcon(null);
            label.setText((value == null) ? null : value.toString());
        } else {
            label.setIcon(model.getIcon(value));
            label.setText(model.getLabel(value));
        }

        return (label);
    }

    /**
     * Set the data model for this renderer.
     *
     * @param model The model.
     */

    public void setModel(KListModel<T> model) {
        this.model = model;
    }
}
