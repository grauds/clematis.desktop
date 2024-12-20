package jworkspace.ui.profiles;

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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.widgets.AvatarChooser;

/**
 * Carrier class for <code>jworkspace.ui.UserDetailsPanel</code>
 * @author Anton Troshin
 */
public class UserDetailsDialog extends ComponentDialog implements ActionListener {

    public static final String AVATAR_IMAGE = "portrait.jpg";

    public static final String USER_VARIABLES = "user_var.png";

    private UserDetailsPanel userDetailsPanel;

    private KButton bNew, bDelete;

    private JTable table;

    private DefaultTableModel tmodel;

    private AvatarChooser avatarChooser;

    @SuppressWarnings("checkstyle:MagicNumber")
    public UserDetailsDialog(Frame parent) {
        super(parent, WorkspaceResourceAnchor.getString("UserDetailsDlg.title"), true);
        getMainContainer().setBorder(new EmptyBorder(5, 15, 5, 5));
    }

    public void setIcon(Icon icon) {
        getAvatarChooser().setIcon((ImageIcon) icon);
    }

    protected AvatarChooser getAvatarChooser() {
        if (avatarChooser == null) {
            avatarChooser = new AvatarChooser(
                new ImageIcon(WorkspaceGUI.getResourceManager().getImage("no_avatar.png"))
            );
        }
        return avatarChooser;
    }

    protected boolean accept() {

        try {
            File file = ServiceLocator
                .getInstance()
                .getProfilesManager()
                .ensureUserHomePath()
                .resolve(AVATAR_IMAGE)
                .toFile();

            ImageIcon image = getAvatarChooser().getIcon();
            RenderedImage rendered;
            if (image instanceof RenderedImage) {
                rendered = (RenderedImage) image;
            } else {
                BufferedImage buffered = new BufferedImage(
                    image.getIconWidth(),
                    image.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g = buffered.createGraphics();
                g.drawImage(image.getImage(), 0, 0, null);
                g.dispose();
                rendered = buffered;
            }
            ImageIO.write(rendered, "JPG", file);

        } catch (IOException e) {
            // do not pay attention
        }

        String name, value;
        int rows = table.getRowCount();

        for (int i = 0; i < rows; i++) {
            name = (String) table.getValueAt(i, 0);
            value = (String) table.getValueAt(i, 1);
            ServiceLocator
                .getInstance()
                .getProfilesManager()
                .getCurrentProfile()
                .getParameters()
                .put(name, value);
        }
        return (userDetailsPanel.syncData());
    }

    public void actionPerformed(ActionEvent ev) {
        Object o = ev.getSource();

        if (o == bNew) {
            ImageIcon icon = new ImageIcon(
                WorkspaceGUI.getResourceManager().getImage(USER_VARIABLES)
            );

            String name = (String) JOptionPane.showInputDialog(
                this,
                WorkspaceResourceAnchor.getString("UserDetailsDlg.addParam.question"),
                WorkspaceResourceAnchor.getString("UserDetailsDlg.addParam.title"),
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                null,
                null
            );

            if (name != null && ServiceLocator
                .getInstance()
                .getProfilesManager()
                .getCurrentProfile()
                .getParameters()
                .get(name) != null) {

                JOptionPane.showMessageDialog(
                    this,
                    WorkspaceResourceAnchor.getString("UserDetailsDlg.addParam.alreadyExists")
                );

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

            if (JOptionPane.showConfirmDialog(
                this,
                WorkspaceResourceAnchor.getString("UserDetailsDlg.deleteParam.message") + "?",
                WorkspaceResourceAnchor.getString("UserDetailsDlg.deleteParam.title"),
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {

                tmodel.removeRow(rows[0]);
                ServiceLocator
                    .getInstance()
                    .getProfilesManager()
                    .getCurrentProfile()
                    .getParameters()
                    .remove(name);
            }
        }
    }

    protected JComponent buildDialogUI() {

        setComment(null);

        getMainContainer().add(WEST_POSITION, getAvatarChooser());

        Image p = null;
        try {
            File file = ServiceLocator
                .getInstance()
                .getProfilesManager()
                .ensureUserHomePath()
                .resolve(AVATAR_IMAGE)
                .toFile();

            p = ImageIO.read(file);
        } catch (IOException e) {
            // do not pay attention
        }

        if (p != null) {
            ImageIcon photo = new ImageIcon(p);
            if (photo.getIconHeight() > 0 && photo.getIconWidth() > 0) {
                setIcon(photo);
            }
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);

        userDetailsPanel = new UserDetailsPanel();
        tabbedPane.addTab(WorkspaceResourceAnchor.getString("UserDetailsDlg.generalTab"), userDetailsPanel);
        tabbedPane.addTab(WorkspaceResourceAnchor.getString("UserDetailsDlg.variablesTab"), buildPropertyEditor());

        return (tabbedPane);
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

        bNew = new KButton(new ImageIcon(kresmgr.getImage("plus.png")));
        bNew.setToolTipText(WorkspaceResourceAnchor.getString("UserDetailsDlg.addParam.tooltip"));
        bNew.addActionListener(this);
        bNew.setDefaultCapable(false);
        pButtons.add(bNew);

        bDelete = new KButton(new ImageIcon(kresmgr.getImage("minus.png")));
        bDelete.setToolTipText(WorkspaceResourceAnchor.getString("UserDetailsDlg.deleteParam.tooltip"));
        bDelete.addActionListener(this);
        bDelete.setDefaultCapable(false);
        pButtons.add(bDelete);

        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);

        tmodel = new DefaultTableModel();
        table.setModel(tmodel);

        tmodel.addColumn(WorkspaceResourceAnchor.getString("UserDetailsDlg.variable"));
        table.addColumn(new TableColumn(0, 20));
        // todo col.setCellEditor(new ImmutableCellEditor());

        tmodel.addColumn(WorkspaceResourceAnchor.getString("UserDetailsDlg.value"));
        table.addColumn(new TableColumn(1, 20));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setColumnSelectionAllowed(false);

        JScrollPane scrollpane = new JScrollPane(table);

        propertyEditor.add(scrollpane, BorderLayout.CENTER);
        pButCarrier.add(pButtons, BorderLayout.NORTH);
        propertyEditor.add(pButCarrier, BorderLayout.EAST);

        setAcceptButtonText(WorkspaceResourceAnchor.getString("UserDetailsDlg.var.save"));

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
        Enumeration<Object> e = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .getCurrentProfile()
            .getParameters()
            .keys();

        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = ServiceLocator
                .getInstance()
                .getProfilesManager()
                .getCurrentProfile()
                .getParameters()
                .getString(name);

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