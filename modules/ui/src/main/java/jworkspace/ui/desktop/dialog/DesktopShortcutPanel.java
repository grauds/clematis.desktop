package jworkspace.ui.desktop.dialog;

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
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.BorderFactory;
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

import static jworkspace.ui.util.ImageUtils.toImageIcon;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IRuntime;
import jworkspace.config.ServiceLocator;
import jworkspace.installer.ApplicationDataSource;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.IDesktop;
import jworkspace.ui.api.ITextConstants;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.dialog.ApplicationChooserDialog;
import jworkspace.ui.resources.ResourcesExplorerDialog;
import jworkspace.ui.widgets.ClassCache;
import jworkspace.ui.widgets.ImageRenderer;

/**
 * This panel gathers user input for desktop icon properties.
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class DesktopShortcutPanel extends KPanel implements ActionListener {

    private JTextField tName, tScriptedMethod, tNativeCommand, tSourceScript, tJavaApp, tWorkingDirectory;

    private final JTextArea tDesc;

    private JButton bIconBrowse, bScriptBrowse, bAppBrowse, bWdBrowse, bNativeBrowse, bLibBrowser;

    private DesktopShortcut desktopShortcut;

    private ImageRenderer lImage;

    private int mode = IRuntime.JAVA_APP_MODE;

    private JRadioButton rb1, rb2, rb3, rb4;

    /**
     * Default constructor
     */
    public DesktopShortcutPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());

        tabbedPane.add(
            WorkspaceResourceAnchor.getString("DesktopIconPanel.imagePanel.title"),
            createFirstPanel()
        );
        tabbedPane.add(
            WorkspaceResourceAnchor.getString("DesktopIconPanel.commandPanel.title"),
            createModesPanel()
        );

        tDesc = new JTextArea(7, 1);
        tDesc.setLineWrap(true);
        tDesc.setWrapStyleWord(true);

        tabbedPane.add(
            WorkspaceResourceAnchor.getString("DesktopIconPanel.descPanel.title"),
            new JScrollPane(tDesc)
        );

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

            if (chooser.showOpenDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
            ) == JFileChooser.APPROVE_OPTION) {
                File scriptFile = chooser.getSelectedFile();
                tSourceScript.setText(scriptFile.getAbsolutePath());
            }
        } else if (o == bAppBrowse) {
            ApplicationChooserDialog chooser = new ApplicationChooserDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
            );
            chooser.setVisible(true);
            if (chooser.isCancelled()) {
                return;
            }
            tJavaApp.setText(chooser.getSelectedApplication().getLinkString().
                substring(ApplicationDataSource.ROOT.length()
                ));
        } else if (o == bWdBrowse) {
            File dir = ClassCache.chooseDirectory(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame());
            if (dir != null) {
                tWorkingDirectory.setText(dir.getAbsolutePath());
            }
        } else if (o == bNativeBrowse) {
            JFileChooser chooser = ClassCache.getFileChooser(
                WorkspaceResourceAnchor.getString(IDesktop.DESKTOP_ICON_PANEL_NATIVE_COMMAND_BROWSE),
                    null, WorkspaceResourceAnchor.getString("DesktopIconPanel.native.command")
                );
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()) == JFileChooser.APPROVE_OPTION
            ) {
                tNativeCommand.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        } else if (o == bLibBrowser) {
            ResourcesExplorerDialog resBrowser = new ResourcesExplorerDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                WorkspaceResourceAnchor.getString("ResourceExplorerDlg.title"),
                "jworkspace/ui/icons"
            );
            callResourceBrowser(resBrowser);
        } else if (o == rb1) {
            disableAllOnModesPanel();
            mode = IRuntime.SCRIPTED_METHOD_MODE;
            tScriptedMethod.setEnabled(true);
        } else if (o == rb2) {
            disableAllOnModesPanel();
            mode = IRuntime.SCRIPTED_FILE_MODE;
            tSourceScript.setEnabled(true);
            bScriptBrowse.setEnabled(true);
        } else if (o == rb3) {
            disableAllOnModesPanel();
            mode = IRuntime.JAVA_APP_MODE;
            tJavaApp.setEnabled(true);
            bAppBrowse.setEnabled(true);
        } else if (o == rb4) {
            disableAllOnModesPanel();
            mode = IRuntime.NATIVE_COMMAND_MODE;
            tNativeCommand.setEnabled(true);
            tWorkingDirectory.setEnabled(true);
            bNativeBrowse.setEnabled(true);
            bWdBrowse.setEnabled(true);
        }
    }

    private void callResourceBrowser(ResourcesExplorerDialog resBrowser) {

        String path = ServiceLocator.getString(IDesktop.DESKTOP_ICONS_REPOSITORY_PARAMETER);
        if (path == null && DesktopServiceLocator.getInstance().getWorkspaceGUI() != null) {

            path = DesktopServiceLocator
                .getInstance()
                .getWorkspaceGUI()
                .getDesktopIconsPath()
                .toFile()
                .getAbsolutePath();

        }

        if (path != null && Files.exists(Path.of(path))) {
            ServiceLocator.putString(IDesktop.DESKTOP_ICONS_REPOSITORY_PARAMETER, path);
            resBrowser.setData(path);
        }
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
        p1.add(BorderLayout.CENTER, sp);
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
        p5.add(BorderLayout.NORTH, bp);

        p1.add(BorderLayout.EAST, p5);

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
        modesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create a sub-panel specifically for the bordered group
        KPanel groupPanel = new KPanel();
        GridBagLayout groupGb = new GridBagLayout();
        GridBagConstraints groupGbc = new GridBagConstraints();
        groupPanel.setLayout(groupGb);

        // Apply the titled border only to the group sub-panel
        groupPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Action"
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        groupGbc.anchor = GridBagConstraints.NORTHWEST;
        groupGbc.fill = GridBagConstraints.HORIZONTAL;
        groupGbc.weightx = 0;

        // --- Add Radio Buttons and fields to the groupPanel ---
        ButtonGroup bg = new ButtonGroup();
        rb3 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.javaApp"));
        rb3.addActionListener(this);
        rb3.setOpaque(false);
        groupGbc.gridwidth = 1;
        groupGbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb3);
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(rb3, groupGbc);

        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        tJavaApp = new JTextField(15);
        tJavaApp.setOpaque(false);
        tJavaApp.setEditable(false);
        p1.add(BorderLayout.CENTER, tJavaApp);

        bAppBrowse = new KButton(ITextConstants.DOTS);
        bAppBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browse.javaApp"));
        bAppBrowse.addActionListener(this);
        bAppBrowse.setDefaultCapable(false);
        p1.add(BorderLayout.EAST, bAppBrowse);

        groupGbc.weightx = 1;
        groupGbc.insets = KiwiUtils.LAST_INSETS;
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(p1, groupGbc);

        rb1 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.bshConsole"));
        rb1.addActionListener(this);
        rb1.setOpaque(false);
        groupGbc.gridwidth = 1;
        groupGbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb1);
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(rb1, groupGbc);

        tScriptedMethod = new JTextField(15);
        groupGbc.weightx = 1;
        groupGbc.insets = KiwiUtils.LAST_INSETS;
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(tScriptedMethod, groupGbc);

        rb2 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.bshFile"));
        rb2.addActionListener(this);
        rb2.setOpaque(false);
        groupGbc.gridwidth = 1;
        groupGbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb2);
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(rb2, groupGbc);

        KPanel p0 = new KPanel();
        p0.setLayout(new BorderLayout(5, 5));
        tSourceScript = new JTextField(15);
        tSourceScript.setOpaque(false);
        tSourceScript.setEditable(false);
        p0.add(BorderLayout.CENTER, tSourceScript);

        bScriptBrowse = new KButton(ITextConstants.DOTS);
        bScriptBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.browse.bshFile"));
        bScriptBrowse.addActionListener(this);
        bScriptBrowse.setDefaultCapable(false);
        p0.add(BorderLayout.EAST, bScriptBrowse);

        groupGbc.weightx = 1;
        groupGbc.insets = KiwiUtils.LAST_INSETS;
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(p0, groupGbc);

        rb4 = new JRadioButton(WorkspaceResourceAnchor.getString("DesktopIconPanel.categ.native"));
        rb4.addActionListener(this);
        rb4.setOpaque(false);
        groupGbc.gridwidth = 1;
        groupGbc.insets = KiwiUtils.FIRST_INSETS;
        bg.add(rb4);
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(rb4, groupGbc);

        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        tNativeCommand = new JTextField(15);
        tNativeCommand.setOpaque(false);
        tNativeCommand.setEditable(true);
        p1.add(BorderLayout.CENTER, tNativeCommand);

        bNativeBrowse = new KButton(ITextConstants.DOTS);
        bNativeBrowse.setToolTipText(
            WorkspaceResourceAnchor.getString(IDesktop.DESKTOP_ICON_PANEL_NATIVE_COMMAND_BROWSE)
        );
        bNativeBrowse.addActionListener(this);
        bNativeBrowse.setDefaultCapable(false);
        p1.add(BorderLayout.EAST, bNativeBrowse);

        groupGbc.weightx = 1;
        groupGbc.insets = KiwiUtils.LAST_INSETS;
        groupGbc.gridwidth = GridBagConstraints.REMAINDER;
        groupPanel.add(p1, groupGbc);


        // --- Layout the main modesPanel ---
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add the bordered group panel to the top
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new java.awt.Insets(0, 0, 10, 0);
        modesPanel.add(groupPanel, gbc);

        // Add Working Directory elements below the bordered panel
        JLabel l = new JLabel(WorkspaceResourceAnchor.getString("DesktopIconPanel.native.wd"));
        l.setOpaque(false);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = KiwiUtils.FIRST_INSETS;
        modesPanel.add(l, gbc);

        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        tWorkingDirectory = new JTextField(15);
        tWorkingDirectory.setOpaque(false);
        tWorkingDirectory.setEditable(true);
        p1.add(BorderLayout.CENTER, tWorkingDirectory);

        bWdBrowse = new KButton(ITextConstants.DOTS);
        bWdBrowse.setToolTipText(WorkspaceResourceAnchor.getString("DesktopIconPanel.native.wd.browse"));
        bWdBrowse.addActionListener(this);
        bWdBrowse.setDefaultCapable(false);
        p1.add(BorderLayout.EAST, bWdBrowse);

        gbc.insets = KiwiUtils.LAST_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modesPanel.add(p1, gbc);

        return modesPanel;
    }

    public void setData(DesktopShortcut data) {

        desktopShortcut = data;
        tName.setText(data.getText());
        lImage.setImage(toImageIcon(data.getIcon()).getImage());
        tDesc.setText(data.getComments());
        mode = desktopShortcut.getMode();

        if (mode == IRuntime.SCRIPTED_METHOD_MODE) {

            tScriptedMethod.setEnabled(true);
            tScriptedMethod.setText(desktopShortcut.getCommandLine());
            rb1.setSelected(true);

        } else if (mode == IRuntime.SCRIPTED_FILE_MODE) {

            tSourceScript.setEnabled(true);
            tSourceScript.setText(desktopShortcut.getCommandLine());
            bScriptBrowse.setEnabled(true);
            rb2.setSelected(true);

        } else if (mode == IRuntime.JAVA_APP_MODE) {

            tJavaApp.setEnabled(true);
            tJavaApp.setText(desktopShortcut.getCommandLine());
            bAppBrowse.setEnabled(true);
            rb3.setSelected(true);

        } else if (mode == IRuntime.NATIVE_COMMAND_MODE) {

            tNativeCommand.setEnabled(true);
            tNativeCommand.setText(desktopShortcut.getCommandLine());
            tWorkingDirectory.setText(desktopShortcut.getWorkingDirectory());
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
        bWdBrowse.setEnabled(false);
    }

    public boolean syncData() {

        desktopShortcut.setText(tName.getText());
        if (lImage.getImage() != null && desktopShortcut.getIcon() != null) {
            desktopShortcut.setIcon(new ImageIcon(lImage.getImage()));
        }
        desktopShortcut.setComments(tDesc.getText());
        desktopShortcut.setMode(this.mode);

        if (mode == IRuntime.SCRIPTED_METHOD_MODE) {
            desktopShortcut.setCommandLine(tScriptedMethod.getText());
        } else if (mode == IRuntime.SCRIPTED_FILE_MODE) {
            desktopShortcut.setCommandLine(tSourceScript.getText());
        } else if (mode == IRuntime.JAVA_APP_MODE) {
            desktopShortcut.setCommandLine(tJavaApp.getText());
        } else if (mode == IRuntime.NATIVE_COMMAND_MODE) {
            desktopShortcut.setCommandLine(tNativeCommand.getText());
        }

        desktopShortcut.setWorkingDirectory(tWorkingDirectory.getText());
        return (true);
    }
}
