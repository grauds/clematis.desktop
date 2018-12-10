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

import javax.swing.Icon;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * An interface that describes an adapter for accessing fields in a domain
 * object. Consider a domain object as an array of fields which map to
 * corresponding columns in a table (specifically, a <code>JTable</code>).
 * In order to display a domain object as a row in such a table, data
 * members of the domain object must be mapped to cardinal fields. This adapter
 * provides the interface to accomplish this.
 *
 * @param <T>
 * @author Mark Lindner
 * @see javax.swing.JTable
 */

public interface DomainObjectFieldAdapter<T> {
    /**
     * Get the field count.
     *
     * @return The number of fields in the domain object.
     */

    int getFieldCount();

    /**
     * Get the name of a field. The name may be used for display purposes, such
     * as in a table column header.
     *
     * @param field The field number.
     * @return The name of the field.
     */

    String getFieldName(int field);

    /**
     * Get a field value.
     *
     * @param object The domain object for which a field value is being
     *               requested.
     * @param field  The field number.
     * @return The value of the field.
     */

    Object getField(T object, int field);

    /**
     * Set a field value.
     *
     * @param object The domain object for which a field value is being set.
     * @param field  The field number.
     * @param value  The new value for the field.
     */

    void setField(T object, int field, Object value);

    /**
     * Get a cell renderer that is appropriate for rendering a given field.
     *
     * @param field The field number.
     * @return A <code>TableCellRenderer</code> that can be used to render the
     * value of this field.
     */

    TableCellRenderer getCellRenderer(int field);

    /**
     * Get a cell editor that is appropriate for editing a given field.
     *
     * @param field The field number.
     * @return A <code>TableCellEditor</code> that can be used to edit the
     * value of this field.
     */

    TableCellEditor getCellEditor(int field);

    /**
     * Determine if the given field is editable.
     *
     * @param object The domain object for which a field editable state is being
     *               requested.
     * @param field  The field number.
     * @return <code>true</code> if the field can be edited by external means
     * (such as through a table control), and <code>false</code> if it is
     * immutable (display-only).
     */

    boolean isFieldEditable(T object, int field);

    /**
     * Determine the type of a given field.
     *
     * @param field The field number.
     * @return The type of the field.
     */

    Class getFieldClass(int field);

    /**
     * Get the preferered column width for this field. This value is used by
     * the <code>JTable</code> or similar component to determine column sizing
     * for the data model that uses this adapter.
     *
     * @param field The field number.
     * @return The preferred width for the field, or 0 if there is no preferred
     * width.
     */

    int getFieldPreferredWidth(int field);

    /**
     * Get the minimum column width for this field. This value is used by
     * the <code>JTable</code> or similar component to determine column sizing
     * for the data model that uses this adapter.
     *
     * @param field The field number.
     * @return The minimum width for the field, or 0 if there is no minimum
     * width.
     */

    int getFieldMinWidth(int field);

    /**
     * Get the maximum column width for this field. This value is used by
     * the <code>JTable</code> or similar component to determine column sizing
     * for the data model that uses this adapter.
     *
     * @param field The field number.
     * @return The maximum width for the field, or 0 if there is no minimum
     * width.
     */

    int getFieldMaxWidth(int field);

    /**
     * Get the label for the given item. This is the textual representation
     * of the item suitable for display in a <code>JList</code> or
     * <code>JComboBox</code>.
     *
     * @param item The item.
     * @return The label.
     * @since Kiwi 2.4
     */

    String getLabel(T item);

    /**
     * Get the Icon for the given item. This ICON is suitable for display in
     * a <code>JList</code> or <code>JComboBox</code>.
     *
     * @param item The item.
     * @return The Icon, which may be <b>null</b>.
     * @since Kiwi 2.4
     */

    Icon getIcon(T item);

}
