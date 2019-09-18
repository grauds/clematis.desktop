package com.hyperrealm.kiwi.io;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
public class StreamUtilsTest {


    private final TemporaryFolder testFolder = new TemporaryFolder();

    private final String text = "This is a test string, Ceci est une chaîne de test, Это тестовая строка";

    @Before
    public void before() throws IOException {
        testFolder.create();
    }

    @Test
    public void readStreamToStream() throws IOException {
        final File file = testFolder.newFile();

        try (FileOutputStream os = new FileOutputStream(file)) {
            StreamUtils.writeStringToStream(text, os);
        }

        try (FileInputStream is = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            StreamUtils.readStreamToStream(is, bos);
            byte[] bytes = StreamUtils.readStreamToByteArray(new ByteArrayInputStream(bos.toByteArray()));
            assertEquals(text, new String(bytes));
        }
    }

    @Test
    public void writeStringToStream() throws IOException {

        final File file = testFolder.newFile();

        try (FileOutputStream os = new FileOutputStream(file)) {
            StreamUtils.writeStringToStream(text, os);
        }

        try (FileInputStream is = new FileInputStream(file)) {
            assertEquals(text, StreamUtils.readStreamToString(is));
        }
    }

    @Test
    public void readStreamToByteArray() throws IOException {
        final File file = testFolder.newFile();

        try (FileOutputStream os = new FileOutputStream(file)) {
            StreamUtils.writeStringToStream(text, os);
        }

        try (FileInputStream is = new FileInputStream(file)) {
            byte[] bytes = StreamUtils.readStreamToByteArray(is);
            assertEquals(text, new String(bytes));
        }

    }


    @After
    public void after() {
        testFolder.delete();
    }
}