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

package com.hyperrealm.kiwi.ui.model;

import java.util.EmptyStackException;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

/**
 * A default implementation of the <code>StackModel</code>
 * interface. It inherits its base functionality from
 * <code>DefaultListModel</code>, and adds only methods for dealing
 * with stack operations.
 *
 * @param <T>
 * @author Mark Lindner
 */

public class DefaultStackModel<T> extends DefaultListModel implements StackModel<T> {

    /**
     * Construct a new <code>DefaultStackModel</code>.
     */

    public DefaultStackModel() {
    }

    /**
     * Push an object on the stack. The object becomes the topmost item on the
     * stack.
     *
     * @param obj The object to push.
     */

    public void push(T obj) {
        insertElementAt(obj, 0);
        fireIntervalAdded(this, 0, 0);
    }

    /**
     * Pop an object off the stack. Pulls the topmost item off the stack.
     *
     * @return The popped object.
     */

    public T pop() throws EmptyStackException {
        return (pick(0));
    }

    /**
     * Drop an item off the stack. Pops and discards the topmost item on the
     * stack.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public void drop() throws EmptyStackException {
        pop(); // discard return value
    }

    /**
     * Retrieve the topmost item from the stack (without removing the item from
     * the stack).
     *
     * @return The topmost item on the stack.
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public T peek() throws EmptyStackException {
        if (isEmpty()) {
            throw (new EmptyStackException());
        }

        return ((T) get(0));
    }

    /**
     * Get the depth of the stack.
     *
     * @return The number of items in the stack.
     */

    public int getDepth() {
        return (getSize());
    }

    /**
     * Swap the topmost items on the stack. If the stack contains only one item,
     * calling this method has no effect.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public void swap() throws EmptyStackException {
        if (getDepth() < 2) {
            throw (new EmptyStackException());
        }

        T a = pop();
        T b = pop();
        push(a);
        push(b);
    }

    /**
     * Determine if the stack is empty.
     *
     * @return <code>true</code> if there are no items in the stack, and
     * <code>false</code> otherwise.
     */

    public boolean isEmpty() {
        return (getSize() == 0);
    }

    /**
     * Remove an object from the stack. Retrieves (and removes) an object from
     * the given offset in the stack.
     *
     * @param index The offset (from the top of the stack) of the item to
     *              remove.
     * @return The object that was removed.
     * @throws java.util.EmptyStackException  If the stack is empty.
     * @throws ArrayIndexOutOfBoundsException If the value of
     *                                        <code>index</code> is out of range.
     */

    public T pick(int index) throws ArrayIndexOutOfBoundsException,
        EmptyStackException {
        if (isEmpty()) {
            throw (new EmptyStackException());
        }

        T o = (T) getElementAt(index);
        remove(index);
        fireIntervalRemoved(this, index, index);
        return (o);
    }

    /**
     * Append an object to the bottom of the stack.
     *
     * @param obj The object to append.
     */

    public void append(T obj) {
        int index = getSize();
        insertElementAt(obj, index);
        fireIntervalAdded(this, index, index);
    }

    /**
     * Add a <code>ListDataListener</code> to this model's list of listeners.
     * Since a stack is essentially a list with some special semantics,
     * <code>ListDataListeners</code> are used.
     *
     * @param listener The listener to add.
     */

    public void addStackDataListener(ListDataListener listener) {
        addListDataListener(listener);
    }

    /**
     * Remove a <code>ListDataListener</code> from this model's list of
     * listeners. Since a stack is essentially a list with some special
     * semantics, <code>ListDataListeners</code> are used.
     *
     * @param listener The listener to add.
     */

    public void removeStackDataListener(ListDataListener listener) {
        removeListDataListener(listener);
    }

}
