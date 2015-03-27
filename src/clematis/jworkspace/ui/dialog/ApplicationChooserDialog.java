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
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import jworkspace.LangResource;
import jworkspace.installer.Application;
import jworkspace.installer.DefinitionNode;
import jworkspace.kernel.Workspace;
import kiwi.ui.ModelListCellRenderer;
import kiwi.ui.dialog.ComponentDialog;
import kiwi.ui.model.ITreeNode;
import kiwi.ui.model.TreeModelListAdapter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Application chooser.
 */
public class ApplicationChooserDialog extends ComponentDialog
{
    private JList list;
    private Application application = null;

    /**
     * Public constructor is used by classes outside
     * this package.
     */
    public ApplicationChooserDialog(Frame parent)
    {
        super(parent, LangResource.getString("ApplicationChooser.title"), true);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTopIcon(new ImageIcon(Workspace.getResourceManager().
                                 getImage("application_big.png")));
    }

    protected boolean accept()
    {
        TreeModelListAdapter.ListEntry le =
                (TreeModelListAdapter.ListEntry) list.getSelectedValue();
        ITreeNode node = le.getObject();
        DefinitionNode n = (DefinitionNode) node.getObject();
        if ((n != null) && (n.getClass() == Application.class))
            application = (Application) n;
        return (application != null);
    }

    protected JComponent buildDialogUI()
    {
        setComment(LangResource.getString("ApplicationChooser.comment"));
        list = new JList();
        TreeModelListAdapter adapter = new TreeModelListAdapter(list);
        adapter.setTreeModel(Workspace.getInstallEngine().getApplicationModel());
        list.setModel(adapter);
        list.setCellRenderer(new ModelListCellRenderer(Workspace.getInstallEngine().
                                                       getApplicationModel()));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroller = new JScrollPane(list);
        scroller.setOpaque(false);
        return scroller;
    }

    protected Border getCommentBorder()
    {
        return new EmptyBorder(0, 5, 0, 5);
    }

    public void dispose()
    {
        destroy();
        super.dispose();
    }

    public Application getSelectedApplication()
    {
        return (application);
    }
}