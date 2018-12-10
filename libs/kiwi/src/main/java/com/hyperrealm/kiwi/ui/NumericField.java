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

import java.text.ParseException;

import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A subclass of <code>DataField</code> for the input and display of
 * specialized data values, such as currency amounts, percentages, and decimal
 * values.
 *
 * <p><center>
 * <img src="snapshot/NumericField.gif"><br>
 * <i>An example NumericField.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class NumericField extends DataField<Number> {

    private static LocaleManager lm = LocaleManager.getDefault();

    private int type, decimals = 2;

    private double value = 0.0, maxValue, minValue;

    private boolean hasMaxValue = false, hasMinValue = false, grouping = true;

    /**
     * Construct a new <code>NumericField</code> of the specified width and
     * a default type of <code>DECIMAL_FORMAT</code>.
     *
     * @param width The width of the field.
     */

    public NumericField(int width) {
        this(width, DECIMAL_FORMAT);
    }

    /**
     * Construct a new <code>NumericField</code> of the specified width, for the
     * specified value type.
     *
     * @param width The width of the field.
     * @param type  A validation type; one of the format constants defined in the
     *              <code>FormatConstants</code> class.
     * @see com.hyperrealm.kiwi.text.FormatConstants
     */

    public NumericField(int width, int type) {
        super(width);

        setType(type);
        setHorizontalAlignment(SwingConstants.RIGHT);
        setFont(KiwiUtils.BOLD_FONT);
    }

    /**
     * @since Kiwi 2.1
     */

    public Number getObject() {
        return getValue();
    }

    /**
     * @since Kiwi 2.1
     */

    public void setObject(Number value) {
        setValue(value.doubleValue());
    }

    /**
     * Get the validation type for this field.
     *
     * @return The validation type.
     */

    public int getType() {
        return (type);
    }

    /**
     * Set the validation type for this field.
     *
     * @param type A validation type; one of the format constants defined in the
     *             <code>FormatConstants</code> class.
     * @see com.hyperrealm.kiwi.text.FormatConstants
     */

    public void setType(int type) {
        this.type = type;

        if (type == INTEGER_FORMAT) {
            setDecimals(0);
        } else {
            setDecimals(2);
        }

        validateInput();
    }

    /**
     * Get the value from the field. The value returned is the value that
     * was parsed by the last call to <code>validateInput()</code>.
     *
     * @return The parsed value, or 0.0 if the last call to
     * <code>validateInput()</code> resulted in a parsing error, or if there was
     * no previous call to <code>validateInput()</code>.
     * @see #validateInput
     */

    public synchronized double getValue() {
        String text = getText().trim();

        try {
            switch (type) {
                case CURRENCY_FORMAT:
                    value = lm.parseCurrency(text);
                    break;

                case PERCENTAGE_FORMAT:
                    value = lm.parsePercentage(text);
                    break;

                case INTEGER_FORMAT:
                    value = (double) lm.parseInteger(text);
                    break;

                case DECIMAL_FORMAT:
                default:
                    value = lm.parseDecimal(text);
                    break;
            }

            if (hasMinValue && (value < minValue)) {
                invalid = true;
            }

            if (hasMaxValue && (value > maxValue)) {
                invalid = true;
            }
        } catch (ParseException ex) {
            invalid = true;
        }

        return (value);
    }

    /**
     * Set the numeric value to be displayed by this field. The value is
     * formatted as a string, according to the rules of the current locale,
     * and displayed in the field. Invalid input flagging is automatically
     * turned off.
     *
     * @param value The value.
     */
    @SuppressWarnings("all")
    public synchronized void setValue(double value) {
        this.value = value;
        String s = "?";

        switch (type) {
            case CURRENCY_FORMAT:
                s = lm.formatCurrency(value, decimals, grouping);
                break;

            case PERCENTAGE_FORMAT:
                s = lm.formatPercentage(value, decimals, grouping);
                break;

            case INTEGER_FORMAT:
                s = lm.formatInteger((long) value, grouping);
                break;

            case DECIMAL_FORMAT:
            default:
                s = lm.formatDecimal(value, decimals, grouping);
                break;
        }

        setText(s);
        invalid = false;
        paintInvalid(invalid);
    }

    /**
     * Set the numeric value to be displayed by this field.
     *
     * @param value The value. This value is cast internally to a double.
     */

    public void setValue(float value) {
        setValue((double) value);
    }

    /**
     * Set the numeric value to be displayed by this field.
     *
     * @param value The value. This value is cast internally to a double.
     */

    public void setValue(int value) {
        setValue((double) value);
    }

    /**
     * Set the numeric value to be displayed by this field.
     *
     * @param value The value. This value is cast internally to a double.
     */

    public void setValue(long value) {
        setValue((double) value);
    }

    /**
     * Set the numeric value to be displayed by this field.
     *
     * @param value The value. This value is cast internally to a double.
     */

    public void setValue(short value) {
        setValue((double) value);
    }

    /**
     * Validate the input in this field.
     *
     * @return <code>true</code> if the field contains valid input, and
     * <code>false</code> otherwise.
     */

    protected boolean checkInput() {
        invalid = false;

        double v = getValue();

        if (!invalid) {
            setValue(v);
        }

        paintInvalid(invalid);

        return (!invalid);
    }

    /**
     * Get the number of decimals being displayed to the right of the radix.
     *
     * @return The decimal count.
     */

    public int getDecimals() {
        return (decimals);
    }

    /**
     * Set the number of decimals to display to the right of the radix. The
     * default is 2.
     *
     * @param decimals The new decimal count.
     */

    public void setDecimals(int decimals) {
        if (decimals < 0) {
            this.decimals = 0;
        } else {
            this.decimals = decimals;
        }
    }

    /**
     * Set a maximum value constraint. If a value is entered that is greater
     * than the maximum value, the input will not validate.
     *
     * @param value The new maximum value.
     */

    public void setMaxValue(double value) {
        maxValue = value;
        hasMaxValue = true;
    }

    /**
     * Set a maximum value constraint. If a value is entered that is greater
     * than the maximum value, the input will not validate.
     *
     * @param value The new maximum value.
     * @since Kiwi 1.3
     */

    public void setMaxValue(int value) {
        setMaxValue((double) value);
    }

    /**
     * Clear the maximum value constraint.
     */

    public void clearMaxValue() {
        hasMaxValue = false;
    }

    /**
     * Set a minimum value constraint. If a value is entered that is less than
     * the minimum value, the input will not validate.
     *
     * @param value The new minimum value.
     */

    public void setMinValue(double value) {
        minValue = value;
        hasMinValue = true;
    }

    /**
     * Set a minimum value constraint. If a value is entered that is less than
     * the minimum value, the input will not validate.
     *
     * @param value The new minimum value.
     * @since Kiwi 1.3
     */

    public void setMinValue(int value) {
        setMinValue((double) value);
    }

    /**
     * Clear the minimum value constraint.
     */

    public void clearMinValue() {
        hasMinValue = false;
    }

    /**
     * Get the grouping mode for this numeric field.
     *
     * @return The current grouping mode.
     */

    public boolean getGrouping() {
        return (grouping);
    }

    /**
     * Set the grouping mode for this numeric field. If grouping is turned
     * off, values will be formatted without grouping characters separating the
     * thousands. The default mode is on.
     *
     * @param grouping A flag specifying whether grouping should be on
     *                 (<code>true</code>) or off (<code>false</code>).
     */

    public void setGrouping(boolean grouping) {
        this.grouping = grouping;
    }

}
