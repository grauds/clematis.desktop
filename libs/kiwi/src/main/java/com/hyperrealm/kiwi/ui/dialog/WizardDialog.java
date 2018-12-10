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

package com.hyperrealm.kiwi.ui.dialog;

import java.awt.Frame;
import java.awt.GridLayout;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.WizardView;
import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * This class displays a <code>WizardView</code> in a dialog window.
 * <p><center>
 * <img src="snapshot/WizardDialog.gif"><br>
 * <i>An example WizardDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.WizardView
 */

public class WizardDialog extends KDialog {
    /**
     * Construct a new <code>WizardDialog</code>. The dialog is created with a
     * default size of 400x400, but this can be overridden with a call to
     * <code>setSize()</code>. The dialog is resizable by default, but this
     * behavior can be changed with a call to <code>setResizable()</code>.
     *
     * @param parent The parent window.
     * @param title  The title for the dialog window.
     * @param modal  A flag specifying whether this dialog will be modal.
     * @param view   The <code>WizardView</code> to display in this dialog.
     */

    public WizardDialog(Frame parent, String title, boolean modal, WizardView view) {
        super(parent, title, modal);

        KPanel main = getMainContainer();
        main.setBorder(KiwiUtils.DEFAULT_BORDER);
        main.setLayout(new GridLayout(1, 0));
        main.add(view);
        view.addActionListener(evt -> {
            if (evt.getActionCommand().equals("finish")) {
                doAccept();
            } else {
                doCancel();
            }
        });

        pack();
    }

}
