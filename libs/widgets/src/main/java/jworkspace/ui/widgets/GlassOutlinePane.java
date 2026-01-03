package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2025 Anton Troshin

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
import java.awt.Graphics;

import javax.swing.JComponent;

import jworkspace.ui.util.SwingUtils;
/**
 * A helper pane to use as an outline for component bounds. It has to be placed on top of the component
 * and have the same size as the component. Then it can be dragged to change the component bounds.
 *
 * @author Anton Troshin
 */
public class GlassOutlinePane extends JComponent {

    private Color bg = Color.darkGray;

    public void paint(Graphics g) {
        g.setColor(bg);
        SwingUtils.drawDashedRect(g, 0, 0, getWidth(), getHeight());
        SwingUtils.drawDashedRect(g, 1, 1, getWidth() - 2, getHeight() - 2);
    }

    public Color getColor() {
        return bg;
    }

    public void setColor(Color bg) {
        this.bg = bg;
    }
}