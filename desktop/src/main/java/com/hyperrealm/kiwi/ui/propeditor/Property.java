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

package com.hyperrealm.kiwi.ui.propeditor;

import javax.swing.Icon;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A class representing an editable property. A property has a textual name,
 * a type, and a value. The type is represented by a <code>PropertyType</code>
 * object which contains metadata that is useful to a visual editor. The value
 * can be any arbitrary object.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class Property {
    private static final Icon PROPERTY_ICON = KiwiUtils.getResourceManager()
        .getIcon("option.png");
    private PropertyType type;
    private Object value;
    private String name;
    private Icon icon;
    private boolean editable = true;

    /**
     * Construct a new property with the given name and type.
     *
     * @param name The textual name for the property.
     * @param type The <code>PropertyType</code> object describing the type of
     *             this property.
     */

    public Property(String name, PropertyType type) {
        this(name, PROPERTY_ICON, type, null);
    }

    /**
     * Construct a new property with the given name, icon, and type.
     *
     * @param name The textual name for the property.
     * @param icon The icon for the property.
     * @param type The <code>PropertyType</code> object describing the type of
     *             this property.
     */

    public Property(String name, Icon icon, PropertyType type) {
        this(name, icon, type, null);
    }

    /**
     * Construct a new property with the given name, type, and value.
     *
     * @param name  The textual name for the property.
     * @param type  The <code>PropertyType</code> object describing the type of
     *              this property.
     * @param value The initial value for the property.
     * @since Kiwi 2.4
     */

    public Property(String name, PropertyType type, Object value) {
        this(name, PROPERTY_ICON, type, value);
    }

    /**
     * Construct a new property with the given name, icon, type, and initial
     * value.
     *
     * @param name  The textual name for the property.
     * @param icon  The icon for the property.
     * @param type  The <code>PropertyType</code> object describing the type of
     *              this property.
     * @param value The initial value for the property.
     */

    public Property(String name, Icon icon, PropertyType type, Object value) {
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.value = value;
    }

    /**
     * Determine if this property is editable.
     *
     * @return <b>true</b> if the property is editable, <b>false</b> otherwise.
     */

    public boolean isEditable() {
        return (editable);
    }

    /**
     * Set the editable state of this property.
     *
     * @param editable A flag indicating whether the property is editable or not.
     */

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Determine if the property currenty has a (non-null) value.
     *
     * @return <b>true</b> if the property has a value, <b>false</b> otherwise.
     */

    public boolean hasValue() {
        return (value != null);
    }

    /**
     * Get the icon, if any, for this property.
     *
     * @return The icon.
     */

    public Icon getIcon() {
        return (icon);
    }

    /**
     * Get the name of this property.
     *
     * @return The textual name.
     */

    public String getName() {
        return (name);
    }

    /**
     * Get the type of this property.
     *
     * @return The <code>PropertyType</code> object for this property.
     */

    public PropertyType getType() {
        return (type);
    }

    /**
     * Get the (possibly null) value of the property.
     *
     * @return The value.
     */

    public Object getValue() {
        return (value);
    }

    /**
     * Set the value of the property.
     *
     * @param value The (possibly null) value for the property.
     */

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Return a textual representation of this property, suitable for rendering
     * purposes.
     */

    public String toString() {
        if (value != null) {
            return (name + ": " + type.formatValue(value));
        } else {
            return (name);
        }
    }
}
