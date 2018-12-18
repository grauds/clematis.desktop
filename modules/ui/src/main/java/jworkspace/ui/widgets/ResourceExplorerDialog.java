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
import java.awt.Dimension;
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
import jworkspace.LangResource;
import jworkspace.ui.ClassCache;
import kiwi.ui.dialog.ComponentDialog;

/**
 * Resource explorer dialog is a holder for ResourceExplorerPanel.
 */
public class ResourceExplorerDialog extends ComponentDialog
    implements ActionListener {
    /**
     * String for caching it in Workspace Cache
     */
    public static final String RESOURCE_EXPLORER = "RESOURCE_EXPLORER";
    ResourceExplorerPanel rexplorer;
    JButton b_icon_browse;
    JTextField tf;
    String path;

    public ResourceExplorerDialog(Frame parent) {
        super(parent, LangResource.getString("ResourceExplorerDlg.title"), true);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                setData();
            }
        });
    }

    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();
        if (o == b_icon_browse) {
            File f = ClassCache.chooseArchiveOrDir(this);
            if (f != null) {
                tf.setText(f.getAbsolutePath());
                rexplorer.setPath(f.getAbsolutePath());
            }
        }
    }

    protected JComponent buildDialogUI() {
        setComment(null);
        KPanel holder = new KPanel();
        holder.setLayout(new BorderLayout(5, 5));
        /**
         * Browser panel.
         */
        KPanel browse_panel = new KPanel();
        browse_panel.setLayout(new BorderLayout(5, 5));
        browse_panel.setPreferredSize(new Dimension(150, 30));
        browse_panel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel l = new JLabel(LangResource.getString("ResourceExplorerDlg.path"));
        browse_panel.add(l, BorderLayout.WEST);

        b_icon_browse = new JButton("...");
        b_icon_browse.setToolTipText
            (LangResource.getString("ResourceExplorerDlg.browse.tooltip"));
        b_icon_browse.addActionListener(this);
        b_icon_browse.setDefaultCapable(false);
        b_icon_browse.setOpaque(false);
        browse_panel.add(b_icon_browse, BorderLayout.EAST);

        tf = new JTextField(15);
        tf.setPreferredSize(new Dimension(30, 20));
        tf.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    rexplorer.setPath(tf.getText());
                    evt.consume();
                }
            }
        });
        browse_panel.add(tf, BorderLayout.CENTER);

        rexplorer = new ResourceExplorerPanel();

        holder.add(browse_panel, BorderLayout.NORTH);
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