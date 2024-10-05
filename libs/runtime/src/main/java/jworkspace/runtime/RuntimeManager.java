package jworkspace.runtime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jworkspace.api.EventsDispatcher;
import jworkspace.api.IWorkspaceComponent;
import jworkspace.api.IWorkspaceListener;

/**
 * Runtime manager workspace component
 */
public class RuntimeManager implements IWorkspaceComponent {
    /**
     * After execution event number
     */
    public static final int AFTER_EXECUTE_EVENT = 1001;
    /**
     * Before execution event number
     */
    public static final int BEFORE_EXECUTE_EVENT = 1000;

    private final ThreadPoolExecutor poolExecutor;
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

    public boolean addListener(IWorkspaceListener l) {
        return this.eventsDispatcher.addListener(l);
    }

    public boolean removeListener(IWorkspaceListener l) {
        return this.eventsDispatcher.removeListener(l);
    }

    public List<Runnable> getTasks() {
        return this.poolExecutor.getQueue().stream().toList();
    }

    @Override
    public void load() throws IOException {

    }

    @Override
    public void save() throws IOException {

    }

    @Override
    public void reset() {
        this.poolExecutor.shutdownNow();
    }

    @Override
    public String getName() {
        return "Runtime Manager";
    }

    public int getActiveCount() {
        return this.poolExecutor.getActiveCount();
    }
}
