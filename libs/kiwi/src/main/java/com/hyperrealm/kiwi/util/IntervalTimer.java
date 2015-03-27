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

/** A class that represents an interval timer. The timer can be started,
 * stopped, resumed, and cleared, much like a stopwatch.
 *
 * @author Mark Lindner
 */   

public class IntervalTimer implements Resource
{
  private long start = 0, stop = 0;
  private long time = 0, accum_time = 0;
  private boolean running = false;
  private String name = null;

  /** Construct a new <code>IntervalTimer</code>.
   */
   
  public IntervalTimer()
  {
  }

  /** Get the name for this timer.
   *
   * @return The timer's name.
   */
  
  public synchronized String getName()
  {
    return(name);
  }

  /** Set the name for this timer.
   *
   * @param name The new name for the timer.
   */
   
  public synchronized void setName(String name)
  {
    this.name = name;
  }

  /** Determine if the timer is currently running. Note that a timer is
   * not <i>running</i> in the sense that a thread is running; no code
   * is being executed while the timer is running.
   *
   * @return <code>true</code> if the timer is running and <code>false</code>
   * otherwise.
   */

  public synchronized boolean isRunning()
  {
    return(running);
  }
  
  /** Start the timer. If the timer is already running, this method does
   * nothing.
   */
  
  public synchronized void start()
  {
    if(running)
      return;
    
    clear();
    start = System.currentTimeMillis();
    running = true;
  }

  /** Stop the timer. If the timer is not running, this method does nothing.
   */
   
  public synchronized void stop()
  {
    if(!running)
      return;
    
    stop = System.currentTimeMillis();
    time = (stop - start);
    accum_time += time;
    running = false;
  }

  /** Resume a stopped timer. If the timer is running, this method does
   * nothing.
   */

  public synchronized void resume()
  {
    if(running)
      return;
    
    start = System.currentTimeMillis();
    running = true;
  }
  
  /** Clear a timer. Regardless of whether the timer is running or not, it
   * is stopped and the accumulated time is reset to 0.
   */
   
  public synchronized void clear()
  {
    start = 0;
    accum_time = 0;
    running = false;
  }

  /** Get the accumulated time for the timer. Time accumulates while the
   * timer is running, but not while it is stopped.
   *
   * @return The amount of time that this timer has accumulated, in
   * microseconds, or <code>-1</code> if the timer is currently running.
   */

  public synchronized long getTime()
  {
    if(running)
      return(-1);

    return(accum_time);
  }

  /** Create a string representation of the timer, which includes its name
   * and accumulated time.
   */
   
  public synchronized String toString()
  {
    return("Timer " + getName() + ": " + (running ? "(running)"
                                          : getTime() + " ms"));
  }

  /** Reserve the timer. This method clears the timer. */
  
  public void reserve()
  {
    clear();
  }

  /** Release the timer. This method clears the timer. */
  
  public void release()
  {
    clear();
  }
  
}

/* end of source file */
