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
 * An output stream for encoding data in the XDR encoding format. See
 * <a href="http://www.faqs.org/rfcs/rfc1832.html">RFC 1832</a> for more
 * information.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class XDROutputStream extends FilterOutputStream
  implements XDRDataOutput
{
  private static final int UNIT_SIZE = 4;
  
  /** Construct a new <code>XDROutputStream</code> that wraps the given
   * stream.
   *
   * @param out The <code>OutputStream</code> to wrap.
   */
  
  public XDROutputStream(OutputStream out)
  {
    super(out);
  }

  /**
   */

  public void writeBoolean(boolean value) throws IOException
  {
    writeInt(value ? 1 : 0);
  }

  /**
   */

  public void writeChar(char value) throws IOException
  {
    write((byte)0);
    write((byte)0);
    write((byte)0);
    write((byte)(0xFF & value));
  }

  /**
   */

  public void writeShort(short value) throws IOException
  {
    write((byte)0);
    write((byte)0);
    write((byte)(0xFF & (value >>> 8)));
    write((byte)(0xFF & value));
  }

  /**
   */

  public void writeUnsignedShort(int value) throws IOException
  {
    // will this work??
    write((byte)0);
    write((byte)0);
    write((byte)(0xFF & (value >>> 8)));
    write((byte)(0xFF & value));
  }

  /**
   */

  public void writeInt(int value) throws IOException
  {
    write((byte)(0xFF & (value >>> 24)));
    write((byte)(0xFF & (value >>> 16)));
    write((byte)(0xFF & (value >>> 8)));
    write((byte)(0xFF & value));
  }

  /**
   */
  
  public void writeUnsignedInt(long value) throws IOException
  {
    write((byte)(0xFF & (value >>> 24)));
    write((byte)(0xFF & (value >>> 16)));
    write((byte)(0xFF & (value >>> 8)));
    write((byte)(0xFF & value));    
  }

  /**
   */

  public void writeLong(long value) throws IOException
  {
    write((byte)(0xFF & (value >>> 56)));
    write((byte)(0xFF & (value >>> 48)));
    write((byte)(0xFF & (value >>> 40)));
    write((byte)(0xFF & (value >>> 32)));
    write((byte)(0xFF & (value >>> 24)));
    write((byte)(0xFF & (value >>> 16)));
    write((byte)(0xFF & (value >>> 8)));
    write((byte)(0xFF & value));
  }

  /**
   */

  public void writeFloat(float value) throws IOException
  {
    writeInt(Float.floatToIntBits(value));
  }

  /**
   */

  public void writeDouble(double value) throws IOException
  {
    writeLong(Double.doubleToLongBits(value));
  }

  /**
   */

  public void writeString(String string) throws IOException
  {
    byte[] ascii = string.getBytes();
    writeByteArray(ascii);
  }

  /**
   */

  public void writeBooleanArray(boolean[] array) throws IOException
  {
    writeBooleanArray(array, 0, array.length);
  }

  /**
   */

  public void writeBooleanArray(boolean[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeBooleanVector(array, offset, length);
  }

  /**
   */

  public void writeBooleanVector(boolean[] array) throws IOException
  {
    writeBooleanVector(array, 0, array.length);
  }

  /**
   */
  
  public void writeBooleanVector(boolean[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeBoolean(array[offset++]);
  }

  /**
   */

  public void writeByteArray(byte[] array) throws IOException
  {
    writeByteArray(array, 0, array.length);
  }

  /**
   */

  public void writeByteArray(byte[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeByteVector(array, offset, length);
  }

  /**
   */

  public void writeByteVector(byte[] array) throws IOException
  {
    writeByteVector(array, 0, array.length);
  }

  /**
   */

  public void writeByteVector(byte[] array, int offset, int length)
    throws IOException
  {
    write(array, offset, length);

    int pad = length % UNIT_SIZE;
    if(pad != 0)
      for(int i = (UNIT_SIZE - pad); i > 0; i--)
        write((byte)0);
  }

  /**
   */
  
  public void writeShortArray(short[] array) throws IOException
  {
    writeShortArray(array, 0, array.length);
  }

  /**
   */

  public void writeShortArray(short[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeShortVector(array, offset, length);
  }

  /**
   */

  public void writeShortVector(short[] array) throws IOException
  {
    writeShortVector(array, 0, array.length);
  }

  /**
   */

  public void writeShortVector(short[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeShort(array[offset++]);    
  }
  
  /**
   */

  public void writeUnsignedShortArray(int[] array) throws IOException
  {
    writeUnsignedShortArray(array, 0, array.length);
  }

  /**
   */

  public void writeUnsignedShortArray(int[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeUnsignedShortVector(array, offset, length);
  }

  /**
   */

  public void writeUnsignedShortVector(int[] array) throws IOException
  {
    writeUnsignedShortVector(array, 0, array.length);
  }
  
  /**
   */

  public void writeUnsignedShortVector(int[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeUnsignedShort(array[offset++]);    
  }
  
  /**
   */

  public void writeIntArray(int[] array) throws IOException
  {
    writeIntArray(array, 0, array.length);
  }

  /**
   */

  public void writeIntArray(int[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeIntVector(array, offset, length);
  }

  /**
   */

  public void writeIntVector(int[] array) throws IOException
  {
    writeIntVector(array, 0, array.length);
  }

  /**
   */

  public void writeIntVector(int[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeInt(array[offset++]);
  }

  /**
   */

  public void writeUnsignedIntArray(long[] array) throws IOException
  {
    writeUnsignedIntArray(array, 0, array.length);
  }

  /**
   */

  public void writeUnsignedIntArray(long[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeUnsignedIntVector(array, offset, length);
  }

  /**
   */

  public void writeUnsignedIntVector(long[] array) throws IOException
  {
    writeUnsignedIntVector(array, 0, array.length);
  }

  /**
   */

  public void writeUnsignedIntVector(long[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeUnsignedInt(array[offset++]);
  }

  /**
   */

  public void writeLongArray(long[] array) throws IOException
  {
    writeLongArray(array, 0, array.length);
  }

  /**
   */

  public void writeLongArray(long[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeLongVector(array, offset, length);
  }

  /**
   */

  public void writeLongVector(long[] array) throws IOException
  {
    writeLongVector(array, 0, array.length);
  }

  /**
   */

  public void writeLongVector(long[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeLong(array[offset++]);    
  }

  /**
   */

  public void writeFloatArray(float[] array) throws IOException
  {
    writeFloatArray(array, 0, array.length);
  }

  /**
   */

  public void writeFloatArray(float[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeFloatVector(array, offset, length);
  }

  /**
   */

  public void writeFloatVector(float[] array) throws IOException
  {
    writeFloatVector(array, 0, array.length);
  }

  /**
   */

  public void writeFloatVector(float[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeFloat(array[offset++]);
  }
  
  /**
   */

  public void writeDoubleArray(double[] array) throws IOException
  {
    writeDoubleArray(array, 0, array.length);
  }

  /**
   */
  
  public void writeDoubleArray(double[] array, int offset, int length)
    throws IOException
  {
    writeInt(length);
    writeDoubleVector(array, offset, length);
  }

  /**
   */

  public void writeDoubleVector(double[] array) throws IOException
  {
    writeDoubleVector(array, 0, array.length);
  }

  /**
   */

  public void writeDoubleVector(double[] array, int offset, int length)
    throws IOException
  {
    for(int i = 0; i < length; i++)
      writeDouble(array[offset++]);
  }

}

/* end of source file */
