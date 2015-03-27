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
import java.io.*;
import java.util.*;

import com.hyperrealm.kiwi.text.*;

/** A class that represents a chart definition. The chart definition describes
 * various aspects of the chart, including the data values that the chart
 * displays, the decimal precision of the data values, the intervals at which
 * tickmarks should be drawn on the chart's scale, and the maximum number of
 * data samples to display in the chart.
 *
 * @author Mark Lindner
 */

public class Chart
{
  private String name;
  private String type = null;
  private double tickInterval = 10.0;
  private int precision = 2;
  private int maxSampleCount = 10;
  private ArrayList<ChartValue> values;

  /** Construct a new, unnamed <code>Chart</code>.
   */
  
  public Chart()
  {
    this(null);
  }
  
  /** Construct a new <code>Chart</code> with the specified name.
   *
   * @param name The name of the chart.
   */

  public Chart(String name)
  {
    this.name = name;

    values = new ArrayList<ChartValue>();
  }

  /** Add a <code>ChartValue</code> to this chart definition.
   *
   * @param value The value to add.
   */
  
  public void addValue(ChartValue value)
  {
    values.add(value);
  }

  /** Add a <code>ChartValue</code> to this chart definition at the specified
   * position.
   *
   * @param value The value to add.
   * @param position The position to add the value at.
   */
  
  public void addValue(ChartValue value, int position)
  {
    values.add(position, value);
  }

  /** Get the value at the specified position in this chart definition.
   *
   * @param position The position of the value.
   * @return the <code>ChartValue</code> object at the specified position.
   */
  
  public ChartValue getValueAt(int position)
  {
    return(values.get(position));
  }

  /** Get an iterator to the values in this chart.
   *
   * @since Kiwi 2.1
   */

  public Iterator<ChartValue> getValues()
  {
    return(values.iterator());
  }

  /** Remove the value at the specified position from this chart definition.
   *
   * @param position The position of the value.
   */
  
  public void removeValueAt(int position)
  {
    values.remove(position);
  }

  /** Remove all of the values from this chart definition.
   */

  public void removeAllValues()
  {
    values.clear();
  }

  /** Get the number of values in this chart definition.
   *
   * @return The value count.
   */
  
  public int getValueCount()
  {
    return(values.size());
  }

  /** Set the tick interval for this chart definition. The tick interval is
   * specified (conceptually) in the same units  as the actual data values in
   * the chart.
   *
   * @param interval The tick interval.
   */
  
  public void setTickInterval(double interval)
  {
    if(interval < 0.01)
      throw(new IllegalArgumentException("Invalid tick interval"));
    
    tickInterval = interval;
  }

  /** Get the tick interval for this chart definition.
   *
   * @return The tick interval.
   */
  
  public double getTickInterval()
  {
    return(tickInterval);
  }

  /** Set the decimal precision for this chart definition. This value
   * determines how many decimal places are significant in the data values
   * that are displayed by this chart; the values in the chart scale will also
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

  /** Get the decimal precision for this chart definition.
   *
   * @return The precision.
   */
  
  public int getPrecision()
  {
    return(precision);
  }

  /** Get the name for this chart definition.
   *
   * @return The name of this chart definition.
   */
  
  public String getName()
  {
    return(name);
  }

  /** Get the chart type for this chart definition.
   *
   * @return The chart type.
   */

  public String getType()
  {
    return(type);
  }

  /** Set the chart type for this chart definition.
   *
   * @param type The chart type.
   */
  
  public void setType(String type)
  {
    this.type = type;
  }

  /** Set the maximum number of data samples that will be displayed in the
   * chart. This essentially specifies how many data samples will be used to
   * generate the chart. The value must be at least 1.
   *
   * @param maxSampleCount The maximum sample count.
   */

  public void setMaxSampleCount(int maxSampleCount)
  {
    if(maxSampleCount < 1)
      throw(new IllegalArgumentException("Invalid max sample count"));

    this.maxSampleCount = maxSampleCount;
  }

  /** Get the maximum number of data samples that will be displayed in the
   * chart.
   *
   * @return The maximum sample count.
   */
  
  public int getMaxSampleCount()
  {
    return(maxSampleCount);
  }

  /** Write this chart definition to a stream.
   *
   * @param outs The stream to read write to.
   * @throws java.io.IOException If an I/O error occurs.
   */
  
  public void write(BufferedWriter outs) throws IOException
  {
    outs.write(name);
    outs.newLine();

    outs.write(type);
    outs.newLine();

    outs.write(String.valueOf(tickInterval));
    outs.newLine();

    outs.write(String.valueOf(precision));
    outs.newLine();

    outs.write(String.valueOf(maxSampleCount));
    outs.newLine();

    Iterator<ChartValue> iter = getValues();
    while(iter.hasNext())
    {
      ChartValue v = iter.next();
      outs.write(v.getName());
      outs.newLine();
      outs.write(v.getLabel());
      outs.newLine();
      outs.write(ColorFormatter.format(v.getColor()));
      outs.newLine();
    }
    
    outs.newLine();
  }

  /** Read this chart definition from a stream.
   *
   * @param ins The stream to read from.
   * @throws java.io.IOException If an I/O error occurs.
   */
  
  public void read(BufferedReader ins) throws IOException
  {
    String s, t, u;

    s = readLine(ins);
    name = s;

    s = readLine(ins);
    type = s;

    s = readLine(ins);
    tickInterval = Double.parseDouble(s);

    s = readLine(ins);
    precision = Integer.parseInt(s);

    s = readLine(ins);
    maxSampleCount = Integer.parseInt(s);
    
    removeAllValues();
    
    for(;;)
    {
      s = readLine(ins);
      if(s.trim().length() == 0)
        break;
      t = readLine(ins);
      if(t.trim().length() == 0)
        break;
      u = readLine(ins);
      if(u.trim().length() == 0)
        break;

      Color c = Color.gray;

      try
      {
        c = ColorFormatter.parse(u);
      }
      catch(ParsingException ex)
      {
      }

      ChartValue v = new ChartValue(s, t, c);
      addValue(v);
    }
  }

  /*
   */

  private String readLine(BufferedReader reader) throws IOException
  {
    String s = reader.readLine();
    if(s == null)
      throw(new IOException("End of file."));
    
    return(s);
  }

  /** Get a string representation of this chart.
   *
   * @return The name of the chart.
   */
  
  public String toString()
  {
    return(name);
  }
  
}

/* end of source file */
