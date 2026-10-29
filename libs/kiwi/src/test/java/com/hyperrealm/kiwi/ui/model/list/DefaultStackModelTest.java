package com.hyperrealm.kiwi.ui.model.list;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultStackModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class DefaultStackModelTest {

    /**
     * Test push/pop.
     */
    @Test
    public void testPushPop() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("one");
        model.push("two");
        model.push("three");
        assertEquals("three", model.pop());
        assertEquals("two", model.pop());
        assertEquals("one", model.pop());
    }

    /**
     * Test isEmpty.
     */
    @Test
    public void testIsEmpty() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        assertTrue(model.isEmpty());
        model.push("item");
        assertFalse(model.isEmpty());
    }

    /**
     * Test peek.
     */
    @Test
    public void testPeek() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("top");
        assertEquals("top", model.peek());
        assertEquals(1, model.getDepth()); // Stack still has item
    }

    /**
     * Test pop from empty stack throws.
     */
    @Test
    public void testPopEmptyThrows() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        assertThrows(EmptyStackException.class, model::pop);
    }

    /**
     * Test peek from empty stack throws.
     */
    @Test
    public void testPeekEmptyThrows() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        assertThrows(EmptyStackException.class, model::peek);
    }

    /**
     * Test drop.
     */
    @Test
    public void testDrop() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("one");
        model.drop();
        assertTrue(model.isEmpty());
    }

    /**
     * Test swap.
     */
    @Test
    public void testSwap() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("one");
        model.push("two");
        model.swap();
        assertEquals("one", model.peek());
    }

    /**
     * Test swap with single item throws.
     */
    @Test
    public void testSwapSingleThrows() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("one");
        assertThrows(EmptyStackException.class, model::swap);
    }

    /**
     * Test append.
     */
    @Test
    public void testAppend() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("top");
        model.append("bottom");
        assertEquals(2, model.getDepth());
        assertEquals("top", model.peek());
    }

    /**
     * Test pick.
     */
    @Test
    public void testPick() {
        DefaultStackModel<String> model = new DefaultStackModel<>();
        model.push("one");
        model.push("two");
        String picked = model.pick(0);
        assertEquals("two", picked);
        assertEquals(1, model.getDepth());
    }
}
