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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link StringHolder}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class StringHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        StringHolder holder = new StringHolder();
        assertNull(holder.getValue());
        holder.setValue("hello");
        assertEquals("hello", holder.getValue());
        holder.setValue("world");
        assertEquals("world", holder.getValue());
    }

    /**
     * Test toString with null.
     */
    @Test
    public void testToStringWithNull() {
        StringHolder holder = new StringHolder();
        assertNull(holder.toString());
    }

    /**
     * Test toString with value.
     */
    @Test
    public void testToStringWithValue() {
        StringHolder holder = new StringHolder("test");
        assertEquals("test", holder.toString());
    }

    /**
     * Test compareTo with null handling.
     */
    @Test
    public void testCompareToNullHandling() {
        StringHolder h1 = new StringHolder("a");
        StringHolder h2 = new StringHolder("b");
        StringHolder h3 = new StringHolder(null);
        StringHolder h4 = new StringHolder(null);

        assertTrue(h1.compareTo(h2) < 0);
        assertEquals(0, h3.compareTo(h4));
        assertTrue(h1.compareTo(h3) > 0); // non-null > null
        assertTrue(h3.compareTo(h1) < 0); // null < non-null
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy() {
        StringHolder original = new StringHolder("test", 1);
        StringHolder copy = (StringHolder) original.copy();
        assertEquals("test", copy.getValue());
        assertEquals(1, copy.getSubtype());
        copy.setValue("changed");
        assertEquals("test", original.getValue());
    }

    /**
     * Test constructor with subtype.
     */
    @Test
    public void testConstructorWithSubtype() {
        StringHolder holder = new StringHolder("value", 5);
        assertEquals("value", holder.getValue());
        assertEquals(5, holder.getSubtype());
    }
}
