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
 * Unit tests for {@link DoubleHolder}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class DoubleHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        DoubleHolder holder = new DoubleHolder();
        assertEquals(0.0, holder.getValue(), 0.001);
        holder.setValue(3.14159);
        assertEquals(3.14159, holder.getValue(), 0.001);
        holder.setValue(-2.718);
        assertEquals(-2.718, holder.getValue(), 0.001);
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        DoubleHolder holder = new DoubleHolder(42.5);
        assertEquals("42.5", holder.toString());
    }

    /**
     * Test compareTo.
     */
    @Test
    public void testCompareTo() {
        DoubleHolder h1 = new DoubleHolder(1.0);
        DoubleHolder h2 = new DoubleHolder(2.0);
        DoubleHolder h3 = new DoubleHolder(1.0);

        assertTrue(h1.compareTo(h2) < 0);
        assertTrue(h2.compareTo(h1) > 0);
        assertEquals(0, h1.compareTo(h3));
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy() {
        DoubleHolder original = new DoubleHolder(3.14, 2);
        DoubleHolder copy = (DoubleHolder) original.copy();
        assertEquals(3.14, copy.getValue(), 0.001);
        assertEquals(2, copy.getSubtype());
        copy.setValue(99.0);
        assertEquals(3.14, original.getValue(), 0.001);
    }
}
