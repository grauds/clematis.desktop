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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.ui.KFrame;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.cpanel.ControlPanel;
import jworkspace.ui.widgets.GlassDragPane;
import jworkspace.util.WorkspaceError;

/**
 * Main frame for Java Workspace UI.
 */
public class MainFrame extends KFrame implements PropertyChangeListener {
    /**
     * The name of content manager
     */
    private static final String CONTENT_MANAGER = "jworkspace.ui.views.ViewsManager";
    /**
     * Content.
     */
    private static AbstractViewsManager content = null;
    /**
     * A reference to Workspace GUI
     */
    protected WorkspaceGUI gui = null;
    /**
     * Control panel.
     */
    private ControlPanel controlPanel = null;
    /**
     * System menu.
     */
    private JMenuBar systemMenu = null;
    /**
     * System clipboard- can be used for copy/paste
     * or drag and drop operations.
     */
    private Clipboard c = new Clipboard("System clipboard");
    /**
     * The drag pane is needed every time dragging
     * occurs. Drag pane displays possible position
     * of the panel.
     */
    private GlassDragPane dragPane = null;
    /**
     * Current orientation
     */
    private String orientation = BorderLayout.EAST;

    /**
     * Main frame for Java Workspace GUI is created
     * with only title. All initialization is made
     * by components of main frame by themselves.
     */
    public MainFrame(String title, WorkspaceGUI gui) {
        super(title);
        /*
         * Insert menu bar on the top.
         */
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.gui = gui;
        this.getMainContainer().setLayout(new BorderLayout());
        this.getMainContainer().setOpaque(false);
        /*
         * ui managers
         */
        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    /**
     * Creates default frame.
     */
    public void create() {
        Workspace.getLogger().info(">" + "Building gui" + "...");
        try {
            Class clazz = Class.forName(MainFrame.CONTENT_MANAGER);
            Object object = clazz.newInstance();

            if (!(object instanceof JComponent)) {
                throw new IllegalArgumentException();
            }

            content = (AbstractViewsManager) object;
            Workspace.getLogger().info(">" + "Loaded content manager" +
                " " + MainFrame.CONTENT_MANAGER);
        }
        /*
         * Catch exception is there is no multidesktop manager
         * installed, or it cannot be loaded.
         */ catch (Exception e) {
            WorkspaceError.exception
                (LangResource.getString("MainFrame.load.CM.failed"), e);
            System.exit(-1);
        }
        /*
         * Ask for buttons and fill control panel.
         */
        CButton[] buttons = content.getButtons();
        if (buttons != null) {
            for (CButton button : buttons) {
                getControlPanel().addButton(button);
            }
            if (buttons.length > 0) {
                getControlPanel().addSeparator();
            }
        }
        /*
         * Ask for menus
         */
        JMenu[] menus = content.getMenu();
        if (menus != null) {
            for (JMenu menu : menus) {
                getJMenuBar().add(menu);
            }
        }
        /*
         * Create ui by default.
         */
        content.create();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, size.width, size.height);

        assemble();
        setVisible(true);
        Workspace.getLogger().info(">" + "Frame is loaded with default configuration");
    }

    /*
     * Called in response to a frame close event to determine if this frame
     * may be closed.
     *
     * @return <code>true</code> if the frame is allowed to close, and
     * <code>false</code> otherwise. The default implementation returns
     * <code>true</code>.
     */
    protected boolean canClose() {
        return (false);
    }

    /*
     * Drags control panel along the frame.
     */
    private void drag(int _x, int _y) {
        if ((_y < getMainContainer().getSize().height / 3) &&
            (_x > getMainContainer().getSize().width / 3) &&
            (_x < 2 * getMainContainer().getSize().width / 3)) {
            if (dragPane == null) {
                dragPane = new GlassDragPane();
            }

            dragPane.setBounds(getMainContainer().getLocation().x,
                getMainContainer().getLocation().y,
                getMainContainer().getSize().width, Math.min(controlPanel.getWidth(),
                    controlPanel.getHeight()));

            orientation = BorderLayout.NORTH;
            getMainContainer().repaint();

        } else if ((_y > 2 * getMainContainer().getSize().height / 3) &&
            (_x > getMainContainer().getSize().width / 3) &&
            (_x < 2 * getMainContainer().getSize().width / 3)) {
            if (dragPane == null) {
                dragPane = new GlassDragPane();
            }

            dragPane.setBounds(getMainContainer().getLocation().x,
                getMainContainer().getLocation().y +
                    getMainContainer().getSize().height - Math.min(controlPanel.getWidth(),
                    controlPanel.getHeight()),

                getMainContainer().getSize().width, Math.min(controlPanel.getWidth(),
                    controlPanel.getHeight()));


            orientation = BorderLayout.SOUTH;
            getMainContainer().repaint();
        } else if (_x < getMainContainer().getSize().width / 3) {
            if (dragPane == null) {
                dragPane = new GlassDragPane();
            }

            dragPane.setBounds(getMainContainer().getLocation().x,
                getMainContainer().getLocation().y, Math.min(controlPanel.getWidth(),
                    controlPanel.getHeight()),

                getMainContainer().getSize().height);

            orientation = BorderLayout.WEST;
            getMainContainer().repaint();
        } else if ((_x > 2 * getMainContainer().getSize().width / 3)) {
            if (dragPane == null) {
                dragPane = new GlassDragPane();
            }

            dragPane.setBounds(getMainContainer().getLocation().x +
                    getMainContainer().getSize().width - Math.min(controlPanel.getWidth(),
                controlPanel.getHeight()),

                getMainContainer().getLocation().y, Math.min(controlPanel.getWidth(),
                    controlPanel.getHeight()),

                getMainContainer().getSize().height);

            orientation = BorderLayout.EAST;
            getMainContainer().repaint();
        }
    }

    /*
     * Returns clipboard for components use.
     */
    public Clipboard getClipboard() {
        return c;
    }

    /*
     * Returns content manager for this frame.
     */
    AbstractViewsManager getContentManager() {
        return content;
    }

    /**
     * Returns control panel for this frame.
     */
    ControlPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new ControlPanel();
            controlPanel.addPropertyChangeListener(this);
        }
        return controlPanel;
    }

    /*
     * Returns system menu bar.
     */
    public JMenuBar getJMenuBar() {
        if (systemMenu == null) {
            createMenuBar();
        }
        return systemMenu;
    }

    /**
     * This method does not allow to set any menus.
     *
     * @param menuBar javax.swing.JMenuBar
     */
    public void setJMenuBar(JMenuBar menuBar) {
    }

    /*
     * Assemble system menu.
     */
    private void createMenuBar() {
        systemMenu = new JMenuBar();

        JMenu wmenu = new JMenu
            (LangResource.getString("WorkspaceFrame.menu.workspace"));
        wmenu.setMnemonic
            (LangResource.getString("WorkspaceFrame.menu.workspace.key").charAt(0));
        /*
         *  My details
         */
        JMenuItem my_details = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.myDetailsActionName));
        my_details.setAccelerator
            (KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        wmenu.add(my_details);
        /*
         * Settings
         */
        JMenuItem settings = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.settingsActionName));
        wmenu.add(settings);
        /*
         * Show control panel
         */
        JCheckBoxMenuItem showControlPanel = Utils.createCheckboxMenuItem
            (gui.getActions().getAction(UIActions.showPanelActionName));
        showControlPanel.setSelected(getControlPanel().isVisible());
        showControlPanel.setAccelerator
            (KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
        wmenu.add(showControlPanel);
        wmenu.addSeparator();
        /*
         * Help
         */
        JMenuItem help = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.helpActionName));
        help.setEnabled(false);
        wmenu.add(help);
        /*
         * About
         */
        JMenuItem about = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.aboutActionName));
        wmenu.add(about);
        wmenu.addSeparator();
        /*
         * Log off
         */
        JMenuItem log_off = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.logoffActionName));
        wmenu.add(log_off);
        /*
         * Exit
         */
        JMenuItem exit = Utils.createMenuItem
            (gui.getActions().getAction(UIActions.exitActionName));
        exit.setAccelerator
            (KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK));
        wmenu.add(exit);

        systemMenu.add(wmenu);
    }

    /*
     * Returns true if any of views claimed it is modified
     * by user.
     */
    public boolean isModified() {
        return content != null && content.isModified();
    }

    /*
     * Loads profile data. Input stream in parameters
     * is from file jwxwin.dat.
     */
    public void load(DataInputStream inputStream) {
        Workspace.getLogger().info(">" + "Loading workspace frame");
        /*
         * Try to load content manager
         */
        try {
            Class clazz = Class.forName(MainFrame.CONTENT_MANAGER);
            Object object = clazz.newInstance();

            if (!(object instanceof JComponent)) {
                throw new IllegalArgumentException();
            }

            content = (AbstractViewsManager) object;
        }
        /*
         * Catch exception is there is no multidesktop manager
         * installed, or it cannot be loaded.
         */ catch (Exception e) {
            WorkspaceError.exception
                (LangResource.getString("MainFrame.cmLoad.failed"), e);
            System.exit(-1);
        }
        boolean visible = false;
        int x = 0;
        int y = 0;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        try {
            /*
             * Read Main Frame config.
             */
            visible = inputStream.readBoolean();
            /*
             * Control panel orientation.
             */
            orientation = inputStream.readUTF();
            /*
             * Location
             */
            x = inputStream.readInt();
            y = inputStream.readInt();
            /*
             * Size
             */
            width = inputStream.readInt();
            height = inputStream.readInt();
        } catch (IOException e) {
            WorkspaceError.exception
                (LangResource.getString("WorkspaceFrame.load.failed"), e);
        }
        /*
         * Control panel
         */
        gui.getActions().getShowPanelAction().setSelected(getControlPanel().isVisible());
        getControlPanel().setVisible(visible);
        getControlPanel().setOrientation(orientation);
        /*
         * Set bounds
         */
        _setBounds(x, y, width, height);

        try {
            /*
             * Ask for buttons and fill control panel.
             */
            CButton[] buttons = content.getButtons();
            if (buttons != null) {
                for (CButton button : buttons) {
                    getControlPanel().addButton(button);
                }
                if (buttons.length > 0) {
                    getControlPanel().addSeparator();
                }
            }
            /*
             * Ask for menus
             */
            JMenu[] menus = content.getMenu();
            if (menus != null) {
                for (JMenu menu : menus) {
                    getJMenuBar().add(menu);
                }
            }
            /*
             * Load and update content
             */
            content.load();
        } catch (IOException e) {
            WorkspaceError.exception
                (LangResource.getString("MainFrame.cmLoad.failed"), e);
        }
        /*
         * End of Main Frame config.
         */
        assemble();
        Workspace.getLogger().info(">" + "Loaded workspace frame");
        setVisible(true);
    }

    /**
     * This method listens for property changes
     * of all nested components, such as control panel,
     * etc.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getSource() instanceof ControlPanel) {
            if (evt.getPropertyName().equals("BEGIN_DRAGGING")) {
                dragPane = new GlassDragPane();
                getLayeredPane().setLayer(dragPane, Integer.MAX_VALUE);
                dragPane.setBounds(controlPanel.getX() + getMainContainer().getX(),
                    controlPanel.getY() + getMainContainer().getY(),
                    controlPanel.getWidth(), controlPanel.getHeight());
                getLayeredPane().add(dragPane);
            } else if (evt.getPropertyName().equals("END_DRAGGING")) {
                getLayeredPane().remove(dragPane);
                dragPane = null;

                getMainContainer().remove(controlPanel);

                controlPanel.setOrientation(orientation);

                getMainContainer().add(controlPanel, orientation);
                getContentPane().validate();
                getContentPane().repaint();
            } else if (evt.getPropertyName().equals("DRAGGED")) {
                /*
                 * Mouse pointer coordinates
                 */
                drag(((Point) evt.getNewValue()).x + controlPanel.getX() + getContentPane().getX(),
                    ((Point) evt.getNewValue()).y + controlPanel.getY() + getContentPane().getY());
            }
        }
    }

    /**
     * Resets frame to its initial state.
     */
    public void reset() {
        getMainContainer().remove(content);
        getMainContainer().remove(getControlPanel());
        removeMenuBar();
        setTexture(null);

        content = null;
        controlPanel = null;
        systemMenu = null;

        update();
        System.gc();
        setVisible(false);
    }

    private void _setBounds(int x, int y, int width, int height) {
        /*
         * Set bounds only if this frame is decorated
         */
        if (!isUndecorated()) {
            if (x > Toolkit.getDefaultToolkit().getScreenSize().width) {
                x = Toolkit.getDefaultToolkit().getScreenSize().width - 10;
            } else if (x < 0) {
                x = 0;
            }
            if (y > Toolkit.getDefaultToolkit().getScreenSize().height) {
                y = Toolkit.getDefaultToolkit().getScreenSize().height - 10;
            } else if (y < 0) {
                y = 0;
            }
            setLocation(x, y);

            if (width < 50 || height < 50 ||
                width > Toolkit.getDefaultToolkit().getScreenSize().width
                || height > Toolkit.getDefaultToolkit().getScreenSize().height) {
                setSize(Toolkit.getDefaultToolkit().getScreenSize());
            } else {
                setSize(width, height);
            }
        }
    }

    /**
     * Saves profile data.
     */
    public void save(DataOutputStream outputStream) {
        Workspace.getLogger().info(">" + "Saving workspace frame");
        /*
         * Write out Main Frame configuration.
         */
        boolean visible = getControlPanel().isVisible();
        try {
            outputStream.writeBoolean(visible);
            outputStream.writeUTF(orientation);
            outputStream.writeInt(getLocation().x);
            outputStream.writeInt(getLocation().y);
            outputStream.writeInt(getWidth());
            outputStream.writeInt(getHeight());
            /*
             * Save content of the frame
             */
            content.save();
            /*
             * Reset content of the frame
             */
            content.reset();
        } catch (IOException e) {
            WorkspaceError.exception
                (LangResource.getString("MainFrame.save.failed"), e);
        }
        Workspace.getLogger().info(">" + "Saved workspace frame");
    }

    /**
     * Set the background texture.
     *
     * @param image The image to use as the background
     *              texture for the frame.
     */
    public void setTexture(Image image) {
        super.setTexture(image);
        getContentPane().repaint();
    }

    /**
     * Sets Workspace frame to a full screen wide.
     */
    private void assemble() {
        /*
         * Settle stuff on frame.
         */
        setMenuBar(getJMenuBar());
        getMainContainer().add(content, BorderLayout.CENTER);
        getMainContainer().add(getControlPanel(), orientation);
        /*
         * Finally display.
         */
        setVisible(true);
        update();
    }

    /**
     * Switches control panel on and off.
     */
    void switchControlPanel() {
        getControlPanel().setVisible(!getControlPanel().isVisible());
        validate();
        repaint();
    }

    /**
     * Updates controls for the component.
     */
    public void update() {
        setTitle(Workspace.getVersion() + " " +
            Workspace.getProfilesEngine().getUserName());
        invalidate();
        validate();
        repaint();
    }
}