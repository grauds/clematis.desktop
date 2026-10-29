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

import java.util.Date;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DateHolder}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class DateHolderTest {

    /**
     * Test getValue and setValue.
     */
    @Test
    public void testGetValueSetValue() {
        Date date = new Date();
        DateHolder holder = new DateHolder(date);
        Date retrieved = holder.getValue();
        assertNotNull(retrieved);
        assertEquals(date.getTime(), retrieved.getTime());
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        DateHolder holder = new DateHolder();
        Date value = holder.getValue();
        assertNotNull(value);
        // Should be close to current time
        long diff = System.currentTimeMillis() - value.getTime();
        assertTrue(diff < 1000);
    }

    /**
     * Test setValue with null.
     */
    @Test
    public void testSetValueWithNull() {
        DateHolder holder = new DateHolder();
        holder.setValue(null);
        assertNull(holder.getValue());
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        Date date = new Date();
        DateHolder holder = new DateHolder(date);
        String str = holder.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    /**
     * Test compareTo.
     */
    @Test
    public void testCompareTo() {
        Date earlier = new Date(System.currentTimeMillis() - 1000000);
        Date later = new Date(System.currentTimeMillis() + 1000000);
        DateHolder h1 = new DateHolder(earlier);
        DateHolder h2 = new DateHolder(later);

        assertTrue(h1.compareTo(h2) < 0);
        assertTrue(h2.compareTo(h1) > 0);
    }

    /**
     * Test copy returns independent copy.
     */
    @Test
    public void testCopy() {
        Date date = new Date();
        DateHolder original = new DateHolder(date, 3);
        DateHolder copy = (DateHolder) original.copy();
        assertEquals(date.getTime(), Objects.requireNonNull(copy.getValue()).getTime());
        assertEquals(3, copy.getSubtype());
        copy.setValue(new Date(0));
        assertEquals(date.getTime(), original.getValue().getTime());
    }
}
