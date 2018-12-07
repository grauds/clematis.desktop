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

/**
 * A class representing a string of bits, useful for maintaining a set of flags
 * in compact form.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 * @deprecated ?
 */
@SuppressWarnings("all")
public final class BitString {

    private static final int WORD_LENGTH = 8;
    /**
     * The bit string data.
     */
    protected byte[] array;
    /**
     * The length of the bit string, in bits.
     */
    protected int length;

    /**
     * Construct a new <code>BitString</code> of the specified length.
     *
     * @param length The length.
     */

    public BitString(int length) {
        if (length < 0) {
            throw (new IllegalArgumentException());
        }

        this.length = length;

        int len = length / WORD_LENGTH;
        if ((length % WORD_LENGTH) > 0) {
            len++;
        }

        array = new byte[len];

        clearAll();
    }

    /**
     * Construct a new <code>BitString</code> of the specified length, with the
     * bits initialized to either the on or off position.
     *
     * @param length       The length.
     * @param setInitially A flag indicating whether all bits should be initially
     *                     set or cleared.
     */

    public BitString(int length, boolean setInitially) {
        this(length);

        if (setInitially) {
            setAll();
        }
    }

    /**
     * Get the length of the <code>BitString</code>.
     */

    public int getLength() {
        return (length);
    }

    /**
     * Clear all of the bits.
     */

    public void clearAll() {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    /**
     * Set all of the bits.
     */

    public void setAll() {
        for (int i = 0; i < array.length; i++) {
            array[i] = ~0;
        }
    }

    /**
     * Set the specified bit.
     *
     * @param bit The index of the bit to set.
     * @throws IllegalArgumentException If the bit index is out of
     *                                  range.
     */

    public void set(int bit) {
        if (bit < 0) {
            throw (new IllegalArgumentException());
        }

        int _byte = bit / WORD_LENGTH;
        int _bit = bit % WORD_LENGTH;

        if (_byte >= array.length) {
            throw (new IllegalArgumentException());
        }

        array[_byte] |= (1 << _bit);
    }

    /**
     * Test the specified bit.
     *
     * @param bit The index of the bit to test.
     * @return <code>true</code> if the bit is set, <code>false</code>
     * otherwise.
     * @throws IllegalArgumentException If the bit index is out of
     *                                  range.
     */

    public boolean isSet(int bit) {
        if (bit < 0) {
            throw (new IllegalArgumentException());
        }

        int _byte = bit / WORD_LENGTH;
        int _bit = bit % WORD_LENGTH;

        if (_byte >= array.length) {
            throw (new IllegalArgumentException());
        }

        return ((array[_byte] & (1 << _bit)) != 0);
    }

    /**
     * Test the specified bit.
     *
     * @param bit The index of the bit to test.
     * @return <code>true</code> if the bit is cleared, <code>false</code>
     * otherwise.
     * @throws IllegalArgumentException If the bit index is out of
     *                                  range.
     */

    public boolean isClear(int bit) {
        return (!isSet(bit));
    }

    /**
     * Clear the specified bit.
     *
     * @param bit The index of the bit to clear.
     * @throws IllegalArgumentException If the bit index is out of
     *                                  range.
     */

    public void clear(int bit) {
        if (bit < 0) {
            throw (new IllegalArgumentException());
        }

        int _byte = bit / WORD_LENGTH;
        int _bit = bit % WORD_LENGTH;

        if (_byte >= array.length) {
            throw (new IllegalArgumentException());
        }

        array[_byte] &= ~(1 << _bit);
    }

    /**
     * Compare this <code>BitString</code> to another. The method
     * returns <code>true</code> if the bits that are set in this
     * <code>BitString</code> are also set in the other
     * <code>BitString</code>. (This is not a test for equality; to test
     * if two <code>BitStrings</code> have exactly the same bits set,
     * use <code>equals()</code>.)
     *
     * @param other The <code>BitString</code> to compare against.
     * @return <code>true</code> for a match, <code>false</code> otherwise.
     */

    public boolean compareTo(BitString other) {
        if (length != other.length) {
            return (false);
        }

        boolean r = false;

        for (int i = 0; i < array.length; i++) {
            r |= ((array[i] & other.array[i]) != array[i]);
        }

        return (r);
    }

    /**
     * Determine if this object is identical to another object.
     */

    public boolean equals(Object other) {
        if (!(other instanceof BitString)) {
            return (false);
        }

        BitString otherbs = (BitString) other;

        if (otherbs.length != length) {
            return (false);
        }

        for (int i = 0; i < length; i++) {
            if (otherbs.array[i] != array[i]) {
                return (false);
            }
        }

        return (true);
    }

    /**
     * Produce a string representation of the object.
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();

        int _byte = 0;
        int _bit = 0;

        for (int i = 0; i < length; i++) {
            sb.append(((array[_byte] & (1 << _bit)) == 0) ? '0' : '1');
            _bit++;
            if (_bit == WORD_LENGTH) {
                _byte++;
                _bit = 0;
            }
        }

        return (sb.toString());
    }

}
