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

package com.hyperrealm.kiwi.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

// ---

import com.hyperrealm.kiwi.text.Base64Codec;

/**
 * This class implements a Base-64 decoding filter. It reads Base-64 encoded
 * data from its input stream and outputs the original (decoded) form of the
 * data. To decode a file encoded in Base-64, one would do something like
 * the following:
 * <p>
 * <pre>
 * File f = new File("encoded.txt");
 * Base64InputStream r64in = new Base64InputStream(new FileInputStream(f));
 * </pre>
 * <p>
 * The decoded data may then be obtained by reading from <code>r64in</code>.
 * <p>
 * This filter disregards any non-Base-64 characters in the input. Base-64
 * characters include '/', '+', '=', 'A' - 'Z', '0' - '9', and 'a' - 'z'.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1.1
 * @deprecated
 * @see org.apache.commons.codec.binary.Base64InputStream
 */
public class Base64InputStream extends FilterInputStream {

    private static final int BUFFER_SIZE = 3;

    private static final int XBUFFER_SIZE = 4;

    private byte[] buf = new byte[BUFFER_SIZE];

    private byte[] bufx = new byte[XBUFFER_SIZE];

    private int c = -1, p = 0;

    /**
     * Construct a new <code>Base64InputStream</code> to filter the given
     * input stream.
     *
     * @param in The <code>InputStream</code> to filter.
     */

    public Base64InputStream(InputStream in) {
        super(in);
    }

    /**
     * Read a byte from the input stream.
     *
     * @return The next (decoded) byte.
     * @throws java.io.IOException If an I/O error occurs.
     */

    public int read() throws IOException {
        // if the decode buffer is empty, fill it

        if (c < 0) {
            // read, but ignore non-radix-64 characters

            int ct = 0;
            for (;;) {
                int r = in.read();
                if (r < 0) {
                    return (-1);
                }

                byte br = (byte) r;

                if (!Base64Codec.isBase64Character(br)) {
                    continue;
                }

                bufx[ct] = br;
                if (++ct == XBUFFER_SIZE) {
                    break;
                }
            }

            // decode buffer

            p = Base64Codec.decode(bufx, 0, buf, 0);
            c = 0;
        }

        // return next byte from buffer

        byte r = buf[c];
        if (++c == p) {
            c = -1;
        }

        return r;
    }

    /**
     * Read a block of data from the input stream.
     *
     * @param b   The byte array to read into.
     * @param off The array offset at which to start storing data.
     * @param len The number of bytes to read.
     * @return The number of bytes read.
     * @throws java.io.IOException If an I/O error occurs.     */

    public int read(byte[] b, int off, int len) throws IOException {

        int i;
        int offset = off;

        for (i = 0; i < len; i++) {
            int r = read();
            if (r < 0) {
                break;
            }

            b[offset++] = (byte) r;
        }

        return (i == 0 ? -1 : i);
    }

}
