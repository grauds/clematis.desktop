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

import java.util.Collections;

import javax.swing.Icon;

import com.hyperrealm.kiwi.ui.model.ImmutableModelException;
import com.hyperrealm.kiwi.ui.model.datasource.ListDataSource;

/**
 * An implementation of <code>KListModel</code> that obtains its data from
 * an external data source.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ExternalKListModel<T> extends DefaultKListModel<T> {

    /**
     * The data source for this model.
     */
    protected ListDataSource<T> source;

    private String[] columnNames;

    private Class[] columnTypes;

    /**
     * Construct a new <code>ExternalKListModel</code> with the given
     * data source.
     *
     * @param source The data source.
     */

    public ExternalKListModel(ListDataSource<T> source) {
        super();

        this.source = source;

        reload();
    }

    /**
     * Reload the list model from the data source.
     */

    public void reload() {
        if (source == null) {
            return;
        }

        // load metadata

        columnNames = (String[]) source.getValueForProperty(
            null, ListDataSource.COLUMN_NAMES_PROPERTY);
        columnTypes = (Class[]) source.getValueForProperty(
            null, ListDataSource.COLUMN_TYPES_PROPERTY);

        // load data

        data.clear();
        T[] items = source.getItems();
        if (items != null) {
            Collections.addAll(data, items);
        }

        support.fireDataChanged();
    }

    /**
     *
     */

    public void addItem(T item) {
        throw (new ImmutableModelException());
    }

    /**
     *
     */

    public void insertItemAt(T item, int index) {
        throw (new ImmutableModelException());
    }

    /**
     *
     */

    public void removeItemAt(int index) {
        throw (new ImmutableModelException());
    }

    /**
     *
     */

    public void removeItem(T item) {
        throw (new ImmutableModelException());
    }

    /**
     *
     */

    public void updateItem(T item) {
        throw (new ImmutableModelException());
    }

    /**
     *
     */

    public void updateItemAt(int index) {
        throw (new ImmutableModelException());
    }

    /**
     * Get the label for an item.
     *
     * @param item The item.
     * @return A string label for the item.
     */

    public String getLabel(T item) {
        String label = source.getLabel(item);

        return (label == null ? item.toString() : label);
    }

    /**
     * Get the ICON for an item.
     *
     * @param item The item.
     * @return An ICON for the item.
     */

    public Icon getIcon(T item) {
        return (source.getIcon(item));
    }

    /**
     *
     */

    public Object getField(T item, int field) {
        return (source.getValueForProperty(item, columnNames[field]));
    }

    /**
     *
     */

    public int getFieldCount() {
        return (columnNames.length);
    }

    /**
     *
     */

    public String getFieldLabel(int field) {
        return (columnNames[field]);
    }

    /**
     *
     */

    public Class getFieldType(int field) {
        return (columnTypes[field]);
    }

}
