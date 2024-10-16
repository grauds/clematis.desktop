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
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import jworkspace.ui.widgets.GlassDragPane;

/**
 * Icon group - this class manages group icons operations. Inner drag panes are nessesary for visual drag movement.
 * @author Anton Troshin
 */
class DesktopIconsGroup implements Serializable {

    private final Desktop desktop;

    private final List<GlassDragPane> dragPanes = new Vector<>();

    private int xShift = 0;

    private int yShift = 0;

    DesktopIconsGroup(Desktop desktop) {

        super();
        this.desktop = desktop;
        /*
         * Create desktop icons group from selected
         * icons - this happens then
         */
        for (int i = 0; i < desktop.getDesktopIcons().length; i++) {
            DesktopIcon icon = desktop.getDesktopIcons()[i];
            if (icon.isSelected()) {

                GlassDragPane dragPane = new GlassDragPane();
                dragPane.setColor(desktop.getSelectionColor());
                desktop.setLayer(dragPane, Integer.MAX_VALUE - i);
                dragPane.setBounds(icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
                desktop.add(dragPane);
                dragPanes.add(dragPane);
            }
        }
    }

    void moveTo(int x, int y) {

        xShift = x;
        yShift = y;
        int counter = 0;
        for (int i = 0; i < desktop.getDesktopIcons().length; i++) {
            DesktopIcon icon = desktop.getDesktopIcons()[i];
            if (icon.isSelected()) {

                GlassDragPane dragPane = dragPanes.get(counter);
                Point location = icon.getLocation();
                dragPane.setLocation(location.x + xShift, location.y + yShift);
                counter++;
            }
        }
    }

    public void destroy() {
        int counter = 0;
        Point point = desktop.getTransferVector(xShift, yShift);
        desktop.requestFocus();
        xShift = point.x;
        yShift = point.y;
        for (int i = 0; i < desktop.getDesktopIcons().length; i++) {
            DesktopIcon icon = desktop.getDesktopIcons()[i];
            if (icon.isSelected()) {

                GlassDragPane dragPane = dragPanes.get(counter);
                desktop.remove(dragPane);
                desktop.remove(icon);

                icon.setXPos(icon.getXPos() + xShift);
                icon.setYPos(icon.getYPos() + yShift);
                desktop.add(icon);
                counter++;
            }
        }
        dragPanes.clear();
        desktop.revalidate();
        desktop.repaint();
    }
}
