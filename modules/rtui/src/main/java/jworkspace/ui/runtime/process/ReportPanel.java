package jworkspace.ui.runtime.process;

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
import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.runtime.AbstractTask;
import jworkspace.ui.runtime.AbstractReportPanel;
import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;

public class ReportPanel extends AbstractReportPanel implements IProcessSelectionListener {

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    void createReport(AbstractTask task) {
        String props = "<font color=\"black\">"
            + "<b>"
            + LangResource.getString("Name") + ": "
            + "</b>"
            + task.getName()
            + "<br><b>"
            + LangResource.getString("Started_at") + ": "
            + "</b>"
            + (task.getStartTime() != null ? task.getStartTime().toString() : "")
            + "<br>";
        String sb = "<body style='" + "text-align:center;" + "font-family:sans-serif;'>"
            + "<div>" + props + "</div>";
        layoutReport(
            sb,
            new ImageIcon(
                new ResourceLoader(RuntimeManagerWindow.class).getResourceAsImage("images/process.png")
            )
        );
    }

    @Override
    public void processSelected(AbstractTask process) {
        createReport(process);
    }
}
