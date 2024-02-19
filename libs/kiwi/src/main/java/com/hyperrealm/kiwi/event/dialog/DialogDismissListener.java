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

package com.hyperrealm.kiwi.event.dialog;

import java.util.EventListener;

/**
 * This class represents a listener that is notified when a dialog window is
 * dismissed. A convenience method for firing <code>DialogDismissEvent</code>s
 * is provided in <code>com.hyperrealm.kiwi.ui.dialog.KDialog</code>.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.dialog.KDialog#fireDialogDismissed
 */

public interface DialogDismissListener extends EventListener {

    /**
     * Invoked after a dialog is dismissed.
     *
     * @param evt The event.
     */

    void dialogDismissed(DialogDismissEvent evt);
}
