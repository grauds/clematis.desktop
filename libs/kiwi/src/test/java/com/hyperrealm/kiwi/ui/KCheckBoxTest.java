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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KCheckBox}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KCheckBoxTest {

    /**
     * Test isSelected/setSelected.
     */
    @Test
    public void testIsSelectedSetSelected() {
        KCheckBox cb = new KCheckBox("Test");
        assertFalse(cb.isSelected());
        cb.setSelected(true);
        assertTrue(cb.isSelected());
        cb.setSelected(false);
        assertFalse(cb.isSelected());
    }

    /**
     * Test constructor variants.
     */
    @Test
    public void testConstructorVariants() {
        new KCheckBox("text");
        new KCheckBox("text", new javax.swing.ImageIcon());
        new KCheckBox(new javax.swing.ImageIcon());
        new KCheckBox(new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
        });
    }

    /**
     * Test text/icon access.
     */
    @Test
    public void testTextIconAccess() {
        KCheckBox cb = new KCheckBox("Test");
        assertEquals("Test", cb.getText());
    }
}
