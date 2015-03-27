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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.util.*;

/** A general-purpose image viewing component. It displays an image in a scroll
 * pane and provides zooming buttons.
 *
 * <p><center>
 * <img src="snapshot/ImageView.gif"><br>
 * <i>An example ImageView.</i>
 * </center>
 *
 * @see java.awt.Image
 *
 * @author Mark Lindner
 */

public class ImageView extends KPanel
{
  private KButton b_zoomin, b_zoomout, b_zoomreset;
  private KLabel viewport;
  private int origw, origh;
  private float scale = 1;
  private Image image, curImage = null;
  private KScrollPane scroll;
  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
  private Cursor defaultCursor;

  /** Construct a new <code>ImageView</code>.
   *
   * @param comment A comment to display above the image.
   * @param image The image to display.
   */

  public ImageView(String comment, Image image)
  {
    origw = image.getWidth(this);
    origh = image.getHeight(this);
    this.image = image;

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    ActionListener actionListener = new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Object o = evt.getSource();
        
          if(o == b_zoomin)
            scaleImage(2.0f);
          else if(o == b_zoomout)
            scaleImage(0.5f);
          else if(o == b_zoomreset)
            scaleImage(1.0f);
        }
      };
    
    defaultCursor = getCursor();

    ResourceManager rm = KiwiUtils.getResourceManager();

    GridBagConstraints gbc = new GridBagConstraints();

    setBorder(KiwiUtils.defaultBorder);
    setLayout(new BorderLayout(5, 5));

    KPanel p1 = new KPanel();
    p1.setLayout(new BorderLayout(5, 5));

    KLabel label = new KLabel(comment);
    label.setFont(new Font("Serif", Font.BOLD, 14));

    p1.add("Center", label);

    JToolBar toolBar = new JToolBar();
    toolBar.setOpaque(false);
    toolBar.setFloatable(false);

    b_zoomin = new KButton(rm.getIcon("zoom_in.png"));
    b_zoomin.setToolTipText(loc.getMessage("kiwi.tooltip.zoom_in"));
    b_zoomin.addActionListener(actionListener);
    toolBar.add(b_zoomin);

    b_zoomout = new KButton(rm.getIcon("zoom_out.png"));
    b_zoomout.setToolTipText(loc.getMessage("kiwi.tooltip.zoom_out"));
    b_zoomout.addActionListener(actionListener);
    toolBar.add(b_zoomout);

    b_zoomreset = new KButton(rm.getIcon("zoom.png"));
    b_zoomreset.setToolTipText(loc.getMessage("kiwi.tooltip.actual_size"));
    b_zoomreset.addActionListener(actionListener);
    toolBar.add(b_zoomreset);

    p1.add("East", toolBar);
    
    add("North", p1);

    viewport = new KLabel();
    viewport.setIcon(new ImageIcon(image));
    viewport.setVerticalAlignment(SwingConstants.CENTER);
    viewport.setHorizontalAlignment(SwingConstants.CENTER);

    scroll = new KScrollPane(viewport);
    scroll.setBackground(Color.white);
    add("Center", scroll);
  }

  /** Scale the image by a given scale factor.
   *
   * @param scaleFactor The scale factor.
   */

  public void scaleImage(float scaleFactor)
  {
    if(scaleFactor != 1)
      scale *= scaleFactor;
    else
      scale = 1;

    KiwiUtils.busyOn(this);
    if(curImage != null) curImage.flush();
    curImage = image.getScaledInstance((int)((float)origw * scale),
                                       (int)((float)origh * scale),
                                       Image.SCALE_SMOOTH);
    viewport.setIcon(new ImageIcon(curImage));
    viewport.invalidate();
    validate();
    KiwiUtils.busyOff(this);
  }

}

/* end of source file */
