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

package com.hyperrealm.kiwi.ui;

import java.util.EmptyStackException;

import javax.swing.JList;

import com.hyperrealm.kiwi.ui.model.list.DefaultStackModel;
import com.hyperrealm.kiwi.ui.model.list.StackModel;

/**
 * A component that displays the contents of a <code>Stack</code> data
 * structure. This is an MVC class that uses a <code>StackModel</code> as its
 * data model.
 *
 * @param <T>
 * @author Mark Lindner
 * @see StackModel
 * @see java.util.Stack
 */

public class StackView<T> extends JList {
    private StackModel<T> model;

    /**
     * Construct a new <code>StackView</code> with a default stack model.
     */

    public StackView() {
        this(new DefaultStackModel<T>());
    }

    /**
     * Construct a new <code>StackView</code> with the given stack model.
     *
     * @param model The <code>StackModel</code> to use.
     */

    public StackView(StackModel<T> model) {
        super.setModel(model);
        this.model = model;
    }

    /**
     * Get the model used by this <code>StackView</code>.
     */

    public StackModel<T> getStackModel() {
        return (model);
    }

    /**
     * Push a new item on the stack.
     *
     * @param obj The object to push on the stack.
     */

    public void push(T obj) {
        model.push(obj);
    }

    /**
     * Pop an item off the stack. Pops the top item off the stack.
     *
     * @return The popped item.
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public T pop() throws EmptyStackException {
        return (model.pop());
    }

    /**
     * Drop the top item off the stack. Pops and discards the top item off the
     * stack.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public void drop() throws EmptyStackException {
        model.drop();
    }

    /**
     * Peek at the top item on the stack.
     *
     * @return The top item on the stack. The item is not removed from the
     * stack.
     */

    public T peek() {
        return (model.peek());
    }

    /**
     * Swap the positions of the top two items on the stack. If there is only
     * one item in the stack, this method has no effect.
     *
     * @throws java.util.EmptyStackException If the stack is empty.
     */

    public void swap() throws EmptyStackException {
        model.swap();
    }

    /**
     * Return the depth of the stack.
     *
     * @return The number of items on the stack.
     */

    public int getDepth() {
        return (model.getDepth());
    }

    /**
     * Check if the stack is empty.
     *
     * @return <code>true</code> if the stack is empty, <code>false</code>
     * otherwise.
     */

    public boolean isEmpty() {
        return (model.isEmpty());
    }

    /**
     * Remove an item from the stack. Removes the item at the specified index
     * from the stack. Index position 0 refers to the top of the stack.
     *
     * @param index The index of the item to remove.
     * @return The removed item.
     * @throws ArrayIndexOutOfBoundsException If <code>index</code>
     *                                        is out of range.
     */

    public T pick(int index) throws ArrayIndexOutOfBoundsException {
        return (model.pick(index));
    }

    /**
     * Append an item to the stack.
     *
     * @param obj The item to add to the bottom of the stack.
     */

    public void append(T obj) {
        model.append(obj);
    }

    /**
     * Replace the top item on the stack. The top item on the stack is replaced
     * with the item <code>obj</code>. If the stack is empty, the item is merely
     * pushed on the stack.
     *
     * @param obj The new item.
     */

    public void replace(T obj) {
        try {
            pop();
        } catch (EmptyStackException ignored) {
        }

        push(obj);
    }

}
