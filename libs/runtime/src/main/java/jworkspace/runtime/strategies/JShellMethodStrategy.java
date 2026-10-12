package jworkspace.runtime.strategies;

import java.io.IOException;
import java.util.function.Consumer;

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.jshell.JShellProcess;

/**
 * Strategy for executing JShell code snippets as method calls.
 */
public class JShellMethodStrategy implements IExecutionStrategy {

    @Override
    public AbstractTask execute(String command, String workingDir, Consumer<AbstractTask> registry) throws IOException {
        // Initialize an isolated evaluation context with a unique time-based identity string
        JShellProcess process = JShellProcess.createAndInitialize(
            String.valueOf(System.currentTimeMillis())
        );
        
        // Dynamically override Java's internal working directory configuration context inside the instance
        if (workingDir != null && !workingDir.isBlank()) {
            // Escapes Windows backslashes so paths don't collapse into invalid escape control characters
            process.execute("System.setProperty(\"user.dir\", \""
                + workingDir.replace("\\", "\\\\") + "\");");
        }
        process.execute(command);

        registry.accept(process);
        return process;
    }
}
