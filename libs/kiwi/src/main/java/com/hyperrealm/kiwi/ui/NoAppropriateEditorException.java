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

/** This exception is thrown by MDI-related classes such as
 * <code>WorkspaceManager</code> when an appropriate editor for a given class
 * or object cannot be created.
 *
 * @see com.hyperrealm.kiwi.ui.WorkspaceEditorFactory
 *
 * @author Mark Lindner
 */

public class NoAppropriateEditorException extends Exception
{
  private Class clazz;

  /** Construct a new <code>NoAppropriateEditorException</code>.
   *
   * @param msg The message.
   * @param clazz The class object associated with the requested editor.
   */

  public NoAppropriateEditorException(String msg, Class clazz)
  {
    super(msg);
    this.clazz = clazz;
  }

  /** Get the class object associated with the requested editor. */

  public Class getObjectType()
  {
    return(clazz);
  }

}

/* end of source file */
