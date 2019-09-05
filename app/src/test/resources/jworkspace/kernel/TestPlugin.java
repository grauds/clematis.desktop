package jworkspace.kernel;

/**
 * @author Anton Troshin
 */
public class TestPlugin implements ITestPlugin {

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int doPluginWork() {
        Workspace.fireEvent(0, 2, 3);
        return 2;
    }
}
