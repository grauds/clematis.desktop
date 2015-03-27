package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.kernel.Workspace;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Gui error reporter for the end-user
 */
public class WorkspaceError
{
    public static ShowMessageLaterRunnable later = null;
    public static Thread laterThread = null;
    public static boolean showingLater = false;

    /**
     * shows the stack trace of an error message
     */
    protected static class ShowErrorDetailsAction extends AbstractAction
    {
        Throwable error;
        Component c;

        public ShowErrorDetailsAction(Throwable t, Component c)
        {
            super(">>");
            error = t;
            this.c = c;
        }

        public void actionPerformed(ActionEvent evt)
        {
            // take the error and print the stack trace out to a string
            StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw);
            error.printStackTrace(w);
            w.close();
            String s = StringUtils.wrapLines(sw.toString(), 150);
            JTextArea ta = new JTextArea(s);
            JScrollPane sp = new JScrollPane(ta);

            // Try to force the exceptions to fit inside the desktop pane

            Dimension desktopsize = Toolkit.getDefaultToolkit().getScreenSize();

            int newwidth = (int) ( desktopsize.getWidth() * 0.75);
            int newheight = (int) ( desktopsize.getHeight() * 0.75);

            Dimension preferredSize = new Dimension(newwidth, newheight);

            int maxwidth = (int) ( desktopsize.getWidth() * 0.9);
            int maxheight = (int) ( desktopsize.getHeight() * 0.9);
            Dimension maximumSize = new Dimension(maxwidth, maxheight);
            sp.setPreferredSize(preferredSize);
            sp.setMaximumSize(maximumSize);

            JPanel p = new JPanel(new BorderLayout());
            p.add(sp, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(c, p);
        }
    }

    public class ShowMessageLaterRunnable implements Runnable
    {
        public String msg = null;
        public String title = null;
        public boolean die = false;

        public void run()
        {
            while (!die)
            {
                try
                {
                    synchronized (later)
                    {
                        later.wait();

                        if (msg != null && title != null)
                        {
                            Thread.sleep(500);
                            // hope half a second is enough
                            msg(title, msg);
                            msg = null;
                            title = null;
                        }
                    }
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public class msgRunnable implements Runnable
    {
        protected String msg = null;
        protected String title = null;

        public msgRunnable(String msg, String title)
        {
            this.msg = msg;
            this.title = title;
        }

        public void run()
        {
            msg(title, msg);
        }
    }

    /**
     * init some neccessary error stuff
     */
    public WorkspaceError()
    {
        later = new ShowMessageLaterRunnable();
        laterThread = new Thread(later);
        laterThread.start();
    }

    public static void exception(String usermsg, Throwable ex)
    {
        Component c = Workspace.getUI().getFrame();
        String excp = null;
        String title = null;

        if (ex instanceof WorkspaceException)
        {
            Throwable t = ((WorkspaceException) ex).getWrappedException();
            if (t != null)
            {
                ex = t;
            }
            title = "Exception";
        }
        excp = ex.getClass().getName();

        if (title == null)
        {
            title = excp;
        }

        int i = excp.lastIndexOf(".");

        if (i < 0)
            i = 0;
        excp = excp.substring(i + 1);

        StringBuffer msg = new StringBuffer(usermsg);
        String s = ex.getMessage();
        if (s != null)
        {
            msg.append("\n" + s);
        }

        // create panel with advanced error message
        JPanel l = WorkspaceUtils.createMultiLineLabel(msg.toString(), 80);
        l.setBorder(new EmptyBorder(5, 5, 15, 5));

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel p = new JPanel(gb);

        JButton b = WorkspaceUtils.
                createButtonFromAction(new ShowErrorDetailsAction(ex, c), true);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        p.add(l, gbc);
        // button panel
        JPanel p0 = new JPanel();
        p0.setLayout(new BorderLayout(5, 5));
        p0.setPreferredSize(new Dimension(10, 25));

        b.setDefaultCapable(false);
        p0.add(b, BorderLayout.EAST);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.lastInsets;

        p.add(p0, gbc);

        // Try to force the exceptions to fit inside the desktop pane
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();

        int maxwidth = (int) ( screen_size.getWidth() * 0.9);
        int maxheight = (int) ( screen_size.getHeight() * 0.9);
        Dimension maximumSize = new Dimension(maxwidth, maxheight);
        p.setMaximumSize(maximumSize);

        JOptionPane.showMessageDialog(
                c,
                p,
                title,
                JOptionPane.ERROR_MESSAGE);

    }

    public static void msg(String usermsg)
    {
        msg("Error", usermsg);
    }

    public static void msg(String title, String usermsg)
    {
        JOptionPane.showMessageDialog
                (Workspace.getUI().getFrame(), usermsg, title, JOptionPane.ERROR_MESSAGE);
    }

    public void quit()
    {
        later.die = true;
        laterThread.interrupt();
    }

    /**
     * Special version of msg that displays messages in a different thread from the
     * calling thread (due to some nasty drag and drop event handling bugs that cause
     * deadlocks if certain gui operations are done at dnd drop event time)
     */
    public static void showMessageLater(String title, String usermsg)
    {
        if (later != null)
        {
            if (showingLater)
            {
                return;
            }
            showingLater = true;
            synchronized (later)
            {
                showingLater = true;

                later.msg = usermsg;
                later.title = title;
                later.notifyAll();
            }
            showingLater = false;
        }
    }
}
