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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.util.ListConsumer;

/**
 * This class represents a visual <i>scrollback</i> buffer. Lines of text may
 * be added to the buffer, which always displays the most recently-added
 * lines. Older lines may be viewed by scrolling back to them using the
 * scrollbar.
 * <p>
 * Once the buffer is full, the oldest lines are discarded as new lines
 * are added.
 *
 * @author Mark Lindner
 */

public class ScrollbackView extends JList implements ListConsumer {

    private static final int DEFAULT_LINES_BUFFER = 100;

    private int saveLines = DEFAULT_LINES_BUFFER;

    private FastListModel model;

    private boolean autoScrollsOnAdd = true;

    /**
     * Construct a new <code>ScrollBackView</code>.
     */

    public ScrollbackView() {
        model = new FastListModel();
        setModel(model);
    }

    /**
     * Clear the scrollback. Removes all rows from the buffer.
     */

    public void clear() {
        model.removeAllElements();
    }

    /**
     * Get the buffer size.
     *
     * @return The maximum number of lines that this buffer will save.
     * @see #setSaveLines
     */

    public int getSaveLines() {
        return (saveLines);
    }

    /**
     * Set the buffer size.
     *
     * @param lines The maximum number of lines to save in the buffer.
     * @see #getSaveLines
     */

    public void setSaveLines(int lines) {
        if (lines > 0) {
            saveLines = lines;
        }
    }

    /**
     * Add an item to the buffer. Adds the specified item to the end of the
     * buffer. If the buffer was full, the oldest line is discarded. If
     * necessary, the buffer is scrolled to make the new item visible.
     *
     * @param item The item to add.
     */

    public void addItem(Object item) {
        SwingUtilities.invokeLater(new Inserter(item));
    }

    /**
     * Add a list of items to the buffer. Adds the specified items to
     * the end of the buffer. If the buffer was full, the oldest lines
     * are discarded. If necessary, the buffer is scrolled to make the
     * new items visible.
     *
     * @param items The items to add.
     */

    public void addItems(List items) {
        SwingUtilities.invokeLater(new Inserter(items));
    }

    /**
     * Specify whether the buffer should automatically scroll to the bottom
     * when a new item is added.
     *
     * @param flag A flag specifying whether autoscrolling should be enabled or
     *             disabled.
     */

    public void setAutoScrollsOnAdd(boolean flag) {
        autoScrollsOnAdd = flag;
    }

    /**
     *
     */

    private static class FastListModel extends AbstractListModel {
        private ArrayList data = new ArrayList();

        public int getSize() {
            return (data.size());
        }

        public void addElement(Object o) {
            int x = data.size();

            data.add(o);
            fireIntervalAdded(this, x, x);
        }

        public void addElements(List list) {
            int x = data.size();

            //int sz = v.size();

            Iterator iter = list.iterator();
            int sz = 0;

            while (iter.hasNext()) {
                data.add(iter.next());
                sz++;
            }

            fireIntervalAdded(this, x, x + sz - 1);
        }

        public void removeRange(int fromIndex, int toIndex) {
            for (int i = toIndex; i >= fromIndex; i--) {
                data.remove(fromIndex);
            }

            fireIntervalRemoved(this, fromIndex, toIndex);
        }

        public void removeAllElements() {
            int sz = data.size();
            data.clear();

            if (sz > 0) {
                fireIntervalRemoved(this, 0, --sz);
            }
        }

        public Object getElementAt(int index) {
            return (data.get(index));
        }

    }

    /*
     */

    private class Inserter implements Runnable {
        private Object item;
        private List items;

        Inserter(Object item) {
            this.item = item;
        }

        Inserter(List items) {
            this.items = items;
        }

        public void run() {
            if (items == null) {
                if (model.getSize() >= saveLines) {
                    model.removeRange(0, (model.getSize() - saveLines));
                }
                model.addElement(item);
                if (autoScrollsOnAdd) {
                    ensureIndexIsVisible(model.getSize() - 1);
                }
            } else {
                int t = model.getSize() + items.size();

                if (t > saveLines) {
                    model.removeRange(0, t - saveLines);
                }
                model.addElements(items);

                if (autoScrollsOnAdd) {
                    ensureIndexIsVisible(model.getSize() - 1);
                }
            }
        }
    }

}
