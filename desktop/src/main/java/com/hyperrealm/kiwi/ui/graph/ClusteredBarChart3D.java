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

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

/**
 * A bar chart that renders each data sample as a cluster of bars; each
 * bar in the cluster represents one of the values in the data sample. This
 * type of chart is used to compare values across data samples.
 *
 * <p><center><img src="snapshot/ClusteredBarChart3D.gif"><br>
 * <i>An example ClusteredBarChart3D.</i>
 * </center>
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public class ClusteredBarChart3D extends BarChart3D {

    /**
     * Construct a new <code>ClusteredBarChart3D</code> for the specified chart
     * definition and with the specified orientation.
     *
     * @param chart       The chart definition.
     * @param orientation The orientation of the chart; one of the constants
     *                    <code>VERTICAL</code> or <code>HORIZONTAL</code> defined in
     *                    <code>ChartView</code>.
     */

    public ClusteredBarChart3D(Chart chart, int orientation) {
        super(chart, orientation);
    }

    /**
     * Paint the chart.
     */

    protected void paintChart(Graphics gc) {
        int cx = horizontalPad;
        int cy = verticalPad;

        if (orientation == HORIZONTAL) {
            cy += BAR_DEPTH + scaleWidth + verticalPad;
        } else {
            cx += scaleWidth + horizontalPad;
        }

        // loop over the bar clusters

        for (DataSample ds : model) {
            Iterator<ChartValue> viter = chart.getValues();
            int valueCount = chart.getValueCount();
            int skip = (valueCount - 1) * BAR_LENGTH;
            if (orientation == HORIZONTAL) {
                cy += skip;
            }

            // loop over the bars in a cluster; if it's a horizontal bar chart,
            // we have to draw them in reverse, because the shadows are above the
            // bars

            while (viter.hasNext()) {
                ChartValue cv = viter.next();

                Color color = cv.getColor();
                Object o = ds.getValue(cv.getName());
                double value = 0.0;
                if (o instanceof Number) {
                    value = ((Number) o).doubleValue();
                }

                switch (orientation) {
                    case VERTICAL:
                    default: {
                        drawVerticalBar(gc, cx, verticalPad, value, color);
                        cx += BAR_LENGTH;
                        break;
                    }

                    case HORIZONTAL: {
                        drawHorizontalBar(gc, horizontalPad, cy, value, color);
                        cy -= BAR_LENGTH;
                        break;
                    }
                }
            }

            if (orientation == VERTICAL) {
                cx += BAR_SPACING;
            } else {
                cy += skip + BAR_LENGTH + BAR_SPACING;
            }
        }

        // draw the scale

        switch (orientation) {

            case HORIZONTAL:
                drawHorizontalScale(gc, verticalPad + scaleWidth);
                break;

            case VERTICAL:
                drawVerticalScale(gc, horizontalPad + scaleWidth);
                break;

            default:
        }
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
