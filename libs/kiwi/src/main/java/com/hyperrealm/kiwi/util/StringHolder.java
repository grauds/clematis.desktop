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
 * A mutable holder for a <code>String</code> value.
 *
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StringHolder extends ValueHolder {
    /**
     * The current value.
     */
    protected String value;

    /**
     * Construct a new <code>StringHolder</code> with an initial value of
     * <code>null</code> and default subtype of <code>0</code>.
     */

    public StringHolder() {
        this(null, 0);
    }

    /**
     * Construct a new <code>StringHolder</code> with a specified initial
     * value and default subtype of <code>0</code>.
     *
     * @param value The initial value.
     */

    public StringHolder(String value) {
        this(value, 0);
    }

    /**
     * Construct a new <code>StringHolder</code> with a specified initial
     * value and subtype.
     *
     * @param value   The initial value.
     * @param subtype The subtype for this value.
     */

    public StringHolder(String value, int subtype) {
        super(subtype);

        this.value = value;
    }

    /**
     * Get the <code>StringHolder</code>'s value.
     *
     * @return The current value.
     */

    public final String getValue() {
        return (value);
    }

    /**
     * Set the <code>StringHolder</code>'s value.
     *
     * @param value The new value.
     */

    public final void setValue(String value) {
        this.value = value;
    }

    /**
     * Get a string representation for this object.
     */

    public String toString() {
        return (value);
    }

    /**
     * Compare this holder object to another.
     */
    public int compareTo(Object other) {
        String v = ((StringHolder) other).getValue();

        int ret;

        if ((v == null) && (value == null)) {
            ret = 0;
        } else if (v == null) {
            ret = 1;
        } else if (value == null) {
            ret = -1;
        } else {
            ret = value.compareTo(v);
        }

        return ret;
    }

    /**
     * Clone this object.
     */

    public Object copy() {
        return new StringHolder(value, subtype);
    }

}
