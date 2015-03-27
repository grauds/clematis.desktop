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

import java.util.*;
import java.io.*;

/** A class of miscellaneous string utilities. All of the methods in this class
 * are static.
 *
 * @author Mark Lindner
 */

public final class StringUtils
{

  private StringUtils() { }

  /** Left justify a string, wrapping words as necessary.
   *
   * @param text The text to justify.
   * @param cols The number of columns to use.
   */

  public final static String justify(String text, int cols)
  {
    StringTokenizer st = new StringTokenizer(text, "\t \f\n", true);
    StringBuilder buf = new StringBuilder(500);
    int ww, lw = 0;
    boolean sawLF = false, first = true;

    while(st.hasMoreTokens())
    {
      String tok = st.nextToken();

      if(tok.equals("\n"))
      {
        if(cols == 0) continue;

        if(sawLF)
        {
          buf.append('\n');
          buf.append('\n');
          lw = 0;
          first = true;
          sawLF = false;
        }
        else sawLF = true;
      }

      else if((tok.equals(" ")) || (tok.equals("\t")) || (tok.equals("\f")))
      {
        sawLF = false;
        continue;
      }
      else
      {
        sawLF = false;
        ww = tok.length();
        if(!first) ww++;
        if((lw + ww) > cols)
        {
          buf.append('\n');
          first = true;
          lw = 0;
        }

        if(!first) buf.append(' ');
        buf.append(tok);
        lw += ww;
        first = false;
      }
    }

    String r = buf.toString();
    buf = null;
    return(r);
  }

  /** Determine if a string consists solely of alphanumeric characters.
   *
   * @param text The string to test.
   *
   * @return <code>true</code> if the string contains only alphanumeric
   * characters, and <code>false</code> otherwise.
   */

  public final static boolean isAlphaNumeric(String text)
  {
    for(int i = 0; i < text.length(); i++)
      if(!Character.isLetterOrDigit(text.charAt(i)))
        return(false);

    return(true);
  }

  /** Split a string into a series of tokens based on the given delimiter.
   *
   * @param s The string to split.
   * @param delimiter A string consisting of characters that should be treated
   * as delimiters.
   * @return An array of tokens.
   * @see #join
   */

  public final static String[] split(String s, String delimiter)
  {
    ArrayList<String> v = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(s, delimiter);
    while(st.hasMoreTokens())
      v.add(st.nextToken());

    String array[] = new String[v.size()];
    return(v.toArray(array));
  }

  /** Join an array of strings into a single string of tokens using the given
   * delimiter.
   *
   * @param array The tokens to join.
   * @param delimiter A string to insert between adjacent tokens.
   * @return The resulting string.
   * @see #split
   */
    
  public final static String join(String array[], String delimiter)
  {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < array.length; i++)
    {
      if(i > 0) sb.append(delimiter);
      sb.append(array[i]);
    }

    return(sb.toString());
  }

  /** Break a string into whitespace delimited words, treating substrings
   * enclosed within pairs of quotes as single words.
   *
   * @param text The text to parse.
   * @param quoteChar The quote character, typically the single- or
   * double-quote.
   * @return An array of the parsed words.
   *
   * @since Kiwi 2.0
   */

  public final static String[] wordBreak(String text, int quoteChar)
  {
    if(text == null)
      return(null);

    ArrayList<String> vec = new ArrayList<String>();
    
    try
    {
      StreamTokenizer st = new StreamTokenizer(new StringReader(text));
      st.ordinaryChars('!', '~');
      st.wordChars('!', '~');
      st.quoteChar(quoteChar);
      
      while(st.nextToken() != st.TT_EOF)
      {
        if((st.ttype == st.TT_WORD) || (st.ttype == quoteChar))
          vec.add(st.sval);
      }
    }
    catch(IOException ex)
    {
    }

    String words[] = new String[vec.size()];
    return(vec.toArray(words));
  }

  /** Get the name of a class; this method returns the last component of the
   * fully qualified name of the given class. For example, 'String' is returned
   * for the class <b>java.lang.String</b>.
   *
   * @param clazz The class.
   * @return The name of the class.
   */
  
  public final static String getClassName(Class clazz)
  {
    String s = clazz.getName();

    int idx = s.lastIndexOf('.');

    return(idx < 0 ? s : s.substring(++idx));
  }

  /** Uppercase the first character in a string.
   *
   * @param string The string.
   * @return The new string.
   * @since Kiwi 2.1
   */

  public final static String uppercaseFirst(String string)
  {
    if((string == null) || (string.length() < 1))
      return(string);

    return(Character.toUpperCase(string.charAt(0)) +
           string.substring(1));
  }

  /** Parse a text string containing a comma-separated list of integer values
   * into an integer array. If the array is longer than the number of elements
   * parsed, the excess elements are filled with the default value. If the
   * array is shorter than the number of elements parsed, the excess elements
   * are discarded. If any token does not parse to a valid integer value, the
   * corresponding element in the array is filled with the default value.
   *
   * @param text The text to parse.
   * @param array The array in which to place the parsed values.
   * @param defaultValue The default value for the array elements.
   * @return The array.
   * @since Kiwi 2.4
   */

  public static int[] parseIntArray(String text, int array[],
                                    int defaultValue)
  {
    int last = 0;
    if(text != null)
    {
      StringTokenizer st = new StringTokenizer(text, ",");
      while(st.hasMoreTokens() && (last < array.length))
      {
        int val = defaultValue;

        try
        {
          val = Integer.parseInt(st.nextToken());
        }
        catch(NumberFormatException ex) { }
        
        array[last++] = val;
      }
    }
    
    for(int i = last; i < array.length; i++)
      array[i] = defaultValue;
    
    return(array);
  }
  
}

/* end of source file */
