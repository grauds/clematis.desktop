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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for {@link KLabelArea}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KLabelAreaTest {

    /**
     * Test label area management.
     */
    @Test
    public void testLabelAreaManagement() {
        KLabelArea area = new KLabelArea("Test text", 5, 20);
        assertNotNull(area);
        assertFalse(area.isFocusable());
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        new KLabelArea("text", 5, 20);
        new KLabelArea(5, 20);
    }

    /**
     * Test setEditable.
     */
    @Test
    public void testSetEditable() {
        KLabelArea area = new KLabelArea(1, 1);
        assertFalse(area.isEditable());
    }
}
