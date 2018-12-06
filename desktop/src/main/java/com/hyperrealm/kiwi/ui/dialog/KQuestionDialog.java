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
 * This class represents a <i>Kiwi</i> question dialog. This dialog displays a
 * message, and has <i>OK</i> and <i>Cancel</i> buttons, which correspond to
 * "yes" and "no" responses.
 *
 * <p><center>
 * <img src="snapshot/KQuestionDialog.gif"><br>
 * <i>An example KQuestionDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class KQuestionDialog extends ComponentDialog {
    /**
     * Dialog type. Specifies an ok/cancel dialog.
     */
    public static final int OK_CANCEL_DIALOG = 0;
    /**
     * Dialog type. Specifies a yes/no dialog.
     */
    public static final int YES_NO_DIALOG = 1;

    private boolean status = false;

    private KLabelArea lText;

    private String sOk, sCancel, sYes, sNo;

    /**
     * Construct a new <code>KQuestionDialog</code>. Constructs a new, modal
     * <code>KQuestionDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     */

    public KQuestionDialog(Frame parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KQuestionDialog</code>. Constructs a new, modal
     * <code>KQuestionDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     * @since Kiwi 1.4
     */

    public KQuestionDialog(Dialog parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KQuestionDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     */

    public KQuestionDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        setResizable(false);
    }

    /**
     * Construct a new <code>KQuestionDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     * @since Kiwi 1.4
     */

    public KQuestionDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, modal);
        setResizable(false);
    }

    /**
     * Set the dialog type. The dialog may either be an ok/cancel dialog or
     * a yes/no dialog; this type merely reflects the labels on the accept and
     * cancel buttons.
     *
     * @param type The new type; one of <code>OK_CANCEL_DIALOG</code> (the
     *             default), or <code>YES_NO_DIALOG</code>.
     */

    public void setType(int type) {
        switch (type) {
            case YES_NO_DIALOG:
                setAcceptButtonText(sYes);
                setCancelButtonText(sNo);
                break;

            case OK_CANCEL_DIALOG:
            default:
                setAcceptButtonText(sOk);
                setCancelButtonText(sCancel);
        }
    }

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            status = false;
            bCancel.requestFocus();
        }
        super.setVisible(flag);
    }

    /**
     * Build the dialog user interface.
     */

    protected Component buildDialogUI() {
        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        lText = new KLabelArea(loc.getMessage("kiwi.dialog.prompt.question"),
            DEFAULT_ROWS_NUMBER, DEFAULT_LABEL_LENGTH);
        lText.setForeground(Color.black);

        setIcon(KiwiUtils.getResourceManager().getIcon("dialog_question.png"));
        setComment("");
        if (getTitle().length() == 0) {
            setTitle(loc.getMessage("kiwi.dialog.title.message"));
        }

        sOk = loc.getMessage("kiwi.button.ok");
        sCancel = loc.getMessage("kiwi.button.cancel");
        sYes = loc.getMessage("kiwi.button.yes");
        sNo = loc.getMessage("kiwi.button.no");

        return (lText);
    }

    /**
     * Set the prompt. Sets the dialog's message.
     *
     * @param message The text of the message.
     */

    public void setMessage(String message) {
        lText.setText(message);
        pack();
    }

    /**
     * Accept the dialog. Always returns <code>true</code>.
     */

    protected boolean accept() {
        status = true;
        return (true);
    }

    /**
     * Get the status of the dialog.
     *
     * @return <code>true</code> if the dialog was dismissed via the <i>OK</i>
     * button and <code>false</code> if it was cancelled.
     */

    public boolean getStatus() {
        return (status);
    }

}
