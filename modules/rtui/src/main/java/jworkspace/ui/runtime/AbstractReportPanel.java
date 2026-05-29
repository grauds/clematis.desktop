package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002, 2019 Anton Troshin

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
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.hyperrealm.kiwi.ui.KPanel;

/**
 * @author Anton Troshin
 */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public abstract class AbstractReportPanel extends KPanel {

    private JTextPane textPane = null;

    protected AbstractReportPanel() {
        super();
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(420, 100));
        JScrollPane scrollPane = new JScrollPane(getTextPane());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected JTextPane getTextPane() {

        if (textPane == null) {
            textPane = new JTextPane();

            textPane.setContentType("text/html");
            textPane.setBackground(Color.white);
            textPane.setOpaque(true);
            textPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            textPane.setEditable(false);
            textPane.setCaret(new DefaultCaret() {
                @Override
                public void paint(Graphics g) {
                    // Do nothing - prevents the cursor from being drawn
                }
            });
            textPane.setBorder(new EmptyBorder(15, 10, 3, 10));

            Font font = textPane.getFont();
            textPane.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

            clearReport();
        }
        return textPane;
    }

    public void clearReport() {
        String sb = "<html><body style='width: 100%;'><b>Select an item from the list</b><br><br></html>";
        getTextPane().setText(sb);
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    protected void layoutReport(String html, Icon icon) {
        HTMLDocument doc = (HTMLDocument) getTextPane().getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) getTextPane().getEditorKit();
        try {
            String sb = "<html>"
                + "<body style=\"font-family: sans-serif;"
                + " background-color: #ffffff; color: #24292e; margin: 10px; width: 420px;\">"
                + html
                + "</body></html>";
            doc.remove(0, doc.getLength());
            if (icon != null) {
                getTextPane().insertIcon(icon);
            }
            kit.insertHTML(doc, doc.getLength(), sb, 0, 0, null);
            getTextPane().setCaretPosition(0);
        } catch (BadLocationException | IOException ex) {
            /* ignore */
        }
    }

}