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

import java.util.EmptyStackException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link Stack}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class StackTest {

    /**
     * Test push, pop, and peek operations.
     */
    @Test
    public void testPushPopPeek() {
        Stack<String> stack = new Stack<>();
        stack.push("one");
        stack.push("two");
        stack.push("three");

        assertEquals("three", stack.peek());
        assertEquals("three", stack.pop());
        assertEquals("two", stack.pop());
        assertEquals("one", stack.pop());
    }

    /**
     * Test empty stack.
     */
    @Test
    public void testEmpty() {
        Stack<String> stack = new Stack<>();
        assertTrue(stack.empty());
        assertEquals(0, stack.size());

        stack.push("item");
        assertFalse(stack.empty());
    }

    /**
     * Test pop from empty stack throws exception.
     */
    @Test
    public void testPopEmptyThrows() {
        Stack<String> stack = new Stack<>();
        assertThrows(EmptyStackException.class, stack::pop);
    }

    /**
     * Test peek from empty stack throws exception.
     */
    @Test
    public void testPeekEmptyThrows() {
        Stack<String> stack = new Stack<>();
        assertThrows(EmptyStackException.class, stack::peek);
    }

    /**
     * Test search method.
     */
    @Test
    public void testSearch() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertEquals(2, stack.search("c")); // Top of stack
        assertEquals(1, stack.search("b"));
        assertEquals(0, stack.search("a"));
        assertEquals(-1, stack.search("d"));
    }

    /**
     * Test edge cases.
     */
    @Test
    public void testEdgeCases() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        Optional<Integer> result = Optional.of(stack.pop());
        assertEquals(Integer.valueOf(1), result.orElse(null));
        assertTrue(stack.empty());

        // Test with null values
        Stack<String> nullStack = new Stack<>();
        nullStack.push(null);
        assertEquals(1, nullStack.size());
        assertNull(nullStack.pop());
    }
}
