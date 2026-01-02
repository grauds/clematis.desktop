package jworkspace.ui.plugins;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003, 2019, 2025 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.runtime.Task;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
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

/**
 * Task that loads system and user plugins (shells) in a background thread.
 * It updates a {@link ProgressDialog} to inform the user about the progress of loading.
 * <p>
 * The class is responsible for:
 * <ul>
 *     <li>Loading system and user plugins from the corresponding directories.</li>
 *     <li>Installing shells into the content manager.</li>
 *     <li>Adding shell buttons to the main control panel.</li>
 *     <li>Updating progress and messages in a {@link ProgressDialog}.</li>
 * </ul>
 *
 * Author: Anton Troshin
 */
@Log
public class ShellsLoader extends Task {

    /** Constant representing 100% progress */
    static final int PROGRESS_COMPLETED = 100;

    /** Directory name under which shell plugins are located */
    static final String SHELLS_DIRECTORY = "shells";

    /** Reference to the progress dialog used to show progress messages and percentage */
    private final ProgressDialog progressDialog;

    /**
     * Constructor for ShellsLoader.
     *
     * @param progressDialog Progress dialog to show progress updates, can be null
     */
    public ShellsLoader(ProgressDialog progressDialog) {
        super();
        this.progressDialog = progressDialog;
    }

    /**
     * Loads plugins from a specified directory using the provided plugin locator.
     * Adds the loaded plugins to the user plugin list in the ServiceLocator.
     *
     * @param pluginLocator Plugin locator used to find and load plugins
     * @param pluginPath Base path for the plugins
     * @param type Type of plugins to load (system/user)
     * @return List of loaded plugins
     */
    private List<Plugin> loadPlugins(WorkspacePluginLocator pluginLocator, Path pluginPath, String type) {
        // Load plugins from the "shells" directory of the given path
        List<Plugin> plugins = pluginLocator.loadPlugins(
            pluginPath.resolve(SHELLS_DIRECTORY), type
        );

        // Add loaded plugins to the user plugin registry
        ServiceLocator.getInstance().getUserPlugins().addAll(plugins);
        return plugins;
    }

    /**
     * Main execution method called by the background task framework.
     * Loads system and user plugins, installs them, and updates the progress dialog.
     */
    public void run() {

        // Retrieve the current profile and plugin locator
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        Profile currentProfile = profilesManager.getCurrentProfile();

        WorkspacePluginLocator pluginLocator = ServiceLocator.getInstance().getPluginLocator();
        pluginLocator.setParentPluginClassLoader(this.getClass().getClassLoader());

        // Load system plugins from the base path
        Path pluginPath = profilesManager.getBasePath();
        List<Plugin> plugins = loadPlugins(pluginLocator, pluginPath, PluginDTO.PLUGIN_TYPE_SYSTEM);

        // If a user profile exists, load user-specific plugins as well
        if (currentProfile != null) {
            pluginPath = currentProfile.getProfilePath(profilesManager.getBasePath());
            plugins.addAll(loadPlugins(pluginLocator, pluginPath, PluginDTO.PLUGIN_TYPE_USER));
        }

        // If no plugins were found, show a message and complete the task
        if (plugins.isEmpty()) {
            showMessageInProgressDialog(WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.notFound"));
            notifyObservers(PROGRESS_COMPLETED);
        } else {

            // Initialize progress dialog and notify observers
            setPercentInProgressDialog(0);
            notifyObservers(0);

            // Load and install each plugin
            for (int i = 0; i < plugins.size(); i++) {
                // Update message in progress dialog
                showMessageInProgressDialog(
                    WorkspaceResourceAnchor.getString("WorkspaceGUI.shell.loading")
                        + Constants.LOG_SPACE + plugins.get(i).getName()
                );

                log.info("Loading " + plugins.get(i).getName() + Constants.LOG_FINISH);
                showIconInProgressDialog(plugins.get(i).getIcon());

                try {
                    // Install the plugin into the workspace
                    installPlugin(plugins.get(i));

                    String installedMessage = "Installed " + plugins.get(i).getName() + Constants.LOG_FINISH;
                    showMessageInProgressDialog(installedMessage);
                    log.info(installedMessage);
                } catch (Exception | Error ex) {
                    // Handle plugin load failures gracefully
                    String failedMessage = "Plugin " + plugins.get(i).getName() + " failed to load: " + ex;
                    showMessageInProgressDialog(failedMessage);
                    log.warning(failedMessage);
                }

                // Update progress percentage
                int percent = i * PROGRESS_COMPLETED / plugins.size();
                setPercentInProgressDialog(percent);
                notifyObservers(percent);
            }

            // Finalize progress
            setPercentInProgressDialog(PROGRESS_COMPLETED);
            notifyObservers(PROGRESS_COMPLETED);
        }
    }

    /**
     * Sets the progress percentage in the progress dialog.
     *
     * @param percent Progress value (0-100)
     */
    private void setPercentInProgressDialog(int percent) {
        if (this.progressDialog != null) {
            this.progressDialog.setProgress(percent);
        }
    }

    /**
     * Displays a message in the progress dialog.
     *
     * @param message Message to display
     */
    private void showMessageInProgressDialog(String message) {
        if (this.progressDialog != null && message != null) {
            this.progressDialog.setMessage(message);
        }
    }

    /**
     * Displays an icon in the progress dialog.
     *
     * @param icon Icon to display
     */
    private void showIconInProgressDialog(Icon icon) {
        if (this.progressDialog != null && icon != null) {
            this.progressDialog.setIcon(icon);
        }
    }

    /**
     * Installs a shell plugin into the workspace content manager.
     * <p>
     * Steps:
     * <ul>
     *     <li>Create a new instance of the plugin.</li>
     *     <li>If it implements {@link IShell}, call {@code load()}.</li>
     *     <li>Add any shell buttons to the main control panel.</li>
     *     <li>If it is a {@link DefaultCompoundView}, attach a UIManager listener to track look-and-feel changes.</li>
     * </ul>
     *
     * @param plugin Plugin to install
     * @throws PluginException if the plugin cannot be instantiated or loaded
     */
    private void installPlugin(Plugin plugin) throws PluginException {

        Object obj = plugin.newInstance();
        if (obj instanceof IShell shell) {
            try {
                shell.load(); // Load user specific info
            } catch (IOException ex) {
                log.warning("> System error: Shell cannot be loaded: " + ex);
            }

            // Retrieve shell buttons and add them to the main control panel
            MainFrame mainFrame = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
            CButton[] buttons = shell.getButtons();
            if (buttons != null && buttons.length > 0) {
                mainFrame.getControlPanel().addSeparator();
                for (CButton button : buttons) {
                    mainFrame.getControlPanel().addButton(button);
                }
            }

            // Notify shell that buttons are loaded and attach UIManager listener for look-and-feel changes
            if (shell instanceof DefaultCompoundView defaultCompoundView) {
                defaultCompoundView.setButtonsLoaded(true);
                UIManager.addPropertyChangeListener(new UISwitchListener(defaultCompoundView));
            }
        }
    }
}
