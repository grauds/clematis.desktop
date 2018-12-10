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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//

import com.hyperrealm.kiwi.text.Base64Codec;

/**
 * This class implements a Base-64 encoding filter. It accepts binary data
 * written to it and writes the encoded form of the data to its output
 * stream. To encode a file in Base-64, one would do something like the
 * following:
 * <p>
 * <pre>
 * File f = new File("plain.txt");
 * Base64OutputStream r64out = new Base64OutputStream(System.out);
 * FileInputStream fin = new FileInputStream(f);
 * int b;
 * while((b = f.read()) >= 0)
 *   r64out.write(b);
 * </pre>
 * <p>
 * The encoded data in this case will be written to standard output.
 * <p>
 * This filter generates 64-column-wide output with a newline character after
 * each line.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1.1
 * @deprecated
 * @see org.apache.commons.codec.binary.Base64OutputStream
 */
public class Base64OutputStream extends FilterOutputStream {

    private static final int TEXT_CHUNK_TO_ENCODE_LENGTH = 3;

    private static final int TEXT_CHUNK_ENCODED_LENGTH = 4;

    private byte[] buf = new byte[TEXT_CHUNK_TO_ENCODE_LENGTH], bufx = new byte[TEXT_CHUNK_ENCODED_LENGTH];

    private int c = 0, w = Base64Codec.TUPLES_PER_LINE;

    /**
     * Construct a new <code>Base64OutputStream</code> to filter the given
     * output stream.
     *
     * @param out The <code>OutputStream</code> to filter.
     */

    public Base64OutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Write a byte to the output stream.
     *
     * @param b The byte to encode and write.
     */

    public void write(int b) throws IOException {
        buf[c++] = (byte) b;
        if (c == TEXT_CHUNK_TO_ENCODE_LENGTH) {
            // encode this 3-byte sequence into a 4-char string & dump it out

            Base64Codec.encode(bufx, 0, buf, 0, c);

            out.write(bufx);

            c = 0;
            if (--w == 0) {
                out.write('\n');
                w = Base64Codec.TUPLES_PER_LINE;
            }
        }
    }

    /**
     * Flush the output stream. Write any remaining data in the encode buffer
     * to the output stream.
     */

    public void flush() throws IOException {
        if (c > 0) {
            Base64Codec.encode(bufx, 0, buf, 0, c);
            out.write(bufx);
            out.write('\n');
        }

        c = 0;
    }

}
