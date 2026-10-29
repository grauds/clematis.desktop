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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link StringUtils}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class StringUtilsTest {

    /**
     * Test isAlphaNumeric with alphanumeric strings.
     */
    @Test
    public void testIsAlphaNumeric() {
        assertTrue(StringUtils.isAlphaNumeric("abc123"));
        assertTrue(StringUtils.isAlphaNumeric("ABC"));
        assertTrue(StringUtils.isAlphaNumeric("123"));
        assertFalse(StringUtils.isAlphaNumeric("abc 123"));
        assertFalse(StringUtils.isAlphaNumeric("abc-123"));
        assertFalse(StringUtils.isAlphaNumeric("abc_123"));
    }

    /**
     * Test split method.
     */
    @Test
    public void testSplit() {
        String[] result = StringUtils.split("a,b,c", ",");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);

        String[] result2 = StringUtils.split("one two three", " ");
        assertArrayEquals(new String[]{"one", "two", "three"}, result2);

        String[] result3 = StringUtils.split("a:b:c", ":");
        assertArrayEquals(new String[]{"a", "b", "c"}, result3);
    }

    /**
     * Test join method.
     */
    @Test
    public void testJoin() {
        String result = StringUtils.join(new String[]{"a", "b", "c"}, ",");
        assertEquals("a,b,c", result);

        String result2 = StringUtils.join(new String[]{"one", "two"}, " - ");
        assertEquals("one - two", result2);
    }

    /**
     * Test uppercaseFirst method.
     */
    @Test
    public void testUppercaseFirst() {
        assertEquals("Hello", StringUtils.uppercaseFirst("hello"));
        assertEquals("Hello", StringUtils.uppercaseFirst("Hello"));
        assertEquals("A", StringUtils.uppercaseFirst("a"));
        assertNull(StringUtils.uppercaseFirst(null));
        assertEquals("", StringUtils.uppercaseFirst(""));
    }

    /**
     * Test parseIntArray method.
     */
    @Test
    public void testParseIntArray() {
        int[] array = new int[5];
        int[] result = StringUtils.parseIntArray("1,2,3", array, -1);
        assertArrayEquals(new int[]{1, 2, 3, -1, -1}, result);

        int[] array2 = new int[3];
        int[] result2 = StringUtils.parseIntArray("10,20,30,40,50", array2, 0);
        assertArrayEquals(new int[]{10, 20, 30}, result2);

        int[] array3 = new int[3];
        int[] result3 = StringUtils.parseIntArray("a,b,c", array3, -1);
        assertArrayEquals(new int[]{-1, -1, -1}, result3);
    }

    /**
     * Test getClassName method.
     */
    @Test
    public void testGetClassName() {
        assertEquals("String", StringUtils.getClassName(String.class));
        assertEquals("Integer", StringUtils.getClassName(Integer.class));
        assertEquals("StringUtils", StringUtils.getClassName(StringUtils.class));
    }

    /**
     * Test wordBreak method.
     */
    @Test
    public void testWordBreak() {
        String[] words = StringUtils.wordBreak("hello world", '"');
        assertArrayEquals(new String[]{"hello", "world"}, words);

        String[] quoted = StringUtils.wordBreak("say \"hello world\"", '"');
        assertArrayEquals(new String[]{"say", "hello world"}, quoted);

        assertNull(StringUtils.wordBreak(null, '"'));
    }

    /**
     * Test justify method.
     */
    @Test
    public void testJustify() {
        String result = StringUtils.justify("one two three four five", 10);
        assertNotNull(result);
        assertTrue(result.contains("\n"));
    }
}
