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
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KTreeTable;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.installer.DefinitionNode;
import jworkspace.installer.Library;
import jworkspace.ui.WorkspaceGUI;

/**
 * This dialog shows a tree of installed libraries in workspace.
 * @author Anton Troshin
 */
public class LibraryChooserDialog extends ComponentDialog {

    private static final String LIBRARY_CHOOSER_DLG_TITLE = "LibraryChooserDlg.title";
    private KTreeTable treeTable;
    private Library[] lib = null;

    public LibraryChooserDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString(LIBRARY_CHOOSER_DLG_TITLE), true);
        setComment(new ImageIcon(WorkspaceGUI.getResourceManager().getImage("library_big.png")),
            WorkspaceResourceAnchor.getString(LIBRARY_CHOOSER_DLG_TITLE));
    }

    protected boolean accept() {
        Object[] selectedItems = treeTable.getSelectedItems();
        lib = new Library[selectedItems.length];
        for (int i = 0; i < selectedItems.length; i++) {
            DefinitionNode n = (DefinitionNode) selectedItems[i];
            if ((n != null) && (n.getClass() == Library.class)) {
                lib[i] = (Library) n;
            }
        }
        return true;
    }

    protected JComponent buildDialogUI() {
        setComment(WorkspaceResourceAnchor.getString("LibraryChooserDlg.comment"));
        treeTable = new KTreeTable();
       // todo treeTable.setTreeModel(Workspace.getInstallEngine().getLibraryModel());
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

    public Library[] getSelectedLibraries() {
        return lib != null ? Arrays.copyOf(lib, lib.length) : null;
    }
}