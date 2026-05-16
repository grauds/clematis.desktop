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
import java.awt.Component;
import java.awt.Frame;

import com.hyperrealm.kiwi.ui.KTabbedPane;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;

public class AboutDialog extends ComponentDialog {

    public AboutDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("Workspace.about.title"), true);
        setComment("");
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected Component buildDialogUI() {
        KTabbedPane tabs = new KTabbedPane();

        tabs.add("About", new ClematisLogoPanel());
        tabs.add("Licenses", new AcknowledgementsPanel());
        tabs.add("JVM Info", new JvmPropertiesPanel());
        tabs.add("OS Info", new EnvironmentVariablesPanel());

        return tabs;
    }
}
