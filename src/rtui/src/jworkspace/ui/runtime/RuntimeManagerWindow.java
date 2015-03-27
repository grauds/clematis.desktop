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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/
import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import jworkspace.kernel.*;
import jworkspace.ui.*;
import jworkspace.ui.views.*;
import jworkspace.ui.cpanel.*;
import jworkspace.util.*;

import kiwi.ui.*;
import kiwi.util.*;
import kiwi.util.plugin.*;
/**
 * Runtime Manager window shows information about loaded
 * shells, processes, available memory, etc.
 */
public class RuntimeManagerWindow extends DefaultCompoundView
                   implements ListSelectionListener, ChangeListener
{
  /**
   * Parent plugin
   */
  private Plugin plugin = null;
  /**
   * A collection of small monitors
   */
  private Vector monitors = new Vector();
  /**
   * Splitter
   */
  private JSplitPane splitter = new JSplitPane();
  /**
   * Properties viewer
   */
  private PropertiesPanel propPanel = new PropertiesPanel();
  /**
   * Actions
   */
  private RuntimeManagerActions actions;
  /**
   * Monitor class - holds different measuring applets
   * to monitor system state.
   */
  public class Monitor extends KPanel
  {
     public Monitor(String title, JComponent component)
     {
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
  public class Nest extends Box
  {
    public Nest()
    {
      super(BoxLayout.Y_AXIS);
      setOpaque(false);
      for (int i = 0; i < monitors.size(); i++)
        add((Monitor)monitors.elementAt(i));
    }
  }
  /**
   * Generic list for services
   */
  public class PluginsList extends JList
  {
   public PluginsList()
   {
      super();
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      setCellRenderer(new DefaultListCellRenderer()
      {
         public Component getListCellRendererComponent(JList list,Object value,
              int index,boolean isSelected,boolean cellHasFocus)
         {
          Component comp = super.getListCellRendererComponent(list, value,
            index, isSelected,	cellHasFocus);
          if (value instanceof Plugin)
          {
            Plugin plugin = (Plugin) value;
            setText(((Plugin)value).toString());
            Icon icon = plugin.getIcon();
            if (icon == null && plugin.getType().equals("XShell"))
            {
              setIcon(new ImageIcon(Workspace.getResourceManager().
                               getImage("shell.png")) );
            }
            else if (icon == null && plugin.getType().equals("XPlugin"))
            {
              setIcon(new ImageIcon(Workspace.getResourceManager().
                               getImage("plugin.png")) );
            }
            else if (icon == null)
            {
              setIcon(new ImageIcon(Workspace.getResourceManager().
                               getImage("unknown.png")) );
            }
            else
            {
              setIcon(icon);
            }
          }
          return comp;
         }
      });
   }
  }
  /**
   * Tabbed panel.
   */
  JTabbedPane tabbed_pane = new JTabbedPane();
  /**
   * List of processes
   */
  protected JList processes = null;
  /**
   * List of plugins
   */
  protected JList plugins = null;

/**
 * RuntimeManagerWindow constructor.
 */
public RuntimeManagerWindow()
{
  super();
  /**
   * Actions
   */
  actions = new RuntimeManagerActions(this);
  /**
   * Create monitors
   */
  monitors.add(new Monitor(LangResource.getString("message#245"),
                 new MemoryMonitor()));
  monitors.add(new Monitor(LangResource.getString("message#248"),
                 new MemoryCompactorPanel()));
  monitors.add(new Monitor(LangResource.getString("message#244"),
                 new IPAddressPanel()));
  /**
   * Create tabbed panes
   */
  tabbed_pane.add(LangResource.getString("Plugins"), createPluginsList() );
  tabbed_pane.add(LangResource.getString("Processes"),
                            createProcessesList() );

  KPanel perf_panel = new KPanel();
  perf_panel.setLayout(new BorderLayout());
  perf_panel.add(createPerformanceLabel(), BorderLayout.NORTH);

  JScrollPane nestScroller = new JScrollPane(new Nest());
  nestScroller.getViewport().setOpaque(false);
  nestScroller.setOpaque(false);

  perf_panel.add(nestScroller, BorderLayout.CENTER);
  tabbed_pane.add(LangResource.getString("Performance"), perf_panel);
  /**
   * Vertical panel with monitors
   */
  this.setLayout(new BorderLayout());
  JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    tabbed_pane, propPanel);
  split.setOpaque(false);
  this.add(split,  BorderLayout.CENTER);
  tabbed_pane.addChangeListener(this);
  tabbed_pane.setOpaque(false);
  setName(LangResource.getString("message#240"));
}
public RuntimeManagerWindow(Plugin plugin)
{
  this();
  this.plugin = plugin;
}
/**
 * Return buttons for control panel
 */
public CButton[] getButtons()
{
  /**
   * Start button.
   */
  Image normal = new ResourceLoader(RuntimeManagerWindow.class)
       .getResourceAsImage("images/runtime.png");
  Image hover = new ResourceLoader(RuntimeManagerWindow.class)
       .getResourceAsImage("images/runtime.png");

  CButton b_show = CButton.create(this, new ImageIcon(normal),
        new ImageIcon(hover),	RuntimeManagerWindow.SHOW,
        LangResource.getString("message#240"));

  return new CButton[]{ b_show };
}
/**
 * This method actually saves data in plugins
 */
public void dispose()
{
  plugins.removeAll();
  processes.removeAll();
}
/**
 * Returns list of processes.
 * @return javax.swing.JList
 */
protected KPanel createProcessesList()
{
  if (processes == null)
  {
    processes = new JList();
    processes.setCellRenderer(new DefaultListCellRenderer()
        {
          public Component getListCellRendererComponent(JList list,Object value,
              int index,boolean isSelected,boolean cellHasFocus)
          {
            Component comp = super.getListCellRendererComponent(list, value,
              index, isSelected,	cellHasFocus);
            if (value instanceof JavaProcess)
            {
              setText(((JavaProcess)value).getName());
              if (((JavaProcess)value).isAlive())
              {
                 setIcon(new ImageIcon
                      (new ResourceLoader(RuntimeManagerWindow.class)
                         .getResourceAsImage("images/alive.gif")));
              }
              else
              {
                 setIcon(new ImageIcon
                      (new ResourceLoader(RuntimeManagerWindow.class)
                         .getResourceAsImage("images/terminated.gif")));
              }
            }
            return comp;
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
protected JLabel createPerformanceLabel()
{
  JLabel l = new JLabel();

  l.setBackground(Color.white);
  l.setOpaque(true);
  l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
      .getResourceAsImage("images/monitor.png")));

  StringBuffer sb = new StringBuffer();
  sb.append("<html><font color=black>");
  sb.append(LangResource.getString("System_Monitors"));
  sb.append("</font><br>");
  sb.append("<font size=\"-2\" color=black><i>");
  sb.append(LangResource.getString("hint1"));
  sb.append("</i></font></html>");

  l.setText(sb.toString());
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
protected JLabel createProcessLabel()
{
  JLabel l = new JLabel();

  l.setBackground(Color.white);
  l.setOpaque(true);
  l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
      .getResourceAsImage("images/process.png")));

  StringBuffer sb = new StringBuffer();
  sb.append("<html><font color=black>");
  sb.append(LangResource.getString("Processes"));
  sb.append("</font>");
  sb.append("</html>");

  l.setText(sb.toString());

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
protected JLabel createPluginsLabel()
{
  JLabel l = new JLabel();

  l.setBackground(Color.white);
  l.setOpaque(true);
  l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
      .getResourceAsImage("images/plugin.png")));

  StringBuffer sb = new StringBuffer();
  sb.append("<html><font color=black>");
  sb.append(LangResource.getString("Plugins"));
  sb.append("</font><br>");
  sb.append("<font size=\"-2\" color=black><i>");
  sb.append(LangResource.getString("hint2"));
  sb.append("</i></font></html>");

  l.setText(sb.toString());

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
public void copyLog()
{
  Object p[] = processes.getSelectedValues();
  if (p == null || p.length == 0) return;
  if (p.length > 1)
  {
    JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
     LangResource.getString("message#252"));
    return;
  }
  kiwi.util.KiwiUtils.setClipboardText(((JavaProcess)p[0]).getLog().toString());
}
/**
 * Create processes toolbar
 */
protected JToolBar createProcessesToolbar()
{
  JToolBar tb = new JToolBar();
  WorkspaceGUI gui = null;
  if (Workspace.getUI() instanceof WorkspaceGUI)
  {
    gui = (WorkspaceGUI) Workspace.getUI();
  }
  tb.setFloatable(false);
  if (gui != null)
  {
    JButton b = WorkspaceUtils.
            createButtonFromAction(RuntimeManagerActions.startAction);
    tb.add(b);
    RuntimeManagerActions.startAction.
          addPropertyChangeListener(gui.createActionChangeListener(b));

    b = WorkspaceUtils.createButtonFromAction(RuntimeManagerActions.killAction);
    tb.add(b);
    RuntimeManagerActions.killAction.
          addPropertyChangeListener(gui.createActionChangeListener(b));

    b = WorkspaceUtils.createButtonFromAction(RuntimeManagerActions.copyLogAction);
    tb.add(b);
    RuntimeManagerActions.copyLogAction.
          addPropertyChangeListener(gui.createActionChangeListener(b));
  }
  return tb;
}
/**
 * Returns list of processes.
 * @return javax.swing.JList
 */
protected KPanel createPluginsList()
{
   KPanel pr = new KPanel();
   plugins = new PluginsList();
   plugins.addListSelectionListener(this);
   pr.setLayout(new BorderLayout());
   pr.add(createPluginsLabel(), BorderLayout.NORTH);
   pr.add(new JScrollPane(plugins), BorderLayout.CENTER);
   return pr;
}
public boolean isUnique()
{
  return true;
}
/**
 * Kill selected processes
 */
public void kill()
{
   Object p[] =  processes.getSelectedValues();
   for (int i = 0; i < p.length; i++)
   {
      if (p[i] instanceof JavaProcess)
         ((JavaProcess)p[i]).kill();
   }
   processes.repaint();
}
/**
 * Kill all processes
 */
public void killAll()
{
   JavaProcess p[] = Workspace.getRuntimeManager().getAllProcesses();
   for (int i = 0; i < p.length; i++)
   {
       p[i].kill();
   }
   processes.repaint();
}
/**
 * Kill process and remove it from list
 */
public void killAndRemove()
{
  Object p[] =  processes.getSelectedValues();
  for (int i = 0; i < p.length; i++)
  {
     if (p[i] instanceof JavaProcess)
     {
      ((JavaProcess)p[i]).kill();
      Workspace.getRuntimeManager().remove((JavaProcess)p[i]);
     }
  }
  processes.setListData(Workspace.getRuntimeManager().getAllProcesses());
  processes.repaint();
}
/**
 * Invoked when the target of the listener has changed its state.
 *
 * @param e  a ChangeEvent object
 */
public void stateChanged(javax.swing.event.ChangeEvent e)
{
  plugins.clearSelection();
  processes.clearSelection();
  propPanel.createDefaultReport();
}
/**
 * Kill all processes and remove them from list
 */
public void killAllAndRemove()
{
   JavaProcess p[] = Workspace.getRuntimeManager().getAllProcesses();
   for (int i = 0; i < p.length; i++)
   {
      p[i].kill();
   }
   Workspace.getRuntimeManager().removeTerminated();
   processes.setListData(Workspace.getRuntimeManager().getAllProcesses());
   processes.repaint();
}
public void activated(boolean flag)
{
  if (flag)
  {
      update();
      revalidate();
      repaint();
  }
}
/**
 * Called whenever the value of the selection changes.
 * @param e the event that characterizes the change.
 */
public void valueChanged(ListSelectionEvent e)
{
  if (e.getValueIsAdjusting()) return;

  if (e.getSource() == plugins)
  {
    if (plugins.getSelectedValue() != null)
    {
      Plugin pl = (Plugin)plugins.getSelectedValue();
      propPanel.createPluginReport(pl);
    }
  }
  else if (e.getSource() == processes)
  {
    if (processes.getSelectedValue() != null)
    {
      JavaProcess pr =
          (JavaProcess)processes.getSelectedValue();
      propPanel.createProcessReport(pr);
      actions.enableActions(true);
      if (pr.isAlive())
      {
        actions.enableActions(true, RuntimeManagerActions.PROCESS_ALIVE_ACTION);
      }
      else
      {
        actions.enableActions(false, RuntimeManagerActions.PROCESS_ALIVE_ACTION);
      }
    }
    else
    {
      actions.enableActions(false);
    }
  }
}
public void update()
{
   JavaProcess[] p = Workspace.getRuntimeManager().getAllProcesses();
   processes.setListData(p);

/* HashSet hplugins = (HashSet)Workspace.getSystemPlugins().clone();
   hplugins.addAll((HashSet)Workspace.getUserPlugins().clone());

   if (Workspace.getUI() instanceof WorkspaceGUI)
   {
     WorkspaceGUI wgui = (WorkspaceGUI) Workspace.getUI();
     hplugins.addAll((HashSet)wgui.getShells().clone());
   }*/
   HashSet hplugins = Workspace.getSystemPlugins();
   hplugins.addAll(Workspace.getUserPlugins());

   if (Workspace.getUI() instanceof WorkspaceGUI)
   {
     WorkspaceGUI wgui = (WorkspaceGUI) Workspace.getUI();
     hplugins.addAll(wgui.getShells());
   }
   plugins.setListData(hplugins.toArray());

   if (processes.isSelectionEmpty())
    actions.enableActions(false);
   else
    actions.enableActions(true);

   super.update();
}
}
