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

/**
 * An abstract base class that encapsulates metadata about a property type.
 * This metadata may include such information as input constraints and
 * formatting directives.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public abstract class PropertyType {

    /**
     * Construct a new <code>PropertyType</code> object.
     */

    protected PropertyType() {
    }

    /**
     * Format a property value as a string. The default implementation calls
     * the object's <code>toString()</code> method.
     *
     * @param value The value, which must be an object compatible with this
     *              property type.
     * @return A string representation of the value, suitable for display
     * purposes.
     */

    public String formatValue(Object value) {
        return (value.toString());
    }

}
