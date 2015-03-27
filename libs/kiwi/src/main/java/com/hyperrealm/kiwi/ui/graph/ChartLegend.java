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

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.ui.*;

/** A component that displays a chart legend. For long legends, consider
 * placing the <code>ChartLegend</code> within a <code>JScrollPane</code>.
 *
 * <p><center><img src="snapshot/ChartLegend.gif"><br>
 * <i>An example ChartLegend.</i>
 * </center>
 *
 * @see com.hyperrealm.kiwi.ui.graph.ChartView
 *
 * @author Mark Lindner
 */

public class ChartLegend extends JList
{
  private static EmptyBorder border = new EmptyBorder(0, 5, 0, 5);
  
  /** Construct a new <code>ChartLegend</code> for the specified chart
   * definition.
   *
   * @param def The chart definition.
   */
  
  public ChartLegend(Chart def)
  {
    DefaultListModel model = new DefaultListModel(); 

    Iterator<ChartValue> iter = def.getValues();
    while(iter.hasNext())
      model.addElement(iter.next());
    
    setModel(model);
    
    setCellRenderer(new ChartLegendRenderer());
  }

  /* The list renderer.
   */

  private class ChartLegendRenderer extends JLabel implements ListCellRenderer
  {
    private ColorSwatch swatch = new ColorSwatch(Color.gray, 10, 10);
    
    ChartLegendRenderer()
    {
      setIcon(swatch);
      setBorder(border);
    }
    
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean hasFocus)
    {
      ChartValue val = (ChartValue)value;
      swatch.setColor(val.getColor());
      setText(val.getLabel());
      return(this);
    }
  }

}

/* end of source file */
