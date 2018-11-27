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
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.ui.model.*;
import com.hyperrealm.kiwi.util.*;

/** A base class for chart components that provides basic rendering logic.
 * <code>ChartView</code> provides logic to recalculate the scale and repaint
 * the chart when the component is resized or when the data model changes.
 * <p>
 * The <i>scale</i> value is the ratio of pixels to units, and is computed by 
 * dividing the width (or height, depending on the orientation) of the
 * component in pixels by the maximum value to be plotted in this chart. The
 * calculation of the  <i>maximum value</i> is a chart-specific; the method
 * <code>getMaxValue()</code> must be overridden to perform this calculation.
 * <p>
 * The method <code>paintChart()</code> should be overridden to paint the
 * chart and (if necessary) the chart scale. Convenience methods are provided
 * for rendering chart scales. By convention, chart scales appear on the left
 * for vertical charts and at the top for horizontal charts.
 *
 * @author Mark Lindner
 */

public abstract class ChartView extends JComponent
  implements ChartModelListener
{
  private double maxValue = 0.0;
  private double tickInterval = 10.0;

  /** A vertical orientation. */
  public static final int VERTICAL = 0;
  /** A horizontal orientation. */
  public static final int HORIZONTAL = 1;
  /** The data model for this view. */
  protected ChartModel model = null;
  /** The scale factor for this view. */
  protected double scale = 1.0;
  /** The horizontal padding for this view. */
  protected int horizontalPad = 10;
  /** The vertical padding for this view. */
  protected int verticalPad = 10;
  /** A cached reference to the locale manager. */
  protected LocaleManager lm = LocaleManager.getDefault();
  /** The orientation for this view. */
  protected int orientation = VERTICAL;
  /** The precision for this view. */
  protected int precision = 2;
  /** The chart definition for this view. */
  protected Chart chart;
  /** The width of the scale, in pixels. */
  protected int scaleWidth = 50;
  
  /** Construct a new <code>ChartView</code> for the specified chart
   * definition.
   *
   * @param chart The chart definition.
   */
  
  protected ChartView(Chart chart)
  {
    setChart(chart);
    
    setFont(new Font("Dialog", Font.PLAIN, 10));
    setBackground(Color.white);
  }

  /** Set the chart definition. The precision and tick interval from the chart
   * definition are automatically copied into the view.
   *
   * @param chart The chart definition.
   */

  public void setChart(Chart chart)
  {
    this.chart = chart;

    setPrecision(chart.getPrecision());
    setTickInterval(chart.getTickInterval());
  }

  /** Get the chart definition.
   *
   * @return The chart definition.
   */
  
  public Chart getChart()
  {
    return(chart);
  }
  
  /** Set the data model for this view.
   *
   * @param model The data model.
   */
  
  public void setModel(ChartModel model)
  {
    if (this.model != null)
      this.model.removeChartModelListener(this);
    this.model = model;
    model.addChartModelListener(this);
  }

  /** Get the data model for this view.
   *
   * @return The data model.
   */

  public ChartModel getModel()
  {
    return(model);
  }

  /** Handle <i>chart data changed</i> events. In response to data model
   * events, the scale is recalculated and the chart is redrawn.
   *
   * @param event The event.
   */

  public void chartDataChanged(ChartModelEvent event)
  {
    recalculateScale();
    repaint();
  }

  /** Paint the component. This method should not be overridden by
   * subclassers.
   */
  
  public void paintComponent(Graphics gc)
  {
    Dimension d = getSize();
    gc.setColor(getBackground());
    gc.fillRect(0, 0, d.width, d.height);

    if(model != null)
      paintChart(gc);
  }

  /** Set the chart orientation.
   *
   * @param orientation The orientation; one of the constants
   * <code>HORIZONTAL</code> or <code>VERTICAL</code>.
   */

  public void setOrientation(int orientation)
  {
    if(orientation < VERTICAL || orientation > HORIZONTAL)
      throw(new IllegalArgumentException("bad orientation value"));

    this.orientation = orientation;
  }

  /** Set the horizontal padding. The padding value is used for the margins of
   * the chart as well as the space between the chart and the scale.
   *
   * @param hpad The horizontal padding, in pixels.
   *
   */

  public void setHorizontalPad(int hpad)
  {
    horizontalPad = hpad;
  }

  /** Set the vertical padding. The padding value is used for the margins of
   * the chart as well as the space between the chart and the scale.
   *
   * @param vpad The vertical padding, in pixels.
   *
   */
  
  public void setVerticalPad(int vpad)
  {
    verticalPad = vpad;
  }

  /** Get the horizontal padding.
   *
   * @return The horizontal padding, in pixels.
   */
  
  public int getHorizontalPad()
  {
    return(horizontalPad);
  }

  /** Get the vertical padding.
   *
   * @return The vertical padding, in pixels.
   */
  
  public int getVerticalPad()
  {
    return(verticalPad);
  }

  /** Set the tick interval for this view. The tick interval is specified
   * (conceptually) in the same units as the actual data values in the chart.
   *
   * @param tickInterval The tick interval.
   */
  
  public void setTickInterval(double tickInterval)
  {
    this.tickInterval = tickInterval;
  }

  /** Get the tick interval for this view.
   *
   * @return The tick interval.
   */

  public double getTickInterval()
  {
    return(tickInterval);
  }

  /** Set the decimal precision for this view. This value
   * determines how many decimal places are significant in the data values
   * that are displayed by this view; the values in the chart scale will also
   * be rendered to this many decimal places.
   *
   * @param precision The precision.
   */

  public void setPrecision(int precision)
  {
    if(precision < 0 || precision > 10)
      throw(new IllegalArgumentException("Invalid precision"));
    
    this.precision = precision;
  }

  /** Get the decimal precision for this view.
   *
   * @return The precision.
   */
  
  public int getPrecision()
  {
    return(precision);
  }
  
  /** Paint the chart.
   */
  
  protected abstract void paintChart(Graphics gc);

  /** Determine if this chart has a scale. Some charts (notably pie charts)
   * do not have a scale. The default implementation returns <code>true</code>.
   * 
   * @return <code>true</code> if the chart has a scale, <code>false</code>
   * otherwise.
   */
  
  protected boolean hasScale()
  {
    return(true);
  }

  /*
   */

  private void recalculateScale()
  {
    if(hasScale())
    {
      Dimension d = getSize();
    
      maxValue = getMaxValue();
    
      if(orientation == VERTICAL)
        scale = (d.height - (2 * verticalPad)) / maxValue;
      else
        scale = (d.width - (2 * horizontalPad)) / maxValue;
    }
  }
  
  /** Overridden to recalculate the scale when the component geometry
   * changes.
   */
  
  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);

    recalculateScale();
  }

  /** Compute the maximum value. The default implementation returns
   * <code>0.0</code>.
   *
   * @return The maximum value to be plotted.
   */

  protected double getMaxValue()
  {
    return(0.0);
  }

  /** A convenience method for drawing a vertical scale with tickmarks and
   * tick labels.
   *
   * @param gc The graphics context.
   * @param x The x-coordinate of the scale.
   */
  
  protected void drawVerticalScale(Graphics gc, int x)
  {
    Dimension d = getSize();
    FontMetrics fm = gc.getFontMetrics(getFont());
    int fh = fm.getAscent() / 2;
    int y = d.height - verticalPad;

    gc.setColor(Color.black);
    gc.drawLine(x, y, x, verticalPad);
    
    for(double tick = 0.0; tick <= maxValue; tick += tickInterval)
    {
      int yp = (int)(y - (tick * scale));
      gc.drawLine(x - 1, yp, x - 4, yp);

      String label = lm.formatDecimal(tick, precision);
      gc.drawString(label, x - 4 - fm.stringWidth(label), yp + fh);
    }
  }

  /** A convenience method for drawing a horizontal scale with tickmarks and
   * tick labels.
   *
   * @param gc The graphics context.
   * @param y The y-coordinate of the scale.
   */

  protected void drawHorizontalScale(Graphics gc, int y)
  {
    Dimension d = getSize();
    FontMetrics fm = gc.getFontMetrics(getFont());
    int fh = fm.getAscent() / 2;
    int x = horizontalPad;

    gc.setColor(Color.black);
    gc.drawLine(x, y, d.width - horizontalPad, y);

    for(double tick = 0.0; tick <= maxValue; tick += tickInterval)
    {
      int xp = (int)(x + (tick * scale));
      gc.drawLine(xp, y - 1, xp, y - 4);
    }

    Graphics2D gc2d = (Graphics2D)gc;
    Paint paint = Color.black;
    gc2d.setPaint(paint);
    AffineTransform rotate = new AffineTransform(0, 1, -1, 0, 0, 0);
    gc2d.transform(rotate);

    for(double tick = 0.0; tick <= maxValue; tick += tickInterval)    
    {
      int xp = (int)(x + (tick * scale));

      String label = lm.formatDecimal(tick, precision);
      // in the rotated coordinate system, (x', y') = (y, -x)
      gc2d.drawString(label, y - 5 - fm.stringWidth(label), -(xp - fh));
    }
  }
  
}

/* end of source file */
