package com.hyperrealm.kiwi.ui.model;
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
 * Unit tests for {@link ModelProperties} interface.
 */
public class ModelPropertiesTest {

    /**
     * Test property constants.
     */
    @Test
    public void testPropertyConstants() {
        assertEquals("__columnNames__", ModelProperties.COLUMN_NAMES_PROPERTY);
        assertEquals("__columnTypes__", ModelProperties.COLUMN_TYPES_PROPERTY);
    }

    /**
     * Test that constants are accessible.
     */
    @Test
    public void testConstantsAccessible() {
        String namesProp = ModelProperties.COLUMN_NAMES_PROPERTY;
        String typesProp = ModelProperties.COLUMN_TYPES_PROPERTY;
        assertNotNull(namesProp);
        assertNotNull(typesProp);
    }
}
