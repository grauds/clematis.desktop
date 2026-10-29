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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link BooleanHolder}.
 */
public class BooleanHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        BooleanHolder holder = new BooleanHolder();
        assertFalse(holder.getValue());
        holder.setValue(true);
        assertTrue(holder.getValue());
        holder.setValue(false);
        assertFalse(holder.getValue());
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        BooleanHolder holderTrue = new BooleanHolder(true);
        assertEquals("true", holderTrue.toString());
        BooleanHolder holderFalse = new BooleanHolder(false);
        assertEquals("false", holderFalse.toString());
    }

    /**
     * Test compareTo.
     */
    @Test
    public void testCompareTo() {
        BooleanHolder h1 = new BooleanHolder(true);
        BooleanHolder h2 = new BooleanHolder(false);
        BooleanHolder h3 = new BooleanHolder(true);

        assertTrue(h1.compareTo(h2) > 0); // true > false
        assertTrue(h2.compareTo(h1) < 0); // false < true
        assertEquals(0, h1.compareTo(h3));
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy() {
        BooleanHolder original = new BooleanHolder(true, 1);
        BooleanHolder copy = (BooleanHolder) original.copy();
        assertTrue(copy.getValue());
        assertEquals(1, copy.getSubtype());
        copy.setValue(false);
        assertTrue(original.getValue());
    }
}
