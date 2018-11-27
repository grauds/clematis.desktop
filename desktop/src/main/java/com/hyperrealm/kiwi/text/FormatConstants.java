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

/** This interface defines common formatting constants.
 *
 * @author Mark Lindner
 */

public interface FormatConstants
{
  /** Custom format. */
  public static final int CUSTOM_FORMAT = 0x00;
  /** Currency format. */
  public static final int CURRENCY_FORMAT = 0x10;
  /** Percentage format. */
  public static final int PERCENTAGE_FORMAT = 0x20;
  /** Integer format. */
  public static final int INTEGER_FORMAT = 0x30;
  /** Decimal format. */
  public static final int DECIMAL_FORMAT = 0x40;
  /** Date and time format. */
  public static final int DATE_TIME_FORMAT = 0x50;
  /** Date format. */
  public static final int DATE_FORMAT = 0x60;
  /** Time format. */
  public static final int TIME_FORMAT = 0x70;

  /** Format type mask. */
  public static final int TYPE_MASK = 0xF0;
  
  /** Short (abbreviated) format. */
  public static final int SHORT = 0x00;
  /** Medium (common) format. */
  public static final int MEDIUM = 0x01;
  /** Long (extended) format. */
  public static final int LONG = 0x02;

  /** Format length mask. */
  public static final int LENGTH_MASK = 0x0F;
}

/* end of source file */
