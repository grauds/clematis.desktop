package com.hyperrealm.kiwi.ui;
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
 * Unit tests for {@link KTextField}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KTextFieldTest {

    /**
     * Test getObject/setObject.
     */
    @Test
    public void testGetObjectSetObject() {
        KTextField field = new KTextField();
        field.setObject("Hello");
        assertEquals("Hello", field.getObject());
        field.setObject("World");
        assertEquals("World", field.getObject());
    }

    /**
     * Test width constructor.
     */
    @Test
    public void testWidthConstructor() {
        KTextField field = new KTextField(20);
        assertNotNull(field);
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        KTextField field = new KTextField();
        assertNotNull(field);
        assertEquals("", field.getObject());
    }
}
