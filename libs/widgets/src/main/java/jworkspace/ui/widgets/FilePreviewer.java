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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * File previewer works with graphic files, allowing thumbnails in JFileDialog.
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class FilePreviewer extends JComponent implements PropertyChangeListener {

    private ImageIcon thumbnail = null;

    private File f = null;

    public FilePreviewer(JFileChooser fc) {
        setPreferredSize(new Dimension(200, 50));
        fc.addPropertyChangeListener(this);
    }

    private void loadImage() {
        if (f != null) {
            Image image;
            try {
                image = ImageIO.read(f);
                ImageIcon tmpIcon = new ImageIcon(image);
                if (tmpIcon.getIconWidth() > 90) {
                    thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(180, -1,
                        Image.SCALE_DEFAULT));
                } else {
                    thumbnail = tmpIcon;
                }
            } catch (Exception e) {
                thumbnail = null;
            }
        }
    }

    public void paint(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
            if (y < 0) {
                y = 0;
            }
            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            f = (File) e.getNewValue();
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    public void reset() {
        f = null;
        thumbnail = null;
        repaint();
    }
}