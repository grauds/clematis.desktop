package jworkspace.api;

public interface IRuntime {
    /**
     *
     */
    int BEFORE_EXECUTE_EVENT = 2000;
    /**
     *
     */
    int AFTER_EXECUTE_EVENT = 2001;
    /**
     *
     */
    int SCRIPTED_METHOD_MODE = 0;
    /**
     *
     */
    int SCRIPTED_FILE_MODE = 1;
    /**
     *
     */
    int NATIVE_COMMAND_MODE = 2;
    /**
     *
     */
    int JAVA_APP_MODE = 3;
}
