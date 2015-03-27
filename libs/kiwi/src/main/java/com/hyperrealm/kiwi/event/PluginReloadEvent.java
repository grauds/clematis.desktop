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

import com.hyperrealm.kiwi.ui.model.*;
import com.hyperrealm.kiwi.util.plugin.*;

/** A class that represents a plugin reload event.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class PluginReloadEvent
{
  private Plugin source;

  /** Construct a new <code>PluginReloadEvent</code> for the specified source.
   *
   * @param source The <code>Plugin</code> that is the source of this
   * event.
   */
  
  public PluginReloadEvent(Plugin source)
  {
    this.source = source;
  }

  /** Get the source of the event.
   *
   * @return The <code>Plugin</code> that is the source of this event.
   */
  
  public Plugin getSource()
  {
    return(source);
  }
  
}

/* end of source file */
