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
import java.awt.Graphics;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.io.ConfigFile;
import com.hyperrealm.kiwi.ui.SplashScreen;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.Task;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IConstants;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.UI;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspacePluginLocator;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.plaf.PlafFactory;
import jworkspace.ui.views.DefaultCompoundView;

/**
 * Workspace Desktop user interface
 *
 * @author Anton Troshin
 */
public class WorkspaceGUI implements UI {

    public static final String MENUS_PARAMETER = "menus";

    public static final String FLAG_PARAMETER = "flag";

    public static final String LOG_FINISH = "...";

    public static final String LOG_SPACE = " ";

    public static final String PROMPT = ">";

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceGUI.class);

    private static final String CK_TEXTURE = "gui.texture",
        CK_LAF = "gui.laf",
        CK_THEME = "gui.theme",
        CK_KIWI = "gui.kiwi.texture.visible",
        CK_UNDECORATED = "gui.frame.undecorated";

    private static final String DEFAULT_LAF = "system";

    private static final String USER_DIR_PROPERTY = "user.dir";

    private static final String LIB_PATH = "lib";

    private static final String RES_PATH = "res";

    private static final String TEXTURES_JAR = "textures.jar";

    private static final String TEXTURE_FILE_NAME = "texture.jpg";

    private static final String DESKTOP_JAR = "desktop.jar";

    private static final String CONFIG_FILE = "jwxwin.cfg";

    private static final String DATA_FILE = "jwxwin.dat";

    private static final String LOADING_LAF_MESSAGE = "Loading laf";

    private static final String READING_FILE = "Reading file";

    private static final String WRITING_FILE = "Writing file";

    private static final String SAVING = "Saving";

    private static final String FRAME_PARAMETER = "frame";

    /**
     * Workspace UI logo.
     */
    private static SplashScreen logo = null;
    /**
     * Workspace main frame
     */
    private MainFrame frame = null;
    /**
     * Workspace background texture
     */
    private BufferedImage texture = null;
    /**
     * Components in content panel are cached along with they names as keys.
     * Use RegisterComponent, UnregisterComponent and IsRegistered to manage custom views.
     */
    private Map<String, Object> components = new HashMap<>();
    /**
     * Plugin shells
     */
    private Set<Plugin> shells = new HashSet<>();
    /**
     * Laf
     */
    private String laf = DEFAULT_LAF;
    /**
     * Is texture visible?
     */
    private boolean isTextureVisible = false;
    /**
     * Is KIWI texture visible?
     */
    private boolean isKiwiTextureVisible = false;
    /**
     * GUI actions
     */
    private UIActions actions = null;
    /**
     * A list of displayed frames
     */
    private ArrayList<Frame> displayedFrames = new ArrayList<>();
    /**
     * Config file
     */
    private ConfigFile config = null;
    /**
     * Progress dialog for observing shells load.
     */
    private ProgressDialog pr = null;

    /**
     * Default constructor.
     */
    public WorkspaceGUI() {
        super();
        UIChangeManager.setDefaultFrameIcon(new WorkspaceResourceManager().getImage("jw_16x16.png"));
        registerListeners();
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
     * Register workspace listeners
     */
    @Override
    public void registerListeners() {
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


    @Override
    public Window getLogoScreen() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (logo == null) {
            Image im = new ResourceLoader(WorkspaceResourceAnchor.class).getResourceAsImage("logo/Logo.gif");
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
        return texture;
    }

    /**
     * Set texture for java workspace gui
     */
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    /**
     * Get default path to workspace textures
     */
    public String getTexturesPath() {
        return System.getProperty(USER_DIR_PROPERTY) + File.separator
            + LIB_PATH + File.separator
            + RES_PATH + File.separator + TEXTURES_JAR;
    }

    /**
     * Get default path to desktop icons
     */
    public String getDesktopIconsPath() {
        return System.getProperty(USER_DIR_PROPERTY) + File.separator
            + LIB_PATH + File.separator
            + RES_PATH + File.separator + DESKTOP_JAR;
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
     * Is texture visible
     */
    public boolean isTextureVisible() {
        return isTextureVisible;
    }

    /**
     * Set texture visibility and revalidates main frame
     * for java workspace.
     */
    public void setTextureVisible(boolean isTextureVisible) {
        this.isTextureVisible = isTextureVisible;
        if (isTextureVisible && texture != null) {
            UIChangeManager.setDefaultTexture(texture);
            ((MainFrame) getFrame()).setTexture(texture);
        } else {
            UIChangeManager.setDefaultTexture(null);
            ((MainFrame) getFrame()).setTexture(null);
        }
    }

    /**
     * Is Kiwi texture visible?
     */
    public boolean isKiwiTextureVisible() {
        return isKiwiTextureVisible;
    }

    /**
     * Set KIWI texture visible and selectable from repository
     */
    public void setKiwiTextureVisible(boolean isKiwiTextureVisible) {
        this.isKiwiTextureVisible = isKiwiTextureVisible;
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
                WorkspaceGUI.LOG.warn("> System error: Shell cannot be loaded:" + ex.toString());
            }
            /*
             * Add view to the list of shells
             */
            shells.add(plugin);
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


        ClassCache.createFileChoosers();
        /*
         * Workspace plugin ICON
         */
        //ImageIcon shellIcon = new ImageIcon(Workspace.getResourceManager().getImage("shell_big.png"));
        /*
         * Create new registry.
         */
        boolean undecorated = false;
        components = new HashMap<>();
        String fileName;

        try {
            fileName = Workspace.getUserHomePath() + CONFIG_FILE;
            WorkspaceGUI.LOG.info(PROMPT + READING_FILE + fileName + LOG_FINISH);

            config = new ConfigFile(new File(fileName), "GUI Definition");
            config.load();

            laf = config.getString(CK_LAF, DEFAULT_LAF);

            PlafFactory.getInstance().setCurrentTheme(laf, config.getString(CK_THEME, ""));
            isTextureVisible = config.getBoolean(CK_TEXTURE, false);
            isKiwiTextureVisible = config.getBoolean(CK_KIWI, true);
            undecorated = config.getBoolean(CK_UNDECORATED, false);

        } catch (Exception ex) {
            laf = DEFAULT_LAF;
            isTextureVisible = false;
            isKiwiTextureVisible = false;
            LOG.warn("Error loading GUI", ex);
        }
        /*
         * Set undecorated
         */
        getFrame().setUndecorated(undecorated);
        if (undecorated) {
            getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        /*
         * Load recently used texture
         */
        try {

            fileName = Workspace.getUserHomePath() + TEXTURE_FILE_NAME;
            /*
             * Read texture
             */
            texture = ImageIO.read(new File(fileName));
            /*
             * Finally set texture on frame
             */
            ((MainFrame) getFrame()).setTexture(texture);

        } catch (IOException e) {

            WorkspaceGUI.LOG.warn("Cannot set texture", e);

        } finally {
            /*
             * Read texture show or hide flag
             */
            setTextureVisible(isTextureVisible);
        }

        // SET LAF

        try {
            if (laf != null && !laf.equals("")) {
                WorkspaceGUI.LOG.info(PROMPT + LOADING_LAF_MESSAGE + laf + LOG_FINISH);
                if (laf.equals(DEFAULT_LAF) || !PlafFactory.getInstance().setLookAndFeel(laf)) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
        } catch (Exception ex) {
            WorkspaceGUI.LOG.warn(ex.getMessage(), ex);
        }

        // LOAD PROFILE DATA
        try (FileInputStream inputFile = new FileInputStream(Workspace.getUserHomePath() + DATA_FILE);
            DataInputStream inputStream = new DataInputStream(inputFile)) {

            WorkspaceGUI.LOG.info(PROMPT + READING_FILE + Workspace.getUserHomePath() + DATA_FILE + LOG_FINISH);
            /*
             * Delegates loading of UI components to workspace frame
             */
            ((MainFrame) getFrame()).load(inputStream);
        } catch (IOException e) {
            ((MainFrame) getFrame()).create();
        }

        update();

        WorkspaceGUI.LOG.info(PROMPT + "GUI configuration is successfully read");
        /*
         * Load plugins
         */
        SwingUtilities.invokeLater(() -> {
            pr = new ProgressDialog(Workspace.getUi().getFrame(),
                WorkspaceResourceAnchor.getString("WorkspaceGUI.shells.loading"), true);

            ShellsLoader shloader = new ShellsLoader();
            pr.track(shloader);
        });
    }

    /**
     * Returns all registered components.
     */
    public Map<String, Object> getAllRegistered() {
        return components;
    }

    /**
     * Check whether argument component is registered.
     */
    public void register(Object obj) {
        if (obj != null) {
            components.put(obj.getClass().getName(), obj);
        }
    }

    /**
     * Reset workspace gui to its initial state
     */
    public synchronized void reset() {
        ((MainFrame) getFrame()).reset();
        frame = null;
        components = new HashMap<>();
        shells = new HashSet<>();
        getFrame().setTitle(IConstants.VERSION);
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

        try {
            String fileName = Workspace.getUserHomePath() + CONFIG_FILE;
            WorkspaceGUI.LOG.info(PROMPT + WRITING_FILE + fileName + LOG_FINISH);
            File file = new File(fileName);

            if (config == null) {
                config = new ConfigFile(file, "GUI Configuration");
            }
            config.putString(CK_LAF, UIManager.getLookAndFeel().getClass().getName());
            MetalTheme theme = PlafFactory.getInstance().getCurrentTheme();
            if (theme != null) {
                config.putString(CK_THEME, theme.getClass().getName());
            }
            config.putBoolean(CK_TEXTURE, isTextureVisible);
            config.putBoolean(CK_KIWI, isKiwiTextureVisible);
            config.putBoolean(CK_UNDECORATED, getFrame().isUndecorated());
            config.store();
        } catch (IOException ex) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceGUI.save.failed"), ex);
        }
        /*
         * Write texture on disk
         */
        if (texture != null) {

            try (OutputStream os = new FileOutputStream(Workspace.getUserHomePath() + TEXTURE_FILE_NAME)) {

                ImageIcon textureIcon = new ImageIcon(texture);
                BufferedImage bi = new BufferedImage(
                    textureIcon.getIconWidth(),
                    textureIcon.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.createGraphics();
                // paint the Icon to the BufferedImage.
                textureIcon.paintIcon(null, g, 0, 0);
                g.dispose();

                if (textureIcon.getIconHeight() > 0 && textureIcon.getIconWidth() > 0) {
                    ImageIO.write(bi, "JPEG", os);
                }

            } catch (IOException e) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceGUI.saveTexture.failed"), e);
            }
        }
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
         * Recreate array of opened frames
         */
        displayedFrames = new ArrayList<>();

        try (FileOutputStream outputFile = new FileOutputStream(Workspace.getUserHomePath() + DATA_FILE);
             DataOutputStream outputStream = new DataOutputStream(outputFile)) {

            WorkspaceGUI.LOG.info(PROMPT + WRITING_FILE + Workspace.getUserHomePath() + DATA_FILE + LOG_FINISH);

            ((MainFrame) getFrame()).save(outputStream);
        } catch (IOException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceGUI.saveFrame.failed"), e);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            WorkspaceGUI.LOG.warn(ex.getMessage(), ex);
        }
    }

    /**
     * Save all graphic UI plugins (shells)
     */
    private void saveShells() {

//        for (Plugin shell : shells) {
//            WorkspaceGUI.LOG.info(PROMPT + SAVING + shell.getName() + LOG_FINISH);
//            try {
//                shell.unload();
//            } catch (PluginException ex) {
//                WorkspaceError.exception(LangResource.getString("WorkspaceGUI.plugin.saveFailed"), ex);
//            }
//        }
    }

    /**
     * Check whether argument component is registered.
     */
    public void unregister(String clazz) {
        components.remove(clazz);
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
     * @param question message
     * @param title    of confirmation dialog
     * @param icon     of confirmation dialog
     */
    @Override
    public boolean showConfirmDialog(String question, String title, Icon icon) {
        return false;
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

    /*
     * Shells loader. Each shell is actually a Kiwi library plugin. This class uses work
     * thread to load plugins
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
                fileName = Workspace.getUserHomePath() + "shells";
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
                        + LOG_SPACE + plugins.get(i).getName());
                    WorkspaceGUI.LOG.info(PROMPT + "Loading " +  plugins.get(i).getName() + LOG_FINISH);
                    if (plugins.get(i).getIcon() != null) {
                        pr.setIcon(plugins.get(i).getIcon());
                    }
                    try {
                        installShell(plugins.get(i));
                        WorkspaceGUI.LOG.info(PROMPT + "Installed " +  plugins.get(i).getName() + LOG_FINISH);
                    } catch (Exception | Error ex) {
                        WorkspaceGUI.LOG.warn(PROMPT + "GUI Shell " +  plugins.get(i).getName()
                            + " failed to load " + ex.toString());
                    }
                    pr.setProgress(i * PROGRESS_COMPLETED / plugins.size());
                }
                pr.setProgress(PROGRESS_COMPLETED);
                update();
            }
        }
    }

    /**
     * Frame listener
     */
    class FrameCloseListener extends WindowAdapter {

        Frame workspaceFrame;

        FrameCloseListener(Frame frame) {
            super();
            this.workspaceFrame = frame;
        }

        public void windowClosing(WindowEvent we) {
            if (workspaceFrame != null && we.getWindow().equals(workspaceFrame)) {
                WorkspaceGUI.LOG.info("Removed frame " + workspaceFrame.getTitle());
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

    class WorkspaceWindowListener implements IWorkspaceListener {

        public static final int CODE = 1001;

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
                    ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
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
                && ((Hashtable) lparam).get(MENUS_PARAMETER) instanceof Vector
                && ((Hashtable) lparam).get(FLAG_PARAMETER) instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;

                Vector menus = (Vector) lhparam.get(MENUS_PARAMETER);
                Boolean flag = (Boolean) lhparam.get(FLAG_PARAMETER);

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
                && ((Hashtable) lparam).get(FRAME_PARAMETER) instanceof Frame) {

                Frame frameParameter = (Frame) ((Hashtable) lparam).get(FRAME_PARAMETER);
                if (frameParameter != null) {
                    displayedFrames.add(frameParameter);
                    frameParameter.addWindowListener(new FrameCloseListener(frameParameter));
                    frameParameter.setVisible(true);
                }
            }
        }
    }
}
