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

/** An implementation of <code>UIElementViewer</code> for previewing textures.
 *
 * @author Mark Lindner
 */

public class TextureViewer extends KPanel implements UIElementViewer
{
  private static final Dimension preferredSize = new Dimension(150, 150);
  
  /** Construct a new <code>TextureViewer</code>.
   */
  
  public TextureViewer()
  {
    setOpaque(true);
    setTexture(null);
    setPreferredSize(preferredSize);
  }

  /** Get a reference to the viewer component.
   *
   * @return The viewer component.
   */
  
  public JComponent getViewerComponent()
  {
    return(this);
  }

  /** Show the specified element.
   *
   * @param element An object, assumed to be an instance of <code>Image</code>,
   * to display.
   */
  
  public void showElement(UIElement element)
  {
    Object obj = element.getObject();
    
    if(obj instanceof Image)
      setTexture((Image)obj);
  }

}

/* end of source file */
