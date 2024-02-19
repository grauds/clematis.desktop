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

package com.hyperrealm.kiwi.ui.model.datasource;

import javax.swing.Icon;

import com.hyperrealm.kiwi.ui.model.ModelProperties;

/**
 * An interface that defines a data source for <code>KListModel</code>s.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface ListDataSource<T> extends ModelProperties {
    /**
     * Get all of the items from the data source.
     *
     * @return An array of objects.
     */

    T[] getItems();

    /**
     * Get the value of an arbitrary property for a given item.
     *
     * @param item     The item.
     * @param property The name of the property.
     * @return The value of the specified property, or <code>null</code> if
     * there is no value for this property.
     */

    Object getValueForProperty(T item, String property);

    /**
     * Get the label for an item.
     *
     * @param item The item.
     * @return A label for the item.
     */

    String getLabel(T item);

    /**
     * Get the ICON for an item.
     *
     * @param item The item.
     * @return An ICON for the item.
     */

    Icon getIcon(T item);

}
