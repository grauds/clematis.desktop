package com.hyperrealm.kiwi.util;
/* ----------------------------------------------------------------------------\
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
   ----------------------------------------------------------------------------\
*/

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link Counter}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class CounterTest {

    /**
     * Test increment operation.
     */
    @Test
    public void testIncrement() {
        Counter counter = new Counter();
        assertEquals(0, counter.getValue());
        counter.increment();
        assertEquals(1, counter.getValue());
        counter.increment();
        counter.increment();
        assertEquals(3, counter.getValue());
    }

    /**
     * Test decrement operation.
     */
    @Test
    public void testDecrement() {
        Counter counter = new Counter(5);
        assertEquals(5, counter.getValue());
        counter.decrement();
        assertEquals(4, counter.getValue());
        counter.decrement();
        counter.decrement();
        counter.decrement();
        counter.decrement();
        assertEquals(0, counter.getValue());
    }

    /**
     * Test initial value.
     */
    @Test
    public void testInitialValue() {
        Counter defaultCounter = new Counter();
        assertEquals(0, defaultCounter.getValue());

        Counter customCounter = new Counter(42);
        assertEquals(42, customCounter.getValue());

        Counter negativeCounter = new Counter(-10);
        assertEquals(-10, negativeCounter.getValue());
    }

    /**
     * Test increment then decrement.
     */
    @Test
    public void testIncrementThenDecrement() {
        Counter counter = new Counter(10);
        counter.increment();
        assertEquals(11, counter.getValue());
        counter.decrement();
        assertEquals(10, counter.getValue());
    }
}
