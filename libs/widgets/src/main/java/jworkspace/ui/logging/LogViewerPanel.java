package jworkspace.ui.logging;
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
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Reusable Swing component for displaying streaming logs.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Push-based updates (no timers)</li>
 *   <li>Colored log levels (INFO / WARN / ERROR)</li>
 *   <li>Append-only document updates</li>
 *   <li>Auto-scroll unless user scrolls up</li>
 *   <li>Clear / Copy All / Save actions</li>
 * </ul>
 */
public final class LogViewerPanel extends JPanel {

    private JTextPane textPane;
    private final StyledDocument document = getTextPane().getStyledDocument();

    private boolean autoScroll = true;

    private final AttributeSet infoStyle;
    private final AttributeSet warnStyle;
    private final AttributeSet errorStyle;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LogViewerPanel() {
        super(new BorderLayout());

        textPane.setEditable(false);

        infoStyle = createStyle(Color.BLACK, false);
        warnStyle = createStyle(new Color(255, 140, 0), false);
        errorStyle = createStyle(Color.RED, true);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        installAutoScrollDetection(scrollPane);

        add(createToolbar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JTextPane getTextPane() {
        if (this.textPane == null) {
            this.textPane = new JTextPane() {
                @Override
                public boolean getScrollableTracksViewportWidth() {
                    // Wrap lines if viewport is narrower than content
                    return getUI().getPreferredSize(this).width <= getParent().getSize().width;
                }
            };

        }
        return this.textPane;
    }

    /**
     * Appends a log entry.
     * Safe to call from any thread.
     */
    public void append(String message) {

        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                LogSeverity severity = LogSeverityDetector.detect(message);
                AttributeSet style = switch (severity) {
                    case INFO -> infoStyle;
                    case WARNING -> warnStyle;
                    case ERROR -> errorStyle;
                };

                doc.insertString(doc.getLength(), message + "\n", style);
                if (autoScroll) {
                    textPane.setCaretPosition(document.getLength());
                }
            } catch (BadLocationException ignored) {
                /* should not happen */
            }
        });
    }

    /**
     * Clears all displayed logs.
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            try {
                document.remove(0, document.getLength());
            } catch (BadLocationException ignored) {
            }
        });
    }

    /**
     * Appends a batch of log lines.
     */
    public void appendAll(Iterable<String> lines) {
        for (String line : lines) {
            append(line);
        }
    }

    private JToolBar createToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton clear = new JButton("Clear");
        JButton copy = new JButton("Copy All");
        JButton save = new JButton("Save");

        clear.addActionListener(e -> clear());
        copy.addActionListener(e -> copyAll());
        save.addActionListener(e -> save());

        tb.add(clear);
        tb.add(copy);
        tb.add(save);

        return tb;
    }

    private void copyAll() {
        textPane.selectAll();
        textPane.copy();
        textPane.select(0, 0);
    }

    private void save() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(
                    fc.getSelectedFile().toPath(),
                    textPane.getText(),
                    StandardCharsets.UTF_8
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Save failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void installAutoScrollDetection(JScrollPane scrollPane) {
        AdjustmentListener l = e -> {
            Adjustable sb = e.getAdjustable();
            int bottom = sb.getMaximum() - sb.getVisibleAmount();
            autoScroll = sb.getValue() >= bottom - 5;
        };
        scrollPane.getVerticalScrollBar().addAdjustmentListener(l);
    }

    private static AttributeSet createStyle(Color color, boolean bold) {
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, color);
        StyleConstants.setBold(s, bold);
        return s;
    }
}
