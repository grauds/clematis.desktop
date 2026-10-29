package com.hyperrealm.kiwi.ui.model.chart;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.graph.Chart;
import com.hyperrealm.kiwi.ui.graph.ChartLegend;
import com.hyperrealm.kiwi.ui.graph.ChartValue;

/**
 * Unit tests for {@link ChartLegend}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ChartLegendTest {

    /**
     * Test legend entry management.
     */
    @Test
    public void testLegendEntryManagement() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X Value", java.awt.Color.RED));
        chart.addValue(new ChartValue("y", "Y Value", java.awt.Color.BLUE));

        ChartLegend legend =
            new ChartLegend(chart);
        assertNotNull(legend);
        assertEquals(2, legend.getModel().getSize());
    }

    /**
     * Test with single value.
     */
    @Test
    public void testSingleValue() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));

        ChartLegend legend =
            new ChartLegend(chart);
        assertEquals(1, legend.getModel().getSize());
    }
}
