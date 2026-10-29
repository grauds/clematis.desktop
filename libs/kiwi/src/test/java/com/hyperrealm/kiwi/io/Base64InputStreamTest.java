package com.hyperrealm.kiwi.io;
/* ----------------------------------------------------------------------------
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
   ----------------------------------------------------------------------------
*/
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link Base64InputStream}.
 * Tests Base-64 decoding from an input stream.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class Base64InputStreamTest {

    /**
     * Tests decoding a simple Base-64 encoded string.
     * Verifies that "SGVsbG8=" decodes to "Hello".
     */
    @Test
    public void testDecodeSimpleString() throws IOException {
        // Arrange - "Hello" encoded in Base64
        String encoded = "SGVsbG8=";
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded.getBytes());
        Base64InputStream bis = new Base64InputStream(bais);

        // Act - read all bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            baos.write(b);
        }
        bis.close();

        // Assert - decoded bytes should be "Hello"
        byte[] expected = "Hello".getBytes();
        assertArrayEquals(expected, baos.toByteArray());
    }

    /**
     * Tests decoding a longer Base-64 encoded string.
     * Verifies that "V29ybGQ=" decodes to "World".
     */
    @Test
    public void testDecodeLongerString() throws IOException {
        // Arrange - "World" encoded in Base64
        String encoded = "V29ybGQ=";
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded.getBytes());
        Base64InputStream bis = new Base64InputStream(bais);

        // Act
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            baos.write(b);
        }
        bis.close();

        // Assert
        byte[] expected = "World".getBytes();
        assertArrayEquals(expected, baos.toByteArray());
    }

    /**
     * Tests decoding a string with multiple Base-64 chunks.
     * "Hello World" encoded in Base64.
     */
    @Test
    public void testDecodeMultiChunkString() throws IOException {
        // Arrange - "Hello World" encoded in Base64
        String encoded = "SGVsbG8gV29ybGQ=";
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded.getBytes());
        Base64InputStream bis = new Base64InputStream(bais);

        // Act
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            baos.write(b);
        }
        bis.close();

        // Assert
        byte[] expected = "Hello World".getBytes();
        assertArrayEquals(expected, baos.toByteArray());
    }

    /**
     * Tests that non-Base64 characters in the input are ignored.
     */
    @Test
    public void testNonBase64CharactersIgnored() throws IOException {
        // Arrange - Base64 with random non-Base64 characters interspersed
        // Hello SGVsbG8
        String encoded = "S!G@V#s$b$%G&8=";
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded.getBytes());
        Base64InputStream bis = new Base64InputStream(bais);

        // Act
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            baos.write(b);
        }
        bis.close();

        // Assert - should decode to "Hello" ignoring non-Base64 chars
        byte[] expected = "Hello".getBytes();
        assertArrayEquals(expected, baos.toByteArray());
    }

    /**
     * Tests reading with the block read method.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testBlockRead() throws IOException {
        // Arrange - "Hi" encoded in Base64 = "SGk="
        String encoded = "SGk=";
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded.getBytes());
        Base64InputStream bis = new Base64InputStream(bais);

        // Act - read in blocks
        byte[] buffer = new byte[10];
        int bytesRead = bis.read(buffer, 0, 5);

        // Assert
        assertTrue(bytesRead > 0);
        String decoded = new String(buffer, 0, bytesRead);
        assertEquals("Hi", decoded);

        bis.close();
    }

    /**
     * Tests that reading from an empty stream returns -1.
     */
    @Test
    public void testEmptyStreamReturnsNegativeOne() throws IOException {
        // Arrange
        byte[] empty = new byte[0];
        ByteArrayInputStream bais = new ByteArrayInputStream(empty);
        Base64InputStream bis = new Base64InputStream(bais);

        // Act & Assert
        int b = bis.read();
        assertEquals(-1, b);

        bis.close();
    }

    /**
     * Tests round-trip encoding and decoding.
     * Encodes "Test123" and then decodes it back.
     */
    @Test
    public void testRoundTrip() throws IOException {
        // Arrange - original string
        String original = "Test123";
        byte[] originalBytes = original.getBytes();

        // First, encode it using Base64OutputStream
        ByteArrayOutputStream encodedStream = new ByteArrayOutputStream();
        Base64OutputStream bos = new Base64OutputStream(encodedStream);
        bos.write(originalBytes);
        bos.flush();
        bos.close();

        // Now decode it using Base64InputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(encodedStream.toByteArray());
        Base64InputStream bis = new Base64InputStream(bais);

        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            decodedStream.write(b);
        }
        bis.close();

        // Assert - decoded should match original
        assertArrayEquals(originalBytes, decodedStream.toByteArray());
    }

    /**
     * Tests decoding binary data (not ASCII text).
     */
    @Test
    public void testDecodeBinaryData() throws IOException {
        // Arrange - encode some binary data
        byte[] original = new byte[]{0x00, 0x01, 0x02};

        // Encode
        ByteArrayOutputStream encodedStream = new ByteArrayOutputStream();
        Base64OutputStream bos = new Base64OutputStream(encodedStream);
        bos.write(original);
        bos.flush();
        bos.close();

        // Decode
        ByteArrayInputStream bais = new ByteArrayInputStream(encodedStream.toByteArray());
        Base64InputStream bis = new Base64InputStream(bais);

        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            decodedStream.write(b);
        }
        bis.close();

        // Assert
        assertArrayEquals(original, decodedStream.toByteArray());
    }
}
