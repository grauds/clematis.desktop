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

import com.hyperrealm.kiwi.ui.model.*;

/** An implementation of <code>ListCellRenderer</code> for use with
 * <code>JList</code>s that are connected to a <code>KListModel</code> via
 * a <code>KListModelListAdapter</code>. This cell renderer consults the list
 * model for a cell's rendering information, such as its label and icon.
 *
 * @see javax.swing.JList
 * @see com.hyperrealm.kiwi.ui.model.KListModel
 * @see com.hyperrealm.kiwi.ui.model.KListModelAdapter
 *
 * @author Mark Lindner
 */

public class KListModelListCellRenderer extends AbstractCellRenderer
{
  private EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
  private KListModel model = null;
  private JLabel label;

  /** Construct a new <code>ModelListCellRenderer</code>.
   */

  public KListModelListCellRenderer()
  {
    label = new JLabel();
  }

  /** Construct a new <code>ModelListCellRenderer</code>.
   *
   * @param model The list model that will be used with this renderer.
   */
  
  public KListModelListCellRenderer(KListModel model)
  {
    this();
    setModel(model);
  }

  /** Set the data model for this renderer.
   *
   * @param model The model.
   */

  public void setModel(KListModel model)
  {
    this.model = model;
  }

  /*
   */

  public JComponent getCellRenderer(JComponent component, Object value,
                                    int row, int column)
  {
    if((model == null) || (value == null))
    {
      label.setIcon(null);
      label.setText((value == null) ? null : value.toString());
    }
    else
    {
      label.setIcon(model.getIcon(value));
      label.setText(model.getLabel(value));
    }

    return(label);
  }

}

/* end of source file */
