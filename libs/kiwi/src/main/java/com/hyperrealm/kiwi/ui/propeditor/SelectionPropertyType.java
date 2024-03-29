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

import java.util.Arrays;

/**
 * A property type representing properties which may have one of a fixed set
 * of possible values.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class SelectionPropertyType extends PropertyType {

    private static final String[] DEFAULT_ITEMS = {"One", "Two", "Three"};

    private Object[] items;

    /**
     * Construct a new <code>SelectionPropertyType</code> with a default set of
     * possible values.
     */

    public SelectionPropertyType() {
        this(DEFAULT_ITEMS);
    }

    /**
     * Construct a new <code>SelectionPropertyType</code> with the given set of
     * possible values.
     *
     * @param items The list of possible values.
     */

    public SelectionPropertyType(Object[] items) {
        setItems(items);
    }

    /**
     * Get the list of possible values.
     *
     * @return The possible values, as an array.
     */

    public Object[] getItems() {
        return items != null ? Arrays.copyOf(items, items.length) : null;
    }

    /**
     * Set the list of possible values.
     *
     * @param items The possible values, as an array.
     */

    public void setItems(Object[] items) {
        this.items = items != null ? Arrays.copyOf(items, items.length) : null;
    }

}
