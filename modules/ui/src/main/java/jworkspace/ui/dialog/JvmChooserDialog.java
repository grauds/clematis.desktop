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
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.LangResource;
import jworkspace.installer.DefinitionNode;
import jworkspace.installer.JVM;
import jworkspace.kernel.Workspace;

/**
 * This dialog shows a tree of installed jvms in workspace.
 * @author Anton Troshin
 */
@SuppressFBWarnings("UR_UNINIT_READ")
public class JvmChooserDialog extends ComponentDialog {

    private static final String JVM_CHOOSER_DLG_TITLE = "JvmChooserDlg.title";

    private KTreeTable treeTable;

    private JVM vm = null;

    JvmChooserDialog(Frame parent) {
        super(parent, LangResource.getString(JVM_CHOOSER_DLG_TITLE), true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setComment(new ImageIcon(Workspace.getResourceManager().getImage("jvm_big.png")),
            LangResource.getString(JVM_CHOOSER_DLG_TITLE));
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

    @SuppressWarnings("MagicNumber")
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