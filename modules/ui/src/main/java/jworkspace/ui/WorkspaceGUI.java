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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.ui.SplashScreen;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.Task;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IConstants;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.UI;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspacePluginLocator;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IShell;
import jworkspace.ui.api.IView;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.config.UIConfig;
import jworkspace.ui.config.plaf.PlafFactory;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.views.DefaultCompoundView;
import jworkspace.ui.widgets.WorkspaceError;

/**
 * Workspace Desktop user interface
 *
 * @author Anton Troshin
 */
public class WorkspaceGUI implements UI {

    /**
     *
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceGUI.class);
    /**
     * Resource manager
     */
    private static WorkspaceResourceManager resourceManager = null;
    /**
     * Workspace UI logo.
     */
    private static SplashScreen logo = null;
    /**
     * Instance of workspace configuration
     */
    private final UIConfig uiConfig;
    /**
     * Workspace main frame
     */
    private MainFrame frame = null;
    /**
     * Plugin shells
     */
    private Set<IShell> shells = new HashSet<>();
    /**
     * GUI actions
     */
    private UIActions actions = null;
    /**
     * A list of displayed frames
     */
    private ArrayList<Frame> displayedFrames = new ArrayList<>();
    /**
     * Progress dialog for observing shells load.
     */
    private ProgressDialog pr = null;

    /**
     * Default constructor.
     */
    public WorkspaceGUI() {
        super();

        UIChangeManager.getInstance().setDefaultFrameIcon(
            new WorkspaceResourceManager(WorkspaceGUI.class).getImage("jw_16x16.png"));

        registerListeners();

        uiConfig
            = new UIConfig(Workspace.getUserHomePath().resolve(Constants.CONFIG_FILE).toFile());
    }

    /**
     * Get workspace GUI actions
     */
    UIActions getActions() {
        if (actions == null) {
            actions = new UIActions(this);
        }
        return actions;
    }

    /**
     * Returns resource manager for the GUI.
     *
     * @return kiwi.util.WorkspaceResourceManager
     */
    public static synchronized WorkspaceResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new WorkspaceResourceManager(WorkspaceGUI.class);
        }
        return resourceManager;
    }

    /**
     * Register workspace listeners
     */
    private void registerListeners() {
        Workspace.addListener(new ExternalFrameListener());
        Workspace.addListener(new SwitchMenuListener());
        Workspace.addListener(new WorkspaceViewListener());
        Workspace.addListener(new WorkspaceWindowListener());
    }

    /**
     * Returns clipboard for graphic interface.
     *
     * @return java.awt.datatransfer.Clipboard
     */
    @Override
    public Clipboard getClipboard() {
        return ((jworkspace.ui.MainFrame) getFrame()).getClipboard();
    }

    @Override
    public Frame getFrame() {
        if (frame == null) {
            frame = new MainFrame(IConstants.VERSION, this);
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
            Image im = getResourceManager().getImage("logo/Logo.gif");
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
        return uiConfig.getTexture();
    }

    /**
     * Set texture for java workspace gui
     */
    public void setTexture(BufferedImage texture) {
        uiConfig.setTexture(texture);
    }

    /**
     * Get default path to workspace textures
     *
     * @return path to textures jar library
     */
    public static Path getTexturesPath() {

        return Workspace.getBasePath()
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
        return Workspace.getBasePath()
            .resolve(Constants.LIB_PATH)
            .resolve(Constants.RES_PATH)
            .resolve(Constants.DESKTOP_JAR);
    }

    /**
     * Is texture visible
     */
    public boolean isTextureVisible() {
        return uiConfig.isTextureVisible();
    }

    /**
     * Set texture visibility and validates main frame
     */
    public void setTextureVisible(boolean isTextureVisible) {

        uiConfig.setTextureVisible(isTextureVisible);

        if (isTextureVisible && uiConfig.getTexture() != null) {

            UIChangeManager.getInstance().setDefaultTexture(uiConfig.getTexture());
            ((MainFrame) getFrame()).setTexture(uiConfig.getTexture());

        } else {

            UIChangeManager.getInstance().setDefaultTexture(null);
            ((MainFrame) getFrame()).setTexture(null);

        }
    }

    /**
     * Check whether this GUI is modified.
     * As this gui is a frame, ask it.
     */
    @Override
    public boolean isModified() {
        return getFrame() != null && ((MainFrame) getFrame()).isModified();
    }

    /**
     * Is Kiwi texture visible?
     */
    public boolean isKiwiTextureVisible() {
        return uiConfig.isKiwiTextureVisible();
    }

    /**
     * Install shell into content manager
     */
    private synchronized void installShell(Plugin plugin) throws PluginException {

        Object obj = plugin.newInstance();
        if (obj instanceof IShell) {
            IShell shell = (IShell) obj;
            try {
                shell.load();
            } catch (IOException ex) {
                LOG.warn("> System error: Shell cannot be loaded:" + ex.toString());
            }
            /*
             * Add view to the list of shells
             */
            shells.add(shell);
            /*
             * Ask for buttons and fill control panel.
             */
            CButton[] buttons = shell.getButtons();
            if (buttons != null && buttons.length > 0) {
                for (CButton button : buttons) {
                    ((MainFrame) getFrame()).getControlPanel().addButton(button);
                }
                ((MainFrame) getFrame()).getControlPanel().addSeparator();
            }
            if (shell instanceof DefaultCompoundView) {
                ((DefaultCompoundView) shell).setButtonsLoaded(true);
                UIManager.addPropertyChangeListener(new UISwitchListener((DefaultCompoundView) shell));
            }
        }
    }

    /**
     * Loads workspace gui profile data.
     */
    public void load() {

        getLogoScreen().setVisible(true);

        /*
         * Workspace configuration
         */
        uiConfig.load();

        /*
         * Set undecorated property
         */
        boolean undecorated = uiConfig.isDecorated();

        getFrame().setUndecorated(undecorated);
        if (undecorated) {
            getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }

        /*
         * Set texture on
         */
        setTextureVisible(uiConfig.isTextureVisible());

        /*
         * Set LAF and styles
         */
        try {
            if (uiConfig.getLaf() != null && !uiConfig.getLaf().equals("")) {

                if (uiConfig.getLaf().equals(Constants.DEFAULT_LAF)
                    || !PlafFactory.getInstance().setLookAndFeel(uiConfig.getLaf())) {

                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } else {

                    PlafFactory.getInstance().setCurrentTheme(uiConfig.getLaf(), uiConfig.getTheme());
                }
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        ClassCache.createFileChoosers();

        try (FileInputStream inputFile = new FileInputStream(Workspace.ensureUserHomePath()
            .resolve(Constants.DATA_FILE).toFile());
             DataInputStream inputStream = new DataInputStream(inputFile)) {
            /*
             * Delegates loading of UI components to workspace frame
             */
            ((MainFrame) getFrame()).load(inputStream);
        } catch (IOException e) {
            ((MainFrame) getFrame()).create();
        }

        /*
         * Update the components tree to apply LAFs
         */
        update();

        /*
         * Load plugins
         */
        SwingUtilities.invokeLater(() -> {
            pr = new ProgressDialog(Workspace.getUi().getFrame(),
                WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.loading"), true);

            ShellsLoader loader = new ShellsLoader();
            pr.track(loader);
        });

        getLogoScreen().setVisible(false);
    }

    /**
     * Reset workspace gui to its initial state
     */
    public synchronized void reset() {
        /*
         * Reset the main frame
         */
        ((MainFrame) getFrame()).reset();
        frame = null;
        /*
         * Empty the list of UI plugins
         */
        shells.clear();
        /*
         * Set title
         */
        getFrame().setTitle(IConstants.VERSION);
        /*
         * Remove all choosers
         */
        ClassCache.resetFileChoosers();
    }

    /**
     * Get human readable name for installer
     */
    public String getName() {
        return "Clematis GUI Engine (R) v1.02";
    }

    /**
     * Saves profile data of Workspace GUI on disk.
     */
    public void save() {
        /*
         * Save the workspace configuration
         */
        uiConfig.save();
        /*
         * Save all other info
         */
        saveShells();
        /*
         * Save look and feel infos
         */
        PlafFactory.getInstance().save();
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

        try (FileOutputStream outputFile = new FileOutputStream(Workspace.ensureUserHomePath()
            .resolve(Constants.DATA_FILE).toFile());
             DataOutputStream outputStream = new DataOutputStream(outputFile)) {

            ((MainFrame) getFrame()).save(outputStream);
        } catch (IOException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceGUI.saveFrame.failed"), e);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    /**
     * Save all graphic UI plugins (shells)
     */
    private void saveShells() {
        for (IShell shell : shells) {
            try {
                shell.save();
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Update GUI
     */
    public void update() {
        ((MainFrame) getFrame()).update();
    }

    /**
     * Show error to user either way it capable of
     *
     * @param usermsg message
     * @param ex      exception
     */
    @Override
    public void showError(String usermsg, Throwable ex) {

    }

    /**
     * Show message to user either way it capable of
     *
     * @param usermsg message
     */
    @Override
    public void showMessage(String usermsg) {

    }

    public void setKiwiTextureVisible(boolean visible) {
        uiConfig.setKiwiTextureVisible(visible);
    }

    /*
     * Shells loader uses a working thread to load plugins
     *
     * @author Anton Troshin
     */
    class ShellsLoader extends Task {

        static final int PROGRESS_COMPLETED = 100;

        ShellsLoader() {
            super();
            addProgressObserver(pr);
        }

        public void run() {

            String fileName;
            try {
                fileName = Workspace.ensureUserHomePath().resolve("shells").toFile().getAbsolutePath();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return;
            }
            List<Plugin> plugins = new WorkspacePluginLocator().loadPlugins(Paths.get(fileName));

            if (plugins == null || plugins.size() == 0) {
                pr.setMessage(WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.notFound"));
                pr.setProgress(PROGRESS_COMPLETED);
            } else {

                for (int i = 0; i < plugins.size(); i++) {
                    pr.setMessage(WorkspaceResourceAnchor.getString("WorkspaceGUI.shell.loading")
                        + Constants.LOG_SPACE + plugins.get(i).getName());
                    LOG.info(Constants.PROMPT + "Loading " +  plugins.get(i).getName() + Constants.LOG_FINISH);
                    if (plugins.get(i).getIcon() != null) {
                        pr.setIcon(plugins.get(i).getIcon());
                    }
                    try {
                        installShell(plugins.get(i));
                        LOG.info(Constants.PROMPT + "Installed " +  plugins.get(i).getName() + Constants.LOG_FINISH);
                    } catch (Exception | Error ex) {
                        LOG.warn(Constants.PROMPT + "GUI Shell " +  plugins.get(i).getName()
                            + " failed to load " + ex.toString());
                    }
                    pr.setProgress(i * PROGRESS_COMPLETED / plugins.size());
                }
                pr.setProgress(PROGRESS_COMPLETED);
            }
        }
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
                LOG.info("Removed frame " + workspaceFrame.getTitle());
                displayedFrames.remove(workspaceFrame);
                workspaceFrame.dispose();
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    public class WorkspaceViewListener implements IWorkspaceListener {

        public static final int CODE = 1000;

        @Override
        public int getCode() {
            return CODE;
        }

        public void processEvent(Integer event, Object lparam, Object rparam) {

            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get(IWorkspaceListener.VIEW_PARAMETER) instanceof IView
                && ((Hashtable) lparam).get(IWorkspaceListener.DISPLAY_PARAMETER) instanceof Boolean
                && ((Hashtable) lparam).get(IWorkspaceListener.REGISTER_PARAMETER) instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;
                IView view = (IView) lhparam.get(IWorkspaceListener.VIEW_PARAMETER);
                Boolean display = (Boolean) lhparam.get(IWorkspaceListener.DISPLAY_PARAMETER);
                Boolean register = (Boolean) lhparam.get(IWorkspaceListener.REGISTER_PARAMETER);

                ((MainFrame) getFrame()).getContentManager().addView(view, display, register);
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

            if ((lparam instanceof Hashtable)
                && ((Hashtable) lparam).get(VIEW_PARAMETER) instanceof JComponent
                && ((Hashtable) lparam).get(DISPLAY_PARAMETER) instanceof Boolean
                && ((Hashtable) lparam).get(REGISTER_PARAMETER) instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;
                JComponent view = (JComponent) lhparam.get(VIEW_PARAMETER);
                Boolean display = (Boolean) lhparam.get(DISPLAY_PARAMETER);
                Boolean register = (Boolean) lhparam.get(REGISTER_PARAMETER);

                IView currentView = ((MainFrame) getFrame()).getContentManager().getCurrentView();
                if (currentView instanceof Desktop) {
                    ((Desktop) currentView).addView(view, display, register);
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

            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get(Constants.MENUS_PARAMETER) instanceof Vector
                && ((Hashtable) lparam).get(Constants.FLAG_PARAMETER) instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;

                Vector menus = (Vector) lhparam.get(Constants.MENUS_PARAMETER);
                Boolean flag = (Boolean) lhparam.get(Constants.FLAG_PARAMETER);

                JMenuBar menuBar = ((MainFrame) getFrame()).getJMenuBar();

                for (int i = 0; i < menus.size(); i++) {
                    if (!(menus.elementAt(i) instanceof JMenu)) {
                        continue;
                    }

                    if (flag) {
                        menuBar.add((JMenu) menus.elementAt(i));
                    } else {
                        menuBar.remove((JMenu) menus.elementAt(i));
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
            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get(Constants.FRAME_PARAMETER) instanceof Frame) {

                Frame frameParameter = (Frame) ((Hashtable) lparam).get(Constants.FRAME_PARAMETER);
                if (frameParameter != null) {
                    displayedFrames.add(frameParameter);
                    frameParameter.addWindowListener(new FrameCloseListener(frameParameter));
                    frameParameter.setVisible(true);
                }
            }
        }
    }
}
