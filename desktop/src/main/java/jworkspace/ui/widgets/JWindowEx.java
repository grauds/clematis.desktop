package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * This window class is a workaround for a Swing bug in
 * JWindow. WIndow does not get focus on any component
 * on it, for example, a text field does not show cursor, etc.
 */
public class JWindowEx extends JWindow implements FocusListener
{
    public JWindowEx(javax.swing.JFrame parent)
    {
        super(parent);
        addFocusListener(this);
    }

    public void focusGained(FocusEvent e)
    {
        dispatchEvent(new java.awt.event.WindowEvent(this,
                                                     java.awt.event.WindowEvent.WINDOW_ACTIVATED));
    }

    public void focusLost(FocusEvent e)
    {
    }
}