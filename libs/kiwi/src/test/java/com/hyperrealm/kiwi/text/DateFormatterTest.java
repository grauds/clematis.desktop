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

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DateFormatter}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class DateFormatterTest {

    /**
     * Test format with simple pattern.
     */
    @Test
    public void testFormatSimplePattern() {
        DateFormatter df = new DateFormatter("%Y-%m-%d");
        String result = df.format(new Date());
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test format with time pattern.
     */
    @Test
    public void testFormatTimePattern() {
        DateFormatter df = new DateFormatter("%H:%M:%S");
        String result = df.format(new Date());
        assertNotNull(result);
        assertTrue(result.contains(":"));
    }

    /**
     * Test format with combined pattern.
     */
    @Test
    public void testFormatCombinedPattern() {
        DateFormatter df = new DateFormatter("%Y-%m-%d %H:%M:%S");
        String result = df.format(new Date());
        assertNotNull(result);
        assertTrue(result.length() > 10);
    }

    /**
     * Test parse with simple pattern.
     */
    @Test
    public void testParseSimplePattern() throws ParseException {
        DateFormatter df = new DateFormatter("%Y-%m-%d");
        String formatted = df.format(new Date());
        Date parsed = df.parse(formatted);
        assertNotNull(parsed);
    }

    /**
     * Test format/parse round-trip.
     */
    @Test
    public void testFormatParseRoundTrip() throws ParseException {
        DateFormatter df = new DateFormatter("%Y-%m-%d");
        Date original = new Date();
        String formatted = df.format(original);
        Date parsed = df.parse(formatted);
        // Year, month, day should match
        assertTrue(Math.abs(original.getTime() - parsed.getTime()) < 86400000);
    }

    /**
     * Test invalid pattern throws exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPattern() {
        new DateFormatter("%X");
    }

    /**
     * Test format with Calendar.
     */
    @Test
    public void testFormatWithCalendar() {
        DateFormatter df = new DateFormatter("%Y");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String result = df.format(cal);
        assertNotNull(result);
    }

    /**
     * Test default format (no args).
     */
    @Test
    public void testFormatDefault() {
        DateFormatter df = new DateFormatter("%Y-%m-%d");
        String result = df.format();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
