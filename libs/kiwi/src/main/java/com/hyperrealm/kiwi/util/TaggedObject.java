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

package com.hyperrealm.kiwi.util;

/** An object-id or object-tag pair. Sometimes it is useful to assign a tag
 * or numeric ID to an object for purposes of identification. Most commonly
 * the identifier is a unique integer, but in some circumstances it is more
 * appropriate to use another object as an identifier. This class allows
 * an object to be associated with either an integer or an arbitrary object.
 *
 * @author Mark Lindner
 */

public class TaggedObject<T, O>
{
  private O obj;
  private T tag = null;
  private int id = -1;

  /** Construct a new <code>TaggedObject</code> for the given user object
   * and identifier object.
   *
   * @param obj The user object.
   * @param tag The identifier object.
   */
  
  public TaggedObject(O obj, T tag)
  {
    this.obj = obj;
    this.tag = tag;
  }

  /** Construct a new <code>TaggedObject</code> for the given user object
   * and numerical ID.
   *
   * @param obj The user object.
   * @param id The numerical ID.
   */
  
  public TaggedObject(O obj, int id)
  {
    this.obj = obj;
    this.id = id;
  }

  /** Get the user object.
   *
   * @return The user object.
   */
  
  public final O getObject()
  {
    return(obj);
  }

  /** Get the numerical ID.
   *
   * @return The numerical ID, or <code>-1</code> if there is no numerical ID
   * for this object.
   */
  
  public final int getID()
  {
    return(id);
  }

  /** Get the identifier object.
   *
   * @return The identifier object, or <code>null</code> if there is no
   * identifier object for this object.
   */
  
  public final T getTag()
  {
    return(tag);
  }

  /** Get a string representation of the tagged object.
   *
   * @since Kiwi 1.3
   */

  public String toString()
  {
    return(obj.toString());
  }
  
}

/* end of source file */
