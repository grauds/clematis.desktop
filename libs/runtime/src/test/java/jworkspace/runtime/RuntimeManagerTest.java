package jworkspace.runtime;

import java.util.concurrent.TimeUnit;

import static jworkspace.runtime.RuntimeManager.AFTER_EXECUTE_EVENT;
import static jworkspace.runtime.RuntimeManager.BEFORE_EXECUTE_EVENT;
import jworkspace.api.IWorkspaceListener;

import org.junit.jupiter.api.Test;

public class RuntimeManagerTest {

    private final RuntimeManager runtimeManager = new RuntimeManager(9,
        18,
        30,
        TimeUnit.MINUTES
    );

    @Test
    public void testTwoTasks() {
        Thread thread1 = new Thread(() -> System.out.println(1));
        Thread thread2 = new Thread(() -> System.out.println(2));
        runtimeManager.addListener(new IWorkspaceListener() {

            @Override
            public int getCode() {
                return BEFORE_EXECUTE_EVENT;
            }

            @Override
            public void processEvent(Integer event, Object lparam, Object rparam) {
                System.out.println("Starting: " + ((Thread) rparam).getName());
            }
        });
        runtimeManager.addListener(new IWorkspaceListener() {
            @Override
            public int getCode() {
                return AFTER_EXECUTE_EVENT;
            }

            @Override
            public void processEvent(Integer event, Object lparam, Object rparam) {
                System.out.println("Finished: " + ((Thread) lparam).getName());
            }
        });
        runtimeManager.take(thread1);
        runtimeManager.take(thread2);
    }
}
