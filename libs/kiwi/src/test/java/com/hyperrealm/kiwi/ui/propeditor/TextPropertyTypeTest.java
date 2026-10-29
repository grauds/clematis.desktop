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
import org.junit.Test;

/**
 * Unit tests for {@link TextPropertyType}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class TextPropertyTypeTest {

    /**
     * Test getMaximumLength.
     */
    @Test
    public void testGetMaximumLength() {
        TextPropertyType type = new TextPropertyType();
        assertEquals(-1, type.getMaximumLength());

        TextPropertyType type2 = new TextPropertyType(100);
        assertEquals(100, type2.getMaximumLength());
    }

    /**
     * Test setMaximumLength.
     */
    @Test
    public void testSetMaximumLength() {
        TextPropertyType type = new TextPropertyType();
        type.setMaximumLength(50);
        assertEquals(50, type.getMaximumLength());
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        TextPropertyType type = new TextPropertyType();
        assertEquals(-1, type.getMaximumLength());
    }

    /**
     * Test constructor with maxLength.
     */
    @Test
    public void testConstructorWithMaxLength() {
        TextPropertyType type = new TextPropertyType(255);
        assertEquals(255, type.getMaximumLength());
    }
}
