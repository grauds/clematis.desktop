package jworkspace.ui.plugins;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.Task;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.ui.MainFrame;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IShell;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.api.cpanel.CButton;
import jworkspace.ui.api.views.DefaultCompoundView;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.users.Profile;
import jworkspace.users.ProfilesManager;
import lombok.extern.java.Log;

/*
 * This class uses a working thread to load plugins
 *
 * @author Anton Troshin
 */
@Log
public class ShellsLoader extends Task {

    static final int PROGRESS_COMPLETED = 100;

    private final ProgressDialog progressDialog;

    public ShellsLoader(ProgressDialog progressDialog) {
        super();
        this.progressDialog = progressDialog;
    }

    public void run() {

        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        Profile currentProfile = profilesManager.getCurrentProfile();
        Path pluginPath;

        if (currentProfile != null) {
            pluginPath = currentProfile.getProfilePath(profilesManager.getBasePath());
        } else {
            pluginPath = profilesManager.getBasePath();
        }

        pluginPath = pluginPath.resolve("shells");

        WorkspacePluginLocator pluginLocator = DesktopServiceLocator.getInstance().getPluginLocator();
        pluginLocator.setParentPluginClassLoader(this.getClass().getClassLoader());
        List<Plugin> plugins = pluginLocator.loadPlugins(
            pluginPath
        );
        ServiceLocator.getInstance().getUserPlugins().addAll(plugins);

        if (plugins.isEmpty()) {
            showMessageInProgressDialog(WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.notFound"));
            notifyObservers(PROGRESS_COMPLETED);
        } else {

            setPercentInProgressDialog(0);
            notifyObservers(0);

            for (int i = 0; i < plugins.size(); i++) {
                showMessageInProgressDialog(
                    WorkspaceResourceAnchor.getString("WorkspaceGUI.shell.loading")
                    + Constants.LOG_SPACE + plugins.get(i).getName()
                );

                log.info("Loading " + plugins.get(i).getName() + Constants.LOG_FINISH);
                showIconInProgressDialog(plugins.get(i).getIcon());

                try {
                    installPlugin(plugins.get(i));

                    String installedMessage = "Installed " + plugins.get(i).getName() + Constants.LOG_FINISH;
                    showMessageInProgressDialog(installedMessage);
                    log.info(installedMessage);
                } catch (Exception | Error ex) {
                    String failedMessage = "Plugin " + plugins.get(i).getName() + " failed to load: " + ex;
                    showMessageInProgressDialog(failedMessage);
                    log.warning(failedMessage);
                }

                int percent = i * PROGRESS_COMPLETED / plugins.size();
                setPercentInProgressDialog(percent);
                notifyObservers(percent);
            }

            setPercentInProgressDialog(PROGRESS_COMPLETED);
            notifyObservers(PROGRESS_COMPLETED);
        }
    }

    private void setPercentInProgressDialog(int percent) {
        if (this.progressDialog != null) {
            this.progressDialog.setProgress(percent);
        }
    }

    private void showMessageInProgressDialog(String message) {
        if (this.progressDialog != null && message != null) {
            this.progressDialog.setMessage(message);
        }
    }

    private void showIconInProgressDialog(Icon icon) {
        if (this.progressDialog != null && icon != null) {
            this.progressDialog.setIcon(icon);
        }
    }

    /**
     * Install shell into content manager
     */
    private void installPlugin(Plugin plugin) throws PluginException {

        Object obj = plugin.newInstance();
        if (obj instanceof IShell shell) {
            try {
                shell.load();
            } catch (IOException ex) {
                log.warning("> System error: Shell cannot be loaded: " + ex);
            }
            /*
             * Ask for buttons and display them in the control panel
             */
            MainFrame mainFrame = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
            CButton[] buttons = shell.getButtons();
            if (buttons != null && buttons.length > 0) {
                mainFrame.getControlPanel().addSeparator();
                for (CButton button : buttons) {
                    mainFrame.getControlPanel().addButton(button);
                }
            }
            /*
             * Notify plugin that the buttons are available and attach UIManager
             * to track look and feel changes
             */
            if (shell instanceof DefaultCompoundView defaultCompoundView) {
                defaultCompoundView.setButtonsLoaded(true);
                UIManager.addPropertyChangeListener(new UISwitchListener(defaultCompoundView));
            }
        }
    }
}
