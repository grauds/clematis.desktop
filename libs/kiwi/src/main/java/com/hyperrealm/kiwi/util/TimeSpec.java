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

import java.util.BitSet;
import java.util.Calendar;

/**
 * Date and time specification for real-time timers. This class
 * contains a collection of masks which collectively specify the times
 * at which a real-time timer should fire.  Each mask represents the
 * complete range of valid values for the corresponding component of a
 * time and date; these values can be individually turned "on" or
 * "off". A timer fires whenever the current system date and time
 * match the <code>TimeSpec</code> associated with that timer; in
 * other words, the timer fires if and only if the values of the
 * components of the current time and date are set in the
 * corresponding masks in the <code>TimeSpec</code>.
 *
 * <p> By convention, if <i>no</i> values are set in a given mask, the
 * meaning is the same as if they were <i>all</i> set.
 *
 * <p>
 * The five date/time components are:
 * <ul>
 * <li><i>hours</i> - hour of the day (0 - 23)
 * <li><i>minutes</i> - minute of the hour (0 - 59)
 * <li><i>days</i> - day of the month (1 - 31)
 * <li><i>months</i> - month of the year (1 - 12)
 * <li><i>weekdays</i> - day of week (0 - 6, where 0 indicates Sunday)
 * </ul>
 * <p>
 *
 * <b>Example.</b> To create a <code>TimeSpec</code> for a timer that should
 * fire at 10 past the hour, every hour except 5pm, on weekdays in June:
 *
 * <pre>
 * TimeSpec ts = new TimeSpec();
 * ts.setAllHours();
 * ts.clearHour(17);
 * ts.setMinute(10);
 * ts.setAllDays();
 * ts.setMonth(6);
 * for(int i = 1; i <= 5; i++)
 *   ts.setDayOfWeek(i);
 * </pre>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.Scheduler
 * @since Kiwi 2.2
 */
@SuppressWarnings("MagicNumber")
public class TimeSpec {
    private BitSet hourMask;
    private BitSet minuteMask;
    private BitSet dayMask;
    private BitSet monthMask;
    private BitSet dowMask;

    /**
     * Construct a new <code>TimeSpec</code> with all masks cleared.
     */

    public TimeSpec() {
        hourMask = new BitSet(24);
        minuteMask = new BitSet(60);
        dayMask = new BitSet(31);
        monthMask = new BitSet(12);
        dowMask = new BitSet(7);
    }

    /**
     * Construct a new <code>TimeSpec</code> based on the given
     * date and time.
     *
     * @param time The date/time.
     */

    public TimeSpec(Calendar time) {
        this();

        hourMask.set(time.get(Calendar.HOUR_OF_DAY));
        minuteMask.set(time.get(Calendar.MINUTE));
        dayMask.set(time.get(Calendar.DAY_OF_MONTH) - 1);
        monthMask.set(time.get(Calendar.MONTH));
        dowMask.set(0, dowMask.size() - 1);
    }

    /**
     * Construct a new <code>TimeSpec</code> for a given time of day.
     *
     * @param hour   The hour.
     * @param minute The minute.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    public TimeSpec(int hour, int minute) throws IllegalArgumentException {
        this();

        hourMask.set(hour);
        minuteMask.set(minute);
        dayMask.set(0, dayMask.size() - 1);
        monthMask.set(0, monthMask.size() - 1);
        dowMask.set(0, dowMask.size() - 1);
    }

    /**
     * Construct a new <code>TimeSpec</code> for the given times of day.
     *
     * @param hour   The hours.
     * @param minute The minutes.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    public TimeSpec(int[] hour, int[] minute) throws IllegalArgumentException {
        this();

        setValues(hourMask, hour, 0);
        setValues(minuteMask, minute, 0);
        dayMask.set(0, dayMask.size() - 1);
        monthMask.set(0, monthMask.size() - 1);
        dowMask.set(0, dowMask.size() - 1);
    }

    /**
     * Construct a new <code>TimeSpec</code> for the given date and time of day.
     *
     * @param hour   The hour.
     * @param minute The minute.
     * @param day    The day of month.
     * @param month  The month.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    public TimeSpec(int hour, int minute, int day, int month)
        throws IllegalArgumentException {
        this();

        hourMask.set(hour);
        minuteMask.set(minute);
        dayMask.set(day - 1);
        monthMask.set(month - 1);
        dowMask.set(0, dowMask.size() - 1);
    }

    /**
     * Construct a new <code>TimeSpec</code> for the given dates and times of
     * day.
     *
     * @param hour   The hours.
     * @param minute The minutes.
     * @param day    The days of the month.
     * @param month  The months.
     * @param dow    The days of week.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    public TimeSpec(int[] hour, int[] minute, int[] day, int[] month, int[] dow)
        throws IllegalArgumentException {
        this();

        setValues(hourMask, hour, 0);
        setValues(minuteMask, minute, 0);
        setValues(monthMask, month, -1);
        setValues(dayMask, day, -1);
        setDowValues(dow);
    }

    /**
     * Construct a new <code>TimeSpec</code> for the given dates and times of
     * day.
     *
     * @param hour   The hours.
     * @param minute The minutes.
     * @param dow    The days of week.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    public TimeSpec(int[] hour, int[] minute, int[] dow)
        throws IllegalArgumentException {
        this();

        setValues(hourMask, hour, 0);
        setValues(minuteMask, minute, 0);
        dayMask.set(0, dayMask.size() - 1);
        monthMask.set(0, monthMask.size() - 1);
        setDowValues(dow);
    }

    /**
     * Set the days of week. The days-of-week mask is modified to contain only
     * the specified days.
     *
     * @param dow The days of week.
     * @throws IllegalArgumentException If any of the values are out of
     *                                  range.
     */

    private void setDowValues(int[] dow) throws IllegalArgumentException {
        if (dow.length > 0) {
            for (int i = 0; i < dow.length; i++) {
                int d = dow[i];
                if (d == 7) {
                    d = 0;
                }

                dowMask.set(d);
            }
        } else {
            dowMask.set(0, dowMask.size() - 1);
        }
    }

    /*
     */

    private void setValues(BitSet mask, int[] values, int adj)
        throws IllegalArgumentException {
        if (values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                mask.set(values[i] + adj);
            }
        } else {
            mask.set(0, mask.size() - 1);
        }
    }

    /**
     * Set the hour. The specified hour is added to the hour mask.
     *
     * @param hour The hour to add to the hour mask.
     * @throws IllegalArgumentException If the hour value is out of
     *                                  range.
     */

    public void setHour(int hour) throws IllegalArgumentException {
        hourMask.set(hour);
    }

    /**
     * Clear an hour. The specified hour is cleared in the hour mask.
     *
     * @param hour The hour to clear in the hour mask.
     * @throws IllegalArgumentException If the hour value is out of
     *                                  range.
     */

    public void clearHour(int hour) throws IllegalArgumentException {
        hourMask.clear(hour);
    }

    /**
     * Set all hours. All hours are set in the hour mask.
     */

    public void setAllHours() {
        hourMask.set(0, hourMask.size() - 1);
    }

    /**
     * Clear all hours. All hours are cleared in the hour mask.
     */

    public void clearAllHours() {
        hourMask.clear();
    }

    /**
     * Set the minute. The specified minute is set in the minute mask.
     *
     * @param minute The minute to set in the minute mask.
     * @throws IllegalArgumentException If the minute value is out of
     *                                  range.
     */

    public void setMinute(int minute) throws IllegalArgumentException {
        minuteMask.set(minute);
    }

    /**
     * Clear a minute. The specified minute is cleared in the minute mask.
     *
     * @param minute The minute to clear in the minute mask.
     * @throws IllegalArgumentException If the minute value is out of
     *                                  range.
     */

    public void clearMinute(int minute) throws IllegalArgumentException {
        minuteMask.clear(minute);
    }

    /**
     * Set all minutes. All minutes are set in the minute mask.
     */

    public void setAllMinutes() {
        minuteMask.set(0, minuteMask.size() - 1);
    }

    /**
     * Clear all minutes. All minutes are cleared in the minute mask.
     */

    public void clearAllMinutes() {
        minuteMask.clear();
    }

    /**
     * Set a day. The specified day is set in the day mask.
     *
     * @param day The day to set in the day mask.
     * @throws IllegalArgumentException If the day value is out of
     *                                  range.
     */

    public void setDay(int day) throws IllegalArgumentException {
        dayMask.set(day - 1);
    }

    /**
     * Clear a day. The specified day is cleared in the day mask.
     *
     * @param day The day to clear in the day mask.
     * @throws IllegalArgumentException If the day value is out of
     *                                  range.
     */

    public void clearDay(int day) throws IllegalArgumentException {
        dayMask.clear(day - 1);
    }

    /**
     * Set all days. All days are set in the day mask.
     */

    public void setAllDays() {
        dayMask.set(0, dayMask.size() - 1);
    }

    /**
     * Clear all days. All days are cleared in the day mask.
     */

    public void clearAllDays() {
        dayMask.clear();
    }

    /**
     * Set a month. The specified month is set in the month mask.
     *
     * @param month The month to set in th emonth mask.
     * @throws IllegalArgumentException If the month value is out of
     *                                  range.
     */

    public void setMonth(int month) throws IllegalArgumentException {
        monthMask.set(month - 1);
    }

    /**
     * Clear a month. The specified month is cleared in the month mask.
     *
     * @param month The month to clear in the month mask.
     * @throws IllegalArgumentException If the month value is out of
     *                                  range.
     */

    public void clearMonth(int month) throws IllegalArgumentException {
        monthMask.clear(month - 1);
    }

    /**
     * Set all months. All months are set in the month mask.
     */

    public void setAllMonths() {
        monthMask.set(0, monthMask.size() - 1);
    }

    /**
     * Clear all months. All months are cleared in the month mask.
     */

    public void clearAllMonths() {
        monthMask.clear();
    }

    /**
     * Set a day of week. The specified day is set in the day-of-week mask.
     *
     * @param dow The day of week to set in the day-of-week mask.
     * @throws IllegalArgumentException If the day of week value is
     *                                  out of range.
     */

    public void setDayOfWeek(int dow) throws IllegalArgumentException {
        dowMask.set(dow == 7 ? 0 : dow);
    }

    /**
     * Clear a day of week. The specified day is cleared in the day-of-week
     * mask.
     *
     * @param dow The day of week to clear in the day-of-week mask.
     * @throws IllegalArgumentException If the day of week value is
     *                                  out of range.
     */

    public void clearDayOfWeek(int dow) throws IllegalArgumentException {
        dowMask.clear(dow == 7 ? 0 : dow);
    }


    /**
     * Set all days of week. All days are set in the day-of-week mask.
     */

    public void setAllDaysOfWeek() {
        dowMask.set(0, dowMask.size() - 1);
    }

    /**
     * Clear all days of week. All days are cleared in the day-of-week mask.
     */

    public void clearAllDaysOfWeek() {
        dowMask.clear();
    }

    /**
     * Attempt a match of this <code>TimeSpec</code> against the given date
     * and time.
     *
     * @param hour   The hour to match against.
     * @param minute The minute to match against.
     * @param month  They month to match against.
     * @param day    The day of month to match against.
     * @param dow    The day of week to match against.
     * @return <b>true</b> for a successful match, <b>false</b> otherwise.
     */

    public boolean match(int hour, int minute, int month, int day, int dow) {
        return hourMask.get(hour)
            && minuteMask.get(minute)
            && monthMask.get(month)
            && dayMask.get(day - 1)
            && dowMask.get(dow == 7 ? 0 : dow);
    }

    /**
     * Attempt a match of this <code>TimeSpec</code> against the given date
     * and time.
     *
     * @param time The date and time to match against.
     * @return <b>true</b> for a successful match, <b>false</b> otherwise.
     */
    public boolean match(Calendar time) {
        return match(time.get(Calendar.HOUR_OF_DAY),
            time.get(Calendar.MINUTE),
            time.get(Calendar.MONTH),
            time.get(Calendar.DAY_OF_MONTH),
            time.get(Calendar.DAY_OF_WEEK) - 1);
    }
}
