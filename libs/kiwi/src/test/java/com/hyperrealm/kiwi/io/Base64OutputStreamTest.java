package com.hyperrealm.kiwi.io;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link Base64OutputStream}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class Base64OutputStreamTest {

    /**
     * Test encoding a simple string.
     */
    @Test
    public void testEncodeSimpleString() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        String input = "Hello";
        for (int i = 0; i < input.length(); i++) {
            b64out.write(input.charAt(i));
        }
        b64out.flush();
        String encoded = out.toString();
        assertEquals("SGVsbG8=\n", encoded);
    }

    /**
     * Test encoding a single byte.
     */
    @Test
    public void testEncodeSingleByte() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        b64out.write(65); // 'A'
        b64out.flush();
        String encoded = out.toString();
        assertEquals("QQ==\n", encoded);
    }

    /**
     * Test encoding two bytes.
     */
    @Test
    public void testEncodeTwoBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        b64out.write(72); // 'H'
        b64out.write(101); // 'e'
        b64out.flush();
        String encoded = out.toString();
        assertEquals("SGU=\n", encoded);
    }

    /**
     * Test encoding three bytes.
     */
    @Test
    public void testEncodeThreeBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        b64out.write(72); // 'H'
        b64out.write(101); // 'e'
        b64out.write(108); // 'l'
        b64out.flush();
        String encoded = out.toString();
        assertEquals("SGVs\n", encoded);
    }

    /**
     * Test that flush writes remaining data.
     */
    @Test
    public void testFlushWritesRemaining() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        b64out.write(65); // Only one byte, not a full chunk
        b64out.flush();
        String encoded = out.toString();
        assertFalse(encoded.isEmpty());
        assertTrue(encoded.contains("QQ=="));
    }

    /**
     * Test 64-column wrapping behavior.
     */
    @Test
    public void testSixtyFourColumnWrapping() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        // Write enough bytes to trigger wrapping (18 tuples per line = 54 bytes)
        for (int i = 0; i < 55; i++) {
            b64out.write(65);
        }
        b64out.flush();
        String encoded = out.toString();
        // Should have at least one newline from wrapping
        assertTrue(encoded.contains("\n"));
    }

    /**
     * Test round-trip encode/decode.
     */
    @Test
    public void testRoundTrip() throws IOException {
        String original = "The quick brown fox jumps over the lazy dog";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        for (int i = 0; i < original.length(); i++) {
            b64out.write(original.charAt(i));
        }
        b64out.flush();

        // Decode using Base64Codec
        String encoded = out.toString();
        String stripped = encoded.replace("\n", "");
        byte[] encodedBytes = stripped.getBytes();
        byte[] decoded = new byte[encodedBytes.length * 3 / 4];
        int pos = 0;
        for (int i = 0; i < encodedBytes.length; i += 4) {
            pos += com.hyperrealm.kiwi.text.Base64Codec.decode(decoded, pos, encodedBytes, i);
        }
        assertEquals(original, new String(decoded, 0, pos));
    }

    /**
     * Test encoding binary data.
     */
    @Test
    public void testBinaryData() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        // Write binary data with various byte values
        for (int i = 0; i < 256; i++) {
            b64out.write(i);
        }
        b64out.flush();
        String encoded = out.toString();
        assertFalse(encoded.isEmpty());
    }

    /**
     * Test encoding zero bytes.
     */
    @Test
    public void testZeroBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream b64out = new Base64OutputStream(out);
        b64out.write(0);
        b64out.write(0);
        b64out.write(0);
        b64out.flush();
        String encoded = out.toString();
        assertEquals("AAAA\n", encoded);
    }
}
