package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

/**
 * String utilities
 */
public class StringUtils
{
    public static final int RECURSION_THRESHOLD = 10;

    public static interface KeyFinder
    {
        String lookupString(String key);
    }

    public static String lookupKeysInString(String str, int recurselvl, KeyFinder finder)
    {
        if (recurselvl > RECURSION_THRESHOLD)
        {
            throw new RuntimeException("Recursion Threshold reached");
        }

        // this is where all those years of c/c++ pay off in java
        //  boolean foundkey = false;

        char[] sb = str.toCharArray();
        int len = sb.length;

        // now go through the string looking for "{%"
        StringBuffer newsb = null;

        int lastKeyEnd = 0;

        for (int i = 0; i < len; i++)
        {
            char c = sb[i];
            if ((c == '{') && (i + 2 < len) && (sb[i + 1] == '%'))
            {
                // we got a potential key

                int endkey = -1;
                StringBuffer key = new StringBuffer();
                for (int j = i + 2; j + 1 < len && endkey < 0; j++)
                {
                    if (sb[j] == '%' && sb[j + 1] == '}')
                    {
                        endkey = j - 1;
                    }
                    else
                    {
                        key.append(sb[j]);
                    }
                }
                if (endkey > 0)
                {
                    String val = finder.lookupString(key.toString());
                    String s = lookupKeysInString(val, recurselvl + 1, finder);
                    if (s != null)
                    {
                        if (newsb == null)
                        {
                            newsb = new StringBuffer(len);
                            for (int k = 0; k < i; k++)
                            {
                                newsb.append(sb[k]);
                            }
                        }
                        else
                        {
                            for (int k = lastKeyEnd + 1; k < i; k++)
                            {
                                newsb.append(sb[k]);
                            }
                        }
                        newsb.append(s);
                        i = endkey + 2;
                        lastKeyEnd = i;

                    }
                }
            }
        }
        if (lastKeyEnd == 0 && newsb == null)
        {
            return str;
        }
        if (lastKeyEnd > 0 && lastKeyEnd + 1 < len)
        {
            for (int k = lastKeyEnd + 1; k < len; k++)
            {
                newsb.append(sb[k]);
            }
        }
        return newsb.toString();

    }

    /**
     * This looks up {% %} delimted keys in a string and replaces them. This
     * is used by resource catalog, TreeConfig, and several other components.
     */
    public static String lookupKeysInString(String str, KeyFinder finder)
    {
        return lookupKeysInString(str, 0, finder);
    }

    /**
     * Match a file glob style expression without ranges.
     * '*' matches zero or more chars.
     * '?' matches any single char.
     *
     * @param pattern           A glob-style pattern to match
     * @param input             The string to match
     *
     * @return whether or not the string matches the pattern.
     */
    public static boolean match(String pattern, String input)
    {
        int patternIndex = 0;
        int inputIndex = 0;
        int patternLen = pattern.length();
        int inputLen = input.length();
        int[] stack = new int[100];
        int stacktop = 0;

        for (; ;)
        {
            if (patternIndex == patternLen)
            {
                if (inputIndex == inputLen)
                {
                    return true;
                }

            }
            else
            {
                char patternChar = pattern.charAt(patternIndex);

                if (inputIndex < inputLen)
                {
                    if (patternChar == '*')
                    {
                        stack[stacktop++] = patternIndex;
                        stack[stacktop++] = inputIndex + 1;
                        patternIndex++;
                        continue;

                    }
                    else if (patternChar == '?' ||
                            patternChar == input.charAt(inputIndex))
                    {
                        patternIndex++;
                        inputIndex++;
                        continue;
                    }

                }
                else if (patternChar == '*')
                {
                    patternIndex++;
                    continue;
                }
            }

            if (stacktop == 0)
            {
                return false;
            }

            inputIndex = stack[--stacktop];
            patternIndex = stack[--stacktop];
        }
    }

    /**
     Search a string for all instances of a substring and replace
     it with another string.  Amazing that this is not a method
     of java.lang.String since I use it all the time.

     @param search Substring to search for
     @param replace String to replace it with
     @param source String to search through
     @return The source with all instances of <code>search</code>
     replaced by <code>replace</code>
     */
    public static String sReplace(String search, String replace, String source)
    {

        int spot;
        String returnString;
        String origSource = new String(source);

        spot = source.indexOf(search);
        if (spot > -1)
            returnString = "";
        else
            returnString = source;
        while (spot > -1)
        {
            if (spot == source.length() + 1)
            {
                returnString = returnString.concat(source.substring(0, source.length() - 1).concat(replace));
                source = "";
            }
            else if (spot > 0)
            {
                returnString = returnString.concat(source.substring(0, spot).concat(replace));
                source = source.substring(spot + search.length(), source.length());
            }
            else
            {
                returnString = returnString.concat(replace);
                source = source.substring(spot + search.length(), source.length());
            }
            spot = source.indexOf(search);
        }
        if (!source.equals(origSource))
        {
            return returnString.concat(source);
        }
        else
        {
            return returnString;
        }
    }

    /**
     * remove all occurences of certain chars from a given string
     *
     * @param  inStr  the string to remove from
     * @param  removeChars  the characters to remove
     *
     * @return the string with removeChars removed
     */
    public static String removeChars(String inStr, String removeChars)
    {
        String outStr = inStr;
        if (removeChars != null)
        {
            int removeLen = removeChars.length();
            int start = 0;
            if (inStr != null)
            {
                int end = inStr.indexOf(removeChars);
                if (end > -1)
                {
                    outStr = "";
                    while (end > -1)
                    {
                        outStr += inStr.substring(start, end);
                        start = end + removeLen;
                        end = inStr.indexOf(removeChars, start);
                    }
                    outStr += inStr.substring(start);
                }
            }
        }
        return outStr;
    }

    /**
     * Replaces sequences of whitespaces
     * and crriage returns in text with single whitespace
     */
    public static String trimInside(String str)
    {
        str = str.trim();
        /**
         * Get rid of carriage returns.
         */
        str = StringUtils.sReplace("\n", " ", str);
        /**
         * Seek for multiple whitespaces.
         */
        return removeChars(str, "  ");
    }

    /**
     * Wraps lines at the given number of columns
     */
    public static String wrapLines(String s, int cols)
    {
        //StringBuffer sb = new StringBuffer();
        char[] c = s.toCharArray();
        char[] d = new char[c.length];

        int i = 0;
        int j = 0;
        int lastspace = -1;
        while (i < c.length)
        {
            if (c[i] == '\n')
            {
                j = 0;
            }
            if (j > cols && lastspace > 0)
            {
                d[lastspace] = '\n';
                j = i - lastspace;
                lastspace = -1;
            }
            if (c[i] == ' ')
            {
                lastspace = i;
            }
            d[i] = c[i];
            i++;
            j++;
        }
        String ret = new String(d);
        return ret;
    }

    public static boolean isEmpty(String str)
    {
        return str == null || str.trim().equals("");
    }
}
