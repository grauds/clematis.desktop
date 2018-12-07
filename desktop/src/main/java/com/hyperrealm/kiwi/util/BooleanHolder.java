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

package com.hyperrealm.kiwi.util;

/**
 * A mutable holder for a <code>boolean</code> value.
 *
 * @author Mark Lindner
 */

public class BooleanHolder extends ValueHolder {
    /**
     * The current value.
     */
    protected boolean value;

    /**
     * Construct a new <code>BooleanHolder</code> with an initial value of
     * <code>false</code> and default subtype of 0.
     */

    public BooleanHolder() {
        this(false, 0);
    }

    /**
     * Construct a new <code>BooleanHolder</code> with a specified initial
     * value and default subtype of 0.
     *
     * @param value The initial value.
     */

    public BooleanHolder(boolean value) {
        this(value, 0);
    }

    /**
     * Construct a new <code>BooleanHolder</code> with a specified initial
     * value and subtype.
     *
     * @param value   The initial value.
     * @param subtype The subtype for this value.
     */

    public BooleanHolder(boolean value, int subtype) {
        super(subtype);

        this.value = value;
    }

    /**
     * Get the <code>BooleanHolder</code>'s value.
     *
     * @return The current value.
     */

    public final boolean getValue() {
        return (value);
    }

    /**
     * Set the <code>BooleanHolder</code>'s value.
     *
     * @param value The new value.
     */

    public final void setValue(boolean value) {
        this.value = value;
    }

    /**
     * Get a string representation for this object.
     */

    public String toString() {
        return (String.valueOf(value));
    }

    /**
     * Compare this holder object to another.
     */

    public int compareTo(Object other) {
        boolean v = ((BooleanHolder) other).getValue();

        return ((value == v) ? 0 : (value ? 1 : -1));
    }

    /**
     * Clone this object.
     */

    public Object copy() {
        return (new BooleanHolder(value, subtype));
    }

}
