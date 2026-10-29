package com.hyperrealm.kiwi.io;
/* ----------------------------------------------------------------------------\
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
   ----------------------------------------------------------------------------\
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link BackupFileOutputStream}.
 */
public class BackupFileOutputStreamTest {

    private File testFile;
    private File tempFile;
    private File backupFile;

    /**
     * Set up test files before each test.
     */
    @Before
    public void setUp() throws IOException {
        testFile = File.createTempFile("kiwi-test-", ".txt");
        tempFile = new File(testFile.getAbsolutePath() + ".tmp");
        backupFile = new File(testFile.getAbsolutePath() + BackupFileOutputStream.BAK);
        // Clean up any existing files
        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Clean up test files after each test.
     */
    @After
    public void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }

    /**
     * Test that backup is created on close.
     */
    @Test
    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MultipleStringLiterals"})
    public void testBackupCreatedOnClose() throws IOException {
        // Create original file
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("original content".getBytes());
        fos.close();
        assertTrue(testFile.exists());

        // Write new content using BackupFileOutputStream
        BackupFileOutputStream bfos = new BackupFileOutputStream(testFile);
        bfos.write("new content".getBytes());
        bfos.close();

        // Verify backup was created
        assertTrue("Backup file should exist", backupFile.exists());
        // Verify original content in backup
        String backupContent = new String(java.nio.file.Files.readAllBytes(backupFile.toPath()));
        assertEquals("original content", backupContent);
        // Verify new content in original file
        String newContent = new String(java.nio.file.Files.readAllBytes(testFile.toPath()));
        assertEquals("new content", newContent);
    }

    /**
     * Test creating a new file.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testCreateNewFile() throws IOException {
        // Ensure file does not exist
        if (testFile.exists()) {
            testFile.delete();
        }

        BackupFileOutputStream bfos = new BackupFileOutputStream(testFile);
        bfos.write("new file content".getBytes());
        bfos.close();

        assertTrue(testFile.exists());
        String content = new String(java.nio.file.Files.readAllBytes(testFile.toPath()));
        assertEquals("new file content", content);
        // No backup should exist for new file
        assertFalse(backupFile.exists());
    }

    /**
     * Test multiple writes.
     */
    @Test
    public void testMultipleWrites() throws IOException {
        BackupFileOutputStream bfos = new BackupFileOutputStream(testFile);
        bfos.write("Hello, ".getBytes());
        bfos.write("World!".getBytes());
        bfos.write(" Goodbye.".getBytes());
        bfos.close();

        String content = new String(java.nio.file.Files.readAllBytes(testFile.toPath()));
        assertEquals("Hello, World! Goodbye.", content);
    }

    /**
     * Test that backup is overwritten on subsequent writes.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testBackupOverwritten() throws IOException {
        // First write
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("first".getBytes());
        fos.close();

        BackupFileOutputStream bfos1 = new BackupFileOutputStream(testFile);
        bfos1.write("second".getBytes());
        bfos1.close();

        // Second write
        BackupFileOutputStream bfos2 = new BackupFileOutputStream(testFile);
        bfos2.write("third".getBytes());
        bfos2.close();

        String content = new String(java.nio.file.Files.readAllBytes(testFile.toPath()));
        assertEquals("third", content);
        String backupContent = new String(java.nio.file.Files.readAllBytes(backupFile.toPath()));
        assertEquals("second", backupContent);
    }

    /**
     * Test writing empty content.
     */
    @Test
    public void testEmptyContent() throws IOException {
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("old".getBytes());
        fos.close();

        BackupFileOutputStream bfos = new BackupFileOutputStream(testFile);
        bfos.close();

        String content = new String(java.nio.file.Files.readAllBytes(testFile.toPath()));
        assertEquals("", content);
        assertTrue(backupFile.exists());
    }
}
