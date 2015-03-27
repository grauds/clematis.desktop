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
import jworkspace.installer.DefinitionNode;
import jworkspace.installer.Library;
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
 * This dialog shows a tree of installed libraries in workspace.
 */
public class LibraryChooserDialog extends ComponentDialog
{
    private JList list;
    private Library[] lib = null;

    public LibraryChooserDialog(Frame parent)
    {
        super(parent, LangResource.getString("LibraryChooserDlg.title"), true);
        setTopIcon(new ImageIcon(Workspace.getResourceManager().
                                 getImage("library_big.png")));
    }

    protected boolean accept()
    {
        Object[] obj_le = list.getSelectedValues();
        lib = new Library[obj_le.length];
        for (int i = 0; i < obj_le.length; i++)
        {
            ITreeNode node = ((TreeModelListAdapter.ListEntry) obj_le[i]).getObject();
            DefinitionNode n = (DefinitionNode) node.getObject();
            if ((n != null) && (n.getClass() == Library.class))
                lib[i] = (Library) n;
        }
        return (lib != null);
    }

    protected JComponent buildDialogUI()
    {
        setComment(LangResource.getString("LibraryChooserDlg.comment"));
        list = new JList();
        TreeModelListAdapter adapter = new TreeModelListAdapter(list);
        adapter.setTreeModel(Workspace.getInstallEngine().getLibraryModel());
        list.setModel(adapter);
        list.setCellRenderer(new ModelListCellRenderer(Workspace.getInstallEngine().
                                                       getLibraryModel()));
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

    public Library[] getSelectedLibraries()
    {
        return (lib);
    }
}