package jworkspace.runtime.jshell;
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
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Date;

import jdk.jshell.JShell;
import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.LogReaderThread;
import lombok.NonNull;
import lombok.Setter;
/**
 * Manages an interactive JShell instance compatible with an executor pool pipeline.
 */
public final class JShellProcess extends AbstractTask {

    @Setter
    private JShell jshell;

    private final PipedOutputStream outputRedirector = new PipedOutputStream();
    private final PipedInputStream inputStreamBridge = new PipedInputStream();
    private final Object exitLock = new Object();
    private volatile boolean isRunning = false;

    private JShellProcess(@NonNull String name) throws IOException {
        super(name);
        this.inputStreamBridge.connect(this.outputRedirector);

        JShell instance = JShell.builder()
            .out(new PrintStream(outputRedirector, true))
            .err(new PrintStream(outputRedirector, true)) // Mimics redirectErrorStream(true)
            .build();

        setJshell(instance);
        this.isRunning = true;
    }

    /**
     * Factory method mimicking ProcessBuilder behavior.
     */
    public static JShellProcess createAndInitialize(@NonNull String name) throws IOException {
        return new JShellProcess(name);
    }

    /**
     * Executes a local .jsh or .java file using JShell's internal open command.
     *
     * @param filePath path to the local script file
     */
    public void execute(@NonNull Path filePath) {
        if (jshell != null && isAlive()) {
            // The absolute path prevents directory resolution bugs inside the VM
            String command = "/open " + filePath.toAbsolutePath();
            jshell.eval(command);
        }
    }

    /**
     * Executes a Java code snippet inside the JShell instance.
     */
    public void execute(@NonNull String snippet) {
        if (jshell != null && isRunning) {
            jshell.eval(snippet);
        }
    }

    @Override
    public void run() {
        if (jshell != null) {
            synchronized (exitLock) {
                setStartTime(new Date());
            }

            // Start background thread to redirect internal JShell stdout/stderr to getLogs()
            new LogReaderThread(inputStreamBridge, this.getLogs()).start();

            // Block the poolExecutor thread until stop() is explicitly called
            synchronized (exitLock) {
                while (isRunning) {
                    try {
                        exitLock.wait();
                    } catch (InterruptedException ignored) {
                        // Thread interruption does not kill the shell session automatically
                    }
                }
            }
        }
    }

    @Override
    public boolean isAlive() {
        return jshell != null && isRunning;
    }

    @Override
    public void stop() {
        synchronized (exitLock) {
            if (jshell != null && isRunning) {
                try {
                    jshell.close();
                    outputRedirector.close();
                    inputStreamBridge.close();
                } catch (Exception ignored) {
                    // Suppress stream closure exceptions on teardown
                }
                isRunning = false;
                exitLock.notifyAll(); // Safely unblocks the thread held by poolExecutor
            }
        }
    }
}


