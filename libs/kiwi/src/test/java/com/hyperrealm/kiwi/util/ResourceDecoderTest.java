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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for {@link ResourceDecoder}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class ResourceDecoderTest {

    /**
     * Test decodeString.
     */
    @Test
    public void testDecodeString() throws IOException {
        ResourceDecoder decoder = new ResourceDecoder();
        String text = "Hello, World!";
        ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes());
        String result = decoder.decodeString(stream);
        assertEquals(text, result);
    }

    /**
     * Test decodeProperties.
     */
    @Test
    public void testDecodeProperties() throws IOException {
        ResourceDecoder decoder = new ResourceDecoder();
        String propsText = "key1=value1\nkey2=value2\n";
        ByteArrayInputStream stream = new ByteArrayInputStream(propsText.getBytes());
        Properties props = decoder.decodeProperties(stream);
        assertEquals("value1", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
    }

    /**
     * Test decodeConfig.
     */
    @Test
    public void testDecodeConfig() throws IOException {
        ResourceDecoder decoder = new ResourceDecoder();
        String configText = "key1=value1\nkey2=value2\n";
        ByteArrayInputStream stream = new ByteArrayInputStream(configText.getBytes());
        Config config = decoder.decodeConfig(stream);
        assertNotNull(config);
        assertEquals("value1", config.getProperty("key1"));
        assertEquals("value2", config.getProperty("key2"));
    }
}
