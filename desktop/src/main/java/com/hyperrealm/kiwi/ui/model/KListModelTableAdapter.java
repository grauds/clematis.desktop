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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** A model adapter that allows a <code>KListModel</code> to be used with a
 * Swing <code>JTable</code> component. This adapter wraps a
 * <code>KListModel</code> implementation and exposes a <code>TableModel</code>
 * interface, and translates the corresponding model events.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KListModelTableAdapter extends KListModelAdapter
  implements TableModel
{
  private static final String defaultColumnNames[] = { "Item" };
  private String columnNames[] = defaultColumnNames;
  private static final Class defaultColumnTypes[] = { String.class };
  private Class columnTypes[] = defaultColumnTypes;
  private boolean columnsAvailable = false;
  private ArrayList<TableModelListener> listeners
    = new ArrayList<TableModelListener>();
  private ExceptionHandler errorHandler = null;

  /** Construct a new <code>KListModelTableAdapter</code>.
   */

  public KListModelTableAdapter()
  {
  }

  /** Construct a new <code>KListModelTableAdapter</code> for the given
   * list model. The <code>TableModel</code> will have a single column,
   * marked "Item".
   *
   * @param model The <code>KListModel</code>.
   */
  
  public KListModelTableAdapter(KListModel model)
  {
    setListModel(model);    
  }

  /*
   */
  
  public void setListModel(KListModel model)
  {
    super.setListModel(model);

    if(model != null)
    {
      int cols = model.getFieldCount();
      columnNames = new String[cols];
      columnTypes = new Class[cols];

      for(int i = 0; i < cols; i++)
      {
        columnNames[i] = model.getFieldLabel(i);
        columnTypes[i] = model.getFieldType(i);
      }

      columnsAvailable = true;
    }
    else
    {
      columnNames = defaultColumnNames;
      columnTypes = defaultColumnTypes;
      columnsAvailable = false;
    }
  }
  
  /* Fire table events.
   */

  private void fireTableEvent(KListModelEvent evt, int type)
  {
    TableModelEvent tevt = null;

    synchronized(listeners)
    {
      Iterator<TableModelListener> iter = listeners.iterator();
      while(iter.hasNext())
      {
        TableModelListener l = iter.next();
        if(tevt == null)
          tevt = new TableModelEvent(this, evt.getStartIndex(),
                                     evt.getEndIndex(),
                                     TableModelEvent.ALL_COLUMNS, type);
        
        l.tableChanged(tevt);
      }
    }
  }

  /* implementation of KListModelListener */

  /*
   */
  
  public void itemsAdded(KListModelEvent evt)
  {
    fireTableEvent(evt, TableModelEvent.INSERT);
  }

  /*
   */
  
  public void itemsChanged(KListModelEvent evt)
  {
    fireTableEvent(evt, TableModelEvent.UPDATE);
  }

  /*
   */

  public void itemsRemoved(KListModelEvent evt)
  {
    fireTableEvent(evt, TableModelEvent.DELETE);
  }

  /*
   */

  public void dataChanged(KListModelEvent evt)
  {
    fireModelChangedEvent();
  }

  /* implementation of ListModelAdapter */

  protected void fireModelChangedEvent()
  {
    TableModelEvent evt = null;

    synchronized(listeners)
    {
      Iterator<TableModelListener> iter = listeners.iterator();
      while(iter.hasNext())
      {
        if(evt == null)
          evt = new TableModelEvent(this);
        TableModelListener l = iter.next();
        l.tableChanged(evt);
      }
    }
  }
  
  /* implementation of TableModel */

  /*
   */

  public Class getColumnClass(int col)
  {
    return(columnTypes[col]);
  }

  /*
   */
  
  public String getColumnName(int col)
  {
    return(columnNames[col]);
  }

  /*
   */
  
  public int getColumnCount()
  {    
    return(columnNames.length);
  }

  /*
   */
  
  public int getRowCount()
  {
    if(model == null)
      return(0);
    
    return(model.getItemCount());
  }

  /*
   */
  
  public Object getValueAt(int row, int column)
  {
    if(model == null)
      return(null);
    
    Object item = model.getItemAt(row);

    if(columnsAvailable)
      return(model.getField(item, column));
    else
      return(model.getLabel(item));
  }

  /** 
   */
  
  public void setValueAt(Object value, int row, int column)
  {
    Object item = model.getItemAt(row);
    try
    {
      model.setField(item, column, value);
    }
    catch(MutatorException ex)
    {
      _deliverException(ex);
    }
  }

  /*
   */
  
  public boolean isCellEditable(int row, int column)
  {
    Object item = model.getItemAt(row);
    return(model.isFieldMutable(item, column));
  }

  /*
   */
  
  public void addTableModelListener(TableModelListener listener)
  {
    synchronized(listeners)
    {
      listeners.add(listener);
    }
  }

  /*
   */
  
  public void removeTableModelListener(TableModelListener listener)
  {
    synchronized(listeners)
    {
      listeners.remove(listener);
    }
  }

  /** Set the <code>ExceptionHandler</code> for this model. If an exception
   * occurs while a domain object is being updated as a result of a change
   * to one of the values in this model, the exception is caught and delivered
   * to the handler (if one is registered).
   *
   * @param handler The new (possibly <code>null</code>) exception handler.
   */
  
  public void setExceptionHandler(ExceptionHandler handler)
  {
    this.errorHandler = handler;
  }
  
  /* Deliver an exception to a handler, if one is registered. */

  private void _deliverException(Exception ex)
  {
    if(errorHandler != null)
      errorHandler.exceptionRaised(ex);
  }
  
}

/* end of source file */
