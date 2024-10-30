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

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.hyperrealm.kiwi.ui.KTabbedPane;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.api.dialog.IDialogPanel;
import lombok.NonNull;

/**
 * Settings dialog shows a list of general workspace options.
 * This configures laf, texture and other features.
 * @author Anton Troshin
 */
public class SettingsDialog extends ComponentDialog {

    private TexturePanel texturePanel;

    private final List<IDialogPanel> additionalPanels = new ArrayList<>();

    private KTabbedPane tabbedPane;

    public SettingsDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("SettingsDialog.title"), true);
        setResizable(false);
    }

    public void addPanel(@NonNull String title, @NonNull IDialogPanel panel) {
        this.additionalPanels.add(panel);
        if (panel instanceof Component component) {
            this.getTabbedPane().addTab(title, component);
        }
    }

    protected JComponent buildDialogUI() {
        setComment(null);

        texturePanel = new TexturePanel();
        getTabbedPane().addTab(WorkspaceResourceAnchor.getString("SettingsDialog.textureTab"), texturePanel);

        return (getTabbedPane());
    }

    protected boolean accept() {
        this.additionalPanels.forEach(IDialogPanel::syncData);
        return texturePanel.syncData();
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public void setData() {
        texturePanel.setData();
    }

    public KTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new KTabbedPane();
        }
        return tabbedPane;
    }
}