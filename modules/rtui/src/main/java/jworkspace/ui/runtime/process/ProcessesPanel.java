package jworkspace.ui.runtime.process;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.LogStreamProvider;
import jworkspace.runtime.TaskLogAdapter;
import jworkspace.ui.logging.LogViewerPanel;
import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;
import jworkspace.ui.util.SwingUtils;

public class ProcessesPanel extends KPanel {

    private static final String PROCESSES =
        LangResource.getString("Processes");

    private final ProcessesActions actions;

    private JList<AbstractTask> tasksList;

    private LogViewerPanel logViewer;

    private final List<IProcessSelectionListener> selectionListeners = new ArrayList<>();

    @SuppressWarnings("checkstyle:MagicNumber")
    public ProcessesPanel() {

        this.actions = new ProcessesActions(getTasksList());

        setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        setLayout(new BorderLayout());

        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitter.setOneTouchExpandable(true);
        splitter.setContinuousLayout(true);
        splitter.setTopComponent(createTasksPanel());

        ReportPanel reportPanel = new ReportPanel();
        this.addSelectionListener(reportPanel);
        splitter.setBottomComponent(reportPanel);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            splitter,
            getLogViewer()
        );

        splitPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        splitPane.setDividerLocation(300);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);
    }

    private JList<AbstractTask> getTasksList() {

        if (tasksList == null) {
            tasksList = new JList<>();
            tasksList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                      JList<?> list,
                      Object value,
                      int index,
                      boolean isSelected,
                      boolean cellHasFocus
                ) {

                    Component component = super.getListCellRendererComponent(
                          list,
                          value,
                          index,
                          isSelected,
                          cellHasFocus
                    );

                    if (value instanceof AbstractTask task) {
                        setText(task.getName());
                        String icon = task.isAlive()
                              ? "images/alive.gif"
                              : "images/terminated.gif";

                        setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                                  .getResourceAsImage(icon)
                              )
                        );
                    }
                    return component;
                }}
            );
            tasksList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tasksList.addListSelectionListener(_ -> {
                fireSelectionChanged();
                switchLogs();
            });
        }
        return tasksList;
    }

    public void addSelectionListener(IProcessSelectionListener listener) {
        selectionListeners.add(listener);
    }

    private void fireSelectionChanged() {
        for (IProcessSelectionListener listener : selectionListeners) {
            if (this.tasksList.getSelectedValue() != null) {
                listener.processSelected(this.tasksList.getSelectedValue());
            }
        }
    }

    private void switchLogs() {
        AbstractTask selectedTask = tasksList.getSelectedValue();

        if (selectedTask != null) {
            // Wrap the domain task inside our agnostic provider interface
            LogStreamProvider provider = new TaskLogAdapter(selectedTask);
            getLogViewer().switchLogs(provider);
        } else {
            // Clear the panel if nothing is selected
            getLogViewer().switchLogs(null);
        }
    }

    private KPanel createTasksPanel() {
        KPanel panel = new KPanel();
        panel.setLayout(new BorderLayout());
        panel.add(createProcessLabel(), BorderLayout.NORTH);
        panel.add(
            new JScrollPane(getTasksList()),
            BorderLayout.CENTER
        );
        panel.add(
            createProcessesToolbar(),
            BorderLayout.SOUTH
        );
        return panel;
    }

    private LogViewerPanel getLogViewer() {
        if (logViewer == null) {
            logViewer = new LogViewerPanel(getPreferredSize().width);
        }
        return logViewer;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JLabel createProcessLabel() {

        JLabel label = new JLabel();
        label.setBackground(Color.white);
        label.setOpaque(true);
        label.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                    .getResourceAsImage(
                        "images/process.png"
                    )
            )
        );

        label.setText("<html><font color=black>" + PROCESSES + "</font></html>");
        label.setForeground(Color.black);
        label.setPreferredSize(new Dimension(250, 70));
        label.setMinimumSize(label.getPreferredSize());
        label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        label.setHorizontalAlignment(JLabel.CENTER);

        return label;
    }

    private JToolBar createProcessesToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        toolbar.add(SwingUtils.createButtonFromAction(actions.getAction(ProcessesActions.START_ACTION_NAME)));
        toolbar.add(SwingUtils.createButtonFromAction(actions.getAction(ProcessesActions.KILL_ACTION_NAME)));
        toolbar.add(SwingUtils.createButtonFromAction(actions.getAction(ProcessesActions.COPY_LOG_ACTION_NAME)));

        return toolbar;
    }
}
