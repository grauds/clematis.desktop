package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;

/**
 * Settings dialog shows a list of general workspace options.
 * This configures laf, texture and other features.
 * @author Anton Troshin
 */
public class SettingsDialog extends ComponentDialog {

    private TexturePanel texturePanel;

    private PlafPanel plafPanel;

    public SettingsDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("SettingsDialog.title"), true);
        setResizable(false);
    }

    protected JComponent buildDialogUI() {
        setComment(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        texturePanel = new TexturePanel();
        plafPanel = new PlafPanel();

        LoggingPanel loggingPanel = new LoggingPanel();

        tabbedPane.addTab(WorkspaceResourceAnchor.getString("SettingsDialog.textureTab"), texturePanel);
        tabbedPane.addTab(WorkspaceResourceAnchor.getString("SettingsDialog.lafTab"), plafPanel);
        tabbedPane.addTab(WorkspaceResourceAnchor.getString("SettingsDialog.loggingTab"), loggingPanel);

        return (tabbedPane);
    }

    protected boolean accept() {
        return texturePanel.syncData() && plafPanel.syncData();
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public void setData() {
        texturePanel.setData();
    }
}