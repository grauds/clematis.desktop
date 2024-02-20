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

import com.hyperrealm.kiwi.event.list.KListModelEvent;
import com.hyperrealm.kiwi.event.list.KListModelListener;

/**
 * A base class for <code>KListModel</code> adapters. See subclasses for
 * details.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public abstract class KListModelAdapter<T> implements KListModelListener {
    /**
     * The <code>KListModel</code> which is wrapped by this adapter.
     */
    protected KListModel<T> model = null;

    /**
     * Construct a new <code>KListModelAdapter</code>.
     */

    protected KListModelAdapter() {
    }

    private void init() {
        if (model != null) {
            model.addListModelListener(this);
        }

        fireModelChangedEvent();
    }

    /* initialization code */

    /**
     * Get the <code>KListModel</code> for this adapter.
     *
     * @return The model.
     */

    public KListModel<T> getListModel() {
        return (model);
    }

    /**
     * Set the <code>KListModel</code> for this adapter.
     *
     * @param model The model.
     */

    public void setListModel(KListModel<T> model) {
        if (this.model != null) {
            this.model.removeListModelListener(this);
        }
        this.model = model;
        init();
    }

    /**
     * Fire the appropriate event to indicate that the wrapped data model
     * has changed significantly. This method is called whenever the
     * <code>KListModel</code> for this adapter is changed.
     */

    protected abstract void fireModelChangedEvent();

    /*
     */

    public abstract void itemsAdded(KListModelEvent evt);

    /*
     */

    public abstract void itemsChanged(KListModelEvent evt);

    /*
     */

    public abstract void itemsRemoved(KListModelEvent evt);

    /*
     */

    public abstract void dataChanged(KListModelEvent evt);

}
