package com.hyperrealm.kiwi.text;
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
import java.util.Arrays;
import java.util.Base64;

/**
 * This class implements a codec for the Base-64 encoding scheme.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1.1
 */
public final class Base64Codec {

    private static final String BASE_64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static final byte PAD = (byte) '=';

    private Base64Codec() {
        // Prevent instantiation
    }

    /**
     * Determine if a byte is a valid Base-64 encoding character; one of the
     * characters: '+', '/', '=', '0' - '9', 'A' - 'Z', 'a' - 'z'.
     *
     * @param c The byte to test.
     * @return <code>true</code> if <code>c</code> is a Base-64 character,
     * <code>false</code> otherwise.
     */
    public static boolean isBase64Character(byte c) {
        return (BASE_64.indexOf((char) c) != -1) || (c == PAD);
    }

    /**
     * Encode up to 3 bytes of binary data as 4 bytes of printable ASCII text.
     * Uses the standard JDK Base64 encoder internally.
     *
     * @param output The output array.
     * @param outpos The starting offset in the output array.
     * @param input  The input array.
     * @param inpos  The starting offset in the input array.
     * @param len    The number of bytes (1, 2, or 3) of input to encode.
     */
    public static void encode(byte[] output, int outpos, byte[] input, int inpos, int len) {
        if (len <= 0) {
            return;
        }

        // Use the native encoder to encode the exact sub-array window
        byte[] encoded = Base64.getEncoder().encode(Arrays.copyOfRange(input, inpos, inpos + len));

        // Copy the resulting 4 characters directly into the designated output buffer slot
        System.arraycopy(encoded, 0, output, outpos, encoded.length);
    }

    /**
     * Decode 4 bytes of printable ASCII text into up to 3 bytes of binary data.
     * Uses the standard JDK Base64 decoder internally.
     *
     * @param output The output array.
     * @param outpos The starting offset in the output array.
     * @param input  The input array.
     * @param inpos  The starting offset in the input array.
     * @return The number of bytes (1, 2, or 3) that were decoded.
     */
    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MagicNumber"})
    public static int decode(byte[] output, int outpos, byte[] input, int inpos) {
        // Isolate the 4-byte chunk from the input stream
        byte[] chunk = Arrays.copyOfRange(input, inpos, inpos + 4);

        // Filter out any unexpected non-Base64 tokens to match legacy error behavior
        int validCount = 0;
        for (byte b : chunk) {
            if (isBase64Character(b)) {
                chunk[validCount++] = b;
            }
        }

        if (validCount == 0) {
            return 0;
        }

        try {
            // Decode the 4-byte frame using the JDK standard decoder
            byte[] decoded = Base64.getDecoder().decode(java.util.Arrays.copyOfRange(chunk, 0, validCount));
            System.arraycopy(decoded, 0, output, outpos, decoded.length);
            return decoded.length;
        } catch (IllegalArgumentException e) {
            // Fallback for corrupt tuple fragments (e.g. "0=")
            // Emulates legacy bit-shifting behaviors by extracting safe bytes
            int len = 3;
            int x = 0;
            for (int i = 0; i < 4; i++) {
                if (i > 0) {
                    x <<= 6;
                }
                byte c = input[inpos + i];
                int index = BASE_64.indexOf(c);
                if (index != -1) {
                    x |= index;
                } else {
                    len--;
                }
            }
            int copied = 0;
            for (int i = 0, n = 16; i < len; i++, n -= 8) {
                output[outpos + copied++] = (byte) ((x >> n) & 0xFF);
            }
            return copied;
        }
    }
}
