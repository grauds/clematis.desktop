package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;

public class HeaderPanel extends KPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        add(createPerformanceLabel(), BorderLayout.CENTER);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public Dimension getPreferredSize() {
        return new Dimension(250, 70);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JLabel createPerformanceLabel() {
        JLabel l = new JLabel();

        l.setOpaque(true);
        l.setBackground(Color.white);

        l.setIcon(new ImageIcon(
            new ResourceLoader(RuntimeManagerWindow.class)
                .getResourceAsImage("images/monitor.png")
        ));

        l.setText(
            "<html><font color=black>"
                + LangResource.getString("System_Monitors")
                + "</font><br><font size=\"-2\" color=black><i>"
                + LangResource.getString("hint1")
                + "</i></font></html>"
        );

        l.setBackground(Color.white);
        l.setForeground(Color.black);

        Dimension size = new Dimension(300, 70);
        l.setPreferredSize(size);
        l.setMinimumSize(size);
        l.setMaximumSize(size);

        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        l.setHorizontalAlignment(JLabel.CENTER);
        l.setHorizontalTextPosition(JLabel.RIGHT);
        l.setIconTextGap(10);

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }
}
