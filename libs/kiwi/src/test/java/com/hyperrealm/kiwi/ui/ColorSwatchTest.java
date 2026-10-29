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

import java.awt.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for {@link ColorSwatch}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class ColorSwatchTest {

    /**
     * Test color access.
     */
    @Test
    public void testColorAccess() {
        ColorSwatch swatch = new ColorSwatch(Color.RED, 50, 15);
        assertEquals(Color.RED, swatch.getColor());
        swatch.setColor(Color.BLUE);
        assertEquals(Color.BLUE, swatch.getColor());
    }

    /**
     * Test selection.
     */
    @Test
    public void testSelection() {
        ColorSwatch swatch = new ColorSwatch(Color.GREEN, 30, 10);
        assertNotNull(swatch);
        assertEquals(30, swatch.getIconWidth());
        assertEquals(10, swatch.getIconHeight());
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        ColorSwatch swatch = new ColorSwatch();
        assertNotNull(swatch);
        assertEquals(50, swatch.getIconWidth());
        assertEquals(15, swatch.getIconHeight());
    }
}
