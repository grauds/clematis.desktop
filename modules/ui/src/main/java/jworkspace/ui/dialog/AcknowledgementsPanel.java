package jworkspace.ui.dialog;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class AcknowledgementsPanel extends JPanel {

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public AcknowledgementsPanel() {

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Title
        JLabel headerLabel = new JLabel("Open Source Libraries & Licenses");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(new Color(75, 16, 176)); // Clematis Purple
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Inner List Container (Stacks items vertically)
        JPanel listContainer = new JPanel();
        listContainer.setBackground(Color.WHITE);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        // Format: addDependencyRow(container, "Library Name", "License Type", "Copyright String")
        addDependencyRow(listContainer, "Kiwi Library", "GPL 2.0", "Copyright © 1998-2008 Mark A Lindner");
        addDependencyRow(listContainer, "Springboard", "GPL 2.0", "Copyright © 1998-2000 Mark A Lindner");
        addDependencyRow(listContainer, "Noia Icon Pack", "GNU LGPL v2.1",
            "Copyright © 2002 - 2019 Carles Carbonell BernadÛ (Carlitus)"
        );
        addDependencyRow(listContainer, "Yoda Time", "Apache License Version 2.0", "Joda.org");

        // Wrap the list in a Scroll Pane for long text lists
        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null); // Remove default harsh borders
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper method to generate uniform, scannable license blocks.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private void addDependencyRow(JPanel container, String name, String license, String copyright) {
        JPanel row = new JPanel();
        row.setBackground(Color.WHITE);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBorder(new EmptyBorder(5, 5, 15, 5));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Library Title
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(new Color(51, 51, 51));

        // License Badge / Text
        JLabel licenseLabel = new JLabel("License: " + license);
        licenseLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        licenseLabel.setForeground(new Color(75, 16, 176));

        // Copyright info
        JLabel copyLabel = new JLabel(copyright);
        copyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        copyLabel.setForeground(new Color(119, 119, 119));

        row.add(nameLabel);
        row.add(Box.createVerticalStrut(2));
        row.add(licenseLabel);
        row.add(Box.createVerticalStrut(2));
        row.add(copyLabel);

        container.add(row);
    }
}

