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

package com.hyperrealm.kiwi.ui.propeditor;

import java.awt.event.*;
import javax.swing.*;

import com.hyperrealm.kiwi.text.FormatConstants;
import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.util.IntegerHolder;

/** A property editor for editing properties which may take on one of a fixed
 * list of predefined values.
 * 
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class SelectionValueEditor extends PropertyValueEditor
{
  private JComboBox list;
  
  /** Construct a new <code>SelectionValueEditor</code>.
   */
  
  public SelectionValueEditor()
  {
    list = new JComboBox();
  }
  
  /**
   */
  
  protected void prepareEditor()
  {
    SelectionPropertyType type = (SelectionPropertyType)property.getType();

    list.removeAllItems();
    Object items[] = type.getItems();
    for(int i = 0; i < items.length; i++)
      list.addItem(items[i]);

    list.setSelectedItem(property.getValue());
  }

  /**
   */
  
  public void startFocus()
  {
    list.requestFocus();
    //list.showPopup();
  }

  /**
   */
  
  public void commitInput()
  {
    property.setValue(list.getSelectedItem());
  }

  /**
   */

  public void addActionListener(ActionListener listener)
  {
    list.addActionListener(listener);
  }

  /**
   */

  public void removeActionListener(ActionListener listener)
  {
    list.removeActionListener(listener);
  }
  
  /**
   */
   
  public JComponent getEditorComponent()
  {
    return(list);
  }
   
}

/* end of source file */
