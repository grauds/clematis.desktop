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
import org.junit.Test;

/**
 * Unit tests for {@link KTextArea}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KTextAreaTest {

    /**
     * Test getText/setText.
     */
    @Test
    public void testGetTextSetText() {
        KTextArea area = new KTextArea();
        area.setText("Hello");
        assertEquals("Hello", area.getText());
        area.setText("World");
        assertEquals("World", area.getText());
    }

    /**
     * Test line access.
     */
    @Test
    public void testLineAccess() {
        KTextArea area = new KTextArea("Line1\nLine2\nLine3");
        assertEquals(3, area.getLineCount());
    }

    /**
     * Test constructors.
     */
    @Test
    public void testConstructors() {
        new KTextArea();
        new KTextArea("text");
        new KTextArea(5, 20);
        new KTextArea("text", 5, 20);
    }

    /**
     * Test setEditable.
     */
    @Test
    public void testSetEditable() {
        KTextArea area = new KTextArea();
        area.setEditable(false);
        assertFalse(area.isEditable());
    }
}
