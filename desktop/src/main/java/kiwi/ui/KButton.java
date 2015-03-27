/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-2001 Mark A. Lindner

   This file is part of Kiwi.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   mark_a_lindner@yahoo.com
   ----------------------------------------------------------------------------
   $Log: KButton.java,v $
   Revision 1.3  2001/03/12 09:27:56  markl
   Source code and Javadoc cleanup.

   Revision 1.2  1999/01/10 02:25:57  markl
   added GPL header & RCS tag
   ----------------------------------------------------------------------------
*/

package kiwi.ui;

import javax.swing.*;

import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.util.KiwiUtils;

/** A trivial extension to <code>JButton</code> that performs some simple
  * customizations.
  *
  * @see javax.swing.JButton
  *
  * @author Mark Lindner
  * @author PING Software Group
  */

public class KButton extends JButton
  {
  /** Construct a new <code>KButton</code>. A new, transparent button will be
    * created.
    */

  public KButton()
    {
    super();
    setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
    }
  /** Construct a new <code>KButton</code>. A new, transparent button will be
    * created.
    *
    * @param text The text to display in the button.
    */

  public KButton(String text)
    {
    super(text);
    setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
    }

  /** Construct a new <code>KButton</code>. A new, transparent button will be
    * created.
    *
    * @param text The text to display in the button.
    * @param icon The icon to display in the button.
    */

  public KButton(String text, Icon icon)
    {
    super(text, icon);
    setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
    }

  /** Construct a new <code>KButton</code>. A new, transparent button will be
    * created with zero-pixel margins and focus painting turned off.
    *
    * @param icon The icon to display in the button.
    */

  public KButton(Icon icon)
    {
    super(icon);
    setMargin(KiwiUtils.emptyInsets);
    setFocusPainted(false);
    setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
    }

   public void updateUI()
   {
       super.updateUI();
       setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
   }

  }

/* end of source file */
