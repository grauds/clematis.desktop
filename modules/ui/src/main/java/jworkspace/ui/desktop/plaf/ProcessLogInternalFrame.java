package jworkspace.ui.desktop.plaf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import jworkspace.runtime.LogStreamProvider;
import jworkspace.runtime.TaskLogAdapter;
import jworkspace.runtime.process.JavaProcess;
import jworkspace.ui.logging.LogViewerPanel;

public final class ProcessLogInternalFrame extends JInternalFrame {

    // Use standard visual emojis or simple text labels as universally stable visual anchors
    private static final String ICON_ALIVE = "🟢 Running";
    private static final String ICON_STOPPED = "🔴 Stopped";

    private final JavaProcess process;
    private final LogViewerPanel logViewerPanel;
    private final JLabel statusLabel;
    private final Timer stateMonitorTimer;

    @SuppressWarnings("checkstyle:MagicNumber")
    public ProcessLogInternalFrame(JavaProcess process, int width) {
        // title, resizable, closable, maximizable, iconifiable
        super(process.getName(), true, true, true, true);
        this.process = process;

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(width, 400));

        // Initialize and attach your streaming log panel
        this.logViewerPanel = new LogViewerPanel(width);
        LogStreamProvider provider = new TaskLogAdapter(process);
        this.logViewerPanel.switchLogs(provider);
        this.add(this.logViewerPanel, BorderLayout.CENTER);

        // Build the Status Bar at the bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(4, 6, 4, 6));
        statusBar.setBackground(new Color(240, 240, 240));

        this.statusLabel = new JLabel(process.isAlive() ? ICON_ALIVE : ICON_STOPPED);
        this.statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        statusBar.add(this.statusLabel, BorderLayout.WEST);
        this.add(statusBar, BorderLayout.SOUTH);

        // Configure a light-weight Swing UI Timer to refresh status indicators safely
        this.stateMonitorTimer = new Timer(1000, e -> updateProcessStatus());
        this.stateMonitorTimer.start();

        // Handle Window Close Operations with Interception Confirmation
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                handleWindowCloseRequest();
            }
        });
    }

    /**
     * Polls the backend process status safely from the Swing Event Dispatch Thread (EDT).
     */
    private void updateProcessStatus() {
        if (process != null) {
            if (process.isAlive()) {
                statusLabel.setText(ICON_ALIVE);
            } else {
                statusLabel.setText(ICON_STOPPED);
                stateMonitorTimer.stop(); // Stop polling once the process dies naturally
            }
        }
    }

    /**
     * Confirms with the user before destroying an active background process.
     */
    private void handleWindowCloseRequest() {
        if (process != null && process.isAlive()) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);

            int choice = JOptionPane.showConfirmDialog(
                parentWindow,
                "The process is still running. Do you want to terminate it and close the log window?",
                "Terminate Process?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                cleanupAndClose();
            }
        } else {
            // Process is already dead, close the window immediately without warning prompts
            cleanupAndClose();
        }
    }

    /**
     * Stops background listeners, kills the native task execution, and disposes of the frame.
     */
    private void cleanupAndClose() {
        stateMonitorTimer.stop();

        // Disconnect the stream pipeline to avoid memory leaks
        if (logViewerPanel != null) {
            logViewerPanel.switchLogs(null);
        }

        if (process != null && process.isAlive()) {
            process.stop();
        }

        this.dispose();
    }
}

