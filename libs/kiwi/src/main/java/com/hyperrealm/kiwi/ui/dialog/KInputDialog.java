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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class represents a <i>Kiwi</i> input dialog. This dialog allows input
 * of a single line of text, and has <i>OK</i> and <i>Cancel</i> buttons.
 * Pressing <i>Return</i> in the text field is equivalent to pressing the
 * <i>OK</i> button.
 *
 * <p><center>
 * <img src="snapshot/KInputDialog.gif"><br>
 * <i>An example KInputDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class KInputDialog extends ComponentDialog {

    private String input = null;

    private JTextField tInput;

    /**
     * Construct a new <code>KInputDialog</code>. Constructs a new, modal
     * <code>KInputDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     */

    public KInputDialog(Frame parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KInputDialog</code>. Constructs a new, modal
     * <code>KInputDialog</code> with a default window title.
     *
     * @param parent The parent window for this dialog.
     * @since Kiwi 1.4
     */

    public KInputDialog(Dialog parent) {
        this(parent, "", true);
    }

    /**
     * Construct a new <code>KInputDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     */

    public KInputDialog(Frame parent, String title, boolean modal) {
        super(parent, title, true);

        setResizable(false);
    }

    /**
     * Construct a new <code>KInputDialog</code>.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog.
     * @param modal  A flag specifying whether this dialog will be modal.
     * @since Kiwi 1.4
     */

    public KInputDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, true);

        setResizable(false);
    }

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            tInput.requestFocus();
        }
        super.setVisible(flag);
    }

    /**
     * Build the dialog user interface.
     */

    protected Component buildDialogUI() {
        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        KPanel jp = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        jp.setLayout(gb);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        tInput = new JTextField(DEFAULT_FIELD_LENGTH);
        registerTextInputComponent(tInput);

        jp.add(tInput, gbc);

        setIcon(KiwiUtils.getResourceManager().getIcon("dialog_question.png"));
        setComment(loc.getMessage("kiwi.dialog.prompt.input"));

        if (getTitle().length() == 0) {
            setTitle(loc.getMessage("kiwi.dialog.title.input"));
        }

        return (jp);
    }

    /**
     * Accept this dialog. Always returns <code>true</code>.
     */

    protected boolean accept() {
        input = tInput.getText();
        return (true);
    }

    /**
     * Retrieve the text entered in the dialog.
     *
     * @return The contents of the dialog's text field, or <code>null</code>
     * if the dialog was cancelled.
     */

    public String getText() {
        return (input);
    }

    /**
     * Set the text in the dialog's text field.
     *
     * @param text The text to place in the textfield.
     */

    public void setText(String text) {
        tInput.setText(text);
    }

    /**
     * Set the prompt. Sets the dialog's input prompt.
     *
     * @param text The text for the prompt.
     */

    public void setPrompt(String text) {
        setComment(text);
        pack();
    }

}
