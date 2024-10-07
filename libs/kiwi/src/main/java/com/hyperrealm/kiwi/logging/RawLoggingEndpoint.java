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

package com.hyperrealm.kiwi.logging;

/**
 * An implementation of <{@link LoggingEndpoint} for standard error.
 *
 * @author Mark Lindner
 */
public class RawLoggingEndpoint implements LoggingEndpoint {

    /**
     * Construct a new {@link RawLoggingEndpoint}
     */
    public RawLoggingEndpoint() {}

    /**
     * Write a message to standard error.
     *
     * @param type    The message type; one of the constants defined in {@link LoggingEndpoint}
     * @param message The message.
     */
    @SuppressWarnings("MagicNumber")
    public void logMessage(Types type, String message) {
        System.err.println(type + " - " + message);
    }

    /**
     * Close the logging endpoint. This method is effectively a no-op, since it
     * is usually undesirable to close the standard error stream.
     */
    public void close() {}
}
