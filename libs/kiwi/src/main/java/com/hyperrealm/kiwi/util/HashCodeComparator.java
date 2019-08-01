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

import java.io.Serializable;
import java.util.Comparator;

/**
 * A hash code comparator. This class compares the hash codes of two objects.
 *
 * @param <T>
 * @author Mark Lindner
 */

public class HashCodeComparator<T> implements Comparator<T>, Serializable {
    /**
     * This class implements the Comparator interface. You should consider whether
     * or not it should also implement the Serializable interface. If a comparator
     * is used to construct an ordered collection such as a TreeMap, then the TreeMap
     * will be serializable only if the comparator is also serializable. As most comparators
     * have little or no state, making them serializable is generally easy and good defensive programming.
     */
    private static final long serialVersionUID = 4112578634029874841L;
    /**
     * Construct a new <code>HashCodeComparator</code>.
     */

    public HashCodeComparator() {
    }

    /**
     * Compare the hash codes of two objects. A
     * <code>hashCode()</code> is performed on both objects, and then the
     * resulting integers are compared.
     *
     * @param a The first object.
     * @param b The second object.
     * @return <code>0</code> if the objects are equal, <code>-1</code>
     * if <code>a</code> is less than <code>b</code>, and <code>1</code>
     * if <code>a</code> is greater than <code>b</code>.
     */

    public int compare(T a, T b) {
        int result;

        if ((a == null) && (b != null)) {
            result = (-1);
        } else if ((a != null) && (b == null)) {
            result = (1);
        } else if (a == null) {
            result = (0);
        } else {
            int ha = a.hashCode(), hb = b.hashCode();
            result = Integer.compare(ha, hb);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass() == this.getClass();
    }
}
