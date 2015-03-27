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

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import kiwi.ui.ImmutableCellEditor;
import kiwi.ui.KButton;
import kiwi.ui.dialog.ComponentDialog;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Carrier class for <code>jworkspace.ui.UserDetailsPanel</code>
 */
public class UserDetailsDialog extends ComponentDialog implements ActionListener
{
    private UserDetailsPanel first_panel;
    private JTabbedPane tabbed_pane = null;
    private KButton b_new, b_delete;
    private JTable table;
    private DefaultTableModel tmodel;
    private static int maxPortraitWidth = 250;
    private static int maxPortraitHeight = 250;

    public UserDetailsDialog(Frame parent)
    {
        super(parent, LangResource.getString("UserDetailsDlg.title"), true);
    }

    protected boolean accept()
    {
        String name, value;
        int rows = table.getRowCount();

        for (int i = 0; i < rows; i++)
        {
            name = (String) table.getValueAt(i, 0);
            value = (String) table.getValueAt(i, 1);
            Workspace.getProfilesEngine().getParameters().put(name, value);
        }
        return (first_panel.syncData());
    }

    public void actionPerformed(ActionEvent ev)
    {
        Object o = ev.getSource();

        if (o == b_new)
        {
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                                           getImage("user_var.png"));

            String name = (String) JOptionPane.showInputDialog
                    (this,
                     LangResource.getString("UserDetailsDlg.addParam.question"),
                     LangResource.getString("UserDetailsDlg.addParam.title"),
                     JOptionPane.INFORMATION_MESSAGE,
                     icon,
                     null, null);

            if (name != null)
            {
                if (Workspace.getProfilesEngine().getParameters().get(name) != null)
                {
                    JOptionPane.showMessageDialog(this,
                                                  LangResource.getString("UserDetailsDlg.addParam.alreadyExists"));
                }
                else
                {
                    Object row[] = new Object[2];
                    row[0] = name;
                    row[1] = "";
                    tmodel.addRow(row);
                }
            }
        }
        else if (o == b_delete)
        {
            int rows[] = table.getSelectedRows();
            if (rows.length == 0) return;
            String name = (String) table.getValueAt(rows[0], 0);

            if (JOptionPane.showConfirmDialog(this,
                                              LangResource.getString("UserDetailsDlg.deleteParam.message") + "?",
                                              LangResource.getString("UserDetailsDlg.deleteParam.title"),
                                              JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                tmodel.removeRow(rows[0]);
                Workspace.getProfilesEngine().getParameters().remove(name);
            }
        }
    }

    protected JComponent buildDialogUI()
    {
        setComment(null);

        String fileName = Workspace.getUserHome() + "portrait.jpg";
        Image p = null;
        try {
            p = Imaging.getBufferedImage(new File(fileName));
        } catch (ImageReadException | IOException e) {
            // do not pay attention
        }

        if (p != null) {
            ImageIcon photo = new ImageIcon(p);

            if (photo.getIconHeight() > 0 && photo.getIconWidth() > 0)
            {
                scaleImage(p, photo);
            }
        }
        else
        {
            setIcon(null);
        }

        tabbed_pane = new JTabbedPane();
        tabbed_pane.setOpaque(false);
        first_panel = new UserDetailsPanel();
        tabbed_pane.addTab(LangResource.getString("UserDetailsDlg.generalTab"),
                           first_panel);
        tabbed_pane.addTab(LangResource.getString("UserDetailsDlg.variablesTab"),
                           buildPropertyEditor());

        return (tabbed_pane);
    }

    private void scaleImage(Image p, ImageIcon photo) {

        if (photo.getIconHeight() > maxPortraitHeight)
        {
            setIcon(new ImageIcon
                    (p.getScaledInstance(-1, maxPortraitHeight, Image.SCALE_SMOOTH)));
        }
        else if (photo.getIconWidth() > maxPortraitWidth)
        {
            setIcon(new ImageIcon
                    (p.getScaledInstance(maxPortraitWidth, -1, Image.SCALE_SMOOTH)));
        }
        else
        {
            setIcon(new ImageIcon(p));
        }
    }

    private KPanel buildPropertyEditor()
    {
        KPanel property_editor = new KPanel();
        property_editor.setLayout(new BorderLayout());
        /**
         * Buttons panel.
         */
        KPanel p_buttons = new KPanel();
        KPanel p_but_carrier = new KPanel();
        p_buttons.setLayout(new GridLayout(0, 1, 5, 5));
        p_but_carrier.setLayout(new BorderLayout());
        ResourceManager kresmgr = KiwiUtils.getResourceManager();

        b_new = new KButton(kresmgr.getIcon("plus.png"));
        b_new.setToolTipText
                (LangResource.getString("UserDetailsDlg.addParam.tooltip"));
        b_new.addActionListener(this);
        b_new.setDefaultCapable(false);
        p_buttons.add(b_new);

        b_delete = new KButton(kresmgr.getIcon("minus.png"));
        b_delete.setToolTipText
                (LangResource.getString("UserDetailsDlg.deleteParam.tooltip"));
        b_delete.addActionListener(this);
        b_delete.setDefaultCapable(false);
        p_buttons.add(b_delete);

        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);

        TableColumn col;

        tmodel = new DefaultTableModel();
        table.setModel(tmodel);

        tmodel.addColumn(LangResource.getString("UserDetailsDlg.variable"));
        table.addColumn(col = new TableColumn(0, 20));
        col.setCellEditor(new ImmutableCellEditor());

        tmodel.addColumn(LangResource.getString("UserDetailsDlg.value"));
        table.addColumn(col = new TableColumn(1, 20));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setColumnSelectionAllowed(false);

        JScrollPane scrollpane = new JScrollPane(table);

        property_editor.add(scrollpane, BorderLayout.CENTER);
        p_but_carrier.add(p_buttons, BorderLayout.NORTH);
        property_editor.add(p_but_carrier, BorderLayout.EAST);

        setAcceptButtonText(LangResource.getString("UserDetailsDlg.var.save"));

        ListSelectionModel sel = new DefaultListSelectionModel();
        table.setSelectionModel(sel);
        sel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sel.addListSelectionListener(
                new ListSelectionListener()
                {
                    public void valueChanged(ListSelectionEvent ev)
                    {
                        b_delete.setEnabled(!(table.getSelectionModel().isSelectionEmpty()));
                    }
                }
        );
        property_editor.setPreferredSize(new Dimension(200, 200));
        return property_editor;
    }

    private void refresh()
    {
        tmodel.setNumRows(0);
        Object row[] = new Object[2];
        Enumeration e = Workspace.getProfilesEngine().getParameters().keys();

        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String value = Workspace.getProfilesEngine().getParameters().getString(name);

            row[0] = name;
            row[1] = value;
            tmodel.addRow(row);
        }
    }

    /** Show or hide the dialog. */
    public void setVisible(boolean flag)
    {
        if (flag)
            refresh();

        super.setVisible(flag);
    }

    public void dispose()
    {
        destroy();
        super.dispose();
    }

    public void setData()
    {
        first_panel.setData();
    }
}