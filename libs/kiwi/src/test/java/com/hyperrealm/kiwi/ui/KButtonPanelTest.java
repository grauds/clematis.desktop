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

import javax.swing.AbstractButton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for {@link ButtonPanel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KButtonPanelTest {

    /**
     * Test button panel layout.
     */
    @Test
    public void testButtonPanelLayout() {
        ButtonPanel panel = new ButtonPanel();
        assertNotNull(panel);
        assertEquals(0, panel.getButtonCount());
    }

    /**
     * Test addButton/removeButton.
     */
    @Test
    public void testAddRemoveButton() {
        ButtonPanel panel = new ButtonPanel();
        KButton button = new KButton("Test");
        panel.addButton(button);
        assertEquals(1, panel.getButtonCount());
        panel.removeButton(button);
        assertEquals(0, panel.getButtonCount());
    }

    /**
     * Test removeAllButtons.
     */
    @Test
    public void testRemoveAllButtons() {
        ButtonPanel panel = new ButtonPanel();
        panel.addButton(new KButton("B1"));
        panel.addButton(new KButton("B2"));
        assertEquals(2, panel.getButtonCount());
        panel.removeAllButtons();
        assertEquals(0, panel.getButtonCount());
    }

    /**
     * Test getButton.
     */
    @Test
    public void testGetButton() {
        ButtonPanel panel = new ButtonPanel();
        KButton button = new KButton("Test");
        panel.addButton(button);

        AbstractButton retrieved = panel.getButton(0);
        assertEquals(button, retrieved);
    }
}
