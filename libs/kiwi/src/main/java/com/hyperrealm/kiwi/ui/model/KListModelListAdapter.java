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

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.hyperrealm.kiwi.event.KListModelEvent;
import com.hyperrealm.kiwi.ui.KListModelListCellRenderer;

/**
 * A model adapter that allows a <code>KListModel</code> to be used with a
 * Swing <code>JList</code> component. This adapter wraps a
 * <code>KListModel</code> implementation and exposes a
 * <code>ListModel</code> interface, and translates the corresponding
 * model events.
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @param <E>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KListModelListAdapter<E> extends KListModelAdapter<E> implements ListModel<E> {

    protected KListModelListCellRenderer<E> renderer;

    private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

    /**
     * Construct a new <code>KListModelListAdapter</code>.
     */

    protected KListModelListAdapter() {
        renderer = new KListModelListCellRenderer<>();
    }

    /**
     * Construct a new <code>KListModelListAdapter</code> for the given
     * <code>JList</code>.
     *
     * @param jlist The <code>JList</code> that will be used with this
     *              adapter.
     */

    public KListModelListAdapter(JList<E> jlist) {
        this();
        jlist.setCellRenderer(renderer);
    }

    /*
     */

    /* implementation of KListModelAdapter */

    protected void fireModelChangedEvent() {

        int ct = 0;

        if (model != null) {
            ct = model.getItemCount();
            if (ct > 0) {
                ct--;
            }
        }

        ListDataEvent evt = null;

        for (ListDataListener listener : listeners) {
            if (evt == null) {
                evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, ct);
            }

            listener.contentsChanged(evt);
        }
    }

    /*
     */

    public void setListModel(KListModel<E> model) {
        super.setListModel(model);

        renderer.setModel(model);
    }

    /* implementation of KListModelListener */

    /*
     */

    public void itemsAdded(KListModelEvent evt) {
        ListDataEvent levt = null;

        for (ListDataListener l : listeners) {
            if (levt == null) {
                levt = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,
                    evt.getStartIndex(), evt.getEndIndex());
            }

            l.intervalAdded(levt);
        }
    }

    /*
     */

    public void itemsChanged(KListModelEvent evt) {
        ListDataEvent levt = null;

        for (ListDataListener l : listeners) {
            if (levt == null) {
                levt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
                    evt.getStartIndex(), evt.getEndIndex());
            }

            l.contentsChanged(levt);
        }
    }

    /*
     */

    public void itemsRemoved(KListModelEvent evt) {
        ListDataEvent levt = null;

        for (ListDataListener l : listeners) {
            if (levt == null) {
                levt = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED,
                    evt.getStartIndex(), evt.getEndIndex());
            }

            l.intervalRemoved(levt);
        }
    }

    /*
     */

    public void dataChanged(KListModelEvent evt) {
        fireModelChangedEvent();
    }

    /* implementation of ListModel */

    /*
     */

    public int getSize() {
        if (model == null) {
            return (0);
        }

        return (model.getItemCount());
    }

    /*
     */

    public E getElementAt(int index) {
        if (model == null) {
            return (null);
        }

        return (model.getItemAt(index));
    }

    /**
     * Add a list model listener. Adds a <code>ListDataListener</code> to
     * this adapter's list of list model listeners.
     *
     * @param listener The listener to add.
     */

    public void addListDataListener(ListDataListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a list model listener. Removes a <code>ListDataListener</code>
     * from this adapter's list of list model listeners.
     *
     * @param listener The listener to remove.
     */

    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }

}

