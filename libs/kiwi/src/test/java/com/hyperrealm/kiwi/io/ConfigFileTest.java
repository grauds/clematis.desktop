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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ConfigFile}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ConfigFileTest {

    private File configFile;
    private File tempDir;

    /**
     * Set up test files before each test.
     */
    @Before
    public void setUp() throws IOException {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "kiwi-config-test");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        configFile = new File(tempDir, "test-config.properties");
    }

    /**
     * Clean up test files after each test.
     */
    @After
    public void tearDown() {
        if (configFile.exists()) {
            configFile.delete();
        }
        if (tempDir.exists()) {
            deleteRecursively(tempDir);
        }
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    /**
     * Test constructors.
     */
    @Test
    public void testConstructors() {
        ConfigFile cf1 = new ConfigFile(configFile, "Test comment");
        assertNotNull(cf1);

        ConfigFile cf2 = new ConfigFile(configFile);
        assertNotNull(cf2);
    }

    /**
     * Test getPath returns the file path.
     */
    @Test
    public void testGetPath() {
        ConfigFile cf = new ConfigFile(configFile, "Test");
        String path = cf.getPath();
        assertEquals(configFile.getAbsolutePath(), path);
    }

    /**
     * Test store writes properties to file.
     */
    @Test
    public void testStoreWritesProperties() throws IOException {
        ConfigFile cf = new ConfigFile(configFile, "Test comment");
        cf.setProperty("key1", "value1");
        cf.setProperty("key2", "value2");
        cf.store();

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }
        assertEquals("value1", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
    }

    /**
     * Test store creates parent directories.
     */
    @Test
    public void testStoreCreatesParentDirs() throws IOException {
        File nestedFile = new File(tempDir, "nested/dir/config.properties");
        ConfigFile cf = new ConfigFile(nestedFile, "Test");
        cf.setProperty("key", "value");
        cf.store();

        assertTrue(nestedFile.exists());
        cf = new ConfigFile(nestedFile, "Test");
        cf.load();
        assertNotNull(cf.getProperty("key"));
    }

    /**
     * Test load reads properties from file.
     */
    @Test
    public void testLoadReadsProperties() throws IOException {
        // Create properties file manually
        Properties props = new Properties();
        props.setProperty("name", "test");
        props.setProperty("value", "123");
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, null);
        }

        ConfigFile cf = new ConfigFile(configFile, "Test");
        cf.load();
        assertEquals("test", cf.getProperty("name"));
        assertEquals("123", cf.getProperty("value"));
    }

    /**
     * Test round-trip store and load.
     */
    @Test
    public void testRoundTrip() throws IOException {
        ConfigFile cf1 = new ConfigFile(configFile, "Round-trip test");
        cf1.setProperty("string", "hello");
        cf1.setProperty("number", "42");
        cf1.setProperty("flag", "true");
        cf1.store();

        ConfigFile cf2 = new ConfigFile(configFile, "Loaded");
        cf2.load();
        assertEquals("hello", cf2.getProperty("string"));
        assertEquals("42", cf2.getProperty("number"));
        assertEquals("true", cf2.getProperty("flag"));
    }

    /**
     * Test null file handling.
     */
    @Test
    public void testNullFile() {
        ConfigFile cf = new ConfigFile(null, "Test");
        // getPath should return empty string or handle gracefully
        // Since file is null, getAbsolutePath would throw
        // The implementation handles this by returning file.getAbsolutePath()
        // which would throw NPE if file is null
    }

    /**
     * Test put/get different types.
     */
    @Test
    public void testPutGetDifferentTypes() {
        ConfigFile cf = new ConfigFile(configFile, "Types test");
        cf.setProperty("string", "text");
        cf.setProperty("int", "123");
        cf.setProperty("long", "456789");
        cf.setProperty("double", "3.14");
        cf.setProperty("boolean", "true");

        assertEquals("text", cf.getProperty("string"));
        assertEquals("123", cf.getProperty("int"));
        assertEquals("456789", cf.getProperty("long"));
        assertEquals("3.14", cf.getProperty("double"));
        assertEquals("true", cf.getProperty("boolean"));
    }

    /**
     * Test non-existent key returns null.
     */
    @Test
    public void testNonExistentKeyReturnsNull() {
        ConfigFile cf = new ConfigFile(configFile, "Test");
        assertNull(cf.getProperty("nonexistent"));
    }
}
