package jworkspace.kernel;

/**
 * @author Anton Troshin
 */
public class TestPlugin2 implements ITestPlugin {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int doPluginWork() {
        return 3;
    }
}
