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


import lombok.EqualsAndHashCode;

/**
 * A base class for problem domain objects; the class provides some
 * rudimentary data validation methods that are intended for use by
 * mutators. For example, if a domain object has a member called 'lastName'
 * which cannot be a null or empty string, the mutator can be implemented as
 * follows:
 * <p>
 * <pre>
 * public void setLastName(String lastName) throws MutatorException
 * {
 *   this.lastName = ensureNotNull(lastName, "Last Name");
 * }
 * </pre>
 *
 * @author Mark Lindner
 */
@EqualsAndHashCode
public abstract class DomainObject {

    private static final String KIWI_DB = "KiwiDB";

    /**
     * Construct a new <code>DomainObject</code>.
     */

    public DomainObject() {
    }

    private static void nullWarning(String fieldName) throws MutatorException {
        String msg = LocaleManager.getDefault()
            .getLocaleData(KIWI_DB).getMessage("kiwi.db.message.not_null",
                fieldName);

        throw (new MutatorException(msg));
    }

    private static void positiveWarning(String fieldName)
        throws MutatorException {
        String msg = LocaleManager.getDefault()
            .getLocaleData(KIWI_DB).getMessage("kiwi.db.message.greater_than_zero",
                fieldName);

        throw (new MutatorException(msg));
    }

    private static void negativeWarning(String fieldName)
        throws MutatorException {
        String msg = LocaleManager.getDefault()
            .getLocaleData(KIWI_DB).getMessage("kiwi.db.message.not_negative",
                fieldName);

        throw (new MutatorException(msg));
    }

    private static void selectionWarning(String fieldName)
        throws MutatorException {
        String msg = LocaleManager.getDefault()
            .getLocaleData(KIWI_DB).getMessage("kiwi.db.message.no_selection",
                fieldName);

        throw (new MutatorException(msg));
    }

    /**
     * Ensure that a String value is not <code>null</code>.
     *
     * @param s The string check.
     * @return An empty string if <code>s</code> is <code>null</code>,
     * and <code>s</code> otherwise.
     */

    protected String trapNull(String s) {
        return ((s == null) ? "" : s);
    }

    /**
     * Ensure that an integer value is greater than zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public int ensurePositive(int value, String desc) throws MutatorException {
        if (value <= 0) {
            positiveWarning(desc);
        }

        return (value);
    }

    /**
     * Ensure that a floating point value is greater than zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public float ensurePositive(float value, String desc) throws MutatorException {
        if (value <= 0.0) {
            positiveWarning(desc);
        }

        return (value);
    }

    /**
     * Ensure that a double precision value is greater than zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public double ensurePositive(double value, String desc)
        throws MutatorException {
        if (value <= 0.0) {
            positiveWarning(desc);
        }

        return (value);
    }

    /* Generate a null value warning. */

    /**
     * Ensure that an integer value is greater than or equal to zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public int ensureNonNegative(int value, String desc) throws MutatorException {
        if (value < 0) {
            negativeWarning(desc);
        }

        return (value);
    }

    /* Generate a <= 0 value warning. */

    /**
     * Ensure that a floating point value is greater than or equal to zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public float ensureNonNegative(float value, String desc)
        throws MutatorException {
        if (value < 0.0) {
            negativeWarning(desc);
        }

        return (value);
    }

    /* Generate a < 0 value warning. */

    /**
     * Ensure that a double precision value is greater than or equal to zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public double ensureNonNegative(double value, String desc)
        throws MutatorException {
        if (value < 0.0) {
            negativeWarning(desc);
        }

        return (value);
    }

    /* Generate an item selection warning. */

    /**
     * Ensure that a String is neither null nor of length zero.
     *
     * @param value The value to check.
     * @param desc  A description of the field that this value represents.
, 
     */

    public String ensureNotNull(String value, String desc) throws MutatorException {

        if (value == null) {
            nullWarning(desc);
        } else if (value.trim().length() == 0) {
            nullWarning(desc);
        }

        return value;
    }
}
