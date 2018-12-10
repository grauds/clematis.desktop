/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * A simple component that renders a color swatch--a filled rectangle with
 * a thin black border.
 *
 * @author Mark Lindner
 */

public class ColorSwatch implements Icon {
    /**
     * The default swatch width.
     */
    private static final int DEFAULT_WIDTH = 50;
    /**
     * The default swatch height.
     */
    private static final int DEFAULT_HEIGHT = 15;
    /**
     * The default swatch color.
     */
    private static final Color DEFAULT_COLOR = Color.gray;
    private Color color;
    private int w, h;

    /**
     * Construct a new <code>ColorSwatch</code> with a default color, width,
     * and height.
     */

    public ColorSwatch() {
        this(DEFAULT_COLOR, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Construct a new <code>ColorSwatch</code> with the specified color and
     * geometry.
     *
     * @param color  The color for the swatch.
     * @param width  The width, in pixels.
     * @param height The height, in pixels.
     */

    public ColorSwatch(Color color, int width, int height) {
        this.color = color;
        this.w = width;
        this.h = height;
    }

    /**
     * Get the color of this swatch.
     *
     * @return The current color of the swatch.
     */

    public Color getColor() {
        return (color);
    }

    /**
     * Set the color of this swatch.
     *
     * @param color The new color for the swatch.
     */

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the width of the swatch.
     *
     * @return The width, in pixels.
     */

    public int getIconWidth() {
        return (w);
    }

    /**
     * Get the height of the swatch.
     *
     * @return The height, in pixels.
     */

    public int getIconHeight() {
        return (h);
    }

    /**
     * Paint the swatch (as an ICON).
     *
     * @param c  The component to paint the swatch in.
     * @param gc The graphics context.
     * @param x  The x-coordinate.
     * @param y  The y-coordinate.
     */

    public void paintIcon(Component c, Graphics gc, int x, int y) {
        gc.setColor(Color.black);
        gc.drawRect(x, y, w - 1, h - 1);
        gc.setColor(color);
        gc.fillRect(x + 1, y + 1, w - 2, h - 2);
    }

}
