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
import java.nio.charset.StandardCharsets;

/**
 * An object for encoding XDR-encoded data directly to a byte buffer. See
 * <a href="http://www.faqs.org/rfcs/rfc1832.html">RFC 1832</a> for more
 * information. The write methods all throw
 * <code>EOFException</code> if there is not enough space remaining in the
 * buffer to satisfy a write request.
 *
 * @author Mark Lindner
 * @link https://en.wikipedia.org/wiki/External_Data_Representation
 * @since Kiwi 2.0
 */
@SuppressWarnings({"unused", "checkstyle:magicnumber", "CheckStyle"})
public class XDRBufferWriter implements XDRDataOutput {
    private byte[] buffer;
    private int left;
    private int pos;

    /**
     * Construct a new <code>XDRBufferWriter</code> that writes to the
     * given byte buffer.
     *
     * @param buffer The byte buffer.
     */

    public XDRBufferWriter(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }

    /**
     * Construct a new <code>XDRBufferWriter</code> that writes to the
     * given byte buffer.
     *
     * @param buffer The byte buffer.
     * @param length The length (the number of available bytes) in the buffer.
     */

    public XDRBufferWriter(byte[] buffer, int length) {
        this(buffer, 0, length);
    }

    /**
     * Construct a new <code>XDRBufferWriter</code> that writes to the
     * given byte buffer.
     *
     * @param buffer The byte buffer.
     * @param offset The offset within the buffer at which to start writing.
     * @param length The length (the number of available bytes) in the buffer.
     */

    private XDRBufferWriter(byte[] buffer, int offset, int length) {
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
     * @param offset The new start offset.
     */

    public void reset(int offset, int length) {
        pos = offset;
        left = length;
    }

    /**
     * Get the length of the data currently in the buffer.
     */

    public int getLength() {
        return (pos);
    }

    /**
     * Set the length of the data currently in the buffer. This method can be
     * used to truncate some data from the buffer.
     *
     * @param pos The new write position. If this value is greater than the
     *            length of the buffer, it is set to to the buffer length.
     */

    public void setLength(int pos) {
        this.pos = Math.min(pos, buffer.length);
    }

    /**
     *
     */

    public void writeBoolean(boolean value) throws IOException {
        writeInt(value ? 1 : 0);
    }

    /**
     *
     */

    public void writeChar(char value) throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        buffer[pos++] = 0;
        buffer[pos++] = 0;
        buffer[pos++] = 0;
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeShort(short value) throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        buffer[pos++] = 0;
        buffer[pos++] = 0;
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 8));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeUnsignedShort(int value) throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        buffer[pos++] = 0;
        buffer[pos++] = 0;
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 8));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeInt(int value) throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 24));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 16));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 8));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeUnsignedInt(long value) throws IOException {
        if (left < XDRConstants.UNIT_SIZE) {
            throw (new EOFException());
        }

        left -= XDRConstants.UNIT_SIZE;

        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 24));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 16));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 8));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeLong(long value) throws IOException {
        if (left < (XDRConstants.UNIT_SIZE * 2)) {
            throw (new EOFException());
        }

        left -= (XDRConstants.UNIT_SIZE * 2);

        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 56));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 48));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 40));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 32));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 24));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 16));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & (value >>> 8));
        buffer[pos++] = (byte) (XDRConstants.STOP_BYTE & value);
    }

    /**
     *
     */

    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    /**
     *
     */

    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    /**
     *
     */

    public void writeString(String string) throws IOException {
        String stringInt = string;

        if (stringInt == null) {
            stringInt = "";
        }

        // There isn't a more efficient way to get to the bytes in the string;
        // we have to duplicate them.

        byte[] ascii = stringInt.getBytes(StandardCharsets.UTF_8);
        writeByteArray(ascii);
    }

    /**
     *
     */

    public void writeBooleanArray(boolean[] array) throws IOException {
        writeBooleanArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeBooleanArray(boolean[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeBooleanVector(array, offset, length);
    }

    /**
     *
     */

    public void writeBooleanVector(boolean[] array) throws IOException {
        writeBooleanVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeBooleanVector(boolean[] array, int offset, int length)
        throws IOException {
        int offsetInt = offset;
        for (int i = 0; i < length; i++) {
            writeBoolean(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeByteArray(byte[] array) throws IOException {
        writeByteArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeByteArray(byte[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeByteVector(array, offset, length);
    }

    /**
     *
     */

    public void writeByteVector(byte[] array) throws IOException {
        writeByteVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeByteVector(byte[] array, int offset, int length)
        throws IOException {
        int pad = getPad(length);
        int len = length + pad;

        if (left < len) {
            throw (new EOFException());
        }

        System.arraycopy(array, offset, buffer, pos, length);

        pos += length;

        for (int i = 0; i < pad; i++) {
            buffer[pos++] = 0;
        }

        left -= len;
    }

    /**
     *
     */

    public void writeShortArray(short[] array) throws IOException {
        writeShortArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeShortArray(short[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeShortVector(array, offset, length);
    }

    /**
     *
     */

    public void writeShortVector(short[] array) throws IOException {
        writeShortVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeShortVector(short[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeShort(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeUnsignedShortArray(int[] array) throws IOException {
        writeUnsignedShortArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeUnsignedShortArray(int[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeUnsignedShortVector(array, offset, length);
    }

    /**
     *
     */

    public void writeUnsignedShortVector(int[] array) throws IOException {
        writeUnsignedShortVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeUnsignedShortVector(int[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeUnsignedShort(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeIntArray(int[] array) throws IOException {
        writeIntArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeIntArray(int[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeIntVector(array, offset, length);
    }

    /**
     *
     */

    public void writeIntVector(int[] array) throws IOException {
        writeIntVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeIntVector(int[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeInt(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeUnsignedIntArray(long[] array) throws IOException {
        writeUnsignedIntArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeUnsignedIntArray(long[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeUnsignedIntVector(array, offset, length);
    }

    /**
     *
     */

    public void writeUnsignedIntVector(long[] array) throws IOException {
        writeUnsignedIntVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeUnsignedIntVector(long[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeUnsignedInt(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeLongArray(long[] array) throws IOException {
        writeLongArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeLongArray(long[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeLongVector(array, offset, length);
    }

    /**
     *
     */

    public void writeLongVector(long[] array) throws IOException {
        writeLongVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeLongVector(long[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeLong(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeFloatArray(float[] array) throws IOException {
        writeFloatArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeFloatArray(float[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeFloatVector(array, offset, length);
    }

    /**
     *
     */

    public void writeFloatVector(float[] array) throws IOException {
        writeFloatVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeFloatVector(float[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeFloat(array[offsetInt++]);
        }
    }

    /**
     *
     */

    public void writeDoubleArray(double[] array) throws IOException {
        writeDoubleArray(array, 0, array.length);
    }

    /**
     *
     */

    public void writeDoubleArray(double[] array, int offset, int length)
        throws IOException {
        writeInt(length);
        writeDoubleVector(array, offset, length);
    }

    /**
     *
     */

    public void writeDoubleVector(double[] array) throws IOException {
        writeDoubleVector(array, 0, array.length);
    }

    /**
     *
     */

    public void writeDoubleVector(double[] array, int offset, int length)
        throws IOException {

        int offsetInt = offset;

        for (int i = 0; i < length; i++) {
            writeDouble(array[offsetInt++]);
        }
    }

    /*
     */

    private int getPad(int len) {
        int pad = len % XDRConstants.UNIT_SIZE;

        return ((pad == 0) ? 0 : (XDRConstants.UNIT_SIZE - pad));
    }

}
