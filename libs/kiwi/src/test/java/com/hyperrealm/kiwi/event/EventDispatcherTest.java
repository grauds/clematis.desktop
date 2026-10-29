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

import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lombok.Getter;
/**
 * Unit tests for {@link EventDispatcher}.
 * Tests reflection-based event dispatching to the most appropriate handler method.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class EventDispatcherTest {

    /**
     * Tests that a derived event is dispatched to the most specific handler.
     */
    @Test
    public void testDispatchToMostSpecificHandler() throws InvocationTargetException {
        // Arrange
        TestEventHandler handler = new TestEventHandler();
        Object source = new Object();

        // Act - dispatch a derived event
        handler.getDispatcher().dispatch(new DerivedEvent(source));

        // Assert - the most specific handler was called
        assertEquals("Derived", handler.lastHandlerCalled);
    }

    /**
     * Tests that a base event is dispatched to the base handler when no derived handler exists.
     */
    @Test
    public void testDispatchToBaseHandlerWhenNoDerivedHandler() throws InvocationTargetException {
        // Arrange
        TestEventHandler handler = new TestEventHandler();
        Object source = new Object();

        // Act - dispatch a base event (not derived)
        handler.getDispatcher().dispatch(new BaseEvent(source));

        // Assert - the base handler was called
        assertEquals("Base", handler.lastHandlerCalled);
    }

    /**
     * Tests that events with no matching handler are silently ignored.
     */
    @Test
    public void testDispatchWithNoMatchingHandler() throws InvocationTargetException {
        // Arrange
        TestEventHandler handler = new TestEventHandler();
        Object source = new Object();

        // Act & Assert - no exception should be thrown for unknown event type
        handler.getDispatcher().dispatch(new UnknownEvent(source));
    }

    /**
     * Tests that the event source is correctly passed to the handler.
     */
    @Test
    public void testEventSourceIsPassedToHandler() throws InvocationTargetException {
        // Arrange
        TestEventHandler handler = new TestEventHandler();
        Object source = new Object();

        // Act
        handler.getDispatcher().dispatch(new DerivedEvent(source));

        // Assert
        assertEquals(source, handler.lastEventSource);
    }

    /**
     * Tests that dispatching with null event throws NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testDispatchWithNullEvent() throws InvocationTargetException {
        // Arrange
        TestEventHandler handler = new TestEventHandler();

        // Act & Assert - null event should cause NullPointerException
        handler.getDispatcher().dispatch(null);
    }

    /**
     * Event handler with testable state for verifying dispatch behavior.
     */
    private static class TestEventHandler {

        volatile String lastHandlerCalled;
        volatile Object lastEventSource;
        @Getter
        private final EventDispatcher dispatcher;

        /**
         * Construct a new TestEventHandler.
         */
        TestEventHandler() {
            dispatcher = new EventDispatcher(this, "handleEvent", BaseEvent.class);
        }

        /**
         * Handle derived events.
         *
         * @param event the derived event
         */
        public void handleEvent(final DerivedEvent event) {
            lastHandlerCalled = "Derived";
            lastEventSource = event.getSource();
        }

        /**
         * Handle base events.
         *
         * @param event the base event
         */
        public void handleEvent(final BaseEvent event) {
            lastHandlerCalled = "Base";
            lastEventSource = event.getSource();
        }

    }

    /**
     * Nested event class that extends BaseEvent but has no specific handler.
     */
    private static class UnknownEvent extends BaseEvent {

        /**
         * Construct a new UnknownEvent.
         *
         * @param source the event source
         */
        UnknownEvent(final Object source) {
            super(source);
        }
    }

    /**
     * Base event class for testing event dispatch hierarchy.
     */
    private static class BaseEvent extends EventObject {

        /**
         * Construct a new BaseEvent.
         *
         * @param source The event source.
         */
        BaseEvent(final Object source) {
            super(source);
        }
    }

    /**
     * Derived event class for testing most-specific handler selection.
     */
    private static class DerivedEvent extends BaseEvent {

        /**
         * Construct a new DerivedEvent.
         *
         * @param source The event source.
         */
        DerivedEvent(final Object source) {
            super(source);
        }
    }
}
