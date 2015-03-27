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

import com.hyperrealm.kiwi.util.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/** A simple, mutable implementation of <code>ListModel</code>.
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class MutableListModel<T> implements ListModel, Iterable<T>
{
  protected ArrayList<T> data = new ArrayList<T>();
  private ArrayList<ListDataListener> listeners
    = new ArrayList<ListDataListener>();

  /** Construct a new, empty <code>MutableListModel</code>.
   */
  
  public MutableListModel()
  {
  }

  /** Construct a new <code>MutableListModel</code> with the given data.
   *
   * @param data A collection of elements to insert into the model.
   */

  public MutableListModel(Collection<T> data)
  {
    this();

    this.data.addAll(data);
  }


  /** Add a list data listener to this model's list of listeners.
   *
   * @param listener The listener to add.
   */
  
  public void addListDataListener(ListDataListener listener)
  {
    synchronized(listeners)
    {
      listeners.add(listener);
    }
  }

  /** Remove a list data listener from this model's list of listeners.
   *
   * @param listener The listener to remove.
   */
  
  public void removeListDataListener(ListDataListener listener)
  {
    synchronized(listeners)
    {
      listeners.remove(listener);
    }
  }

  /** Get the size of the model.
   *
   * @return The number of items in the model.
   */
  
  public int getSize()
  {
    return(data.size());
  }

  /** Get the object at the given index in the model.
   *
   * @param index The index of the object to retrieve.
   * @return The object at the given index, or <code>null</code> if the index
   * is out of range.
   */
  
  public T getElementAt(int index)
  {
    return(data.get(index));
  }

  /** Add an object to the model. The object is inserted at the proper index
   * to maintain a sorted model.
   *
   * @param elem The new object to add.
   */
  
  public void addElement(T elem)
  {
    int sz = data.size();
    data.add(elem);
    fireIntervalAdded(sz, sz);
  }

  /** Remove all elements from the model. */
  
  public void clear()
  {
    if(! data.isEmpty())
    {
      int sz = data.size();
      data.clear();
      fireIntervalRemoved(0, sz - 1);
    }
  }

  /** Determine if the model contains an object.
   *
   * @param elem The object to search for.
   * @return <code>true</code> if the object is in the model,
   * <code>false</code> otherwise.
   */

  public boolean contains(T elem)
  {
    return(data.contains(elem));
  }

  /** Populate the model with an array of elements, replacing any previous
   * content in the model.
   *
   * @param elements The array of elements.
   */

  public void setElements(T elements[])
  {
    clear();
    
    if(elements != null)
      for(int i = 0; i < elements.length; i++)
        addElement(elements[i]);
  }

  /** Fetch the current elements from the model into an array.
   *
   * @param elements The array in which to store the elements.
   */
  
  public void getElements(T elements[])
  {
    data.toArray(elements);
  }

  /** Get an iterator to the elements in the model.
   */

  public Iterator<T> iterator()
  {
    return(data.iterator());
  }

  /** Get the index of an element in the model. */
  
  public int indexOf(T elem)
  {
    return(data.indexOf(elem));
  }

  /** Determine if the model is empty.
   *
   * @return <code>true</code> if there are no elements in the model,
   * <code>false</code> otherwise.
   */
  
  public boolean isEmpty()
  {
    return(data.isEmpty());
  }

  /** Remove an element at the given index in the model.
   *
   * @param index The index of the element to remove.
   */
  
  public void removeElementAt(int index)
  {
    if((index >= 0) && (index < data.size()))
    {
      data.remove(index);
      fireIntervalRemoved(index, index);
    }
  }

  /** Remove a range of elements form the model.
   *
   * @param fromIndex The starting index of the range.
   * @param toIndex The ending index of the range.
   */
  
  public void removeRange(int fromIndex, int toIndex)
  {
    if((fromIndex >= 0) && (toIndex < data.size()) && (fromIndex <= toIndex))
    {
      int ct = toIndex - fromIndex;

      for(int i = 0; i < ct; i++)
        data.remove(fromIndex);

      fireIntervalRemoved(fromIndex, toIndex);
    }
  }
  
  /** Fire an <i>interval added</i> event.
   *
   * @param start The starting index of the interval.
   * @param end The ending index of the interval.
   */

  protected void fireIntervalAdded(int start, int end)
  {
    ListDataEvent evt = null;

    synchronized(listeners)
    {
      Iterator<ListDataListener> iter = listeners.iterator();
      while(iter.hasNext())
      {
        ListDataListener l = iter.next();
        if(evt == null)
          evt = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start,
                                  end);
        
        l.intervalAdded(evt);
      }
    }
  }

  /** Fire an <i>interval removed</i> event.
   *
   * @param start The starting index of the interval.
   * @param end The ending index of the interval.
   */
  
  protected void fireIntervalRemoved(int start, int end)
  {
    ListDataEvent evt = null;

    synchronized(listeners)
    {
      Iterator<ListDataListener> iter = listeners.iterator();
      while(iter.hasNext())
      {
        ListDataListener l = iter.next();
        if(evt == null)
          evt = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start,
                                  end);
        
        l.intervalRemoved(evt);
      }
    }
  }

  /** Fire a <i>contents changed</i> event.
   */
  
  protected void fireContentsChanged()
  {
    fireContentsChanged(0, data.size() - 1);
  }
    
  /** Fire a <i>contents changed</i> event.
   *
   * @param start The beginning row of the interval.
   * @param end The ending row of the interval.
   *
   * @since Kiwi 2.2
   */

  protected void fireContentsChanged(int start, int end)
  {
    ListDataEvent evt = null;

    synchronized(listeners)
    {
      Iterator<ListDataListener> iter = listeners.iterator();
      while(iter.hasNext())
      {
        ListDataListener l = iter.next();
        if(evt == null)
          evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start,
                                  end);
        
        l.contentsChanged(evt);
      }
    }
  }  
  
}

/* end of source file */
