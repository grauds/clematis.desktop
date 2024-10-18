package jworkspace.ui.config;

import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.plaf.PlafFactory;
import lombok.Getter;

@Getter
public class DesktopServiceLocator {

    private final WorkspaceGUI workspaceGUI = new WorkspaceGUI();

    private final WorkspacePluginLocator pluginLocator = new WorkspacePluginLocator();

    private final UIConfig uiConfig = new UIConfig();

    private final PlafFactory plafFactory = new PlafFactory();

    private DesktopServiceLocator() {}

    public static DesktopServiceLocator getInstance() {
        return DesktopServiceLocator.InstanceHolder.SERVICE_LOCATOR;
    }

    private static final class InstanceHolder {
        private static final DesktopServiceLocator SERVICE_LOCATOR = new DesktopServiceLocator();
    }
}
