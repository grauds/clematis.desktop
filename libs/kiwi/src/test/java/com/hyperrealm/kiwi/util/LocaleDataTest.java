package com.hyperrealm.kiwi.util;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link LocaleData}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class LocaleDataTest {

    /**
     * Test getMessage with simple key-value.
     */
    @Test
    public void testGetMessage() {
        Map<String, String> source = new HashMap<>();
        source.put("greeting", "Hello");
        source.put("farewell", "Goodbye");
        LocaleData localeData = new LocaleData(source);
        assertEquals("Hello", localeData.getMessage("greeting"));
        assertEquals("Goodbye", localeData.getMessage("farewell"));
    }

    /**
     * Test getMessage with formatting arguments.
     */
    @Test
    public void testGetMessageWithArgs() {
        Map<String, String> source = new HashMap<>();
        source.put("greeting", "Hello, {0}!");
        LocaleData localeData = new LocaleData(source);
        String result = localeData.getMessage("greeting", "World");
        assertTrue(result.contains("World"));
    }

    /**
     * Test getMessageList.
     */
    @Test
    public void testGetMessageList() {
        Map<String, String> source = new HashMap<>();
        source.put("colors", "red,green,blue");
        LocaleData localeData = new LocaleData(source);
        String[] colors = localeData.getMessageList("colors");
        assertEquals(3, colors.length);
        assertEquals("red", colors[0]);
        assertEquals("green", colors[1]);
        assertEquals("blue", colors[2]);
    }

    /**
     * Test parent fallback.
     */
    @Test
    public void testParentFallback() {
        Map<String, String> childSource = new HashMap<>();
        childSource.put("childKey", "childValue");
        LocaleData child = new LocaleData(childSource);

        Map<String, String> parentSource = new HashMap<>();
        parentSource.put("parentKey", "parentValue");
        LocaleData parent = new LocaleData(parentSource);

        child.setParent(parent);
        assertEquals("childValue", child.getMessage("childKey"));
        assertEquals("parentValue", child.getMessage("parentKey"));
    }

    /**
     * Test ResourceNotFoundException for missing key.
     */
    @Test
    public void testMissingKeyThrows() {
        LocaleData localeData = new LocaleData(new HashMap<>());
        assertThrows(ResourceNotFoundException.class, () -> localeData.getMessage("nonexistent"));
    }

    /**
     * Test isMessageDefined.
     */
    @Test
    public void testIsMessageDefined() {
        Map<String, String> source = new HashMap<>();
        source.put("key1", "value1");
        LocaleData localeData = new LocaleData(source);
        assertTrue(localeData.isMessageDefined("key1"));
        assertFalse(localeData.isMessageDefined("nonexistent"));
    }
}
