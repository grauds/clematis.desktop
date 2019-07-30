package jworkspace.ui.installer;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2002 Anton Troshin

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

   Authors may be contacted at:

   frenzy@ix.netcom.com
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import com.hyperrealm.kiwi.ui.KTreeTable;
import com.hyperrealm.kiwi.util.ResourceLoader;
import jworkspace.installer.JVM;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.installer.actions.WorkbenchActions;
import jworkspace.ui.installer.dialog.JvmDialog;
import jworkspace.util.WorkspaceUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * JVM workbench.
 */
public class JvmWorkbench extends Workbench {
    /**
     * Constructor for jvm workbench.
     */
    public JvmWorkbench(InstallerWindow installer) {
        super(installer);
        icon = new ImageIcon(new ResourceLoader(InstallerWindow.class)
                .getResourceAsImage("images/jvm_big.png"));
        text = LangResource.getString("Java_Virtual_Machines");
        comment = LangResource.getString("Manages_jvms");
        layoutFullHeightContent();
    }

    public JToolBar getToolBar() {
        if (ctoolbar == null) {
            ctoolbar = new JToolBar();
            WorkspaceGUI gui = null;
            if (Workspace.getUI() instanceof WorkspaceGUI) {
                gui = (WorkspaceGUI) Workspace.getUI();
            }
            ctoolbar.setFloatable(false);

            if (gui != null) {
                JButton b = WorkspaceUtils.
                        createButtonFromAction(WorkbenchActions.addFolderAction, false);
                ctoolbar.add(b);
                WorkbenchActions.addFolderAction.
                        addPropertyChangeListener(gui.createActionChangeListener(b));

                b = WorkspaceUtils.
                        createButtonFromAction(WorkbenchActions.addAction, true);
                ctoolbar.add(b);
                WorkbenchActions.addAction.
                        addPropertyChangeListener(gui.createActionChangeListener(b));

                b = WorkspaceUtils.
                        createButtonFromAction(WorkbenchActions.editAction, false);
                ctoolbar.add(b);
                WorkbenchActions.editAction.
                        addPropertyChangeListener(gui.createActionChangeListener(b));

                b = WorkspaceUtils.
                        createButtonFromAction(WorkbenchActions.deleteAction, false);
                ctoolbar.add(b);
                WorkbenchActions.deleteAction.
                        addPropertyChangeListener(gui.createActionChangeListener(b));
            }
        }
        return ctoolbar;
    }

    public KTreeTable getContentTree() {
        return initContentTree(Workspace.getInstallEngine().getJvmModel());
    }

    /**
     * Adds jvm with argument node as parent.

     */
    public void add() {
        if (currentNode == null) {
            return;
        }

        JVM jvm = null;

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                getImage("jvm_big.png"));
        String name = (String) JOptionPane.showInputDialog(
                this,
                LangResource.getString("message#174"),
                LangResource.getString("New JVM Configuration File"),
                JOptionPane.QUESTION_MESSAGE,
                icon, null, null);

        if (!validateInstName(currentNode, name, "jvm")) {
            return;
        }

        jvm = new JVM(currentNode, name);

        JvmDialog jvm_dlg = new JvmDialog(Workspace.getUI().getFrame());
        jvm_dlg.setData(jvm);
        jvm_dlg.setVisible(true);

        if (!jvm_dlg.isCancelled()) {
            try {
                jvm.save();
                currentNode.add(jvm);
            } catch (IOException ex) {
                Workspace.logException(LangResource.getString("message#169")
                        + ex.getMessage());
                JOptionPane.showMessageDialog(this, LangResource.getString("message#169")
                        + ex.getMessage());
            }
        }
    }

    /**
     * Edit jvm.
     */
    public void edit() {
        if (currentNode == null || !(currentNode instanceof JVM)) {
            return;
        }
        JVM jvm = (JVM) currentNode;

        JvmDialog jvm_dlg = new JvmDialog(Workspace.getUI().getFrame());
        jvm_dlg.setData(jvm);
        jvm_dlg.setVisible(true);

        if (!jvm_dlg.isCancelled()) {
            try {
                jvm.save();
                String source = null;
                String cover = null;
                source = (jvm).getSource();
                cover = (jvm).getDocs();
                jarInspector.setSource(source);
            } catch (IOException ex) {
                Workspace.logException(LangResource.getString("message#169")
                        + ex.getMessage());
                JOptionPane.showMessageDialog(this, LangResource.getString("message#187")
                        + ex.getMessage());
            }
        }
    }
}
