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

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KLabel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KLabelTest {

    /**
     * Test constructor variants.
     */
    @Test
    public void testConstructorVariants() {
        new KLabel();
        new KLabel("text");
        new KLabel(new ImageIcon());
        new KLabel("text", SwingConstants.CENTER);
        new KLabel("text", new ImageIcon(), SwingConstants.CENTER);
    }

    /**
     * Test text access.
     */
    @Test
    public void testTextAccess() {
        KLabel label = new KLabel("Hello");
        assertEquals("Hello", label.getText());
    }

    /**
     * Test icon access.
     */
    @Test
    public void testIconAccess() {
        KLabel label = new KLabel();
        assertNotNull(label.getIcon());
    }

    /**
     * Test antialiased.
     */
    @Test
    public void testAntialiased() {
        KLabel label = new KLabel();
        assertFalse(label.isAntialiased());
        label.setAntialiased(true);
        assertTrue(label.isAntialiased());
    }
}
