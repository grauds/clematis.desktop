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
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import static com.hyperrealm.kiwi.ui.SplashScreen.MILLISEC_IN_SECOND;

import jworkspace.LangResource;
import jworkspace.installer.ApplicationDataSource;

/**
 * This class represent a single java runtime process,
 * with all required means for diagnostics and management.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
public final class JavaProcess {

    private static final int DEFAULT_FONT_SIZE = 13;

    private static final String STARTED_WITH_COMMAND_LINE = "> Started with command line: ";

    private static final String WHITESPACE = " ";

    private static final String JAVA_PROCESS_MESSAGE = "JavaProcess.Process";

    private static final String JAVA_PROCESS_STARTED_AT = "JavaProcess.StartedAt";

    private static final String JAVA_PROCESS_EXIT_VALUE = "JavaProcess.ExitValue";

    private static final String LEFT_BR = " [ ";

    private static final String SEC = "sec";
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
            super.setFont(new Font("Monospaced", Font.PLAIN, DEFAULT_FONT_SIZE));
        }

        public void append(String str) {
            super.append(str + "\n");
            Rectangle b = new Rectangle(0, getHeight() - DEFAULT_FONT_SIZE, getWidth(), getHeight());
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
     * Constructor
     */
    JavaProcess(String[] args, String processName) throws IOException {

        if (args == null) {
            return;
        }

        for (String arg : args) {
            log.append(STARTED_WITH_COMMAND_LINE).append(arg);
            vlog.append(STARTED_WITH_COMMAND_LINE + arg);
        }

        setName(processName);

        Runtime runtime = Runtime.getRuntime();
        process = runtime.exec(args);

        startTime = new Date();

        ReaderThread inputStreamReader = new ReaderThread(process.getInputStream());
        inputStreamReader.start();
        ReaderThread errorStreamReader = new ReaderThread(process.getErrorStream());
        errorStreamReader.start();

        Thread waitThread = new Thread(this::waitForLogs);
        waitThread.start();

        log.append(LangResource.getString(JAVA_PROCESS_MESSAGE))
            .append(WHITESPACE)
            .append(processName)
            .append(WHITESPACE)
            .append(LangResource.getString(JAVA_PROCESS_STARTED_AT))
            .append(WHITESPACE)
            .append(java.text.DateFormat.getInstance().format(startTime));

        vlog.append(LangResource.getString(JAVA_PROCESS_MESSAGE)
            + WHITESPACE
            + processName
            + WHITESPACE
            + LangResource.getString(JAVA_PROCESS_STARTED_AT)
            + WHITESPACE
            + java.text.DateFormat.getInstance().format(startTime));
        alive = true;
    }

    private void waitForLogs() {
        for (;;) {
            try {
                int x = process.waitFor();
                log.append(LEFT_BR).append(java.text.DateFormat.getInstance()
                    .format(startTime))
                    .append(WHITESPACE)
                    .append(LangResource.getString(JAVA_PROCESS_EXIT_VALUE))
                    .append(WHITESPACE)
                    .append(x);
                vlog.append(LEFT_BR
                    + DateFormat.getInstance().format(startTime)
                    + WHITESPACE
                    + LangResource.getString(JAVA_PROCESS_EXIT_VALUE)
                    + WHITESPACE
                    + x);
                alive = false;
                break;
            } catch (InterruptedException ex) {
                Workspace.ui.showError(LangResource.getString("JavaProcess.CannotWait"), ex);
            }
        }
    }

    /**
     * Returns time, elapsed from process start.
     */
    private long getElapsedTime() {
        return (System.currentTimeMillis() - startTime.getTime()) / MILLISEC_IN_SECOND;
    }

    /**
     * Returns process name.
     *
     * @return java.lang.String
     */
    public String getName() {
        return processName;
    }

    /**
     * Sets process name.
     *
     * @param processName java.lang.String
     */
    public void setName(java.lang.String processName) {
        if (processName.startsWith(ApplicationDataSource.ROOT)) {
            this.processName = processName.substring(ApplicationDataSource.ROOT.length());
        } else {
            this.processName = processName;
        }
        /*
         * Set name for log.
         */
        vlog.setName(this.processName);
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
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
     * If current process alive.
     *
     * @return boolean
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Reader thread to get all process log information back to workspace.
     */
    private class ReaderThread extends Thread {

        static final int BUFFER_SIZE = 360;

        private InputStream stream;

        private byte[] buf = new byte[BUFFER_SIZE];

        ReaderThread(InputStream stream) {
            this.stream = stream;
            setDaemon(true);
        }

        public void run() {
            for (;;) {
                try {
                    int b = stream.read(buf);
                    if (b > 0) {
                        String str = new String(buf, 0, b, StandardCharsets.UTF_8);
                        log.append(LEFT_BR)
                            .append(getElapsedTime())
                            .append(WHITESPACE)
                            .append(SEC)
                            .append(WHITESPACE)
                            .append(str);

                        StringTokenizer st = new StringTokenizer(str, "\n\r\f\t");

                        while (st.hasMoreTokens()) {
                            String little = st.nextToken();
                            vlog.append(LEFT_BR
                                + getElapsedTime()
                                + WHITESPACE
                                + SEC
                                + WHITESPACE
                                + little);
                        }
                    } else {
                        if (b < 0) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    Workspace.ui.showError("Cannot read process log", ex);
                    break;
                }
            }
        }
    }
}