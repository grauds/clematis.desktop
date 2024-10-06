package jworkspace.runtime;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static jworkspace.runtime.RuntimeManager.AFTER_EXECUTE_EVENT;
import static jworkspace.runtime.RuntimeManager.BEFORE_EXECUTE_EVENT;
import jworkspace.api.IWorkspaceListener;
import jworkspace.installer.Application;
import jworkspace.installer.JVM;
import jworkspace.installer.WorkspaceInstaller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

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

    @Test
    public void testJavaProcess() throws IOException {

        // create a test folder to hold the configuration files
        TemporaryFolder testFolder = new TemporaryFolder();
        testFolder.create();

        // get created root file
        File dataRoot = testFolder.getRoot().toPath().toFile();
        WorkspaceInstaller workspaceInstaller = new WorkspaceInstaller(dataRoot);


        // create default jvm
        JVM jvm = JVM.getCurrentJvm(workspaceInstaller.getJvmData().getRoot());

        // register the sample program
        Application sampleJavaProgram = new Application(workspaceInstaller.getApplicationData().getRoot(), "test");
        sampleJavaProgram.setMainClass("jworkspace.runtime.SampleJavaProgram");
        sampleJavaProgram.setJvm(jvm.getLinkString());

        // get invocation arguments
        String[] args = workspaceInstaller.getInvocationArgs(sampleJavaProgram);
        runtimeManager.addListener(new IWorkspaceListener() {
            @Override
            public int getCode() {
                return AFTER_EXECUTE_EVENT;
            }

            @Override
            public void processEvent(Integer event, Object lparam, Object rparam) {
                Assertions.assertEquals("Test process", ((JavaProcess) lparam).getName());
            }
        });
        runtimeManager.take(new JavaProcess(args, "Test process"));
        Assertions.assertEquals(1, runtimeManager.getActiveCount());
    }
}
