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
import javax.swing.JComponent;

/** A base class for property value editors.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public abstract class PropertyValueEditor
{
  /** The property currently being edited by this editor. */
  protected Property property;

  /** Construct a new <code>PropertyValueEditor</code>. */
  
  protected PropertyValueEditor()
  {
  }
  
  /** Get the actual editor component.
   *
   * @return The editor component.
   */
  
  public abstract JComponent getEditorComponent();

  /** Set the property whose value will be edited by this editor.
   *
   * @param property The property.
   */
  
  public void setProperty(Property property)
  {
    this.property = property;

    prepareEditor();
  }

  /** Get the property whose value is being edited by this editor.
   */

  public Property getProperty()
  {
    return(property);
  }

  /** Prepare the editor for editing. This method is called after the property
   * has been set with <code>setProperty()</code>, to allow the editor to
   * populate itself with the current value of the property. The default
   * implementation does nothing.
   */

  protected void prepareEditor()
  {
  }

  /** Assign keyboard focus to the editor.
   */

  public abstract void startFocus();

  /** Validate the input currently entered in the editor. The default
   * implementation returns <b>true</b>.
   *
   * @return <b>true</b> if the input is valid, <b>false</b> otherwise.
   */
  
  public boolean validateInput()
  {
    return(true);
  }

  /** Commit the input currently entered in this editor to the property.
   */

  public void commitInput()
  {
  }

  /** Add an <code>ActionListener</code> to this editor's list of listeners.
   *
   * @param listener The listener to add.
   */
  
  public abstract void addActionListener(ActionListener listener);

  /** Remove an <code>ActionListener</code> from this editor's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */
  
  public abstract void removeActionListener(ActionListener listener);
  
}

/* end of source file */
