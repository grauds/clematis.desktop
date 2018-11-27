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

import java.util.HashMap;

/**
 * A basic implementation of the <i>DataSample</i> interface.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class BasicDataSample implements DataSample
{
  private HashMap data;

  /**
   * Construct a new <code>BasicDataSample</code>.
   */
  
  public BasicDataSample()
  {
    data = new HashMap();
  }

  /**
   */

  public Object getValue(String var)
  {
    return(data.get(var));
  }

  /**
   * Store an integer value in the data sample.
   *
   * @param var The variable name.
   * @param value The variable value.
   */
  
  public void putValue(String var, int value)
  {
    data.put(var, new Integer(value));
  }

  /**
   * Store a double value in the data sample.
   *
   * @param var The variable name.
   * @param value The variable value.
   */

  public void putValue(String var, double value)
  {
    data.put(var, new Double(value));
  }

  /**
   * Remove a value from the data sample.
   *
   * @param var The variable name.
   */
  
  public void removeValue(String var)
  {
    data.remove(var);
  }

  /**
   * Remove all values from the data sample.
   */
  
  public void clear()
  {
    data.clear();
  }

}

/* end of source file */
