package jworkspace.runtime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jworkspace.api.EventsDispatcher;
import jworkspace.api.IWorkspaceListener;

/**
 * Runtime manager component
 */
public class RuntimeManager {
    /**
     * After execution event number
     */
    public static final int AFTER_EXECUTE_EVENT = 1000;
    /**
     * Before execution event number
     */
    public static final int BEFORE_EXECUTE_EVENT = 1001;
    /**
     * Thread pool executor for plugins and Java programs
     */
    private final ThreadPoolExecutor poolExecutor;
    /**
     * Dispatcher for lifecycle events of the threads in the pool
     */
    private final EventsDispatcher eventsDispatcher = new EventsDispatcher();

    public RuntimeManager(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maximumPoolSize);
        this.poolExecutor = new ThreadPoolExecutor(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, queue
        ) {
            /**
             * Method invoked prior to executing the given Runnable in the given thread.
             * This method is invoked by thread t that will execute task r
             *
             * @param t the thread that will run task {@code r}
             * @param r the task that will be executed
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                eventsDispatcher.fireEvent(BEFORE_EXECUTE_EVENT, t, r);
            }

            /**
             * Useful callback to notify listeners about the thread execution results.
             * @param r the runnable that has completed
             * @param t the exception that caused termination, or null if
             * execution completed normally
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                eventsDispatcher.fireEvent(AFTER_EXECUTE_EVENT, r, t);
            }
        };
    }

    public void take(Runnable runnable) {
        this.poolExecutor.execute(runnable);
    }

    public Runnable run(String command) throws IOException {
        JavaProcess process
            = new JavaProcess(Runtime.getRuntime().exec(command), String.valueOf(System.currentTimeMillis()));
        take(process);
        return process;
    }

    public boolean addListener(IWorkspaceListener l) {
        return this.eventsDispatcher.addListener(l);
    }

    public boolean removeListener(IWorkspaceListener l) {
        return this.eventsDispatcher.removeListener(l);
    }

    public List<Runnable> getQueueTasks() {
        return this.poolExecutor.getQueue().stream().toList();
    }

    public int getActiveCount() {
        return this.poolExecutor.getActiveCount();
    }

    public void yield() {
        this.poolExecutor.shutdown();
    }
}
