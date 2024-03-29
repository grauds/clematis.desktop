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

/* The original code came from http://www.codeguru.com/java/articles/199.shtml
 */

package com.hyperrealm.kiwi.ui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * A LabelUI for drawing vertical labels.
 *
 * @author Zafir Anjum
 * @author Mark Lindner
 * @since Kiwi 2.4
 */

public class VerticalLabelUI extends BasicLabelUI {

    private Rectangle paintIconR = new Rectangle();

    private Rectangle paintTextR = new Rectangle();

    private Rectangle paintViewR = new Rectangle();

    private Insets paintViewInsets = new Insets(0, 0, 0, 0);

    private boolean up;

    /**
     * Construct a new <code>VerticalLabelUI</code> with an orientation of "up",
     * that is, bottom-to-top.
     */

    public VerticalLabelUI() {
        this(true);
    }

    /**
     * Construct a new <code>VerticalLabelUI</code> with the given orientation.
     *
     * @param up The orientation; if <b>true</b>, the label is drawn bottom to
     *           top, otherwise top to bottom.
     */

    public VerticalLabelUI(boolean up) {
        this.up = up;
    }

    /*
     */

    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = super.getPreferredSize(c);
        return new Dimension(dim.height, dim.width);
    }

    /*
     */

    public void paint(Graphics g, JComponent c) {
        JLabel label = (JLabel) c;
        String text = label.getText();
        Icon icon = (label.isEnabled() ? label.getIcon()
            : label.getDisabledIcon());

        if ((icon == null) && (text == null)) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        paintViewInsets = c.getInsets(paintViewInsets);

        paintViewR.x = paintViewInsets.left;
        paintViewR.y = paintViewInsets.top;

        // Use inverted height & width
        paintViewR.height = (c.getWidth() - (paintViewInsets.left + paintViewInsets.right));
        paintViewR.width = (c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom));

        paintIconR.x = 0;
        paintIconR.y = 0;
        paintIconR.width = 0;
        paintIconR.height = 0;

        paintTextR.x = 0;
        paintTextR.y = 0;
        paintTextR.width = 0;
        paintTextR.height = 0;

        String clippedText = layoutCL(label, fm, text, icon, paintViewR,
            paintIconR, paintTextR);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tr = g2.getTransform();
        if (up) {
            g2.rotate(-Math.PI / 2);
            g2.translate(-c.getHeight(), 0);
        } else {
            g2.rotate(Math.PI / 2);
            g2.translate(0, -c.getWidth());
        }

        if (icon != null) {
            icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
        }

        if (text != null) {
            int textX = paintTextR.x;
            int textY = paintTextR.y + fm.getAscent();

            if (label.isEnabled()) {
                paintEnabledText(label, g, clippedText, textX, textY);
            } else {
                paintDisabledText(label, g, clippedText, textX, textY);
            }
        }

        g2.setTransform(tr);
    }
}
