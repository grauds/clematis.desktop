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
import jworkspace.ui.runtime.monitor.widgets.ClassLoadingMonitor;
import jworkspace.ui.runtime.monitor.widgets.CpuLoadMonitor;
import jworkspace.ui.runtime.monitor.widgets.GcTimeMonitor;
import jworkspace.ui.runtime.monitor.widgets.IPAddressPanel;
import jworkspace.ui.runtime.monitor.widgets.MemoryMonitor;
import jworkspace.ui.runtime.monitor.widgets.ThreadMonitor;

public class MonitorPanel extends KPanel {

    public MonitorPanel() {
        List<Monitor> monitors = new ArrayList<>();
        // Monitors
        monitors.add(new Monitor(LangResource.getString("message#244"), new IPAddressPanel()));
        monitors.add(new Monitor(LangResource.getString("message#245"), new MemoryMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#248"), new MemoryCompactorPanel()));
        monitors.add(new Monitor(LangResource.getString("message#254"), new ClassLoadingMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#255"), new CpuLoadMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#256"), new GcTimeMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#257"), new ThreadMonitor()));

        JScrollPane nestScroller = new JScrollPane(new Dashboard(monitors));
        nestScroller.getViewport().setOpaque(false);
        nestScroller.setOpaque(false);

        setLayout(new BorderLayout());
        add(new HeaderPanel(), BorderLayout.NORTH);
        add(nestScroller, BorderLayout.CENTER);
    }
}
