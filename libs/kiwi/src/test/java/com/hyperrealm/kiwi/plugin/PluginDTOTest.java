package com.hyperrealm.kiwi.plugin;
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

import java.util.jar.Manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

/**
 * Unit tests for {@link PluginDTO}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class PluginDTOTest {

    /**
     * Test equals and hashCode.
     */
    @Test
    public void testEqualsAndHashCode() {
        PluginDTO dto1 = new PluginDTO();
        dto1.setClassName("com.example.Plugin");
        dto1.setTitle("Test Plugin");
        dto1.setName("test");

        PluginDTO dto2 = new PluginDTO();
        dto2.setClassName("com.example.Plugin");
        dto2.setTitle("Test Plugin");
        dto2.setName("test");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    /**
     * Test getManifestHeader.
     */
    @Test
    public void testGetManifestHeader() {
        Manifest manifest = PluginDTO.getManifestHeader();
        assertNotNull(manifest);
        assertNotNull(manifest.getMainAttributes());
        assertEquals("1.0", manifest.getMainAttributes().getValue(
            java.util.jar.Attributes.Name.MANIFEST_VERSION));
    }

    /**
     * Test getManifest.
     */
    @Test
    public void testGetManifest() {
        PluginDTO plugin = new PluginDTO(
            "com.example.Plugin",
            "Test Plugin",
            "test",
            "type",
            "ANY",
            "Description",
            "icon.png",
            "1.0",
            "http://example.com/help"
        );
        Manifest manifest = PluginDTO.getManifest(plugin);
        assertNotNull(manifest);
        assertNotNull(manifest.getEntries().get("com.example.Plugin"));
    }

    /**
     * Test setHelpUrl.
     */
    @Test
    public void testSetHelpUrl() {
        PluginDTO plugin = new PluginDTO();
        plugin.setHelpUrl("http://example.com/help");
        assertNotNull(plugin.getHelpURL());
        assertEquals("http://example.com/help", plugin.getHelpURL().toString());
    }

    /**
     * Test setHelpUrl with invalid URL.
     */
    @Test
    public void testSetHelpUrlInvalid() {
        PluginDTO plugin = new PluginDTO();
        assertThrows(IllegalArgumentException.class, () -> plugin.setHelpUrl("invalid-url"));
    }

    /**
     * Test constants.
     */
    @Test
    public void testConstants() {
        assertEquals("PluginTitle", PluginDTO.PLUGIN_TITLE);
        assertEquals("PluginName", PluginDTO.PLUGIN_NAME);
        assertEquals("ANY", PluginDTO.PLUGIN_LEVEL_ANY);
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        PluginDTO plugin = new PluginDTO();
        assertNotNull(plugin);
    }

    /**
     * Test all-args constructor.
     */
    @Test
    public void testAllArgsConstructor() {
        PluginDTO plugin = new PluginDTO(
            "com.example.Plugin",
            "Test Plugin",
            "test",
            "type",
            "ANY",
            "Description",
            "icon.png",
            "1.0",
            "http://example.com/help"
        );
        assertEquals("com.example.Plugin", plugin.getClassName());
        assertEquals("Test Plugin", plugin.getTitle());
        assertEquals("test", plugin.getName());
        assertEquals("type", plugin.getType());
        assertEquals("ANY", plugin.getLevel());
        assertEquals("Description", plugin.getDescription());
        assertEquals("icon.png", plugin.getIconFile());
        assertEquals("1.0", plugin.getVersion());
    }
}
