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

/** A default implementation of <code>ChartModel</code> that also implements
 * the Swing <code>TableModel</code> interface. The labels of the chart values
 * in this chart model provide the names of the columns for the table model.
 * Therefore, each data sample in the chart model represents one row in the
 * table model, and each chart value in the chart definition represents one
 * column in the table model. This model can thus be used to simultaneously
 * drive a <code>ChartView</code> and a <code>JTable</code>, effectively
 * providing both a graphical and a spreadsheet view of the same underlying
 * data.
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Mark Lindner
 */

public class DefaultChartModel extends AbstractTableModel implements ChartModel
{
  private EventListenerList listeners = new EventListenerList(); 
  private ArrayList<DataSample> data;
  private Chart chart;

  /** Construct a new <code>DefaultChartModel</code> for the specified chart
   * definition.
   *
   * @param chart The chart definition.
   */
  
  public DefaultChartModel(Chart chart)
  {
    this.chart = chart;

    data = new ArrayList<DataSample>();
  }

  /** Get the number of rows in the table model.
   *
   * @return The row count.
   */
  
  public int getRowCount()
  {
    return(data.size());
  }

  /** Get the number of columns in the table model.
   *
   * @return The column count.
   */
  
  public int getColumnCount()
  {
    return(chart.getValueCount());
  }
  
  /** Get the name of the specified column in the table model.
   *
   * @param col The column.
   * @return The name of the specified column.
   */
  
  public String getColumnName(int col)
  {
    return(chart.getValueAt(col).getLabel());
  }

  /** Determine if the given cell is editable.
   *
   * @param row The row.
   * @param col The Column.
   * @return <code>true</code> if the cell is editable, <code>false</code>
   * otherwise. This implementation always returns <code>false</code>.
   */
  
  public boolean isCellEditable(int row, int col)
  {
    return(false);
  }

  /** Add a <code>ChartModelListener</code> to this model's list of listeners.
   *
   * @param listener The listener to add.
   */
  
  public void addChartModelListener(ChartModelListener listener) 
  { 
    listeners.add(ChartModelListener.class, listener); 
  } 

  /** Remove a <code>ChartModelListener</code> from this model's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */

  public void removeChartModelListener(ChartModelListener listener) 
  {
    listeners.remove(ChartModelListener.class, listener);
  }

  /** Fire a <i>chart data changed</i> event.
   */
  
  protected void fireChartDataChanged() 
  {
    ChartModelEvent evt = null; 
    
    Object[] list = listeners.getListenerList(); 
    
    for(int i = list.length - 2; i >= 0; i -= 2) 
    { 
      if(list[i] == ChartModelListener.class) 
      { 
        // Lazily create the event: 
        if(evt == null) 
          evt = new ChartModelEvent(this); 
        ((ChartModelListener)list[i + 1]).chartDataChanged(evt); 
      }
    }
  }

  /** Add a data sample to this model.
   *
   * @param ds The data sample to add.
   */
  
  public void addDataSample(DataSample ds)
  {
    data.add(ds);
    fireChartDataChanged();
    fireTableDataChanged();
  }

  /** Get the number of data samples in this model.
   *
   * @return The number of data samples.
   */
  
  public int getDataSampleCount()
  {
    return(data.size());
  }

  /** Get an iterator to the data samples in this model.
   *
   * @since Kiwi 2.1
   */

  public Iterator<DataSample> iterator()
  {
    return(data.iterator());
  }

  /** Get the data sample at the specified index.
   *
   * @param index The index of the desired data sample.
   * @return The <code>DataSample</code> at the specified index, or
   * <code>null</code> if there is no data sample at that index.
   */
  
  public DataSample getDataSample(int index)
  {
    return(data.get(index));
  }

  /** Remove the data sample at the specified index from this model.
   *
   * @param index The index of the data sample to remove.
   */
  
  public void removeDataSample(int index)
  {
    data.remove(index);
  }

  /** Remove all data samples from this model.
   */
  
  public void clear()
  {
    data.clear();
    fireChartDataChanged();
    fireTableDataChanged();
  }

  /** Get the value at the specified row and column in the table model.
   *
   * @param row The row.
   * @param col The column.
   * @return The value at the specified row and column.
   */
  
  public Object getValueAt(int row, int col)
  {
    DataSample ds = data.get(row);
    ChartValue val = chart.getValueAt(col);
    return(ds.getValue(val.getName()));
  }

}

/* end of source file */
