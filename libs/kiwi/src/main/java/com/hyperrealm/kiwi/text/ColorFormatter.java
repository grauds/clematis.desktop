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

import java.awt.Color;

/** This class provides methods for converting colors to and from string
 * representations. Known colors (those defined as constants in the
 * <code>Color</code> class) are converted directly to or from symbolic names
 * such as "green" or "magenta". Other colors are converted to or from RGB
 * specifications in the format #RRGGBB - a '#' followed by 6 hexadecimal
 * digits.
 *
 * @see java.awt.Color
 *
 * @author Mark Lindner
 */

public class ColorFormatter
{
  private static String colorNames[] = { "black", "blue", "cyan", "gray",
                                         "green", "magenta", "orange", "pink",
                                         "red", "white", "yellow" };
  /** A list of basic colors. */
  public static Color basicColors[] = { Color.black, Color.blue, Color.cyan,
                                        Color.gray, Color.green, Color.magenta,
                                        Color.orange, Color.pink, Color.red,
                                        Color.white, Color.yellow };

  private ColorFormatter() {}

  /** Get a name for a color. Returns a name for the color, if the color is one
   * of the colors predefined in the <code>Color</code> class: black, blue,
   * cyan, gray, green, magenta, orange, pink, red, white, or yellow.
   *
   * @param c The color.
   * @return The name of the color, or <code>null</code> if <code>c</code> is
   * not one of the colors listed above.
   *
   * @see #nameForColor
   */
  
  public static String nameForColor(Color c)
  {
    for(int i = 0; i < basicColors.length; i++)
      if(c.equals(basicColors[i]))
        return(colorNames[i]);

    return(null);
  }

  /** Get a color for a name. Returns a color for a color name, if the name
   * identifies one of the colors predefined in the <code>Color</code> class.
   *
   * @param name The name of the color.
   * @return The <code>Color</code> object for the given name, or
   * <code>null</code> if <code>name</code> does not identify one of the
   * colors listed above.
   *
   * @see #colorForName
   */
  
  public static Color colorForName(String name)
  {
    for(int i = 0; i < basicColors.length; i++)
      if(name.equalsIgnoreCase(colorNames[i]))
        return(basicColors[i]);

    return(null);
  }

  /** Format a color as a string. Returns a string representation of the given
   * color as either a symbolic name (if the color is one of the colors
   * predefined in the <code>Color</code> class), or a hex representation of
   * the color in the format <tt>#RRGGBB</tt>.
   *
   * @param color The <code>Color</code> to parse.
   * @return A string representation of the color.
   *
   * @see #parse
   */
  
  public static String format(Color color)
  {
    String s = nameForColor(color);
    if(s != null)
      return(s);

    // otherwise format it as #RRGGBB

    StringBuilder sb = new StringBuilder();
    sb.append('#');

    s = Integer.toHexString(color.getRGB() & 0x1FFFFFF);
    sb.append(s.substring(1));

    return(sb.toString());
  }

  /** Parse a color representation, returning an appropriate <code>Color</code>
   * object.
   *
   * @param name The name of the color; one of the strings <i>black, blue,
   * cyan, gray, green, magenta, orange, pink, red, white, yellow</i>, or an
   * RGB color specification of the form <tt>#RRGGBB</tt>.
   * @return An appropriate <code>Color</code> object.
   * @exception com.hyperrealm.kiwi.text.ParsingException If <code>name</code> is an
   * invalid color representation.
   *
   * @see #format
   */

  public static Color parse(String name) throws ParsingException
  {
    Color r;

    if(name.charAt(0) == '#')
    {
      // process #RRGGBB specification
      
      try
      {
        Integer rgb = Integer.valueOf(name.substring(1), 16);
        return(new Color(rgb.intValue()));
      }
      catch(NumberFormatException e)
      {
        throw(new ParsingException("Invalid RGB color syntax: " + name));
      }
    }
    else
    {
      r = colorForName(name);
      if(r == null)
        throw(new ParsingException("Unknown color name: " + name));
      else
        return(r);
    }
  }
  
}

/* end of source file */
