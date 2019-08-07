package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2000 Anton Troshin

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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;

/**
 * Carrier class for <code>jworkspace.ui.UserDetailsPanel</code>
 * @author Anton Troshin
 */
public class UserDetailsDialog extends ComponentDialog implements ActionListener {
    private static final int MAX_PORTRAIT_WIDTH = 250;
    private static final int MAX_PORTRAIT_HEIGHT = 250;
    private UserDetailsPanel userDetailsPanel;
    private KButton bNew, bDelete;
    private JTable table;
    private DefaultTableModel tmodel;

    public UserDetailsDialog(Frame parent) {
        super(parent, LangResource.getString("UserDetailsDlg.title"), true);
    }

    protected boolean accept() {
        String name, value;
        int rows = table.getRowCount();

        for (int i = 0; i < rows; i++) {
            name = (String) table.getValueAt(i, 0);
            value = (String) table.getValueAt(i, 1);
            Workspace.getProfilesEngine().getParameters().put(name, value);
        }
        return (userDetailsPanel.syncData());
    }

    public void actionPerformed(ActionEvent ev) {
        Object o = ev.getSource();

        if (o == bNew) {
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                getImage("user_var.png"));

            String name = (String) JOptionPane.showInputDialog(this,
                    LangResource.getString("UserDetailsDlg.addParam.question"),
                    LangResource.getString("UserDetailsDlg.addParam.title"),
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    null, null);

            if (name != null && Workspace.getProfilesEngine().getParameters().get(name) != null) {
                JOptionPane.showMessageDialog(this,
                    LangResource.getString("UserDetailsDlg.addParam.alreadyExists"));
            } else if (name != null){
                Object[] row = new Object[2];
                row[0] = name;
                row[1] = "";
                tmodel.addRow(row);
            }
        } else if (o == bDelete) {
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) {
                return;
            }
            String name = (String) table.getValueAt(rows[0], 0);

            if (JOptionPane.showConfirmDialog(this,
                LangResource.getString("UserDetailsDlg.deleteParam.message") + "?",
                LangResource.getString("UserDetailsDlg.deleteParam.title"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tmodel.removeRow(rows[0]);
                Workspace.getProfilesEngine().getParameters().remove(name);
            }
        }
    }

    protected JComponent buildDialogUI() {
        setComment(null);

        String fileName = Workspace.getUserHomePath() + "portrait.jpg";
        Image p = null;
        try {
            p = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            // do not pay attention
        }

        if (p != null) {
            ImageIcon photo = new ImageIcon(p);

            if (photo.getIconHeight() > 0 && photo.getIconWidth() > 0) {
                scaleImage(p, photo);
            }
        } else {
            setIcon(null);
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);

        userDetailsPanel = new UserDetailsPanel();
        tabbedPane.addTab(LangResource.getString("UserDetailsDlg.generalTab"), userDetailsPanel);
        tabbedPane.addTab(LangResource.getString("UserDetailsDlg.variablesTab"), buildPropertyEditor());

        return (tabbedPane);
    }

    private void scaleImage(Image p, ImageIcon photo) {

        if (photo.getIconHeight() > MAX_PORTRAIT_HEIGHT) {
            setIcon(new ImageIcon(p.getScaledInstance(-1, MAX_PORTRAIT_HEIGHT, Image.SCALE_SMOOTH)));
        } else if (photo.getIconWidth() > MAX_PORTRAIT_WIDTH) {
            setIcon(new ImageIcon(p.getScaledInstance(MAX_PORTRAIT_WIDTH, -1, Image.SCALE_SMOOTH)));
        } else {
            setIcon(new ImageIcon(p));
        }
    }

    @SuppressWarnings("MagicNumber")
    private KPanel buildPropertyEditor() {
        KPanel propertyEditor = new KPanel();
        propertyEditor.setLayout(new BorderLayout());
        /*
         * Buttons panel.
         */
        KPanel pButtons = new KPanel();
        KPanel pButCarrier = new KPanel();
        pButtons.setLayout(new GridLayout(0, 1, 5, 5));
        pButCarrier.setLayout(new BorderLayout());
        ResourceManager kresmgr = KiwiUtils.getResourceManager();

        bNew = new KButton(kresmgr.getIcon("plus.png"));
        bNew.setToolTipText(LangResource.getString("UserDetailsDlg.addParam.tooltip"));
        bNew.addActionListener(this);
        bNew.setDefaultCapable(false);
        pButtons.add(bNew);

        bDelete = new KButton(kresmgr.getIcon("minus.png"));
        bDelete.setToolTipText(LangResource.getString("UserDetailsDlg.deleteParam.tooltip"));
        bDelete.addActionListener(this);
        bDelete.setDefaultCapable(false);
        pButtons.add(bDelete);

        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);

        tmodel = new DefaultTableModel();
        table.setModel(tmodel);

        tmodel.addColumn(LangResource.getString("UserDetailsDlg.variable"));
        table.addColumn(new TableColumn(0, 20));
        // todo col.setCellEditor(new ImmutableCellEditor());

        tmodel.addColumn(LangResource.getString("UserDetailsDlg.value"));
        table.addColumn(new TableColumn(1, 20));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setColumnSelectionAllowed(false);

        JScrollPane scrollpane = new JScrollPane(table);

        propertyEditor.add(scrollpane, BorderLayout.CENTER);
        pButCarrier.add(pButtons, BorderLayout.NORTH);
        propertyEditor.add(pButCarrier, BorderLayout.EAST);

        setAcceptButtonText(LangResource.getString("UserDetailsDlg.var.save"));

        ListSelectionModel sel = new DefaultListSelectionModel();
        table.setSelectionModel(sel);
        sel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sel.addListSelectionListener(
            ev -> bDelete.setEnabled(!(table.getSelectionModel().isSelectionEmpty()))
        );
        propertyEditor.setPreferredSize(new Dimension(200, 200));
        return propertyEditor;
    }

    private void refresh() {
        tmodel.setNumRows(0);
        Object[] row = new Object[2];
        Enumeration e = Workspace.getProfilesEngine().getParameters().keys();

        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = Workspace.getProfilesEngine().getParameters().getString(name);

            row[0] = name;
            row[1] = value;
            tmodel.addRow(row);
        }
    }

    /**
     * Show or hide the dialog.
     */
    public void setVisible(boolean flag) {
        if (flag) {
            refresh();
        }

        super.setVisible(flag);
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    public void setData() {
        userDetailsPanel.setData();
    }
}