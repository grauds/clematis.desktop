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

/** This class represents a user interface element, such as a texture, icon,
 * or audio clip.
 *
 * @author Mark Lindner
 */

public class UIElement
{
  private Object element;
  private String name;

  /** Construct a new <code>UIElement</code>.
   */
  
  public UIElement()
  {
    this(null, null);
  }

  /** Construct a new <code>UIElement</code> for the specified element and
   * name.
   *
   * @param element The user interface element object proper.
   * @param name A descriptive name for the element.
   */
  
  public UIElement(Object element, String name)
  {
    this.element = element;
    this.name = name;
  }

  /** Get the name of the element.
   *
   * @return The name of the element.
   */
  
  public String getName()
  {
    return(name);
  }

  /** Get the element object.
   *
   * @return The user interface element object proper.
   */
  
  public Object getObject()
  {
    return(element);
  }

  /** Get a string representation for this object.
   *
   * @return The name of the element.
   */

  public String toString()
  {
    return(name);
  }
  
}

/* end of source file */
