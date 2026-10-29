package com.hyperrealm.kiwi.plugin;
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

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link VersionInfo}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class VersionInfoTest {

    /**
     * Test null handling for version.
     */
    @Test
    public void testNullVersion() {
        VersionInfo info = new VersionInfo(null, "123", "2024-01-01");
        assertEquals("0.0.0", info.version());
    }

    /**
     * Test trimming and uppercasing build number.
     */
    @Test
    public void testTrimmingUppercasing() {
        VersionInfo info = new VersionInfo("1.0", "  abc  ", "2024-01-01");
        assertEquals("ABC", info.buildNumber());
    }

    /**
     * Test default values.
     */
    @Test
    public void testDefaultValues() {
        VersionInfo info = new VersionInfo(null, null, null);
        assertEquals("0.0.0", info.version());
        assertEquals("LOCAL", info.buildNumber());
        assertEquals("", info.buildDate());
    }

    /**
     * Test with URL.
     */
    @Test
    public void testWithUrl() throws Exception {
        URL url = new URL("http://example.com");
        VersionInfo info = new VersionInfo("1.0", "123", "2024-01-01", url);
        assertEquals("1.0", info.version());
        assertEquals(url, info.url());
    }

    /**
     * Test with null URL.
     */
    @Test
    public void testWithNullUrl() {
        VersionInfo info = new VersionInfo("1.0", "123", "2024-01-01", null);
        assertNull(info.url());
    }

    /**
     * Test constructor with 3 args.
     */
    @Test
    public void testThreeArgConstructor() {
        VersionInfo info = new VersionInfo("1.0", "123", "2024-01-01");
        assertEquals("1.0", info.version());
        assertNull(info.url());
    }

    /**
     * Test with valid values.
     */
    @Test
    public void testValidValues() {
        VersionInfo info = new VersionInfo("2.5.1", "456", "2024-06-15");
        assertEquals("2.5.1", info.version());
        assertEquals("456", info.buildNumber());
        assertEquals("2024-06-15", info.buildDate());
    }
}
