package jworkspace.runtime;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jworkspace.api.EventsDispatcher;
import jworkspace.api.IRuntime;
import jworkspace.api.IWorkspaceListener;
import jworkspace.runtime.jshell.JShellProcess;
import jworkspace.runtime.process.JavaProcess;
import lombok.Getter;

/**
 * The {@code RuntimeManager} handles the execution, tracking, and lifecycle management
 * of concurrent background tasks, including native terminal commands, external Java processes,
 * and JShell scripts.
 * <p>
 * This class abstracts a specialized {@link ThreadPoolExecutor} and hooks into an
 * {@link EventsDispatcher} to fire lifecycle notifications (e.g., before and after execution hooks).
 * It also maintains an active registry of tasks to support global runtime actions like
 * terminating all executing tasks simultaneously.
 * </p>
 */
@Getter
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class RuntimeManager {

    /**
     * Managed thread pool executor dedicated to running background plugins,
     * external sub-processes, and Java evaluations safely.
     */
    private final ThreadPoolExecutor poolExecutor;

    /**
     * Internal event dispatcher responsible for broadcasting task lifecycle events
     * (such as pre-execution setups or post-execution cleanups) to registered workspace listeners.
     */
    private final EventsDispatcher eventsDispatcher = new EventsDispatcher();

    /**
     * Thread-safe global registry containing all active, running, or stalled background
     * runtime tasks managed by this system.
     */
    private final List<AbstractTask> tasks = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructs a new {@code RuntimeManager} with a fully customized internal thread pool pool.
     * Overrides standard execution lifecycle interceptors to trigger global events natively.
     *
     * @param corePoolSize    the minimum number of threads to keep alive in the pool
     * @param maximumPoolSize the maximum bound of concurrent threads allowed in the pool
     * @param keepAliveTime   idle thread survival wait limit before being purged
     * @param unit            the timescale unit for the keepAliveTime parameter
     */
    public RuntimeManager(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit
    ) {
        // Enforce a bounded queue to match maximum pool capacity restrictions explicitly
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maximumPoolSize);

        this.poolExecutor = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            queue
        ) {
            /**
             * Interceptor triggered immediately before a thread starts processing a task.
             * Broadcasts an execution event out to all registered workspace monitors.
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                eventsDispatcher.fireEvent(IRuntime.BEFORE_EXECUTE_EVENT, t, r);
            }

            /**
             * Interceptor triggered immediately after a task finishes running.
             * Captures any thrown uncaught runtime exceptions and alerts listeners.
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                eventsDispatcher.fireEvent(IRuntime.AFTER_EXECUTE_EVENT, r, t);
            }
        };
    }

    /**
     * Schedules a standard {@link Runnable} task into the background execution pool queue.
     * Automatically inspects if the job matches the {@link AbstractTask} specification
     * to safely track it in the active task registry.
     *
     * @param runnable the executable task to schedule
     */
    public void take(Runnable runnable) {
        if (runnable instanceof AbstractTask task) {
            register(task);
        }
        poolExecutor.execute(runnable);
    }

    /**
     * Manually registers a managed task directly into the internal tracker list.
     * Useful for binding externally managed threads that shouldn't be governed
     * by the primary internal thread pool pool.
     *
     * @param task the abstract task structure to track
     */
    public void register(AbstractTask task) {
        tasks.add(task);
    }

    /**
     * Overloaded helper method to queue and register an explicitly structured
     * {@link AbstractTask} immediately into the background tracking and thread pool engine.
     *
     * @param task the abstract task to register and process
     */
    public void take(AbstractTask task) {
        register(task);
        poolExecutor.execute(task);
    }

    /**
     * Centralized factory and routing gateway for routing user execution requests
     * to their respective processing modules based on an operational runtime routing flag.
     *
     * @param command    the target terminal sequence, path to execute, or code snippet to interpret
     * @param workingDir the targeted absolute base folder paths where operations should occur
     * @param mode       numeric mode selection token matching defined {@link IRuntime} standards
     * @return an {@link AbstractTask} monitoring handle for the underlying running process instance
     * @throws IOException           if system directory transitions or subprocess configurations fail
     * @throws IllegalStateException if an unsupported execution mode identifier is passed
     */
    @SuppressWarnings("checkstyle:InnerAssignment")
    public AbstractTask run(String command, String workingDir, int mode) throws IOException {
        AbstractTask task;
        switch (mode) {
            case IRuntime.JAVA_APP_MODE, IRuntime.NATIVE_COMMAND_MODE -> task = run(command, workingDir);
            case IRuntime.SCRIPTED_METHOD_MODE -> task = eval(command, workingDir);
            case IRuntime.SCRIPTED_FILE_MODE -> task = evalSnippet(command, workingDir);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        return task;
    }

    /**
     * Evaluates custom plain-text code snippets on-the-fly via isolated execution engines.
     * Injectively patches runtime parameters into the session before parsing logic commands.
     *
     * @param command    the code block statement to run inside the engine instance
     * @param workingDir the folder location system IO operations inside the engine should resolve to
     * @return the tracking handle wrapper representing the managed script engine execution thread
     * @throws IOException if engine instance loading, mapping, or initialization scripts fail
     */
    public AbstractTask eval(String command, String workingDir) throws IOException {
        // Initialize an isolated evaluation context with a unique time-based identity string
        JShellProcess process = JShellProcess.createAndInitialize(
            String.valueOf(System.currentTimeMillis())
        );
        take(process);

        // Dynamically override Java's internal working directory configuration context inside the instance
        if (workingDir != null && !workingDir.isBlank()) {
            // Escapes Windows backslashes so paths don't collapse into invalid escape control characters
            process.execute("System.setProperty(\"user.dir\", \""
                + workingDir.replace("\\", "\\\\") + "\");");
        }
        process.execute(command);

        return process;
    }

    /**
     * Evaluates external standalone script source code files located on disk.
     * Explicitly resolves relative paths based on user configuration contexts safely.
     *
     * @param command    the system filepath string leading to the targeted script text source
     * @param workingDir the base folder used to resolve relative paths and internal file references
     * @return the tracking handle representing the running evaluation session
     * @throws IOException if script reading, target resolution, or engine execution fails
     */
    public AbstractTask evalSnippet(String command, String workingDir) throws IOException {
        JShellProcess process = JShellProcess.createAndInitialize(
            String.valueOf(System.currentTimeMillis())
        );
        take(process);

        Path scriptPath = Path.of(command);

        // If the file parameter path isn't absolute, safely re-anchor it using the assigned working directory
        if (workingDir != null && !workingDir.isBlank() && !scriptPath.isAbsolute()) {
            scriptPath = Path.of(workingDir).resolve(scriptPath);
        }

        // Apply environment folder re-anchoring to the scripting runtime context
        if (workingDir != null && !workingDir.isBlank()) {
            process.execute("System.setProperty(\"user.dir\", \""
                + workingDir.replace("\\", "\\\\") + "\");");
        }
        process.execute(scriptPath);

        return process;
    }

    /**
     * Convenience method to start an external terminal native command or binary sub-process
     * without defining a specific active working directory context.
     *
     * @param command the terminal executable sequence or string array statement to run
     * @return a wrapped process monitor abstraction tracking execution statuses
     * @throws IOException if underlying platform shell integrations or executable calls fail
     */
    public JavaProcess run(String command) throws IOException {
        return run(command, null);
    }

    /**
     * Launches a native system sub-process using shell parsing rules.
     * Sets environment parameters and isolates error streams natively.
     * Sets environment parameters and isolates error streams natively.
     *
     * @param command    the target script or command sequence string line to execute
     * @param workingDir the destination workspace folder context where the shell command executes
     * @return a wrapped tracking abstraction referencing the live operating system process handles
     * @throws IOException if system calls fail, or if the specified working directory does not exist
     */
    public JavaProcess run(String command, String workingDir) throws IOException {
        // Spins up an isolated Unix/Linux shell environment wrapper to process arbitrary terminal inputs
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);

        // Redirect standard error streams directly into standard output for consolidated log handling
        pb.redirectErrorStream(true);

        // Formally configure the active execution path block context for the native sub-process builder
        if (workingDir != null && !workingDir.isBlank()) {
            pb.directory(new File(workingDir));
        }

        // Start the process execution stream and track it using an integer timestamp sequence
        JavaProcess process = new JavaProcess(
            pb.start(),
            String.valueOf(System.currentTimeMillis())
        );
        take(process);
        return process;
    }

    /**
     * Retrieves a snapshot copy of all active registered tasks currently monitored by this runtime.
     * Utilizes explicit array isolation locks to guarantee cross-thread concurrency safety.
     *
     * @return a fresh array list copy containing current snapshot allocations
     */
    public List<AbstractTask> getAllTasks() {
        synchronized (tasks) {
            return new ArrayList<>(tasks);
        }
    }

    /**
     * Manually deletes an assigned task from the active monitoring system registry.
     *
     * @param task the specific objective container allocation to remove
     */
    public void remove(AbstractTask task) {
        tasks.remove(task);
    }

    /**
     * Scans through the registry system to automatically clear out any tasks
     * that have already completed, crashed, or terminated.
     */
    public void removeTerminated() {
        tasks.removeIf(task -> !task.isAlive());
    }

    /**
     * Sends immediate execution termination interrupt signals out across all tracked
     * background tasks simultaneously.
     */
    public void stopAll() {
        getAllTasks().forEach(AbstractTask::stop);
    }

    /**
     * Hooks a custom listener instance up to the system's underlying runtime event engine.
     *
     * @param l the target listener implementation to bind
     * @return {@code true} if registration completes successfully, {@code false} otherwise
     */
    public boolean addListener(IWorkspaceListener l) {
        return eventsDispatcher.addListener(l);
    }

    /**
     * Unregisters a custom listener instance, stopping it from receiving future execution notifications.
     *
     * @param l the target listener instance to detach
     * @return {@code true} if deletion succeeds, {@code false} otherwise
     */
    public boolean removeListener(IWorkspaceListener l) {
        return eventsDispatcher.removeListener(l);
    }

    /**
     * Inspects the execution pool queue allocations to extract a copy list
     * of all pending unexecuted runnables.
     *
     * @return a list containing all currently queued tasks waiting for a thread allocation
     */
    public List<Runnable> getQueueTasks() {
        return poolExecutor
            .getQueue()
            .stream()
            .toList();
    }

    /**
     * Checks the internal executor pool engine state to read out the exact number
     * of threads actively running operations.
     *
     * @return the total number of actively processing threads inside the pool right now
     */
    public int getActiveCount() {
        return poolExecutor.getActiveCount();
    }

    /**
     * Safely shuts down the manager. It terminates all executing sub-processes
     * and stops the internal thread pool from accepting any new work requests.
     */
    public void yield() {
        stopAll();
        poolExecutor.shutdown();
    }
}