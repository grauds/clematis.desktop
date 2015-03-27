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

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import com.hyperrealm.kiwi.util.*;

/** A <code>KListModel</code> that utilizes a
 * {@link com.hyperrealm.kiwi.ui.model.DomainObjectFieldAdapter DomainObjectFieldAdapter}
 * to map object fields.
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * To properly associate a <code>JTable</code> with
 * a <code>DomainObjectListModel</code>, use code similar to the following:
 * <pre>
 * JTable table = new JTable();
 * DomainObjectListModel model = ...;
 * table.setModel(model);
 * model.prepareJTable(table);
 * </pre>
 * 
 * @author Mark Lindner
 *
 * @see com.hyperrealm.kiwi.ui.model.DomainObjectFieldAdapter
 * @since Kiwi 2.3
 */

public class DomainObjectListModel<T> extends DefaultKListModel<T>
{
  private DomainObjectFieldAdapter<T> fieldAdapter;

  /** Construct a new <code>DomainObjectTableModel</code> for the given
   * field adapter.
   *
   * @param adapter The field adapter to use.
   */
  
  public DomainObjectListModel(DomainObjectFieldAdapter<T> adapter)
  {
    fieldAdapter = adapter;
  }

  /** Get the field adapter for this model.
   *
   * @since Kiwi 2.3
   */

  public DomainObjectFieldAdapter<T> getFieldAdapter()
  {
    return(fieldAdapter);
  }

  /*
   */
  
  public int getFieldCount()
  {
    return(fieldAdapter.getFieldCount());
  }

  /*
   */
  
  public String getFieldLabel(int field)
  {
    return(fieldAdapter.getFieldName(field));
  }

  /*
   */
  
  public Class getFieldType(int field)
  {
    return(fieldAdapter.getFieldClass(field));
  }

  /*
   */

  public Object getField(T item, int field)
  {
    return(fieldAdapter.getField(item, field));
  }

  /*
   */
  
  public void setField(T item, int field, Object value) throws MutatorException
  {
    int row = indexOf(item);
    if(row >= 0)
    {
      fieldAdapter.setField(item, field, value);
      updateItemAt(row, field);
    }
  }

  /*
   */

  public boolean isFieldMutable(T item, int field)
  {
    return(fieldAdapter.isFieldEditable(item, field));
  }

  /*
   */

  public String getLabel(T item)
  {
    return(fieldAdapter.getLabel(item));
  }

  /*
   */

  public Icon getIcon(T item)
  {
    return(fieldAdapter.getIcon(item));
  }
  
  /** Prepare a <code>JTable</code> for use with this model. This method
   * creates the necessary columns in the <code>JTable</code> and assigns
   * the appropriate cell renderers and editors (as provided by the field
   * adapter) for each column.
   *
   * @param table The <code>JTable</code> with which this model will be used.
   */
  
  public void prepareJTable(JTable table)
  {
    TableColumnModel cmodel = table.getColumnModel();
    int cols = cmodel.getColumnCount();

    for(int i = 0; i < cols; i++)
    {
      TableColumn tc = cmodel.getColumn(i);
      tc.setCellRenderer(fieldAdapter.getCellRenderer(i));
      tc.setCellEditor(fieldAdapter.getCellEditor(i));

      int w = fieldAdapter.getFieldPreferredWidth(i);
      if(w > 0)
        tc.setPreferredWidth(w);

      w = fieldAdapter.getFieldMinWidth(i);
      if(w > 0)
        tc.setMinWidth(w);

      w = fieldAdapter.getFieldMaxWidth(i);
      if(w > 0)
        tc.setMaxWidth(w);
    }
  }


}

/* end of source file */
