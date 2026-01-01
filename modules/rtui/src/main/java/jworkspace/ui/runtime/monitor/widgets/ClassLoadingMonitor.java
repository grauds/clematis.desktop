package jworkspace.ui.runtime.monitor.widgets;
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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

import jworkspace.ui.runtime.monitor.AbstractJvmGraphMonitor;

public class ClassLoadingMonitor extends AbstractJvmGraphMonitor {

    private final ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();

    @Override
    protected float sampleValue() {
        // Float for smooth graph
        return (float) bean.getLoadedClassCount();
    }

    @Override
    protected String title() {
        // Integer display
        return "Loaded Classes";
    }

    @Override
    protected String unit() {
        return "classes";
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void drawLegend(int x) {
        g2.setColor(Color.green);
        g2.drawString(title(), x, ascent + 1);

        if (ptNum > 0) {
            String cur = String.format("%d %s", (int) pts[ptNum - 1], unit());
            g2.drawString(cur, w - g2.getFontMetrics().stringWidth(cur) - 5, ascent + 1);
        }
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Override
    public String getToolTipText(MouseEvent e) {
        if (pts == null || ptNum == 0) {
            return null;
        }
        int idx = ptNum - (getWidth() - e.getX());
        if (idx < 0 || idx >= ptNum) {
            return null;
        }

        return String.format("%d classes", (int) pts[idx]);
    }
}
