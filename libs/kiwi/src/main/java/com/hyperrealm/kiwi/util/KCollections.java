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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that provides methods for performing additional common
 * operations on sorted and unsorted collections.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public final class KCollections {
    /*
     */

    private KCollections() {
    }

    /**
     * Search for an object in a list. If the list is sorted, a binary
     * search is performed. Otherwise, a linear search is performed.
     *
     * @param l          The list to search.
     * @param o          The object to search for.
     * @param sorted     A flag specifying whether the list is sorted.
     * @param comparator The comparator to use.
     * @return The index of the object, or <code>-1</code> if not found.
     */

    public static int search(List l, Object o, boolean sorted, Comparator comparator) {
        return (sorted ? Collections.binarySearch(l, o, comparator)
            : linearSearch(l, o, comparator));
    }

    /**
     * Perform a linear search on a collection.
     *
     * @param c          The collection to search.
     * @param o          The object to search for.
     * @param comparator The comparator to use.
     * @return The index of the object, or <code>-1</code> if not found.
     */

    public static int linearSearch(Collection c, Object o, Comparator comparator) {
        Iterator iter = c.iterator();

        for (int i = 0; iter.hasNext(); i++) {
            Object obj = iter.next();
            if (comparator.compare(o, obj) == 0) {
                return (i);
            }
        }

        return (-1);
    }

    /**
     * Compare two lists. The lists are considered "equal" if both lists
     * contain equivalent elements, but not necessarily in the same order.
     *
     * @param l1         The first list.
     * @param l2         The second list.
     * @param sorted     A flag specifying whether the lists are sorted.
     * @param comparator The comparator to use.
     * @return <code>true</code> if the lists are "equal" and
     * <code>false</code> otherwise.
     */

    public static boolean compare(List l1, List l2, boolean sorted, Comparator comparator) {
        int n = l1.size();
        if (l2.size() != n) {
            return (false);
        }

        for (Object o : l1) {
            if (search(l2, o, sorted, comparator) >= 0) {
                n--;
            }
        }

        return (n == 0);
    }

    /**
     * Compute the union of two lists. The union consists of all items
     * from the two lists that are not in both lists.
     *
     * @param l1         The first list.
     * @param l2         The second list.
     * @param sorted     A flag specifying whether the lists are sorted.
     * @param comparator The comparator to use.
     * @return A list consisting of all items in <code>l1</code> and all
     * items in <code>l2</code> that are not also in <code>l1</code>.
     */

    public static List union(List l1, List l2, boolean sorted,
                             Comparator comparator) {
        List list = new ArrayList();

        add(list, l1);

        for (Object o : l2) {
            if (search(l1, o, sorted, comparator) < 0) {
                list.add(o);
            }
        }

        return list;
    }

    /**
     * Compute the intersection of two lists. The intersection of two lists
     * consists of all the elements that appear in both lists.
     *
     * @param l1         The first list.
     * @param l2         The second list.
     * @param sorted     A flag specifying whether the lists are sorted.
     * @param comparator The comparator to use.
     * @return A list that includes all items that are in both <code>l1</code>
     * and <code>l2</code>.
     */

    public static List intersection(List l1, List l2, boolean sorted,
                                    Comparator comparator) {
        List list = new ArrayList();

        for (Object o : l1) {
            if (search(l2, o, sorted, comparator) >= 0) {
                list.add(o);
            }
        }

        return (list);
    }

    /**
     * Compute the difference between two lists. The difference
     * consists of all items in one list that are not in the other
     * list.
     *
     * @param l1         The first list.
     * @param l2         The second list.
     * @param sorted     A flag specifying whether the lists are sorted.
     * @param comparator The comparator to use.
     * @return A list that includes all elements from <code>l1</code> that are
     * not in <code>l2</code>.
     */

    public static List difference(List l1, List l2, boolean sorted,
                                  Comparator comparator) {
        List list = new ArrayList();

        for (Object o : l1) {
            if (search(l2, o, sorted, comparator) < 0) {
                list.add(o);
            }
        }

        return (list);
    }

    /**
     * Add the elements from one collection to another collection.
     *
     * @param c1 The original collection.
     * @param c2 The collection of items to add to <code>c1</code>.
     * @return <code>c1</code>
     */

    public static Collection add(Collection c1, Collection c2) {
        if (!((c2 == null) || (c2.size() == 0) || (c1 == null))) {
            c1.addAll(c2);
        }

        return (c1);
    }

}
