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

/** An abstract class providing base functionality for table and list
 * cell renderers. Concrete subclasses provide the actual rendering component,
 * which is then decorated with a border and background color based on the
 * selection and focus states of the cell.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public abstract class AbstractCellRenderer
  implements ListCellRenderer, TableCellRenderer
{
  private Border emptyBorder;
  private boolean inheritsFont = true;

  /** Construct a new <code>AbstractCellRenderer</code>.
   */
  
  protected AbstractCellRenderer()
  {
    emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
  }

  /** Get a table cell renderer component.
   */

  public final Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column)
  {
    JComponent c = getCellRenderer(table, value, row, column);

    if(inheritsFont)
      c.setFont(table.getFont());

    c.updateUI();
    
    if(isSelected)
    {
      c.setForeground(table.getSelectionForeground());
      c.setBackground(table.getSelectionBackground());
    }
    else
    {
      c.setForeground(table.getForeground());
      c.setBackground(table.getBackground());
    }

    c.setBorder(hasFocus
                ? UIManager.getBorder("Table.focusCellHighlightBorder")
                : emptyBorder);

    c.setOpaque(isSelected);
    
    return(c);
  }
  
  /** Get a list cell renderer component.
   */
  
  public final Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean hasFocus)
  {
    JComponent c = getCellRenderer(list, value, index, 0);

    if(inheritsFont)
      c.setFont(list.getFont());
    
    if(isSelected)
    {
      c.setBackground(list.getSelectionBackground());
      c.setForeground(list.getSelectionForeground());
    }
    else
    {
      c.setBackground(list.getBackground());
      c.setForeground(list.getForeground());
    }
    
    c.setBorder(hasFocus
                ? UIManager.getBorder("List.focusCellHighlightBorder")
                : emptyBorder);

    c.setOpaque(isSelected);

    return(c);
  }

  /**
   * Get the actual renderer component.
   *
   * @param component The <code>JList</code> or <code>JTable</code> for which
   * the renderer is being requested.
   * @param value The value to render.
   * @param row The row in the table or index in the list of the cell being
   * rendered.
   * @param column The column in the table of the cell being rendered, (or 0
   * if a list).
   * @return The renderer component.
   */

  protected abstract JComponent getCellRenderer(JComponent component,
                                                Object value, int row,
                                                int column);

  /**
   * Specify whether the renderer will inherit the font of the
   * <code>JList</code> or <code>JTable</code> in which it is used. By
   * default this property is true.
   *
   * @since Kiwi 2.4.1
   */

  public void setInheritsFont(boolean inheritsFont)
  {
    this.inheritsFont = inheritsFont;
  }

  /**
   * Determine whether the renderer will inherit the font of the
   * <code>JList</code> or <code>JTable</code> in which it is used. By
   * default this property is true.
   *
   * @since Kiwi 2.4.1
   */

  public boolean getInheritsFont()
  {
    return(inheritsFont);
  }
  
}

/* end of source file */
