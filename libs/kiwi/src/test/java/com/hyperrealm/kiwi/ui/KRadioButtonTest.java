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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KRadioButton}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KRadioButtonTest {

    /**
     * Test isSelected/setSelected.
     */
    @Test
    public void testIsSelectedSetSelected() {
        KRadioButton rb = new KRadioButton("Test");
        assertFalse(rb.isSelected());
        rb.setSelected(true);
        assertTrue(rb.isSelected());
        rb.setSelected(false);
        assertFalse(rb.isSelected());
    }

    /**
     * Test constructors.
     */
    @Test
    public void testConstructors() {
        new KRadioButton("text");
        new KRadioButton("text", new ImageIcon());
        new KRadioButton(new ImageIcon());
        new KRadioButton(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {}
        });
    }
}
