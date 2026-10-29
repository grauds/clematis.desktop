package com.hyperrealm.kiwi.text;
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

import java.awt.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Unit tests for {@link ColorFormatter}.
 */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class ColorFormatterTest {

    /**
     * Test nameForColor for known colors.
     */
    @Test
    public void testNameForColor() {
        assertEquals("black", ColorFormatter.nameForColor(Color.black));
        assertEquals("blue", ColorFormatter.nameForColor(Color.blue));
        assertEquals("red", ColorFormatter.nameForColor(Color.red));
        assertNull(ColorFormatter.nameForColor(new Color(123, 45, 67)));
    }

    /**
     * Test colorForName for known colors.
     */
    @Test
    public void testColorForName() {
        assertEquals(Color.black, ColorFormatter.colorForName("black"));
        assertEquals(Color.blue, ColorFormatter.colorForName("blue"));
        assertEquals(Color.red, ColorFormatter.colorForName("red"));
        assertNull(ColorFormatter.colorForName("unknown"));
    }

    /**
     * Test case-insensitive colorForName.
     */
    @Test
    public void testColorForNameCaseInsensitive() {
        assertEquals(Color.red, ColorFormatter.colorForName("Red"));
        assertEquals(Color.red, ColorFormatter.colorForName("RED"));
    }

    /**
     * Test format for known colors.
     */
    @Test
    public void testFormatKnownColor() {
        assertEquals("black", ColorFormatter.format(Color.black));
        assertEquals("blue", ColorFormatter.format(Color.blue));
        assertEquals("red", ColorFormatter.format(Color.red));
    }

    /**
     * Test format for unknown colors (hex format).
     */
    @Test
    public void testFormatUnknownColor() {
        String formatted = ColorFormatter.format(new Color(123, 45, 67));
        assertTrue(formatted.startsWith("#"));
        assertEquals(7, formatted.length());
    }

    /**
     * Test parse for known colors.
     */
    @Test
    public void testParseKnownColor() throws ParsingException {
        assertEquals(Color.black, ColorFormatter.parse("black"));
        assertEquals(Color.blue, ColorFormatter.parse("blue"));
        assertEquals(Color.red, ColorFormatter.parse("red"));
    }

    /**
     * Test parse for hex colors.
     */
    @Test
    public void testParseHexColor() throws ParsingException {
        Color parsed = ColorFormatter.parse("#FF0000");
        assertEquals(255, parsed.getRed());
        assertEquals(0, parsed.getGreen());
        assertEquals(0, parsed.getBlue());
    }

    /**
     * Test format/parse round-trip.
     */
    @Test
    public void testFormatParseRoundTrip() throws ParsingException {
        Color original = Color.green;
        String formatted = ColorFormatter.format(original);
        Color parsed = ColorFormatter.parse(formatted);
        assertEquals(original, parsed);
    }

    /**
     * Test parse throws exception for unknown color.
     */
    @Test
    public void testParseUnknownColor() {
        try {
            ColorFormatter.parse("unknowncolor");
            fail(); // Should have thrown
        } catch (ParsingException e) {
            assertTrue(e.getMessage().contains("Unknown color name"));
        }
    }
}
