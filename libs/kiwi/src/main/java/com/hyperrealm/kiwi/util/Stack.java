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
import java.util.EmptyStackException;

/**
 * An unsynchronized replacement for <code>java.util.Stack</code>.
 *
 * @param <E>
 * @author Mark Lindner
 * @since Kiwi 2.1
 */

public class Stack<E> extends ArrayList<E> {
    /**
     * Construct a new <code>Stack</code> with the given initial capacity.
     *
     * @param initialCapacity The initial capacity.
     */

    public Stack(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Construct a new <code>Stack</code>.
     */

    public Stack() {
        super();
    }

    /**
     * Determine if the stack is empty.
     */

    public boolean empty() {
        return (size() == 0);
    }

    /**
     * Push an item onto the stack.
     *
     * @param item The item.
     * @return The item.
     */

    public E push(E item) {
        add(item);

        return (item);
    }

    /**
     * Pop the topmost item off the stack.
     *
     * @return The item.
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public E pop() throws EmptyStackException {
        int s = size();
        if (s == 0) {
            throw (new EmptyStackException());
        }

        return (remove(--s));
    }

    /**
     * Fetch the topmost item from the stack, without removing it from
     * the stack.
     *
     * @return The item.
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public E peek() throws EmptyStackException {
        int s = size();
        if (s == 0) {
            throw (new EmptyStackException());
        }

        return (get(--s));
    }

    /**
     * Search for an item on the stack.
     *
     * @param item The item.
     * @return The index of the item, or -1 if not found.
     */

    public int search(E item) {
        return (indexOf(item));
    }

}
