package com.hyperrealm.kiwi.util;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link IDMap}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class IDMapTest {

    /**
     * Test put and get operations.
     */
    @Test
    public void testPutGet() {
        IDMap map = new IDMap();
        map.put(1L, "one");
        map.put(2L, "two");
        map.put(3L, "three");
        assertEquals("one", map.get(1L));
        assertEquals("two", map.get(2L));
        assertEquals("three", map.get(3L));
    }

    /**
     * Test remove operation.
     */
    @Test
    public void testRemove() {
        IDMap map = new IDMap();
        map.put(1L, "one");
        map.put(2L, "two");
        map.put(3L, "three");
        assertEquals("two", map.remove(2L));
        assertNull(map.get(2L));
        assertEquals(2, map.size());
    }

    /**
     * Test sort behavior.
     */
    @Test
    public void testSortBehavior() {
        IDMap map = new IDMap();
        map.put(3L, "three");
        map.put(1L, "one");
        map.put(2L, "two");
        // Access should trigger sort
        assertEquals("one", map.get(1L));
        assertEquals("two", map.get(2L));
        assertEquals("three", map.get(3L));
    }

    /**
     * Test duplicate keys (later value should be stored).
     */
    @Test
    public void testDuplicateKeys() {
        IDMap map = new IDMap();
        map.put(1L, "first");
        map.put(1L, "second");
        // Both entries are stored, but get() returns the first found after sort
        assertNotNull(map.get(1L));
        assertEquals(2, map.size());
    }

    /**
     * Test getValueAt and getKeyAt.
     */
    @Test
    public void testGetValueAtGetKeyAt() {
        IDMap map = new IDMap();
        map.put(10L, "value10");
        map.put(20L, "value20");
        map.put(30L, "value30");
        // Force sort by calling get
        map.get(10L);
        assertEquals(10L, map.getKeyAt(0));
        assertEquals(20L, map.getKeyAt(1));
        assertEquals(30L, map.getKeyAt(2));
        assertEquals("value10", map.getValueAt(0));
        assertEquals("value20", map.getValueAt(1));
        assertEquals("value30", map.getValueAt(2));
    }

    /**
     * Test size.
     */
    @Test
    public void testSize() {
        IDMap map = new IDMap();
        assertEquals(0, map.size());
        map.put(1L, "one");
        assertEquals(1, map.size());
        map.put(2L, "two");
        assertEquals(2, map.size());
    }
}
