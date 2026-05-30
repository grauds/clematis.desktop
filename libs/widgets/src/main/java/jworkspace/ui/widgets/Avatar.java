package jworkspace.ui.widgets;
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
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import lombok.Getter;
import lombok.Setter;

/**
 * Avatar component thanks to <a href="https://github.com/DJ-Raven/java-swing-image-avatar-v2">
 *     https://github.com/DJ-Raven/java-swing-image-avatar-v2
 * </a>
 */
@Getter
@Setter
public class Avatar extends JComponent {

    private ImageIcon icon;
    private int borderSize = 0;
    private double imageScale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean editable = false;

    private Point dragStartPoint;
    private int dragStartOffsetX;
    private int dragStartOffsetY;

    public Avatar() {
        initMouseListeners();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void initMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (editable && icon != null) {
                    dragStartPoint = e.getPoint();
                    dragStartOffsetX = offsetX;
                    dragStartOffsetY = offsetY;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (editable && icon != null && dragStartPoint != null) {
                    offsetX = dragStartOffsetX + (e.getX() - dragStartPoint.x);
                    offsetY = dragStartOffsetY + (e.getY() - dragStartPoint.y);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStartPoint = null;
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseWheelListener(e -> {
            if (editable && icon != null) {
                double zoomFactor = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
                imageScale = Math.clamp(imageScale * zoomFactor, 0.1, 10.0);
                repaint();
            }
        });
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        resetTransformations();
        repaint();
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        repaint();
    }

    public void resetTransformations() {
        this.imageScale = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    public ImageIcon getCroppedIcon() {
        if (icon == null) {
            return null;
        }

        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height) - (borderSize * 2);

        if (diameter <= 0) {
            return icon;
        }

        BufferedImage croppedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = croppedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Fill the circle matching the canvas bounding coordinates
        g2d.fillOval(0, 0, diameter, diameter);
        g2d.setComposite(AlphaComposite.SrcIn);

        // Calculate base bounding size centered inside a 0-indexed canvas matching 'diameter'
        Rectangle baseSize = getAutoSize(icon, diameter);
        int finalWidth = (int) (baseSize.width * imageScale);
        int finalHeight = (int) (baseSize.height * imageScale);

        // Fix: Coordinates must center around the (diameter / 2) origin, ignoring parent UI layouts
        int finalX = baseSize.x + offsetX - (int) ((finalWidth - baseSize.width) / 2.0);
        int finalY = baseSize.y + offsetY - (int) ((finalHeight - baseSize.height) / 2.0);

        g2d.drawImage(icon.getImage(), finalX, finalY, finalWidth, finalHeight, null);
        g2d.dispose();

        return new ImageIcon(croppedImage);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        // Always execute background super pipeline calls first
        super.paintComponent(grphcs);

        if (icon != null) {
            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height);

            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;
            int border = borderSize * 2;

            diameter -= border;

            Rectangle size = getAutoSize(icon, diameter);

            BufferedImage img = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D imageG2D = img.createGraphics();

            imageG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            imageG2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            imageG2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            imageG2D.fillOval(0, 0, diameter, diameter);

            Composite composite = imageG2D.getComposite();
            imageG2D.setComposite(AlphaComposite.SrcIn);

            int finalWidth = (int) (size.width * imageScale);
            int finalHeight = (int) (size.height * imageScale);

            int finalX = size.x + offsetX - (int) ((finalWidth - size.width) / 2.0);
            int finalY = size.y + offsetY - (int) ((finalHeight - size.height) / 2.0);

            imageG2D.drawImage(icon.getImage(), finalX, finalY, finalWidth, finalHeight, null);
            imageG2D.setComposite(composite);
            imageG2D.dispose();

            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Fix: Render the border background rings underneath the raster image buffer
            if (borderSize > 0) {
                g2.setColor(getForeground());
                g2.fillOval(x, y, diameter + border, diameter + border);
            }

            if (isOpaque()) {
                g2.setColor(getBackground());
                g2.fillOval(x + borderSize, y + borderSize, diameter, diameter);
            }

            g2.drawImage(img, x + borderSize, y + borderSize, null);
        }
    }

    private Rectangle getAutoSize(Icon image, int size) {
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();

        double xScale = (double) size / iw;
        double yScale = (double) size / ih;
        // Cover strategy: guarantee whole square mask is fully covered
        double scale = Math.max(xScale, yScale);

        int width = Math.max(1, (int) (scale * iw));
        int height = Math.max(1, (int) (scale * ih));

        int x = (size - width) / 2;
        int y = (size - height) / 2;

        return new Rectangle(x, y, width, height);
    }
}

