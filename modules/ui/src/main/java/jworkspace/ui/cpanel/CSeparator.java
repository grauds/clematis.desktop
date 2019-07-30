package jworkspace.ui.cpanel;

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
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * Separator is generally a thin component, that divides button on logic groups. The pecularity is expressed in
 * boolean reverse field, that is used by control panel's layout algorythm. Use <code>addLayoutComponent()</code>
 * method with specified parameters to insert buttons and separators into control panel.
 */
class CSeparator extends JComponent implements PropertyChangeListener {
    /**
     * Orientation of control bar relative to parent component.
     */
    private int orientation;

    /**
     * Empty constructor for compatibility
     * with beans standard.
     */
    private CSeparator(int orientation) {
        super();
        this.orientation = orientation;
    }

    /**
     * Paints separator. In this case it is just two one pixel width lines of colors, compatible with
     * Java Workspace Look And Feel (Glass Onion). BUG: this is not necessarily works on other's laf
     * color schemes. Should be replaced by <code>Color.darker()</code> and <code>Color.lighter</code> methods.
     */
    public void paintComponent(Graphics g) {
        if (orientation == ControlPanel.X_AXIS) {
            g.setColor(UIManager.getColor(CScrollButton.CONTROL_SHADOW_COLOUR));
            g.drawLine(0, 0, 0, getSize().height);
            g.setColor(UIManager.getColor(CScrollButton.CONTROL_HIGHLIGHT_COLOUR));
            g.drawLine(1, 0, 1, getSize().height);
        } else if (orientation == ControlPanel.Y_AXIS) {
            g.setColor(UIManager.getColor(CScrollButton.CONTROL_SHADOW_COLOUR));
            g.drawLine(0, 0, getSize().width, 0);
            g.setColor(UIManager.getColor(CScrollButton.CONTROL_HIGHLIGHT_COLOUR));
            g.drawLine(0, 1, getSize().width, 1);
        }
    }

    /**
     * Each time control panel will change
     * its orientation property, this will
     * handle incoming event.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Integer && evt.getPropertyName().equals(ControlPanel.ORIENTATION_PROPERTY)) {
            orientation = (Integer) evt.getNewValue();
        }
    }

    public float getAlignmentX() {
        return CENTER_ALIGNMENT;
    }

    public float getAlignmentY() {
        return CENTER_ALIGNMENT;
    }

    /**
     * Calculates the preferred size dimensions for the specified panel given the components
     * in the specified parent container.
     */
    @SuppressWarnings("MagicNumber")
    public Dimension getPreferredSize() {
        if (orientation == ControlPanel.X_AXIS) {
            return new Dimension(2, 20);
        } else {
            return new Dimension(20, 2);
        }
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}