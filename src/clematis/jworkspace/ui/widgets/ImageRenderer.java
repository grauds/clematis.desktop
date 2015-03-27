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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import kiwi.ui.UIElement;
import kiwi.ui.UIElementViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Simple image renderer.
 */
public class ImageRenderer extends JComponent
        implements ImageObserver, UIElementViewer
{
    protected ImageIcon image = null;

    public ImageRenderer()
    {
        super();
        setOpaque(true);
        setPreferredSize(new Dimension(60, 60));
    }

    public Image getImage()
    {
        return image.getImage();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (image != null)
        {
            g.drawImage(image.getImage(), (getWidth() - image.getIconWidth()) / 2,
                        (getHeight() - image.getIconHeight()) / 2, this);
        }
    }

    public synchronized void setImage(Image image)
    {
        if (image == null) return;
        this.image = new ImageIcon(image);
        setPreferredSize(new Dimension(this.image.getIconWidth(),
                                       this.image.getIconHeight()));
        revalidate();
        repaint();
    }

    /** Get a reference to the viewer component.
     *
     * @return The viewer component.
     */

    public JComponent getViewerComponent()
    {
        return (this);
    }

    /** Show the specified element.
     *
     * @param element An object, assumed to be an instance of <code>Image</code>,
     * to display.
     */

    public void showElement(UIElement element)
    {
        Object obj = element.getObject();

        if (obj instanceof Image)
            setImage((Image) obj);
    }
}