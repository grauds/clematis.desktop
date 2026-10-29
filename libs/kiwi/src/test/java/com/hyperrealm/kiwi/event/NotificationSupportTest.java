package com.hyperrealm.kiwi.event;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link NotificationSupport}.
 */
@SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MagicNumber"})
public class NotificationSupportTest {

    /**
     * Tests that adding and removing notification listeners works correctly.
     * Verifies that events are only delivered to registered listeners.
     */
    @Test
    public void testAddAndRemoveNotificationListener() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);
        List<NotificationEvent> receivedEvents = new ArrayList<>();

        NotificationListener listener = receivedEvents::add;

        support.addNotificationListener(listener);
        support.fireNotificationEvent(1);

        // Assert - listener received the event
        assertEquals(1, receivedEvents.size());
        assertEquals(1, receivedEvents.getFirst().getID());

        // Remove listener and fire again
        support.removeNotificationListener(listener);
        support.fireNotificationEvent(1);

        // Assert - listener no longer receives events
        assertEquals(1, receivedEvents.size());
    }

    /**
     * Tests that multiple listeners all receive notification events.
     * Verifies that event delivery is broadcast to all registered listeners.
     */
    @Test
    public void testMultipleListenersAllReceiveEvents() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);
        List<Integer> listener1Count = new ArrayList<>();
        List<Integer> listener2Count = new ArrayList<>();

        NotificationListener listener1 = e -> listener1Count.add(e.getID());
        NotificationListener listener2 = e -> listener2Count.add(e.getID());

        support.addNotificationListener(listener1);
        support.addNotificationListener(listener2);

        // Act - fire multiple events with different IDs
        support.fireNotificationEvent(1);
        support.fireNotificationEvent(2);
        support.fireNotificationEvent(3);

        // Assert - both listeners received all events
        assertEquals(3, listener1Count.size());
        assertEquals(3, listener2Count.size());
        assertEquals(1, listener1Count.get(0).intValue());
        assertEquals(2, listener1Count.get(1).intValue());
        assertEquals(3, listener1Count.get(2).intValue());
    }

    /**
     * Tests firing notification events with different IDs.
     * Verifies that the event ID is correctly propagated to listeners.
     */
    @Test
    public void testFireNotificationEventWithDifferentIds() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);
        final int[] receivedId = {0};

        NotificationListener listener = e -> receivedId[0] = e.getID();
        support.addNotificationListener(listener);

        // Act - fire events with different IDs
        support.fireNotificationEvent(100);
        assertEquals(100, receivedId[0]);

        support.fireNotificationEvent(200);
        assertEquals(200, receivedId[0]);

        support.fireNotificationEvent(0);
        assertEquals(0, receivedId[0]);
    }

    /**
     * Tests that firing notification events with no listeners does not throw exceptions.
     * Verifies graceful handling of empty listener lists.
     */
    @Test
    public void testFireNotificationEventWithNoListeners() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);

        // Act & Assert - no exceptions should be thrown
        support.fireNotificationEvent(0);
        support.fireNotificationEvent(1);
        support.fireNotificationEvent(999);
    }

    /**
     * Tests that the event source is correctly set to the owner object.
     */
    @Test
    public void testEventSourceIsCorrect() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);
        final Object[] receivedSource = {null};

        NotificationListener listener = e -> receivedSource[0] = e.getSource();
        support.addNotificationListener(listener);

        // Act
        support.fireNotificationEvent(1);

        // Assert
        assertEquals(source, receivedSource[0]);
    }

    /**
     * Tests that removing a non-existent listener does not throw exceptions.
     */
    @Test
    public void testRemoveNonExistentListener() {
        // Arrange
        Object source = new Object();
        NotificationSupport support = new NotificationSupport(source);
        NotificationListener listener = _ -> {};

        // Act & Assert - removing a listener that was never added should not throw
        support.removeNotificationListener(listener);
        support.fireNotificationEvent(1); // should still work
    }
}
