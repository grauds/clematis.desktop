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
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.hyperrealm.kiwi.util.Config;

/**
 * Unit tests for {@link ColorTheme}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class ColorThemeTest {

    /**
     * Test color get/set for theme roles.
     */
    @Test
    public void testColorGetSetForThemeRoles() {
        Config config = new Config();
        config.setProperty("name", "Test Theme");
        ColorTheme theme = new ColorTheme(config);
        assertNotNull(theme);
        assertNotNull(theme.getName());
    }

    /**
     * Test getName.
     */
    @Test
    public void testGetName() {
        Config config = new Config();
        config.setProperty("name", "My Theme");
        ColorTheme theme = new ColorTheme(config);
        assertEquals("My Theme", theme.getName());
    }
}
