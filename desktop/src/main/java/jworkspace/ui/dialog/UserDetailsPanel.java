package jworkspace.ui.dialog;

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

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import kiwi.ui.KButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User details panel gathers user data for
 * workspace user profiles engine.
 */

/**
 * Change log:
 * 22.11.01 changes to accomodate editing of
 * user description, setting new password option and
 * editing of user properties.
 */
class UserDetailsPanel extends KPanel
{
    private JTextField t_nick, t_name, t_surname, t_mail;
    private JTextArea t_description;

    UserDetailsPanel()
    {
        super();

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(LangResource.getString("UserDetailsPanel.nick"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        t_nick = new JTextField(20);
        t_nick.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(t_nick, gbc);

        l = new JLabel(LangResource.getString("UserDetailsPanel.name"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        t_name = new JTextField(20);
        t_name.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(t_name, gbc);

        l = new JLabel(LangResource.getString("UserDetailsPanel.surname"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        t_surname = new JTextField(20);
        t_surname.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(t_surname, gbc);

        l = new JLabel(LangResource.getString("UserDetailsPanel.mail"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        t_mail = new JTextField(20);
        t_mail.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(t_mail, gbc);

        l = new JLabel(LangResource.getString("UserDetailsPanel.desc"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        t_description = new JTextArea(5, 1);
        t_description.setLineWrap(true);
        t_description.setWrapStyleWord(true);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(new JScrollPane(t_description), gbc);

        l = new JLabel(LangResource.getString("UserDetailsPanel.security"));
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.firstInsets;
        add(l, gbc);

        KPanel button_holder = new KPanel();
        button_holder.setLayout(new BorderLayout());
        KButton t_change_password = new KButton
                (LangResource.getString("UserDetailsPanel.chpasswd"));
        t_change_password.setDefaultCapable(false);
        t_change_password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ChangePasswordDlg dlg =
                        new ChangePasswordDlg(Workspace.getUI().getFrame());
                dlg.setVisible(true);
            }
        });
        button_holder.add(t_change_password, BorderLayout.EAST);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        add(button_holder, gbc);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public void setData()
    {
        t_nick.setText(Workspace.getProfilesEngine().getUserName());
        t_name.setText(Workspace.getProfilesEngine().getUserFirstName());
        t_surname.setText(Workspace.getProfilesEngine().getUserLastName());
        t_mail.setText(Workspace.getProfilesEngine().getEmail());
        t_description.setText(Workspace.getProfilesEngine().getDescription());
    }

    public boolean syncData()
    {
        Workspace.getProfilesEngine().setUserFirstName(t_name.getText());
        Workspace.getProfilesEngine().setUserLastName(t_surname.getText());
        Workspace.getProfilesEngine().setEmail(t_mail.getText());
        Workspace.getProfilesEngine().setDescription(t_description.getText());
        if (!Workspace.getProfilesEngine().setUserName(t_nick.getText()))
        {
            JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                                          LangResource.getString("UserDetailsPanel.cannotApply.message"),
                                          LangResource.getString("UserDetailsPanel.cannotApply.title"),
                                          JOptionPane.WARNING_MESSAGE);
        }
        return true;
    }
}
