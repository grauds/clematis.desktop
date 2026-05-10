package jworkspace.ui.desktop.plaf;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Insets;
import java.awt.LayoutManager;

public class DesktopOverlayLayout implements LayoutManager {
    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int w = parent.getWidth() - insets.left - insets.right;
        int h = parent.getHeight() - insets.top - insets.bottom;

        for (Component comp : parent.getComponents()) {
            // Only resize your specific "full-screen" layers
            if (comp instanceof DesktopShortcutsLayer || comp instanceof DesktopShortcutSelector) {
                comp.setBounds(insets.left, insets.top, w, h);
            }
            // Internal frames are usually managed by the DesktopManager,
            // so we leave them alone here.
        }
    }

    // Required but unused for this specific logic
    @Override public void addLayoutComponent(String name, Component comp) {}
    @Override public void removeLayoutComponent(Component comp) {}
    @Override public Dimension preferredLayoutSize(Container parent) {
        return parent.getSize();
    }
    @Override public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }
}
