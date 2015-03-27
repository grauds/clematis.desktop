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

package com.hyperrealm.kiwi.ui.model;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.ui.graph.*;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

/** This interface defines the behavior for a data model for charts. A
 * <code>ChartModel</code> consists of a collection of
 * <code>DataSample</code>s. A <code>ChartView</code> plots these data
 * samples graphically.
 *
 * @see com.hyperrealm.kiwi.ui.graph.ChartView
 *
 * @author Mark Lindner
 */

public interface ChartModel extends Iterable<DataSample>
{

  /** Add a <code>ChartModelListener</code> to this model's list of listeners.
   *
   * @param listener The listener to add.
   */
  
  public void addChartModelListener(ChartModelListener listener);

  /** Remove a <code>ChartModelListener</code> from this model's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */
  
  public void removeChartModelListener(ChartModelListener listener);

  /** Get the number of data samples in this model.
   *
   * @return The number of data samples.
   */
  
  public int getDataSampleCount();

  /** Get the data sample at the specified index.
   *
   * @param index The index of the desired data sample.
   * @return The <code>DataSample</code> at the specified index, or
   * <code>null</code> if there is no data sample at that index.
   */
  
  public DataSample getDataSample(int index);

  /** Get all of the data samples in this model.
   *
   * @return An <code>Enumeration</code> of the <code>DataSample</code>
   * objects in this model.
   * @since Kiwi 2.1
   */
  
  public Iterator<DataSample> iterator();

  /** Add a data sample to this model.
   *
   * @param ds The data sample to add.
   */
  
  public void addDataSample(DataSample ds);

  /** Remove the data sample at the specified index from this model.
   *
   * @param index The index of the data sample to remove.
   */
  
  public void removeDataSample(int index);

  /** Remove all data samples from this model.
   */
  
  public void clear();
}

/* end of source file */
