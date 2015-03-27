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

/** General-purpose mutator exception. This exception is thrown by a problem
 * domain object when an invalid value is passed to a mutator method.
 *
 * @author Mark Lindner
 */

public class MutatorException extends Exception
{
  /** Construct a new <code>MutatorException</code>.
   */
  
  public MutatorException()
  {
    super();
  }

  /** Construct a new <code>MutatorException</code> with the given message.
   *
   * @param message The message for the exception.
   */
  
  public MutatorException(String message)
  {
    super(message);
  }
}

/* end of source file */
