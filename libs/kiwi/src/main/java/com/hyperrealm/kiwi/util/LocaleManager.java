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

import java.text.Collator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.hyperrealm.kiwi.text.FormatConstants;

/**
 * The Kiwi locale manager. This class
 * retrieves resource bundles from one or more <code>ResourceManager</code>s,
 * and provides convenience methods for formatting various types of data
 * according to the rules of the current locale.
 * <p>
 * In the case of decimal-based values such as percentages and currency
 * amounts, if the corresponding parse fails, this class will resort to
 * parsing the string as a generic decimal value, throwing an exception if
 * that also fails. This allows for the parsing of values that are formatted
 * specifically or generically. For example, the currency value $49.55 will
 * parse to the decimal value <tt>49.55</tt> whether the source string is
 * <tt>"$49.55"</tt> or <tt>"49.55"</tt>.
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public class LocaleManager implements FormatConstants {

    /**
     * The default number of decimal digits to retain when formatting
     * currency values.
     */
    private static final int DEFAULT_CURRENCY_DECIMALS = 2;

    /**
     * The default number of decimal digits to retain when formatting
     * percentage values.
     */
    private static final int DEFAULT_PERCENTAGE_DECIMALS = 4;

    /**
     * The default number of decimal digits to retain when formatting
     * numeric values.
     */
    private static final int DEFAULT_NUMBER_DECIMALS = 4;

    private static final int DATE_PLACEHOLDERS = 3;

    private static final int PERCENT = 100;

    private static LocaleManager defaultLocaleManager = new LocaleManager();

    private static ParsePosition pos = new ParsePosition(0);

    private Collator collator;

    private final ArrayList<ResourceManager> resourceManagers;

    private NumberFormat currencyFormat;
    private NumberFormat percentFormat;
    private NumberFormat decimalFormat;
    private NumberFormat integerFormat;

    private DateFormat[] dateFormat = new DateFormat[DATE_PLACEHOLDERS];

    private DateFormat[] timeFormat = new DateFormat[DATE_PLACEHOLDERS];

    private DateFormat[] dateTimeFormat = new DateFormat[DATE_PLACEHOLDERS];

    private int[] lengthTypes = {DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG};

    private DateFormatSymbols dateFormatSymbols = null;

    /**
     * Construct a new <code>LocaleManager</code>.
     *
     * @param resourceManager The <code>ResourceManager</code> that will be
     *                        used to load resource bundles.
     * @since Kiwi 1.3
     */

    public LocaleManager(ResourceManager resourceManager) {
        resourceManagers = new ArrayList<>();
        resourceManagers.add(resourceManager);

        setLocale(Locale.getDefault());
    }

    /**
     * Construct a new <code>LocaleManager</code>.
     *
     * @since Kiwi 1.3
     */

    public LocaleManager() {
        this(ResourceManager.getKiwiResourceManager());
    }

    /**
     * Get a reference to the default <code>LocaleManager</code>.
     *
     * @return The default <code>LocaleManager</code>.
     * @since Kiwi 2.0
     */

    public static LocaleManager getDefault() {
        return (defaultLocaleManager);
    }

    /**
     * Get the default locale.
     *
     * @return The current locale for this <code>LocaleManager</code>.
     * @since Kiwi 1.3
     */

    public Locale getLocale() {
        return (Locale.getDefault());
    }

    /**
     * Set the default locale. This method should be called in place of
     * <code>Locale.setDefault()</code> (though it does call this method itself).
     *
     * @param locale The new locale.
     */

    public void setLocale(Locale locale) {
        if (!locale.equals(Locale.getDefault())) {
            Locale.setDefault(locale);
        }

        for (int i = 0; i < DATE_PLACEHOLDERS; i++) {
            dateFormat[i] = DateFormat.getDateInstance(lengthTypes[i]);
            dateFormat[i].setLenient(false);
            dateFormat[i].setTimeZone(TimeZone.getDefault());
            timeFormat[i] = DateFormat.getTimeInstance(lengthTypes[i]);
            timeFormat[i].setLenient(false);
            dateTimeFormat[i] = DateFormat.getDateTimeInstance(lengthTypes[i], lengthTypes[i]);
            dateTimeFormat[i].setLenient(false);
        }

        currencyFormat = NumberFormat.getCurrencyInstance();
        setPercentFormat(NumberFormat.getPercentInstance());
        setDecimalFormat(NumberFormat.getNumberInstance());

        setIntegerFormat(NumberFormat.getNumberInstance());
        getIntegerFormat().setParseIntegerOnly(true);
        getIntegerFormat().setMaximumFractionDigits(0);

        dateFormatSymbols = new DateFormatSymbols(locale);

        collator = Collator.getInstance(locale);
    }

    /**
     * Register a <code>ResourceManager</code> with the locale manager. The
     * locale manager searches through all registered resource managers when
     * searching for a resource bundle. The Kiwi internal resource manager is
     * always registered.
     *
     * @param manager The <code>ResourceManager</code> to register.
     */

    public void addResourceManager(ResourceManager manager) {
        synchronized (resourceManagers) {
            // don't allow the same manager to be added twice

            int i = resourceManagers.indexOf(manager);
            if (i < 0) {
                resourceManagers.add(manager);
            }
        }
    }

    /**
     * Unregister a <code>ResourceManager</code> from the locale manager. The
     * Kiwi internal resource manager is always registered and cannot be removed.
     *
     * @param manager The <code>ResourceManager</code> to unregister.
     */

    public void removeResourceManager(ResourceManager manager) {
        synchronized (resourceManagers) {
            int i = resourceManagers.indexOf(manager);
            if (i > 0) {// don't allow the default manager to be removed

                resourceManagers.remove(i);
            }
        }
    }

    /**
     * Retrieve the named resource bundle. The locale manager queries all
     * resource managers sequentially for the desired resource bundle, starting
     * with the Kiwi internal resource manager, returning as soon as the
     * bundle is found.
     *
     * @return The <code>LocaleData</code> object representing the specified
     * resource bundle.
     * @throws ResourceNotFoundException If the specified bundle
     *                                   could not be located by any of the registered resource managers.
     */

    public LocaleData getLocaleData(String name) throws ResourceNotFoundException {
        LocaleData ld = null;

        synchronized (resourceManagers) {
            for (ResourceManager rm : resourceManagers) {
                try {
                    ld = rm.getResourceBundle(name);
                } catch (ResourceNotFoundException ignored) {
                }
            }
        }

        if (ld == null) {
            throw (new ResourceNotFoundException(name));
        }

        return (ld);
    }

    /**
     * Format a date according to the rules of the current locale, with the
     * default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatDate(Calendar date) {
        return (formatDate(date.getTime(), MEDIUM));
    }

    /**
     * Format a date according to the rules of the current locale, with the
     * default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatDate(Date date) {
        return (formatDate(date, MEDIUM));
    }

    /**
     * Format a date according to the rules of the current locale, with the
     * specified format length.
     *
     * @param date The date to format.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return A string representation of the value.
     */

    public String formatDate(Date date, int type) {
        return (dateFormat[type].format(date));
    }

    /**
     * Format a time according to the rules of the current locale, with the
     * default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatTime(Calendar date) {
        return (formatTime(date.getTime(), MEDIUM));
    }

    /**
     * Format a time according to the rules of the current locale, with the
     * default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatTime(Date date) {
        return (formatTime(date, MEDIUM));
    }

    /**
     * Format a time according to the rules of the current locale, with the
     * specified format length.
     *
     * @param date The date to format.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return A string representation of the value.
     */

    public String formatTime(Date date, int type) {
        return (timeFormat[type].format(date));
    }

    /**
     * Format a date and time according to the rules of the current locale,
     * with the default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatDateTime(Calendar date) {
        return (formatDateTime(date.getTime(), MEDIUM));
    }

    /**
     * Format a date and time according to the rules of the current locale,
     * with the default (medium) format length.
     *
     * @param date The date to format.
     * @return A string representation of the value.
     */

    public String formatDateTime(Date date) {
        return (formatDateTime(date, MEDIUM));
    }

    /**
     * Format a date and time according to the rules of the current locale,
     * with the specified format length.
     *
     * @param date The date to format.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return A string representation of the value.
     */

    public String formatDateTime(Date date, int type) {
        return (dateTimeFormat[type].format(date));
    }

    /**
     * Parse a date value from a string using the rules of the current locale,
     * with the default (medium) format length.
     *
     * @param s The string to parse.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public Date parseDate(String s) throws ParseException {
        return (parseDate(s, MEDIUM));
    }

    /**
     * Parse a date value from a string using the rules of the current locale,
     * with the specified format length.
     *
     * @param s    The string to parse.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized Date parseDate(String s, int type) throws ParseException {
        pos.setIndex(0);
        Date d = dateFormat[type].parse(s, pos);
        trapGarbage(s);
        return (d);
    }

    /**
     * Parse a time value from a string using the rules of the current locale,
     * with the default (medium) format length.
     *
     * @param s The string to parse.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public Date parseTime(String s) throws ParseException {
        return (parseTime(s, MEDIUM));
    }

    /**
     * Parse a time value from a string using the rules of the current locale,
     * with the specified format length.
     *
     * @param s    The string to parse.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized Date parseTime(String s, int type) throws ParseException {
        pos.setIndex(0);
        Date d = timeFormat[type].parse(s, pos);
        trapGarbage(s);
        return (d);
    }

    /**
     * Parse a date and time value from a string using the rules of the current
     * locale, with the default (medium) format length.
     *
     * @param s The string to parse.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public Date parseDateTime(String s) throws ParseException {
        return (parseDateTime(s, MEDIUM));
    }

    /**
     * Parse a date and time value from a string using the rules of the current
     * locale, with the specified format length.
     *
     * @param s    The string to parse.
     * @param type The format length; one of the symbolic constants
     *             <code>SHORT</code>, <code>MEDIUM</code>, or <code>LONG</code>.
     * @return The resulting <code>Date</code> object.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized Date parseDateTime(String s, int type)
        throws ParseException {
        pos.setIndex(0);
        Date d = dateTimeFormat[type].parse(s, pos);
        trapGarbage(s);
        return (d);
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with <code>DEFAULT_CURRENCY_DECIMALS</code> decimal places retained, and
     * grouping turned off.
     *
     * @param value The currency value to format.
     * @return A string representation of the value.
     */

    public String formatCurrency(double value) {
        return (formatCurrency(value, DEFAULT_CURRENCY_DECIMALS, false));
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with the specified number of decimal places retained, and grouping turned
     * off.
     *
     * @param value    The currency value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatCurrency(double value, int decimals) {
        return (formatCurrency(value, decimals, false));
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with the specified number of decimal places retained, and the specified
     * grouping policy.
     *
     * @param value    The currency value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public synchronized String formatCurrency(double value,
                                              int decimals,
                                              boolean grouping) {
        currencyFormat.setMaximumFractionDigits(decimals);
        currencyFormat.setMinimumFractionDigits(decimals);
        currencyFormat.setGroupingUsed(grouping);
        return currencyFormat.format(value);
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with <code>DEFAULT_CURRENCY_DECIMALS</code> decimal places retained, and
     * grouping turned off.
     *
     * @param value The currency value to format.
     * @return A string representation of the value.
     */

    public String formatCurrency(float value) {
        return (formatCurrency(value, DEFAULT_CURRENCY_DECIMALS, false));
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with the specified number of decimal places retained, and grouping turned
     * off.
     *
     * @param value    The currency value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatCurrency(float value, int decimals) {
        return (formatCurrency(value, decimals, false));
    }

    /**
     * Format a currency value according to the rules of the current locale,
     * with the specified number of decimal places retained, and the specified
     * grouping policy.
     *
     * @param value    The currency value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public String formatCurrency(float value, int decimals, boolean grouping) {
        synchronized (currencyFormat) {
            currencyFormat.setMaximumFractionDigits(decimals);
            currencyFormat.setMinimumFractionDigits(decimals);
            currencyFormat.setGroupingUsed(grouping);
            return (currencyFormat.format(value));
        }
    }

    /**
     * Parse a currency value from a string using the rules of the current
     * locale. The input string may include grouping characters.
     *
     * @param s The string to parse.
     * @return The resulting value.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized double parseCurrency(String s) throws ParseException {
        try {
            return parseCurrency(s.trim(), currencyFormat);
        } catch (ParseException ex) {
            return (parseDecimal(s));
        }
    }

    private double parseCurrency(String curr, NumberFormat currencyFormat) throws ParseException {
        currencyFormat.setGroupingUsed(true);
        pos.setIndex(0);
        Number n = currencyFormat.parse(curr, pos);
        trapGarbage(curr);
        return ((n == null) ? 0 : n.doubleValue());
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with <code>DEFAULT_PERCENTAGE_DECIMALS</code> decimal places retained, and
     * grouping turned off.
     *
     * @param value The percentage value to format.
     * @return A string representation of the value.
     */

    public String formatPercentage(double value) {
        return (formatPercentage(value, DEFAULT_PERCENTAGE_DECIMALS, false));
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with the specified number of decimal places retained, and grouping turned
     * off.
     *
     * @param value    The percentage value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatPercentage(double value, int decimals) {
        return (formatPercentage(value, decimals, false));
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with the specified number of decimal places retained, and the specified
     * grouping policy.
     *
     * @param value    The percentage value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public synchronized String formatPercentage(double value, int decimals,
                                                boolean grouping) {
        getPercentFormat().setMinimumFractionDigits(decimals);
        getPercentFormat().setMaximumFractionDigits(decimals);
        getPercentFormat().setGroupingUsed(grouping);
        return (getPercentFormat().format(value));
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with <code>DEFAULT_PERCENTAGE_DECIMALS</code> decimal places retained, and
     * grouping turned off.
     *
     * @param value The percentage value to format.
     * @return A string representation of the value.
     */

    public String formatPercentage(float value) {
        return (formatPercentage(value, DEFAULT_PERCENTAGE_DECIMALS, false));
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with the specified number of decimal places retained, and grouping turned
     * off.
     *
     * @param value    The percentage value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatPercentage(float value, int decimals) {
        return (formatPercentage(value, decimals, false));
    }

    /**
     * Format a percentage value according to the rules of the current locale,
     * with the specified number of decimal places retained, and the specified
     * grouping policy.
     *
     * @param value    The percentage value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public synchronized String formatPercentage(float value, int decimals,
                                                boolean grouping) {
        getPercentFormat().setMinimumFractionDigits(decimals);
        getPercentFormat().setMaximumFractionDigits(decimals);
        getPercentFormat().setGroupingUsed(grouping);
        return (getPercentFormat().format(value));
    }

    /**
     * Parse a percentage value from a string using the rules of the current
     * locale. The input string may include grouping characters.
     *
     * @param s The string to parse.
     * @return The resulting value.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized double parsePercentage(String s) throws ParseException {
        try {
            return parseCurrency(s.trim(), getPercentFormat());
        } catch (ParseException ex) {
            return (parseDecimal(s) / PERCENT);
        }
    }

    /**
     * Format an integer value according to the rules of the current
     * locale, with grouping turned off.
     *
     * @param value The integer value to format.
     * @return A string representation of the value.
     */

    public String formatInteger(int value) {
        return (formatInteger((long) value));
    }

    /**
     * Format an integer value according to the rules of the current
     * locale, with grouping turned off.
     *
     * @param value The integer value to format.
     * @return A string representation of the value.
     */

    public String formatInteger(long value) {
        return (formatInteger(value, false));
    }

    /**
     * Format an integer value according to the rules of the current
     * locale, and the specified grouping policy.
     *
     * @param value The integer value to format.
     * @return A string representation of the value.
     */

    public String formatInteger(int value, boolean grouping) {
        return (formatInteger((long) value, grouping));
    }

    /**
     * Format an integer value according to the rules of the current
     * locale, and the specified grouping policy.
     *
     * @param value The integer value to format.
     * @return A string representation of the value.
     */

    public synchronized String formatInteger(long value, boolean grouping) {
        getIntegerFormat().setGroupingUsed(grouping);
        return (getIntegerFormat().format(value));
    }

    /**
     * Parse an integer value from a string using the rules of the current
     * locale. The input string may include grouping characters.
     *
     * @param s The string to parse.
     * @return The resulting value.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized long parseInteger(String s) throws ParseException {
        String str = s.trim();
        getIntegerFormat().setGroupingUsed(true);
        pos.setIndex(0);
        Number n = getIntegerFormat().parse(str, pos);
        trapGarbage(str);
        return ((n == null) ? 0 : n.longValue());
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with <code>DEFAULT_NUMBER_DECIMALS</code> decimal places
     * retained, and grouping turned off.
     *
     * @param value The floating point value to format.
     * @return A string representation of the value.
     */

    public String formatDecimal(double value) {
        return (formatDecimal(value, DEFAULT_NUMBER_DECIMALS, false));
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with the specified number of decimal places retained, and
     * grouping turned off.
     *
     * @param value    The floating point value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatDecimal(double value, int decimals) {
        return (formatDecimal(value, decimals, false));
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with the specified number of decimal places retained, and the
     * specified grouping policy.
     *
     * @param value    The floating point value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public synchronized String formatDecimal(double value, int decimals,
                                             boolean grouping) {
        getDecimalFormat().setMinimumFractionDigits(decimals);
        getDecimalFormat().setMaximumFractionDigits(decimals);
        getDecimalFormat().setGroupingUsed(grouping);
        return getDecimalFormat().format(value);
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with <code>DEFAULT_NUMBER_DECIMALS</code> decimal places
     * retained, and grouping turned off.
     *
     * @param value The floating point value to format.
     * @return A string representation of the value.
     */

    public String formatDecimal(float value) {
        return (formatDecimal((double) value));
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with the specified number of decimal places retained, and
     * grouping turned off.
     *
     * @param value    The floating point value to format.
     * @param decimals The number of decimal places to retain.
     * @return A string representation of the value.
     */

    public String formatDecimal(float value, int decimals) {
        return (formatDecimal((double) value, decimals));
    }

    /**
     * Format a floating point value according to the rules of the current
     * locale, with the specified number of decimal places retained, and the
     * specified grouping policy.
     *
     * @param value    The floating point value to format.
     * @param decimals The number of decimal places to retain.
     * @param grouping A flag specifying whether grouping should be used.
     * @return A string representation of the value.
     */

    public String formatDecimal(float value, int decimals, boolean grouping) {
        return (formatDecimal((double) value, decimals, grouping));
    }

    /**
     * Parse a numeric value from a string using the rules of the current
     * locale. The input string may include grouping characters.
     *
     * @param s The string to parse.
     * @return The resulting value.
     * @throws java.text.ParseException If the value could not be parsed.
     */

    public synchronized double parseDecimal(String s) throws ParseException {
        return parseCurrency(s.trim(), getDecimalFormat());
    }

    /**
     * Get an instance of the <code>DateFormatSymbols</code> object for the
     * current locale.
     *
     * @return The <code>DateFormatSymbols</code> object.
     */

    public DateFormatSymbols getDateFormatSymbols() {
        return (dateFormatSymbols);
    }

    /**
     * Get an instance of the <code>Collator</code> object for the current
     * locale.
     *
     * @return The <code>Collator</code> object.
     */

    public Collator getCollator() {
        return collator;
    }

    /**
     * Get a reference to the short date format object for the current locale.
     *
     * @return The <code>DateFormat</code> object.
     * @since Kiwi 1.4
     */

    public DateFormat getShortDateFormat() {
        return (dateFormat[SHORT]);
    }

    /*
     */

    private void trapGarbage(String s) throws ParseException {
        if (pos.getIndex() != s.length()) {
            throw (new ParseException("Garbage in string " + s, pos.getIndex()));
        }
    }

    public NumberFormat getPercentFormat() {
        return percentFormat;
    }

    public void setPercentFormat(NumberFormat percentFormat) {
        this.percentFormat = percentFormat;
    }

    public NumberFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat(NumberFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    public NumberFormat getIntegerFormat() {
        return integerFormat;
    }

    public void setIntegerFormat(NumberFormat integerFormat) {
        this.integerFormat = integerFormat;
    }
}
