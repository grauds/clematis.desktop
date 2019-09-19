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

package com.hyperrealm.kiwi.io.xdr;

import java.io.EOFException;
import java.io.IOException;

/**
 * An object for decoding XDR-encoded data directly from a byte
 * buffer. See <a href="http://www.faqs.org/rfcs/rfc1832.html">RFC
 * 1832</a> for more information. The read methods all throw
 * <code>EOFException</code> if there is not enough data remaining in the
 * buffer to satisfy a read request.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 * @link https://en.wikipedia.org/wiki/External_Data_Representation
 */
@SuppressWarnings({"unused", "magicnumber"})
public class XDRBufferReader implements XDRDataInput {

    private byte[] buffer;

    private int left;

    private int pos;

    /**
     * Construct a new <code>XDRBufferReader</code> that reads from the
     * given byte buffer. It is assumed that every byte in the buffer contains
     * significant data.
     *
     * @param buffer The byte buffer.
     */

    public XDRBufferReader(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }

    /**
     * Construct a new <code>XDRBufferReader</code> that reads from the
     * given byte buffer.
     *
     * @param buffer The byte buffer.
     * @param length The length of the data (the number of significant bytes)
     *               in the buffer.
     */

    public XDRBufferReader(byte[] buffer, int length) {
        this(buffer, 0, length);
    }

    /**
     * Construct a new <code>XDRBufferReader</code> that reads from the
     * given byte buffer.
     *
     * @param buffer The byte buffer.
     * @param offset The offset of the beginning of the data.
     * @param length The length of the data (the number of significant bytes)
     *               in the buffer.
     */

    private XDRBufferReader(byte[] buffer, int offset, int length) {
        this.buffer = buffer;
        left = length;
        pos = offset;
    }

    /**
     * Reset the buffer.
     */

    public void reset() {
        reset(0, buffer.length);
    }

    /**
     * Reset the buffer.
     *
     * @param offset The new offset.
     * @param length The new length.
     */

    public void reset(int offset, int length) {
        left = length;
        pos = offset;
    }

    /**
     * Get the number of bytes left to read from the buffer.
     */

    public int getLeft() {
        return left;
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
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;
        pos += 3;

        return ((char) (buffer[pos++] & XDRConstants.STOP_BYTE));
    }

    /**
     *
     */

    public short readShort() throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }
        left -= XDRConstants.UNIT_SIZE;
        pos += 2;

        return ((short) ((buffer[pos++] << 8) | (buffer[pos++] & XDRConstants.STOP_BYTE)));
    }

    /**
     *
     */

    public int readUnsignedShort() throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;
        pos += 2;

        return (((buffer[pos++] & XDRConstants.STOP_BYTE) << 8) | (buffer[pos++] & XDRConstants.STOP_BYTE));
    }

    /**
     *
     */

    public int readInt() throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        return (((buffer[pos++] & XDRConstants.STOP_BYTE) << 24)
                | ((buffer[pos++] & XDRConstants.STOP_BYTE) << 16)
                | ((buffer[pos++] & XDRConstants.STOP_BYTE) << 8)
                | (buffer[pos++] & XDRConstants.STOP_BYTE));
    }

    /**
     *
     */

    public long readUnsignedInt() throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        return ((((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 24)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 16)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 8)
                | ((long) (buffer[pos++] & XDRConstants.STOP_BYTE)));
    }

    /**
     *
     */

    public long readLong() throws IOException {
        if (left < (XDRConstants.UNIT_SIZE * 2)) {
            throw (new EOFException());
        }

        left -= (XDRConstants.UNIT_SIZE * 2);

        return ((((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 56)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 48)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 40)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 32)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 24)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 16)
                | (((long) (buffer[pos++] & XDRConstants.STOP_BYTE)) << 8)
                | ((long) (buffer[pos++] & XDRConstants.STOP_BYTE)));
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

    /**
     *
     */

    public String readString(int length) throws IOException {
        int len = length + getPad(length);

        if (left < len) {
            throw (new EOFException());
        }

        String s = null;

        try {
            s = new String(buffer, pos, length, XDRConstants.ASCII_ENCODING);
        } catch (java.io.UnsupportedEncodingException ex) {
            // shouldn't happen
        }

        pos += len;
        left -= len;

        return (s);
    }

    /**
     *
     */

    public String readString() throws IOException {
        int len = readInt();

        return (readString(len));
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readBoolean();
        }
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
        int len = length + getPad(length);

        if (left < len) {
            throw (new EOFException());
        }

        System.arraycopy(buffer, pos, array, 0, length);

        pos += len;
        left -= len;
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readShort();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readUnsignedShort();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readInt();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readUnsignedInt();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readLong();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readFloat();
        }
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

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            array[offsetInt++] = readDouble();
        }
    }

    /*
     */

    private int getPad(int len) {
        int pad = len % XDRConstants.UNIT_SIZE;

        return ((pad == 0) ? 0 : (XDRConstants.UNIT_SIZE - pad));
    }

}
