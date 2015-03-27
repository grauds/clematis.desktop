package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

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
import jworkspace.LangResource;
import jworkspace.installer.Application;
import jworkspace.installer.ApplicationDataSource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceClassCache;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.dialog.ApplicationChooserDialog;
import jworkspace.ui.widgets.ImageRenderer;
import jworkspace.ui.widgets.ResourceExplorerDialog;
import kiwi.ui.KButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This panel gathers user input for desktop icon properties.
 */

/**
 * Change log:
 * 15.11.2001 - Added text field for working directory of
 * native process.
 *
 * 24.05.02 - New panel for desktop icon with repository
 * explorer.
 */
public class DesktopIconPanel extends KPanel implements ActionListener
{
    private JTextField t_name, t_scripted_method, t_native_command,
    t_source_script, t_java_app, t_working_dir;
    private JTextArea t_desc;
    private JButton b_icon_browse, b_script_browse, b_app_browse, b_wd_browse,
    b_native_browse, b_lib_browser;
    private DesktopIcon desktopIcon;
    private ImageRenderer l_image;
    private int mode = DesktopIcon.JAVA_APP_MODE;
    private JRadioButton rb1, rb2, rb3, rb4;
    /**
     * Data holders
     */
    private File script_file;

    /**
     * Default constructor
     */
    DesktopIconPanel()
    {
        JTabbedPane tabbed_pane = new JTabbedPane();
        setLayout(new BorderLayout());

        tabbed_pane.add(LangResource.getString("DesktopIconPanel.imagePanel.title"),
                        createFirstPanel());
        tabbed_pane.add(LangResource.getString("DesktopIconPanel.commandPanel.title"),
                        createModesPanel());

        t_desc = new JTextArea(7, 1);
        t_desc.setLineWrap(true);
        t_desc.setWrapStyleWord(true);

        tabbed_pane.add(LangResource.getString("DesktopIconPanel.descPanel.title"),
                        new JScrollPane(t_desc));
        tabbed_pane.setOpaque(false);
        disableAllOnModesPanel();
        add(tabbed_pane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent evt)
    {
        Object o = evt.getSource();
        if (o == b_icon_browse)
        {
            Image im = WorkspaceClassCache.chooseImage(this);
            if (im != null)
            {
                l_image.setImage(im);
            }
        }
        else if (o == b_script_browse)
        {
            JFileChooser chooser = WorkspaceClassCache.getFileChooser
                    (jworkspace.LangResource.getString("DesktopIconPanel.chooseScript.title"),
                     new String[]{"bsh"},
                     LangResource.getString("DesktopIconPanel.script")
                    );
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(Workspace.getUI().getFrame())
                    == JFileChooser.APPROVE_OPTION)
            {
                script_file = chooser.getSelectedFile();
                t_source_script.setText(script_file.getAbsolutePath());
            }
        }
        else if (o == b_app_browse)
        {
            ApplicationChooserDialog chooser =
                    new ApplicationChooserDialog(Workspace.getUI().getFrame());
            chooser.setVisible(true);
            if (chooser.isCancelled())
                return;
            t_java_app.setText(chooser.getSelectedApplication().getLinkString().
                               substring(ApplicationDataSource.ROOT.length(),
                                         chooser.getSelectedApplication().getLinkString().length()));
        }
        else if (o == b_wd_browse)
        {
            File dir = WorkspaceClassCache.chooseDirectory(Workspace.getUI().
                                                           getFrame());
            if (dir != null)
                t_working_dir.setText(dir.getAbsolutePath());
        }
        else if (o == b_native_browse)
        {
            JFileChooser chooser = WorkspaceClassCache.getFileChooser
                    (jworkspace.LangResource.getString
                     ("DesktopIconPanel.native.command.browse"),
                     null, LangResource.getString("DesktopIconPanel.native.command")
                    );
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(Workspace.getUI().getFrame())
                    == JFileChooser.APPROVE_OPTION)
            {
                t_native_command.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
        else if (o == b_lib_browser)
        {
            ResourceExplorerDialog res_browser =
                    new ResourceExplorerDialog(Workspace.getUI().getFrame());
            callResourceBrowser(res_browser);
            /**
             * Delete all trash after disposal of large number of
             * graphic resources.
             */
            Runtime rt = Runtime.getRuntime();
            rt.gc();
            rt.runFinalization();
        }
        else if (o == rb1)
        {
            disableAllOnModesPanel();
            mode = DesktopIcon.SCRIPTED_METHOD_MODE;
            t_scripted_method.setEnabled(true);
        }
        else if (o == rb2)
        {
            disableAllOnModesPanel();
            mode = DesktopIcon.SCRIPTED_FILE_MODE;
            t_source_script.setEnabled(true);
            b_script_browse.setEnabled(true);
        }
        else if (o == rb3)
        {
            disableAllOnModesPanel();
            mode = DesktopIcon.JAVA_APP_MODE;
            t_java_app.setEnabled(true);
            b_app_browse.setEnabled(true);
        }
        else if (o == rb4)
        {
            disableAllOnModesPanel();
            mode = DesktopIcon.NATIVE_COMMAND_MODE;
            t_native_command.setEnabled(true);
            t_working_dir.setEnabled(true);
            b_native_browse.setEnabled(true);
            b_wd_browse.setEnabled(true);
        }
    }

    protected void callResourceBrowser(ResourceExplorerDialog res_browser)
    {
        res_browser.setHint(false);
        String path = Workspace.getProfilesEngine().getParameters().
                getString("DESKTOP_ICONS_REPOSITORY");
        if (path == null && Workspace.getUI() instanceof WorkspaceGUI)
        {
            path = ((WorkspaceGUI) Workspace.getUI()).getDesktopIconsPath();
            Workspace.getProfilesEngine().
                    getParameters().putString("DESKTOP_ICONS_REPOSITORY", path);
        }
        res_browser.setData(path);
        res_browser.setVisible(true);

        if (!res_browser.isCancelled())
        {
            ImageIcon[] icons = res_browser.getSelectedImages();
            if (icons != null && icons.length != 0
                    && icons[0] != null)
            {
                l_image.setImage(icons[0].getImage());
            }
        }
    }

    protected KPanel createFirstPanel()
    {
        KPanel first_panel = new KPanel();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        first_panel.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        JLabel l;

        l = new JLabel(LangResource.getString("DesktopIconPanel.icon.name"));
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        first_panel.add(l, gbc);

        t_name = new JTextField(20);
        t_name.setPreferredSize(new Dimension(150, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        first_panel.add(t_name, gbc);
        /**
         * Chooser panel
         */
        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setBorder(new EmptyBorder(0, 0, 5, 0));

        l_image = new jworkspace.ui.widgets.ImageRenderer();
        JScrollPane sp = new JScrollPane(l_image);
        sp.setPreferredSize(new Dimension(200, 200));
        p1.add("Center", sp);
        /**
         * Buttons panel
         */
        KPanel bp = new KPanel();
        bp.setLayout(new GridLayout(0, 1, 5, 5));

        ImageIcon icon = new ImageIcon(Workspace.
                                       getResourceManager().getImage("folder.png"));
        b_icon_browse = new JButton(icon);
        b_icon_browse.setToolTipText
                (LangResource.getString("DesktopIconPanel.browseImage.tooltip"));
        b_icon_browse.addActionListener(this);
        b_icon_browse.setDefaultCapable(false);
        b_icon_browse.setOpaque(false);
        bp.add(b_icon_browse);

        icon = new ImageIcon(Workspace.
                             getResourceManager().getImage("repository.png"));
        b_lib_browser = new JButton(icon);
        b_lib_browser.setToolTipText
                (LangResource.getString("DesktopIconPanel.browseRepos.tooltip"));
        b_lib_browser.addActionListener(this);
        b_lib_browser.setDefaultCapable(false);
        b_lib_browser.setOpaque(false);
        bp.add(b_lib_browser);

        KPanel p5 = new KPanel();
        p5.setLayout(new BorderLayout(5, 5));
        p5.add("North", bp);

        p1.add("East", p5);

        gbc.insets = KiwiUtils.lastBottomInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        first_panel.add(p1, gbc);

        first_panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        return first_panel;
    }

    protected KPanel createModesPanel()
    {
        /**
         * Modes panel configures execution mode
         * for shortcut.
         */
        KPanel modes_panel = new KPanel();

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        modes_panel.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        ButtonGroup bg = new ButtonGroup();
        /**
         * Execute installed java application.
         */
        rb3 = new JRadioButton
                (LangResource.getString("DesktopIconPanel.categ.javaApp"));
        rb3.addActionListener(this);
        rb3.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        bg.add(rb3);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(rb3, gbc);
        /**
         * Panel No.1.
         */
        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setPreferredSize(new Dimension(150, 20));

        t_java_app = new JTextField(15);
        t_java_app.setPreferredSize(new Dimension(150, 20));
        t_java_app.setOpaque(false);
        t_java_app.setEditable(false);

        p1.add("Center", t_java_app);

        b_app_browse = new KButton("...");
        b_app_browse.setToolTipText
                (LangResource.getString("DesktopIconPanel.browse.javaApp"));
        b_app_browse.addActionListener(this);
        b_app_browse.setDefaultCapable(false);
        p1.add("East", b_app_browse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(p1, gbc);
        /**
         * Execute function from bsh console.
         */
        rb1 = new JRadioButton
                (LangResource.getString("DesktopIconPanel.categ.bshConsole"));
        rb1.addActionListener(this);
        rb1.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        bg.add(rb1);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(rb1, gbc);

        t_scripted_method = new JTextField(15);
        t_scripted_method.setPreferredSize(new Dimension(150, 20));
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(t_scripted_method, gbc);
        /**
         * Execute function from bsh console.
         */
        rb2 = new JRadioButton
                (LangResource.getString("DesktopIconPanel.categ.bshFile"));
        rb2.addActionListener(this);
        rb2.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        bg.add(rb2);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(rb2, gbc);
        /**
         * Panel No.2.
         */
        KPanel p0 = new KPanel();
        p0.setLayout(new BorderLayout(5, 5));
        p0.setPreferredSize(new Dimension(150, 20));

        t_source_script = new JTextField(15);
        t_source_script.setPreferredSize(new Dimension(150, 20));
        t_source_script.setOpaque(false);
        t_source_script.setEditable(false);

        p0.add("Center", t_source_script);

        b_script_browse = new KButton("...");
        b_script_browse.setToolTipText
                (LangResource.getString("DesktopIconPanel.browse.bshFile"));
        b_script_browse.addActionListener(this);
        b_script_browse.setDefaultCapable(false);
        p0.add("East", b_script_browse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(p0, gbc);
        /**
         * Execute native command.
         */
        rb4 = new JRadioButton
                (LangResource.getString("DesktopIconPanel.categ.native"));
        rb4.addActionListener(this);
        rb4.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        bg.add(rb4);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(rb4, gbc);

        /**
         * Panel No.3.
         */
        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setPreferredSize(new Dimension(150, 20));

        t_native_command = new JTextField(15);
        t_native_command.setPreferredSize(new Dimension(150, 20));
        t_native_command.setOpaque(false);
        t_native_command.setEditable(true);

        p1.add("Center", t_native_command);

        b_native_browse = new KButton("...");
        b_native_browse.setToolTipText
                (LangResource.getString("DesktopIconPanel.native.command.browse"));
        b_native_browse.addActionListener(this);
        b_native_browse.setDefaultCapable(false);
        p1.add("East", b_native_browse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(p1, gbc);
        /**
         * Label for working directory.
         */
        JLabel l = new JLabel(LangResource.getString("DesktopIconPanel.native.wd"));
        l.setOpaque(false);
        gbc.gridwidth = 1;
        gbc.insets = KiwiUtils.firstInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(l, gbc);
        /**
         * Panel No.4.
         */
        p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setPreferredSize(new Dimension(150, 20));

        t_working_dir = new JTextField(15);
        t_working_dir.setPreferredSize(new Dimension(150, 20));
        t_working_dir.setOpaque(false);
        t_working_dir.setEditable(true);

        p1.add("Center", t_working_dir);

        b_wd_browse = new KButton("...");
        b_wd_browse.setToolTipText
                (LangResource.getString("DesktopIconPanel.native.wd.browse"));
        b_wd_browse.addActionListener(this);
        b_wd_browse.setDefaultCapable(false);
        p1.add("East", b_wd_browse);

        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        modes_panel.add(p1, gbc);

        modes_panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        return modes_panel;
    }

    public void setData(DesktopIcon data)
    {
        desktopIcon = (DesktopIcon) data;
        t_name.setText(data.getName());
        l_image.setImage(data.getIcon().getImage());
        t_desc.setText(data.getComments());
        mode = desktopIcon.getMode();
        if (mode == DesktopIcon.SCRIPTED_METHOD_MODE)
        {
            t_scripted_method.setEnabled(true);
            t_scripted_method.setText(desktopIcon.getCommandLine());
            rb1.setSelected(true);
        }
        else if (mode == DesktopIcon.SCRIPTED_FILE_MODE)
        {
            t_source_script.setEnabled(true);
            t_source_script.setText(desktopIcon.getCommandLine());
            b_script_browse.setEnabled(true);
            rb2.setSelected(true);
        }
        else if (mode == DesktopIcon.JAVA_APP_MODE)
        {
            t_java_app.setEnabled(true);
            t_java_app.setText(desktopIcon.getCommandLine());
            b_app_browse.setEnabled(true);
            rb3.setSelected(true);
        }
        else if (mode == DesktopIcon.NATIVE_COMMAND_MODE)
        {
            t_native_command.setEnabled(true);
            t_native_command.setText(desktopIcon.getCommandLine());
            t_working_dir.setText(desktopIcon.getWorkingDirectory());
            t_working_dir.setEnabled(true);
            b_native_browse.setEnabled(true);
            b_wd_browse.setEnabled(true);
            rb4.setSelected(true);
        }
    }

    protected void disableAllOnModesPanel()
    {
        b_app_browse.setEnabled(false);
        b_script_browse.setEnabled(false);
        t_scripted_method.setEnabled(false);
        t_source_script.setEnabled(false);
        t_java_app.setEnabled(false);
        b_native_browse.setEnabled(false);
        t_native_command.setEnabled(false);
        t_working_dir.setEnabled(false);
        b_wd_browse.setEnabled(false);
    }

    public boolean syncData()
    {
        desktopIcon.setName(t_name.getText());
        desktopIcon.setIcon(new ImageIcon(l_image.getImage()));
        desktopIcon.setComments(t_desc.getText());
        desktopIcon.setMode(this.mode);
        if (mode == DesktopIcon.SCRIPTED_METHOD_MODE)
        {
            desktopIcon.setCommandLine(t_scripted_method.getText());
        }
        else if (mode == DesktopIcon.SCRIPTED_FILE_MODE)
        {
            desktopIcon.setCommandLine(t_source_script.getText());
        }
        else if (mode == DesktopIcon.JAVA_APP_MODE)
        {
            desktopIcon.setCommandLine(t_java_app.getText());
        }
        else if (mode == DesktopIcon.NATIVE_COMMAND_MODE)
        {
            desktopIcon.setCommandLine(t_native_command.getText());
            desktopIcon.setWorkingDirectory(t_working_dir.getText());
        }
        return (true);
    }
}
