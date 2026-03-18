package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any LATER version.

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

import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.api.WorkspaceException;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.util.SwingUtils;
import lombok.extern.java.Log;

/**
 * Gui error reporter for the end-user
 *
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Log
public class WorkspaceError {

    private static final double DEFAULT_SCALE = 0.9;

    private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(5, 5, 15, 5);

    private static final int COLS = 80;

    private static final Dimension PREFERRED_SIZE = new Dimension(10, 25);

    private static final BorderLayout BORDER_LAYOUT = new BorderLayout(5, 5);

    /**
     * init some necessary error stuff
     */
    WorkspaceError() {}

    public static void exception(String usermsg, Throwable ex) {

        Throwable throwable = ex;
        Component c = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
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

        StringBuilder msg = new StringBuilder(usermsg);
        String s = throwable.getMessage();
        if (s != null) {
            msg.append("\n").append(s);
        }

        // create the panel with an advanced error message
        JPanel l = SwingUtils.createMultiLineLabel(msg.toString(), COLS);
        l.setBorder(EMPTY_BORDER);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel p = new JPanel(gb);

        JButton b = SwingUtils.createButtonFromAction(new ShowErrorDetailsAction(ex, c), true);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        p.add(l, gbc);
        // button panel
        JPanel p0 = new JPanel();
        p0.setLayout(BORDER_LAYOUT);
        p0.setPreferredSize(PREFERRED_SIZE);

        b.setDefaultCapable(false);
        p0.add(b, BorderLayout.EAST);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;

        p.add(p0, gbc);

        // Try to force the exceptions to fit inside the desktop pane
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int maxwidth = (int) (screenSize.getWidth() * DEFAULT_SCALE);
        int maxheight = (int) (screenSize.getHeight() * DEFAULT_SCALE);
        Dimension maximumSize = new Dimension(maxwidth, maxheight);
        p.setMaximumSize(maximumSize);

        JOptionPane.showMessageDialog(
            c,
            p,
            title,
            JOptionPane.ERROR_MESSAGE);

    }


    public static void msg(String usermsg) {
        msg("Error", usermsg);
    }

    public static void msg(String title, String usermsg) {
        JOptionPane.showMessageDialog(DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            usermsg,
            title,
            JOptionPane.ERROR_MESSAGE
        );
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
            JScrollPane sp = new JScrollPane(ta);

            // Try to force the exceptions to fit inside the desktop pane

            Dimension desktopsize = Toolkit.getDefaultToolkit().getScreenSize();

            int newwidth = (int) (desktopsize.getWidth() * DESKTOP_SCALE_FACTOR);
            int newheight = (int) (desktopsize.getHeight() * DESKTOP_SCALE_FACTOR);
            Dimension preferredSize = new Dimension(newwidth, newheight);

            int maxwidth = (int) (desktopsize.getWidth() * DEFAULT_SCALE);
            int maxheight = (int) (desktopsize.getHeight() * DEFAULT_SCALE);
            Dimension maximumSize = new Dimension(maxwidth, maxheight);

            sp.setPreferredSize(preferredSize);
            sp.setMaximumSize(maximumSize);

            JPanel p = new JPanel(new BorderLayout());
            p.add(sp, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(c, p);
        }
    }
}
