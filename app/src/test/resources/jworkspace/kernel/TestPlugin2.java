package jworkspace.kernel;

import jworkspace.api.IWorkspaceListener;

/**
 * @author Anton Troshin
 */
public class TestPlugin2 implements ITestPlugin, IWorkspaceListener {

    @SuppressWarnings("checkstyle:MagicNumber")
    private int value = 3;
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int doPluginWork() {
        Workspace.addListener(this);
        return value;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public void processEvent(Integer event, Object lparam, Object rparam) {

        if (lparam instanceof Integer && rparam instanceof Integer) {
            value += (int) lparam;
            value += (int) rparam;
        }
    }
}
