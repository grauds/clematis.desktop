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
 * An interface for decoding various datatypes from XDR.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface XDRDataInput
{

  /**
   * Read a boolean value.
   */

  public boolean readBoolean() throws IOException;

  /**
   * Read a character (8-bit integer) value.
   */
  
  public char readChar() throws IOException;
  
  /**
   * Read a short (16-bit integer) value.
   */
  
  public short readShort() throws IOException;

  /**
   * Read an unsigned short (unsigned 16-bit integer value, coerced to a Java
   * <tt>int</tt>).
   */

  public int readUnsignedShort() throws IOException;

  /**
   * Read an int (32-bit integer) value.
   */

  public int readInt() throws IOException;

  /**
   * Read an unsigned integer value (coerced to a 64-bit signed Java
   * <tt>long</tt>).
   */

  public long readUnsignedInt() throws IOException;
  
  /**
   * Read a long (64-bit integer) value.
   */

  public long readLong() throws IOException;

  /**
   * Read a float value.
   */

  public float readFloat() throws IOException;

  /**
   * Read a double value.
   */

  public double readDouble() throws IOException;

  /**
   * Read a variable-length string.
   */
  
  public String readString() throws IOException;
  
  /**
   * Read a fixed-length string.
   *
   * @param length The length of the string.
   */
  
  public String readString(int length) throws IOException;

  /**
   * Read a variable-length array of <tt>boolean</tt> values.
   */
  
  public boolean[] readBooleanArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>boolean</tt> values.
   *
   * @param array The array to read into.
   */

  public void readBooleanVector(boolean[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>boolean</tt> values.
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */

  public void readBooleanVector(boolean[] array, int offset, int length)
    throws IOException;

  /**
   * Read a variable-length array of bytes.
   */

  public byte[] readByteArray() throws IOException;

  /**
   * Read a fixed-length array of bytes.
   *
   * @param array The array to read into.
   */
  
  public void readByteVector(byte[] array) throws IOException;

  /**
   * Read a fxied-length array of bytes.
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readByteVector(byte[] array, int offset, int length)
    throws IOException;  
  
  /**
   * Read a variable-length array of <tt>short</tt> values (16-bit integers).
   */
  
  public short[] readShortArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>short</tt> values (16-bit integers).
   *
   * @param array The array to read into.
   */
  
  public void readShortVector(short[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>short</tt> values (16-bit integers).
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readShortVector(short[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>unsigned short</tt> values
   * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
   */

  public int[] readUnsignedShortArray() throws IOException;
  
  /**
   * Read a fixed-length array of <tt>unsigned short</tt> values
   * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
   *
   * @param array The array to read into.   
   */
  
  public void readUnsignedShortVector(int[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>unsigned short</tt> values
   * (16-bit unsigned integers, coerced to 32-bit signed Java <tt>int</tt>s).
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readUnsignedShortVector(int[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>int</tt> values (32-bit integers).
   */
  
  public int[] readIntArray() throws IOException;
  
  /**
   * Read a fixed-length array of <tt>int</tt> values (32-bit integers).
   *
   * @param array The array to read into.   
   */

  public void readIntVector(int[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>int</tt> values (32-bit integers).
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readIntVector(int[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>unsigned int</tt> values (32-bit
   * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
   */

  public long[] readUnsignedIntArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>unsigned int</tt> values (32-bit
   * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
   *
   * @param array The array to read into.   
   */
  
  public void readUnsignedIntVector(long[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>unsigned int</tt> values (32-bit
   * unsigned integers, coerced to 64-bit signed Java <tt>long</tt>s).
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readUnsignedIntVector(long[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>long</tt> values (64-bit integers).
   */
  
  public long[] readLongArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>long</tt> values (64-bit integers).
   *
   * @param array The array to read into.   
   */

  public void readLongVector(long[] array) throws IOException;

  /**
   * Read a variable-length array of <tt>long</tt> values (64-bit integers).
   * 
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readLongVector(long[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>float</tt> values.
   */
  
  public float[] readFloatArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>float</tt> values.
   *
   * @param array The array to read into.
   */

  public void readFloatVector(float[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>float</tt> values.
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */

  public void readFloatVector(float[] array, int offset, int length)
    throws IOException;
  
  /**
   * Read a variable-length array of <tt>double</tt> values.
   */
  
  public double[] readDoubleArray() throws IOException;

  /**
   * Read a fixed-length array of <tt>double</tt> values.
   *
   * @param array The array to read into.
   */

  public void readDoubleVector(double[] array) throws IOException;

  /**
   * Read a fixed-length array of <tt>double</tt> values.
   *
   * @param array The array to read into.
   * @param offset The offset in the array in which to begin storing values.
   * @param length The number of values to read.
   */
  
  public void readDoubleVector(double[] array, int offset, int length)
    throws IOException;
}

/* end of source file */
