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
 * An implementation of <code>LoggingEndpoint</code> that serves as a "bit
 * bucket," discarding all messages.
 *
 * @author Mark Lindner
 */

public class NullLoggingEndpoint implements LoggingEndpoint {

    /**
     * Construct a new <code>NullLoggingEndpoint</code>.
     */

    public NullLoggingEndpoint() {
    }

    /**
     * Accept (and discard) a message. This method is effectively a no-op.
     */

    public void logMessage(int type, String s) {
    }

    /**
     * Close the logging endpoint. This method is effectively a no-op.
     */

    public void close() {
    }

}
