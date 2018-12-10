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
 * An interface for delivering an exception to a handler without using the
 * <i>try / catch</i> programming construct. Sometimes an exception may be
 * thrown within a method that cannot itself throw an exception, for example if
 * the method is an implementation of an interface and that interface does not
 * define a <i>throws</i> clause for that method's signature.
 * <p>
 * This interface may be used to deliver caught Exceptions to an appropriate
 * handler, perhaps in a different object altogether.
 *
 * @author Mark Lindner
 */

public interface ExceptionHandler {
    /**
     * Handle an exception.
     *
     * @param ex The exception that was raised.
     */

    void exceptionRaised(Exception ex);
}
