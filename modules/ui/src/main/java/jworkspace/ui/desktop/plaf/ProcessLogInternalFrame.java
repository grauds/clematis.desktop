package jworkspace.ui.desktop.plaf;
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

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.logging.LogStreamProvider;
import jworkspace.runtime.logging.TaskLogAdapter;
import jworkspace.ui.logging.LogViewerPanel;

public final class ProcessLogInternalFrame extends JInternalFrame {

    // Use standard visual emojis or simple text labels as universally stable visual anchors
    private static final String ICON_ALIVE = "🟢 Running";
    private static final String ICON_STOPPED = "🔴 Stopped";

    private final AbstractTask process;
    private final LogViewerPanel logViewerPanel;
    private final JLabel statusLabel;
    private final Timer stateMonitorTimer;

    @SuppressWarnings("checkstyle:MagicNumber")
    public ProcessLogInternalFrame(AbstractTask process, int width) {
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

        // Disconnect the UI log viewer immediately to stop rendering streams
        if (logViewerPanel != null) {
            logViewerPanel.switchLogs(null);
        }

        // Move potential blocking I/O and process destruction off the EDT
        if (process != null && process.isAlive()) {
            new Thread(() -> {
                process.stop(); // Executes safely on a background thread
                // Dispose of the UI component back on the EDT once the process releases
                SwingUtilities.invokeLater(this::dispose);
            }, "Process-Cleanup-Thread").start();
        } else {
            this.dispose();
        }
    }
}

