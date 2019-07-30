package jworkspace.ui.desktop;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003, 2019 Anton Troshin

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
import java.awt.Point;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * The manager is used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class ScrollingDesktopManager extends DefaultDesktopManager {

    private Desktop desktop;

    ScrollingDesktopManager(Desktop desktop) {
        this.desktop = desktop;
    }

    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        resizeDesktop();
    }

    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        resizeDesktop();
    }

    void setNormalSize() {

        JScrollPane scrollPane = getScrollPane();

        int x = 0;
        int y = 0;
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                    d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }
            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    private Insets getScrollPaneInsets() {
        JScrollPane scrollPane = getScrollPane();
        if (scrollPane == null) {
            return new Insets(0, 0, 0, 0);
        } else {
            return getScrollPane().getBorder().getBorderInsets(scrollPane);
        }
    }

    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport) desktop.getParent();
            if (viewPort.getParent() instanceof JScrollPane) {
                return (JScrollPane) viewPort.getParent();
            }
        }
        return null;
    }

    void resizeDesktop() {

        Point point = new Point(0, 0);

        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {

            calculateArea(point, desktop.getAllFrames());
            calculateArea(point, desktop.getDesktopIcons());

            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                    d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }
            if (point.x <= d.getWidth()) {
                point.x = ((int) d.getWidth()) - 20;
            }
            if (point.y <= d.getHeight()) {
                point.y = ((int) d.getHeight()) - 20;
            }
            desktop.setAllSize(point.x, point.y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    private static void calculateArea(Point point, Component... components) {
        int i = 0;
        while (i < components.length) {
            if (components[i].getX() + components[i].getWidth() > point.x) {
                point.x = components[i].getX() + components[i].getWidth();
            }
            if (components[i].getY() + components[i].getHeight() > point.y) {
                point.y = components[i].getY() + components[i].getHeight();
            }
            i++;
        }
    }
}
