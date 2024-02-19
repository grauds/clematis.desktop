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

package com.hyperrealm.kiwi.ui.model.list;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * A model adapter that allows a <code>KListModel</code> to be used with a
 * Swing <code>JComboBox</code> component. This adapter wraps a
 * <code>KListModel</code> implementation and exposes a
 * <code>ComboBoxModel</code> interface, and translates the corresponding
 * model events.
 *
 * @param <E>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KListModelComboBoxAdapter<E> extends KListModelListAdapter<E> implements ComboBoxModel<E> {

    private Object selectedItem = null;

    /**
     * Construct a new <code>KListModelComboBoxAdapter</code> for the given
     * <code>JComboBox</code>.
     *
     * @param jcombobox The <code>JComboBox</code> that will be used with this
     *                  adapter.
     */

    public KListModelComboBoxAdapter(JComboBox<E> jcombobox) {
        super();
        jcombobox.setRenderer(renderer);
    }

    /* implementation of ComboBoxModel */

    /*
     */

    public Object getSelectedItem() {
        return (selectedItem);
    }

    /*
     */

    public void setSelectedItem(Object item) {
        selectedItem = item;
    }

}
