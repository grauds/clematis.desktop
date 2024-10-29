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
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.InputEvent;
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

import jworkspace.Workspace;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.api.AbstractViewsManager;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.api.cpanel.CButton;
import jworkspace.ui.cpanel.ControlPanel;
import jworkspace.ui.profiles.LoginPanel;
import jworkspace.ui.utils.SwingUtils;
import jworkspace.ui.widgets.GlassDragPane;
import jworkspace.ui.widgets.WorkspaceError;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * Main frame for Java Workspace UI
 * @author Anton Troshin
 */
@Log
public class MainFrame extends KFrame implements PropertyChangeListener {
    /**
     * The name of content manager
     */
    private static final String CONTENT_MANAGER = "jworkspace.ui.views.ViewsManager";
    /**
     * Message to show if this frame has failed to load
     */
    private static final String MAIN_FRAME_LOAD_FAILED_MESSAGE = "MainFrame.cmLoad.failed";
    /**
     * A reference to Workspace GUI
     */
    @Getter
    private final WorkspaceGUI gui;
    /**
     * System menu.
     */
    @Getter
    private JMenuBar systemMenu = null;
    /**
     * Content.
     */
    private AbstractViewsManager contentManager = null;
    /**
     * Control panel.
     */
    private ControlPanel controlPanel = null;
    /**
     * Control panel orientation
     */
    @Getter
    @Setter
    private String orientation = BorderLayout.EAST;
    /**
     * System clipboard- can be used for copy/paste
     * or dragControlPanel and drop operations.
     */
    @Getter
    private final Clipboard clipboard = new Clipboard("System clipboard");
    /**
     * The dragControlPanel pane is needed every time dragging
     * occurs. Drag pane displays possible position
     * of the panel.
     */
    private GlassDragPane dragPane = null;
    /**
     * Top login panel overlay
     */
    private final LoginPanel loginPanel = new LoginPanel();
    /**
     * Main frame for Java Workspace GUI is created
     * with only title. All initialization is made
     * by components of main frame by themselves.
     */
    MainFrame(String title, WorkspaceGUI gui) {
        super(title);

        // save reference for the ui
        this.gui = gui;

        // insert menu bar on the top.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.getMainContainer().setLayout(new BorderLayout());
        this.getMainContainer().setOpaque(false);

        // ui managers
        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    /**
     * Creates default frame.
     */
    public void create() {

        log.info("Building gui" + Constants.LOG_FINISH);
        /*
         * Ask for buttons and fill control panel.
         */
        CButton[] buttons = getContentManager().getButtons();
        if (buttons != null) {
            for (CButton button : buttons) {
                getControlPanel().addButton(button);
            }
        }
        /*
         * Ask for menus
         */
        JMenu[] menus = getContentManager().getMenu();
        if (menus != null) {
            for (JMenu menu : menus) {
                getJMenuBar().add(menu);
            }
        }

        // assemble default ui
        getContentManager().create();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, size.width, size.height);

        assemble();
        setVisible(true);
        log.info("Frame is loaded with default configuration");
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
    @SuppressWarnings("MagicNumber")
    private void dragControlPanel(int x, int y) {
        
        if ((y < getMainContainer().getSize().height / 3) 
            && (x > getMainContainer().getSize().width / 3) 
            && (x < 2 * getMainContainer().getSize().width / 3)
        ) {

            getDragPane().setBounds(
                getMainContainer().getLocation().x,
                getMainContainer().getLocation().y,
                getMainContainer().getSize().width,
                Math.min(getControlPanel().getWidth(), getControlPanel().getHeight())
            );

            setOrientation(BorderLayout.NORTH);
            getMainContainer().repaint();

        } else if ((y > 2 * getMainContainer().getSize().height / 3) 
            && (x > getMainContainer().getSize().width / 3) 
            && (x < 2 * getMainContainer().getSize().width / 3)
        ) {

            getDragPane().setBounds(
                getMainContainer().getLocation().x,
                getMainContainer().getLocation().y 
                    + getMainContainer().getSize().height
                    - Math.min(getControlPanel().getWidth(), getControlPanel().getHeight()),
                getMainContainer().getSize().width,
                Math.min(getControlPanel().getWidth(), getControlPanel().getHeight())
            );


            setOrientation(BorderLayout.SOUTH);
            getMainContainer().repaint();

        } else if (x < getMainContainer().getSize().width / 3) {

            getDragPane().setBounds(
                getMainContainer().getLocation().x,
                getMainContainer().getLocation().y,
                Math.min(getControlPanel().getWidth(), getControlPanel().getHeight()),
                getMainContainer().getSize().height
            );

            setOrientation(BorderLayout.WEST);
            getMainContainer().repaint();

        } else if ((x > 2 * getMainContainer().getSize().width / 3)) {

            getDragPane().setBounds(
                getMainContainer().getLocation().x
                    + getMainContainer().getSize().width
                    - Math.min(getControlPanel().getWidth(), getControlPanel().getHeight()),
                getMainContainer().getLocation().y,
                Math.min(getControlPanel().getWidth(), getControlPanel().getHeight()),
                getMainContainer().getSize().height
            );

            setOrientation(BorderLayout.EAST);
            getMainContainer().repaint();
        }
    }

    protected GlassDragPane getDragPane() {
        if (dragPane == null) {
            dragPane = new GlassDragPane();
        }
        return dragPane;
    }

    /*
     * Returns content manager for this frame.
     * // todo can be a plugin extension point
     */
    AbstractViewsManager getContentManager() throws RuntimeException {

        if (contentManager == null) {
            try {
                Class<?> clazz = Class.forName(MainFrame.CONTENT_MANAGER);
                Object object = clazz.getDeclaredConstructor().newInstance();

                if (!(object instanceof JComponent)) {
                    throw new IllegalArgumentException();
                }

                contentManager = (AbstractViewsManager) object;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return contentManager;
    }

    /**
     * Returns control panel for this frame.
     */
    public ControlPanel getControlPanel() {
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
            systemMenu = new JMenuBar();

            JMenu wmenu = new JMenu(WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.workspace"));
            wmenu.setMnemonic(WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.workspace.key").charAt(0));
            /*
             *  My details
             */
            JMenuItem myDetails = SwingUtils.createMenuItem(getGui().getActions().getAction(
                UIActions.MY_DETAILS_ACTION_NAME)
            );
            myDetails.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
            wmenu.add(myDetails);
            /*
             * Settings
             */
            JMenuItem settings = SwingUtils.createMenuItem(
                getGui().getActions().getAction(UIActions.SETTINGS_ACTION_NAME)
            );
            wmenu.add(settings);
            /*
             * Show control panel
             */
            JCheckBoxMenuItem showControlPanel = SwingUtils.createCheckboxMenuItem(getGui().getActions()
                .getAction(UIActions.SHOW_PANEL_ACTION_NAME));
            showControlPanel.setSelected(getControlPanel().isVisible());
            showControlPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
            wmenu.add(showControlPanel);
            wmenu.addSeparator();
            /*
             * Help
             */
            JMenuItem help = SwingUtils.createMenuItem(getGui().getActions().getAction(UIActions.HELP_ACTION_NAME));
            help.setEnabled(false);
            wmenu.add(help);
            /*
             * About
             */
            JMenuItem about = SwingUtils.createMenuItem(getGui().getActions().getAction(UIActions.ABOUT_ACTION_NAME));
            wmenu.add(about);
            wmenu.addSeparator();
            /*
             * Log off
             */
            JMenuItem logOff = SwingUtils.createMenuItem(getGui().getActions().getAction(UIActions.LOGOFF_ACTION_NAME));
            wmenu.add(logOff);
            /*
             * Exit
             */
            JMenuItem exit = SwingUtils.createMenuItem(getGui().getActions().getAction(UIActions.EXIT_ACTION_NAME));
            exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
            wmenu.add(exit);

            systemMenu.add(wmenu);
        }
        return systemMenu;
    }

    /**
     * This method does not allow to set any menus.
     *
     * @param menuBar javax.swing.JMenuBar
     */
    public void setJMenuBar(JMenuBar menuBar) {}

    /*
     * Returns true if any of views claimed it is modified
     * by user.
     */
    public boolean isModified() {
        return getContentManager() != null && getContentManager().isModified();
    }

    /*
     * Loads profile data. Input stream in parameters is from file jwxwin.dat.
     */
    public void load(DataInputStream inputStream) {
        log.info("Loading workspace frame");

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
            setOrientation(inputStream.readUTF());
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
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("WorkspaceFrame.load.failed"), e);
        }
        /*
         * Control panel
         */
        getGui().getActions().getShowPanelAction().setSelected(getControlPanel().isVisible());
        getControlPanel().setVisible(visible);
        getControlPanel().setOrientation(getOrientation());
        /*
         * Set bounds
         */
        setGlassBounds(x, y, width, height);

        try {
            /*
             * Ask for buttons and fill control panel.
             */
            CButton[] buttons = getContentManager().getButtons();
            if (buttons != null) {
                for (CButton button : buttons) {
                    getControlPanel().addButton(button);
                }
            }
            /*
             * Ask for menus
             */
            JMenu[] menus = getContentManager().getMenu();
            if (menus != null) {
                for (JMenu menu : menus) {
                    getJMenuBar().add(menu);
                }
            }
            /*
             * Load and update content
             */
            getContentManager().load();
        } catch (IOException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString(MAIN_FRAME_LOAD_FAILED_MESSAGE), e);
        }
        /*
         * End of Main Frame config.
         */
        assemble();
        log.info("Loaded workspace frame");
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

                getLayeredPane().setLayer(getDragPane(), Integer.MAX_VALUE);
                getDragPane().setBounds(
                    getControlPanel().getX() + getMainContainer().getX(),
                    getControlPanel().getY() + getMainContainer().getY(),
                    getControlPanel().getWidth(), getControlPanel().getHeight()
                );
                getLayeredPane().add(getDragPane());

            } else if (evt.getPropertyName().equals("END_DRAGGING")) {

                getLayeredPane().remove(getDragPane());
                dragPane = null;
                getMainContainer().remove(getControlPanel());

                getControlPanel().setOrientation(getOrientation());

                getMainContainer().add(getControlPanel(), getOrientation());
                getContentPane().validate();
                getContentPane().repaint();

            } else if (evt.getPropertyName().equals("DRAGGED")) {

                // follow mouse pointer
                dragControlPanel(
                    ((Point) evt.getNewValue()).x + getControlPanel().getX() + getContentPane().getX(),
                    ((Point) evt.getNewValue()).y + getControlPanel().getY() + getContentPane().getY()
                );
            }
        }
    }

    /**
     * Resets frame to its initial state.
     */
    public void reset() {

        removeMenuBar(getJMenuBar());

        getMainContainer().remove(getContentManager());
        getMainContainer().remove(getControlPanel());
        getMainContainer().remove(getJMenuBar());
        setTexture(null);

        contentManager = null;
        controlPanel = null;
        systemMenu = null;

        addLoginPanel();

        invalidate();
        validate();
        repaint();
    }

    @SuppressWarnings("MagicNumber")
    private void setGlassBounds(int x, int y, int width, int height) {
        /*
         * Set bounds only if this frame is decorated
         */
        int locationX = x;
        int locationY = y;

        if (!isUndecorated()) {
            if (x > Toolkit.getDefaultToolkit().getScreenSize().width) {
                locationX = Toolkit.getDefaultToolkit().getScreenSize().width - 10;
            } else if (x < 0) {
                locationX = 0;
            }
            if (y > Toolkit.getDefaultToolkit().getScreenSize().height) {
                locationY = Toolkit.getDefaultToolkit().getScreenSize().height - 10;
            } else if (y < 0) {
                locationY = 0;
            }
            setLocation(locationX, locationY);

            if (width < 50 || height < 50
                || width > Toolkit.getDefaultToolkit().getScreenSize().width
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
        log.info("Saving workspace frame");
        /*
         * Write out Main Frame configuration.
         */
        boolean visible = getControlPanel().isVisible();
        try {
            outputStream.writeBoolean(visible);
            outputStream.writeUTF(getOrientation());
            outputStream.writeInt(getLocation().x);
            outputStream.writeInt(getLocation().y);
            outputStream.writeInt(getWidth());
            outputStream.writeInt(getHeight());
            /*
             * Save content of the frame
             */
            getContentManager().save();
            /*
             * Reset content of the frame
             */
            getContentManager().reset();
        } catch (IOException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("MainFrame.save.failed"), e);
        }
        log.info("Saved workspace frame");
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
        getMainContainer().add(getContentManager(), BorderLayout.CENTER);
        getMainContainer().add(getControlPanel(), getOrientation());
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
        setTitle(Workspace.VERSION
            + Constants.LOG_SPACE
            + ServiceLocator.getInstance().getProfilesManager().getCurrentProfile().getUserName()
        );
        invalidate();
        validate();
        repaint();
    }

    protected void addLoginPanel() {
        loginPanel.setBounds(0, 0,
            getMainContainer().getWidth(),
            getMainContainer().getHeight());
        getMainContainer().add(loginPanel);
        getMainContainer().repaint();
    }

    protected void removeLoginPanel() {
        getMainContainer().remove(loginPanel);
    }

}