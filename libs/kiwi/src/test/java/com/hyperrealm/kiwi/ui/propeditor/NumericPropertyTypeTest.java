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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.hyperrealm.kiwi.util.DoubleHolder;

/**
 * Unit tests for {@link NumericPropertyType}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class NumericPropertyTypeTest {

    /**
     * Test get/set min/max.
     */
    @Test
    public void testGetSetMinMax() {
        NumericPropertyType type = new NumericPropertyType();
        assertFalse(type.hasMinimumValue());
        assertFalse(type.hasMaximumValue());

        type.setMinimumValue(0);
        type.setMaximumValue(100);
        assertTrue(type.hasMinimumValue());
        assertTrue(type.hasMaximumValue());
        assertEquals(0, type.getMinimumValue(), 0.001);
        assertEquals(100, type.getMaximumValue(), 0.001);
    }

    /**
     * Test clear min/max.
     */
    @Test
    public void testClearMinMax() {
        NumericPropertyType type = new NumericPropertyType();
        type.setMinimumValue(0);
        type.setMaximumValue(100);
        type.clearMinimumValue();
        type.clearMaximumValue();
        assertFalse(type.hasMinimumValue());
        assertFalse(type.hasMaximumValue());
    }

    /**
     * Test decimals.
     */
    @Test
    public void testDecimals() {
        NumericPropertyType type = new NumericPropertyType();
        assertEquals(2, type.getDecimals());
        type.setDecimals(4);
        assertEquals(4, type.getDecimals());
    }

    /**
     * Test grouping.
     */
    @Test
    public void testGrouping() {
        NumericPropertyType type = new NumericPropertyType();
        assertTrue(type.getGrouping());
        type.setGrouping(false);
        assertFalse(type.getGrouping());
    }

    /**
     * Test format.
     */
    @Test
    public void testFormat() {
        NumericPropertyType type = new NumericPropertyType();
        DoubleHolder holder = new DoubleHolder(42.5);
        String result = type.formatValue(holder);
        assertNotNull(result);
    }
}
