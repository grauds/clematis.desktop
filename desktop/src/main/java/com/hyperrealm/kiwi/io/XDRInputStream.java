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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * An input stream for decoding data from the XDR encoding format. See
 * <a href="http://www.faqs.org/rfcs/rfc1832.html">RFC 1832</a> for more
 * information.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class XDRInputStream extends FilterInputStream implements XDRDataInput {
    private static final int UNIT_SIZE = 4;
    byte[] unitbuf = new byte[UNIT_SIZE];
    byte[] unit2buf = new byte[UNIT_SIZE * 2];

    /**
     * Construct a new <code>XDRInputStream</code> that wraps the given
     * stream.
     *
     * @param in The <code>InputStream</code> to wrap.
     */
    public XDRInputStream(InputStream in) {
        super(in);
    }

    /**
     *
     */

    public boolean readBoolean() throws IOException {
        return (readInt() != 0);
    }

    /**
     *
     */

    public char readChar() throws IOException {
        readFully(unitbuf);

        return ((char) (unitbuf[3] & 0xFF));
    }

    /**
     *
     */

    public short readShort() throws IOException {
        readFully(unitbuf);

        return ((short) ((unitbuf[2] << 8) | (unitbuf[3] & 0xFF)));
    }

    /**
     *
     */

    public int readUnsignedShort() throws IOException {
        readFully(unitbuf);

        return (((unitbuf[2] & 0xFF) << 8) | (unitbuf[3] & 0xFF));
    }

    /**
     *
     */

    public int readInt() throws IOException {
        readFully(unitbuf);

        return (((unitbuf[0] & 0xFF) << 24)
                | ((unitbuf[1] & 0xFF) << 16)
                | ((unitbuf[2] & 0xFF) << 8)
                | (unitbuf[3] & 0xFF));
    }

    /**
     *
     */

    public long readUnsignedInt() throws IOException {
        readFully(unitbuf);

        return ((((long) (unitbuf[0] & 0xFF)) << 24)
                | (((long) (unitbuf[1] & 0xFF)) << 16)
                | (((long) (unitbuf[2] & 0xFF)) << 8)
                | ((long) (unitbuf[3] & 0xFF)));
    }

    /**
     *
     */

    public long readLong() throws IOException {
        readFully(unit2buf);

        return ((((long) (unit2buf[0] & 0xFF)) << 56)
                | (((long) (unit2buf[1] & 0xFF)) << 48)
                | (((long) (unit2buf[2] & 0xFF)) << 40)
                | (((long) (unit2buf[3] & 0xFF)) << 32)
                | (((long) (unit2buf[4] & 0xFF)) << 24)
                | (((long) (unit2buf[5] & 0xFF)) << 16)
                | (((long) (unit2buf[6] & 0xFF)) << 8)
                | ((long) (unit2buf[7] & 0xFF)));
    }

    /**
     *
     */

    public float readFloat() throws IOException {
        return (Float.intBitsToFloat(readInt()));
    }

    /**
     *
     */

    public double readDouble() throws IOException {
        return (Double.longBitsToDouble(readLong()));
    }

    /*
     */

    private void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    /*
     */

    private void readFully(byte[] b, int offset, int length) throws IOException {
        while (length > 0) {
            // in.read will block until some data is available.
            int r = in.read(b, offset, length);
            if (r < 0)
                throw new EOFException();
            length -= r;
            offset += r;
        }
    }

    /**
     *
     */

    private void align(int length) throws IOException {
        int pad = length % UNIT_SIZE;

        if (pad != 0)
            readFully(unitbuf, 0, UNIT_SIZE - pad);
    }

    /**
     *
     */

    public String readString(int length) throws IOException {
        byte[] ascii = new byte[length];
        readFully(ascii);

        align(length);

        return (new String(ascii, StandardCharsets.US_ASCII));
    }

    /**
     *
     */

    public String readString() throws IOException {
        int length = readInt();

        return (readString(length));
    }

    /**
     *
     */

    public boolean[] readBooleanArray() throws IOException {
        int len = readInt();
        boolean[] array = new boolean[len];

        readBooleanVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readBooleanVector(boolean[] array) throws IOException {
        readBooleanVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readBooleanVector(boolean[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readBoolean();
    }

    /**
     *
     */

    public byte[] readByteArray() throws IOException {
        int len = readInt();
        byte[] array = new byte[len];

        readByteVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readByteVector(byte[] array) throws IOException {
        readByteVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readByteVector(byte[] array, int offset, int length)
            throws IOException {
        readFully(array, offset, length);

        align(length);
    }

    /**
     *
     */

    public short[] readShortArray() throws IOException {
        int len = readInt();
        short[] array = new short[len];

        readShortVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readShortVector(short[] array) throws IOException {
        readShortVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readShortVector(short[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readShort();
    }

    /**
     *
     */

    public int[] readUnsignedShortArray() throws IOException {
        int len = readInt();
        int[] array = new int[len];

        readUnsignedShortVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readUnsignedShortVector(int[] array) throws IOException {
        readUnsignedShortVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readUnsignedShortVector(int[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readUnsignedShort();
    }

    /**
     *
     */

    public int[] readIntArray() throws IOException {
        int len = readInt();
        int[] array = new int[len];

        readIntVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readIntVector(int[] array) throws IOException {
        readIntVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readIntVector(int[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readInt();
    }

    /**
     *
     */

    public long[] readUnsignedIntArray() throws IOException {
        int len = readInt();
        long[] array = new long[len];

        readUnsignedIntVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readUnsignedIntVector(long[] array) throws IOException {
        readUnsignedIntVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readUnsignedIntVector(long[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readUnsignedInt();
    }

    /**
     *
     */

    public long[] readLongArray() throws IOException {
        int len = readInt();
        long[] array = new long[len];

        readLongVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readLongVector(long[] array) throws IOException {
        readLongVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readLongVector(long[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readLong();
    }

    /**
     *
     */

    public float[] readFloatArray() throws IOException {
        int len = readInt();
        float[] array = new float[len];

        readFloatVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readFloatVector(float[] array) throws IOException {
        readFloatVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readFloatVector(float[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readFloat();
    }

    /**
     *
     */

    public double[] readDoubleArray() throws IOException {
        int len = readInt();
        double[] array = new double[len];

        readDoubleVector(array, 0, len);

        return (array);
    }

    /**
     *
     */

    public void readDoubleVector(double[] array) throws IOException {
        readDoubleVector(array, 0, array.length);
    }

    /**
     *
     */

    public void readDoubleVector(double[] array, int offset, int length)
            throws IOException {
        for (int i = 0; i < length; i++)
            array[offset++] = readDouble();
    }

}
