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

import java.util.Iterator;

import javax.swing.Icon;

import com.hyperrealm.kiwi.event.KListModelListener;
import com.hyperrealm.kiwi.util.MutatorException;

/**
 * This interface defines the behavior for a data model for list data
 * structures.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface KListModel<T> extends Iterable<T> {
    /**
     * Determine if the model is empty.
     *
     * @return <code>true</code> if the model is empty, and <code>false</code>
     * otherwise.
     */

    boolean isEmpty();

    /**
     * Remove all items from the model.
     */

    void clear();

    /**
     * Get the number of items in the model.
     *
     * @return The number of items.
     */

    int getItemCount();

    /**
     * Return an iterator to the items in the model.
     *
     * @return An iterator to the items.
     */

    Iterator<T> iterator();

    /**
     * Get the item at the specified index in the model.
     *
     * @param index The index.
     * @return The item at the specified index.
     */

    T getItemAt(int index);

    /**
     * Get the index of the specified item in the model.
     *
     * @param item The item.
     * @return The index of the item, or <code>-1</code> if the item is not in
     * the model.
     */

    int indexOf(T item);

    /**
     * Add an item to the model. The item is added as the last item in the
     * model.
     *
     * @param item The new item.
     */

    void addItem(T item);

    /**
     * Insert an item at the specified index in the model.
     *
     * @param item  The new item.
     * @param index The index.
     */

    void insertItemAt(T item, int index);

    /**
     * Remove the item at the specified index from the model.
     *
     * @param index The index.
     */

    void removeItemAt(int index);

    /**
     * Remove the specified item from the model.
     *
     * @param item The item.
     */

    void removeItem(T item);

    /**
     * Indicate to listeners that the specified item has changed.
     *
     * @param item The item.
     */

    void updateItem(T item);

    /**
     * Indicate to listeners that the specified item field has changed.
     *
     * @param item  The item.
     * @param field The field number that has changed.
     * @since Kiwi 2.4.1
     */

    void updateItem(T item, int field);

    /**
     * Indicate to listeners that the item at the specified index has changed.
     *
     * @param index The index.
     */

    void updateItemAt(int index);

    /**
     * Indicate to listeners that the item at the specified index has changed.
     *
     * @param index The index.
     * @param field The field number that has changed.
     * @since Kiwi 2.4.1
     */

    void updateItemAt(int index, int field);

    /**
     * Add a <code>ListModelListener</code> to this model's list of listeners.
     *
     * @param listener The listener to add.
     */

    void addListModelListener(KListModelListener listener);

    /**
     * Remove a <code>KListModelListener</code> from this model's list of
     * listeners.
     *
     * @param listener The listener to remove.
     */

    void removeListModelListener(KListModelListener listener);

    /**
     * Get the label for an item.
     *
     * @param item The item.
     * @return A string label for the item.
     */

    String getLabel(T item);

    /**
     * Get the icon for an item.
     *
     * @param item The item.
     * @return An icon for the item.
     */

    Icon getIcon(T item);

    /**
     * Get the field count for this model.
     *
     * @return The field count.
     * @since Kiwi 2.3
     */

    int getFieldCount();

    /**
     * Get the label for the given field.
     *
     * @param field The field index.
     * @return The label for the field.
     * @since Kiwi 2.3
     */

    String getFieldLabel(int field);

    /**
     * Get the type of the given field.
     *
     * @param field The field index.
     * @return The type of the field.
     * @since Kiwi 2.3
     */

    Class getFieldType(int field);

    /**
     * Get the value for a field in the given item.
     *
     * @param item  The item.
     * @param field The field index.
     * @return The value of the field.
     * @since Kiwi 2.3
     */

    Object getField(T item, int field);

    /**
     * Set the value of a field in the given item.
     *
     * @param item  The item.
     * @param field The field index.
     * @param value The new value for the field.
     * @throws MutatorException If an error occurs.
     * @since Kiwi 2.3
     */

    void setField(T item, int field, Object value)
        throws MutatorException;

    /**
     * Determine if a field is mutable in the given item.
     *
     * @param item  The item.
     * @param field The field index.
     * @return <b>true</b> if the field is mutable, <b>false</b> otherwise.
     * @since Kiwi 2.3
     */

    boolean isFieldMutable(T item, int field);

}
