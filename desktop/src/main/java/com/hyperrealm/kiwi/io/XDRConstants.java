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

package com.hyperrealm.kiwi.io;

/**
 * XDR encoding constants.
 *
 * @author Mark Lindner
 * @since Kiwi 2.4.1
 */

public final class XDRConstants {
    /**
     * XDR encoding unit size.
     */
    public static final int UNIT_SIZE = 4;
    /**
     * Character encoding for ASCII strings.
     */
    public static final String ASCII_ENCODING = "US-ASCII";

    private XDRConstants() {
    }
}
