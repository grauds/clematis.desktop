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

package com.hyperrealm.kiwi.text;

/** General-purpose parsing exception.
 *
 * @author Mark Lindner
 */

public class ParsingException extends Exception
{
  private int line = -1;
  private String message = "";

  /** Construct a new <code>ParsingException</code>.
   *
   * @param message The exception message.
   */

  public ParsingException(String message)
  {
    this(message, -1);
  }

  /** Construct a new <code>ParsingException</code>.
   *
   * @param message The exception message.
   * @param line The line number in the input where the exception occurred.
   */

  public ParsingException(String message, int line)
  {
    super(message);
    this.message = message;
    this.line = line;
  }

  /** Get the line number of this exception. If no line number is available,
   * this method returns -1.
   */

  public int getLine()
  {
    return(line);
  }

  /** Get the message of this exception. */

  public String getMessage()
  {
    return(message);
  }

  /** Convert the parsing exception to a string that contains the message and
   * line number.
   */

  public String toString()
  {
    String msg = getMessage();
    if(line >= 0)
      msg += (" on line " + line);
    
    return(msg);
  }

}

/* end of source file */
