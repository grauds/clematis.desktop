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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A reference-counted set for arbitrary keys. A given key can be
 * inserted into the set multiple times, but only the first such
 * insert actually adds the key to the set, whereas all subsequent
 * inserts simply increment a counter for that key. Removing a key
 * decrements the counter for that key, and, if the counter's value
 * becomes 0 as a result, the key is removed from the set.
 *
 * @param <K>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class RefSet<K> {

    private Map<K, Counter> map = new HashMap<K, Counter>();

    /**
     * Construct a new, empty <code>RefSet</code>.
     */

    public RefSet() {
    }

    /**
     * Insert a key into the set, and set its reference count to 1. If
     * the key was already in the set, its reference count is incremented
     * by 1.
     *
     * @param key The key to insert.
     * @return <b>true</b> if the key was added to the set, and <b>false</b> if it
     * was already in the set.
     */

    public boolean insert(K key) {
        Counter val = map.get(key);
        if (val == null) {
            val = new Counter(1);
            map.put(key, val);
            return (true);
        } else {
            val.increment();
            return (false);
        }
    }

    /**
     * Remove a key from the set. The reference count for the key is decremented,
     * and if it reaches 0, the key is removed from the set.
     *
     * @param key The key to remove from the set.
     * @return <b>true</b> if the key was removed from the set, and <b>false</b>
     * if its reference counter was merely decremented.
     */

    public boolean remove(K key) {
        Counter val = map.get(key);
        if (val != null) {
            if (val.decrement() == 0) {
                map.remove(key);
                return (true);
            }
        }
        return false;
    }

    /**
     * Remove all keys from the set.
     */

    public void clear() {
        map.clear();
    }

    /**
     * Determine if a key is in the set.
     *
     * @param key The key.
     * @return <b>true</b> if the key exists in the set, and <b>false</b>
     * otherwise.
     * @since Kiwi 2.0.1
     */

    public boolean contains(K key) {
        return (map.containsKey(key));
    }

    /**
     * Get a list of keys in the set.
     *
     * @return The (immutable) set of keys.
     * @since Kiwi 2.0.1
     */

    public Set<K> keySet() {
        return (Collections.unmodifiableSet(map.keySet()));
    }

}
