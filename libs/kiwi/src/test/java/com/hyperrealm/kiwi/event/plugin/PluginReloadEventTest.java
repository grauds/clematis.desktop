package com.hyperrealm.kiwi.event.plugin;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link PluginReloadEvent} record.
 */
public class PluginReloadEventTest {

    /**
     * Tests the primary constructor with a source plugin.
     */
    @Test
    public void testConstructorWithSource() {
        // Arrange
        Object mockPlugin = new Object();

        // Act
        PluginReloadEvent event = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);

        // Assert
        assertNotNull(event.source());
        assertEquals(mockPlugin, event.source());
    }

    /**
     * Tests that source can be null.
     */
    @Test
    public void testSourceCanBeNull() {
        // Act
        PluginReloadEvent event = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) null);

        // Assert - source should be null
        assertNull(event.source());
    }

    /**
     * Tests that the record accessor source() returns the correct value.
     */
    @Test
    public void testSourceAccessor() {
        // Arrange
        Object mockPlugin = new Object();
        PluginReloadEvent event = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);

        // Act & Assert
        assertEquals(mockPlugin, event.source());
    }

    /**
     * Tests that two events with the same source are equal.
     */
    @Test
    public void testEqualityWithSameSource() {
        // Arrange
        Object mockPlugin = new Object();
        PluginReloadEvent event1 = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);
        PluginReloadEvent event2 = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);

        // Assert - records compare by component values
        assertEquals(event1, event2);
    }

    /**
     * Tests that toString() produces a meaningful string.
     */
    @Test
    public void testToString() {
        // Arrange
        PluginReloadEvent event = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) null);

        // Act
        String str = event.toString();

        // Assert - should not be empty
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    /**
     * Tests that hashCode is consistent with equals.
     */
    @Test
    public void testHashCodeConsistentWithEquals() {
        // Arrange
        Object mockPlugin = new Object();
        PluginReloadEvent event1 = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);
        PluginReloadEvent event2 = new PluginReloadEvent(
            (com.hyperrealm.kiwi.plugin.Plugin) mockPlugin);

        // Assert - equal objects must have equal hash codes
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    /**
     * Tests that the record component name is correct.
     */
    @Test
    public void testRecordComponentName() {
        // Arrange
        PluginReloadEvent event = new PluginReloadEvent(
            null
        );

        // Act & Assert - verify the record has the expected component
        assertEquals("source",
            event.getClass().getRecordComponents()[0].getName());
    }
}
