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

import java.text.*;
import java.util.*;

/** This class represents a high-level date parser/formatter that is almost
 * fully format-string-compatible with the UNIX <b>strftime(3)</b> function.
 * Date formats are specified by formatting directives; these directives are
 * enumerated in the table below:
 * <p>
 * <center><table border=1 cellspacing=0 cellpadding=2>
 * <tr><th>Directive</th><th align=left>Meaning</th></tr>
 * <tr><td>%a</td>      <td>Abbreviated weekday name (3 characters).</td></tr>
 * <tr><td>%A</td>      <td>Full weekday name.</td></tr>
 * <tr><td>%b</td>      <td>Abbreviated month name (3 characters).</td></tr>
 * <tr><td>%B</td>      <td>Full month name.</td></tr>
 * <tr><td>%d</td>      <td>Day of month (01-31).</td></tr>
 * <tr><td>%e</td>      <td>Day of month (no leading zero).</td></tr>
 * <tr><td>%H</td>      <td>24-hour clock hour (00-23).</td></tr>
 * <tr><td>%I</td>      <td>12-hour clock hour (01-12).</td></tr>
 * <tr><td>%j</td>      <td>Day in year (001-366).</td></tr>
 * <tr><td>%m</td>      <td>Month in year (01-12).</td></tr>
 * <tr><td>%M</td>      <td>Minutes (00-59).</td></tr>
 * <tr><td>%N</td>      <td>Era name.</td></tr>
 * <tr><td>%p</td>      <td>AM/PM character.</td></tr>
 * <tr><td>%r</td>      <td>Equivalent to %I:%M:%S %p.</td></tr>
 * <tr><td>%R</td>      <td>Equivalent to %H:%M.</td></tr>
 * <tr><td>%s</td>      <td>Milliseconds (000-999).</td></tr>
 * <tr><td>%S</td>      <td>Seconds (00-59).</td></tr>
 * <tr><td>%t</td>      <td>Tab character.</td></tr>
 * <tr><td>%T</td>      <td>Equivalent to %H:%M%:S.</td></tr>
 * <tr><td>%u</td>      <td>Equivalent to %a.</td></tr>
 * <tr><td>%U</td>      <td>Week of year.</td></tr>
 * <tr><td>%y</td>      <td>Year (2 digits).</td></tr>
 * <tr><td>%Y</td> <td>Year (4 digits).</td></tr>
 * <tr><td>%Z</td> <td>Time zone name (4 characters).</td></tr>
 * <tr><td>%%</td> <td>The % character.</td></tr>
 * <tr><td>. : / - , ;</td> <td>These characters are interpreted literally.
 * </td></tr>
 * </table></center>
 *
 * @author Mark Lindner
 */

public class DateFormatter
{
  private SimpleDateFormat fmt;
  private String pattern;

  /** Construct a new <code>DateFormatter</code>. See the table of formatting
   * directives above.
   *
   * @param pattern The pattern to be used by this formatter.
   * @exception IllegalArgumentException If the pattern contains
   * unrecognized formatting directives.
   */

  public DateFormatter(String pattern) throws IllegalArgumentException
  {
    fmt = new SimpleDateFormat(constructPattern(pattern));
    fmt.setTimeZone(TimeZone.getDefault());
  }

  /** Parse a date, returning a <code>Date</code> object.
   *
   * @param text The text to parse.
   * @return A <code>Date</code> object corresponding to the parsed date.
   * @exception java.text.ParseException If a parsing error occurred.
   * @see #format
   */

  public Date parse(String text) throws ParseException
  {
    return(fmt.parse(text));
  }

  /** Format a <code>Calendar</code> object. The date is formatted according
   * to the pattern specified in this object's constructor.
   *
   * @param date The <code>Date</code> to format.
   * @return The date formatted as a string.
   * @see #parse
   */

  public String format(Calendar date)
  {
    return(format(date.getTime()));
  }

  /** Format the current date. The date is formatted according to the pattern
   * specified in this object's constructor.
   *
   * @return The date formatted as a string.
   * @see #parse
   */

  public String format()
  {
    return(format(new Date()));
  }
  
  /** Format a date from a <code>Date</code> object. The date is formatted
   * according to the pattern specified in this object's constructor.
   *
   * @param date The <code>Date</code> to format.
   * @return The date formatted as a string.
   * @see #parse
   */

  public String format(Date date)
  {
    return(fmt.format(date));
  }

  /* pattern translator */

  private String constructPattern(String text) throws IllegalArgumentException
  {
    StringBuilder sb = new StringBuilder();
    boolean escaped = false;

    for(int i = 0; i < text.length(); i++)
    {
      char c = text.charAt(i);

      if(!escaped)
      {
        switch(c)
        {
          case ':':
          case ';':
          case '/':
          case '.':
          case ',':
          case '-':
          case ' ':
          case '\t':
          case '(':
          case ')':
            sb.append(c);
            escaped = false;
            break;

          case '%':
            if(escaped)
            {
              sb.append(c);
              escaped = false;
            }
            else escaped = true;
            break;

          default:
            throw(new IllegalArgumentException("Unknown format character :"
                                               + c));
        }
      }
      else
      {
        switch(c)
        {
          case 'a':
            sb.append("EE");
            break;
          case 'A':
            sb.append("EEEE");
            break;
          case 'b':
            sb.append("MMM");
            break;
          case 'B':
            sb.append("MMMM");
            break;
          case 'd':
            sb.append("dd");
            break;
          case 'e':
            sb.append('d');
            break;
          case 'H':
            sb.append("HH");
            break;
          case 'I':
            sb.append("hh");
            break;
          case 'j':
            sb.append("DDD");
            break;
          case 'm':
            sb.append("MM");
            break;
          case 'M':
            sb.append("mm");
            break;
          case 'N':
            sb.append('G');
            break;
          case 'p':
            sb.append('a');
            break;
          case 'r':
            sb.append(constructPattern("%I:%M:%S %p"));
            break;
          case 'R':
            sb.append(constructPattern("%H:%M"));
            break;
          case 'S':
            sb.append("ss");
            break;
          case 't':
            sb.append('\t');
            break;
          case 'T':
            sb.append(constructPattern("%H:%M:%S"));
            break;
          case 'u':
            sb.append("EE");
            break;
          case 'U':
            sb.append("ww");
            break;
          case 's':
            sb.append("SSS");
            break;
          case 'y':
            sb.append("yy");
            break;
          case 'Y':
            sb.append("yyyy");
            break;
          case 'Z':
            sb.append("zzzz");
            break;
          default:
            throw(new IllegalArgumentException("Unknown escape sequence: %"
                                               + c));
        }
        escaped = false;
      }
    }
    return(sb.toString());
  }
  
}

/* end of source file */
