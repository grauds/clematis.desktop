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

import com.hyperrealm.kiwi.ui.*;

/** A property editor for editing textual properties.
 * 
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class TextValueEditor extends PropertyValueEditor
{
  protected KTextField field;
  
  /** Construct a new <code>TextValueEditor</code>.
   */
  
  public TextValueEditor()
  {
    field = new KTextField(15);
  }
  
  /**
   */
  
  protected void prepareEditor()
  {
    TextPropertyType type = (TextPropertyType)property.getType();
    field.setMaximumLength(type.getMaximumLength());

    field.setText((String)property.getValue());
  }

  /**
   */
  
  public void commitInput()
  {
    property.setValue(field.getText());
  }
  
  /**
   */

  public void addActionListener(ActionListener listener)
  {
    field.addActionListener(listener);
  }

  /**
   */

  public void removeActionListener(ActionListener listener)
  {
    field.removeActionListener(listener);
  }
  
  /**
   */
   
  public JComponent getEditorComponent()
  {
    return(field);
  }

  /**
   */
  
  public void startFocus()
  {
    field.requestFocus();
  }
  
}

/* end of source file */
