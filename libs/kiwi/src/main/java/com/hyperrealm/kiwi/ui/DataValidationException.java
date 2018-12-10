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

package com.hyperrealm.kiwi.ui;

/**
 * An exception indicating a data validation error.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.AbstractEditorPanel
 * @since Kiwi 2.1
 */

public class DataValidationException extends Exception {
    /**
     *
     */

    public DataValidationException() {
        super();
    }

    /**
     *
     */

    public DataValidationException(String message) {
        super(message);
    }

    /**
     *
     */

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     */

    public DataValidationException(Throwable cause) {
        super(cause);
    }

}
