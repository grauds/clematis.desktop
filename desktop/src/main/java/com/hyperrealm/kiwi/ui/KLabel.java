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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A trivial extension to <code>JLabel</code> that performs some simple
 * customizations and supports optional anti-aliased rendering.
 *
 * @author Mark Lindner
 * @since Kiwi 1.3
 */

public class KLabel extends JLabel {

    private boolean antialiased = false;

    /**
     * Construct a new <code>KLabel</code>.
     */

    public KLabel() {
        super();

        init();
    }

    /**
     * Construct a new <code>KLabel</code> with the specified image.
     *
     * @param image The image.
     */

    public KLabel(Icon image) {
        super(image);

        init();
    }

    /**
     * Construct a new <code>KLabel</code> with the specified image and
     * horizontal alignment.
     *
     * @param image               The image.
     * @param horizontalAlignment The horizontal alignment.
     */

    public KLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);

        init();
    }

    /**
     * Construct a new <code>KLabel</code> with the specified text.
     *
     * @param text The text.
     */

    public KLabel(String text) {
        super(text);

        init();
    }

    /**
     * Construct a new <code>KLabel</code> with the specified text, icon and
     * horizontal alignment.
     *
     * @param text                The text.
     * @param icon                The icon.
     * @param horizontalAlignment The horizontal alignment.
     */

    public KLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);

        init();
    }

    /**
     * Construct a new <code>KLabel</code> with the specified text and
     * horizontal alignment.
     *
     * @param text                The text.
     * @param horizontalAlignment The horizontal alignment.
     */

    public KLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);

        init();
    }

    /*
     */

    private void init() {
        setOpaque(false);
        setForeground(Color.black);
    }

    /**
     * Determine if antialiased rendering is enabled for this label.
     *
     * @since Kiwi 2.2
     */

    public boolean isAntiAliased() {
        return (antialiased);
    }

    /**
     * Enable or disable antialiased rendering for this label.
     *
     * @since Kiwi 2.2
     */

    public void setAntiAliased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    /*
     */

    public void paintComponent(Graphics g) {
        if (antialiased) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        }

        super.paintComponent(g);
    }

}
