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
import javax.swing.*;

/** An icon that is drawn as a portion of a larger image.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class SubImageIcon implements Icon
{
  private Image image;
  private int x0, y0, w, h;

  /** Construct a new <code>SubImageIcon</code>.
   *
   * @param image The parent image.
   * @param x The x-offset of the icon within the image.
   * @param y The y-offset of the icon within the image.
   * @param w The width of the icon.
   * @param h The height of the icon.
   */
  
  public SubImageIcon(Image image, int x, int y, int w, int h)
  {
    this.image = image;
    this.x0 = x;
    this.y0 = y;
    this.w = w;
    this.h = h;
  }

  /**
   */

  public int getIconHeight()
  {
    return(h);
  }

  /**
   */

  public int getIconWidth()
  {
    return(w);
  }

  /**
   */

  public void paintIcon(Component c, Graphics gc, int x, int y)
  {
    gc.drawImage(image, x, y, x + w, y + h, x0, y0, x0 + w, y0 + h, c);
  }
  
}

/* end of source file */
