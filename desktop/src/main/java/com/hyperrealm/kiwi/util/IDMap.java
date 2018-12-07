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
 * A map that associates numeric IDs (<code>long</code>s) with
 * arbitrary objects. May be used in place of HashMaps when the key is
 * an integer primitive type.
 * <p>
 * Instances of this class do not allocate any objects beyond the two arrays
 * used to store the keys and values. These arrays are automatically resized
 * as necessary. Lookups are performed in O(log N) time, but may incur some
 * overhead to sort the map if it is not sorted at the time of lookup. The
 * map becomes "unsorted" whenever new items are added to it.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1
 */

public class IDMap<T> {
    /**
     * The default initial capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 100;

    private long[] keys;

    private T[] values;

    private int length = 0;

    private int initialCapacity;

    private int capacity;

    private boolean sorted = true;

    /**
     * Construct a new <code>IDMap</code> with the default initial capacity.
     */

    public IDMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Construct a new <code>IDMap</code> with the specified initial capacity.
     *
     * @param initialCapacity The initial capacity.
     */

    public IDMap(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        capacity = initialCapacity;

        keys = new long[capacity];
        values = (T[]) new Object[capacity];
    }

    /**
     * Store a new value in the map. The map becomes "unsorted" as a
     * side-effect of this call; the next call to <code>get()</code> or
     * <code>remove()</code> will force a re-sort of the map.
     *
     * @param key   The key.
     * @param value The value.
     */

    public void put(long key, T value) {
        if (length == capacity) {
            capacity += initialCapacity;

            long[] newKeys = new long[capacity];
            System.arraycopy(keys, 0, newKeys, 0, length);
            keys = newKeys;

            Object[] newValues = new Object[capacity];
            System.arraycopy(values, 0, newValues, 0, length);
            values = (T[]) newValues;
        }

        keys[length] = key;
        values[length] = value;
        length++;
        sorted = false;
    }

    /**
     * Look up a value in the map. If the map is not currently sorted, it will
     * be sorted as a side-effect of this call.
     *
     * @param key The key.
     * @return The associated value object, or <b>null</b> if there is no
     * value associated with the given key.
     */

    public T get(long key) {
        if (!sorted) {
            sort();
        }

        int x = doSearch(key);

        return ((x < 0) ? null : values[x]);
    }

    /**
     * Remove the value associated with the given key from the map. If the map
     * is not currently sorted, it will be sorted as a side-effect of this call.
     * A remove does not cause the map to become "unsorted".
     *
     * @param key The key.
     * @return The object that was removed, or <b>null</b> if there was no
     * value associated with the given key.
     */

    public T remove(long key) {
        if (!sorted) {
            sort();
        }

        int x = doSearch(key);
        if (x < 0) {
            return (null);
        }

        T v = values[x];
        length--;

        for (int i = x; i < length; i++) {
            keys[i] = keys[i + 1];
            values[i] = values[i + 1];
        }

        return (v);
    }

    /**
     * Force a sort of the map.
     */

    public void sort() {
        doSort(0, length);

        sorted = true;
    }

    /**
     * Determine if the map is currently sorted.
     */

    public boolean isSorted() {
        return (true);
    }

    /**
     * Get the size of the map.
     *
     * @return The number of items stored in the map.
     */

    public int size() {
        return (length);
    }

    /**
     * Get the value at the specified index in the map.
     *
     * @param index The index.
     * @return The value at the specified index.
     */

    public T getValueAt(int index) {
        if ((index < 0) || (index >= length)) {
            throw (new IndexOutOfBoundsException());
        }

        return (values[index]);
    }

    /**
     * Get the key at the specified index in the map.
     *
     * @param index The index.
     * @return The key at the specified index.
     */

    public long getKeyAt(int index) {
        if ((index < 0) || (index >= length)) {
            throw (new IndexOutOfBoundsException());
        }

        return (keys[index]);
    }

    /* The following search & sort code was copied from java/util/Arrays.java,
     * but was modified slightly for our purposes. It was not possible to use
     * the methods in java.util.Arrays directly.
     */

    private int doSearch(long key) {
        int low = 0;
        int high = length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            long midVal = keys[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    /*
     */
    @SuppressWarnings("all")
    private void doSort(int off, int len) {
        // Insertion sort on smallest arrays
        if (len < 7) {
            for (int i = off; i < len + off; i++) {
                for (int j = i; j > off && keys[j - 1] > keys[j]; j--) {
                    swap(j, j - 1);
                }
            }
            return;
        }

        // Choose a partition element, v
        int m = off + len / 2;       // Small arrays, middle element
        if (len > 7) {
            int l = off;
            int n = off + len - 1;
            if (len > 40) {        // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3(l, l + s, l + 2 * s);
                m = med3(m - s, m, m + s);
                n = med3(n - 2 * s, n - s, n);
            }
            m = med3(l, m, n); // Mid-size, med of 3
        }
        long v = keys[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while (true) {
            while (b <= c && keys[b] <= v) {
                if (keys[b] == v) {
                    swap(a++, b);
                }
                b++;
            }
            while (c >= b && keys[c] >= v) {
                if (keys[c] == v) {
                    swap(c, d--);
                }
                c--;
            }
            if (b > c) {
                break;
            }
            swap(b++, c--);
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = Math.min(a - off, b - a);
        vecswap(off, b - s, s);
        s = Math.min(d - c, n - d - 1);
        vecswap(b, n - s, s);

        // Recursively sort non-partition-elements
        s = b - a;
        if (s > 1) {
            doSort(off, s);
        }
        s = d - c;
        if (s > 1) {
            doSort(n - s, s);
        }
    }

    private int med3(int a, int b, int c) {
        return (keys[a] < keys[b] ? (keys[b] < keys[c] ? b : keys[a] < keys[c] ? c : a)
            : (keys[b] > keys[c] ? b : keys[a] > keys[c] ? c : a));
    }

    private void vecswap(int a, int b, int n) {
        int aInt = a;
        int bInt = b;

        for (int i = 0; i < n; i++, aInt++, bInt++) {
            swap(aInt, bInt);
        }
    }

    private void swap(int a, int b) {
        long k = keys[a];
        keys[a] = keys[b];
        keys[b] = k;

        T v = values[a];
        values[a] = values[b];
        values[b] = v;
    }

}
