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

package com.hyperrealm.kiwi.ui.model;

/** An exception that is thrown when an attempt is made to modify an
 * immutable data model.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ImmutableModelException extends RuntimeException
{
  
  /** Construct a new <code>ImmutableModelException</code>.
   */
  
  ImmutableModelException()
  {
    super();
  }

  /** Construct a new <code>ImmutableModelException</code> with the given
   * message.
   *
   * @param message The message.
   */
  
  ImmutableModelException(String message)
  {
    super(message);
  }
  
}

/* end of source file */
