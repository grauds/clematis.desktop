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

import java.beans.*;

/** Interface that must be implemented by objects that are sources of
 * <code>PropertyChangeEvent</code>s.
 *
 * @see java.beans.PropertyChangeEvent
 *
 * @author Mark Lindner
 */

public interface PropertyChangeSource
{

  /** Register a new property change listener.
   *
   * @param listener The <code>PropertyChangeListener</code> to be added to
   * this object's list of listeners.
   */

  public void addPropertyChangeListener(PropertyChangeListener listener);

  /** Unregister a property change listener.
   *
   * @param listener The <code>PropertyChangeListener</code> to be removed
   * from this object's list of listeners.
   */

  public void removePropertyChangeListener(PropertyChangeListener listener);
}

/* end of source file */
