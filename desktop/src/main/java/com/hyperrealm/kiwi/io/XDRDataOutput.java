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

import java.io.IOException;

/**
 * An interface for encoding various datatypes to XDR.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface XDRDataOutput {

    /**
     * Write a boolean value.
     */

    void writeBoolean(boolean value) throws IOException;

    /**
     * Write a character (8-bit integer) value.
     */

    void writeChar(char value) throws IOException;

    /**
     * Write a short (16-bit integer) value.
     */

    void writeShort(short value) throws IOException;

    /**
     * Write an unsigned short (unsigned 16-bit integer value, represented by
     * a Java <tt>init</tt>.
     */

    void writeUnsignedShort(int value) throws IOException;

    /**
     * * Write an int (32-bit integer) value.
     */

    void writeInt(int value) throws IOException;

    /**
     * Write an unsigned integer value (represented by a 64-bit signed Java
     * <tt>long</tt).
     */

    void writeUnsignedInt(long value) throws IOException;

    /**
     * Write a long (64-bit integer) value.
     */

    void writeLong(long value) throws IOException;

    /**
     * Write a float value.
     */

    void writeFloat(float value) throws IOException;

    /**
     * Write a double value.
     */

    void writeDouble(double value) throws IOException;

    /**
     * Write a fixed-length string.
     */

    void writeString(String string) throws IOException;

    /**
     * Write a variable-length array of <tt>boolean</tt> values.
     */

    void writeBooleanArray(boolean[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>boolean</tt>values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeBooleanArray(boolean[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>boolean</tt> values.
     */

    void writeBooleanVector(boolean[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>boolean</tt> values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeBooleanVector(boolean[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of bytes.
     */

    void writeByteArray(byte[] array) throws IOException;

    /**
     * Write a variable-length array of bytes.
     *
     * @param array  The array of bytes.
     * @param offset The index of the first bytes to write.
     * @param length The number of bytes to write.
     */

    void writeByteArray(byte[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of bytes.
     */

    void writeByteVector(byte[] array) throws IOException;

    /**
     * Write a fixed-length array of bytes.
     *
     * @param array  The array of bytes.
     * @param offset The index of the first bytes to write.
     * @param length The number of bytes to write.
     */

    void writeByteVector(byte[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>short</tt> values (16-bit integers).
     */

    void writeShortArray(short[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>short</tt> values (16-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeShortArray(short[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>short</tt> values (16-bit integers).
     */

    void writeShortVector(short[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>short</tt> values (16-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeShortVector(short[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, represented by 32-bit signed Java
     * <tt>int</tt>s).
     */

    void writeUnsignedShortArray(int[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, represented by 32-bit signed Java
     * <tt>int</tt>s).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeUnsignedShortArray(int[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, represented by 32-bit signed Java
     * <tt>int</tt>s).
     */

    void writeUnsignedShortVector(int[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>unsigned short</tt> values
     * (16-bit unsigned integers, represented by 32-bit signed Java
     * <tt>int</tt>s).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeUnsignedShortVector(int[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>int</tt> values (32-bit integers).
     */

    void writeIntArray(int[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>int</tt> values (32-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeIntArray(int[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>int</tt> values (32-bit integers).
     */

    void writeIntVector(int[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>int</tt> values (32-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeIntVector(int[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, represented as Java <tt>long</tt> values).
     */

    void writeUnsignedIntArray(long[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, represented as Java <tt>long</tt> values).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeUnsignedIntArray(long[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, represented as Java <tt>long</tt> values).
     */

    void writeUnsignedIntVector(long[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>unsigned int</tt> values (32-bit
     * unsigned integers, represented as Java <tt>long</tt> values).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeUnsignedIntVector(long[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>long</tt> values (64-bit integers).
     */

    void writeLongArray(long[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>long</tt> values (64-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeLongArray(long[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>long</tt> values (64-bit integers).
     */

    void writeLongVector(long[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>long</tt> values (64-bit integers).
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeLongVector(long[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>float</tt> values.
     */

    void writeFloatArray(float[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>float</tt> values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeFloatArray(float[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>double</tt> values.
     */

    void writeFloatVector(float[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>double</tt> values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeFloatVector(float[] array, int offset, int length)
            throws IOException;

    /**
     * Write a variable-length array of <tt>double</tt> values.
     */

    void writeDoubleArray(double[] array) throws IOException;

    /**
     * Write a variable-length array of <tt>double</tt> values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeDoubleArray(double[] array, int offset, int length)
            throws IOException;

    /**
     * Write a fixed-length array of <tt>double</tt> values.
     */

    void writeDoubleVector(double[] array) throws IOException;

    /**
     * Write a fixed-length array of <tt>double</tt> values.
     *
     * @param array  The array of values.
     * @param offset The index of the first element to write.
     * @param length The number of elements to write.
     */

    void writeDoubleVector(double[] array, int offset, int length)
            throws IOException;
}
