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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A general-purpose login dialog. The login dialog includes text
 * fields for the entry of a username and password and <i>OK</i> and
 * <i>Cancel</i> buttons. Validation is performed in the method
 * <code>validate()</code>, which should be overridden by subclassers
 * to provide the desired validation logic. If a validation succeeds,
 * the login dialog disappears. Otherwise, a warning dialog is
 * displayed, and once it's dismissed by the user, control returns to
 * the login dialog. The dialog is unconditionally modal.
 *
 * <p><center>
 * <img src="snapshot/LoginDialog.gif"><br>
 * <i>An example LoginDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public class LoginDialog extends ComponentDialog {
    /**
     * The username text field.
     */
    private JTextField tUser;
    /**
     * The password text field.
     */
    private JPasswordField tPasswd;

    private final DialogSet dialogs;

    private String loginFailedMessage;

    private KLabel lUsername, lPassword;

    /**
     * Construct a new <code>LoginDialog</code> with a default title.
     *
     * @param parent  The parent frame.
     * @param comment A comment string to display in the upper portion of the
     *                window.
     */

    public LoginDialog(Frame parent, String comment) {
        this(parent, "", comment);
    }

    /**
     * Construct a new <code>LoginDialog</code>.
     *
     * @param parent  The parent frame.
     * @param title   The dialog window's title.
     * @param comment A comment string to display in the upper portion of the
     *                window.
     */

    public LoginDialog(Frame parent, String title, String comment) {
        super(parent, title, true);
        setResizable(false);

        setComment(comment);

        setIcon(KiwiUtils.getResourceManager().getIcon("key.png"));

        dialogs = new DialogSet(this, DialogSet.CENTER_PLACEMENT);
    }

    /*
     */

    protected Component buildDialogUI() {
        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        loginFailedMessage = loc.getMessage("kiwi.dialog.message.login_failed");

        KPanel main = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        main.setLayout(gb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        Insets insets0 = new Insets(0, 0, DEFAULT_PADDING, DEFAULT_PADDING);
        Insets insets1 = new Insets(0, 0, DEFAULT_PADDING, 0);

        lUsername = new KLabel(loc.getMessage("kiwi.dialog.prompt.username"));
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.insets = insets0;
        main.add(lUsername, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        tUser = new JTextField(DEFAULT_FIELD_LENGTH);
        tUser.addActionListener(evt -> tPasswd.requestFocus());
        gbc.insets = insets1;
        main.add(tUser, gbc);

        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.weightx = 0;
        gbc.insets = insets0;
        lPassword = new KLabel(loc.getMessage("kiwi.dialog.prompt.password"));
        main.add(lPassword, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = insets1;
        tPasswd = new JPasswordField(DEFAULT_FIELD_LENGTH);
        registerTextInputComponent(tPasswd);
        main.add(tPasswd, gbc);

        if (getTitle().isEmpty()) {
            setTitle(loc.getMessage("kiwi.dialog.title.login"));
        }

        return (main);
    }

    /**
     * Perform the login validation. The default implementation returns
     * <b>true</b>.
     *
     * @param username The username that was entered.
     * @param password The password that was entered.
     * @return <b>true</b> if the validation succeeded, <b>false</b> otherwise.
     */

    protected boolean validate(String username, String password) {
        return (true);
    }

    /*
     */

    protected boolean accept() {
        boolean ok = validate(tUser.getText(), new String(tPasswd.getPassword()));
        if (ok) {
            return (true);
        }

        dialogs.showMessageDialog(loginFailedMessage);
        tUser.requestFocus();
        return (false);
    }

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            tUser.setText("");
            tPasswd.setText("");
            tUser.requestFocus();
        }
        super.setVisible(flag);
    }

    /**
     * Set the text for the username prompt label.
     *
     * @param text The new text for the label
     * @since Kiwi 1.3.3
     */

    public void setUsernamePromptText(String text) {
        if (text == null) {
            return;
        }

        lUsername.setText(text);
        pack();
    }

    /**
     * Set the text for the password prompt label.
     *
     * @param text The new text for the label
     * @since Kiwi 1.3.3
     */

    public void setPasswordPromptText(String text) {
        if (text == null) {
            return;
        }

        lPassword.setText(text);
        pack();
    }

}
