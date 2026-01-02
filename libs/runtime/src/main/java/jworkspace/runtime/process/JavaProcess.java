package jworkspace.runtime.process;

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
import java.util.Date;

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.LogReaderThread;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents a single running Java runtime process.
 *
 * <p>This class acts as a managed wrapper around a native {@link Process},
 * providing lifecycle control, log capture, and execution timing.
 * It is designed to be executed as a task and integrates with
 * {@link LogReaderThread} to consume process output streams.</p>
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@Getter
public final class JavaProcess extends AbstractTask {

    /**
     * Underlying native operating system process.
     */
    @Setter
    private Process process;

    /**
     * Creates a new {@code JavaProcess} from an existing {@link Process}.
     *
     * @param process native process instance
     * @param name    human-readable process name
     */
    public JavaProcess(@NonNull Process process, @NonNull String name) {
        super(name);
        setProcess(process);
    }

    /**
     * Creates and starts a new Java process using the given command arguments.
     *
     * @param args command-line arguments used to start the process
     * @param name human-readable process name
     * @throws IOException if the process cannot be started
     */
    public JavaProcess(@NonNull String[] args, @NonNull String name) throws IOException {
        this(Runtime.getRuntime().exec(args), name);
    }

    /**
     * Executes the process lifecycle.
     *
     * <p>This method:</p>
     * <ol>
     *   <li>Marks the process start time</li>
     *   <li>Starts background threads to consume stdout and stderr</li>
     *   <li>Blocks until the process exits</li>
     * </ol>
     *
     * <p>Intended to be run in a background thread.</p>
     */
    @SuppressWarnings("checkstyle:TodoComment")
    @Override
    public void run() {
        if (process != null && process.isAlive()) {

            // Record the process start time for elapsed-time calculations
            setStartTime(new Date());

            // Start background readers for standard output and error streams
            new LogReaderThread(this, process.getInputStream(), this.getLogs()).start();
            new LogReaderThread(this, process.getErrorStream(), this.getLogs()).start();

            try {
                // Block until the process exits
                int ret = process.waitFor();
                /* TODO: notify workspace listeners about process completion */
            } catch (InterruptedException ignored) {
                // Interruption is ignored; process lifecycle remains unchanged
            }
        }
    }

    /**
     * Checks whether the underlying process is still running.
     *
     * @return {@code true} if the process exists and is alive
     */
    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    /**
     * Terminates the underlying process.
     *
     * <p>This sends a termination signal to the process. It does not
     * guarantee immediate shutdown.</p>
     */
    public void kill() {
        if (process != null) {
            process.destroy();
        }
    }

}
