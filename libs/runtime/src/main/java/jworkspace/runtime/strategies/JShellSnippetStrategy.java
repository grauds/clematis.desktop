package jworkspace.runtime.strategies;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.jshell.JShellProcess;

/**
 * Strategy for executing JShell code from standalone script files.
 */
public class JShellSnippetStrategy implements IExecutionStrategy {

    @Override
    public AbstractTask execute(String command, String workingDir, Consumer<AbstractTask> registry) throws IOException {
        JShellProcess process = JShellProcess.createAndInitialize(
            String.valueOf(System.currentTimeMillis())
        );

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

        registry.accept(process);
        return process;
    }
}
