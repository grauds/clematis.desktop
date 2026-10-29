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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link ActionSupport}.
 */
public class ActionSupportTest {

    /**
     * Tests that adding and removing action listeners works correctly.
     * Verifies that events are only delivered to registered listeners.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testAddAndRemoveActionListener() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);
        List<String> receivedEvents = new ArrayList<>();

        ActionListener listener = e -> receivedEvents.add(e.getActionCommand());

        // Act - add listener and fire event
        support.addActionListener(listener);
        support.fireActionEvent("cmd1");

        // Assert - listener received the event
        assertEquals(1, receivedEvents.size());
        assertEquals("cmd1", receivedEvents.getFirst());

        // Remove listener and fire again
        support.removeActionListener(listener);
        support.fireActionEvent("cmd2");

        // Assert - listener no longer receives events
        assertEquals(1, receivedEvents.size());
    }

    /**
     * Tests firing an action event with no command string.
     * Verifies that null command is passed correctly to the listener.
     */
    @Test
    public void testFireActionEventWithNullCommand() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);
        final String[] receivedCommand = {null};

        ActionListener listener = e -> receivedCommand[0] = e.getActionCommand();
        support.addActionListener(listener);

        // Act
        support.fireActionEvent();

        // Assert
        assertNull(receivedCommand[0]);
    }

    /**
     * Tests firing an action event with a custom ID and modifiers.
     * Verifies that all event properties are correctly propagated.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testFireActionEventWithCustomIdAndModifiers() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);
        final int[] receivedId = {0};
        final String[] receivedCommand = {null};
        final int[] receivedModifiers = {0};

        ActionListener listener = e -> {
            receivedId[0] = e.getID();
            receivedCommand[0] = e.getActionCommand();
            receivedModifiers[0] = e.getModifiers();
        };
        support.addActionListener(listener);

        // Act
        int customId = 999;
        String command = "customCmd";
        int modifiers = ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK;
        support.fireActionEvent(customId, command, modifiers);

        // Assert
        assertEquals(customId, receivedId[0]);
        assertEquals(command, receivedCommand[0]);
        assertEquals(modifiers, receivedModifiers[0]);
    }

    /**
     * Tests that multiple listeners all receive events.
     * Verifies that event delivery is broadcast to all registered listeners.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testMultipleListenersAllReceiveEvents() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);
        List<String> listener1Events = new ArrayList<>();
        List<String> listener2Events = new ArrayList<>();

        ActionListener listener1 = e -> listener1Events.add(e.getActionCommand());
        ActionListener listener2 = e -> listener2Events.add(e.getActionCommand());

        support.addActionListener(listener1);
        support.addActionListener(listener2);

        // Act
        support.fireActionEvent("broadcast");

        // Assert - both listeners received the event
        assertEquals(1, listener1Events.size());
        assertEquals(1, listener2Events.size());
        assertEquals("broadcast", listener1Events.getFirst());
        assertEquals("broadcast", listener2Events.getFirst());
    }

    /**
     * Tests that firing events with no listeners does not throw exceptions.
     * Verifies graceful handling of empty listener lists.
     */
    @Test
    public void testFireEventWithNoListeners() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);

        // Act & Assert - no exceptions should be thrown
        support.fireActionEvent();
        support.fireActionEvent("command");
       // support.fireActionEvent(ActionEvent.CUSTOM, "cmd", 0);
    }

    /**
     * Tests that the event source is correctly set to the owner object.
     */
    @Test
    public void testEventSourceIsCorrect() {
        // Arrange
        Object source = new Object();
        ActionSupport support = new ActionSupport(source);
        final Object[] receivedSource = {null};

        ActionListener listener = e -> receivedSource[0] = e.getSource();
        support.addActionListener(listener);

        // Act
        support.fireActionEvent();

        // Assert
        assertEquals(source, receivedSource[0]);
    }
}
