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
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link LRUCache}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class LRUCacheTest {

    /**
     * Test put and get operations.
     */
    @Test
    public void testPutGet() {
        LRUCache<String, String> cache = new LRUCache<>(10);
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
    }

    /**
     * Test LRU eviction when cache is full.
     */
    @Test
    public void testLRUEviction() {
        LRUCache<String, String> cache = new LRUCache<>(3);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        // Adding 'd' should evict 'a' (least recently used)
        cache.put("d", "4");
        assertNull(cache.get("a"));
        assertEquals("2", cache.get("b"));
        assertEquals("3", cache.get("c"));
        assertEquals("4", cache.get("d"));
    }

    /**
     * Test that access order affects LRU.
     */
    @Test
    public void testAccessOrder() {
        LRUCache<String, String> cache = new LRUCache<>(3);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        // Access 'a' to make it most recently used
        cache.get("a");
        // Adding 'd' should evict 'b' (least recently used)
        cache.put("d", "4");
        assertEquals("1", cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals("3", cache.get("c"));
        assertEquals("4", cache.get("d"));
    }

    /**
     * Test maxSize behavior.
     */
    @Test
    public void testMaxSize() {
        LRUCache<String, String> cache = new LRUCache<>(2);
        cache.put("a", "1");
        cache.put("b", "2");
        assertEquals(2, cache.size());
        cache.put("c", "3");
        assertEquals(2, cache.size());
    }

    /**
     * Test itemDropped callback.
     */
    @Test
    public void testItemDroppedCallback() {
        final String[] dropped = {
            null
        };
        LRUCache<String, String> cache = new LRUCache<>(3) {
            @Override
            protected void itemDropped(String item) {
                dropped[0] = item;
            }
        };
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        cache.put("d", "4"); // Should drop 'a'
        assertEquals("1", dropped[0]);
    }
}
