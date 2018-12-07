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

import java.util.ArrayList;
import java.util.List;

/**
 * A logging multiplexor. This class manages a set of
 * <code>LoggingEndpoint</code>s and itself implements the
 * <code>LoggingEndpoint</code> interface. It may be use to direct logging
 * messages to several endpoints simultaneously. For example, an application
 * may send messages to both a console and a file.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.LoggingEndpoint
 */

public class LoggingMux implements LoggingEndpoint {

    private static final int INITIAL_CAPACITY = 5;

    private List<LoggingEndpoint> v;

    /**
     * Construct a new <code>LoggingMux</code>.
     */

    public LoggingMux() {
        v = new ArrayList<LoggingEndpoint>(INITIAL_CAPACITY);
    }

    /**
     * Log a message to all endpoints in this set.
     */

    public void logMessage(int type, String message) {
        int l = v.size();
        for (LoggingEndpoint loggingEndpoint : v) {
            loggingEndpoint.logMessage(type, message);
        }
    }

    /**
     * Close this set of endpoints. Equivalent to <code>close(false)</code>.
     */

    public void close() {
        close(false);
    }

    /**
     * Close this set of endpoints.
     *
     * @param closeEndpoints If <code>true</code>, in addition to removing every
     *                       <code>LoggingEndpoint</code> from its list, the <code>LoggingMux</code>
     *                       closes each <code>LoggingEndpoint</code> explicitly via a call to its
     *                       <code>close()</code> method.
     */

    public void close(boolean closeEndpoints) {
        if (closeEndpoints) {
            int l = v.size();
            for (LoggingEndpoint loggingEndpoint : v) {
                loggingEndpoint.close();
            }
        }

        removeAllLoggingEndpoints();
    }

    /**
     * Add a <code>LoggingEndpoint</code> to the set.
     *
     * @param endpoint The <code>LoggingEndpoint</code> to add.
     */

    public void addLoggingEndpoint(LoggingEndpoint endpoint) {
        v.add(endpoint);
    }

    /**
     * Remove a <code>LoggingEndpoint</code> from the set.
     *
     * @param endpoint The <code>LoggingEndpoint</code> to remove.
     */

    public void removeLoggingEndpoint(LoggingEndpoint endpoint) {
        v.remove(endpoint);
    }

    /**
     * Remove all <code>LoggingEndpoint</code>s from the set.
     */

    public void removeAllLoggingEndpoints() {
        v.clear();
    }

}
