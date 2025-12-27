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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.ui.runtime.LangResource;

public class MonitorPanel extends KPanel {

    public MonitorPanel() {
        List<Monitor> monitors = new ArrayList<>();
        // Monitors
        monitors.add(new Monitor(LangResource.getString("message#244"), new IPAddressPanel()));
        monitors.add(new Monitor(LangResource.getString("message#245"), new MemoryMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#248"), new MemoryCompactorPanel()));

        JScrollPane nestScroller = new JScrollPane(new Nest(monitors));
        nestScroller.getViewport().setOpaque(false);
        nestScroller.setOpaque(false);

        setLayout(new BorderLayout());
        add(new JScrollPane(new Nest(monitors)), BorderLayout.CENTER);
    }
}
