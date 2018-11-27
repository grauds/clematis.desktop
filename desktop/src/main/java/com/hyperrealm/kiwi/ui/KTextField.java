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

/** A subclass of <code>DataField</code> for the input and display of text.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1
 */

public class KTextField extends DataField<String>
{

  /** Construct a new <code>KTextField</code>.
   */
  
  public KTextField()
  {
    super();
  }

  /** Construct a new <code>KTextField</code> of the specified width.
   *
   * @param width The width of the field.
   */
  
  public KTextField(int width)
  {
    super(width);
  }

  /** Set the object being edited. Equivalent to <code>setText()</code>.
   */

  public void setObject(String text)
  {
    setText(text);
  }

  /** Get the object being edited. Equivalent to <code>getText()</code>.
   */
  
  public String getObject()
  {
    return(getText());
  }

}

/* end of source file */
