package jworkspace.ui.desktop;
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.Serializable;

/**
 * DesktopLayout class lays out a desktop with icons.
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
record DesktopLayout(Desktop desktop) implements LayoutManager, Serializable {

    public void layoutContainer(Container c) {
        for (DesktopIcon icon : desktop.getDesktopIcons()) {
            icon.setBounds(icon.getX(),
                icon.getY(),
                icon.getPreferredSize().width,
                icon.getPreferredSize().height
            );
        }
    }

    public void addLayoutComponent(String str, Component c) {}

    public Dimension minimumLayoutSize(Container c) {
        return c.getPreferredSize();
    }

    public Dimension preferredLayoutSize(Container c) {
        return new Dimension(50, 50);
    }

    public void removeLayoutComponent(Component c) {}
}
