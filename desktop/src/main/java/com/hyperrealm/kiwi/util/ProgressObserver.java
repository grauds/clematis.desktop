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
 * This class defines the behavior of an object that wishes to receive
 * periodic updates on the progress of a lengthy task.
 *
 * @author Mark Lindner
 */

public interface ProgressObserver {
    /**
     * Set the progress amount to <code>progress</code>, which is a value
     * between 0 and 100. (Out of range values should be silently clipped.)
     *
     * @param progress The percentage of the task completed.
     */

    void setProgress(int progress);
}
