package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import com.hyperrealm.kiwi.io.ConfigFile;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.Task;
import com.hyperrealm.kiwi.util.plugin.PluginException;

import jworkspace.LangResource;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.IWorkspaceListener;
import jworkspace.kernel.ResourceManager;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.engines.GUI;
import jworkspace.ui.action.ActionChangedListener;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.plaf.PlafFactory;
import jworkspace.ui.views.DefaultCompoundView;
import jworkspace.util.WorkspaceError;

import kiwi.ui.SplashScreen;
import kiwi.ui.dialog.ProgressDialog;
import kiwi.util.plugin.Plugin;

import org.apache.commons.imaging.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * GUI engine is one of required by kernel.
 * This class implements interface
 * <code>jworkspace.kernel.engines.GUI</code>.
 */
public class WorkspaceGUI implements GUI {
    /**
     * GUI actions
     */
    private UIActions actions = null;
    /**
     * Workspace main frame
     */
    private static WorkspaceFrame frame = null;
    /**
     * A list of displayed frames
     */
    private ArrayList displayedFrames = new ArrayList();
    /**
     * Workspace background texture
     */
    protected BufferedImage texture = null;
    /**
     * Workspace UI logo.
     */
    private static SplashScreen logo = null;
    /**
     * Workspace plugin icon
     */
    private ImageIcon shell_icon = null;
    /**
     * Components in content panel are cached
     * along with they names as keys. Use RegisterComponent
     * UnregisterComponent and IsRegistered to
     * manage custom views.
     */
    protected Hashtable components = new Hashtable();
    /**
     * Plugin shells
     */
    protected HashSet shells = new HashSet();
    /**
     * GUI texture and laf.
     */
    public static final String CK_TEXTURE = "gui.texture", CK_LAF = "gui.laf",
            CK_THEME = "gui.theme",
            CK_KIWI = "gui.kiwi.texture.visible",
            CK_UNDECORATED = "gui.frame.undecorated";
    /**
     * Laf
     */
    protected String laf = "system";
    /**
     * Is texture visible?
     */
    protected boolean isTextureVisible = false;
    /**
     * Is KIWI texture visible?
     */
    protected boolean isKiwiTextureVisible = false;
    /**
     * Config file
     */
    private ConfigFile config = null;
    /**
     * Progress dialog for observing shells load.
     */
    private ProgressDialog pr = null;

    /**
     * Shells loader. Each shell is actually a
     * Kiwi library plugin. This class uses work
     * thread to load plugins
     */
    class ShellsLoader extends Task {
        public ShellsLoader() {
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
                    Workspace.getLogger().info(">" + "Loading " + shells[i].getName() + "...");
                    if (shells[i].getBigIcon() != null) {
                        pr.setIcon(shells[i].getBigIcon());
                    } else {
                        pr.setIcon(shell_icon);
                    }
                    try {
                        shells[i].load();
                        installShell(shells[i]);
                        Workspace.getLogger().info(">" + "Installed " + shells[i].getName() + "...");
                    } catch (Exception | Error ex) {
                        Workspace.getLogger().warning(">" + "GUI Shell " + shells[i].getName()
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
     * Frame close listener
     */
    class FrameCloseListener extends WindowAdapter {
        Frame _frame = null;

        FrameCloseListener(Frame frame) {
            super();
            this._frame = frame;
        }

        public void windowClosing(WindowEvent we) {
            if (_frame != null && we.getWindow().equals(_frame)) {
                Workspace.getLogger().fine("Removed frame " + _frame.getTitle());
                displayedFrames.remove(_frame);
                _frame.dispose();
            }
        }
    }

    /**
     * Default constructor.
     */
    public WorkspaceGUI() {
        super();
        UIChangeManager.setDefaultFrameIcon(new ResourceManager().getImage("jw_16x16.png"));
    }

    public PropertyChangeListener createActionChangeListener(JMenuItem b) {
        return new ActionChangedListener(b);
    }

    public PropertyChangeListener createActionChangeListener(AbstractButton b) {
        return new ActionChangedListener(b);
    }

    /**
     * Get workspace GUI actions
     */
    public UIActions getActions() {
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
    public Clipboard getClipboard() {
        return ((WorkspaceFrame) getFrame()).getClipboard();
    }

    /**
     * Creates frame from the scratch with default
     * parameters.
     */
    public java.awt.Frame getFrame() {
        if (frame == null) {
            frame = new WorkspaceFrame(Workspace.getVersion(), this);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    if (e.getSource() == frame) {
                        Workspace.exit();
                    }
                }
            });
        }
        return frame;
    }

    /**
     * Returns splash screen.
     */
    public Window getLogoScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (logo == null) {
            Image im = new ResourceLoader(WorkspaceResourceAnchor.class)
                    .getResourceAsImage("logo/Logo.gif");
            logo = new SplashScreen(new Frame(), im, null);
            ImageIcon imc = new ImageIcon(im);
            Rectangle logoBounds = new Rectangle((screenSize.width -
                    imc.getIconWidth()) / 2,
                    (screenSize.height - imc.getIconHeight()) / 2, imc.getIconWidth(),
                    imc.getIconHeight());
            logo.setBounds(logoBounds);
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
     * Get default path to workspace textures
     */
    public String getTexturesPath() {
        return System.getProperty("user.dir") + File.separator
                + "lib" + File.separator +
                "res" + File.separator + "textures.jar";
    }

    /**
     * Get default path to desktop icons
     */
    public String getDesktopIconsPath() {
        return System.getProperty("user.dir") + File.separator
                + "lib" + File.separator +
                "res" + File.separator + "desktop.jar";
    }

    /**
     * Check whether this GUI is modified.
     * As this gui is a frame, ask it.
     */
    public boolean isModified() {
        if (((WorkspaceFrame) getFrame()) == null)
            return false;
        else
            return ((WorkspaceFrame) getFrame()).isModified();
    }

    /**
     * Check whether argument component is registered.
     *
     * @return registered component.
     */
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
     * Is Kiwi texture visible?
     */
    public boolean isKiwiTextureVisible() {
        return isKiwiTextureVisible;
    }

    /**
     * Install shell into content manager
     */
    protected synchronized void installShell(Plugin plugin) {
        Object obj = plugin.getPluginObject();
        if (obj instanceof IShell) {
            IShell shell = (IShell) obj;
            try {
                shell.load();
            } catch (IOException ex) {
                Workspace.getLogger().warning(">"
                        + "System error: Shell cannot be loaded:" + ex.toString());
            }
            /**
             * Add view to the list of shells
             */
            shells.add(plugin);
            /**
             * Ask for buttons and fill control panel.
             */
            CButton[] buttons = shell.getButtons();
            if (buttons != null) {
                for (int i = 0; i < buttons.length; i++) {
                    ((WorkspaceFrame) getFrame()).getControlPanel().addButton(buttons[i]);
                }
                if (buttons.length > 0) {
                    ((WorkspaceFrame) getFrame()).getControlPanel().addSeparator();
                }
            }
            if (shell instanceof DefaultCompoundView) {
                ((DefaultCompoundView) shell).setButtonsLoaded(true);
                UIManager.addPropertyChangeListener
                        (new UISwitchListener((DefaultCompoundView) shell));
            }
        }
    }

    /**
     * Loads workspace gui profile data.
     */
    public void load() {
        WorkspaceClassCache.createFileChoosers();
        shell_icon = new ImageIcon(Workspace.getResourceManager().
                getImage("shell_big.png"));
        /**
         * Create new registry.
         */
        components = new Hashtable();
        String fileName = Workspace.getUserHome() + "jwxwin.cfg";
        Workspace.getLogger().info(">" + "Reading file" + " " + fileName + "...");
        boolean undecorated = false;
        try {
            config = new ConfigFile(new File(fileName), "GUI Definition");
            config.load();
            laf = config.getString(CK_LAF, "system");
            PlafFactory.getInstance().setCurrentTheme(laf, config.getString(CK_THEME, ""));
            isTextureVisible = config.getBoolean(CK_TEXTURE, false);
            isKiwiTextureVisible = config.getBoolean(CK_KIWI, true);
            undecorated = config.getBoolean(CK_UNDECORATED, false);
        } catch (FileNotFoundException ex) {
            laf = "system";
            isTextureVisible = false;
            isKiwiTextureVisible = false;
        } catch (Exception ex) {
            // silently ignore
        }
        /**
         * Set undecorated
         */
        getFrame().setUndecorated(undecorated);
        if (undecorated) {
            getFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        /**
         * Load recently used texture
         */

        try {

            fileName = Workspace.getUserHome() + "texture.jpg";
            /**
             * Read texture
             */
            texture = Imaging.getBufferedImage(new File(fileName));
            /**
             * Finally set texture on frame
             */
            ((WorkspaceFrame) getFrame()).setTexture(texture);

        } catch (ImageReadException | IOException e) {
            Workspace.getLogger().log(Level.WARNING, "Cannot set texture", e);
        } finally {
            /**
             * Read texture show or hide flag
             */
            setTextureVisible(isTextureVisible);
        }

        // SET LAF

        try {
            if (laf != null && !laf.equals("")) {
                Workspace.getLogger().info(">" + "Loading laf" + " " + laf + " ...");
                if (laf.equals("system") || !PlafFactory.getInstance().setLookAndFeel(laf)) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
        } catch (Exception ex) {
            Workspace.getLogger().log(Level.WARNING, "Cannot set look and feel", ex);
        }
        // LOAD PROFILE DATA

        fileName = Workspace.getUserHome() + "jwxwin.dat";
        Workspace.getLogger().info(">" + "Reading file" + " " + fileName + "...");
        try {
            FileInputStream inputFile = new FileInputStream(fileName);
            DataInputStream inputStream = new DataInputStream(inputFile);
            /**
             * Delegates loading of UI components to workspace frame
             */
            ((WorkspaceFrame) getFrame()).load(inputStream);
        } catch (IOException e) {
            ((WorkspaceFrame) getFrame()).create();
        }

        update();

        Workspace.getLogger().info(">" + "GUI configuration is successfully read");
        /**
         * Load plugins
         */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pr = new ProgressDialog(Workspace.getUI().getFrame(),
                        LangResource.getString("WorkspaceGUI.shells.loading"), true);

                ShellsLoader shloader = new ShellsLoader();
                pr.track(shloader);
            }
        });
    }

    /**
     * Returns all registered components.
     */
    public Hashtable getAllRegistered() {
        return components;
    }

    /**
     * Returns all shells repository copy.
     */
    public HashSet getShells() {
        return (HashSet) shells.clone();
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
        ((WorkspaceFrame) getFrame()).reset();
        frame = null;
        components = new Hashtable();
        shells = new HashSet();
        getFrame().setTitle(Workspace.getVersion());
        WorkspaceClassCache.resetFileChoosers();
        System.gc();
    }

    /**
     * Get human readable name for installer
     */
    public String getName() {
        return "Clematis GUI Engine (R) v1.02";
    }

    /**
     * Set texture for java workspace gui
     */
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    /**
     * Set texture visibility and revalidates main frame
     * for java workspace.
     */
    public void setTextureVisible(boolean isTextureVisible) {
        this.isTextureVisible = isTextureVisible;
        if (isTextureVisible && texture != null) {
            UIChangeManager.setDefaultTexture(texture);
            ((WorkspaceFrame) getFrame()).setTexture(texture);
        } else {
            UIChangeManager.setDefaultTexture(null);
            ((WorkspaceFrame) getFrame()).setTexture(null);
        }
    }

    /**
     * Set KIWI texture visible and selectable from repository
     */
    public void setKiwiTextureVisible(boolean isKiwiTextureVisible) {
        this.isKiwiTextureVisible = isKiwiTextureVisible;
    }

    /**
     * Saves profile data of Workspace GUI on disk.
     */
    public void save() {
        String fileName = Workspace.getUserHome() + "jwxwin.cfg";
        Workspace.getLogger().info(">" + "Writing file" + " " + fileName + "...");

        File file = new File(Workspace.getUserHome());

        if (!file.exists()) {
            file.mkdirs();
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
            WorkspaceError.exception
                    (LangResource.getString("WorkspaceGUI.save.failed"), ex);
        }
        /**
         * Write texture on disk
         */
        if (texture != null) {

            try
            {
                fileName = Workspace.getUserHome() + "texture.jpg";
                OutputStream os = new FileOutputStream(fileName);
                ImageIcon textureIcon = new ImageIcon(texture);

                BufferedImage bi = new BufferedImage(
                        textureIcon.getIconWidth(),
                        textureIcon.getIconHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.createGraphics();
                // paint the Icon to the BufferedImage.
                textureIcon.paintIcon(null, g, 0,0);
                g.dispose();

                if (textureIcon.getIconHeight() > 0 && textureIcon.getIconWidth() > 0)
                {
                    Imaging.writeImage(bi, os, ImageFormats.JPEG, null);
                }

            } catch (ImageWriteException | IOException e) {
                WorkspaceError.exception
                        (LangResource.getString("WorkspaceGUI.saveTexture.failed"), e);
            }
        }
        /**
         * Save all other info
         */
        saveShells();
        /**
         * Save look and feel infos
         */
        PlafFactory.getInstance().save();
        /**
         * Dispose opened frames
         */
        for (Object displayedFrame : displayedFrames) {
            ((Frame) displayedFrame).dispose();
        }
        /**
         * Recreate array of opened frames
         */
        displayedFrames = new ArrayList();

        fileName = Workspace.getUserHome() + "jwxwin.dat";
        Workspace.getLogger().info(">" + "Writing file" + " " + fileName + "...");

        try {
            FileOutputStream outputFile = new FileOutputStream(fileName);
            DataOutputStream outputStream = new DataOutputStream(outputFile);
            ((WorkspaceFrame) getFrame()).save(outputStream);
        } catch (IOException e) {
            WorkspaceError.exception
                    (LangResource.getString("WorkspaceGUI.saveFrame.failed"), e);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Workspace.getLogger().log(Level.WARNING, "Cannot set look and feel", ex);
        }
    }

    /**
     * Save all graphic UI plugins (shells)
     */
    protected void saveShells() {
        Iterator iter = shells.iterator();
        while (iter.hasNext()) {
            Plugin shell = (Plugin) iter.next();
            Workspace.getLogger().info(">" + "Saving " + shell.getName() + "...");
            try {
                shell.dispose();
            } catch (PluginException ex) {
                WorkspaceError.exception
                        (LangResource.getString("WorkspaceGUI.plugin.saveFailed"), ex);
            }
        }
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
        ((WorkspaceFrame) getFrame()).update();
    }

    /**
     * Process event. Such event is send to every
     * subsribed event listener in synchronous manner.
     */
    public void processEvent(Object event, Object lparam, Object rparam) {
        /**
         * Activate unique GUI shell, i.e. bring the shell to the front
         */
        if (event instanceof Integer && ((Integer) event).intValue() == 1000) {
            new Listener1000().processEvent(event, lparam, rparam);
        }
        /**
         * Adds new internal frame (JInternalFrame) on currenlty opened desktop
         */
        else if (event instanceof Integer && ((Integer) event).intValue() == 1001) {
            new Listener1001().processEvent(event, lparam, rparam);
        }
        /**
         * Switch menu of the view, then it's becomes inactive or otherwise activated.
         */
        else if (event instanceof Integer && ((Integer) event).intValue() == 1002) {
            new Listener1002().processEvent(event, lparam, rparam);
        }
        /**
         * Show external frame and put frame into array of displayed frames
         */
        else if (event instanceof Integer && ((Integer) event).intValue() == 1003) {
            new Listener1003().processEvent(event, lparam, rparam);
        }
    }

    class Listener1000 implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {
            if (lparam instanceof Hashtable) {
                if (((Hashtable) lparam).get("view") instanceof IView
                        && ((Hashtable) lparam).get("display") instanceof Boolean
                        && ((Hashtable) lparam).get("register") instanceof Boolean) {
                    Hashtable lhparam = (Hashtable) lparam;
                    IView view = (IView) lhparam.get("view");
                    Boolean display = (Boolean) lhparam.get("display");
                    Boolean register = (Boolean) lhparam.get("register");

                    ((WorkspaceFrame) getFrame()).getContentManager().addView(view, display.booleanValue(),
                            register.booleanValue());
                }
            }
        }
    }

    class Listener1001 implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {
            if (lparam instanceof Hashtable) {
                if (((Hashtable) lparam).get("view") instanceof JComponent
                        && ((Hashtable) lparam).get("display") instanceof Boolean
                        && ((Hashtable) lparam).get("register") instanceof Boolean) {
                    Hashtable lhparam = (Hashtable) lparam;
                    JComponent view = (JComponent) lhparam.get("view");
                    Boolean display = (Boolean) lhparam.get("display");
                    Boolean register = (Boolean) lhparam.get("register");

                    IView cur_view = ((WorkspaceFrame) getFrame()).getContentManager().getCurrentView();
                    if (cur_view instanceof Desktop) {
                        ((Desktop) cur_view).addView(view, display.booleanValue(),
                                register.booleanValue());
                    } else {
                        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                                getImage("desktop/desktop_big.png"));
                        JOptionPane.showMessageDialog(((WorkspaceFrame) getFrame()),
                                LangResource.getString("WorkspaceGUI.intWnd.onlyOnDesktop"),
                                LangResource.getString("WorkspaceGUI.intWnd.onlyOnDesktop.title"),
                                JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                }
            }
        }
    }

    class Listener1002 implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {
            if (lparam instanceof Hashtable) {
                if (((Hashtable) lparam).get("menus") instanceof Vector
                        && ((Hashtable) lparam).get("flag") instanceof Boolean) {
                    Hashtable lhparam = (Hashtable) lparam;
                    Vector menus = (Vector) lhparam.get("menus");
                    /**
                     * Flag - true - add, false - remove.
                     */
                    Boolean flag = (Boolean) lhparam.get("flag");

                    if (flag.booleanValue()) {
                        for (int i = 0; i < menus.size(); i++) {
                            if (menus.elementAt(i) instanceof JMenu) {
                                ((WorkspaceFrame) getFrame()).getJMenuBar().add((JMenu) menus.elementAt(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < menus.size(); i++) {
                            if (menus.elementAt(i) instanceof JMenu) {
                                ((WorkspaceFrame) getFrame()).getJMenuBar().remove((JMenu) menus.elementAt(i));
                            }
                        }
                    }
                    ((WorkspaceFrame) getFrame()).getJMenuBar().revalidate();
                    ((WorkspaceFrame) getFrame()).getJMenuBar().repaint();
                }
            }
        }
    }

    class Listener1003 implements IWorkspaceListener {
        public void processEvent(Object event, Object lparam, Object rparam) {
            if (lparam instanceof Hashtable) {
                if (((Hashtable) lparam).get("frame") instanceof Frame) {
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