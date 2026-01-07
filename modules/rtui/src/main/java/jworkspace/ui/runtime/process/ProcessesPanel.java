package jworkspace.ui.runtime.process;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.RuntimeManager;
import jworkspace.runtime.process.JavaProcess;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.logging.LogViewerPanel;
import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;
import jworkspace.ui.util.SwingUtils;

public class ProcessesPanel extends KPanel {

    private static final String PROCESSES = LangResource.getString("Processes");

    private ProcessesActions actions;

    private JList<JavaProcess> processes = null;

    private LogViewerPanel logViewer;

    @SuppressWarnings("checkstyle:MagicNumber")
    public ProcessesPanel() {
        this.actions = new ProcessesActions();

        setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(createProcessesList()),
            new JScrollPane(getLogViewer())
        );
        splitPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        splitPane.setDividerLocation(300);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
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
        /*ServiceLocator
            .getInstance()
            .getRuntimeManager()
            .getAllProcesses().forEach(JavaProcess::kill);*/
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
               /* todo ServiceLocator
                    .getInstance()
                    .getRuntimeManager()
                    .remove(o);*/
            }
        }
        /*todo processes.setListData(
            ServiceLocator
                .getInstance()
                .getRuntimeManager()
                .getAllProcesses().toArray(JavaProcess[]::new)
        );*/
        processes.repaint();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        //_plugins.clearSelection();
        processes.clearSelection();
    }

    /**
     * Kill all processes and remove them from list
     */
    void killAndRemoveAll() {
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
     /* todo runtimeManager.getAllProcesses().forEach(JavaProcess::kill);
        todo runtimeManager.removeTerminated();
        todo processes.setListData(
            runtimeManager.getAllProcesses().toArray(JavaProcess[]::new)
        );
      */
        processes.repaint();
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
                public Component getListCellRendererComponent(JList list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus
                ) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setListValue(value);
                    return comp;
                }

                private void setListValue(Object value) {
                    if (!(value instanceof JavaProcess javaProcess)) {
                        return;
                    }

                    setText(javaProcess.getName());
                    if (javaProcess.isAlive()) {
                        setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/alive.gif")));
                    } else {
                        setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/terminated.gif")));
                    }
                }
            });
            processes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        KPanel pr = new KPanel();
        pr.setLayout(new BorderLayout());
        pr.add(createProcessLabel(), BorderLayout.NORTH);
        pr.add(new JScrollPane(processes), BorderLayout.CENTER);
        pr.add(createProcessesToolbar(), BorderLayout.SOUTH);
        return pr;
    }

    private LogViewerPanel getLogViewer() {
        if (logViewer == null) {
            logViewer = new LogViewerPanel(getPreferredSize().width);
        }
        return logViewer;
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
     * Create processes toolbar
     */
    private JToolBar createProcessesToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton b = SwingUtils.createButtonFromAction(
            this.actions.getAction(ProcessesActions.START_ACTION_NAME)
        );
        tb.add(b);

        b = SwingUtils.createButtonFromAction(
            this.actions.getAction(ProcessesActions.KILL_ACTION_NAME)
        );
        tb.add(b);

        b = SwingUtils.createButtonFromAction(
            this.actions.getAction(ProcessesActions.COPY_LOG_ACTION_NAME)
        );
        tb.add(b);

        return tb;
    }

    /**
     * Copy log
     */
    void copyLog() {
        List<JavaProcess> p = processes.getSelectedValuesList();
        if (p == null || p.isEmpty()) {
            return;
        }
        if (p.size() > 1) {
            JOptionPane.showMessageDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                LangResource.getString("message#252")
            );
        }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     */
   /* public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        if (e.getSource() == processes) {
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
    }*/

    public void update() {

        List<JavaProcess> p = new ArrayList<>();
        /* todo ServiceLocator
            .getInstance()
            .getRuntimeManager()
            .getAllProcesses();
        */
        processes.setListData(p.toArray(JavaProcess[]::new));
        this.actions.enableActions(!processes.isSelectionEmpty());
    }
}
