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
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.Icon;

import com.hyperrealm.kiwi.event.KListModelListener;
import com.hyperrealm.kiwi.event.KListModelSupport;
import com.hyperrealm.kiwi.util.MutatorException;

/**
 * A default implementation of the <code>KListModel</code> interface.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class DefaultKListModel<T> implements KListModel<T> {
    /**
     * The actual data for this model.
     */
    protected final ArrayList<T> data;
    /**
     * The support object for firing <code>KListModelEvent</code>s.
     */
    protected final KListModelSupport support;

    /**
     * Construct a new <code>KBasicListModel</code>.
     */

    public DefaultKListModel() {
        support = new KListModelSupport(this);
        data = new ArrayList<T>();
    }

    /*
     */

    public boolean isEmpty() {
        return (data.size() == 0);
    }

    /*
     */

    public void clear() {
        int index = data.size();

        if (index > 0) {
            data.clear();
            support.fireItemsRemoved(0, index - 1);
        }
    }

    /*
     */

    public int getItemCount() {
        return (data.size());
    }

    /*
     */

    public Iterator<T> iterator() {
        return (data.iterator());
    }

    /*
     */

    public T getItemAt(int index) {
        return (data.get(index));
    }

    /*
     */

    public int indexOf(T item) {
        return (data.indexOf(item));
    }

    /*
     */

    public void addItem(T item) {
        int index = data.size();

        data.add(item);
        support.fireItemAdded(index);
    }

    /*
     */

    public void insertItemAt(T item, int index) {
        data.add(index, item);
        support.fireItemAdded(index);
    }

    /*
     */

    public void removeItemAt(int index) {
        if ((index < 0) || (index >= data.size())) {
            return;
        }

        data.remove(index);
        support.fireItemRemoved(index);
    }

    /*
     */

    public void removeItem(T item) {
        int index = data.indexOf(item);

        if (index >= 0) {
            data.remove(index);
            support.fireItemRemoved(index);
        }
    }

    /*
     */

    public void updateItem(T item) {
        updateItem(item, -1);
    }

    /*
     */

    public void updateItem(T item, int field) {
        int index = data.indexOf(item);
        if (index >= 0) {
            support.fireItemChanged(index, field);
        }
    }

    /*
     */

    public void updateItemAt(int index) {
        updateItemAt(index, -1);
    }

    /*
     */

    public void updateItemAt(int index, int field) {
        if ((index >= 0) && (index < data.size())) {
            support.fireItemChanged(index, field);
        }
    }

    /*
     */

    public void addListModelListener(KListModelListener listener) {
        support.addListModelListener(listener);
    }

    /*
     */

    public void removeListModelListener(KListModelListener listener) {
        support.removeListModelListener(listener);
    }

    /*
     */

    public String getLabel(T item) {
        return (item == null ? null : item.toString());
    }

    /*
     */

    public Icon getIcon(T item) {
        return (null);
    }

    /*
     */

    public int getFieldCount() {
        return (1);
    }

    /*
     */

    public String getFieldLabel(int field) {
        return (null);
    }

    /*
     */

    public Class getFieldType(int field) {
        return (Object.class);
    }

    /*
     */

    public Object getField(T item, int field) {
        return (item);
    }

    /*
     */

    public void setField(T item, int field, Object value) throws MutatorException {
        // no-op
    }

    /*
     */

    public boolean isFieldMutable(T item, int field) {
        return (false);
    }

    /**
     * Sort the model.
     *
     * @param comparator The <code>Comparator</code> to use for the sorting.
     */

    public void sort(Comparator<T> comparator) {
        data.sort(comparator);
        support.fireDataChanged();
    }

}
