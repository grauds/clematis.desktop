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

import java.util.Date;

import com.hyperrealm.kiwi.text.FormatConstants;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A mutable holder for a <code>Date</code> value.
 *
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DateHolder extends ValueHolder {
    /**
     * The current value.
     */
    protected Date value;

    /**
     * Construct a new <code>DateHolder</code> with an initial value of the
     * current date and time, and a default subtype of
     * <code>FormatConstants.DATE_FORMAT</code>.
     *
     * @see com.hyperrealm.kiwi.text.FormatConstants
     */

    public DateHolder() {
        this(new Date());
    }

    /**
     * Construct a new <code>DateHolder</code> with a specified initial
     * value, and a default subtype of
     * <code>FormatConstants.DATE_FORMAT</code>.
     *
     * @param value The initial value.
     */

    public DateHolder(Date value) {
        this(value, FormatConstants.DATE_FORMAT);
    }

    /**
     * Construct a new <code>DateHolder</code> with a specified initial
     * value and subtype. A subtype is particularly useful for a
     * <code>Date</code> object, since it may be used to specify which part
     * of the value is significant (such as the date, the time, or both).
     *
     * @param value The initial value.
     */

    public DateHolder(Date value, int subtype) {
        super(subtype);

        setValue(value);
    }

    /**
     * Get the <code>DateHolder</code>'s value.
     *
     * @return The current value.
     */

    public final Date getValue() {
        return value != null ? new Date(value.getTime()) : null;
    }

    /**
     * Set the <code>DateHolder</code>'s value.
     *
     * @param value The new value.
     */

    public final void setValue(Date value) {
        this.value = value != null ? new Date(value.getTime()) : null;
    }

    /**
     * Get a string representation for this object.
     */

    public String toString() {
        return (value == null ? super.toString() : String.valueOf(value));
    }

    /**
     * Compare this holder object to another.
     */
    public int compareTo(Object other) {
        Date v = ((DateHolder) other).getValue();

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
        return (new DateHolder((Date) value.clone(), subtype));
    }

}
