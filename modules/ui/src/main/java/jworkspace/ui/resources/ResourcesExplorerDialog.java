package jworkspace.ui.resources;
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

public class ResourcesExplorerDialog  extends ComponentDialog {

    private ResourceExplorerPanel rexplorer;

    private String path;

    public ResourcesExplorerDialog(Frame parent, String title, String path) {
        super(parent, title, true);
        setResizable(true);
        this.path = path;
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                setData();
            }
        });
    }

    @SuppressWarnings("MagicNumber")
    protected JComponent buildDialogUI() {
        setComment(null);

        KPanel holder = new KPanel();
        holder.setLayout(new BorderLayout(5, 5));

        rexplorer = new ResourceExplorerPanel(this);

        JScrollPane scroll = new JScrollPane(rexplorer,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        holder.add(scroll, BorderLayout.CENTER);
        return holder;
    }

    protected void cancel() {
        destroy();
    }

    public ImageIcon[] getSelectedImages() {
        return rexplorer.getSelectedImages();
    }

    protected boolean accept() {
        return true;
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public void setData(String path) {
        this.path = path;
        if (isVisible()) {
            setData();
        }
    }

    private void setData() {
        rexplorer.setPath(path);
    }
}