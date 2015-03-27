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

import com.hyperrealm.kiwi.util.KiwiUtils;

/** This class represents a <i>splash screen</i>: an untitled, frameless window
 * that briefly appears on the desktop, typically while an application or
 * installer program is launching. A <code>SplashScreen</code> contains an
 * image and, optionally, a one-line textual caption. It is drawn with a
 * 1-pixel wide black border and appears at the center of the screen when
 * shown. The <code>SplashScreen</code> appears above all other windows on the
 * desktop.
 * <p>
 * As with all <code>Component</code>s, the <code>setForeground()</code> and
 * <code>setBackground()</code> methods may be called to change the appearance
 * of the splash screen.
 *
 * <p><center>
 * <img src="snapshot/SplashScreen.gif"><br>
 * <i>An example SplashScreen.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class SplashScreen extends Window
{
  private int delay = 5;
  private Image image;
  private String caption;

  /** Construct a new <code>SplashScreen</code>.
   *
   * @param image The image to display in the splash screen.
   * @param caption A short text caption to display below the image (may be
   * <code>null</code>).
   */

  public SplashScreen(Image image, String caption)
  {
    super(KiwiUtils.getPhantomFrame());

    this.image = image;
    this.caption = caption;

    setForeground(Color.black);
  }

  /** Set the display duration.
   *
   * @param seconds The number of seconds that the splash screen should remain
   * onscreen before it is automatically hidden. If 0, it will remain onscreen
   * until explicitly hidden via a call to <code>setVisible()</code> or
   * <code>dispose()</code>.
   *
   * @exception IllegalArgumentException If <code>seconds</code> is
   * less than 0.
   */

  public void setDelay(int seconds) throws IllegalArgumentException
  {
    if(seconds < 0)
      throw(new IllegalArgumentException("Delay must be >= 0 seconds."));

    delay = seconds;
  }

  /** Paint the splash screen. */

  public void paint(Graphics gc)
  {
    Dimension size = getSize();

    FontMetrics fm = gc.getFontMetrics();

    gc.setColor(Color.black);
    gc.drawRect(0, 0, size.width - 1, size.height - 1);
    gc.drawImage(image, 1, 1, null);

    if(caption != null)
    {
      int y = image.getHeight(null) + 2 + fm.getAscent();
      int x = (size.width - fm.stringWidth(caption)) / 2;

      gc.setColor(getForeground());
      gc.drawString(caption, x, y);
    }
  }

  /** Display or hide the splash screen. The splash screen is displayed on the
   * desktop, centered on the screen. Although this method returns
   * immediately, the splash screen remains on the desktop for the duration
   * of the time delay, or indefinitely if the delay was set to 0.
   */

  public void setVisible(boolean flag)
  {
    if(flag)
    {
      pack();
      KiwiUtils.centerWindow(this);
    }
    super.setVisible(flag);

    if(flag && (delay > 0))
    {
      Thread thread = new Thread(new Runnable()
        {
          public void run()
          {
            try
            {
              Thread.currentThread().sleep(delay * 1000);
            }
            catch(InterruptedException ex) {}
            dispose();
          }
        });

      thread.start();
    }
  }

  /** Get the splash screen's preferred size.
   *
   * @return The preferred size of the component.
   */

  public Dimension getPreferredSize()
  {
    FontMetrics fm = getGraphics().getFontMetrics();

    Dimension d = new Dimension(image.getWidth(null) + 2,
                                image.getHeight(null) + 2);
    if(caption != null) d.height += fm.getHeight() + 2;

    return(d);
  }

}

/* end of source file */
