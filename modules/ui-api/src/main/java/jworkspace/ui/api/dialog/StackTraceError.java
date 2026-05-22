package jworkspace.ui.api.dialog;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import jworkspace.api.WorkspaceException;
import jworkspace.ui.api.util.ActionUtils;
import jworkspace.ui.util.TextUtils;

public class StackTraceError {

    private static final double DEFAULT_SCALE = 0.9;
    private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(5, 5, 15, 5);
    private static final int COLS = 80;
    private static final Dimension PREFERRED_SIZE = new Dimension(10, 25);
    private static final BorderLayout BORDER_LAYOUT = new BorderLayout(5, 5);

    protected StackTraceError() {}

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void exception(Component parent, String usermsg, Throwable ex) {

        Throwable throwable = ex;
        String excp;
        String title = null;

        if (ex instanceof WorkspaceException) {
            Throwable t = ((WorkspaceException) ex).getWrappedException();
            if (t != null) {
                throwable = t;
            }
            title = "Exception";
        }
        excp = throwable.getClass().getName();

        if (title == null) {
            title = excp;
        }

        StringBuilder msg = new StringBuilder(StackTraceError.truncateText(usermsg));
        String s = throwable.getMessage();
        if (s != null) {
            msg.append("\n").append(StackTraceError.truncateText(s));
        }

        // create the panel with an advanced error message
        JPanel l = TextUtils.createMultiLineLabel(msg.toString(), StackTraceError.COLS);
        l.setBorder(StackTraceError.EMPTY_BORDER);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel p = new JPanel(gb);

        JButton b = ActionUtils.createButtonFromAction(new ShowErrorDetailsAction(ex, parent), true);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        p.add(l, gbc);
        // button panel
        JPanel p0 = new JPanel();
        p0.setLayout(StackTraceError.BORDER_LAYOUT);
        p0.setPreferredSize(StackTraceError.PREFERRED_SIZE);

        b.setDefaultCapable(false);
        p0.add(b, BorderLayout.EAST);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 5, 0);

        p.add(p0, gbc);

        // Try to force the exceptions to fit inside the desktop pane
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int maxwidth = (int) (screenSize.getWidth() * StackTraceError.DEFAULT_SCALE);
        int maxheight = (int) (screenSize.getHeight() * StackTraceError.DEFAULT_SCALE);
        Dimension maximumSize = new Dimension(maxwidth, maxheight);
        p.setMaximumSize(maximumSize);

        JOptionPane.showMessageDialog(
            parent,
            p,
            title,
            JOptionPane.ERROR_MESSAGE);

    }

    public static void msg(Component parent, String title, String usermsg) {
        JOptionPane.showMessageDialog(parent,
            usermsg,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Truncates a string to 128 characters and appends "..." if it exceeds the limit.
     *
     * @param input the target string to process
     * @return the truncated string with ellipsis, or the original string if under the limit
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:ReturnCount"})
    private static String truncateText(String input) {
        if (input == null) {
            return null;
        }
        if (input.length() > 128) {
            return input.substring(0, 128) + "...";
        }
        return input;
    }

    /**
     * Shows the stack trace of an error message
     */
    protected static class ShowErrorDetailsAction extends AbstractAction {

        static final double DESKTOP_SCALE_FACTOR = 0.75;

        Throwable error;

        Component c;

        ShowErrorDetailsAction(Throwable t, Component c) {
            super(">>");
            error = t;
            this.c = c;
        }

        @SuppressWarnings("Regexp")
        public void actionPerformed(ActionEvent evt) {
            // take the error and print the stack trace out to a string
            StringWriter sw = new StringWriter();
            try (PrintWriter w = new PrintWriter(sw)) {
                error.printStackTrace(w);
            }
            String s = sw.toString(); //StringUtils.wrapLines(sw.toString(), 150);

            JTextArea ta = new JTextArea(s);
            JScrollPane sp = getJScrollPane(ta);

            JPanel p = new JPanel(new BorderLayout());
            p.add(sp, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(c, p);
        }

        private JScrollPane getJScrollPane(JTextArea ta) {
            JScrollPane sp = new JScrollPane(ta);

            // Try to force the exceptions to fit inside the desktop pane
            Dimension desktopsize = Toolkit.getDefaultToolkit().getScreenSize();

            int newwidth = (int) (desktopsize.getWidth() * DESKTOP_SCALE_FACTOR);
            int newheight = (int) (desktopsize.getHeight() * DESKTOP_SCALE_FACTOR);
            Dimension preferredSize = new Dimension(newwidth, newheight);

            int maxwidth = (int) (desktopsize.getWidth() * StackTraceError.DEFAULT_SCALE);
            int maxheight = (int) (desktopsize.getHeight() * StackTraceError.DEFAULT_SCALE);
            Dimension maximumSize = new Dimension(maxwidth, maxheight);

            sp.setPreferredSize(preferredSize);
            sp.setMaximumSize(maximumSize);
            return sp;
        }
    }
}
