package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import jworkspace.util.WorkspaceUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Drag pane is created each time a component
 * is moved in Java Workspace. For example,
 * desktop icons or control panel use this component.
 */
public class GlassDragPane extends JComponent
{
    private Color bg = Color.darkGray;

    public void paint(Graphics g)
    {
        g.setColor(bg);
        WorkspaceUtils.drawDashedRect(g, 0, 0, getWidth(), getHeight());
        WorkspaceUtils.drawDashedRect(g, 1, 1, getWidth() - 2, getHeight() - 2);
    }

    public void setColor(Color bg)
    {
        this.bg = bg;
    }

    public Color getColor()
    {
        return bg;
    }
}