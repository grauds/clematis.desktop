package jworkspace.runtime.strategies;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.process.JavaProcess;

/**
 * Strategy for executing native system commands or Java applications via the shell.
 */
public class NativeCommandStrategy implements IExecutionStrategy {

    @Override
    public AbstractTask execute(String command, String workingDir, Consumer<AbstractTask> registry) throws IOException {
        // Replicates the original behavior of using /bin/bash -c for command execution
        // and correctly setting the working directory.
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
        pb.redirectErrorStream(true);

        if (workingDir != null && !workingDir.isBlank()) {
            pb.directory(new File(workingDir));
        }

        // JavaProcess handles the log reading and lifecycle management
        AbstractTask task = new JavaProcess(pb.start(), "NativeCommand-" + System.currentTimeMillis());
        registry.accept(task);
        return task;
    }
}
