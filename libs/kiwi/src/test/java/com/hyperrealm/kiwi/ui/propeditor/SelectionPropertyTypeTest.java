package com.hyperrealm.kiwi.ui.propeditor;
/* ----------------------------------------------------------------------------\
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
   ----------------------------------------------------------------------------\
*/

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link SelectionPropertyType}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class SelectionPropertyTypeTest {

    /**
     * Test selection options.
     */
    @Test
    public void testSelectionOptions() {
        SelectionPropertyType type = new SelectionPropertyType();
        Object[] items = type.getItems();
        assertNotNull(items);
        assertArrayEquals(new Object[]{"One", "Two", "Three"}, items);
    }

    /**
     * Test setItems.
     */
    @Test
    public void testSetItems() {
        SelectionPropertyType type = new SelectionPropertyType();
        type.setItems(new Object[]{"A", "B", "C"});
        Object[] items = type.getItems();
        assertArrayEquals(new Object[]{"A", "B", "C"}, items);
    }

    /**
     * Test constructor with items.
     */
    @Test
    public void testConstructorWithItems() {
        SelectionPropertyType type = new SelectionPropertyType(new Object[]{"X", "Y"});
        Object[] items = type.getItems();
        assertArrayEquals(new Object[]{"X", "Y"}, items);
    }

    /**
     * Test setItems with null.
     */
    @Test
    public void testSetItemsWithNull() {
        SelectionPropertyType type = new SelectionPropertyType();
        type.setItems(null);
        Object[] items = type.getItems();
        assertNull(items);
    }
}
