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
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import static com.hyperrealm.kiwi.ui.ConsolePanel.DEFAULT_FONT_SIZE;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * A general-purpose image viewing component. It displays an image in a scroll
 * pane and provides zooming buttons.
 *
 * <p><center>
 * <img src="snapshot/ImageView.gif"><br>
 * <i>An example ImageView.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see java.awt.Image
 */

public class ImageView extends KPanel {

    private static final float SCALE_FACTOR = 1.5f;

    private KButton bZoomin, bZoomout, bZoomreset;

    private KLabel viewport;

    private int origw, origh;

    private float scale = 1;

    private Image image, curImage = null;

    /**
     * Construct a new <code>ImageView</code>.
     *
     * @param comment A comment to display above the image.
     * @param image   The image to display.
     */

    public ImageView(String comment, Image image) {

        origw = image.getWidth(this);

        origh = image.getHeight(this);

        this.image = image;

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        ActionListener actionListener = evt -> {

            Object o = evt.getSource();

            if (o == bZoomin) {
                scaleImage(2.0f);
            } else if (o == bZoomout) {
                scaleImage(SCALE_FACTOR);
            } else if (o == bZoomreset) {
                scaleImage(1.0f);
            }
        };

        ResourceManager rm = KiwiUtils.getResourceManager();

        setBorder(KiwiUtils.DEFAULT_BORDER);
        setLayout(DEFAULT_BORDER_LAYOUT);

        KPanel p1 = new KPanel();
        p1.setLayout(DEFAULT_BORDER_LAYOUT);

        KLabel label = new KLabel(comment);
        label.setFont(new Font("Serif", Font.BOLD, DEFAULT_FONT_SIZE));

        p1.add(CENTER_POSITION, label);

        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);

        bZoomin = new KButton(rm.getIcon("zoom_in.png"));
        bZoomin.setToolTipText(loc.getMessage("kiwi.tooltip.zoom_in"));
        bZoomin.addActionListener(actionListener);
        toolBar.add(bZoomin);

        bZoomout = new KButton(rm.getIcon("zoom_out.png"));
        bZoomout.setToolTipText(loc.getMessage("kiwi.tooltip.zoom_out"));
        bZoomout.addActionListener(actionListener);
        toolBar.add(bZoomout);

        bZoomreset = new KButton(rm.getIcon("zoom.png"));
        bZoomreset.setToolTipText(loc.getMessage("kiwi.tooltip.actual_size"));
        bZoomreset.addActionListener(actionListener);
        toolBar.add(bZoomreset);

        p1.add(EAST_POSITION, toolBar);

        add("North", p1);

        viewport = new KLabel();
        viewport.setIcon(new ImageIcon(image));
        viewport.setVerticalAlignment(SwingConstants.CENTER);
        viewport.setHorizontalAlignment(SwingConstants.CENTER);

        KScrollPane scroll = new KScrollPane(viewport);
        scroll.setBackground(Color.white);
        add(CENTER_POSITION, scroll);
    }

    /**
     * Scale the image by a given scale factor.
     *
     * @param scaleFactor The scale factor.
     */

    public void scaleImage(float scaleFactor) {

        if (scaleFactor != 1) {
            scale *= scaleFactor;
        } else {
            scale = 1;
        }

        KiwiUtils.busyOn(this);
        if (curImage != null) {
            curImage.flush();
        }
        curImage = image.getScaledInstance((int) ((float) origw * scale),
            (int) ((float) origh * scale),
            Image.SCALE_SMOOTH);
        viewport.setIcon(new ImageIcon(curImage));
        viewport.invalidate();
        validate();
        KiwiUtils.busyOff(this);
    }

}
