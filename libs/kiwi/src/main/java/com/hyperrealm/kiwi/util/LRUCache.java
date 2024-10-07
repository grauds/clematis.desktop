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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple LRU cache.
 *
 * @param <K>
 * @param <V>
 * @author Mark Lindner
 * @since Kiwi 2.1.4
 */

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private static final float LOAD_FACTOR = 0.75f;

    private static final int INITIAL_CAPACITY = 100;

    private final int maxSize;

    /**
     * Construct a new <code>LRUCache</code> with the given initial capacity
     * and maximum size.
     *
     * @param initialCapacity The initial capacity.
     * @param maxSize         The maximum size; that is, the maximum number of items
     *                        that can ever be in the cache.
     */

    public LRUCache(int initialCapacity, int maxSize) {
        super(initialCapacity, LOAD_FACTOR, true);

        this.maxSize = maxSize;
    }

    /**
     * Construct a new <code>LRUCache</code> with a default initial capacity
     * of 100 and the given maximum size.
     *
     * @param maxSize The maximum size; that is, the maximum number of items
     *                that can ever be in the cache.
     */

    public LRUCache(int maxSize) {
        this(INITIAL_CAPACITY, maxSize);
    }

    /**
     *
     */
    protected final boolean removeEldestEntry(Map.Entry<K, V> oldest) {
        if (size() > maxSize) {
            itemDropped(oldest.getValue());
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * This method is called whenever the oldest item is removed from
     * the cache to make room for a new item. The default implementation
     * does nothing.
     *
     * @param item The item that was removed.
     */

    protected void itemDropped(V item) {}

}
