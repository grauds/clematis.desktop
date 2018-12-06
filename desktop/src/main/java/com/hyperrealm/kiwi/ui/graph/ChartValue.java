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

import lombok.Data;

/**
 * A class that represents a data value in a chart. A <code>ChartValue</code>
 * consists of a name (which is used to look up the data value in a
 * <code>DataSample</code>), a label (which is used when rendering the legend
 * for a chart), and a color (which is used by the <code>ChartView</code> to
 * render the value).
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.graph.DataSample
 * @see com.hyperrealm.kiwi.ui.graph.ChartView
 * @see com.hyperrealm.kiwi.ui.graph.ChartLegend
 */
@Data
public class ChartValue implements Cloneable {

    private String name;

    private String label;

    private Color color;

    /**
     * Construct a new <code>ChartValue</code> with the specified variable name,
     * label, and color.
     *
     * @param name  The name of the variable that contains this value.
     * @param label The textual label for this value.
     * @param color The color in which to render the graphical representation of
     *              this value.
     */

    public ChartValue(String name, String label, Color color) {
        this.name = name;
        this.label = label;
        this.color = color;
    }

}
