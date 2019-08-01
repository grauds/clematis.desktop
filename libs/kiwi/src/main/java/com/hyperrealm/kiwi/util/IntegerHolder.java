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

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A mutable holder for an <code>int</code> value.
 *
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IntegerHolder extends ValueHolder {
    /**
     * The current value.
     */
    protected int value;

    /**
     * Construct a new <code>IntegerHolder</code> with an initial value of
     * <code>0</code> and default subtype of <code>0</code>.
     */

    public IntegerHolder() {
        this(0, 0);
    }

    /**
     * Construct a new <code>IntegerHolder</code> with a specified initial
     * value and default subtype of <code>0</code>.
     *
     * @param value The initial value.
     */

    public IntegerHolder(int value) {
        this(value, 0);
    }

    /**
     * Construct a new <code>IntegerHolder</code> with a specified initial
     * value and subtype.
     *
     * @param value   The initial value.
     * @param subtype The subtype for this value.
     */

    public IntegerHolder(int value, int subtype) {
        super(subtype);

        setValue(value);
    }

    /**
     * Get the <code>IntegerHolder</code>'s value.
     *
     * @return The current value.
     */

    public final synchronized int getValue() {
        return value;
    }

    /**
     * Set the <code>IntegerHolder</code>'s value.
     *
     * @param value The new value.
     */

    public final synchronized void setValue(int value) {
        this.value = value;
    }

    /**
     * Get a string representation for this object.
     */

    public String toString() {
        return (String.valueOf(getValue()));
    }

    /**
     * Compare this holder object to another.
     */

    public int compareTo(Object other) {
        int v = ((IntegerHolder) other).getValue();

        return Integer.compare(getValue(), v);
    }

    /**
     * Clone this object.
     */

    public Object copy() {
        return (new IntegerHolder(getValue(), subtype));
    }

}
