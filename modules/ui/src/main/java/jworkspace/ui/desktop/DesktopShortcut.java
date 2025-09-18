package jworkspace.ui.desktop;
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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;
import lombok.Getter;
import lombok.Setter;

public class DesktopShortcut extends JComponent {

    private static final int DESKTOP_ICON_PREFERRED_SIZE = 130;

    @Getter
    private boolean selected;

    private Point lastMouse;

    private Runnable toggleSelectionHandler;

    private Runnable exclusiveSelectionHandler;

    @Getter
    @Setter
    private Supplier<Boolean> selectionProvider;

    @SuppressWarnings("checkstyle:MagicNumber")
    public DesktopShortcut(Icon icon, String text) {
        setLayout(new BorderLayout());
        setOpaque(false);

        DesktopIconLabel textLabel = new DesktopIconLabel(text);
        textLabel.setAlignment(DesktopIconLabel.CENTER);
        textLabel.setFont(textLabel.getFont().deriveFont(11f));
        add(textLabel, BorderLayout.SOUTH);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        add(iconLabel, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        installDragSupport();
    }

    @SuppressWarnings("checkstyle:AnonInnerLength")
    private void installDragSupport() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                if (e.isControlDown()) {
                    if (toggleSelectionHandler != null) {
                        toggleSelectionHandler.run();
                    }
                } else {
                    if (selectionProvider == null || !selectionProvider.get()) {
                        if (exclusiveSelectionHandler != null) {
                            exclusiveSelectionHandler.run();
                        }
                    }
                }

                lastMouse = SwingUtilities.convertPoint(DesktopShortcut.this, e.getPoint(), getParent());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMouse == null) {
                    return;
                }

                Point currentMouse = SwingUtilities.convertPoint(DesktopShortcut.this, e.getPoint(), getParent());
                int dx = currentMouse.x - lastMouse.x;
                int dy = currentMouse.y - lastMouse.y;

                if (dx != 0 || dy != 0) {
                    Container parent = getParent();
                    if (parent instanceof DesktopShortcutsLayer layer) {
                        for (DesktopShortcut s : layer.getSelectedShortcuts()) {
                            s.setLocation(s.getX() + dx, s.getY() + dy);
                        }
                    }
                }

                // 🔑 always update lastMouse here
                lastMouse = currentMouse;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouse = null;
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    // Selection API
    public void setSelected(boolean selected) {
        this.selected = selected; repaint();
    }

    public void addSelectionHandler(Runnable r) {
        this.toggleSelectionHandler = r;
    }

    public void addExclusiveSelectionHandler(Runnable r) {
        this.exclusiveSelectionHandler = r;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (selected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 120, 215, 80));
            g2.fillRoundRect(0, 0,  getWidth(),  getHeight(), 8, 8);
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DESKTOP_ICON_PREFERRED_SIZE, DESKTOP_ICON_PREFERRED_SIZE);
    }
}
