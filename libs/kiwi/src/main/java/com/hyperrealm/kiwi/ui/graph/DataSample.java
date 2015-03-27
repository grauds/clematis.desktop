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

/** A class that represents a data sample. A data sample consists of one or
 * more variables and their values. These variables correspond to those
 * defined in a chart definition (<code>Chart</code> object).
 *
 * @see com.hyperrealm.kiwi.ui.graph.ChartValue
 * @see com.hyperrealm.kiwi.ui.graph.Chart
 *
 * @author Mark Lindner
 */

public interface DataSample
{
  /** Get the value associated with the specified variable.
   *
   * @param var The variable name.
   * @return The value for the variable, or <code>null</code> if the variable
   * was not found or has no value.
   */
  
  public Object getValue(String var);
}

/* end of source file */
