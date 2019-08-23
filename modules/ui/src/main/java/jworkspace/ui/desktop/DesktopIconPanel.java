package jworkspace.ui.desktop;

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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.installer.ApplicationDataSource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.ClassCache;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.dialog.ApplicationChooserDialog;
import jworkspace.ui.widgets.ImageRenderer;
import jworkspace.ui.widgets.ResourceExplorerDialog;

/**
 * This panel gathers user input for desktop icon properties.
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class DesktopIconPanel extends KPanel implements ActionListener {

    private JTextField tName, tScriptedMethod, tNativeCommand, tSourceScript, tJavaApp, field;

    private JTextArea tDesc;

    private JButton bIconBrowse, bScriptBrowse, bAppBrowse, bWdBrowse, bNativeBrowse, bLibBrowser;

    private DesktopIcon desktopIcon;

    private ImageRenderer lImage;

    private int mode = DesktopConstants.JAVA_APP_MODE;

    private JRadioButton rb1, rb2, rb3, rb4;

    /**
     * Default constructor
     */
    DesktopIconPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());

        tabbedPane.add(WorkspaceResourceAnchor.getString("DesktopIconPanel.imagePanel.title"),
            createFirstPanel());
        tabbedPane.add(WorkspaceResourceAnchor.getString("DesktopIconPanel.commandPanel.title"),
            createModesPanel());

        tDesc = new JTextArea(7, 1);
        tDesc.setLineWrap(true);
        tDesc.setWrapStyleWord(true);

        tabbedPane.add(WorkspaceResourceAnchor.getString("DesktopIconPanel.descPanel.title"),
            new JScrollPane(tDesc));
        tabbedPane.setOpaque(false);
        disableAllOnModesPanel();
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();
        if (o == bIconBrowse) {
            Image im = ClassCache.chooseImage(this);
            if (im != null) {
                lImage.setImage(im);
            }
        } else if (o == bScriptBrowse) {
            JFileChooser chooser
                = ClassCache.getFileChooser(WorkspaceResourceAnchor.getString("DesktopIconPanel.chooseScript.title"),
                    new String[]{"bsh"},
                    WorkspaceResourceAnchor.getString("DesktopIconPanel.script")
                );

            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);

            if (chooser.showOpenDialog(Workspace.getUi().getFrame()) == JFileChooser.APPROVE_OPTION) {
                File scriptFile = chooser.getSelectedFile();
                tSourceScript.setText(scriptFile.getAbsolutePath());
            }
        } else if (o == bAppBrowse) {
            ApplicationChooserDialog chooser = new ApplicationChooserDialog(Workspace.getUi().getFrame());
            chooser.setVisible(true);
            if (chooser.isCancelled()) {
                return;
            }
            tJavaApp.setText(chooser.getSelectedApplication().getLinkString().
                substring(ApplicationDataSource.ROOT.length()
                ));
        } else if (o == bWdBrowse) {
            File dir = ClassCache.chooseDirectory(Workspace.getUi().getFrame());
            if (dir != null) {
                field.setText(dir.getAbsolutePath());
            }
        } else if (o == bNativeBrowse) {
            JFileChooser chooser = ClassCache.getFileChooser(
                WorkspaceResourceAnchor.getString(DesktopConstants.DESKTOP_ICON_PANEL_NATIVE_COMMAND_BROWSE),
                    null, WorkspaceResourceAnchor.getString("DesktopIconPanel.native.command")
                );
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(Workspace.getUi().getFrame()) == JFileChooser.APPROVE_OPTION) {
                tNativeCommand.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } else if (o == bLibBrowser) {
            ResourceExplorerDialog resBrowser = new ResourceExplorerDialog(Workspace.getUi().getFrame());
            callResourceBrowser(resBrowser);
        } else if (o == rb1) {
            disableAllOnModesPanel();
            mode = DesktopConstants.SCRIPTED_METHOD_MODE;
            tScriptedMethod.setEnabled(true);
        } else if (o == rb2) {
            disableAllOnModesPanel();
            mode = DesktopConstants.SCRIPTED_FILE_MODE;
            tSourceScript.setEnabled(true);
            bScriptBrowse.setEnabled(true);
        } else if (o == rb3) {
            disableAllOnModesPanel();
            mode = DesktopConstants.JAVA_APP_MODE;
            tJavaApp.setEnabled(true);
            bAppBrowse.setEnabled(true);
        } else if (o == rb4) {
            disableAllOnModesPanel();
            mode = DesktopConstants.NATIVE_COMMAND_MODE;
            tNativeCommand.setEnabled(true);
            field.setEnabled(true);
            bNativeBrowse.setEnabled(true);
            bWdBrowse.setEnabled(true);
        }
    }

    private void callResourceBrowser(ResourceExplorerDialog resBrowser) {

        resBrowser.setHint(false);
        String path = Workspace.getUserManager().getParameters().
            getString(DesktopConstants.DESKTOP_ICONS_REPOSITORY_PARAMETER);

        if (path == null && Workspace.getUi() instanceof WorkspaceGUI) {
            path = ((WorkspaceGUI) Workspace.getUi()).getDesktopIconsPath();
            Workspace.getUserManager().getParameters().
                putString(DesktopConstants.DESKTOP_ICONS_REPOSITORY_PARAMETER, path);
        }
        resBrowser.setData(path);
        resBrowser.setVisible(true);

        if (!resBrowser.isCancelled()) {
            ImageIcon[] icons = resBrowser.getSelectedImages();
            if (icons != null && icons.length != 0
                && icons[0] != null) {
                lImage.setImage(icons[0].getImage());
            }
        }
    }

    private KPanel createFirstPanel() {
        KPanel firstPanel = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        firstPanel.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(WorkspaceResourceAnchor.getString("DesktopIconPanel.icon.name"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        firstPanel.add(l, gbc);

        tName = new JTextField(20);
 //       tName.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        firstPanel.add(tName, gbc);
        /*
         * Chooser panel
         */
        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setBorder(new EmptyBorder(0, 0, 5, 0));

        lImage = new jworkspace.ui.widgets.ImageRenderer();
        JScrollPane sp = new JScrollPane(lImage);
        sp.setPreferredSize(new Dimension(200, 200));
        p1.add(DesktopConstants.CENTER, sp);
        /*
         * Buttons panel
         */
        KPanel bp = new KPanel();
        bp.setLayout(new GridLayout(0, 1, 5, 5));

        ImageIcon icon = new ImageIcon(WorkspaceGUI.
            getResourceManager().getImage("folder.png"));
        bIconBrowse = new JButton(icon);
        bIconBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browseImage.tooltip"));
        bIconBrowse.addActionListener(this);
        bIconBrowse.setDefaultCapable(false);
        bIconBrowse.setOpaque(false);
        bp.add(bIconBrowse);

        icon = new ImageIcon(WorkspaceGUI.
            getResourceManager().getImage("repository.png"));
        bLibBrowser = new JButton(icon);
        bLibBrowser.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browseRepos.tooltip"));
        bLibBrowser.addActionListener(this);
        bLibBrowser.setDefaultCapable(false);
        bLibBrowser.setOpaque(false);
        bp.add(bLibBrowser);

        KPanel p5 = new KPanel();
        p5.setLayout(new BorderLayout(5, 5));
        p5.add(DesktopConstants.NORTH, bp);

        p1.add(DesktopConstants.EAST, p5);

        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        firstPanel.add(p1, gbc);

        firstPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        return firstPanel;
    }

    @SuppressWarnings("checkstyle:MethodLength")
    private KPanel createModesPanel() {
        KPanel modesPanel = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        modesPanel.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        ButtonGroup bg = new ButtonGroup();
        rb3 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.javaApp"));
        rb3.addActionListener(this);
        rb3.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb3);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(rb3, gbc);

        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
//        p1.setPreferredSize(new Dimension(150, 20));

        tJavaApp = new JTextField(15);
 //       tJavaApp.setPreferredSize(new Dimension(150, 20));
        tJavaApp.setOpaque(false);
        tJavaApp.setEditable(false);

        p1.add(DesktopConstants.CENTER, tJavaApp);

        bAppBrowse = new KButton(DesktopConstants.DOTS);
        bAppBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browse.javaApp"));
        bAppBrowse.addActionListener(this);
        bAppBrowse.setDefaultCapable(false);
        p1.add(DesktopConstants.EAST, bAppBrowse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(p1, gbc);

        rb1 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.bshConsole"));
        rb1.addActionListener(this);
        rb1.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb1);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(rb1, gbc);

        tScriptedMethod = new JTextField(15);
 //       tScriptedMethod.setPreferredSize(new Dimension(150, 20));
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(tScriptedMethod, gbc);

        rb2 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.bshFile"));
        rb2.addActionListener(this);
        rb2.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb2);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(rb2, gbc);

        KPanel p0 = new KPanel();
        p0.setLayout(new BorderLayout(5, 5));
 //       p0.setPreferredSize(new Dimension(150, 20));

        tSourceScript = new JTextField(15);
 //       tSourceScript.setPreferredSize(new Dimension(150, 20));
        tSourceScript.setOpaque(false);
        tSourceScript.setEditable(false);

        p0.add(DesktopConstants.CENTER, tSourceScript);

        bScriptBrowse = new KButton(DesktopConstants.DOTS);
        bScriptBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browse.bshFile"));
        bScriptBrowse.addActionListener(this);
        bScriptBrowse.setDefaultCapable(false);
        p0.add(DesktopConstants.EAST, bScriptBrowse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(p0, gbc);

        rb4 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.native"));
        rb4.addActionListener(this);
        rb4.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb4);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(rb4, gbc);

        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
 //       p1.setPreferredSize(new Dimension(150, 20));

        tNativeCommand = new JTextField(15);
 //       tNativeCommand.setPreferredSize(new Dimension(150, 20));
        tNativeCommand.setOpaque(false);
        tNativeCommand.setEditable(true);

        p1.add(DesktopConstants.CENTER, tNativeCommand);

        bNativeBrowse = new KButton(DesktopConstants.DOTS);
        bNativeBrowse.setToolTipText(WorkspaceResourceAnchor.getString(
            DesktopConstants.DESKTOP_ICON_PANEL_NATIVE_COMMAND_BROWSE));
        bNativeBrowse.addActionListener(this);
        bNativeBrowse.setDefaultCapable(false);
        p1.add(DesktopConstants.EAST, bNativeBrowse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(p1, gbc);

        JLabel l = new JLabel(WorkspaceResourceAnchor.getString("DesktopIconPanel.native.wd"));
        l.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(l, gbc);

        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
 //       p1.setPreferredSize(new Dimension(150, 20));

        field = new JTextField(15);
 //       field.setPreferredSize(new Dimension(150, 20));
        field.setOpaque(false);
        field.setEditable(true);

        p1.add(DesktopConstants.CENTER, field);

        bWdBrowse = new KButton(DesktopConstants.DOTS);
        bWdBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.native.wd.browse"));
        bWdBrowse.addActionListener(this);
        bWdBrowse.setDefaultCapable(false);
        p1.add(DesktopConstants.EAST, bWdBrowse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(p1, gbc);

        modesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        return modesPanel;
    }

    public void setData(DesktopIcon data) {

        desktopIcon = data;
        tName.setText(data.getName());
        lImage.setImage(data.getIcon().getImage());
        tDesc.setText(data.getComments());
        mode = desktopIcon.getMode();

        if (mode == DesktopConstants.SCRIPTED_METHOD_MODE) {

            tScriptedMethod.setEnabled(true);
            tScriptedMethod.setText(desktopIcon.getCommandLine());
            rb1.setSelected(true);

        } else if (mode == DesktopConstants.SCRIPTED_FILE_MODE) {

            tSourceScript.setEnabled(true);
            tSourceScript.setText(desktopIcon.getCommandLine());
            bScriptBrowse.setEnabled(true);
            rb2.setSelected(true);

        } else if (mode == DesktopConstants.JAVA_APP_MODE) {

            tJavaApp.setEnabled(true);
            tJavaApp.setText(desktopIcon.getCommandLine());
            bAppBrowse.setEnabled(true);
            rb3.setSelected(true);

        } else if (mode == DesktopConstants.NATIVE_COMMAND_MODE) {

            tNativeCommand.setEnabled(true);
            tNativeCommand.setText(desktopIcon.getCommandLine());
            field.setText(desktopIcon.getWorkingDirectory());
            field.setEnabled(true);
            bNativeBrowse.setEnabled(true);
            bWdBrowse.setEnabled(true);
            rb4.setSelected(true);
        }
    }

    private void disableAllOnModesPanel() {
        bAppBrowse.setEnabled(false);
        bScriptBrowse.setEnabled(false);
        tScriptedMethod.setEnabled(false);
        tSourceScript.setEnabled(false);
        tJavaApp.setEnabled(false);
        bNativeBrowse.setEnabled(false);
        tNativeCommand.setEnabled(false);
        field.setEnabled(false);
        bWdBrowse.setEnabled(false);
    }

    boolean syncData() {

        desktopIcon.setName(tName.getText());
        desktopIcon.setIcon(new ImageIcon(lImage.getImage()));
        desktopIcon.setComments(tDesc.getText());
        desktopIcon.setMode(this.mode);

        if (mode == DesktopConstants.SCRIPTED_METHOD_MODE) {

            desktopIcon.setCommandLine(tScriptedMethod.getText());

        } else if (mode == DesktopConstants.SCRIPTED_FILE_MODE) {

            desktopIcon.setCommandLine(tSourceScript.getText());

        } else if (mode == DesktopConstants.JAVA_APP_MODE) {

            desktopIcon.setCommandLine(tJavaApp.getText());

        } else if (mode == DesktopConstants.NATIVE_COMMAND_MODE) {

            desktopIcon.setCommandLine(tNativeCommand.getText());
            desktopIcon.setWorkingDirectory(field.getText());

        }
        return (true);
    }
}
