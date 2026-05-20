package jworkspace.ui.runtime.plugin;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.ui.runtime.plugin.reports.SimplePluginReport;

public class PluginPropertiesPanel extends KPanel implements ActionListener {

    private final SimplePluginReport pluginReport = new SimplePluginReport();
    private final JCheckBox bInstallForAllUsers;

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:WhitespaceAfter"})
    public PluginPropertiesPanel() {

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        JSeparator sep = new JSeparator();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(sep, gbc);

        KPanel props = new KPanel();
        props.setLayout(new BorderLayout(5, 5));
        props.add("Center", pluginReport);

        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane scroller = new JScrollPane(props);
        scroller.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        add(scroller, gbc);

        bInstallForAllUsers = new JCheckBox("Install for all user profiles");
        bInstallForAllUsers.setOpaque(false);
        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(bInstallForAllUsers, gbc);

    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    public boolean syncData() {
        return true;
    }

    public void setData(Plugin data) {
        pluginReport.createReport(data);
    }

    public boolean isAllUsers() {
        return bInstallForAllUsers.isSelected();
    }
}
