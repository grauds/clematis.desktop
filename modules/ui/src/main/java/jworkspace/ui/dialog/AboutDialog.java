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
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;

import com.hyperrealm.kiwi.ui.KTabbedPane;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.widgets.ImageRenderer;

public class AboutDialog extends ComponentDialog {

    public AboutDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("Workspace.about.title"), true);
        setComment("");
    }

    @Override
    protected Component buildDialogUI() {
        KTabbedPane tabs = new KTabbedPane();

        Image im = WorkspaceGUI.getResourceManager().getImage("logo/Logo.png");
        ImageRenderer imr = new ImageRenderer();
        imr.setImage(im);
        imr.setBackground(Color.WHITE);
        tabs.add("About", imr);

        tabs.add("JVM Info", new JvmPropertiesPanel());
        tabs.add("OS Info", new EnvironmentVariablesPanel());

        return tabs;
    }
}
