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

/*
 * @(#)TableSorter.java 1.5 97/12/17
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package com.hyperrealm.kiwi.ui.model;

import java.awt.Point;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

import com.hyperrealm.kiwi.util.*;

/**
 * A sorter for <code>TableModel</code>s. The sorter has a model (conforming
 * to <code>TableModel</code>) and itself implements <code>TableModel</code>.
 * <code>TableSorter</code> does not store or copy the data in the
 * <code>TableModel</code>; instead it maintains an array of integers which it
 * keeps the same size as the number of rows in its model. When the model
 * changes it notifies the sorter that something has changed eg. "rowsAdded"
 * so that its internal array of integers can be reallocated. As requests are
 * made of the sorter (like <code>getValueAt(row, col)</code>) it redirects
 * them to its model via the mapping array. That way the
 * <code>TableSorter</code> appears to hold another copy of the table with
 * the rows in a different order. The sorting algorthm used is stable which
 * means that it does not move around rows when its comparison function
 * returns 0 to denote that they are equivalent.
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Philip Milne
 * @author Mark Lindner
 */

public class TableSorter extends ProxyTableModel
{
  private int indexes[];
  private ArrayList<Integer> sortingColumns = new ArrayList<Integer>();
  private boolean ascending = true;
  private int compares;
  private JTable tableView;
  private int sortedColumn = -1;
  private int sortedVisibleColumn = -1;
  private boolean sortedAscending = true;

  /** Construct a new <code>TableSorter</code>. */

  public TableSorter()
  {
    indexes = new int[0]; // For consistency.
  }

  /** Construct a new <code>TableSorter</code> with a specified data model..
   *
   * @param model The <code>TableModel</code> to use.
   */

  public TableSorter(TableModel model)
  {
    setModel(model);
  }

  /** Return the index of the given row in the <i>unsorted</i> model that this
   * model wraps. This method is useful for determining which row in the
   * actual model is mapped to the currently selected row in the sorted
   * model.
   *
   * @see #getReverseRowTranslation
   */

  public int getRowTranslation(int row)
  {
    return(indexes[row]);
  }

  /** Return the visible index of the given row in the <i>unsorted</i> model.
   * This method performs the reverse of <code>getRowTranslation()</code>
   *
   * @see #getRowTranslation
   */
  
  public int getReverseRowTranslation(int row)
  {
    for(int i = 0; i < indexes.length; i++)
      if(indexes[i] == row)
        return(i);

    return(-1);
  }

  /** Set the <code>TableModel</code> for this table sorter.
   *
   * @param model The <code>TableModel</code> to use.
   */

  public void setModel(TableModel model)
  {
    super.setModel(model);
    reallocateIndexes();
  }

  /** Get the value at the given row and column of the unsorted table.
   *
   * @param row The row.
   * @param col The column.
   *
   * @return The object at the given position in the <i>unsorted</i> model.
   */

  public Object getValueAt(int row, int col)
  {
    // The mapping only affects the contents of the data rows.
    // Pass all requests to these rows through the mapping array: "indexes".

    checkModel();
    return model.getValueAt(indexes[row], col);
  }

  /** Set the value at the given row and column of the unsorted table.
   *
   * @param row The row.
   * @param col The column.
   * @param value The new value.
   *
   */

  public void setValueAt(Object value, int row, int col)
  {
    checkModel();
    model.setValueAt(value, indexes[row], col);
  }

  /** Sort a column in the table in ascending order.
   *
   * @param column The index of the column to sort.
   */

  public void sortByColumn(int column)
  {
    boolean asc = true;
    
    if(column == sortedColumn)
      asc = ! sortedAscending;
    
    sortByColumn(column, asc);
  }

  /** Sort a column in the table.
   *
   * @param column The index of the column to sort.
   * @param ascending If <code>true</code>, sorts in ascending order;
   * otherwise, sorts in descending order.
   */

  public void sortByColumn(int column, boolean ascending)
  {
    boolean bigTable = (getRowCount() > 10000);

    if(bigTable)
      KiwiUtils.busyOn(tableView);
    
    this.sortedColumn = column;
    this.sortedAscending = ascending;
    this.ascending = ascending;
    sortingColumns.clear();
    sortingColumns.add(new Integer(column));
    sort(this);

    super.tableChanged(new TableModelEvent(this));

    if(bigTable)
      KiwiUtils.busyOff(tableView);
  }

  /** Get the index of the last column that the table was sorted on.
   *
   * @return The column index, or -1 if there was no previous sort.
   *
   * @since Kiwi 1.4
   */

  public int getSortedColumn()
  {
    return(sortedColumn);
  }

  /** Get the index of the last <i>visible</i> column that the table was
   * sorted on.
   *
   * @return The column index, or -1 if there was no previous sort.
   *
   * @since Kiwi 2.1
   */

  public int getSortedVisibleColumn()
  {
    return(sortedVisibleColumn);
  }

  /** Determine if the last column sort was ascending or descending.
   *
   * @return <code>true</code> if the sort was ascending, and
   * <code>false</code> otherwise. This value is only meaningful if
   * <code>getSortedColumn()</code> returns a non-negative value.
   *
   * @since Kiwi 1.4
   */

  public boolean isSortedAscending()
  {
    return(sortedAscending);
  }
  
  /** Handle <i>table changed</i> events. */

  public void tableChanged(TableModelEvent e)
  {
    reallocateIndexes();
    super.tableChanged(e);
  }

  /** Add a mouse listener to the <code>JTable</code> to trigger a table sort
   * when a column heading is clicked. A shift click causes the column to
   * be sorted in descending order, whereas a simple click causes the column
   * to be sorted in ascending order.
   *
   * @param table The <code>JTable</code> to listen for events on.
   */

  public void registerTableHeaderListener(JTable table)
  {
    final TableSorter sorter = this;
    tableView = table;

    tableView.setColumnSelectionAllowed(false);
    MouseAdapter listMouseListener = new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          // If the table is disabled, we will ignore the mouse event,
          // effectively preventing sorting.
        
          if(!tableView.isEnabled() || tableView.isEditing())
            return;

          if(!((e.getClickCount() == 1) && (e.getButton() == e.BUTTON1)))
            return;
        
          // Save a list of highlighted rows. I don't call getSelectedRows()
          // directly since that would force me to have *two* working arrays.

          int hrows[] = new int[tableView.getSelectedRowCount()];
          int nr = tableView.getRowCount();
          for(int c = 0, i = 0; i < nr ; i++)
            if(tableView.isRowSelected(i))
              hrows[c++] = getRowTranslation(i);

          // figure out which column was selected, & sort it

          sortedVisibleColumn = tableView.columnAtPoint(new Point(e.getX(),
                                                                  e.getY()));
        
          TableColumnModel columnModel = tableView.getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX());
          int column = tableView.convertColumnIndexToModel(viewColumn);

          if(column != -1)
          {
            /* int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
               boolean ascending = (shiftPressed == 0);
               sorter.sortByColumn(column, ascending); */

            sorter.sortByColumn(column);
          }

          // now rehighlight the rows in their new positions

          tableView.clearSelection();
          for(int i = 0; i < hrows.length; i++)
          {
            int r = getReverseRowTranslation(hrows[i]);
            tableView.addRowSelectionInterval(r, r);
          }
        
        }
      };

    JTableHeader th = tableView.getTableHeader();
    th.addMouseListener(listMouseListener);
  }

  /* internal code follows */

  private int compareRowsByColumn(int row1, int row2, int column)
  {
    Class type = model.getColumnClass(column);
    Collator collator = LocaleManager.getDefault().getCollator();

    TableModel data = model;

    // Check for nulls

    Object o1 = data.getValueAt(row1, column);
    Object o2 = data.getValueAt(row2, column);

    // If both values are null return 0

    if((o1 == null) && (o2 == null))
    {
      return(0);
    }
    else if(o1 == null)
    {
      // Define null less than everything.
      return(-1);
    }
    else if(o2 == null)
    {
      return(1);
    }

    /* We copy all returned values from the getValue call in case an optimised
     * model is reusing one object to return many values. The Number
     * subclasses in the JDK are immutable and so will not be used in this way
     * but other subclasses of Number might want to do this to save space and
     * avoid unnecessary heap allocation.
     */

    Class stype = type.getSuperclass();
    
    if(stype == Number.class)
    {
      Number n1 = (Number)o1;
      double d1 = n1.doubleValue();
      Number n2 = (Number)o2;
      double d2 = n2.doubleValue();

      return((d1 < d2) ? -1 : ((d1 > d2) ? 1 : 0));
    }
    else if((type == String.class) || (type == Date.class)
            || (stype == ValueHolder.class))
    {
      Comparable c1 = (Comparable)o1;
      Comparable c2 = (Comparable)o2;

      return(((Comparable)c1).compareTo(c2));
    }
    else if(type == Boolean.class)
    {
      Boolean bb1 = (Boolean)o1;
      Boolean bb2 = (Boolean)o2;

      boolean b1 = bb1.booleanValue();
      boolean b2 = bb2.booleanValue();
      
      return(b1 == b2 ? 0 : (b1 ? 1 : -1));
    }
    else
    {
      if(o1 instanceof Comparable)
        return(((Comparable)o1).compareTo(o2));
      else
      {
        String s1 = o1.toString();
        String s2 = o2.toString();

        return(s1.compareTo(s2));
      }
    }
  }

  private int compare(int row1, int row2)
  {
    compares++;
    for(int level = 0; level < sortingColumns.size(); level++)
    {
      Integer column = sortingColumns.get(level);
      int result = compareRowsByColumn(row1, row2, column.intValue());
      if(result != 0)
        return ascending ? result : -result;
    }
    return 0;
  }

  /*
   */
  
  private void reallocateIndexes()
  {
    int rowCount = model.getRowCount();

    // Set up a new array of indexes with the right number of elements
    // for the new data model.

    indexes = new int[rowCount];

    // Initialise with the identity mapping.

    for(int row = 0; row < rowCount; row++)
      indexes[row] = row;
  }

  /*
   */

  private void checkModel()
  {
    if(indexes.length != model.getRowCount())
    {
      System.err.println("Sorter not informed of a change in model.");
    }
  }

  /*
   */

  private void sort(Object sender)
  {
    checkModel();

    compares = 0;

    shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
  }

  /*
    public void n2sort()
    {
    for(int i = 0; i < getRowCount(); i++) {
    for(int j = i+1; j < getRowCount(); j++) {
    if (compare(indexes[i], indexes[j]) == -1) {
    swap(i, j);
    }
    }
    }
    }
  */

  // This is a home-grown implementation which we have not had time to research
  // - it may perform poorly in some circumstances. It requires twice the space
  // of an in-place algorithm and makes NlogN assigments shuttling the values
  // between the two arrays. The number of compares appears to vary between N-1
  // and NlogN depending on the initial order but the main reason for using it
  // here is that, unlike qsort, it is stable.

  private void shuttlesort(int from[], int to[], int low, int high)
  {
    if (high - low < 2)
    {
      return;
    }

    int middle = (low + high)/2;
    shuttlesort(to, from, low, middle);
    shuttlesort(to, from, middle, high);

    int p = low;
    int q = middle;

    /* This is an optional short-cut; at each recursive call, check to see if
     * the elements in this subset are already ordered.  If so, no further
     * comparisons are needed; the sub-array can just be copied.  The array
     * must be copied rather than assigned otherwise sister calls in the
     * recursion might get out of sinc.  When the number of elements is three
     * they are partitioned so that the first set, [low, mid), has one element
     * and the second, [mid, high), has two. We skip the optimisation when the
     * number of elements is three or less as the first compare in the normal
     * merge will produce the same sequence of steps. This optimisation seems
     * to be worthwhile for partially ordered lists but some analysis is
     * needed to find out how the performance drops to Nlog(N) as the initial
     * order diminishes - it may drop very quickly.
     */

    if(high - low >= 4 && compare(from[middle-1], from[middle]) <= 0)
    {
      for(int i = low; i < high; i++)
      {
        to[i] = from[i];
      }
      return;
    }

    // A normal merge.

    for(int i = low; i < high; i++)
    {
      if(q >= high || (p < middle && compare(from[p], from[q]) <= 0))
      {
        to[i] = from[p++];
      }
      else
      {
        to[i] = from[q++];
      }
    }
  }

  /*
   */
  
  private void swap(int i, int j)
  {
    int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }

}

/* end of source file */
