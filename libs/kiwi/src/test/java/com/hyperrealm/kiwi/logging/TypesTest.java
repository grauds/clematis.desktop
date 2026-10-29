package com.hyperrealm.kiwi.logging;
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
import org.junit.Test;

/**
 * Unit tests for {@link Types} enum.
 */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class TypesTest {

    /**
     * Test enum values.
     */
    @Test
    public void testEnumValues() {
        Types[] values = Types.values();
        assertEquals(4, values.length);
        assertNotNull(Types.INFO);
        assertNotNull(Types.STATUS);
        assertNotNull(Types.WARNING);
        assertNotNull(Types.ERROR);
    }

    /**
     * Test getType() method.
     */
    @Test
    public void testGetType() {
        assertEquals("INFO", Types.INFO.getType());
        assertEquals("STATUS", Types.STATUS.getType());
        assertEquals("WARNING", Types.WARNING.getType());
        assertEquals("ERROR", Types.ERROR.getType());
    }

    /**
     * Test valueOf.
     */
    @Test
    public void testValueOf() {
        assertEquals(Types.INFO, Types.valueOf("INFO"));
        assertEquals(Types.ERROR, Types.valueOf("ERROR"));
    }
}
