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
import javax.swing.border.*;
import javax.swing.table.*;

import com.hyperrealm.kiwi.text.ColorFormatter;

/** A cell renderer for color swatches.
 *
 * @author Mark Lindner
 */

public class ColorSwatchCellRenderer extends AbstractCellRenderer
  implements ListCellRenderer, TableCellRenderer
{
  private static ColorSwatch swatch = new ColorSwatch();
  private JLabel renderer;

  /** Construct a new <code>ColorSwatchCellRenderer</code>.
   */
  
  public ColorSwatchCellRenderer()
  {
    renderer = new JLabel();
    renderer.setIcon(swatch);
  }

  /**
   */

  protected JComponent getCellRenderer(JComponent component, Object value,
                                       int row, int column)
  {
    Color c = (Color)value;

    renderer.setFont(component.getFont());
    
    swatch.setColor(c);
    renderer.setText(ColorFormatter.nameForColor(c));

    return(renderer);
  }

}

/* end of source file */
