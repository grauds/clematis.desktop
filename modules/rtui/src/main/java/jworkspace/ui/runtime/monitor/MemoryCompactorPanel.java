package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

@SuppressWarnings("checkstyle:MagicNumber")
public class MemoryCompactorPanel extends KPanel {

    private static final int DEFAULT_PERCENT_OF_FREE_MEMORY = 50;

    public MemoryCompactorPanel() {
        super();

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);
        setOpaque(false);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        MemoryCompactor mc = new MemoryCompactor(DEFAULT_PERCENT_OF_FREE_MEMORY, new boolean[1]);
        mc.start();

        KPanel widget = new KPanel();
        mc.setStatusArea(widget);

        widget.setOpaque(false);
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(widget, gbc);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 70);
    }
}