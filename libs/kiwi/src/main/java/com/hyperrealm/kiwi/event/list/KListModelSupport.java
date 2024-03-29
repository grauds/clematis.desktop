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

package com.hyperrealm.kiwi.event.list;

import java.util.ArrayList;

/**
 * A support object for generating <code>KListModelEvent</code>s.
 *
 * @author Mark Lindner
 * @see KListModelEvent
 * @since Kiwi 2.0
 */
@SuppressWarnings("unused")
public class KListModelSupport {

    private final ArrayList<KListModelListener> listeners;

    private Object source;

    /**
     * Construct a new <code>KListModelSupport</code> object.
     *
     * @param source The owner of this object (and the source of the events that
     *               will be generated by it).
     */
    public KListModelSupport(Object source) {
        listeners = new ArrayList<>();
        this.source = source;
    }

    /**
     * Add a <code>KListModelListener</code> to this object's list of
     * listeners.
     *
     * @param listener The listener to add.
     */
    public void addListModelListener(KListModelListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a <code>KListModelListener</code> from this object's list
     * of listeners.
     *
     * @param listener The listener to remove.
     */
    public void removeListModelListener(KListModelListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fire an <i>items added</i> event for a single item.
     *
     * @param index The offset at which this item will be inserted.
     */

    public void fireItemAdded(int index) {
        fireItemsAdded(index, index);
    }

    /**
     * Fire an <i>items added</i> event for a range of items.
     *
     * @param startIndex The offset of the first item in the range.
     * @param endIndex   The offset of the last item in the range.
     */

    private void fireItemsAdded(int startIndex, int endIndex) {

        KListModelEvent evt = null;

        synchronized (listeners) {
            for (KListModelListener l : listeners) {
                if (evt == null) {
                    evt = new KListModelEvent(source, startIndex, endIndex);
                }
                l.itemsAdded(evt);
            }
        }
    }

    /**
     * Fire an <i>items removed</i> event for a single item.
     *
     * @param index The offset of the item that was removed.
     */

    public void fireItemRemoved(int index) {
        fireItemsRemoved(index, index);
    }

    /**
     * Fire an <i>items removed</i> event for a range of items.
     *
     * @param startIndex The offset of the first item in the range.
     * @param endIndex   The offset of the last item in the range.
     */

    public void fireItemsRemoved(int startIndex, int endIndex) {
        KListModelEvent evt = null;

        synchronized (listeners) {
            for (KListModelListener l : listeners) {
                if (evt == null) {
                    evt = new KListModelEvent(source, startIndex, endIndex);
                }
                l.itemsRemoved(evt);
            }
        }
    }

    /**
     * Fire an <i>items changed</i> event for a single item.
     *
     * @param index The offset of the item that changed.
     */
    public void fireItemChanged(int index) {
        fireItemsChanged(index, index, -1);
    }

    /**
     * Fire an <i>items changed</i> event for a single item.
     *
     * @param index The offset of the item that changed.
     * @param field The field number that changed.
     * @since Kiwi 2.4.1
     */

    public void fireItemChanged(int index, int field) {
        fireItemsChanged(index, index, field);
    }

    /**
     * Fire an <i>items changed</i> event for a range of items.
     *
     * @param startIndex The offset of the first item in the range.
     * @param endIndex   The offset of the last item in the range.
     * @param field      The field number that changed, or -1 if potentially
     *                   all fields of the object changed.
     * @since Kiwi 2.4.1
     */

    private void fireItemsChanged(int startIndex, int endIndex, int field) {
        KListModelEvent evt = null;

        synchronized (listeners) {
            for (KListModelListener l : listeners) {
                if (evt == null) {
                    evt = new KListModelEvent(source, startIndex, endIndex, field);
                }
                l.itemsChanged(evt);
            }
        }
    }

    /**
     * Fire a <i>data changed</i> event.
     */

    public void fireDataChanged() {
        KListModelEvent evt = null;

        synchronized (listeners) {
            for (KListModelListener l : listeners) {
                if (evt == null) {
                    evt = new KListModelEvent(source);
                }
                l.dataChanged(evt);
            }
        }
    }

}
