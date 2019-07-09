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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import com.hyperrealm.kiwi.io.ConfigFile;
import com.hyperrealm.kiwi.ui.SplashScreen;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.Task;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import jworkspace.LangResource;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.UI;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceException;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.plaf.PlafFactory;
import jworkspace.ui.views.DefaultCompoundView;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workspace Desktop user interface
 *
 * @author Anton Troshin
 */
public class WorkspaceGUI implements UI {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);
    /**
     * GUI texture and laf.
     */
    private static final String CK_TEXTURE = "gui.texture",
        CK_LAF = "gui.laf",
        CK_THEME = "gui.theme",
        CK_KIWI = "gui.kiwi.texture.visible",
        CK_UNDECORATED = "gui.frame.undecorated";
    /**
     * Default look and feel
     */
    private static final String DEFAULT_LAF = "system";

    private static final String USER_DIR_PROPERTY = "user.dir";

    private static final String LIB_PATH = "lib";

    private static final String RES_PATH = "res";

    private static final String TEXTURES_JAR = "textures.jar";

    private static final String TEXTURE_FILE_NAME = "texture.jpg";

    private static final String DESKTOP_JAR = "desktop.jar";

    private static final String CONFIG_FILE = "jwxwin.cfg";

    private static final String DATA_FILE = "jwxwin.dat";

    private static final String PROMPT = ">";

    private static final String LOG_FINISH = "...";

    private static final String LOADING_LAF_MESSAGE = "Loading laf";

    private static final String READING_FILE = "Reading file";

    private static final String WRITING_FILE = "Writing file";

    private static final String SAVING = "Saving";
    /**
     * Workspace main frame
     */
    private static MainFrame frame = null;
    /**
     * Workspace UI logo.
     */
    private static SplashScreen logo = null;
    /**
     * Workspace background texture
     */
    private BufferedImage texture = null;
    /**
     * Components in content panel are cached along with they names as keys. Use RegisterComponent
     * UnregisterComponent and IsRegistered to manage custom views.
     */
    private HashMap<String, Object> components = new HashMap<>();
    /**
     * Plugin shells
     */
    private HashSet<Plugin> shells = new HashSet<>();
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
     * Returns clipboard for graphic interface.
     *
     * @return java.awt.datatransfer.Clipboard
     */
    @Override
    public Clipboard getClipboard() {
        return ((jworkspace.ui.MainFrame) getFrame()).getClipboard();
    }

    /**
     * Creates frame from the scratch with default
     * parameters.
     */
    @Override
    public Frame getFrame() {
        if (frame == null) {
            frame = new MainFrame(Workspace.getVersion(), this);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (e.getSource() == frame) {
                        try {
                            Workspace.exit();
                        } catch (WorkspaceException ex) {
                            WorkspaceError.exception(ex.getMessage(), ex);
                        }
                    }
                }
            });
        }
        return frame;
    }

    /**
     * Returns splash screen.
     */
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
     * Check whether argument component is registered.
     *
     * @return registered component.
     */
    @Override
    public Object isRegistered(String clazz) {
        return components.get(clazz);
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
        ImageIcon shellIcon = new ImageIcon(Workspace.getResourceManager().getImage("shell_big.png"));
        /*
         * Create new registry.
         */
        components = new HashMap<>();
        String fileName = Workspace.getUserHome() + CONFIG_FILE;
        WorkspaceGUI.LOG.info(PROMPT + READING_FILE + fileName + LOG_FINISH);
        boolean undecorated = false;
        try {
            config = new ConfigFile(new File(fileName), "GUI Definition");
            config.load();

            laf = config.getString(CK_LAF, DEFAULT_LAF);

            PlafFactory.getInstance().setCurrentTheme(laf, config.getString(CK_THEME, ""));
            isTextureVisible = config.getBoolean(CK_TEXTURE, false);
            isKiwiTextureVisible = config.getBoolean(CK_KIWI, true);
            undecorated = config.getBoolean(CK_UNDECORATED, false);

        } catch (FileNotFoundException ex) {

            laf = DEFAULT_LAF;
            isTextureVisible = false;
            isKiwiTextureVisible = false;

        } catch (Exception ex) {
            // silently ignore
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

            fileName = Workspace.getUserHome() + TEXTURE_FILE_NAME;
            /*
             * Read texture
             */
            texture = Imaging.getBufferedImage(new File(fileName));
            /*
             * Finally set texture on frame
             */
            ((MainFrame) getFrame()).setTexture(texture);

        } catch (ImageReadException | IOException e) {

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

        fileName = Workspace.getUserHome() + DATA_FILE;
        WorkspaceGUI.LOG.info(PROMPT + READING_FILE + fileName + LOG_FINISH);
        try {
            FileInputStream inputFile = new FileInputStream(fileName);
            DataInputStream inputStream = new DataInputStream(inputFile);
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
                LangResource.getString("WorkspaceGUI.shells.loading"), true);

            ShellsLoader shloader = new ShellsLoader();
            pr.track(shloader);
        });
    }

    /**
     * Returns all registered components.
     */
    public HashMap<String, Object> getAllRegistered() {
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
    public void reset() {
        ((MainFrame) getFrame()).reset();
        frame = null;
        components = new HashMap<>();
        shells = new HashSet<>();
        getFrame().setTitle(Workspace.getVersion());
        ClassCache.resetFileChoosers();
        System.gc();
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
        String fileName = Workspace.getUserHome() + CONFIG_FILE;
        WorkspaceGUI.LOG.info(PROMPT + WRITING_FILE + fileName + LOG_FINISH);

        File file = new File(Workspace.getUserHome());

        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
            }
        }

        file = new File(fileName);

        try {
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
            WorkspaceError.exception(LangResource.getString("WorkspaceGUI.save.failed"), ex);
        }
        /*
         * Write texture on disk
         */
        if (texture != null) {

            try {
                fileName = Workspace.getUserHome() + TEXTURE_FILE_NAME;
                OutputStream os = new FileOutputStream(fileName);
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
                    Imaging.writeImage(bi, os, ImageFormats.JPEG, null);
                }

            } catch (ImageWriteException | IOException e) {
                WorkspaceError.exception(LangResource.getString("WorkspaceGUI.saveTexture.failed"), e);
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

        fileName = Workspace.getUserHome() + DATA_FILE;
        WorkspaceGUI.LOG.info(PROMPT + WRITING_FILE + fileName + LOG_FINISH);

        try {
            FileOutputStream outputFile = new FileOutputStream(fileName);
            DataOutputStream outputStream = new DataOutputStream(outputFile);
            ((MainFrame) getFrame()).save(outputStream);
        } catch (IOException e) {
            WorkspaceError.exception(LangResource.getString("WorkspaceGUI.saveFrame.failed"), e);
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

    /**
     * Process event. Such event is send to every
     * subsribed event listener in synchronous manner.
     */
    public void processEvent(Object event, Object lparam, Object rparam) {
        /*
         * Activate unique GUI shell, i.e. bring the shell to the front
         */
        if (event instanceof Integer && (Integer) event == 1000) {
            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get("view") instanceof IView
                && ((Hashtable) lparam).get("display") instanceof Boolean
                && ((Hashtable) lparam).get("register") instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;
                IView view = (IView) lhparam.get("view");
                Boolean display = (Boolean) lhparam.get("display");
                Boolean register = (Boolean) lhparam.get("register");

                ((MainFrame) getFrame()).getContentManager().addView(view, display, register);
            }
        } else if (event instanceof Integer && (Integer) event == 1001) {
            /*
             * Adds new internal frame (JInternalFrame) on currently opened desktop
             */
            new AddWorkspaceWindowListener().processEvent(event, lparam, rparam);
        } else if (event instanceof Integer && (Integer) event == 1002) {
            /*
             * Switch menu of the view, then it's becomes inactive or otherwise activated.
             */
            new SwitchMenuListener().processEvent(event, lparam, rparam);
        } else if (event instanceof Integer && (Integer) event == 1003) {
            /*
             * Show external frame and put frame into array of displayed frames
             */
            new AddExternalFrameListener().processEvent(event, lparam, rparam);
        }
    }

    /*
     * Shells loader. Each shell is actually a
     * Kiwi library plugin. This class uses work
     * thread to load plugins
     */
    class ShellsLoader extends Task {

        ShellsLoader() {
            super();
            addProgressObserver(pr);
        }

        public void run() {
            String fileName = Workspace.getUserHome() + "shells";
            Plugin[] shells = Workspace.getRuntimeManager().loadPlugins(fileName);
            if (shells == null || shells.length == 0) {
                pr.setMessage(LangResource.getString("WorkspaceGUI.shells.notFound"));
                pr.setProgress(100);
                return;
            } else {
                for (int i = 0; i < shells.length; i++) {
                    pr.setMessage(LangResource.getString("WorkspaceGUI.shell.loading")
                        + " " + shells[i].getName());
                    WorkspaceGUI.LOG.info(PROMPT + "Loading " + shells[i].getName() + LOG_FINISH);
                    if (shells[i].getIcon() != null) {
                        pr.setIcon(shells[i].getIcon());
                    }
                    try {
                        shells[i].newInstance();
                        installShell(shells[i]);
                        WorkspaceGUI.LOG.info(PROMPT + "Installed " + shells[i].getName() + LOG_FINISH);
                    } catch (Exception | Error ex) {
                        WorkspaceGUI.LOG.warn(PROMPT + "GUI Shell " + shells[i].getName()
                            + " failed to load " + ex.toString());
                    }
                    pr.setProgress(i * 100 / shells.length);
                }
                pr.setProgress(100);
            }
            update();
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

    class AddWorkspaceWindowListener implements IWorkspaceListener {

        public void processEvent(Object event, Object lparam, Object rparam) {

            if ((lparam instanceof Hashtable)
                && ((Hashtable) lparam).get("view") instanceof JComponent
                && ((Hashtable) lparam).get("display") instanceof Boolean
                && ((Hashtable) lparam).get("register") instanceof Boolean) {

                Hashtable lhparam = (Hashtable) lparam;
                JComponent view = (JComponent) lhparam.get("view");
                Boolean display = (Boolean) lhparam.get("display");
                Boolean register = (Boolean) lhparam.get("register");

                IView currentView = ((MainFrame) getFrame()).getContentManager().getCurrentView();
                if (currentView instanceof Desktop) {
                    ((Desktop) currentView).addView(view, display, register);
                } else {
                    ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                        getImage("desktop/desktop_big.png"));
                    JOptionPane.showMessageDialog(getFrame(),
                        LangResource.getString("WorkspaceGUI.intWnd.onlyOnDesktop"),
                        LangResource.getString("WorkspaceGUI.intWnd.onlyOnDesktop.title"),
                        JOptionPane.INFORMATION_MESSAGE, icon);
                }
            }
        }
    }

    class SwitchMenuListener implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {

            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get("menus") instanceof Vector
                && ((Hashtable) lparam).get("flag") instanceof Boolean) {
                Hashtable lhparam = (Hashtable) lparam;
                Vector menus = (Vector) lhparam.get("menus");
                /*
                 * Flag - true - add, false - remove.
                 */
                Boolean flag = (Boolean) lhparam.get("flag");

                if (flag) {
                    for (int i = 0; i < menus.size(); i++) {
                        if (menus.elementAt(i) instanceof JMenu) {
                            ((MainFrame) getFrame()).getJMenuBar().add((JMenu) menus.elementAt(i));
                        }
                    }
                } else {
                    for (int i = 0; i < menus.size(); i++) {
                        if (menus.elementAt(i) instanceof JMenu) {
                            ((MainFrame) getFrame()).getJMenuBar().remove((JMenu) menus.elementAt(i));
                        }
                    }
                }
                ((MainFrame) getFrame()).getJMenuBar().revalidate();
                ((MainFrame) getFrame()).getJMenuBar().repaint();
            }
        }
    }

    class AddExternalFrameListener implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {
            if (lparam instanceof Hashtable
                && ((Hashtable) lparam).get("frame") instanceof Frame) {

                    Frame frame = (Frame) ((Hashtable) lparam).get("frame");
                    if (frame != null) {
                        displayedFrames.add(frame);
                        frame.addWindowListener(new FrameCloseListener(frame));
                        frame.setVisible(true);
                    }
                }
            }
        }
    }
}