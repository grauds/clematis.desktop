package jworkspace.runtime.strategies;

import java.io.IOException;
import java.util.function.Consumer;

import jworkspace.runtime.AbstractTask;

/**
 * Defines a strategy for executing a specific type of command or script
 * within the Clematis Java Workspace environment.
 */
public interface IExecutionStrategy {

    /**
     * Executes the given command using the specific strategy logic.
     *
     * @param command    the input command, script, or path to execute
     * @param workingDir the directory context for the execution
     * @param registry    a callback to register the created task
     * @return a managed {@link AbstractTask} representing the running execution
     * @throws IOException if the execution cannot be started or fails to initialize
     */
    AbstractTask execute(String command, String workingDir, Consumer<AbstractTask> registry) throws IOException;
}
