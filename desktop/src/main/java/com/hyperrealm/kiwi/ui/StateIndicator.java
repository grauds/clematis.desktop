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

package com.hyperrealm.kiwi.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.util.KiwiUtils;

/** A graphical state indicator component. This component displays one
 * of a collection of images, depending on what its current "state"
 * is. Each state is indicated by a unique string; the component maintains a
 * mapping of these strings to the corresponding state icons.
 * <p>
 * This component can be used to create status icons and other
 * types of multi-state indicators.
 *
 * @see com.hyperrealm.kiwi.ui.ToggleIndicator
 *
 * @since Kiwi 1.4.3
 *
 * @author Mark Lindner
 */

public class StateIndicator extends JLabel
{
  private String state = null;
  private HashMap<String, Icon> icons = new HashMap<String, Icon>();
  private Icon defaultIcon;

  /** Construct a new <code>StateIndicator</code>.
   *
   * @param defaultIcon The default icon to display when the state is unknown.
   */
  
  public StateIndicator(Icon defaultIcon)
  {
    this.defaultIcon = defaultIcon;

    setHorizontalAlignment(SwingConstants.CENTER);
    setVerticalAlignment(SwingConstants.CENTER);

    setIcon(defaultIcon);
  }

  /**
   * Remove all states from the indicator.
   */

  public void clearStates()
  {
    icons.clear();
  }

  /**
   * Add a new state to the indicator.
   *
   * @param state The name of the state.
   * @param icon The icon to display for this state.
   */

  public void addState(String state, Icon icon)
  {
    icons.put(state, icon);
  }

  /**
   * Remove a state from the indicator.
   *
   * @param state The name of the state.
   */

  public void removeState(String state)
  {
    icons.remove(state);
  }

  /** Set the state of the indicator.
   *
   * @param state The new state for the indicator. The indicator will
   * be repainted immediately. If the specified <code>state</code> is
   * invalid, the state will be set to <code>null</code> and the icon
   * to default icon.
   */
  
  public synchronized void setState(String state)
  {
    Icon icon = icons.get(state);
    if(icon == null)
    {
      icon = defaultIcon;
      this.state = null;
    }
    else
      this.state = state;
    
    setIcon(icon);
    KiwiUtils.paintImmediately(this);
  }

  /** Get the current state of the indicator.
   *
   * @return The current state of the indicator, which may be
   * <code>null</code>.
   */

  public synchronized String getState()
  {
    return(state);
  }

}

/* end of source file */
