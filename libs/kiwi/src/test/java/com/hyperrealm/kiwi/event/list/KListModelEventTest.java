package com.hyperrealm.kiwi.event.list;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

/**
 * Unit tests for {@link KListModelEvent}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class KListModelEventTest {

    /**
     * Tests the two-argument constructor with startIndex and endIndex.
     */
    @Test
    public void testConstructorWithStartAndEndIndex() {
        // Arrange
        Object source = new Object();

        // Act
        KListModelEvent event = new KListModelEvent(source, 3, 7);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(3, event.getStartIndex());
        assertEquals(7, event.getEndIndex());
        assertEquals(-1, event.getField());
        assertEquals(3, event.getIndex());
    }

    /**
     * Tests the three-argument constructor with startIndex, endIndex, and field.
     */
    @Test
    public void testConstructorWithStartEndIndexAndField() {
        // Arrange
        Object source = new Object();

        // Act
        KListModelEvent event = new KListModelEvent(source, 0, 5, 2);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(0, event.getStartIndex());
        assertEquals(5, event.getEndIndex());
        assertEquals(2, event.getField());
        assertEquals(0, event.getIndex());
    }

    /**
     * Tests the single-argument constructor with only source.
     */
    @Test
    public void testConstructorWithOnlySource() {
        // Arrange
        Object source = new Object();

        // Act
        KListModelEvent event = new KListModelEvent(source);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(-1, event.getStartIndex());
        assertEquals(-1, event.getEndIndex());
        assertEquals(-1, event.getField());
        assertEquals(-1, event.getIndex());
    }

    /**
     * Tests that getIndex() returns the same value as getStartIndex().
     */
    @Test
    public void testIndexEqualsStartIndex() {
        // Arrange
        Object source = new Object();

        // Act
        KListModelEvent event = new KListModelEvent(source, 10, 20);

        // Assert
        assertEquals(event.getStartIndex(), event.getIndex());
    }

    /**
     * Tests that events with different parameters are not equal.
     */
    @Test
    public void testDifferentEventsAreNotEqual() {
        // Arrange
        Object source = new Object();
        KListModelEvent event1 = new KListModelEvent(source, 0, 5);
        KListModelEvent event2 = new KListModelEvent(source, 0, 5);

        // Assert - different instances, not equal by reference
        assertNotEquals(event1, event2);
    }

    /**
     * Tests that the same event instance equals itself.
     */
    @Test
    public void testSameEventEqualsItself() {
        // Arrange
        Object source = new Object();
        KListModelEvent event = new KListModelEvent(source, 0, 5);

        // Assert
        assertEquals(event, event);
    }

    /**
     * Tests that null source results in null getSource().
     */
    @Test
    public void testNullSource() {
        // Act
        assertThrows(IllegalArgumentException.class, () -> new KListModelEvent(null));
    }

    /**
     * Tests constructor with zero indices.
     */
    @Test
    public void testZeroIndices() {
        // Arrange
        Object source = new Object();

        // Act
        KListModelEvent event = new KListModelEvent(source, 0, 0);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(0, event.getStartIndex());
        assertEquals(0, event.getEndIndex());
        assertEquals(-1, event.getField());
    }
}
