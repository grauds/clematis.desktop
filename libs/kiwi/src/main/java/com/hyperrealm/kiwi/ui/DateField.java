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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A subclass of <code>DataField</code> for the input and display of
 * dates.
 *
 * @author Mark Lindner
 * @since Kiwi 1.4
 */

public class DateField extends DataField<Date> {
    /**
     * The current date entered in this field.
     */
    protected Date date = null;
    /**
     * The formatter used to parse and format dates in this field.
     */
    private DateFormat dateFormat;
    /**
     *
     */
    private ParsePosition pos = new ParsePosition(0);

    /**
     * Construct a new <code>DateField</code> with the specified width and a
     * default, locale-specific date format.
     *
     * @param width The width of the field.
     */

    public DateField(int width) {
        this(width, null);
    }

    /**
     * Construct a new <code>DateField</code> with the specified width and
     * date format.
     *
     * @param width  The width of the field.
     * @param format The date format.
     */

    public DateField(int width, String format) throws IllegalArgumentException {
        super(width);

        LocaleManager locmgr = LocaleManager.getDefault();

        if (format == null) {
            dateFormat = locmgr.getShortDateFormat();
        } else {
            dateFormat = new SimpleDateFormat(format);
        }

        setFont(KiwiUtils.BOLD_FONT);
    }

    /**
     * Parse the contents of the field as a date, and return the date.
     *
     * @return The parsed date, or <code>null</code> if parsing failed.
     */

    public Date getDate() {
        checkInput();

        return date != null ? new Date(date.getTime()) : null;
    }

    /**
     * Set the data to be displayed by this field. The date is formatted as a
     * string, according to the rules of the current locale, and displayed in the
     * field. Invalid input flagging is automatically turned off.
     */

    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());

        setText((date == null) ? "" : dateFormat.format(date));

        invalid = false;
        paintInvalid(invalid);
    }

    /**
     * Get the object being edited. Equivalent to <code>getDate()</code>.
     *
     * @since Kiwi 2.0
     */

    public Date getObject() {
        return (getDate());
    }

    /**
     * Set the object being edited. Equivalent to <code>setDate()</code>.
     *
     * @since Kiwi 2.0
     */

    public void setObject(Date obj) {
        setDate(obj);
    }

    /**
     * Clear the field.
     *
     * @since Kiwi 2.0
     */

    public void clear() {
        setDate(null);
    }

    /**
     * Determine if the given input is valid for this field.
     *
     * @return <code>true</code> if the input is valid, and <code>false</code>
     * otherwise.
     */

    protected boolean checkInput() {
        invalid = false;

        try {
            pos.setIndex(0);
            String s = getText();
            Date d = dateFormat.parse(s, pos);
            trapGarbage(s);
            setDate(d);
        } catch (ParseException ex) {
            invalid = true;
            date = null;
        }

        paintInvalid(invalid);

        return (!invalid);
    }

    /*
     */

    private void trapGarbage(String s) throws ParseException {
        if (pos.getIndex() != s.length()) {
            throw (new ParseException("Garbage in string " + s, pos.getIndex()));
        }
    }

}
