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
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.graph.BasicDataSample;
import com.hyperrealm.kiwi.ui.graph.Chart;
import com.hyperrealm.kiwi.ui.graph.ChartValue;
import com.hyperrealm.kiwi.ui.graph.DataSample;

/**
 * Unit tests for {@link DefaultChartModel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class DefaultChartModelTest {

    /**
     * Test addDataSample.
     */
    @Test
    public void testAddDataSample() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        BasicDataSample ds = new BasicDataSample();
        ds.putValue("x", 10);
        model.addDataSample(ds);
        assertEquals(1, model.getDataSampleCount());
        assertEquals(1, model.getRowCount());
    }

    /**
     * Test removeDataSample.
     */
    @Test
    public void testRemoveDataSample() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        BasicDataSample ds = new BasicDataSample();
        ds.putValue("x", 10);
        model.addDataSample(ds);
        model.removeDataSample(0);
        assertEquals(0, model.getDataSampleCount());
    }

    /**
     * Test clear.
     */
    @Test
    public void testClear() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        BasicDataSample ds = new BasicDataSample();
        ds.putValue("x", 10);
        model.addDataSample(ds);
        model.clear();
        assertEquals(0, model.getDataSampleCount());
        assertEquals(0, model.getRowCount());
    }

    /**
     * Test getRowCount/getColumnCount.
     */
    @Test
    public void testGetRowCountGetColumnCount() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        chart.addValue(new ChartValue("y", "Y", java.awt.Color.BLUE));
        DefaultChartModel model = new DefaultChartModel(chart);
        assertEquals(0, model.getRowCount());
        assertEquals(2, model.getColumnCount());
    }

    /**
     * Test getColumnName.
     */
    @Test
    public void testGetColumnName() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X Label", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        assertEquals("X Label", model.getColumnName(0));
    }

    /**
     * Test isCellEditable.
     */
    @Test
    public void testIsCellEditable() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        assertFalse(model.isCellEditable(0, 0));
    }

    /**
     * Test iterator.
     */
    @Test
    public void testIterator() {
        Chart chart = new Chart();
        chart.addValue(new ChartValue("x", "X", java.awt.Color.RED));
        DefaultChartModel model = new DefaultChartModel(chart);
        BasicDataSample ds = new BasicDataSample();
        ds.putValue("x", 10);
        model.addDataSample(ds);
        int count = 0;
        for (DataSample sample : model) {
            count++;
        }
        assertEquals(1, count);
    }
}
