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

/** A customized header table cell renderer that left-justifies the header
 * text and may optionally render an icon.
 *
 * @author Mark Lindner
 */

public class HeaderCellRenderer extends JPanel implements TableCellRenderer
{
  private static final Border headerBorder
    = new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
                         new EmptyBorder(0, 3, 0, 3));
  private JLabel l_text, l_icon;
   
  /** Construct a new <code>HeaderCellRenderer</code>.
   */
   
  public HeaderCellRenderer()
  {
    setOpaque(false);
    setBorder(headerBorder);
    setLayout(new BorderLayout(0, 0));

    l_text = new KLabel();
    l_text.setVerticalTextPosition(SwingConstants.CENTER);
    l_text.setHorizontalTextPosition(SwingConstants.LEFT);
    add("Center", l_text);

    l_icon = new KLabel();
    add("East", l_icon);
  }

  /** Set the icon for the renderer. The icon is rendered against the right
   * edge of the renderer.
   *
   * @param icon The icon.
   */

  public void setIcon(Icon icon)
  {
    l_icon.setIcon(icon);
  }

  /** Set the alignment for the renderer's text; should be one of
   * <code>SwingConstants.LEFT</code>, <code>SwingConstants.CENTER</code>,
   * <code>SwingConstants.RIGHT</code>.
   *
   * @since Kiwi 2.4
   */

  public void setTextAlignment(int alignment)
  {
    l_text.setHorizontalAlignment(alignment);
  }
  
  /** Get the cell renderer component.
   */
   
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column)
  {
    l_text.setText(value.toString());
    
    return(this);
  }

}

/* end of source file */
