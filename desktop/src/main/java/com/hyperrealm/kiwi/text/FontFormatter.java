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

import java.awt.Font;

import com.hyperrealm.kiwi.util.StringUtils;

/**
 * This class provides methods for converting fonts to and from string
 * representations.
 *
 * @author Mark Lindner
 * @see java.awt.Font
 */
public class FontFormatter {

    private static final int[] STYLE_CODES
            = {Font.PLAIN, Font.BOLD, Font.ITALIC, (Font.BOLD | Font.ITALIC)};

    private static final String[] STYLE_NAMES
            = {"Plain", "Bold", "Italic", "BoldItalic"};

    private static final String COMMA = ",";

    private static final int FONT_NAME_LENGTH = 3;

    /*
     */

    private FontFormatter() {
    }

    /*
     */

    private static String nameForStyle(int style) {
        for (int i = 0; i < STYLE_CODES.length; i++) {
            if (style == STYLE_CODES[i]) {
                return (STYLE_NAMES[i]);
            }
        }
        return (null);
    }

    /*
     */

    private static int styleForName(String style) {
        for (int i = 0; i < STYLE_CODES.length; i++) {
            if (style.equalsIgnoreCase(STYLE_NAMES[i])) {
                return (STYLE_CODES[i]);
            }
        }

        return (-1);
    }

    /**
     * Format a <code>Font</code> as a string. The format is "Name,Style,Size"
     * where <i>Style</i> is one of <i>Plain</i>, <i>Bold</i>, <i>Italic</i>, or
     * <i>BoldItalic</i>.
     *
     * @param font The <code>Font</code> to format.
     * @return A string representation of the font.
     * @see #parse
     */

    public static String format(Font font) {

        String styleName = nameForStyle(font.getStyle());
        if (styleName == null) {
            return null;
        }

        return (font.getName() + COMMA + styleName + COMMA + font.getSize());
    }

    /**
     * Parse a font representation, returning an appropriate <code>Font</code>
     * object.
     *
     * @param font The string representation of the font.
     * @return An appropriate <code>Font</code> object.
     * @throws com.hyperrealm.kiwi.text.ParsingException If
     *                                                   <code>font</code> is an invalid font representation.
     * @see #format
     */

    @SuppressWarnings("MagicConstant")
    public static Font parse(String font) throws ParsingException {

        String[] def = StringUtils.split(font, COMMA);
        if (def.length != FONT_NAME_LENGTH) {
            return null;
        }

        int styleCode = -1, sz;

        try {
            styleCode = styleForName(def[1]);
            sz = Integer.parseInt(def[2]);
        } catch (NumberFormatException ex) {
            sz = 0;
        }

        if ((styleCode < 0) || (sz == 0)) {
            throw (new ParsingException("Invalid Font specification."));
        }

        return (new Font(def[0], styleCode, sz));
    }

}
