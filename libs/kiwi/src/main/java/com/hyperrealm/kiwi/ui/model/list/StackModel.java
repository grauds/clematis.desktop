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

package com.hyperrealm.kiwi.ui.model.list;

import java.util.EmptyStackException;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * An interface describing the data model for a stack. This interface extends
 * <code>ListModel</code> as a stack is essentially a list with special
 * semantics.
 *
 * @param <T>
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.StackView
 */

public interface StackModel<T> extends ListModel<T> {

    /**
     * Push an object on the stack. The object becomes the topmost item on the
     * stack.
     *
     * @param obj The object to push.
     */

    void push(T obj);

    /**
     * Pop an object off the stack. Pulls the topmost item off the stack.
     *
     * @return The popped object.
     */

    T pop() throws EmptyStackException;

    /**
     * Drop an item off the stack. Pops and discards the topmost item on the
     * stack.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    void drop();

    /**
     * Retrieve the topmost item from the stack (without removing the item from
     * the stack).
     *
     * @return The topmost item on the stack.
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    T peek() throws ArrayIndexOutOfBoundsException;

    /**
     * Get the depth of the stack.
     *
     * @return The number of items in the stack.
     */

    int getDepth();

    /**
     * Swap the topmost items on the stack. If the stack contains only one item,
     * calling this method has no effect.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    void swap() throws EmptyStackException;

    /**
     * Determine if the stack is empty.
     *
     * @return <code>true</code> if there are no items in the stack, and
     * <code>false</code> otherwise.
     */

    boolean isEmpty();

    /**
     * Remove an object from the stack. Retrieves (and removes) an object from
     * the given offset in the stack.
     *
     * @param index The offset (from the top of the stack) of the item to remove.
     * @return The object that was removed.
     * @throws java.util.EmptyStackException  If the stack is empty.
     * @throws ArrayIndexOutOfBoundsException If the value of
     *                                        <code>index</code> is out of range.
     */

    T pick(int index) throws ArrayIndexOutOfBoundsException,
        EmptyStackException;

    /**
     * Append an object to the bottom of the stack.
     *
     * @param obj The object to append.
     */

    void append(T obj);

    /**
     * Add a <code>ListDataListener</code> to this model's list of listeners.
     * Since a stack is essentially a list with some special semantics,
     * <code>ListDataListeners</code> are used.
     *
     * @param listener The listener to add.
     */

    void addStackDataListener(ListDataListener listener);

    /**
     * Remove a <code>ListDataListener</code> from this model's list of
     * listeners. Since a stack is essentially a list with some special
     * semantics, <code>ListDataListeners</code> are used.
     *
     * @param listener The listener to add.
     */

    void removeStackDataListener(ListDataListener listener);
}
