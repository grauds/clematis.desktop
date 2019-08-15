package jworkspace.kernel;

/**
 * @author Anton Troshin
 */
public class TestPlugin implements ITestPlugin {

    @Override
    public int doPluginWork() {
        return 2;
    }
}
