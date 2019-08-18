package jworkspace.kernel;

/**
 * @author Anton Troshin
 */
public class TestPlugin implements ITestPlugin {

    @Override
    public int doPluginWork() {
        Workspace.fireEvent(0, 2, 3);
        return 2;
    }
}
