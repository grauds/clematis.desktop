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

/** An abstract base class for mutable holder objects. This base class declares
 * a comparator method that may be used to determine the relative order of
 * two holder objects, based on the cardinality of their respective values.
 * It also introduces the notion of a <i>subtype</i>, which is an arbitrary
 * integer that may be used to store additional information about the value
 * stored in this holder. One possible use for the subtype field is to store
 * a format type (such as one of the data format constants defined in the
 * <code>com.hyperrealm.kiwi.text.FormatConstants</code> interface); such
 * information can be useful to tree and table cell renderers, for example.
 *
 * @see com.hyperrealm.kiwi.text.FormatConstants
 *
 * @author Mark Lindner
 */

public abstract class ValueHolder implements Comparable, Cloneable
{
  /** The subtype for the value stored by this holder. */
  protected int subtype;

  /** Construct a new <code>ValueHolder</code>. */
  
  protected ValueHolder()
  {
    this(0);
  }

  /** Construct a new <code>ValueHolder</code> with the specified subtype.
   *
   * @param subtype The subtype.
   */
  
  protected ValueHolder(int subtype)
  {
    this.subtype = subtype;
  }
  
  /** Compare the value in this <code>ValueHolder</code> to the value in
   * another <code>ValueHolder</code>. It is assumed that the two values
   * are of the same type (hence that the holders are also of the same type).
   *
   * @param other The <code>ValueHolder</code> to compare against.
   * @return <code>-1</code> if this object is "less than" the other object;
   * <code>1</code> if this object is "greater than" the other object, and
   * <code>0</code> if the objects are "equal."
   */
  
  public abstract int compareTo(Object other);

  /** Set the subtype for the value stored by this holder.
   *
   * @param subtype The new subtype.
   */
  
  public void setSubtype(int subtype)
  {
    this.subtype = subtype;
  }

  /** Get the subtype for the value stored by this holder.
   *
   * @return The current subtype. The default subtype is 0.
   */
  
  public int getSubtype()
  {
    return(subtype);
  }

  /** Clone this object.
   */
  
  public abstract Object clone() throws CloneNotSupportedException;
  
}

/* end of source file */
