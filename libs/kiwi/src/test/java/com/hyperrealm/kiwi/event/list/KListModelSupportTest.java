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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link KListModelSupport}.
 * Tests event firing for list model changes.
 */
@SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KListModelSupportTest {

    /**
     * Tests adding and removing list model listeners.
     */
    @Test
    public void testAddAndRemoveListModelListener() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        List<String> receivedEvents = new ArrayList<>();

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                receivedEvents.add("added");
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };

        support.addListModelListener(listener);
        support.fireItemAdded(0);

        // Assert - listener received the event
        assertEquals(1, receivedEvents.size());

        // Remove listener and fire again
        support.removeListModelListener(listener);
        support.fireItemAdded(1);

        // Assert - listener no longer receives events
        assertEquals(1, receivedEvents.size());
    }

    /**
     * Tests firing item added event for a single item.
     */
    @Test
    public void testFireItemAdded() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] endIndex = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                startIndex[0] = evt.getStartIndex();
                endIndex[0] = evt.getEndIndex();
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemAdded(5);

        // Assert - single item at index 5
        assertEquals(5, startIndex[0]);
        assertEquals(5, endIndex[0]);
    }

    /**
     * Tests firing items added event for a range of items via individual calls.
     */
    @Test
    public void testFireItemsAdded() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] endIndex = {0};
        int[] callCount = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                callCount[0]++;
                startIndex[0] = evt.getStartIndex();
                endIndex[0] = evt.getEndIndex();
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act - fireItemAdded(index) calls fireItemsAdded(index, index) internally
        support.fireItemAdded(2);
        support.fireItemAdded(7);

        // Assert - verify two separate single-item events were fired
        assertEquals(2, callCount[0]);
        assertEquals(7, startIndex[0]);
        assertEquals(7, endIndex[0]);
    }

    /**
     * Tests firing item removed event for a single item.
     */
    @Test
    public void testFireItemRemoved() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] index = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
                index[0] = evt.getStartIndex();
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemRemoved(3);

        // Assert
        assertEquals(3, index[0]);
    }

    /**
     * Tests firing items removed event for a range of items.
     */
    @Test
    public void testFireItemsRemoved() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] endIndex = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
                startIndex[0] = evt.getStartIndex();
                endIndex[0] = evt.getEndIndex();
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemsRemoved(1, 4);

        // Assert
        assertEquals(1, startIndex[0]);
        assertEquals(4, endIndex[0]);
    }

    /**
     * Tests firing item changed event for a single item.
     */
    @Test
    public void testFireItemChanged() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] endIndex = {0};
        final int[] field = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
                startIndex[0] = evt.getStartIndex();
                endIndex[0] = evt.getEndIndex();
                field[0] = evt.getField();
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemChanged(5);

        // Assert - single item changed, field is -1 (unknown)
        assertEquals(5, startIndex[0]);
        assertEquals(5, endIndex[0]);
        assertEquals(-1, field[0]);
    }

    /**
     * Tests firing item changed event with a specific field number.
     */
    @Test
    public void testFireItemChangedWithField() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] field = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
                startIndex[0] = evt.getStartIndex();
                field[0] = evt.getField();
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemChanged(3, 2);

        // Assert - field 2 changed
        assertEquals(3, startIndex[0]);
        assertEquals(2, field[0]);
    }

    /**
     * Tests firing data-changed event.
     */
    @Test
    public void testFireDataChanged() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final int[] startIndex = {0};
        final int[] endIndex = {0};
        final int[] field = {0};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
                startIndex[0] = evt.getStartIndex();
                endIndex[0] = evt.getEndIndex();
                field[0] = evt.getField();
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireDataChanged();

        // Assert - data-changed event has -1 for all indices
        assertEquals(-1, startIndex[0]);
        assertEquals(-1, endIndex[0]);
        assertEquals(-1, field[0]);
    }

    /**
     * Tests that multiple listeners all receive events.
     */
    @Test
    public void testMultipleListenersAllReceiveEvents() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        List<String> listener1Events = new ArrayList<>();
        List<String> listener2Events = new ArrayList<>();

        KListModelListener listener1 = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                listener1Events.add("added");
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };

        KListModelListener listener2 = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                listener2Events.add("added");
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };

        support.addListModelListener(listener1);
        support.addListModelListener(listener2);

        // Act
        support.fireItemAdded(0);

        // Assert - both listeners received the event
        assertEquals(1, listener1Events.size());
        assertEquals(1, listener2Events.size());
    }

    /**
     * Tests that firing events with no listeners does not throw exceptions.
     */
    @Test
    public void testFireEventWithNoListeners() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);

        // Act & Assert - no exceptions should be thrown
        support.fireItemAdded(0);
        support.fireItemRemoved(0);
        support.fireItemChanged(0);
        support.fireDataChanged();
    }

    /**
     * Tests that the event source is correctly set to the owner object.
     */
    @Test
    public void testEventSourceIsCorrect() {
        // Arrange
        Object source = new Object();
        KListModelSupport support = new KListModelSupport(source);
        final Object[] receivedSource = {null};

        KListModelListener listener = new KListModelListener() {
            @Override
            public void itemsAdded(final KListModelEvent evt) {
                receivedSource[0] = evt.getSource();
            }

            @Override
            public void itemsRemoved(final KListModelEvent evt) {
            }

            @Override
            public void itemsChanged(final KListModelEvent evt) {
            }

            @Override
            public void dataChanged(final KListModelEvent evt) {
            }
        };
        support.addListModelListener(listener);

        // Act
        support.fireItemAdded(0);

        // Assert
        assertEquals(source, receivedSource[0]);
    }
}
