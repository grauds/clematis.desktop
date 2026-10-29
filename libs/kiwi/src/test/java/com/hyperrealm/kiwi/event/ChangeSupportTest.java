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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link ChangeSupport}.
 */
@SuppressWarnings("checkstyle:ReturnCount")
public class ChangeSupportTest {

    /**
     * Tests that adding and removing change listeners works correctly.
     * Verifies that events are only delivered to registered listeners.
     */
    @Test
    public void testAddAndRemoveChangeListener() {
        // Arrange
        Object source = new Object();
        ChangeSupport support = new ChangeSupport(source);
        List<ChangeEvent> receivedEvents = new ArrayList<>();

        ChangeListener listener = receivedEvents::add;

        support.addChangeListener(listener);
        support.fireChangeEvent();

        // Assert - listener received the event
        assertEquals(1, receivedEvents.size());
        assertEquals(source, receivedEvents.getFirst().getSource());

        // Remove listener and fire again
        support.removeChangeListener(listener);
        support.fireChangeEvent();

        // Assert - listener no longer receives events
        assertEquals(1, receivedEvents.size());
    }

    /**
     * Tests that multiple listeners all receive change events.
     * Verifies that event delivery is broadcast to all registered listeners.
     */
    @Test
    public void testMultipleListenersAllReceiveEvents() {
        // Arrange
        Object source = new Object();
        ChangeSupport support = new ChangeSupport(source);
        List<Integer> listener1Count = new ArrayList<>();
        List<Integer> listener2Count = new ArrayList<>();

        ChangeListener listener1 = _ -> listener1Count.add(1);
        ChangeListener listener2 = _ -> listener2Count.add(1);

        support.addChangeListener(listener1);
        support.addChangeListener(listener2);

        // Act - fire multiple events
        support.fireChangeEvent();
        support.fireChangeEvent();

        // Assert - both listeners received both events
        assertEquals(2, listener1Count.size());
        assertEquals(2, listener2Count.size());
    }

    /**
     * Tests that firing change events with no listeners does not throw exceptions.
     * Verifies graceful handling of empty listener lists.
     */
    @Test
    public void testFireChangeEventWithNoListeners() {
        // Arrange
        Object source = new Object();
        ChangeSupport support = new ChangeSupport(source);

        // Act & Assert - no exceptions should be thrown
        support.fireChangeEvent();
        support.fireChangeEvent();
    }

    /**
     * Tests that the event source is correctly set to the owner object.
     */
    @Test
    public void testEventSourceIsCorrect() {
        // Arrange
        Object source = new Object();
        ChangeSupport support = new ChangeSupport(source);
        final Object[] receivedSource = {null};

        ChangeListener listener = e -> receivedSource[0] = e.getSource();
        support.addChangeListener(listener);

        // Act
        support.fireChangeEvent();

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
        ChangeSupport support = new ChangeSupport(source);
        ChangeListener listener = _ -> {
        };

        // Act & Assert - removing a listener that was never added should not throw
        support.removeChangeListener(listener);
        support.fireChangeEvent(); // should still work
    }
}
