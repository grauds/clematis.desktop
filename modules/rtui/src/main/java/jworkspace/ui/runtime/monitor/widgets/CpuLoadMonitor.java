package jworkspace.ui.runtime.monitor.widgets;

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

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

import jworkspace.ui.runtime.monitor.AbstractJvmGraphMonitor;

public class CpuLoadMonitor extends AbstractJvmGraphMonitor {

    private final OperatingSystemMXBean os =
        (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected float sampleValue() {
        double load = os.getProcessCpuLoad();
        return load < 0 ? 0f : (float) (load * 100f);
    }

    @Override
    protected String title() {
        return "CPU Load (%)";
    }

    @Override
    protected String unit() {
        return "";
    }
}
