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

import java.io.*;

/**
 * This class consists of several convenience routines for reading and
 * writing streams. The methods are all static.
 *
 * @author Mark Lindner
 * @since Kiwi 1.3.1
 */

public final class StreamUtils {
    /**
     * The data transfer block size.
     */
    public static final int blockSize = 4096;

    /* Private constructor */

    private StreamUtils() {
    }

    /**
     * Read all of the data from a stream, writing it to another stream. Reads
     * data from the input stream and writes it to the output stream, until no
     * more data is available.
     *
     * @param input  The input stream.
     * @param output The output stream.
     * @throws java.io.IOException If an error occurred while reading from
     *                             the stream.
     */

    public static final OutputStream readStreamToStream(InputStream input,
                                                        OutputStream output)
            throws IOException {
        byte[] buf = new byte[blockSize];
        int b;

        while ((b = input.read(buf)) > 0)
            output.write(buf, 0, b);

        return (output);
    }

    /**
     * Read all of the data from a stream, returning the contents as a
     * <code>String</code>. Note that this method is not unicode-aware.
     *
     * @param input The stream to read from.
     * @return The contents of the stream, as a <code>String</code>.
     * @throws java.io.IOException If an error occurred while reading from
     *                             the stream.
     */

    public static final String readStreamToString(InputStream input)
            throws IOException {
        return (readStream(input).toString());
    }

    /**
     * Write a string to a stream. Note that this method is not unicode-aware.
     *
     * @param s      The string to write.
     * @param output The stream to write it to.
     * @throws java.io.IOException If an error occurred while writing to the
     *                             stream.
     */

    public static final void writeStringToStream(String s, OutputStream output)
            throws IOException {
        InputStream input = new ByteArrayInputStream(s.getBytes());

        readStreamToStream(input, output);
    }

    /**
     * Read all of the data from a stream, returning the contents as a
     * <code>byte</code> array.
     *
     * @param input The stream to read from.
     * @return The contents of the stream, as a <code>byte</code> array.
     * @throws java.io.IOException If an error occurred while reading from
     *                             the stream.
     */

    public static final byte[] readStreamToByteArray(InputStream input)
            throws IOException {
        return (readStream(input).toByteArray());
    }

    /* Fully read an <code>InputStream</code>, writing the data read to a
     * <code>ByteArrayOutputStream. Returns a reference to the resulting stream.
     */

    private static final ByteArrayOutputStream readStream(InputStream input)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(blockSize);

        readStreamToStream(input, output);

        return (output);
    }

}
