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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link BitString}.
 */
public class BitStringTest {

    /**
     * Test set and isSet operations.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testSetIsSet() {
        BitString bs = new BitString(8, false);
        assertFalse(bs.isSet(0));
        bs.set(0);
        assertTrue(bs.isSet(0));
        bs.set(3);
        assertTrue(bs.isSet(3));
        assertFalse(bs.isSet(7));
    }

    /**
     * Test clear and isClear operations.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testClearIsClear() {
        BitString bs = new BitString(8, true);
        assertTrue(bs.isSet(0));
        bs.clear(0);
        assertTrue(bs.isClear(0));
        assertFalse(bs.isSet(0));
    }

    /**
     * Test compareTo method.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testCompareTo() {
        BitString bs1 = new BitString(4, false);
        bs1.set(0);
        bs1.set(1);

        BitString bs2 = new BitString(4, false);
        bs2.set(0);
        bs2.set(1);
        bs2.set(2);

        // bs1 bits are a subset of bs2 bits
        assertFalse(bs1.compareTo(bs2));
        // bs2 has bits that bs1 doesn't have
        assertTrue(bs2.compareTo(bs1));
    }

    /**
     * Test toString method.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testToString() {
        BitString bs = new BitString(8, false);
        bs.set(0);
        bs.set(3);
        bs.set(7);
        String str = bs.toString();
        assertEquals("10010001", str);
    }

    /**
     * Test edge cases.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testEdgeCases() {
        BitString bs = new BitString(1, false);
        bs.set(0);
        assertTrue(bs.isSet(0));

        BitString bs2 = new BitString(1, true);
        assertTrue(bs2.isSet(0));

        // Test invalid bit index
        assertThrows(IllegalArgumentException.class, () -> bs.set(-1));
        assertThrows(IllegalArgumentException.class, () -> bs.isSet(-1));
        assertThrows(IllegalArgumentException.class, () -> bs.clear(-1));
        assertThrows(IllegalArgumentException.class, () -> bs.set(8));
    }

    /**
     * Test clearAll and setAll.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testClearAllSetAll() {
        BitString bs = new BitString(8, true);
        assertTrue(bs.isSet(0));
        assertTrue(bs.isSet(7));
        bs.clearAll();
        assertFalse(bs.isSet(0));
        assertFalse(bs.isSet(7));
        bs.setAll();
        assertTrue(bs.isSet(0));
        assertTrue(bs.isSet(7));
    }
}
