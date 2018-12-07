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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

import com.hyperrealm.kiwi.text.FormatConstants;
import com.hyperrealm.kiwi.util.DateHolder;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A table cell renderer for displaying dates and/or times, formatted
 * according to the rules of the current locale.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.text.FormatConstants
 * @see com.hyperrealm.kiwi.util.LocaleManager
 */

public class DateTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * A string representation of the "unknown value"; a value that is either
     * of the wrong type or for which there is no available format.
     */

    private static final String VALUE_UNKNOWN = "--";

    private LocaleManager lm = LocaleManager.getDefault();

    private int type = FormatConstants.CUSTOM_FORMAT;

    private DateFormat customFormat = null;

    /**
     * Construct a new <code>DateTableCellRenderer</code> for dates.
     */

    public DateTableCellRenderer() {
        this(FormatConstants.DATE_FORMAT);
    }

    /**
     * Construct a new <code>DateTableCellRenderer</code> of the specified
     * type.
     *
     * @param type The fromatting type to be used by this field; one of the
     *             constants <code>DATE_FORMAT</code>, <code>TIME_FORMAT</code>, or
     *             <code>DATE_TIME_FORMAT</code>, defined in
     *             <code>com.hyperrealm.kiwi.text.FormatConstants</code>.
     */

    public DateTableCellRenderer(int type) {
        this.type = type;
    }

    /**
     * Construct a new <code>DateTableCellRenderer</code> with a custom
     * format.
     *
     * @param format The format. See {@link SimpleDateFormat
     *               SimpleDateFormat for examples}.
     * @throws IllegalArgumentException If the format is invalid.
     * @since Kiwi 2.4
     */

    public DateTableCellRenderer(String format) throws IllegalArgumentException {
        customFormat = new SimpleDateFormat(format);
    }

    /**
     * Get the formatting type.
     *
     * @return The data type being rendered by this cell renderer.
     */

    public int getType() {
        return (type);
    }

    /**
     * Set the formatting type.
     *
     * @param type The data type to be rendered by this cell renderer. See the
     *             constructor for more information.
     */

    public void setType(int type) {
        this.type = type;
    }

    /**
     * Set the value to be displayed by this cell renderer. It is
     * assumed that the object passed in is an instance of
     * <code>Date</code>, <code>Calendar</code>, or
     * <code>DateHolder</code>; if any other type of object is passed
     * in, or if the rendering type is not recognized, the
     * <code>VALUE_UNKNOWN</code> string will be rendered in the cell.
     *
     * @param value The value to render (must be a <code>Double</code>).
     */

    protected void setValue(Object value) {
        Date d = null;

        if (value instanceof Date) {
            d = (Date) value;
        } else if (value instanceof Calendar) {
            d = ((Calendar) value).getTime();
        } else if (value instanceof DateHolder) {
            d = ((DateHolder) value).getValue();
        } else if (value.getClass() == Long.class) {
            long val = (Long) value;
            d = (val >= 0) ? new Date(val) : null;
        }

        String s = VALUE_UNKNOWN;
        if (d != null) {
            switch (type) {
                case FormatConstants.DATE_FORMAT:
                    s = lm.formatDate(d);
                    break;

                case FormatConstants.TIME_FORMAT:
                    s = lm.formatTime(d);
                    break;

                case FormatConstants.DATE_TIME_FORMAT:
                    s = lm.formatDateTime(d);
                    break;

                case FormatConstants.CUSTOM_FORMAT:
                    if (customFormat != null) {
                        s = customFormat.format(d);
                    }
                    break;
                default:
            }
        }

        setText(s);
    }

}
