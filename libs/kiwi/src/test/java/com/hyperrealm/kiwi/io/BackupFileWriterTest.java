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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
public class BackupFileWriterTest {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    private final String text = "This is a test string, Ceci est une chaîne de test, Это тестовая строка";

    private final String text2 = "This is a test string 2, Ceci est une chaîne de test 2, Это тестовая строка 2";

    @Before
    public void before() throws IOException {
        testFolder.create();
    }

    @Test
    public void testBackupOutputStream() throws IOException {

        File test = testFolder.newFile();
// create the first version of the file
        try (BackupFileOutputStream outputStream = new BackupFileOutputStream(test)) {
            outputStream.write(text.getBytes(StandardCharsets.UTF_8));
        }
// the second version
        try (BackupFileOutputStream outputStream = new BackupFileOutputStream(test)) {
            outputStream.write(text2.getBytes(StandardCharsets.UTF_8));
        }
// validate the backup file exists
        Path backup = Paths.get(test.getAbsolutePath() + BackupFileOutputStream.BAK);
        assertTrue(Files.exists(backup));
        try (FileInputStream is = new FileInputStream(backup.toFile())) {
            assertEquals(text, StreamUtils.readStreamToString(is));
        }
    }

    @Test
    public void testBackupWriter() throws IOException {

        File test = testFolder.newFile();
// create the first version of the file
        try (BackupFileWriter backupFileWriter = new BackupFileWriter(test)) {
            backupFileWriter.append(text);
        }
// the second version
        try (BackupFileWriter backupFileWriter = new BackupFileWriter(test)) {
            backupFileWriter.append(text2);
        }
// validate the backup file exists
        Path backup = Paths.get(test.getAbsolutePath() + BackupFileWriter.BAK);
        assertTrue(Files.exists(backup));
        try (FileInputStream is = new FileInputStream(backup.toFile())) {
            assertEquals(text, StreamUtils.readStreamToString(is));
        }
    }

    @After
    public void after() {
        testFolder.delete();
    }
}