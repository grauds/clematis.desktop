package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jworkspace.LangResource;
import jworkspace.installer.ApplicationDataSource;
import jworkspace.util.WorkspaceError;

/**
 * This class represent a single java runtime process,
 * with all required means for diagnostics and management.
 */
public final class JavaProcess {

    /**
     * Default process name
     */
    private String processName = "Untitled";
    /**
     * Native process
     */
    private Process process;
    /**
     * Start time
     */
    private Date startTime;
    /**
     * String from process
     */
    private StringBuffer log = new StringBuffer();
    /**
     * Some UI stuff - the scroller for visual log.
     */
    private JScrollPane scroller = null;
    /**
     * Alive flag
     */
    private boolean alive = false;
    /**
     * Scrollback view automatically scrolls view as new entries are added.
     */
    private JTextArea vlog = new JTextArea() {

        public void setFont(Font font) {
            font = new Font("Monospaced", Font.PLAIN, 13);
            super.setFont(font);
        }

        public void append(String str) {
            super.append(str + "\n");
            Rectangle b = new Rectangle(0, getHeight() - 13, getWidth(), getHeight());
            scrollRectToVisible(b);
        }

        public void setBackground(Color bg) {
            super.setBackground(Color.lightGray);
        }

        public void setForeground(Color fg) {
            super.setForeground(Color.black);
        }
    };

    /**
     * Reader thread to get all process log information back to workspace.
     */
    private class ReaderThread extends Thread {

        private InputStream stream;
        private byte[] buf = new byte[360];

        ReaderThread(InputStream stream) {
            this.stream = stream;
            setDaemon(true);
        }

        public void run() {
            for (; ; ) {
                try {
                    int b = stream.read(buf);
                    if (b > 0) {
                        String str = new String(buf, 0, b);
                        log.append(" [ ")
                                .append(getElapsedTime())
                                .append(" ")
                                .append("sec")
                                .append(" ")
                                .append(str);

                        StringTokenizer st = new StringTokenizer(str, "\n\r\f\t");

                        while (st.hasMoreTokens()) {
                            String little = st.nextToken();
                            vlog.append(" [ " + getElapsedTime() + " " + "sec" + " " + little);
                        }
                    } else {
                        if (b < 0) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    WorkspaceError.exception("Cannot read process log", ex);
                    break;
                }
            }
        }
    }

    /**
     * Constructor
     */
    JavaProcess(String[] args, String processName) throws IOException {

        if (args == null) {
            return;
        }

        for (String _arg : args) {
            log.append(">" + "Started with command line" + " ").append(_arg);
            vlog.append(">" + "Started with command line" + " " + _arg);
        }

        setName(processName);

        Runtime runtime = Runtime.getRuntime();
        process = runtime.exec(args);

        startTime = new Date();

        ReaderThread inputStreamReader = new ReaderThread(process.getInputStream());
        inputStreamReader.start();
        ReaderThread errorStreamReader = new ReaderThread(process.getErrorStream());
        errorStreamReader.start();

        Thread waitThread = new Thread(new Runnable() {

            public void run() {
                waitForLogs();
            }
        });
        waitThread.start();

        log.append(LangResource.getString("JavaProcess.Process"))
                .append(" ")
                .append(processName)
                .append(" ")
                .append(LangResource.getString("JavaProcess.StartedAt"))
                .append(" ")
                .append(java.text.DateFormat.getInstance().format(startTime));

        vlog.append(LangResource.getString("JavaProcess.Process") +
                " " + processName +
                " " + LangResource.getString("JavaProcess.StartedAt") +
                " " + java.text.DateFormat.getInstance().format(startTime));
        alive = true;
    }

    private void waitForLogs() {
        for (; ; ) {
            try {
                int x = process.waitFor();
                log.append(" [ ").append(java.text.DateFormat.getInstance()
                        .format(startTime))
                        .append(" ")
                        .append(LangResource.getString("JavaProcess.ExitValue"))
                        .append(" ")
                        .append(x);
                vlog.append(" [ " +
                        java.text.DateFormat.getInstance().format(startTime)
                        + " " + LangResource.getString("JavaProcess.ExitValue") +
                        " " + x);
                alive = false;
                break;
            } catch (InterruptedException ex) {
                WorkspaceError.exception
                        (LangResource.getString("JavaProcess.CannotWait"), ex);
            }
        }
    }

    /**
     * Returns time, elapsed from process start.
     */
    private long getElapsedTime() {
        return ((System.currentTimeMillis() - startTime.getTime()) / 1000);
    }

    /**
     * Returns process name.
     *
     * @return java.lang.String
     */
    public String getName() {
        return processName;
    }

    public Date getStartTime() {
        return startTime;
    }

    /**
     * Get string log
     */
    public StringBuffer getLog() {
        return log;
    }

    /**
     * Get log for visual components.
     */
    public JScrollPane getVLog() {
        if (scroller == null) {
            scroller = new JScrollPane(vlog);
            scroller.setName(vlog.getName());
        }
        return scroller;
    }

    /**
     * Kills this process.
     */
    public void kill() {
        if (process != null) {
            process.destroy();
        }
        alive = false;
    }

    /**
     * Sets process name.
     *
     * @param process_name java.lang.String
     */
    public void setName(java.lang.String process_name) {
        if (process_name.startsWith(ApplicationDataSource.ROOT)) {
            this.processName = process_name.substring(ApplicationDataSource.ROOT.
                    length());
        } else {
            this.processName = process_name;
        }
        /*
         * Set name for log.
         */
        vlog.setName(this.processName);
    }

    /**
     * If current process alive.
     *
     * @return boolean
     */
    public boolean isAlive() {
        return alive;
    }
}