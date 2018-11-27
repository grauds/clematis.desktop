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

import java.awt.Color;
import javax.swing.*;
import javax.swing.border.LineBorder;

import com.hyperrealm.kiwi.text.*;

/** A <code>MarkupProxy</code> is a graphical element that serves as a proxy
 * for a data structure that cannot easily be rendered in-line within a
 * <code>SimpleStyledEditor</code>.
 *
 * @see com.hyperrealm.kiwi.ui.SimpleStyledEditor
 * @see com.hyperrealm.kiwi.ui.MarkupProxyFactory
 *
 * @author Mark Lindner
 */

public abstract class MarkupProxy extends JLabel
{
  /** The text for this proxy. */
  protected String text;

  /** Construct a new <code>MarkupProxy</code>.
   *
   * @param icon The icon for this proxy.
   * @param text The text to display in this proxy.
   */

  public MarkupProxy(Icon icon, String text)
  {
    setText(text);
    setIcon(icon);
    setBackground(new Color(200, 200, 200));
    setOpaque(true);
    setToolTipText(getDescription());
    setBorder(new LineBorder(Color.black, 1));
  }

  /** Get the textual markup used to encode this proxy. */

  public abstract String getMarkup();

  /** Get a description of this proxy. */

  public abstract String getDescription();

}

/* end of source file */
