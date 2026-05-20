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
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.KTabbedPane;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.plugin.WorkspacePluginContext;
import jworkspace.ui.api.cpanel.CButton;
import jworkspace.ui.api.views.DefaultCompoundView;
import jworkspace.ui.runtime.monitor.MonitorPanel;
import jworkspace.ui.runtime.plugin.PluginsDownloaderPanel;
import jworkspace.ui.runtime.plugin.PluginsPanel;
import jworkspace.ui.runtime.process.ProcessesPanel;

/**
 * Runtime Manager window shows information about loaded shells,
 * processes, available memory, etc.
 *
 * @author Anton Troshin
 */
public class RuntimeManagerWindow extends DefaultCompoundView {

    private static final String RUNTIME_MANAGER = LangResource.getString("message#240");

    private final PluginsPanel pluginsPanel = new PluginsPanel();
    private final PluginsDownloaderPanel pluginsDownloaderPanel = new PluginsDownloaderPanel();

    private final WorkspacePluginContext pluginContext;

    public RuntimeManagerWindow(WorkspacePluginContext pluginContext) {
        super();

        this.pluginContext = pluginContext;

        KTabbedPane tabbedPane = new KTabbedPane();
        tabbedPane.add("Plugins", pluginsPanel);
        tabbedPane.add("Processes", new ProcessesPanel());
        tabbedPane.add("Downloader", pluginsDownloaderPanel);
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(Color.WHITE);

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(new MonitorPanel(), BorderLayout.EAST);

        setName(RUNTIME_MANAGER);
    }

    /**
     * Return buttons for the control panel
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
        //pluginsPanel.load(this.pluginContext);
        pluginsDownloaderPanel.load(this.pluginContext);
    }

    @Override
    public void reset() {

    }

    @Override
    public void save() throws IOException {
       // pluginsPanel.save(this.pluginContext);
        pluginsDownloaderPanel.save(this.pluginContext);
    }

    public void update() {
        // update plugins list
        List<Plugin> listData = new ArrayList<>();
        listData.addAll(ServiceLocator.getInstance().getSystemPlugins());
        listData.addAll(ServiceLocator.getInstance().getUserPlugins());
        pluginsPanel.setPlugins(listData);
    }
}
