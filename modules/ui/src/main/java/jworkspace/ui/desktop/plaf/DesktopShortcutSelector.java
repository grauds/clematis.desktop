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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jworkspace.ui.desktop.DesktopShortcut;
import lombok.extern.java.Log;

@Log
public class DesktopShortcutSelector extends JComponent {

    private final DesktopShortcutsLayer shortcutsLayer;

    private Rectangle selectionRect;
    private Point bandStart;
    private Point dragStart;

    private final Map<DesktopShortcut, Point> initialPositions = new HashMap<>();
    private boolean draggingIcons = false;

    public DesktopShortcutSelector(DesktopShortcutsLayer shortcutsLayer) {
        this.shortcutsLayer = shortcutsLayer;
        setOpaque(false);

        MouseAdapter adapter = new SelectorMouseAdapter();
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    class SelectorMouseAdapter extends MouseAdapter {

        private void redispatch(MouseEvent e) {
            // 1. Find who is actually under the mouse in the shortcutsLayer
            Component c = SwingUtilities.getDeepestComponentAt(shortcutsLayer, e.getX(), e.getY());

            if (c != null && c != DesktopShortcutSelector.this) {
                // 2. Convert coordinates from the Selector's space to the target component's space
                Point p = SwingUtilities.convertPoint(DesktopShortcutSelector.this, e.getPoint(), c);

                // 3. Dispatch a copy of the event to the correct component
                c.dispatchEvent(new MouseEvent(
                    c,
                    e.getID(),
                    e.getWhen(),
                    e.getModifiersEx(),
                    p.x, p.y,
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()
                ));
            }
        }

        @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:ReturnCount"})
        @Override
        public void mousePressed(MouseEvent e) {
            Component c = shortcutsLayer.getComponentAt(e.getPoint());
            if (c instanceof DesktopShortcut shortcut) {
                if (e.isControlDown()) {
                    // Toggle selection
                    if (shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                        shortcutsLayer.removeFromSelection(shortcut);
                    } else {
                        shortcutsLayer.addToSelection(shortcut);
                    }
                } else {
                    // If the shortcut ISN'T selected, clear everything and select just this one.
                    // If it IS already selected, do nothing (keep the group selected for dragging).
                    if (!shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                        shortcutsLayer.clearSelection();
                        shortcutsLayer.addToSelection(shortcut);
                    }
                }

                // Prepare drag for the whole selected group
                dragStart = e.getPoint();
                initialPositions.clear();
                for (DesktopShortcut s : shortcutsLayer.getSelectedShortcuts()) {
                    initialPositions.put(s, s.getLocation());
                }
                draggingIcons = true;

            } else {
                // Clicked empty space → clear all and start rubber band
                shortcutsLayer.clearSelection();
                bandStart = e.getPoint();
                selectionRect = new Rectangle(bandStart);
                draggingIcons = false;
            }
            repaint();

            if (e.isPopupTrigger()) {
                redispatch(e);
            }
        }

        @SuppressWarnings("checkstyle:NestedIfDepth")
        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (draggingIcons && dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;

                    for (Map.Entry<DesktopShortcut, Point> entry : initialPositions.entrySet()) {
                        DesktopShortcut s = entry.getKey();
                        Point startPos = entry.getValue();
                        s.setLocation(startPos.x + dx, startPos.y + dy);
                    }
                } else if (bandStart != null) {
                    selectionRect.setBounds(
                        Math.min(bandStart.x, e.getX()),
                        Math.min(bandStart.y, e.getY()),
                        Math.abs(bandStart.x - e.getX()),
                        Math.abs(bandStart.y - e.getY())
                    );

                    for (DesktopShortcut s : shortcutsLayer.getShortcuts()) {
                        boolean inside = selectionRect.intersects(s.getBounds());
                        if (inside) {
                            shortcutsLayer.addToSelection(s);
                        } else {
                            shortcutsLayer.removeFromSelection(s);
                        }
                    }
                }
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragStart = null;
                bandStart = null;
                initialPositions.clear();
                selectionRect = null;
                draggingIcons = false;

                repaint();
            }
        }
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:InnerTypeLast"})
    @Override
    protected void paintComponent(Graphics g) {
        if (selectionRect != null) {
            g.setColor(new Color(0,  120,  215,  50));
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            g.setColor(Color.BLUE);
            g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
    }

    @SuppressWarnings("checkstyle:InnerTypeLast")
    @Override
    public boolean contains(int x, int y) {
        // If mid-selection, we must keep control of the mouse
        if (draggingIcons || selectionRect != null) {
            return true;
        }

        Component c = shortcutsLayer.getComponentAt(x, y);
        return (c == shortcutsLayer || c instanceof DesktopShortcut);
    }
}
