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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link IntegerHolder}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class IntegerHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        IntegerHolder holder = new IntegerHolder();
        assertEquals(0, holder.getValue());
        holder.setValue(42);
        assertEquals(42, holder.getValue());
        holder.setValue(-10);
        assertEquals(-10, holder.getValue());
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        IntegerHolder holder = new IntegerHolder(123);
        assertEquals("123", holder.toString());
    }

    /**
     * Test compareTo.
     */
    @Test
    public void testCompareTo() {
        IntegerHolder h1 = new IntegerHolder(10);
        IntegerHolder h2 = new IntegerHolder(20);
        IntegerHolder h3 = new IntegerHolder(10);

        assertTrue(h1.compareTo(h2) < 0);
        assertTrue(h2.compareTo(h1) > 0);
        assertEquals(0, h1.compareTo(h3));
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy() {
        IntegerHolder original = new IntegerHolder(42, 5);
        IntegerHolder copy = (IntegerHolder) original.copy();
        assertEquals(42, copy.getValue());
        assertEquals(5, copy.getSubtype());
        copy.setValue(100);
        assertEquals(42, original.getValue()); // Original unchanged
    }

    /**
     * Test constructor with subtype.
     */
    @Test
    public void testConstructorWithSubtype() {
        IntegerHolder holder = new IntegerHolder(99, 3);
        assertEquals(99, holder.getValue());
        assertEquals(3, holder.getSubtype());
    }
}
