package jworkspace.ui.profiles;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2000 Anton Troshin

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

   Author may be contacted at:

   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.users.Profile;

/**
 * User details panel gathers user data for profiles.
 */
class UserDetailsPanel extends KPanel {
    private final JTextField tNick;
    private final JTextField tName;
    private final JTextField tSurname;
    private final JTextField tMail;
    private final JTextArea tDescription;

    @SuppressWarnings("MagicNumber")
    UserDetailsPanel() {
        super();

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.nick"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        tNick = new JTextField(20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(tNick, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.name"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        tName = new JTextField(20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(tName, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.surname"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        tSurname = new JTextField(20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(tSurname, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.mail"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        tMail = new JTextField(20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(tMail, gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.desc"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        tDescription = new JTextArea(5, 1);
        tDescription.setLineWrap(true);
        tDescription.setWrapStyleWord(true);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(new JScrollPane(tDescription), gbc);

        l = new JLabel(WorkspaceResourceAnchor.getString("UserDetailsPanel.security"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        add(l, gbc);

        KPanel buttonHolder = new KPanel();
        buttonHolder.setLayout(new BorderLayout());
        KButton tChangePassword = new KButton(WorkspaceResourceAnchor.getString("UserDetailsPanel.chpasswd"));
        tChangePassword.setDefaultCapable(false);
        tChangePassword.addActionListener(evt -> {
            ChangePasswordDlg dlg =
                new ChangePasswordDlg(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame());
            dlg.setVisible(true);
        });
        buttonHolder.add(tChangePassword, BorderLayout.EAST);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(buttonHolder, gbc);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public void setData() {
        Profile currentProfile = ServiceLocator.getInstance().getProfilesManager().getCurrentProfile();

        tNick.setText(currentProfile.getUserName());
        tName.setText(currentProfile.getUserFirstName());
        tSurname.setText(currentProfile.getUserLastName());
        tMail.setText(currentProfile.getEmail());
        tDescription.setText(currentProfile.getDescription());
    }

    public boolean syncData() {
        Profile currentProfile = ServiceLocator.getInstance().getProfilesManager().getCurrentProfile();

        currentProfile.setUserFirstName(tName.getText());
        currentProfile.setUserLastName(tSurname.getText());
        currentProfile.setEmail(tMail.getText());
        currentProfile.setDescription(tDescription.getText());
        currentProfile.setUserName(tNick.getText());

        return true;
    }
}
