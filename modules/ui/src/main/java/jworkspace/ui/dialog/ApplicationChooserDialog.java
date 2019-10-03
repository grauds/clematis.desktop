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

import com.hyperrealm.kiwi.ui.KTreeTable;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.installer.Application;
import jworkspace.installer.DefinitionNode;
import jworkspace.ui.WorkspaceGUI;

/**
 * Application chooser
 * @author Anton Troshin
 */
@SuppressFBWarnings("UR_UNINIT_READ")
public class ApplicationChooserDialog extends ComponentDialog {

    private static final String TITLE = WorkspaceResourceAnchor.getString("ApplicationChooser.title");
    private KTreeTable treeTable;

    private Application application = null;

    /**
     * Public constructor is used by classes outside
     * this package.
     */
    public ApplicationChooserDialog(Frame parent) {
        super(parent, TITLE, true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setComment(new ImageIcon(WorkspaceGUI.getResourceManager().getImage("application_big.png")), TITLE);
    }

    protected boolean accept() {
        Object selectedItem = treeTable.getSelectedItem();
        if (selectedItem instanceof DefinitionNode) {
            DefinitionNode n = (DefinitionNode) selectedItem;
            if (n.getClass() == Application.class) {
                application = (Application) n;
            }
            return (application != null);
        }
        return false;
    }

    protected JComponent buildDialogUI() {
        setComment(WorkspaceResourceAnchor.getString("ApplicationChooser.comment"));
        treeTable = new KTreeTable();
     // todo   treeTable.setTreeModel(Workspace.getInstallEngine().getApplicationModel());
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroller = new JScrollPane(treeTable);
        scroller.setOpaque(false);
        return scroller;
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public Application getSelectedApplication() {
        return (application);
    }
}