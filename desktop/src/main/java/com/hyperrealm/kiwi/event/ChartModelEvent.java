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

package com.hyperrealm.kiwi.event;

import java.util.EventObject;

import com.hyperrealm.kiwi.ui.model.ChartModel;

/**
 * A class that represents a general <code>ChartModel</code> event. Any
 * change in the data will require a complete repaint of the chart, so more
 * specific events would not be useful.
 *
 * @author Mark Lindner
 */

public class ChartModelEvent extends EventObject {

    /**
     * Construct a new <code>ChartModelEvent</code> for the specified source.
     *
     * @param source The <code>ChartModel</code> that is the source of this
     *               event.
     */

    public ChartModelEvent(ChartModel source) {
        super(source);
    }

}
