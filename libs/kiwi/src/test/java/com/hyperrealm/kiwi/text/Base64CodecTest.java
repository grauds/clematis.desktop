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
 * Unit tests for {@link Base64Codec}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class Base64CodecTest {

    /**
     * Test encode/decode a round-trip for a simple string.
     */
    @Test
    public void testEncodeDecodeRoundTrip() {
        String input = "Hello, World!";
        byte[] inputBytes = input.getBytes();
        byte[] encoded = new byte[256];
        Base64Codec.encode(encoded, 0, inputBytes, 0, inputBytes.length);
        String encodedStr = new String(encoded, 0, (inputBytes.length + 2) / 3 * 4);

        byte[] decoded = new byte[256];
        int decodedLen = 0;
        for (int i = 0; i < encodedStr.length(); i += 4) {
            int end = Math.min(i + 4, encodedStr.length());
            byte[] chunk = encodedStr.substring(i, end).getBytes();
            decodedLen += Base64Codec.decode(decoded, decodedLen, chunk, 0);
        }
        assertEquals(input, new String(decoded, 0, decodedLen));
    }

    /**
     * Test encoding single byte.
     */
    @Test
    public void testEncodeSingleByte() {
        byte[] input = new byte[]{(byte) 65};
        byte[] output = new byte[4];
        Base64Codec.encode(output, 0, input, 0, 1);
        String result = new String(output, 0, 4);
        assertTrue(result.startsWith("QQ"));
    }

    /**
     * Test encoding two bytes.
     */
    @Test
    public void testEncodeTwoBytes() {
        byte[] input = new byte[]{(byte) 72, (byte) 101};
        byte[] output = new byte[4];
        Base64Codec.encode(output, 0, input, 0, 2);
        String result = new String(output, 0, 4);
        assertTrue(result.startsWith("SGU"));
    }

    /**
     * Test encoding three bytes.
     */
    @Test
    public void testEncodeThreeBytes() {
        byte[] input = new byte[]{(byte) 72, (byte) 101, (byte) 108};
        byte[] output = new byte[4];
        Base64Codec.encode(output, 0, input, 0, 3);
        String result = new String(output, 0, 4);
        assertEquals("SGVs", result);
    }

    /**
     * Test isBase64Character.
     */
    @Test
    public void testIsBase64Character() {
        assertTrue(Base64Codec.isBase64Character((byte) 'A'));
        assertTrue(Base64Codec.isBase64Character((byte) 'a'));
        assertTrue(Base64Codec.isBase64Character((byte) '0'));
        assertTrue(Base64Codec.isBase64Character((byte) '+'));
        assertTrue(Base64Codec.isBase64Character((byte) '/'));
        assertTrue(Base64Codec.isBase64Character((byte) '='));
        assertFalse(Base64Codec.isBase64Character((byte) ' '));
        assertFalse(Base64Codec.isBase64Character((byte) '\n'));
    }

    /**
     * Test edge cases with empty input.
     */
    @Test
    public void testEdgeCases() {
        byte[] output = new byte[4];
        Base64Codec.encode(output, 0, new byte[0], 0, 0);
        // Should produce padding characters
    }
}
