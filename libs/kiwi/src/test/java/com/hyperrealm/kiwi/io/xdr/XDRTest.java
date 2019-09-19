package com.hyperrealm.kiwi.io.xdr;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class XDRTest {

    private static final String TEST_STRING = "Test string, тестовая строка";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        testFolder.create();
    }

    @Test
    public void testXDR() throws IOException {
        File toStoreXDR = testFolder.newFile();

        try (XDROutputStream xout = new XDROutputStream(new FileOutputStream(toStoreXDR))) {
            writeTestOutput(xout);

        }

        try (XDRInputStream xin = new XDRInputStream(new FileInputStream(toStoreXDR))) {
            readTestInput(xin);
        }
    }

    @Test
    public void testXDR2() throws IOException {
        byte[] buffer = new byte[1024];
        XDRBufferWriter xout = new XDRBufferWriter(buffer);
        writeTestOutput(xout);

        XDRBufferReader xin = new XDRBufferReader(buffer);
        readTestInput(xin);
    }

    private void writeTestOutput(XDRDataOutput xout) throws IOException {
        xout.writeInt(1);
        xout.writeBoolean(true);
        xout.writeDouble(2d);
        xout.writeFloat(3.2f);
        xout.writeLong(155L);
        xout.writeChar('f');
        xout.writeShort((short) 45);
        xout.writeUnsignedInt(4);
        xout.writeUnsignedShort(47);
        xout.writeString(TEST_STRING);

        xout.writeIntArray(new int[]{1, 2, 3}, 1, 2);
        xout.writeBooleanArray(new boolean[] {true, false, true}, 1, 1);
        xout.writeDoubleArray(new double[]{2d, 4d, 6d});
        xout.writeFloatArray(new float[]{3.2f});
        xout.writeLongVector(new long[]{155L, 134L, 178L, 133L}, 3, 1);
        xout.writeShortArray(new short[] {45, 56, 99});
        xout.writeUnsignedIntArray(new long[]{4, 8, 9});
        xout.writeUnsignedShortVector(new int[]{47, 49, 51});
    }

    private void readTestInput(XDRDataInput xin) throws IOException {
        assertEquals(1, xin.readInt());
        assertTrue(xin.readBoolean());
        assertEquals(2d, xin.readDouble(), 0);
        assertEquals(3.2f, xin.readFloat(), 0);
        assertEquals(155L, xin.readLong());
        assertEquals('f', xin.readChar());
        assertEquals(45, xin.readShort());
        assertEquals(4, xin.readUnsignedInt());
        assertEquals(47, xin.readUnsignedShort());
// russian text is not preserved
        assertNotEquals(TEST_STRING, xin.readString());

        assertArrayEquals(new int[]{2, 3}, xin.readIntArray());
        assertArrayEquals(new boolean[] {false}, xin.readBooleanArray());
        assertArrayEquals(new double[]{2d, 4d, 6d}, xin.readDoubleArray(), 0);
        assertArrayEquals(new float[]{3.2f}, xin.readFloatArray(), 0);

        long[] longs = new long[1];
        xin.readLongVector(longs);
        assertArrayEquals(new long[]{133L}, longs);

        assertArrayEquals(new short[] {45, 56, 99}, xin.readShortArray());
        assertArrayEquals(new long[]{4, 8, 9}, xin.readUnsignedIntArray());

        int[] ints = new int[3];
        xin.readIntVector(ints);
        assertArrayEquals(new int[]{47, 49, 51}, ints);
    }

    @After
    public void tearDown() {
        testFolder.delete();
    }
}