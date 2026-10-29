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

import java.awt.Image;
import java.awt.Toolkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for {@link SubImageIcon}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class SubImageIconTest {

    /**
     * Test icon construction with offset.
     */
    @Test
    public void testIconConstructionWithOffset() {
        Image image = Toolkit.getDefaultToolkit().createImage(
            new byte[0]);
        SubImageIcon icon = new SubImageIcon(image, 10, 20, 50, 30);
        assertNotNull(icon);
        assertNotNull(icon);
    }

    /**
     * Test getIconWidth/Height.
     */
    @Test
    public void testGetIconWidthHeight() {
        Image image = Toolkit.getDefaultToolkit().createImage(
            new byte[0]);
        SubImageIcon icon = new SubImageIcon(image, 10, 20, 50, 30);
        assertEquals(50, icon.getIconWidth());
        assertEquals(30, icon.getIconHeight());
    }
}
