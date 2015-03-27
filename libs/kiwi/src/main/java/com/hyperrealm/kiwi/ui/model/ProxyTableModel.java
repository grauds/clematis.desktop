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

import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/** A mapping object for <code>TableModel</code>s.  In a chain of data
 * manipulators some behavior is common.
 * <code>ProxyTableModel</code> provides most of this behavior and
 * can be subclassed by filters that only need to override a handful
 * of specific methods.  <code>ProxyTableModel</code> implements
 * <code>javax.swing.table.TableModel</code> by routing all requests
 * to its model, and
 * <code>javax.swing.event.TableModelListener</code> by routing all
 * events to its listeners. Inserting a <code>ProxyTableModel</code>
 * which has not been subclassed into a chain of table filters should
 * have no effect.
 *
 * @see javax.swing.table.TableModel
 * @see com.hyperrealm.kiwi.ui.model.TableSorter
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ProxyTableModel extends AbstractTableModel
  implements TableModelListener
{
  /** The <code>TableModel</code> that this model proxies for. */
  protected TableModel model;

  /** Construct a new <code>ProxyTableModel</code>. */
  
  public ProxyTableModel()
  {
  }
  
  /** Get the <code>TableModel</code> used by this map. */

  public TableModel getModel()
  {
    return model;
  }

  /** Set the <code>TableModel</code> to use with this map.
   *
   * @param model The <code>TableModel</code> to use.
   */

  public void setModel(TableModel model)
  {
    this.model = model;
    model.addTableModelListener(this);
  }

  /* By default, Implement TableModel by forwarding all messages to the
     inner model. */

  public Object getValueAt(int row, int col)
  {
    return(model.getValueAt(row, col));
  }

  public void setValueAt(Object value, int row, int col)
  {
    model.setValueAt(value, row, col);
  }

  public int getRowCount()
  {
    return((model == null) ? 0 : model.getRowCount());
  }

  public int getColumnCount()
  {
    return((model == null) ? 0 : model.getColumnCount());
  }

  public String getColumnName(int col)
  {
    return model.getColumnName(col);
  }

  public Class getColumnClass(int col)
  {
    return model.getColumnClass(col);
  }

  public boolean isCellEditable(int row, int col)
  {
    return model.isCellEditable(row, col);
  }

  /* Implementation of the TableModelListener interface. By default
   * forward all events to all the listeners.
   */

  public void tableChanged(TableModelEvent e)
  {
    fireTableChanged(e);
  }

}

/* end of source file */
