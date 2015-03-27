package jworkspace.ui.dialog;

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
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.util.WorkspaceError;
import kiwi.ui.KPanel;
import kiwi.ui.dialog.ComponentDialog;
import kiwi.util.KiwiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Change password of a user profile dialog.
 */
public class ChangePasswordDlg extends ComponentDialog
{
    private JPasswordField old_pass, new_pass, confirm_pass;
    private JPanel carrier;

    public ChangePasswordDlg(Frame parent)
    {
        super(parent, LangResource.getString("ChangePasswordDlg.title"), true);
        setResizable(false);
    }

    public void dispose()
    {
        destroy();
        super.dispose();
    }

    protected boolean accept()
    {
        try
        {
            Workspace.getProfilesEngine().setPassword(
                    new String(old_pass.getPassword()),
                    new String(new_pass.getPassword()),
                    new String(confirm_pass.getPassword()));
        }
        catch (Exception ex)
        {
            WorkspaceError.exception(LangResource.
                                     getString("ChangePasswordDlg.pwdChange.failed"), ex);
        }
        return true;
    }

    protected JComponent buildDialogUI()
    {
        setComment(null);
        carrier = new JPanel();
        carrier.setOpaque(false);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        carrier.setLayout(gb);

        gbc.anchor = gbc.NORTHWEST;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(LangResource.getString("ChangePasswordDlg.oldPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        carrier.add(l, gbc);

        old_pass = new JPasswordField(20);
        old_pass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        carrier.add(old_pass, gbc);

        l = new JLabel(LangResource.getString("ChangePasswordDlg.newPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        carrier.add(l, gbc);

        new_pass = new JPasswordField(20);
        new_pass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        carrier.add(new_pass, gbc);

        l = new JLabel(LangResource.getString("ChangePasswordDlg.confirmPwd"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        carrier.add(l, gbc);

        confirm_pass = new JPasswordField(20);
        confirm_pass.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = gbc.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        carrier.add(confirm_pass, gbc);

        carrier.setBorder(new EmptyBorder(5, 0, 0, 0));

        KPanel t = new KPanel(new BorderLayout());
        t.add(carrier, BorderLayout.CENTER);
        return t;
    }
}
