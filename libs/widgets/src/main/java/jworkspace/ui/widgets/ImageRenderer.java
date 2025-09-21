package jworkspace.ui.widgets;

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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.hyperrealm.kiwi.ui.UIElement;
import com.hyperrealm.kiwi.ui.UIElementViewer;

/**
 * Simple image renderer.
 * @author Anton Troshin
 */
public class ImageRenderer extends JComponent implements ImageObserver, UIElementViewer {

    protected ImageIcon image = null;

    @SuppressWarnings("MagicNumber")
    public ImageRenderer() {
        super();
        setOpaque(true);
        setPreferredSize(new Dimension(60, 60));
    }

    public synchronized Image getImage() {
        if (image != null) {
            return image.getImage();
        }
        return null;
    }

    public synchronized void setImage(Image image) {
        if (image == null) {
            return;
        }
        this.image = new ImageIcon(image);
        setPreferredSize(
            new Dimension(this.image.getIconWidth(), this.image.getIconHeight())
        );
        revalidate();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (image != null) {
            g.drawImage(image.getImage(), (getWidth() - image.getIconWidth()) / 2,
                (getHeight() - image.getIconHeight()) / 2, this);
        }
    }

    /**
     * Get a reference to the viewer component.
     *
     * @return The viewer component.
     */

    public JComponent getViewerComponent() {
        return (this);
    }

    /**
     * Show the specified element.
     *
     * @param element An object, assumed to be an instance of <code>Image</code>,
     *                to display.
     */

    public void showElement(UIElement element) {
        Object obj = element.getObject();

        if (obj instanceof Image) {
            setImage((Image) obj);
        }
    }
}