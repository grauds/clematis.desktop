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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KPanel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KPanelTest {

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        KPanel panel = new KPanel();
        assertNotNull(panel);
    }

    /**
     * Test layout.
     */
    @Test
    public void testLayout() {
        KPanel panel = new KPanel();
        assertNotNull(panel.getLayout());
    }

    /**
     * Test with image constructor.
     */
    @Test
    public void testWithImage() {
        KPanel panel = new KPanel((java.awt.Image) null);
        assertNotNull(panel);
    }

    /**
     * Test with layout constructor.
     */
    @Test
    public void testWithLayout() {
        KPanel panel = new KPanel(new java.awt.BorderLayout());
        assertNotNull(panel);
    }

    /**
     * Test setTexture.
     */
    @Test
    public void testSetTexture() {
        KPanel panel = new KPanel();
        panel.setTexture(null);
        // Should not throw
    }

    /**
     * Test setAlpha.
     */
    @Test
    public void testSetAlpha() {
        KPanel panel = new KPanel();
        panel.setAlpha(0.5f);
        assertEquals(0.5f, panel.getAlpha(), 0.001f);
    }

    /**
     * Test setFadingEnabled.
     */
    @Test
    public void testSetFadingEnabled() {
        KPanel panel = new KPanel();
        panel.setFadingEnabled(true);
        assertTrue(panel.isFadingEnabled());
        panel.setFadingEnabled(false);
        assertFalse(panel.isFadingEnabled());
    }
}
