package com.hyperrealm.kiwi.util;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Anton Troshin
 */
public class RefSetTest {

    private static final String TEST_STRING = "test";

    @Test
    public void trivialTest() {

        RefSet<String> testSet = new RefSet<>();
        assertTrue(testSet.insert(TEST_STRING));
        assertFalse(testSet.insert(TEST_STRING));
        assertTrue(testSet.contains(TEST_STRING));
        assertFalse(testSet.remove(TEST_STRING));
        assertTrue(testSet.remove(TEST_STRING));

        testSet.clear();

        assertTrue(testSet.keySet().isEmpty());
    }
}