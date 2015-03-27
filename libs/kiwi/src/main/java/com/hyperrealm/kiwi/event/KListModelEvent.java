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

package com.hyperrealm.kiwi.event;

import java.util.EventObject;

/** Event for notifying listeners that a list data structure has changed.
 *
 * @see com.hyperrealm.kiwi.event.KListModelListener
 * @author Mark Lindner
 *
 * @since Kiwi 2.0
 */

public class KListModelEvent extends EventObject
{
  private int startIndex = 0;
  private int endIndex = 0;
  private int field = -1;

  /** Construct a new <code>KListModelEvent</code>.
   *
   * @param source The source of the event.
   * @param startIndex The offset of the first item in a range of items that is
   * affected by this event.
   * @param endIndex The offset of the last item in a range of items that is
   * affected by this event.
   */

  public KListModelEvent(Object source, int startIndex, int endIndex)
  {
    this(source, startIndex, endIndex, -1);
  }
  
  /** Construct a new <code>KListModelEvent</code>.
   *
   * @param source The source of the event.
   * @param startIndex The offset of the first item in a range of items that is
   * affected by this event.
   * @param endIndex The offset of the last item in a range of items that is
   * affected by this event.
   * @param field The field number affected by this event, or -1 if potentially
   * all fields in the affected object(s) have changed.
   *
   * @since Kiwi 2.4.1
   */

  public KListModelEvent(Object source, int startIndex, int endIndex,
                         int field)
  {
    super(source);

    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.field = field;
  }

  /** Construct a new <code>KListModelEvent</code>. The index is set to
   * 0.
   *
   * @param source The source of the event.
   */

  public KListModelEvent(Object source)
  {
    this(source, -1, -1, -1);
  }
  
  /** Get the offset (index) for this event. For insertions, the
   * position at which an item will be inserted; for deletions, the
   * position of an item being removed; for updates, the position of an item
   * that has changed.
   */

  public int getIndex()
  {
    return(startIndex);
  }

  /** Get the start index for this event. For insertions, the offset
   * of the first item in a range of items that will be inserted; for
   * deletions, the position of the first item in a range of items
   * being removed; for updates, the position of the first item in a range of
   * items that have changed.
   */

  public int getStartIndex()
  {
    return(startIndex);
  }

  /** Get the end index for this event. For insertions, the offset of
   * the last item in a range of items that will be inserted; for
   * deletions, the position of the last item in a range of items
   * being removed; for updates, the position of the last item in a
   * range of items that have changed.
   */
  
  public int getEndIndex()
  {
    return(endIndex);
  }

  /** Get the field number for this event.
   *
   * @since Kiwi 2.4.1
   */
  
  public int getField()
  {
    return(field);
  }
  
}

/* end of source file */
