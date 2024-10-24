package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003, 2019 Anton Troshin

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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.ui.SplashScreen;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.util.ResourceManager;

import static jworkspace.ui.api.Constants.DISPLAY_PARAMETER;
import static jworkspace.ui.api.Constants.REGISTER_PARAMETER;
import static jworkspace.ui.api.Constants.VIEW_PARAMETER;
import jworkspace.Workspace;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.IWorkspaceUI;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.WorkspacePluginContext;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IView;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.plugins.PluginsLoaderComponent;
import jworkspace.ui.widgets.WorkspaceError;
import lombok.extern.java.Log;

/**
 * Workspace Desktop user interface
 *
 * @author Anton Troshin
 */
@Log
public class WorkspaceGUI implements IWorkspaceUI {
    /**
     * Workspace UI logo.
     */
    private static SplashScreen logo = null;
    /**
     * Plugin context to be injected by plugin loader
     */
    private final WorkspacePluginContext pluginContext;
    /**
     * Workspace main frame
     */
    private MainFrame frame = null;
    /**
     * GUI actions
     */
    private UIActions actions = null;
    /**
     * A list of displayed frames
     */
    private final ArrayList<Frame> displayedFrames = new ArrayList<>();
    /**
     * Default constructor.
     */
    public WorkspaceGUI(WorkspacePluginContext pluginContext) {
        super();

        this.pluginContext = pluginContext;

        UIChangeManager.getInstance().setDefaultFrameIcon(getResourceManager().getImage("jw_16x16.png"));
        registerListeners();
        DesktopServiceLocator.getInstance().setWorkspaceGUI(this);
    }

    UIActions getActions() {
        if (actions == null) {
            actions = new UIActions(this);
        }
        return actions;
    }

    public static ResourceManager getResourceManager() {
        return ResourceManagerHolder.RESOURCE_MANAGER;
    }

    /**
     * Register workspace listeners
     */
    private void registerListeners() {
        ServiceLocator.getInstance().getEventsDispatcher().addListener(new ExternalFrameListener());
        ServiceLocator.getInstance().getEventsDispatcher().addListener(new SwitchMenuListener());
        ServiceLocator.getInstance().getEventsDispatcher().addListener(new WorkspaceViewListener());
        ServiceLocator.getInstance().getEventsDispatcher().addListener(new WorkspaceWindowListener());
    }

    /**
     * Returns clipboard for graphic interface.
     *
     * @return java.awt.datatransfer.Clipboard
     */
    @Override
    public Clipboard getClipboard() {
        return getFrame().getClipboard();
    }

    @Override
    public MainFrame getFrame() {
        if (frame == null) {
            frame = new MainFrame(Workspace.VERSION, this);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (e.getSource() == frame) {
                        Workspace.exit();
                    }
                }
            });
        }
        return frame;
    }

    private Window getLogoScreen() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (logo == null) {
            Image im = getResourceManager().getImage("logo/Logo.png");
            logo = new SplashScreen(new Frame(), im, null);

            ImageIcon imc = new ImageIcon(im);

            Rectangle logoBounds = new Rectangle((screenSize.width
                - imc.getIconWidth()) / 2,
                (screenSize.height - imc.getIconHeight()) / 2,
                imc.getIconWidth(),
                imc.getIconHeight());

            logo.setBounds(logoBounds);
            logo.getParent().setBounds(logoBounds);
            logo.setDelay(0);
        }
        return logo;
    }

    /**
     * Returns texture image for settings dialog
     */
    public Image getTexture() {
        return DesktopServiceLocator.getInstance().getUiConfig().getTexture();
    }

    /**
     * Set texture for java workspace gui
     */
    public void setTexture(BufferedImage texture) {
        DesktopServiceLocator.getInstance().getUiConfig().setTexture(texture);
    }

    /**
     * Get default path to workspace textures
     *
     * @return path to textures jar library
     */
    public Path getTexturesPath() {

        return pluginContext
            .getUserDir()
            .resolve(Constants.LIB_PATH)
            .resolve(Constants.RES_PATH)
            .resolve(Constants.TEXTURES_JAR);
    }

    /**
     * Get default path to desktop icons
     *
     * @return path to desktop icons jar library
     */
    public Path getDesktopIconsPath() {
        return pluginContext
            .getUserDir()
            .resolve(Constants.LIB_PATH)
            .resolve(Constants.RES_PATH)
            .resolve(Constants.DESKTOP_JAR);
    }

    /**
     * Is texture visible
     */
    public boolean isTextureVisible() {
        return DesktopServiceLocator.getInstance().getUiConfig().isTextureVisible();
    }

    /**
     * Set texture visibility and validates main frame
     */
    public void setTextureVisible(boolean isTextureVisible) {

        DesktopServiceLocator.getInstance().getUiConfig().setTextureVisible(isTextureVisible);
        if (isTextureVisible && DesktopServiceLocator.getInstance().getUiConfig().getTexture() != null) {
            UIChangeManager.getInstance().setDefaultTexture(
                DesktopServiceLocator.getInstance().getUiConfig().getTexture()
            );
            getFrame().setTexture(DesktopServiceLocator.getInstance().getUiConfig().getTexture());
        } else {
            UIChangeManager.getInstance().setDefaultTexture(null);
            getFrame().setTexture(null);
        }
    }

    /**
     * Check whether this GUI is modified.
     * As this gui is a frame, ask it.
     */
    @Override
    public boolean isModified() {
        return getFrame() != null && getFrame().isModified();
    }

    /**
     * Is Kiwi texture visible?
     */
    public boolean isKiwiTextureVisible() {
        return DesktopServiceLocator.getInstance().getUiConfig().isKiwiTextureVisible();
    }


    /**
     * Loads workspace gui profile data.
     */
    public void load() {
        /*
         * Display splash screen
         */
        getLogoScreen().setVisible(true);
        /*
         * Read workspace UI configuration
         */
        DesktopServiceLocator
            .getInstance()
            .getUiConfig()
            .setConfigFile(
                this.pluginContext.getUserDir().resolve("ui").toFile()
            );

        DesktopServiceLocator.getInstance().getUiConfig().load();

        /*
         * Set undecorated property
         */
        boolean undecorated = DesktopServiceLocator.getInstance().getUiConfig().isDecorated();
        getFrame().setUndecorated(undecorated);
        if (undecorated) {
            getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }

        /*
         * Set texture on
         */
        setTextureVisible(DesktopServiceLocator.getInstance().getUiConfig().isTextureVisible());
        /*
         * Create heavy dialogs and store them in the cache
         */
        ClassCache.createFileChoosers();

        try (
            FileInputStream inputFile = new FileInputStream(
                this.pluginContext.getUserDir()
                    .resolve(Constants.DATA_FILE).toFile()
            ); DataInputStream inputStream = new DataInputStream(inputFile)
        ) {
            getFrame().load(inputStream);
        } catch (IOException e) {
            getFrame().create();
        }
        /*
         * Refresh component tree to update styles
         */
        update();
        /*
         * Load plugins for Workspace Desktop UI
         */
        new PluginsLoaderComponent().loadPlugins();
        /*
         * Hide logo screen
         */
        getLogoScreen().setVisible(false);
    }

    /**
     * Reset workspace gui to its initial state
     */
    public synchronized void reset() {
        /*
         * Reset the main frame
         */
        getFrame().reset();
        /*
         * Assign reference to null to create another instance in the next line
         */
        frame = null;
        /*
         * Set title
         */
        getFrame().setTitle(Workspace.VERSION);
        /*
         * Remove all choosers
         */
        ClassCache.resetFileChoosers();
    }

    /**
     * Get human readable name for this UI component
     */
    public String getName() {
        return "Clematis Desktop";
    }

    /**
     * Saves profile data of Workspace GUI on disk.
     */
    public void save() {
        /*
         * Save the workspace configuration
         */
        DesktopServiceLocator.getInstance().getUiConfig().save();
        /*
         * Dispose opened frames
         */
        for (Frame displayedFrame : displayedFrames) {
            displayedFrame.dispose();
        }
        /*
         * Clear the array of opened frames
         */
        displayedFrames.clear();

        try (FileOutputStream outputFile = new FileOutputStream(ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(Constants.DATA_FILE).toFile()
        ); DataOutputStream outputStream = new DataOutputStream(outputFile)) {

            getFrame().save(outputStream);
        } catch (IOException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceGUI.saveFrame.failed"), e);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            log.warning(ex.getMessage());
        }
    }

    /**
     * Update GUI
     */
    public void update() {
        getFrame().update();
    }

    /**
     * Show error to user either way it capable of
     *
     * @param usermsg message
     * @param ex      exception
     */
    @Override
    public void showError(String usermsg, Throwable ex) {}

    /**
     * Show message to user either way it capable of
     *
     * @param usermsg message
     */
    @Override
    public void showMessage(String usermsg) {}

    public void setKiwiTextureVisible(boolean visible) {
        DesktopServiceLocator.getInstance().getUiConfig().setKiwiTextureVisible(visible);
    }

    /**
     * @author Anton Troshin
     */
    class FrameCloseListener extends WindowAdapter {

        Frame workspaceFrame;

        FrameCloseListener(Frame frame) {
            super();
            this.workspaceFrame = frame;
        }

        public void windowClosing(WindowEvent we) {
            if (workspaceFrame != null && we.getWindow().equals(workspaceFrame)) {
                log.info("Removed frame " + workspaceFrame.getTitle());
                displayedFrames.remove(workspaceFrame);
                workspaceFrame.dispose();
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    public class WorkspaceViewListener implements IWorkspaceListener {

        public static final int CODE = Constants.WORKSPACE_VIEW_LISTENER_CODE;

        @Override
        public int getCode() {
            return CODE;
        }

        public void processEvent(Integer event, Object lparam, Object rparam) {

            if (lparam instanceof Map<?, ?> map
                && map.get(VIEW_PARAMETER) instanceof IView view
                && map.get(DISPLAY_PARAMETER) instanceof Boolean display
                && map.get(REGISTER_PARAMETER) instanceof Boolean register) {

                getFrame().getContentManager().addView(view, display, register);
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    class WorkspaceWindowListener implements IWorkspaceListener {

        static final int CODE = 1001;

        @Override
        public int getCode() {
            return CODE;
        }

        public void processEvent(Integer event, Object lparam, Object rparam) {

            if ((lparam instanceof Map<?, ?> map)
                && map.get(VIEW_PARAMETER) instanceof JComponent view
                && map.get(DISPLAY_PARAMETER) instanceof Boolean display
                && map.get(REGISTER_PARAMETER) instanceof Boolean register) {

                IView currentView = getFrame().getContentManager().getCurrentView();

                if (currentView instanceof Desktop desktop) {
                    desktop.addView(view, display, register);
                } else {
                    ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().
                        getImage("desktop/desktop_big.png"));
                    JOptionPane.showMessageDialog(getFrame(),
                        WorkspaceResourceAnchor.getString("WorkspaceGUI.intWnd.onlyOnDesktop"),
                        WorkspaceResourceAnchor.getString("WorkspaceGUI.intWnd.onlyOnDesktop.title"),
                        JOptionPane.INFORMATION_MESSAGE, icon);
                }
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    public class SwitchMenuListener implements IWorkspaceListener {

        public static final int CODE = 1002;

        @Override
        public int getCode() {
            return CODE;
        }

        public void processEvent(Integer event, Object lparam, Object rparam) {

            if (lparam instanceof Map<?, ?> map
                && map.get(Constants.MENUS_PARAMETER) instanceof List<?> menus
                && map.get(Constants.FLAG_PARAMETER) instanceof Boolean flag
            ) {

                JMenuBar menuBar = getFrame().getJMenuBar();

                for (Object object : menus) {
                    if (!(object instanceof JMenu menu)) {
                        continue;
                    }
                    if (flag) {
                        menuBar.add(menu);
                    } else {
                        menuBar.remove(menu);
                    }
                }

                menuBar.revalidate();
                menuBar.repaint();
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    public class ExternalFrameListener implements IWorkspaceListener {

        public static final int CODE = 1003;

        @Override
        public int getCode() {
            return CODE;
        }

        public void processEvent(Integer event, Object lparam, Object rparam) {

            if (lparam instanceof Map<?, ?> map
                && map.get(Constants.FRAME_PARAMETER) instanceof Frame frameParameter
            ) {

                displayedFrames.add(frameParameter);
                frameParameter.addWindowListener(new FrameCloseListener(frameParameter));
                frameParameter.setVisible(true);
            }
        }
    }

    private static final class ResourceManagerHolder {
        private static final ResourceManager RESOURCE_MANAGER = new ResourceManager(WorkspaceGUI.class);
    }
}
