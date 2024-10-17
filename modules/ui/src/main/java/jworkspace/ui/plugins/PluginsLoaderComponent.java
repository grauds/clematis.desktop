package jworkspace.ui.plugins;

import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.config.DesktopServiceLocator;

public class PluginsLoaderComponent {

    /**
     * Progress dialog for observing shells load.
     */
    private final ProgressDialog pr;
    /**
     * Plugins loader
     */
    private final ShellsLoader shellsLoader;

    public PluginsLoaderComponent() {
        pr = new ProgressDialog(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.loading"),
            true
        );
        this.shellsLoader = new ShellsLoader(pr);
    }

    public void loadPlugins() {
        /*
         * Load plugins in a separate worker thread in order to update the swing thread
         */
        SwingUtilities.invokeLater(() -> pr.track(this.shellsLoader));
    }
}
