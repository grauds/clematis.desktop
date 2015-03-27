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

/** A special value editor which does not actually allow editing.
 * 
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public final class FixedValueEditor extends PropertyValueEditor
{
  private JLabel label;
  
  /** Construct a new <code>FixedValueEditor</code>.
   */
  
  public FixedValueEditor()
  {
    label = new JLabel();
  }
  
  /**
   */
  
  protected void prepareEditor()
  {
    Object value = property.getValue();
    
    label.setText(value == null ? null : value.toString());
  }
  
  /**
   */
   
  public JComponent getEditorComponent()
  {
    return(label);
  }
  
  /**
   */
  
  public void addActionListener(ActionListener listener)
  {
  }
  
  /**
   */
  
  public void removeActionListener(ActionListener listener)
  {
  }

  /**
   */

  public void startFocus()
  {
  }
  
}

/* end of source file */
