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

    private final int preferredWidth;
    private JTextPane textPane;
    private final StyledDocument document = getTextPane().getStyledDocument();

    private boolean autoScroll = true;

    private final AttributeSet infoStyle;
    private final AttributeSet warnStyle;
    private final AttributeSet errorStyle;

    @SuppressWarnings("checkstyle:MagicNumber")
    public LogViewerPanel(int preferredWidth) {
        super(new BorderLayout());
        this.preferredWidth = preferredWidth;

        infoStyle = createStyle(Color.BLACK, false);
        warnStyle = createStyle(new Color(255, 140, 0), false);
        errorStyle = createStyle(Color.RED, true);

        JScrollPane scrollPane = new JScrollPane(getTextPane());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        installAutoScrollDetection(scrollPane);

        add(createToolbar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(preferredWidth, 200));
    }

    private JTextPane getTextPane() {
        if (this.textPane == null) {
            this.textPane = new JTextPane() {
                @Override
                public boolean getScrollableTracksViewportWidth() {
                    // Always wrap to viewport width, never expand horizontally
                    return getParent() != null && getParent().getWidth() >= preferredWidth;
                }
            };
            this.textPane.setEditable(false);
            this.textPane.setEditorKit(new WrapEditorKit());
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
                StyledDocument doc = getTextPane().getStyledDocument();
                LogSeverity severity = LogSeverityDetector.detect(message);
                AttributeSet style = switch (severity) {
                    case INFO -> infoStyle;
                    case WARNING -> warnStyle;
                    case ERROR -> errorStyle;
                };

                doc.insertString(doc.getLength(), message + "\n", style);
                if (autoScroll) {
                    getTextPane().setCaretPosition(document.getLength());
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
        getTextPane().selectAll();
        getTextPane().copy();
        getTextPane().select(0, 0);
    }

    private void save() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(
                    fc.getSelectedFile().toPath(),
                    getTextPane().getText(),
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
                                    // Forces wrapping horizontally
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
