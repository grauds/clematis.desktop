package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016,2025 Anton Troshin

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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class Dashboard extends JPanel implements Scrollable {

    public Dashboard(List<Monitor> monitors) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        for (Monitor m : monitors) {
            add(m);
        }

        add(Box.createVerticalGlue());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Dimension getPreferredSize() {
        // Sum of all child heights + insets (ignoring the glue component)
        int height = 0;
        int maxWidth = 200; // Sensible default fallback width

        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            // Don't include the vertical glue in calculations
            if (!(comp instanceof Box.Filler)) {
                height += comp.getPreferredSize().height;
                if (comp.getPreferredSize().width > maxWidth) {
                    maxWidth = comp.getPreferredSize().width;
                }
            }
        }
        Insets insets = getInsets();
        return new Dimension(maxWidth + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Dimension getMaximumSize() {
        // FIX: Allow width to expand to match viewport tracking, but lock height to exact content preferences
        return new Dimension(32767, getPreferredSize().height);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(200, 500);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int getScrollableUnitIncrement(
        Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int getScrollableBlockIncrement(
        Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport vp) {
            return vp.getHeight() > getPreferredSize().height;
        }
        return false;
    }
}
