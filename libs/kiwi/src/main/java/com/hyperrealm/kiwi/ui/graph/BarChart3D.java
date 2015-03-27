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

import com.hyperrealm.kiwi.util.*;

/** A base class for bar charts that provides some general painting logic.
 *
 * @author Mark Lindner
 */

public abstract class BarChart3D extends ChartView
{
  /** The width of a bar.
   */
  
  protected int barWidth = 10;

  /** The amount of space between bars (or bar clusters).
   */
  
  protected int barSpacing = 20;

  /** The depth of a bar (shading).
   */
  
  protected int barDepth = barWidth / 2;

  /** Construct a new <code>BarChart3D</code> for the specified chart
   * definition and orientation.
   *
   * @param chart The chart definition.
   * @param orientation The orientation; one of the symbolic constants
   * <code>HORIZONTAL</code> or <code>VERTICAL</code>.
   */
  
  public BarChart3D(Chart chart, int orientation)
  {
    super(chart);
    setOrientation(orientation);

    setTickInterval(chart.getTickInterval());
  }

  /** Draw a horizontal bar.
   *
   * @param gc The graphics context.
   * @param x The x-coordinate of the upper left corner of the base.
   * @param y The y-coordinate of the upper left corner of the base.
   * @param value The value that this bar represents.
   * @param color The color of the bar.
   *
   * @return The x-coordinate of the end of the bar.
   */
  
  protected int drawHorizontalBar(Graphics gc, int x, int y, double value,
                                  Color color)
  {
    int px, py;
    int barLength = (int)(value * scale);
    int ox = x;
    int oy = y;

    // draw face of bar
    
    gc.setColor(color);
    gc.fillRect(ox, oy, barLength, barWidth);

    // draw shadow (above)

    px = ox;
    py = oy - 1;

    gc.setColor(color.darker());
    for(int i = 0; i < barDepth; i++)
    {
      gc.drawLine(px, py, px + barLength, py);
      px++;
      py--;
    }

    // draw end of bar

    px = ox + barLength - 1;
    py = oy - 1;

    gc.setColor(color);
    for(int i = 0; i < barDepth; i++)
    {
      gc.drawLine(px, py, px, py + barWidth);
      px++;
      py--;
    }

    // return the x coordinate of the end of this bar

    return(x + barLength);
  }
  
  /** Draw a vertical bar.
   *
   * @param gc The graphics context.
   * @param x The x-coordinate of the lower left corner of the base.
   * @param y The y-coordinate of the lower left corner of the base.
   * @param value The value that this bar represents.
   * @param color The color of the bar.
   *
   * @return The y-coordinate of the top of the bar.
   */
  
  protected int drawVerticalBar(Graphics gc, int x, int y, double value,
                                Color color)
  {
    int barDepth = barWidth / 2;
    int px, py;
    int barLength = (int)(value * scale);
    int ox = x;
    int oy = getSize().height - y - barLength;
    
    // draw face of bar
    
    gc.setColor(color);
    gc.fillRect(ox, oy, barWidth, barLength);

    // draw shadow (right side)

    px = ox + barWidth;
    py = oy;

    gc.setColor(color.darker());
    for(int i = 0; i < barDepth; i++)
    {
      gc.drawLine(px, py, px, py + barLength - 1);
      px++;
      py--;
    }

    // draw top of bar

    px = ox;
    py = oy - 1;

    gc.setColor(color);
    for(int i = 0; i < barDepth; i++)
    {
      gc.drawLine(px, py, px + barWidth + 1, py);
      px++;
      py--;
    }

    // return the y coordinate of the top of this bar
    
    return(y + barLength);
  }
  
}

/* end of source file */
