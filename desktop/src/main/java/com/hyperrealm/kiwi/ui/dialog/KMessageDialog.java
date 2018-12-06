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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import com.hyperrealm.kiwi.ui.KLabelArea;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class represents a <i>Kiwi</i> message dialog. This dialog displays a
 * textual message, and has an <i>OK</i> button.
 *
 * <p><center>
 * <img src="snapshot/KMessageDialog.gif"><br>
 * <i>An example KMessageDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class KMessageDialog extends ComponentDialog {

    private KLabelArea lText;

    /**
     * Construct a new <code>KMessageDialog</code>. Constructs a new, modal
     * <code>KMessageDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     */

    public KMessageDialog(Frame parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KMessageDialog</code>. Constructs a new, modal
     * <code>KMessageDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     * @since Kiwi 1.4
     */

    public KMessageDialog(Dialog parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KMessageDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     */

    public KMessageDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal, false);
        setResizable(false);
    }

    /**
     * Construct a new <code>KMessageDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     * @since Kiwi 1.4
     */

    public KMessageDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, modal, false);
        setResizable(false);
    }

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            bOk.requestFocus();
        }
        super.setVisible(flag);
    }

    /**
     * Build the dialog user interface.
     */

    protected Component buildDialogUI() {
        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        lText = new KLabelArea(loc.getMessage("kiwi.dialog.prompt.message"),
            DEFAULT_ROWS_NUMBER,
            DEFAULT_LABEL_LENGTH);
        lText.setForeground(Color.black);

        setIcon(KiwiUtils.getResourceManager().getIcon("dialog_exclamation.png"));
        setComment("");
        if (getTitle().length() == 0) {
            setTitle(loc.getMessage("kiwi.dialog.title.message"));
        }

        return (lText);
    }

    /**
     * Set the message. Sets the dialog's message.
     *
     * @param text The text for the message.
     */

    public void setMessage(String text) {
        lText.setText(text);
        pack();
    }

}
