package jworkspace.ui.widgets;

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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.ClassCache;

/**
 * Resource explorer dialog is a holder for ResourceExplorerPanel.
 * @author Anton Troshin
 */
public class ResourceExplorerDialog extends ComponentDialog
    implements ActionListener {

    private ResourceExplorerPanel rexplorer;

    private JButton bIconBrowse;

    private JTextField tf;

    private String path;

    public ResourceExplorerDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("ResourceExplorerDlg.title"), true);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                setData();
            }
        });
    }

    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();
        if (o == bIconBrowse) {
            File f = ClassCache.chooseArchiveOrDir(this);
            if (f != null) {
                tf.setText(f.getAbsolutePath());
                rexplorer.setPath(f.getAbsolutePath());
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    protected JComponent buildDialogUI() {
        setComment(null);
        KPanel holder = new KPanel();
        holder.setLayout(new BorderLayout(5, 5));
        /*
         * Browser panel.
         */
        KPanel browsePanel = new KPanel();
        browsePanel.setLayout(new BorderLayout(5, 5));
        //browsePanel.setPreferredSize(new Dimension(150, 30));
        browsePanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel l = new JLabel(WorkspaceResourceAnchor.getString("ResourceExplorerDlg.path"));
        browsePanel.add(l, BorderLayout.WEST);

        bIconBrowse = new JButton("...");
        bIconBrowse.setToolTipText(WorkspaceResourceAnchor.getString("ResourceExplorerDlg.browse.tooltip"));
        bIconBrowse.addActionListener(this);
        bIconBrowse.setDefaultCapable(false);
        bIconBrowse.setOpaque(false);
        browsePanel.add(bIconBrowse, BorderLayout.EAST);

        tf = new JTextField(15);
      //  tf.setPreferredSize(new Dimension(30, 20));
        tf.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    rexplorer.setPath(tf.getText());
                    evt.consume();
                }
            }
        });
        browsePanel.add(tf, BorderLayout.CENTER);

        rexplorer = new ResourceExplorerPanel();

        holder.add(browsePanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(rexplorer,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

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

    public void setHint(boolean isTextureChooser) {
        rexplorer.isTextureChooser = isTextureChooser;
    }

    public void setData(String path) {
        this.path = path;

        if (isVisible()) {
            setData();
        }
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    private void setData() {
        rexplorer.setPath(path);
        if (path != null) {
            tf.setText(path);
        } else {
            tf.setText("");
        }
    }
}