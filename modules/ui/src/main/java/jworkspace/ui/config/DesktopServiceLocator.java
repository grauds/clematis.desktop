package jworkspace.ui.config;

import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.ui.WorkspaceGUI;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DesktopServiceLocator {

    @Setter
    private WorkspaceGUI workspaceGUI;

    private final WorkspacePluginLocator pluginLocator = new WorkspacePluginLocator();

    private final UIConfig uiConfig = new UIConfig();

    private DesktopServiceLocator() {}

    public static DesktopServiceLocator getInstance() {
        return DesktopServiceLocator.InstanceHolder.SERVICE_LOCATOR;
    }

    private static final class InstanceHolder {
        private static final DesktopServiceLocator SERVICE_LOCATOR = new DesktopServiceLocator();
    }
}
