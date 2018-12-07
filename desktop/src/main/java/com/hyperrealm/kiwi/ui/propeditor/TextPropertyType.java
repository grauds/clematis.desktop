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
 * A property type representing text properties.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class TextPropertyType extends PropertyType {
    private int maxLength;

    /**
     * Construct a new <code>TextPropertyType</code> for text values of
     * unlimited length.
     */

    public TextPropertyType() {
        this(-1);
    }

    /**
     * Construct a new <code>TextPropertyType</code> for text values with a
     * maximum length.
     *
     * @param maxLength The maximum length, in characters, that a value may
     *                  have.
     */

    public TextPropertyType(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Get the maximum length that a value may have.
     *
     * @return The maximum length, in characters.
     */

    public int getMaximumLength() {
        return (maxLength);
    }

    /**
     * Set the maximum length that a value may have.
     *
     * @param maxLength The maximum length, in characters.
     */

    public void setMaximumLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
