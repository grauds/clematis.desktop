package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

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

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KTreeTable;
import jworkspace.LangResource;
import jworkspace.installer.DefinitionNode;
import jworkspace.installer.JVM;
import jworkspace.kernel.Workspace;
import kiwi.ui.dialog.ComponentDialog;

/**
 * This dialog shows a tree of installed jvms in workspace.
 */
public class JvmChooserDialog extends ComponentDialog {
    private KTreeTable treeTable;
    private JVM vm = null;

    public JvmChooserDialog(Frame parent) {
        super(parent, LangResource.getString("JvmChooserDlg.title"), true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTopIcon(new ImageIcon(Workspace.getResourceManager().
            getImage("jvm_big.png")));
    }

    protected boolean accept() {
        Object selectedItem = treeTable.getSelectedItem();
        if (selectedItem instanceof DefinitionNode) {
            DefinitionNode n = (DefinitionNode) selectedItem;
            if (n.getClass() == JVM.class) {
                vm = (JVM) n;
            }
            return (vm != null);
        }
        return false;
    }

    protected JComponent buildDialogUI() {
        setComment(LangResource.getString("JvmChooserDlg.comment"));
        treeTable = new KTreeTable();
        treeTable.setTreeModel(Workspace.getInstallEngine().getJvmModel());
        JScrollPane scroller = new JScrollPane(treeTable);
        scroller.setOpaque(false);
        return scroller;
    }

    protected Border getCommentBorder() {
        return new EmptyBorder(0, 5, 0, 5);
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public JVM getSelectedJVM() {
        return (vm);
    }
}