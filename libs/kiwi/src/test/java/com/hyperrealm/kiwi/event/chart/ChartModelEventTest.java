package com.hyperrealm.kiwi.event.chart;
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
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link ChartModelEvent}.
 */
public class ChartModelEventTest {

    /**
     * Tests that the event source is correctly set to the ChartModel.
     */
    @Test
    public void testEventSourceIsChartModel() {
        // Arrange
        Object mockModel = new Object();

        // Act
        ChartModelEvent event = new ChartModelEvent(
            (com.hyperrealm.kiwi.ui.model.chart.ChartModel) mockModel);

        // Assert
        assertEquals(mockModel, event.getSource());
    }

    /**
     * Tests that ChartModelEvent extends EventObject.
     */
    @Test
    public void testExtendsEventObject() {
        // Arrange
        Object mockModel = new Object();

        // Act
        ChartModelEvent event = new ChartModelEvent(
            (com.hyperrealm.kiwi.ui.model.chart.ChartModel) mockModel);

        // Assert
        assertEquals(java.util.EventObject.class, event.getClass().getSuperclass());
    }

    /**
     * Tests constructor with null source.
     */
    @Test
    public void testNullSource() {
        // Act
        ChartModelEvent event = new ChartModelEvent(null);

        // Assert
        assertNull(event.getSource());
    }
}
