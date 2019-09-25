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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * @author Anton Troshin
 */
public class KCollectionsTest {

    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private final String[] list = new String[]{A, B, C, D, E};
    private final String[] anotherList = new String[]{A, B, C, D, E, F, G};
    private final String[] union = new String[]{A, B, C, D, E, F, G};
    private final String[] diff = new String[]{F, G};

    @Test
    public void search() {
// treat it as unsorted
        search(list, false);
// treat it as sorted
        search(list, true);
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static void search(String[] list, boolean b) {

        assertEquals(1, KCollections.search(Arrays.asList(list), B, b,
            (Comparator<String>) String::compareTo)
        );
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Test
    public void compare() {
        assertFalse(KCollections.compare(Arrays.asList(list), Arrays.asList(anotherList), false,
            Comparator.naturalOrder()
        ));
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Test
    public void union() {
        assertEquals(Arrays.asList(union),
            KCollections.union(Arrays.asList(list), Arrays.asList(anotherList), false,
                (Comparator<String>) String::compareTo
            ));
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Test
    public void intersection() {
        assertEquals(Arrays.asList(list),
            KCollections.intersection(Arrays.asList(list), Arrays.asList(anotherList), false,
                (Comparator<String>) String::compareTo
            ));
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    @Test
    public void difference() {
        assertEquals(Collections.emptyList(),
            KCollections.difference(Arrays.asList(list), Arrays.asList(anotherList), false,
                (Comparator<String>) String::compareTo
            ));
        assertEquals(Arrays.asList(diff),
            KCollections.difference(Arrays.asList(anotherList), Arrays.asList(list), false,
                (Comparator<String>) String::compareTo
            ));
    }

    @Test
    public void add() {
    }
}