package jworkspace.ui.widgets;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTextArea;

/**
 * Scrollback view automatically scrolls view as new entries are added
 *
 * @author Anton Troshin
 */
public class ScrollbackView extends JTextArea {

    private static final int DEFAULT_FONT_SIZE = 13;

    public void setFont(Font font) {
        super.setFont(new Font("Monospaced", Font.PLAIN, DEFAULT_FONT_SIZE));
    }

    public void append(String str) {
        super.append(str + "\n");
        Rectangle b = new Rectangle(0, getHeight() - DEFAULT_FONT_SIZE, getWidth(), getHeight());
        scrollRectToVisible(b);
    }

    public void setBackground(Color bg) {
        super.setBackground(Color.lightGray);
    }

    public void setForeground(Color fg) {
        super.setForeground(Color.black);
    }
}
