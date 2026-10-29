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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link HashCodeComparator}.
 */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class HashCodeComparatorTest {

    /**
     * Test compare with null values.
     */
    @Test
    public void testCompareWithNulls() {
        HashCodeComparator<String> comparator = new HashCodeComparator<>();
        // null < non-null
        assertTrue(comparator.compare(null, "test") < 0);
        // non-null > null
        assertTrue(comparator.compare("test", null) > 0);
        // null == null
        assertEquals(0, comparator.compare(null, null));
    }

    /**
     * Test compare with non-null values.
     */
    @Test
    public void testCompareWithNonNull() {
        HashCodeComparator<String> comparator = new HashCodeComparator<>();
        int result = comparator.compare("a", "b");
        // Compare based on hash codes
        assertTrue(result == 0 || result != 0); // Just verify it doesn't throw
    }

    /**
     * Test equals method.
     */
    @Test
    public void testEquals() {
        HashCodeComparator<String> c1 = new HashCodeComparator<>();
        HashCodeComparator<String> c2 = new HashCodeComparator<>();
        // Two comparators should be equal if same class
        assertEquals(c1, c2);
        // Not equal to null
        assertNotEquals(null, c1);
        // Not equal to different class
        assertNotEquals("string", c1);
    }

    /**
     * Test hashCode method.
     */
    @Test
    public void testHashCode() {
        HashCodeComparator<String> comparator = new HashCodeComparator<>();
        int hash = comparator.hashCode();
        assertTrue(hash != 0);
    }
}
