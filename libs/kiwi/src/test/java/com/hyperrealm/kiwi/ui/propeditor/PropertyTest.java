package com.hyperrealm.kiwi.ui.propeditor;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link Property}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class PropertyTest {

    /**
     * Test name.
     */
    @Test
    public void testName() {
        PropertyType type = new TextPropertyType();
        Property prop = new Property("testName", type);
        assertEquals("testName", prop.getName());
    }

    /**
     * Test type.
     */
    @Test
    public void testType() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("test", type);
        assertEquals(type, prop.getType());
    }

    /**
     * Test value.
     */
    @Test
    public void testValue() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("test", type, "value");
        assertEquals("value", prop.getValue());
        prop.setValue("new value");
        assertEquals("new value", prop.getValue());
    }

    /**
     * Test icon.
     */
    @Test
    public void testIcon() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("test", type);
        assertNotNull(prop.getIcon());
    }

    /**
     * Test editable.
     */
    @Test
    public void testEditable() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("test", type);
        assertTrue(prop.isEditable());
        prop.setEditable(false);
        assertFalse(prop.isEditable());
    }

    /**
     * Test hasValue.
     */
    @Test
    public void testHasValue() {
        TextPropertyType type = new TextPropertyType();
        Property prop1 = new Property("test", type);
        assertFalse(prop1.hasValue());
        Property prop2 = new Property("test", type, "value");
        assertTrue(prop2.hasValue());
    }

    /**
     * Test toString.
     */
    @Test
    public void testToString() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("name", type, "value");
        String str = prop.toString();
        assertTrue(str.contains("name"));
        assertTrue(str.contains("value"));
    }

    /**
     * Test toString without value.
     */
    @Test
    public void testToStringWithoutValue() {
        TextPropertyType type = new TextPropertyType();
        Property prop = new Property("name", type);
        String str = prop.toString();
        assertEquals("name", str);
    }
}
