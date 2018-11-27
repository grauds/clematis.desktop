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

/** An integer counter object.
 *
 * @author Mark Lindner
 */

public class Counter extends IntegerHolder
{

  /** Construct a new <code>Counter</code> with a specified initial value.
   *
   * @param value The initial value.
   */
  
  public Counter(int value)
  {
    super(value);
  }

  /** Construct a new <code>Counter</code> with an initial value of 0.
   */
  
  public Counter()
  {
    super();
  }

  /** Increment the counter by 1.
   */
  
  public synchronized final int increment()
  {
    return(++value);
  }

  /** Decrement the counter by 1.
   */
  
  public synchronized final int decrement()
  {
    return(--value);
  }
  
}

/* end of source file */
