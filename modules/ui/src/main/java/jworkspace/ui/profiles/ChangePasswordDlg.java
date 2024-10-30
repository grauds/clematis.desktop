package jworkspace.ui.profiles;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Authors may be contacted at:

   frenzy@ix.netcom.com
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceError;

/**
 * Change password of a user profile dialog
 *
 */
class ChangePasswordDlg extends ComponentDialog {

    private JPasswordField oldPass, newPass, confirmPass;

    ChangePasswordDlg(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("ChangePasswordDlg.title"), true);
        setResizable(false);
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    protected boolean accept() {
        try {
            ServiceLocator
                .getInstance()
                .getProfilesManager()
                .getCurrentProfile()
                .setPassword(
                    new String(oldPass.getPassword()),
                    new String(newPass.getPassword()),
                    new String(confirmPass.getPassword())
                );
        } catch (Exception ex) {
            WorkspaceError.exception(WorkspaceResourceAnchor.
                getString("ChangePasswordDlg.pwdChange.failed"), ex
            );
        }
        return true;
    }

    @SuppressWarnings("MagicNumber")
    protected JComponent buildDialogUI() {
        setComment(null);
        JPanel carrier = new JPanel();
        carrier.setOpaque(false);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        carrier.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(WorkspaceResourceAnchor.getString("ChangePasswordDlg.oldPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        carrier.add(l, gbc);

        oldPass = new JPasswordField(20);
      //  oldPass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        carrier.add(oldPass, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("ChangePasswordDlg.newPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        carrier.add(l, gbc);

        newPass = new JPasswordField(20);
    //    newPass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        carrier.add(newPass, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("ChangePasswordDlg.confirmPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        carrier.add(l, gbc);

        confirmPass = new JPasswordField(20);
   //     confirmPass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        carrier.add(confirmPass, gbc);

        carrier.setBorder(new EmptyBorder(5, 0, 0, 0));

        KPanel t = new KPanel(new BorderLayout());
        t.add(carrier, BorderLayout.CENTER);
        return t;
    }
}
