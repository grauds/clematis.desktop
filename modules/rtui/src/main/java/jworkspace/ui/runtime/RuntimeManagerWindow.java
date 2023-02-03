package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.plugin.Plugin;

import jworkspace.kernel.JavaProcess;
import jworkspace.kernel.Workspace;
import jworkspace.ui.Utils;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.WorkspaceResourceManager;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.views.DefaultCompoundView;

/**
 * Runtime Manager window shows information about loaded shells,
 * processes, available memory, etc.
 *
 * @author Anton Troshin
 */
public class RuntimeManagerWindow extends DefaultCompoundView
    implements ListSelectionListener, ChangeListener {

    private static final String PLUGINS = LangResource.getString("Plugins");

    private static final String PROCESSES = LangResource.getString("Processes");

    private static final String RUNTIME_MANAGER = LangResource.getString("message#240");

    private static RuntimeManagerActions actions;

    private static WorkspaceResourceManager resourceManager;

    private final List<Monitor> monitors = new Vector<>();

    private final PropertiesPanel propPanel = new PropertiesPanel();

    private JList<JavaProcess> processes = null;

    private JList<Plugin> plugins = null;

    /**
     * RuntimeManagerWindow constructor.
     */
    public RuntimeManagerWindow() {
        super();

        monitors.add(new Monitor(LangResource.getString("message#245"), new MemoryMonitor()));
        monitors.add(new Monitor(LangResource.getString("message#248"), new MemoryCompactorPanel()));
        monitors.add(new Monitor(LangResource.getString("message#244"), new IPAddressPanel()));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(PLUGINS, createPluginsList());
        tabbedPane.add(PROCESSES, createProcessesList());

        KPanel perfPanel = new KPanel();
        perfPanel.setLayout(new BorderLayout());
        perfPanel.add(createPerformanceLabel(), BorderLayout.NORTH);

        JScrollPane nestScroller = new JScrollPane(new Nest());
        nestScroller.getViewport().setOpaque(false);
        nestScroller.setOpaque(false);

        perfPanel.add(nestScroller, BorderLayout.CENTER);
        tabbedPane.add(LangResource.getString("Performance"), perfPanel);

        this.setLayout(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, propPanel);
        split.setOpaque(false);

        this.add(split, BorderLayout.CENTER);

        tabbedPane.addChangeListener(this);
        tabbedPane.setOpaque(false);

        setName(RUNTIME_MANAGER);
    }

    public synchronized RuntimeManagerActions getActions() {
        if (actions == null) {
            actions = new RuntimeManagerActions(this);
        }
        return actions;
    }

    /**
     * Return buttons for control panel
     */
    public CButton[] getButtons() {
        /*
         * Start button.
         */
        Image normal = new ResourceLoader(RuntimeManagerWindow.class).getResourceAsImage("images/runtime.png");

        CButton bShow = CButton.create(this,
            new ImageIcon(normal),
            new ImageIcon(normal),
            RuntimeManagerWindow.SHOW,
            RUNTIME_MANAGER);

        return new CButton[]{bShow};
    }

    /**
     * This method actually saves data in plugins
     */
    public void dispose() {
        plugins.removeAll();
        processes.removeAll();
    }

    /**
     * Returns list of processes.
     *
     * @return javax.swing.JList
     */
    private KPanel createProcessesList() {
        if (processes == null) {
            processes = new JList<>();
            processes.setCellRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setListValue(value);
                    return comp;
                }

                private void setListValue(Object value) {
                    if (!(value instanceof JavaProcess)) {
                        return;
                    }

                    setText(((JavaProcess) value).getName());
                    if (((JavaProcess) value).isAlive()) {
                        setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/alive.gif")));
                    } else {
                        setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/terminated.gif")));
                    }
                }
            });
            processes.addListSelectionListener(this);
            processes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        KPanel pr = new KPanel();
        pr.setLayout(new BorderLayout());
        pr.add(createProcessLabel(), BorderLayout.NORTH);
        pr.add(new JScrollPane(processes), BorderLayout.CENTER);
        pr.add(createProcessesToolbar(), BorderLayout.SOUTH);
        return pr;
    }

    /**
     * Get performance label
     */
    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
    private JLabel createPerformanceLabel() {

        JLabel l = new JLabel();

        l.setBackground(Color.white);
        l.setOpaque(true);
        l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/monitor.png")));

        String sb = "<html><font color=black>"
            + LangResource.getString("System_Monitors")
            + "</font><br><font size=\"-2\" color=black><i>"
            + LangResource.getString("hint1") + "</i></font></html>";
        l.setText(sb);
        l.setForeground(Color.black);
        l.setPreferredSize(new Dimension(250, 70));
        l.setMinimumSize(l.getPreferredSize());
        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        l.setHorizontalAlignment(JLabel.CENTER);
        return l;
    }

    /**
     * Get performance label
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private JLabel createProcessLabel() {
        JLabel l = new JLabel();

        l.setBackground(Color.white);
        l.setOpaque(true);
        l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/process.png")));

        String sb = "<html><font color=black>" + PROCESSES + "</font></html>";

        l.setText(sb);
        l.setForeground(Color.black);
        l.setPreferredSize(new Dimension(250, 70));
        l.setMinimumSize(l.getPreferredSize());
        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        l.setHorizontalAlignment(JLabel.CENTER);

        return l;
    }

    /**
     * Get performance label
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private JLabel createPluginsLabel() {
        JLabel l = new JLabel();

        l.setBackground(Color.white);
        l.setOpaque(true);
        l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/plugin.png")));

        String sb = "<html><font color=black>" +
                PLUGINS +
                "</font><br><font size=\"-2\" color=black><i>" +
                LangResource.getString("hint2") +
                "</i></font></html>";

        l.setText(sb);

        l.setForeground(Color.black);
        l.setPreferredSize(new Dimension(250, 70));
        l.setMinimumSize(l.getPreferredSize());
        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        l.setHorizontalAlignment(JLabel.CENTER);

        return l;
    }

    /**
     * Copy log
     */
    void copyLog() {
        List<JavaProcess> p = processes.getSelectedValuesList();
        if (p == null || p.size() == 0) {
            return;
        }
        if (p.size() > 1) {
            JOptionPane.showMessageDialog(Workspace.getUi().getFrame(),
                LangResource.getString("message#252"));
        }
    }

    /**
     * Create processes toolbar
     */
    private JToolBar createProcessesToolbar() {
        JToolBar tb = new JToolBar();
        WorkspaceGUI gui = null;
        if (Workspace.getUi() instanceof WorkspaceGUI) {
            gui = (WorkspaceGUI) Workspace.getUi();
        }
        tb.setFloatable(false);
        if (gui != null) {
            JButton b = Utils.createButtonFromAction(getActions().getAction(RuntimeManagerActions.START_ACTION_NAME));
            tb.add(b);

            b = Utils.createButtonFromAction(getActions().getAction(RuntimeManagerActions.KILL_ACTION_NAME));
            tb.add(b);

            b = Utils.createButtonFromAction(getActions().getAction(RuntimeManagerActions.COPY_LOG_ACTION_NAME));
            tb.add(b);
        }
        return tb;
    }

    /**
     * Returns list of processes.
     *
     * @return javax.swing.JList
     */
    private KPanel createPluginsList() {
        KPanel pr = new KPanel();
        plugins = new PluginsList();
        plugins.addListSelectionListener(this);
        pr.setLayout(new BorderLayout());
        pr.add(createPluginsLabel(), BorderLayout.NORTH);
        pr.add(new JScrollPane(plugins), BorderLayout.CENTER);
        return pr;
    }

    public boolean isUnique() {
        return true;
    }

    /**
     * Kill selected processes
     */
    void kill() {
        List<JavaProcess> p = processes.getSelectedValuesList();
        for (JavaProcess o : p) {
            if (o != null) {
                o.kill();
            }
        }
        processes.repaint();
    }

    /**
     * Kill all processes
     */
    void killAll() {
        Workspace.getRuntimeManager().getAllProcesses().forEach(JavaProcess::kill);
        processes.repaint();
    }

    /**
     * Kill process and remove it from list
     */
    void killAndRemove() {
        List<JavaProcess> p = processes.getSelectedValuesList();
        for (JavaProcess o : p) {
            if (o != null) {
                o.kill();
                Workspace.getRuntimeManager().remove(o);
            }
        }
        processes.setListData(Workspace.getRuntimeManager()
                .getAllProcesses().toArray(JavaProcess[]::new));
        processes.repaint();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        plugins.clearSelection();
        processes.clearSelection();
        propPanel.createDefaultReport();
    }

    /**
     * Kill all processes and remove them from list
     */
    void killAllAndRemove() {
        Workspace.getRuntimeManager().getAllProcesses().forEach(JavaProcess::kill);
        Workspace.getRuntimeManager().removeTerminated();
        processes.setListData(Workspace.getRuntimeManager()
                .getAllProcesses().toArray(JavaProcess[]::new));
        processes.repaint();
    }

    public void activated(boolean flag) {
        if (flag) {
            update();
        }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {

        if (e.getValueIsAdjusting()) {
            return;
        }

        if (e.getSource() == plugins) {
            createPluginReport(plugins, propPanel);
        } else if (e.getSource() == processes) {
            createProcessReport(processes, propPanel, getActions());
        }
    }

    private static void createProcessReport(JList<JavaProcess> processes,
                                            PropertiesPanel propPanel,
                                            RuntimeManagerActions actions) {

        if (processes.getSelectedValue() != null) {
            JavaProcess pr = processes.getSelectedValue();
            propPanel.createProcessReport(pr);
            actions.enableActions(true);
            actions.enableActions(pr.isAlive(), RuntimeManagerActions.PROCESS_ALIVE_ACTION);
        } else {
            actions.enableActions(false);
        }
    }

    private static void createPluginReport(JList<Plugin> plugins, PropertiesPanel propPanel) {

        if (plugins.getSelectedValue() != null) {
            Plugin pl = plugins.getSelectedValue();
            propPanel.createPluginReport(pl);
        }
    }

    public void update() {
        List<JavaProcess> p = Workspace.getRuntimeManager().getAllProcesses();
        processes.setListData(p.toArray(JavaProcess[]::new));

        Set<Plugin> listData = new HashSet<>();
        listData.addAll(Workspace.getSystemPlugins());
        listData.addAll(Workspace.getUserPlugins());
        plugins.setListData(listData.toArray(new Plugin[] {}));

        getActions().enableActions(!processes.isSelectionEmpty());
        super.update();
    }

    /**
     * Returns resource manager for the GUI.
     *
     * @return kiwi.util.WorkspaceResourceManager
     */
    public static synchronized WorkspaceResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new WorkspaceResourceManager(RuntimeManagerWindow.class);
        }
        return resourceManager;
    }

    /**
     * Monitor class - holds different measuring applets to monitor system state.
     */
    public static class Monitor extends KPanel {
        Monitor(String title, JComponent component) {
            super();
            setBorder(new TitledBorder(title));
            setLayout(new BorderLayout());
            add(component, BorderLayout.CENTER);
            setMaximumSize(component.getPreferredSize());
            setMinimumSize(component.getPreferredSize());
        }
    }

    /**
     * A place, there all monitors nest
     */
    public class Nest extends Box {
        Nest() {
            super(BoxLayout.Y_AXIS);
            setOpaque(false);
            for (Monitor monitor : monitors) {
                add(monitor);
            }
        }
    }

    /**
     * Generic list for services
     */
    public static class PluginsList extends JList<Plugin> {

        PluginsList() {
            super();
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            setCellRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value,
                        index, isSelected, cellHasFocus);
                    if (value instanceof Plugin) {
                        Plugin plugin = (Plugin) value;
                        setText(value.toString());
                        Icon icon = plugin.getIcon();
                        if (icon == null && plugin.getType().equals("XShell")) {
                            setIcon(new ImageIcon(getResourceManager().getImage("shell.png")));
                        } else if (icon == null && plugin.getType().equals("XPlugin")) {
                            setIcon(new ImageIcon(getResourceManager().getImage("plugin.png")));
                        } else {
                            setIcon(Objects.requireNonNullElseGet(icon,
                                    () -> new ImageIcon(getResourceManager().getImage("unknown.png"))));
                        }
                    }
                    return comp;
                }
            });
        }
    }

}
