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

import javax.swing.*;

import com.hyperrealm.kiwi.util.KiwiUtils;

/** A trivial extension to <code>JCheckBox</code> that performs some simple
 * customizations.
 *
 * @see javax.swing.JCheckBox
 *
 * @author Mark Lindner
 *
 * @since Kiwi 1.4.3
 */

public class KCheckBox extends JCheckBox
{

  /** Construct a new <code>KCheckBox</code>. A new, transparent checkbox
   * will be created.
   *
   * @param action The action for the checkbox.
   * @since Kiwi 2.2
   */

  public KCheckBox(Action action)
  {
    super(action);
    _init();
  }

  /** Construct a new <code>KCheckBox</code>. A new, transparent checkbox
   * will be created.
   *
   * @param text The text to display for the checkbox.
   */

  public KCheckBox(String text)
  {
    super(text);
    _init();
  }

  /** Construct a new <code>KCheckBox</code>. A new, transparent checkbox
   * will be created.
   *
   * @param text The text to display for the checkbox.
   * @param icon The icon to display for the checkbox.
   */

  public KCheckBox(String text, Icon icon)
  {
    super(text, icon);
    _init();
  }

  /** Construct a new <code>KCheckBox</code>. A new, transparent checkbox
   * will be created.
   *
   * @param icon The icon to display in the button.
   */

  public KCheckBox(Icon icon)
  {
    super(icon);
    _init();
  }

  /*
   */
    
  private void _init()
  {
    setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
  }

}

/* end of source file */
