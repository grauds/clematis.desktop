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

/* Original paint logic lifted from 3D Pie Chart applet by Ciaran Treanor
 * <ciaran@broadcom.ie>.
 */

package com.hyperrealm.kiwi.ui.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/** A chart that renders the sum total of the values of each variable across
 * data samples as a wedge in a circular "pie" whose whole represents the sum
 * total of all values from all data samples. This type of chart is used to
 * compare the cumulative contributions of values to the total.
 *
 * <p><center><img src="snapshot/PieChart3D.gif"><br>
 * <i>An example PieChart3D.</i>
 * </center>
 *
 * @author Ciaran Treanor
 * @author Mark Lindner
 */

public class PieChart3D extends ChartView
{
  private double aspectRatio = 2.5;
  private int pieDepth = 5, pieWidth = 250, pieHeight;
  private int cx, cy, rx, ry;
  private boolean drawLabels = true;

  /** Construct a new <code>PieChart3D</code> for the specified chart
   * definition.
   *
   * @param chart The chart definition.
   */
  
  public PieChart3D(Chart chart)
  {
    super(chart);

    _init();
  }

  /*
   */
  
  private void _init()
  {
    pieHeight = (int)(pieWidth / aspectRatio);
    
    rx = pieWidth / 2;
    ry = pieHeight / 2;
    
    cx = rx + horizontalPad;
    cy = ry + verticalPad;
  }

  /** Set the width of the pie.
   *
   * @param pieWidth The width, in pixels.
   */

  public void setPieWidth(int pieWidth)
  {
    this.pieWidth = pieWidth;

    _init();
  }

  /** Get the width of the pie.
   *
   * @return The width, in pixels.
   */
  
  public int getPieWidth()
  {
    return(pieWidth);
  }

  /** Set the depth of the pie.
   *
   * @param pieDepth The depth, in pixels.
   */

  public void setPieDepth(int pieDepth)
  {
    this.pieDepth = pieDepth;

    _init();
  }

  /** Get the depth of the pie.
   *
   * @return The depth, in pixels.
   */
  
  public int getPieDepth()
  {
    return(pieDepth);
  }
    
  /** Paint the chart.
   */

  /** Specify whether labels will be drawn beside each slice in the pie
   * chart.
   *
   * @since Kiwi 1.4.3
   */

  public void setDrawsLabels(boolean flag)
  {
    drawLabels = flag;
  }

  /** Determine whether labels will be drawn beside each slice in the pie
   * chart.
   *
   * @since Kiwi 1.4.3
   */

  public boolean getDrawsLabels()
  {
    return(drawLabels);
  }

  /**
   */
  
  protected void paintChart(Graphics gc)
  {
    int startAngle;
    double angle;
    Dimension d = getSize();
    FontMetrics fm = getFontMetrics(getFont());

    horizontalPad = (int)((d.getWidth() - pieWidth) / 2);
    verticalPad = (int)((d.getHeight() - (pieHeight + pieDepth)) / 2);

    cx = rx + horizontalPad;
    cy = ry + verticalPad;

    // precompute the slices

    int sliceCount = chart.getValueCount();
    double slices[] = new double[sliceCount];
    Color colors[] = new Color[sliceCount];
    double total = 0.0;
    
    for(int i = 0; i < sliceCount; i++)
    {
      ChartValue cv = (ChartValue)chart.getValueAt(i);
      String var = cv.getName();
      colors[i] = cv.getColor();

      Iterator<DataSample> iter  = model.iterator();
      while(iter.hasNext())
      {
        DataSample ds = iter.next();
        Object o = ds.getValue(var);
        double value = 0.0;
        if((o != null) && (o instanceof Number))
          value = ((Number)o).doubleValue();

        slices[i] += value;
        total += value;
      }
    }

    // This is less than optimal, but we don't have a floodfill.
    // Draw pieDepth-1 ovals in a darker color
    
    for(int x = pieDepth; x > 0; x--)
    {
      startAngle = -45;

      
      for(int i = 0; i < sliceCount; i++)
      {
        gc.setColor(colors[i].darker());
        angle = Math.round(360 * (slices[i] / total));
        gc.fillArc(horizontalPad, verticalPad + x, pieWidth,
                   (int)(pieWidth / aspectRatio), startAngle, (int)angle);
        startAngle += angle;
      }
    }

    // Now draw the final (top) oval in the undarkened color
    
    startAngle = -45;
    for(int i = 0; i < sliceCount; i++)
    {
      gc.setColor(colors[i]);
      angle = Math.round(360 * (slices[i] / total));
      gc.fillArc(horizontalPad, verticalPad, pieWidth,
                 (int)(pieWidth / aspectRatio), startAngle, (int)angle);
      startAngle += angle;
    }

    // add labels

    if(drawLabels)
    {
      startAngle = -45;
      gc.setColor(Color.black);
    
      double bisect, sx, sy;

      for(int i = 0; i < sliceCount; i++)
      {
        angle = Math.round(360 * (slices[i] / total));
        bisect = (startAngle + angle / 2.0) * Math.PI / 180.0;

        sx = 1.2 * rx * Math.cos(bisect);
        sy = 1.2 * ry * Math.sin(bisect);

        String label = lm.formatDecimal(slices[i], precision)
          + " (" + lm.formatPercentage((slices[i] / total), precision) + ")";

        if(bisect < 0.0)
          sy -= (fm.getAscent() + pieDepth);
        else if(bisect < Math.PI / 2.0)
          ;
        else if(bisect < Math.PI)
          sx -= fm.stringWidth(label);
        else
        {
          /* bisect < 270 */
          sx -= fm.stringWidth(label);
          sy -= (fm.getAscent() + pieDepth);
        }

        gc.drawString(label, (int)(sx + cx), (int)(-sy + cy));
      
        startAngle += angle;
      }
    }
  }

}

/* end of source file */
