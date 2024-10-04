package jworkspace.kernel;

import jworkspace.runtime.ITestPlugin;
import jworkspace.runtime.TestWorkspace;

/**
 * @author Anton Troshin
 */
public class TestPlugin implements ITestPlugin {

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int doPluginWork() {
        TestWorkspace.EVENTS_DISPATCHER.fireEvent(0, 2, 3);
        return 2;
    }
}
