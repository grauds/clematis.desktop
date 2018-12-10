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

import java.io.IOException;

/**
 * An interface for decoding various datatypes from XDR.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */
@SuppressWarnings("unused")
public interface XDRDataInput {

    /**
     * Read a boolean value.
     */

    boolean readBoolean() throws IOException;

    /**
     * Read a character (8-bit integer) value.
     */

    char readChar() throws IOException;

    /**
     * Read a short (16-bit integer) value.
     */

    short readShort() throws IOException;

    /**
     * Read an unsigned short (unsigned 16-bit integer value, coerced to a Java
     * <tt>int</tt>).
     */

    int readUnsignedShort() throws IOException;

    /**
     * Read an int (32-bit integer) value.
     */

    int readInt() throws IOException;

    /**
     * Read an unsigned integer value (coerced to a 64-bit signed Java
     * <tt>long</tt>).
     */

    long readUnsignedInt() throws IOException;

    /**
     * Read a long (64-bit integer) value.
     */

    long readLong() throws IOException;

    /**
     * Read a float value.
     */

    float readFloat() throws IOException;

    /**
     * Read a double value.
     */

    double readDouble() throws IOException;

    /**
     * Read a variable-length string.
     */

    String readString() throws IOException;

    /**
     * Read a fixed-length string.
     *
     * @param length The length of the string.
     */

    String readString(int length) throws IOException;

    /**
     * Read a variable-length array of <tt>boolean</tt> values.
     */

    boolean[] readBooleanArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>boolean</tt> values.
     *
     * @param array The array to read into.
     */

    void readBooleanVector(boolean[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>boolean</tt> values.
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readBooleanVector(boolean[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of bytes.
     */

    byte[] readByteArray() throws IOException;

    /**
     * Read a fixed-length array of bytes.
     *
     * @param array The array to read into.
     */

    void readByteVector(byte[] array) throws IOException;

    /**
     * Read a fxied-length array of bytes.
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readByteVector(byte[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>short</tt> values (16-bit integers).
     */

    short[] readShortArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>short</tt> values (16-bit integers).
     *
     * @param array The array to read into.
     */

    void readShortVector(short[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>short</tt> values (16-bit integers).
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readShortVector(short[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
     */

    int[] readUnsignedShortArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
     *
     * @param array The array to read into.
     */

    void readUnsignedShortVector(int[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readUnsignedShortVector(int[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>int</tt> values (32-bit integers).
     */

    int[] readIntArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>int</tt> values (32-bit integers).
     *
     * @param array The array to read into.
     */

    void readIntVector(int[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>int</tt> values (32-bit integers).
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readIntVector(int[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
     */

    long[] readUnsignedIntArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
     *
     * @param array The array to read into.
     */

    void readUnsignedIntVector(long[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readUnsignedIntVector(long[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>long</tt> values (64-bit integers).
     */

    long[] readLongArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>long</tt> values (64-bit integers).
     *
     * @param array The array to read into.
     */

    void readLongVector(long[] array) throws IOException;

    /**
     * Read a variable-length array of <tt>long</tt> values (64-bit integers).
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readLongVector(long[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>float</tt> values.
     */

    float[] readFloatArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>float</tt> values.
     *
     * @param array The array to read into.
     */

    void readFloatVector(float[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>float</tt> values.
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readFloatVector(float[] array, int offset, int length)
            throws IOException;

    /**
     * Read a variable-length array of <tt>double</tt> values.
     */

    double[] readDoubleArray() throws IOException;

    /**
     * Read a fixed-length array of <tt>double</tt> values.
     *
     * @param array The array to read into.
     */

    void readDoubleVector(double[] array) throws IOException;

    /**
     * Read a fixed-length array of <tt>double</tt> values.
     *
     * @param array  The array to read into.
     * @param offset The offset in the array in which to begin storing values.
     * @param length The number of values to read.
     */

    void readDoubleVector(double[] array, int offset, int length)
            throws IOException;
}
