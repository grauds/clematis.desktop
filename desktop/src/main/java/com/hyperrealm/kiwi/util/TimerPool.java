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
 * A concrete implementation of <code>ResourcePool</code> for managing a
 * pool of <code>IntervalTimer</code>s.
 *
 * @author Mark Lindner
 */

public class TimerPool extends ResourcePool<IntervalTimer> {
    /**
     * Construct a new <code>TimerPool</code> of the specified size.
     *
     * @param size The number of <code>IntervalTimer</code>s to preallocate.
     */

    public TimerPool(int size) {
        super(size);
    }

    /**
     * Construct a new <code>IntervalTimer</code>.
     *
     * @return The new <code>IntervalTimer</code>.
     */

    protected IntervalTimer construct() {
        return (new IntervalTimer());
    }

}
