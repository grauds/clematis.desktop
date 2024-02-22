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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ListModel;

/**
 * An implementation of <code>ListModel</code> that keeps its
 * elements ordered, and rejects duplicate elements.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class OrderedListModel<T extends Comparable<? super T>> extends MutableListModel<T> implements ListModel {

    private Comparator<T> comparator;

    /**
     * Construct a new, empty <code>OrderedListModel</code>.
     */

    public OrderedListModel() {
    }

    /**
     * Construct a new <code>OrderedListModel</code> with the given data.
     *
     * @param data A collection of elements to insert into the model. The
     *             elements are inserted in sorted order.
     */

    public OrderedListModel(Collection<T> data, Comparator<T> comparator) {
        super(data);

        if (comparator != null) {
            this.comparator = comparator;
            this.data.sort(comparator);
        }
    }

    /**
     * Set the comparator to be used by this model.
     *
     * @param comparator The comparator. May be <b>null</b> to indicate that
     *                   sorting should be based on the natural ordering of the elements.
     * @since Kiwi 2.2
     */

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Add an object to the model. The object is inserted at the proper index
     * to maintain a sorted model. If a matching object is already in the model,
     * the method does nothing.
     *
     * @param elem The new object to add.
     */

    public void addElement(T elem) {
        int index = Collections.binarySearch(data, elem, comparator);
        if (index < 0) {
            index = -index - 1;

            data.add(index, elem);
            fireIntervalAdded(index, index);
        }
    }

    /**
     * Notify listeners that the given element has been updated.
     *
     * @param elem The element. If this object is not actually in the model,
     *             the method does nothing.
     * @since Kiwi 2.2
     */

    public void updateItem(T elem) {
        int index = data.indexOf(elem);
        if (index >= 0) {
            updateItemAt(index);
        }
    }

    /**
     * Notify listeners that the element at the given index has been updated.
     *
     * @param index The index of the element. If the index is out of range, the
     *              method does nothing.
     * @since Kiwi 2.2
     */

    public void updateItemAt(int index) {
        if ((index >= 0) && (index < data.size())) {
            fireContentsChanged(index, index);
        }
    }

}
