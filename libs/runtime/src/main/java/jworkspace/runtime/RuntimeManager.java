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
import java.io.IOException;
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
import jworkspace.runtime.process.JavaProcess;
import lombok.Getter;

@Getter
public class RuntimeManager {

    /**
     * Thread pool executor for plugins and Java programs.
     */
    private final ThreadPoolExecutor poolExecutor;

    /**
     * Dispatcher for lifecycle events.
     */
    private final EventsDispatcher eventsDispatcher = new EventsDispatcher();

    /**
     * Runtime task registry.
     */
    private final List<AbstractTask> tasks = Collections.synchronizedList(new ArrayList<>());

    public RuntimeManager(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit
    ) {

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maximumPoolSize);
        this.poolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                queue
            ) {

                @Override
                protected void beforeExecute(Thread t, Runnable r) {
                    eventsDispatcher.fireEvent(IRuntime.BEFORE_EXECUTE_EVENT, t, r);
                }

                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    eventsDispatcher.fireEvent(IRuntime.AFTER_EXECUTE_EVENT, r, t);
                }
            };
    }

    /**
     * Execute runnable.
     */
    public void take(Runnable runnable) {
        if (runnable instanceof AbstractTask task) {
            register(task);
        }
        poolExecutor.execute(runnable);
    }

    /**
     * Register task without executing it.
     * Useful for externally-managed threads.
     */
    public void register(AbstractTask task) {
        tasks.add(task);
    }

    /**
     * Execute task.
     */
    public void take(AbstractTask task) {
        register(task);
        poolExecutor.execute(task);
    }

    /**
     * Launch external process.
     */
    public JavaProcess run(String command) throws IOException {

        JavaProcess process =
            new JavaProcess(
                Runtime.getRuntime().exec(command),
                String.valueOf(
                    System.currentTimeMillis()
                )
            );

        take(process);
        return process;
    }

    /**
     * All runtime tasks.
     */
    public List<AbstractTask> getAllTasks() {
        synchronized (tasks) {
            return new ArrayList<>(tasks);
        }
    }

    /**
     * Remove task from registry.
     */
    public void remove(AbstractTask task) {
        tasks.remove(task);
    }

    /**
     * Remove completed tasks.
     */
    public void removeTerminated() {
        tasks.removeIf(task -> !task.isAlive());
    }

    /**
     * Stop all tasks.
     */
    public void stopAll() {
        getAllTasks().forEach(AbstractTask::stop);
    }

    public boolean addListener(IWorkspaceListener l) {
        return eventsDispatcher.addListener(l);
    }

    public boolean removeListener(IWorkspaceListener l) {
        return eventsDispatcher.removeListener(l);
    }

    public List<Runnable> getQueueTasks() {
        return poolExecutor
            .getQueue()
            .stream()
            .toList();
    }

    public int getActiveCount() {
        return poolExecutor.getActiveCount();
    }

    public void yield() {
        stopAll();
        poolExecutor.shutdown();
    }
}