package jworkspace.ui.widgets;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;

/**
 * A helper pane to use as an outline for component bounds. It has to be placed on top of the component
 * and have the same size as the component. Then it can be dragged to change the component bounds.
 *
 * @author Anton Troshin
 */
public class GlassOutlinePane extends JComponent {

    public static final String DRAGGING_PROPERTY = "DRAGGING";
    public static final String DRAGGED_PROPERTY = "DRAGGED";

    private boolean dragged = false;
    private Point pointer = new Point();

    @Getter
    @Setter
    private Rectangle paintingBounds = new Rectangle();

    private final Color outlineColor = Color.DARK_GRAY;

    public GlassOutlinePane() {
        setOpaque(false);
        setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GlassOutlinePane.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                GlassOutlinePane.this.mouseReleased(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                GlassOutlinePane.this.mouseDragged(e);
            }
        });
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void paint(Graphics g) {
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(outlineColor);
            g2.setStroke(new BasicStroke(2,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0, new float[]{5}, 0)
            );
            g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
            g2.drawRect(getPaintingBounds().x,
                getPaintingBounds().y,
                (int) (getPaintingBounds().getWidth()),
                (int) (getPaintingBounds().getHeight())
            );
            g2.dispose();
        }
    }

    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        dragged = true;
        pointer = e.getPoint();
        setVisible(true);
        firePropertyChange(DRAGGING_PROPERTY, false, true);
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && dragged) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            firePropertyChange(DRAGGED_PROPERTY, pointer, e.getPoint());
            pointer = e.getPoint();
        } else {
            endDrag();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        endDrag();
    }

    public void endDrag() {
        if (dragged) {
            dragged = false;
            setVisible(false);
            firePropertyChange(DRAGGING_PROPERTY, true, false);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public void setPaintingBounds(int x, int y, int width, int height) {
        this.paintingBounds = new Rectangle(x, y, width, height);
    }
}