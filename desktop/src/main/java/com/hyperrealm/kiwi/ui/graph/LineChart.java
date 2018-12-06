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

package com.hyperrealm.kiwi.ui.graph;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;

/**
 * A chart that renders the value of each variable as a series of points
 * connected by lines. This type of chart is used to illustrate trends across
 * data samples.
 *
 * <p><center><img src="snapshot/LineChart.gif"><br>
 * <i>An example LineChart.</i>
 * </center>
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public class LineChart extends ChartView {

    private static final int DEFAULT_POINT_SPACING = 40;

    private int pointSpacing = DEFAULT_POINT_SPACING;

    private int pointRadius = 2;

    private int pointSize;

    /**
     * Construct a new <code>LineChart</code> for the specified chart
     * definition.
     *
     * @param chart The chart definition.
     */

    public LineChart(Chart chart) {
        super(chart);

        setOrientation(VERTICAL);
        setTickInterval(chart.getTickInterval());
        setPointRadius(2);
    }

    /**
     * Get the point spacing (the space between the points on the line, in
     * pixels.)
     *
     * @return The point spacing, in pixels.
     * @since Kiwi 1.4.3
     */

    public int getPointSpacing() {
        return (pointSpacing);
    }

    /**
     * Set the point spacing (the space between points on the line, in pixels)
     *
     * @param spacing The new spacing. The value must be greater than zero.
     * @since Kiwi 1.4.3
     */

    public void setPointSpacing(int spacing) {
        if (spacing > 0) {
            pointSpacing = spacing;
        }
    }

    /**
     * Get the point radius (the size of a point on the line, in pixels)
     *
     * @return The point radius, in pixels.
     * @since Kiwi 1.4.3
     */

    public int getPointRadius() {
        return (pointRadius);
    }

    /**
     * Set the point radius (the size of a point on the line, in pixels)
     *
     * @param radius The new radius. The value must be greater than or equal
     *               to zero.
     * @since Kiwi 1.4.3
     */

    public void setPointRadius(int radius) {
        if (radius >= 0) {
            pointRadius = radius;
        }

        pointSize = (pointRadius * 2) + 1;
    }

    /**
     * Paint the chart.
     */

    protected void paintChart(Graphics gc) {
        int lx = 0, ly = 0, px, py;
        Dimension d = getSize();

        Iterator<ChartValue> viter = chart.getValues();
        int scaleWidth = DEFAULT_SCALE_WIDTH;
        while (viter.hasNext()) {
            boolean first = true;
            px = (horizontalPad * 2) + scaleWidth;

            ChartValue cv = viter.next();
            String var = cv.getName();
            gc.setColor(cv.getColor());

            for (DataSample ds : model) {
                Object o = ds.getValue(var);
                double value = 0.0;
                if (o instanceof Number) {
                    value = ((Number) o).doubleValue();
                }

                py = (int) (d.height - verticalPad - (value * scale));

                if (!first) {
                    gc.drawLine(lx, ly, px, py);
                }

                gc.fillRect(px - pointRadius, py - pointRadius, pointSize, pointSize);
                lx = px;
                ly = py;
                first = false;

                px += pointSpacing;
            }
        }

        drawVerticalScale(gc, horizontalPad + scaleWidth);
    }

    /**
     * Compute the maximum value.
     */

    protected double getMaxValue() {
        double maxval = 0.0;

        Iterator<ChartValue> viter = chart.getValues();
        while (viter.hasNext()) {
            ChartValue cv = viter.next();
            String var = cv.getName();

            for (DataSample ds : model) {
                Object o = ds.getValue(var);
                double value = 0.0;
                if (o instanceof Number) {
                    value = ((Number) o).doubleValue();
                }

                if (value > maxval) {
                    maxval = value;
                }
            }
        }

        return (maxval);
    }

}
