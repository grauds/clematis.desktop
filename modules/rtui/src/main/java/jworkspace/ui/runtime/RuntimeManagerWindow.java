package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002,2025-2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.dialog.KMessageDialog;
import com.hyperrealm.kiwi.ui.dialog.KQuestionDialog;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.plugin.WorkspacePluginContext;
import jworkspace.ui.api.cpanel.CButton;
import jworkspace.ui.api.views.DefaultCompoundView;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.runtime.downloader.PluginsDownloaderPanel;
import jworkspace.ui.runtime.monitor.MonitorPanel;
import jworkspace.ui.runtime.plugin.IPluginUninstallActionListener;
import jworkspace.ui.runtime.plugin.PluginsPanel;
import jworkspace.ui.runtime.process.ProcessesPanel;

/**
 * Runtime Manager window shows information about loaded shells,
 * processes, available memory, etc.
 *
 * @author Anton Troshin
 */
public class RuntimeManagerWindow extends DefaultCompoundView
    implements IPluginUninstallActionListener {

    private static final String RUNTIME_MANAGER = LangResource.getString("message#240");

    private final PluginsPanel pluginsPanel = new PluginsPanel();

    private final WorkspacePluginContext pluginContext;

    public RuntimeManagerWindow(WorkspacePluginContext pluginContext) {
        super();

        this.pluginContext = pluginContext;

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            pluginsPanel,
            new PluginsDownloaderPanel()
        );
        splitPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Plugins", splitPane);
        tabbedPane.add("Processes", new ProcessesPanel());

        this.setLayout(new BorderLayout());

        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(new MonitorPanel(), BorderLayout.EAST);

        tabbedPane.setOpaque(false);

        pluginsPanel.addPluginUninstallListener(this);

        setName(RUNTIME_MANAGER);
    }

    /**
     * Return buttons for control panel
     */
    public CButton[] getButtons() {
        Image normal = new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/runtime.png");
        CButton bShow = CButton.create(this,
            new ImageIcon(normal),
            new ImageIcon(normal),
            RuntimeManagerWindow.SHOW,
            RUNTIME_MANAGER
        );
        return new CButton[]{bShow};
    }


    public void activated(boolean flag) {
        if (flag) {
            update();
        }
    }

    @Override
    public void load() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void save() {

    }

    public void update() {
        // update plugins list
        List<Plugin> listData = new ArrayList<>();
        listData.addAll(ServiceLocator.getInstance().getSystemPlugins());
        listData.addAll(ServiceLocator.getInstance().getUserPlugins());
        pluginsPanel.setPlugins(listData);
    }

    @Override
    public void pluginUninstallSelected(Plugin p) {
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
        KQuestionDialog questionDialog = new KQuestionDialog(
            parent
        ) {
            @Override
            protected boolean accept() {
                KMessageDialog messageDialog = new KMessageDialog(parent);
                try {
                    if (Files.deleteIfExists(Path.of(p.getJarFile()))) {
                        messageDialog.setMessage("Deleted successfully.");
                    } else {
                        messageDialog.setMessage(String.format("File %s doesn't exist.", p.getJarFile()));
                    }
                } catch (IOException e) {
                    messageDialog.setMessage(
                        String.format("Error deleting the file: %s. %s", p.getJarFile(), e.getMessage())
                    );
                }
                messageDialog.setVisible(true);
                return true;
            }
        };
        questionDialog.setMessage(String.format("Are you sure to uninstall %s?", p));
        questionDialog.setVisible(true);
    }
}
