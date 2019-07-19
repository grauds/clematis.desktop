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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Desktop key listener
 * @author Anton Troshin
 */
class DesktopKeyAdapter extends KeyAdapter {
    private Desktop desktop;

    DesktopKeyAdapter(Desktop desktop) {
        this.desktop = desktop;
    }

    @SuppressWarnings("CyclomaticComplexity")
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (e.getSource() instanceof DesktopIcon) {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_NORTH, (DesktopIcon) e.getSource());
            } else {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_NORTH);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (e.getSource() instanceof DesktopIcon) {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_SOUTH, (DesktopIcon) e.getSource());
            } else {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_SOUTH);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (e.getSource() instanceof DesktopIcon) {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_WEST, (DesktopIcon) e.getSource());
            } else {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_WEST);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (e.getSource() instanceof DesktopIcon) {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_EAST, (DesktopIcon) e.getSource());
            } else {
                desktop.selectNextIcon(DesktopConstants.ICON_ON_EAST);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            desktop.removeSelectedIcons();
        } else if (e.getKeyCode() == KeyEvent.VK_V
            && e.getModifiers() == KeyEvent.CTRL_MASK) {
            if (e.getSource() instanceof Desktop) {
                ((Desktop) e.getSource()).pasteIcons();
            } else if (e.getSource() instanceof DesktopIcon) {
                ((DesktopIcon) e.getSource()).desktop.pasteIcons();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A
            && e.getModifiers() == KeyEvent.CTRL_MASK) {
            if (e.getSource() instanceof Desktop) {
                ((Desktop) e.getSource()).selectAll();
            } else if (e.getSource() instanceof DesktopIcon) {
                ((DesktopIcon) e.getSource()).desktop.selectAll();
            }
        }
    }
}
