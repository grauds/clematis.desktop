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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link XMLElement}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class XMLElementTest {

    /**
     * Test tag operations.
     */
    @Test
    public void testTag() {
        XMLElement elem = new XMLElement("div", false);
        assertEquals("div", elem.getTag());
        elem.setTag("P");
        assertEquals("p", elem.getTag()); // Tag is lowercased
    }

    /**
     * Test end tag.
     */
    @Test
    public void testEndTag() {
        XMLElement start = new XMLElement("div", false);
        XMLElement end = new XMLElement("div", true);
        assertFalse(start.isEnd());
        assertTrue(end.isEnd());
    }

    /**
     * Test empty tag.
     */
    @Test
    public void testEmptyTag() {
        XMLElement elem = new XMLElement("br", false);
        assertFalse(elem.isEmpty());
        elem.setEmpty(true);
        assertTrue(elem.isEmpty());
    }

    /**
     * Test toString with attributes.
     */
    @Test
    public void testToString() {
        XMLElement elem = new XMLElement("img", false);
        elem.addAttribute("src", "image.png");
        elem.addAttribute("width", "100");
        String str = elem.toString();
        assertTrue(str.contains("img"));
        assertTrue(str.contains("src=\"image.png\""));
        assertTrue(str.contains("width=\"100\""));
    }

    /**
     * Test toString with empty tag.
     */
    @Test
    public void testToStringEmpty() {
        XMLElement elem = new XMLElement("br", false);
        elem.setEmpty(true);
        String str = elem.toString();
        assertTrue(str.endsWith("/>"));
    }

    /**
     * Test toString with end tag.
     */
    @Test
    public void testToStringEnd() {
        XMLElement elem = new XMLElement("div", true);
        String str = elem.toString();
        assertEquals("</div>", str);
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        XMLElement elem = new XMLElement();
        assertEquals("", elem.getTag());
        assertFalse(elem.isEnd());
        assertFalse(elem.isEmpty());
    }

    /**
     * Test addAttribute replaces existing.
     */
    @Test
    public void testAddAttributeReplace() {
        XMLElement elem = new XMLElement("div", false);
        elem.addAttribute("class", "old");
        elem.addAttribute("class", "new");
        assertEquals("new", elem.getAttributeValue("class"));
    }

    /**
     * Test getAttributeNames.
     */
    @Test
    public void testGetAttributeNames() {
        XMLElement elem = new XMLElement("div", false);
        elem.addAttribute("a", "1");
        elem.addAttribute("b", "2");
        assertTrue(elem.getAttributeNames().hasNext());
    }
}
