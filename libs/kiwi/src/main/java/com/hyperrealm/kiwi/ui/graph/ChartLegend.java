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
import java.awt.Component;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.ColorSwatch;

/**
 * A component that displays a chart legend. For long legends, consider
 * placing the <code>ChartLegend</code> within a <code>JScrollPane</code>.
 *
 * <p><center><img src="snapshot/ChartLegend.gif"><br>
 * <i>An example ChartLegend.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.graph.ChartView
 */

public class ChartLegend extends JList<ChartValue> {

    private static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(0, 5, 0, 5);

    /**
     * Construct a new <code>ChartLegend</code> for the specified chart
     * definition.
     *
     * @param def The chart definition.
     */

    public ChartLegend(Chart def) {
        DefaultListModel<ChartValue> model = new DefaultListModel<>();

        Iterator<ChartValue> iter = def.getValues();
        while (iter.hasNext()) {
            model.addElement(iter.next());
        }

        setModel(model);

        setCellRenderer(new ChartLegendRenderer());
    }

    /* The list renderer.
     */

    private static class ChartLegendRenderer extends JLabel implements ListCellRenderer<ChartValue> {

        private final ColorSwatch swatch = new ColorSwatch(Color.gray, 10, 10);

        ChartLegendRenderer() {
            setIcon(swatch);
            setBorder(DEFAULT_EMPTY_BORDER);
        }

        public Component getListCellRendererComponent(JList list,
                                                      ChartValue value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean hasFocus) {
            swatch.setColor(value.getColor());
            setText(value.getLabel());
            return (this);
        }
    }

}
