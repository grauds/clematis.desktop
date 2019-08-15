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

package com.hyperrealm.kiwi.util.plugin;

/**
 * General-purpose plugin exception.
 *
 * @author Mark Lindner
 * @since Kiwi 1.3
 */

public class PluginException extends Exception {
    /**
     * Construct a new <code>PluginException</code>.
     */

    public PluginException() {
        super();
    }

    /**
     * Construct a new <code>PluginException</code> with the specified message.
     *
     * @param message The exception message.
     */

    public PluginException(String message) {
        super(message);
    }

    /**
     * Construct a new <code>PluginException</code> with the specified cause.
     *
     * @param cause The cause.
     * @since Kiwi 2.3
     */

    public PluginException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new <code>PluginException</code> with the specified message
     * and cause.
     *
     * @param message The exception message.
     * @param cause   The cause.
     * @since Kiwi 2.3
     */

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "PluginException{} " + super.toString()
            + (getCause() != null ? (": " + getCause()) : "");
    }
}
