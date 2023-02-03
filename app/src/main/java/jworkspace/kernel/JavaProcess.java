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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hyperrealm.kiwi.util.KiwiUtils.MILLISEC_IN_SECOND;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.installer.ApplicationDataSource;
import lombok.Setter;

/**
 * This class represent a single java runtime process,
 * with all required means for diagnostics and management.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
public final class JavaProcess {
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(JavaProcess.class);

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
    @Setter
    private Process process;
    /**
     * Start time
     */
    private Date startTime;
    /**
     * Alive flag
     */
    private boolean alive = false;

    JavaProcess(Process process, String processName) {
        setName(processName);
        setProcess(process);
        follow(processName);
    }

    /**
     * Constructor
     */
    JavaProcess(String[] args, String processName) throws IOException {

        if (args == null) {
            return;
        }

        for (String arg : args) {
            LOG.info(STARTED_WITH_COMMAND_LINE + arg);
        }

        setName(processName);

        Runtime runtime = Runtime.getRuntime();
        setProcess(runtime.exec(args));

        follow(processName);
    }

    private void follow(String processName) {

        startTime = new Date();

        ReaderThread inputStreamReader = new ReaderThread(process.getInputStream());
        inputStreamReader.start();
        ReaderThread errorStreamReader = new ReaderThread(process.getErrorStream());
        errorStreamReader.start();

        Thread waitThread = new Thread(this::waitForLogs);
        waitThread.start();

        LOG.info(WorkspaceResourceAnchor.getString(JAVA_PROCESS_MESSAGE) + WHITESPACE + processName);
        LOG.info(WorkspaceResourceAnchor.getString(JAVA_PROCESS_STARTED_AT)
            + WHITESPACE + DateFormat.getInstance().format(startTime));

        alive = true;
    }

    private void waitForLogs() {
        try {
            int x = process.waitFor();
            LOG.info(LEFT_BR + DateFormat.getInstance().format(startTime)
                + WHITESPACE + WorkspaceResourceAnchor.getString(JAVA_PROCESS_EXIT_VALUE)
                + WHITESPACE + x);
            alive = false;
        } catch (InterruptedException ex) {
            LOG.error(WorkspaceResourceAnchor.getString("JavaProcess.CannotWait"), ex);
        }
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
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

        private final InputStream stream;

        private final byte[] buf = new byte[BUFFER_SIZE];

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
                        LOG.info(LEFT_BR + getElapsedTime() + WHITESPACE + SEC + WHITESPACE + str);
                    } else {
                        if (b < 0) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    LOG.error("Cannot read process log", ex);
                    break;
                }
            }
        }
    }
}