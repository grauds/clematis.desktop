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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import jworkspace.runtime.LogStreamProvider;

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

    private LogStreamProvider currentProvider = null;

    private final int preferredWidth;
    private final JTextPane textPane;
    private final StyledDocument document;

    private boolean autoScroll = true;

    private final AttributeSet infoStyle;
    private final AttributeSet warnStyle;
    private final AttributeSet errorStyle;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LogViewerPanel(int preferredWidth) {
        super(new BorderLayout());
        this.preferredWidth = preferredWidth;

        // 1. Core visual styles configuration
        infoStyle = createStyle(Color.BLACK, false);
        warnStyle = createStyle(new Color(255, 140, 0), false);
        errorStyle = createStyle(Color.RED, true);

        // 2. Controlled initialization order prevents field reference leaks
        this.textPane = createTextPaneInstance();
        this.document = this.textPane.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(this.textPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        installAutoScrollDetection(scrollPane);

        add(createToolbar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(preferredWidth, 200));
    }

    private JTextPane createTextPaneInstance() {
        JTextPane pane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getParent() != null && getParent().getWidth() >= preferredWidth;
            }
        };
        pane.setEditable(false);
        pane.setEditorKit(new WrapEditorKit());
        return pane;
    }

    /**
     * Switches the viewer to target a new log stream provider source.
     * Safe to invoke from any thread context.
     */
    public void switchLogs(LogStreamProvider newProvider) {
        // Clean up old listener attachment using our reference
        if (this.currentProvider != null) {
            this.currentProvider.setStreamListener(null);
        }

        // Retain a strong field reference immediately
        // on the invoking thread before SwingUtilities can run.
        this.currentProvider = newProvider;

        // Clear and set up the new stream asynchronously on the EDT safely
        SwingUtilities.invokeLater(() -> {
            try {
                // Wipe previous records safely
                document.remove(0, document.getLength());
            } catch (BadLocationException ignored) {}

            // Use the instance field reference to prevent Garbage Collection drops
            if (this.currentProvider != null) {
                String history = this.currentProvider.getLogs();
                if (history != null && !history.isEmpty()) {
                    appendRawChunk(history);
                }
                // Bind the live stream to our raw layout consumer wrapper
                this.currentProvider.setStreamListener(this::appendRawChunkFromStream);
            }
        });
    }

    /**
     * Internal entry point handling real-time asynchronous background stream deliveries safely.
     */
    private void appendRawChunkFromStream(String textChunk) {
        SwingUtilities.invokeLater(() -> appendRawChunk(textChunk));
    }

    /**
     * Inserts text chunks into the text pane and evaluates text styling rules.
     * Must be called exclusively from inside the Swing Event Dispatch Thread (EDT).
     */
    private void appendRawChunk(String text) {
        try {
            LogSeverity severity = LogSeverityDetector.detect(text);
            AttributeSet style = switch (severity) {
                case INFO -> infoStyle;
                case WARNING -> warnStyle;
                case ERROR -> errorStyle;
            };

            document.insertString(document.getLength(), text, style);
            if (autoScroll) {
                textPane.setCaretPosition(document.getLength());
            }
        } catch (BadLocationException ignored) {
            /* Should not occur during append-only actions */
        }
    }

    /**
     * Public method to append completed individual log messages manually.
     * Safe to invoke from any background execution thread.
     */
    public void append(String message) {
        SwingUtilities.invokeLater(() -> appendRawChunk(message + "\n"));
    }

    /**
     * Clears all displayed logs from the screen instantly.
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            try {
                document.remove(0, document.getLength());
            } catch (BadLocationException ignored) {}
        });
    }

    /**
     * Appends a collection of completed lines sequentially.
     */
    public void appendAll(Iterable<String> lines) {
        for (String line : lines) {
            append(line);
        }
    }

    private JToolBar createToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton clearBtn = new JButton("Clear");
        JButton copyBtn = new JButton("Copy All");
        JButton saveBtn = new JButton("Save");

        clearBtn.addActionListener(_ -> clear());
        copyBtn.addActionListener(_ -> copyAll());
        saveBtn.addActionListener(_ -> save());

        tb.add(clearBtn);
        tb.add(copyBtn);
        tb.add(saveBtn);

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

    // WrapEditorKit inner classes remain here unchanged...
    private static class WrapEditorKit extends StyledEditorKit {
        @Override
        public ViewFactory getViewFactory() {
            return new WrapColumnFactory();
        }

        private static class WrapColumnFactory implements ViewFactory {
            @SuppressWarnings({"checkstyle:MissingSwitchDefault", "checkstyle:ReturnCount"})
            @Override
            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    switch (kind) {
                        case AbstractDocument.ContentElementName:
                            return new LabelView(elem);
                        case AbstractDocument.ParagraphElementName:
                            return new ParagraphView(elem) {
                                @Override
                                public float getMinimumSpan(int axis) {
                                    if (axis == View.X_AXIS) {
                                        return 0;
                                    }
                                    return super.getMinimumSpan(axis);
                                }
                            };
                        case AbstractDocument.SectionElementName:
                            return new BoxView(elem, View.Y_AXIS);
                        case StyleConstants.ComponentElementName:
                            return new ComponentView(elem);
                        case StyleConstants.IconElementName:
                            return new IconView(elem);
                    }
                }
                return new LabelView(elem);
            }
        }
    }
}
