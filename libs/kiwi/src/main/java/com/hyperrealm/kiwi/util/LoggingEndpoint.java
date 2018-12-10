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

/**
 * Logging endpoint interface. A logging endpoint accepts messages and writes
 * them to a file, a graphical console window, or some other type of data
 * sink.
 *
 * @author Mark Lindner
 */

public interface LoggingEndpoint {

    /**
     * Informational message type.
     */
    int INFO = 0;

    /**
     * Status message type.
     */
    int STATUS = 1;

    /**
     * Warning message type.
     */
    int WARNING = 2;

    /**
     * Error condition message type.
     */
    int ERROR = 3;

    /**
     * Accept a new message. Writes the message to the data sink.
     *
     * @param type    The message type; one of the static constants defined above.
     * @param message The message.
     */

    void logMessage(int type, String message);

    /**
     * Close the logging endpoint. The logging endpoint is closed. Once a
     * logging endpoint is closed, it cannot accept any more messages.
     */

    void close();
}
