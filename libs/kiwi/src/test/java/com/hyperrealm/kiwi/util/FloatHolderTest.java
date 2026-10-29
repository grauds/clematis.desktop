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
 * Unit tests for {@link FloatHolder}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class FloatHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        FloatHolder holder = new FloatHolder();
        assertEquals(0.0f, holder.getValue(), 0.001f);
        holder.setValue(3.14f);
        assertEquals(3.14f, holder.getValue(), 0.001f);
        holder.setValue(-2.5f);
        assertEquals(-2.5f, holder.getValue(), 0.001f);
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        FloatHolder holder = new FloatHolder(42.5f);
        assertEquals("42.5", holder.toString());
    }

    /**
     * Test compareTo.
     */
    @Test
    public void testCompareTo() {
        FloatHolder h1 = new FloatHolder(1.0f);
        FloatHolder h2 = new FloatHolder(2.0f);
        FloatHolder h3 = new FloatHolder(1.0f);

        assertTrue(h1.compareTo(h2) < 0);
        assertTrue(h2.compareTo(h1) > 0);
        assertEquals(0, h1.compareTo(h3));
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy() {
        FloatHolder original = new FloatHolder(3.14f, 2);
        FloatHolder copy = (FloatHolder) original.copy();
        assertEquals(3.14f, copy.getValue(), 0.001f);
        assertEquals(2, copy.getSubtype());
        copy.setValue(99.0f);
        assertEquals(3.14f, original.getValue(), 0.001f);
    }
}
