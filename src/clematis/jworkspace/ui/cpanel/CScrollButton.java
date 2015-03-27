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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import kiwi.ui.KButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * This class is a replacement for scrollbar
 * in scrollpane. Everytime user clicks on
 * the scroll button, the buttons on the
 * viewport will scroll.
 */
class CScrollButton extends KButton implements PropertyChangeListener
{
    /**
     * Orientation of control bar relative to
     * parent component.
     */
    private int orientation;
    /**
     * Scroll button can be incremental or decremental.
     * Incremental button increases the scrolling
     * value and is usually is in the bottom
     * of control panel. Decremental button decreases
     * scrolling value and is usually on the top
     * of control panel.
     */
    private boolean incremental = true;

    /**
     * Empty constructor for compatibility
     * with beans standard.
     */
    public CScrollButton()
    {
        super();
        setOpaque(false);
        setDefaultCapable(false);
    }

    /**
     * Scroll bar will send action events to
     * buttons pane, allowing it to scroll
     * buttons layout. For successful
     * scrolling action listener must
     * be a viewport.
     */
    public CScrollButton(ActionListener listener)
    {
        super();
        this.addActionListener(listener);
        setOpaque(false);
        setDefaultCapable(false);
    }

    /**
     * Scroll bar will send action events to
     * buttons pane, allowing it to scroll
     * buttons layout. For successful
     * scrolling action listener must
     * be a viewport.
     */
    public CScrollButton(ActionListener listener, boolean incremental)
    {
        super();
        this.addActionListener(listener);
        this.incremental = incremental;
        if (incremental)
        {
            this.setActionCommand("INCREMENT");
        }
        else
        {
            this.setActionCommand("DECREMENT");
        }
        setOpaque(false);
        setDefaultCapable(false);
    }

    /**
     * Paints button background and two scrolling
     * arrows depending on the control's panel orientation.
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        boolean isPressed, isEnabled;
        int w, h, size;
        w = getSize().width;
        h = getSize().height;
        isPressed = getModel().isPressed();
        isEnabled = isEnabled();

        size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);

        if (!isPressed)
            paintArrow(g, (w - size) / 2, (h - size) / 2, size, isEnabled);
        else if (isPressed)
            paintArrow(g, (w - size) / 2 + 1, (h - size) / 2 + 1, size, isEnabled);
    }

    /**
     * Paints single arrow as basic UI does.
     * BUG: this is not nesessarily works on other's laf
     * color schemes. Should be replaced by
     * <code>Color.darker()</code> and <code>Color.lighter</code>
     * methods.
     */
    protected void paintArrow(Graphics g, int x, int y, int size,
                              boolean isEnabled)
    {
        int mid, i, j;
        j = 0;
        size = Math.max(size, 2);
        mid = size / 2;
        g.translate(x, y);

        if (isEnabled)
            g.setColor(UIManager.getColor("controlText"));
        else
            g.setColor(UIManager.getColor("controlLightShadow"));

        if ( orientation == ControlPanel.Y_AXIS )
        {
            /**
             * Up vertical button.
             */
            if (incremental)
            {
                for (i = 0; i < size; i++)
                {
                    g.drawLine(mid - i, i, mid + i, i);
                }
                if (!isEnabled)
                {
                    g.setColor(UIManager.getColor("controlHighlight"));
                    g.drawLine(mid - i + 2, i, mid + i, i);
                }
            }
            /**
             * down vertical button.
             */
            else
            {
                if (!isEnabled)
                {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlHighlight"));
                    for (i = size - 1; i >= 0; i--)
                    {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(UIManager.getColor("controlShadow"));
                }
                j = 0;
                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
            }
        }
        else
        {
            /**
             * Up horizontal button.
             */
            if (incremental)
            {
                for (i = 0; i < size; i++)
                {
                    g.drawLine(i, mid - i, i, mid + i);
                }
                if (!isEnabled)
                {
                    g.setColor(UIManager.getColor("controlHighlight"));
                    g.drawLine(i, mid - i + 2, i, mid + i);
                }
            }
            /**
             * Down horizontal button.
             */
            else
            {
                if (!isEnabled)
                {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlHighlight"));
                    for (i = size - 1; i >= 0; i--)
                    {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(UIManager.getColor("controlShadow"));
                }
                j = 0;
                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(j, mid - i, j, mid + i);
                    j++;
                }
            }
        }
        g.translate(-x, -y);
    }

    /**
     * Each time control panel will change
     * its orientation property, this will
     * handle incoming event.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt)
    {
        if (evt.getNewValue() instanceof Integer &&
                evt.getPropertyName().equals("ORIENTATION"))
        {
            orientation = ((Integer) evt.getNewValue()).intValue();
        }
    }

    public Dimension getPreferredSize()
    {
        if ( orientation == ControlPanel.Y_AXIS )
        {
            return new Dimension((int) super.getPreferredSize().getWidth() + 5,
                                 (int) super.getPreferredSize().getHeight() + 5);
        }
        else
        {
            return new Dimension((int) super.getPreferredSize().getHeight() + 5,
                                 (int) super.getPreferredSize().getWidth() + 5);
        }
    }
}