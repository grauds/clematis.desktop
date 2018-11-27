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

import java.util.*;

/** This class represents a notification event. Such an event can be used to
 * notify listeners of a condition through the delivery of a token. In this
 * case, the token is simply an integer.
 *
 * @author Mark Lindner
 */
 
public class NotificationEvent extends EventObject
{
  /** The token ID for this event. */
  protected int id;

  /** Construct a new <code>Notification Event</code>.
   *
   * @param source The source of this event.
   * @param id The token ID for this event.
   */
  
  public NotificationEvent(Object source, int id)
  {
    super(source);

    this.id = id;
  }

  /** Get the token ID associated with this event.
   *
   * @return The token ID.
   */
  
  public int getID()
  {
    return(id);
  }
  
}

/* end of source file */
