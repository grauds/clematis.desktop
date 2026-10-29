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

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KButton}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KButtonTest {

    /**
     * Test isRepeating/setRepeating.
     */
    @Test
    public void testIsRepeatingSetRepeating() {
        KButton button = new KButton("Test");
        assertFalse(button.isRepeating());
        button.setRepeating(true);
        assertTrue(button.isRepeating());
        button.setRepeating(false);
        assertFalse(button.isRepeating());
    }

    /**
     * Test setInitialDelay.
     */
    @Test
    public void testSetInitialDelay() {
        KButton button = new KButton("Test");
        assertEquals(300, button.getInitialDelay());
        button.setInitialDelay(500);
        assertEquals(500, button.getInitialDelay());
        // Negative values clipped to 0
        button.setInitialDelay(-10);
        assertEquals(0, button.getInitialDelay());
    }

    /**
     * Test setRepeatDelay.
     */
    @Test
    public void testSetRepeatDelay() {
        KButton button = new KButton("Test");
        assertEquals(60, button.getRepeatDelay());
        button.setRepeatDelay(100);
        assertEquals(100, button.getRepeatDelay());
        // Negative values clipped to 0
        button.setRepeatDelay(-5);
        assertEquals(0, button.getRepeatDelay());
    }

    /**
     * Test constructors.
     */
    @Test
    public void testConstructors() {
        new KButton("text");
        new KButton("text", new ImageIcon());
        new KButton(new ImageIcon());
        new KButton(new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
        });
    }
}
