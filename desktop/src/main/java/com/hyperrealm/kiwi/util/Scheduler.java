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
import java.util.Calendar;
import java.util.Iterator;

import static com.hyperrealm.kiwi.ui.SplashScreen.MILLISEC_IN_SECOND;

/**
 * A real-time scheduler with a resolution of one minute. The scheduler
 * maintains a list of timers. Each timer has a time specification (as
 * described by an instance of {@link com.hyperrealm.kiwi.util.TimeSpec}), a
 * unique numeric ID, and can be associated with an arbitrary user-supplied
 * object. When a timer's time specification matches the current system time,
 * the timer is "fired", and the <code>timerFired()</code> method is called.
 * This is an abstract method which must be implemented by subclassers.
 * <p>
 * This class is threadsafe.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.2
 */

public abstract class Scheduler<T> extends Thread {

    private static final int SECONDS_IN_MINUTE = 60;

    private final ArrayList<TimerDef<T>> timers;

    private int timerID = 0;

    /*
     */

    /**
     * Construct a new <code>Scheduler</code>. The scheduler must be explicitly
     * started via a call to the inherited <code>start()</code> method.
     */

    public Scheduler() {
        super("Kiwi Scheduler");

        timers = new ArrayList<>();

        setDaemon(true);
    }

    /**
     * Add a new timer to the scheduler.
     *
     * @param timeSpec  The time specification for the timer.
     * @param repeating A flag indicating whether the timer is repeating or
     *                  should only fire once.
     * @param object    An arbitrary user object to associate with the timer. This
     *                  may be <b>null</b>.
     * @return The ID of the new timer.
     */

    public int addTimer(TimeSpec timeSpec, boolean repeating, T object) {
        int id = 0;

        synchronized (timers) {
            id = ++timerID;
            timers.add(new TimerDef(object, timeSpec, !repeating, id));
        }

        return (id);
    }

    /**
     * Remove a timer from the scheduler.
     *
     * @param id The ID of the timer to remove.
     * @throws IllegalArgumentException If the ID does not refer to
     *                                  an existing timer.
     */

    public void removeTimer(int id) throws IllegalArgumentException {
        synchronized (timers) {
            Iterator<TimerDef<T>> iter = timers.iterator();

            while (iter.hasNext()) {
                TimerDef tdef = iter.next();

                if (tdef.getID() == id) {
                    iter.remove();
                    return;
                }
            }
        }

        throw new IllegalArgumentException("no such timer");
    }

    /**
     * Remove all timers from the scheduler.
     */

    public void removeAllTimers() {
        synchronized (timers) {
            timers.clear();
        }
    }

    /**
     * Main scheduler loop.
     */

    public void run() {
        // sweep & sleep loop

        Calendar now = Calendar.getInstance();

        while (!isInterrupted()) {
            // sweep

            now.setTimeInMillis(System.currentTimeMillis());

            synchronized (timers) {
                Iterator<TimerDef<T>> iter = timers.iterator();
                while (iter.hasNext()) {
                    TimerDef<T> timer = iter.next();

                    if (timer.getTimeSpec().match(now)) {
                        timerFired(timer.getID(), timer.getObject());

                        if (timer.once) {
                            iter.remove();
                        }
                    }
                }
            }

            // sleep

            now.setTimeInMillis(System.currentTimeMillis());

            int sec = SECONDS_IN_MINUTE - now.get(Calendar.SECOND);

            try {
                sleep(sec * MILLISEC_IN_SECOND);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Timer callback. This method is called when a timer fires.
     *
     * @param id     the id of the timer that fired.
     * @param object The user object that is associated with the timer. May be
     *               <b>null</b>.
     */

    abstract void timerFired(int id, T object);

    private class TimerDef<M> {
        M object;
        TimeSpec timeSpec;
        int id;
        boolean once;

        /*
         */

        TimerDef(M object, TimeSpec timeSpec, boolean once, int id) {
            this.object = object;
            this.timeSpec = timeSpec;
            this.once = once;
            this.id = id;
        }

        /*
         */

        TimeSpec getTimeSpec() {
            return (timeSpec);
        }

        /*
         */

        M getObject() {
            return (object);
        }

        /*
         */

        int getID() {
            return (id);
        }
    }

}
