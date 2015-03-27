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

/** A default implementation of the <code>PropertyEditorFactory</code>
 * interface, providing construction of all property editors defined in this
 * package.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class DefaultPropertyEditorFactory implements PropertyEditorFactory
{
  private static DefaultPropertyEditorFactory instance
    = new DefaultPropertyEditorFactory();

  /** Get a reference to the singleton instance.
   */

  public static PropertyEditorFactory getInstance()
  {
    return(instance);
  }

  /** Construct a new <code>DefaultPropertyEditorFactory</code>.
   */
  
  protected DefaultPropertyEditorFactory()
  {
  }

  /**
   */

  public PropertyValueEditor createEditor(PropertyType type)
  {
    PropertyValueEditor editor = null;

    if(type instanceof TextPropertyType)
      editor = new TextValueEditor();

    else if(type instanceof NumericPropertyType)
      editor = new NumericValueEditor();

    else if(type instanceof SelectionPropertyType)
      editor = new SelectionValueEditor();

    else
      editor = new FixedValueEditor();

    return(editor);
  }
  
}

/* end of source file */
