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

/** A mutable holder for a <code>float</code> value.
 *
 * @author Mark Lindner
 */

public class FloatHolder extends ValueHolder
{
  /** The current value. */
  protected float value;

  /** Construct a new <code>FloatHolder</code> with an initial value of
   * <code>0.0f</code> and default subtype of <code>0</code>.
   */
  
  public FloatHolder()
  {
    this(0.0f, 0);
  }

  /** Construct a new <code>FloatHolder</code> with a specified initial
   * value and default subtype of <code>0</code>.
   *
   * @param value The initial value.
   */
  
  public FloatHolder(float value)
  {
    this(value, 0);
  }

  /** Construct a new <code>FloatHolder</code> with a specified initial
   * value and subtype.
   *
   * @param value The initial value.
   * @param subtype The subtype for this value.
   */
  
  public FloatHolder(float value, int subtype)
  {
    super(subtype);
    
    this.value = value;
  }
  
  /** Set the <code>FloatHolder</code>'s value.
   *
   * @param value The new value.
   */
  
  public final void setValue(float value)
  {
    this.value = value;
  }

  /** Get the <code>FloatHolder</code>'s value.
   *
   * @return The current value.
   */
  
  public final float getValue()
  {
    return(value);
  }

  /** Get a string representation for this object. */
  
  public String toString()
  {
    return(String.valueOf(value));
  }

  /** Compare this holder object to another. */
  
  public int compareTo(Object other)
  {
    float v = ((FloatHolder)other).getValue();

    return((value < v) ? -1 : ((value > v) ? 1 : 0));
  }

  /** Clone this object. */

  public Object clone() throws CloneNotSupportedException
  {
    return(new FloatHolder(value, subtype));
  }
  
}

/* end of source file */
