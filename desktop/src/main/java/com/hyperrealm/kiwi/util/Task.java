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

/**
 * This class represents an asynchronous task whose progress can be tracked
 * by a <code>ProgressObserver</code>.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ProgressObserver
 * @see com.hyperrealm.kiwi.ui.dialog.ProgressDialog
 * @see Runnable
 */

public abstract class Task implements Runnable {

    private static final int MAX_PERCENT = 100;

    private ArrayList<ProgressObserver> observers;

    /**
     * Construct a new <code>Task</code>.
     */

    public Task() {
        observers = new ArrayList<ProgressObserver>();
    }

    /**
     * Run the task. This method is the body of the thread for this task.
     */

    public abstract void run();

    /**
     * Add a progress observer to this task's list of observers. Observers are
     * notified by the task of progress made.
     *
     * @param observer The observer to add.
     */

    public final void addProgressObserver(ProgressObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove a progress observer from this task's list of observers.
     *
     * @param observer The observer to remove.
     */

    public final void removeProgressObserver(ProgressObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all observers about the percentage of the task completed.
     *
     * @param percent The percentage of the task completed, an integer value
     *                between 0 and 100 inclusive. Values outside of this range are silently
     *                clipped.
     */

    protected final void notifyObservers(int percent) {

        int percentInt = percent;

        if (percentInt < 0) {
            percentInt = 0;
        } else if (percentInt > MAX_PERCENT) {
            percentInt = MAX_PERCENT;
        }

        for (ProgressObserver observer : observers) {
            observer.setProgress(percentInt);
        }
    }

}
