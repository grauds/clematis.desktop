package jworkspace.ui.desktop;

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.DefaultDesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.hyperrealm.kiwi.ui.KDesktopPane;
import com.hyperrealm.kiwi.ui.KPanel;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.IView;
import jworkspace.ui.ClassCache;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.widgets.GlassDragPane;
import jworkspace.util.WorkspaceError;
import jworkspace.ui.Utils;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

/**
 * Java Desktop.
 */
public class Desktop extends KDesktopPane implements IView, MouseListener, MouseMotionListener,
    ActionListener, ClipboardOwner {
    /**
     * Tile image
     */
    public static final int TILE_IMAGE = 1;
    /**
     * Center image
     */
    public static final int CENTER_IMAGE = 2;
    /**
     * Stretch image
     */
    public static final int STRETCH_IMAGE = 3;
    /**
     * Top left corner image
     */
    public static final int TOP_LEFT_CORNER_IMAGE = 4;
    /**
     * Bottom left corner image
     */
    public static final int BOTTOM_LEFT_CORNER_IMAGE = 5;
    /**
     * Top right corner image
     */
    public static final int TOP_RIGHT_CORNER_IMAGE = 6;
    /**
     * Bottom right corner image
     */
    public static final int BOTTOM_RIGHT_CORNER_IMAGE = 7;
    /**
     * Create new shortcut
     */
    public static final String CREATE_SHORTCUT = "CREATE_SHORTCUT";
    /**
     * Grafient fill of background
     */
    public static final String GRADIENT_FILL = "GRADIENT_FILL";
    /**
     * Paste icons
     */
    public static final String PASTE = "PASTE";
    /**
     * Select all icons
     */
    public static final String SELECT_ALL = "SELECT_ALL";
    /**
     * Select all icons
     */
    public static final String BACKGROUND = "BACKGROUND";
    /**
     * Close all windows
     */
    public static final String CLOSE_ALL_WINDOWS = "CLOSE_ALL_WINDOWS";
    /**
     * Show or hide cover
     */
    public static final String SWITCH_COVER = "SWITCH_COVER";
    /**
     * Choose background image
     */
    public static final String CHOOSE_BACKGROUND_IMAGE = "CHOOSE_BACKGROUND_IMAGE";
    /**
     * Icon on NORTH
     */
    private static final int ICON_ON_NORTH = 0;
    /**
     * Icon on SOUTH
     */
    private static final int ICON_ON_SOUTH = 1;
    /**
     * Icon on WEST
     */
    private static final int ICON_ON_WEST = 2;
    /**
     * Icon on EAST
     */
    private static final int ICON_ON_EAST = 3;
    /**
     * Top Left Icon
     */
    private static final int TOP_LEFT_ICON = 0;
    /**
     * Top Right Icon
     */
    private static final int TOP_RIGHT_ICON = 1;
    /**
     * Bottom Left Icon
     */
    private static final int BOTTOM_LEFT_ICON = 2;
    /**
     * Bottom Right Icon
     */
    private static final int BOTTOM_RIGHT_ICON = 3;
    /**
     * Frame offset
     */
    private static int FRAME_OFFSET = 20;
    /**
     * Image ICON - desktop wallpaper.
     */
    protected ImageIcon cover = null;
    /**
     * Path to image
     */
    protected String path_to_image = null;
    /**
     * List of desktopIcons.
     */
    private Vector desktopIcons = new Vector();
    /**
     * Selector frame.
     */
    private transient GlassDragPane selectPane = new GlassDragPane();
    /**
     * Starting selection point.
     */
    private Point startingSelection = new Point();
    /**
     * 2nd color of desktop.
     */
    private Color bg_color_2 = UIManager.getColor("desktop");
    /**
     * Ending selection point.
     */
    private Point endingSelection = new Point();
    /**
     * Coordinates of top left menu corner.
     */
    private Point menuLeftTopCorner = new Point();
    /**
     * Desktop Icons group.
     */
    private transient DesktopIconsGroup iconGroup = null;
    /**
     * Desktop currently dragging icons.
     */
    private boolean isDraggingState = false;
    /**
     * Desktop popup menu.
     */
    private DesktopMenu desktop_popup_menu = new DesktopMenu(this);
    /**
     * Desktop menu
     */
    private JMenu desktop_menu = null;
    /**
     * Counters for tiling desktop windows.
     */
    private transient int hpos = 0;
    private transient int hstep = 50;
    private transient int vpos = 0;
    private transient int vstep = 50;
    /**
     * Gradient fill flag.
     */
    private boolean gradientFill = false;
    /**
     * Current image render mode
     */
    private int render_mode = 2;
    /**
     * Image cover is visible?
     */
    private boolean coverVisible = true;
    /**
     * New desktop manager
     */
    private XDesktopManager manager;
    /**
     * Save path. Relative to user.home
     */
    private String path = "";

    /**
     * Java Desktop constructor.
     */
    public Desktop() {
        super();

        addMouseListener(this);
        addMouseMotionListener(this);
        setLayout(new Layout());
        addKeyListener(new DesktopKeyAdapter());
        setName(LangResource.getString("Desktop.defaultName"));
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        /**
         * Install desktop manager
         */
        manager = new XDesktopManager(this);
        setDesktopManager(manager);

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    /**
     * Java Desktop constructor.
     */
    public Desktop(String desktopTitle) {
        this();
        setName(desktopTitle);
    }

    /**
     * Desktop activated or deactivated
     */
    public void activated(boolean flag) {
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(Desktop.GRADIENT_FILL)) {
            gradientFill = !gradientFill;
            repaint();
        } else if (command.equals(Desktop.CREATE_SHORTCUT)) {
            DesktopIcon icon = new DesktopIcon(
                LangResource.getString("Desktop.defaultIconName"),
                LangResource.getString("Desktop.defaultIconCommand"),
                "", this, null);
            DesktopIconDialog dlg =
                new DesktopIconDialog(Workspace.getUI().getFrame());

            dlg.setData(icon);
            dlg.setVisible(true);

            if (!dlg.isCancelled()) {
                icon.setXPos(50);
                icon.setYPos(100);
                addDesktopIcon(icon);
            }
        } else if (command.equals(Desktop.BACKGROUND)) {
            if (!isGradientFill()) {
                Color color = JColorChooser.showDialog(Workspace.getUI().getFrame(),
                    LangResource.getString("Desktop.chooseBg.title"),
                    getBackground());

                if (color != null) {
                    setBackground(color);
                    bg_color_2 = color;
                    repaint();
                }
            } else {
                Color color1 = JColorChooser.showDialog(Workspace.getUI().getFrame(),
                    LangResource.getString("Desktop.chooseBg1.title"),
                    getBackground());
                Color color2 = JColorChooser.showDialog(Workspace.getUI().getFrame(),
                    LangResource.getString("Desktop.chooseBg2.title"),
                    bg_color_2);
                if (color1 != null) {
                    setBackground(color1);
                }
                if (color2 != null) {
                    bg_color_2 = color2;
                }
                repaint();
            }
        } else if (command.equals(Desktop.PASTE)) {
            pasteIcons();
        } else if (command.equals(Desktop.SELECT_ALL)) {
            selectAll();
        } else if (command.equals(Desktop.CLOSE_ALL_WINDOWS)) {
            this.closeAllFrames();
        } else if (command.equals(Desktop.SWITCH_COVER)) {
            setCoverVisible(!coverVisible);
            repaint();
        } else if (command.equals(Desktop.CHOOSE_BACKGROUND_IMAGE)) {
            JFileChooser fch = ClassCache.
                getIconChooser();
            if (fch.showOpenDialog(Workspace.getUI().getFrame())
                != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File imf = fch.getSelectedFile();
            if (imf != null) {
                String test_path = imf.getAbsolutePath();
                ImageIcon test_cover = null;
                try {
                    test_cover = new ImageIcon(Imaging.getBufferedImage(imf));

                    if (test_cover.getIconHeight() != -1 && test_cover.getIconWidth() != -1) {
                        path_to_image = test_path;
                        cover = test_cover;
                        repaint();
                    }
                } catch (ImageReadException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Create and add desktop ICON to desktop.
     *
     * @param name        The name of desktop ICON
     * @param commandLine Command, that will this desktop
     *                    ICON launch, depending on execution mode.
     * @param mode        Execution mode can be the follows
     *                    <ol>
     *                    <li>SCRIPTED_METHOD_MODE = 0 - Executes scripted BSH method;
     *                    <li>SCRIPTED_FILE_MODE = 1 - Executes scripted BSH file;
     *                    <li>NATIVE_COMMAND_MODE = 2 - Executes native command;
     *                    <li>JAVA_APP_MODE = 3 - Executes installed application;
     *                    </li>
     *                    </ol>
     * @param icon        Picture for this desktop ICON
     * @param xPos        Initial x coordinate of desktop ICON
     * @param yPos        Initial y coordinate of desktop ICON
     */
    public void addDesktopIcon(String name,
                               String commandLine, String working_dir,
                               int mode, ImageIcon icon,
                               int xPos, int yPos) {
        DesktopIcon desktopIcon = new DesktopIcon(name,
            commandLine, working_dir, this, icon);
        desktopIcons.addElement(desktopIcon);
        desktopIcon.setXPos(xPos);
        desktopIcon.setMode(mode);
        desktopIcon.setYPos(yPos);
        desktopIcon.add();
        validate();
        repaint();
    }

    /**
     * Add desktop ICON to desktop.
     */
    public void addDesktopIcon(DesktopIcon icon) {
        desktopIcons.addElement(icon);
        icon.add();
        validate();
        repaint();
    }

    /**
     * Adds view to desktop and nest it into JInternalFrame.
     * Desktop places each new view into JInternalFrame.
     * There can be only one frame with given title by now.
     */
    public void addView(javax.swing.JComponent panel,
                        boolean displayImmediately, boolean unique) {
        if (panel.getName() == null) {
            panel.setName(LangResource.getString("Desktop.defaultName"));
        }

        JInternalFrame existing = findFrame(panel.getName());
        if (unique && existing != null) {
            getDesktopManager().activateFrame(existing);
            return;
        }
        /**
         * If JComponent instance of internal frame,
         * do not create internal frame.
         */

        if (!(panel instanceof JInternalFrame)) {
            JInternalFrame nest = new JInternalFrame(panel.getName(),
                true, true, true, true);
            add(nest);
            nest.getContentPane().add(panel);

            /**
             * Check if window is fully visible
             */
            if (hpos + hstep + 500 > getWidth()) {
                hpos = 50;
            } else {
                hpos += vstep;
            }

            if (vpos + vstep + 400 > getHeight()) {
                vpos = 50;
            } else {
                vpos += vstep;
            }

            if (nest.getWidth() == 0 || nest.getHeight() == 0) {
                nest.setBounds(hpos, vpos, 500, 400);
            } else {
                nest.setLocation(hpos, vpos);
            }

            nest.setVisible(true);
            getDesktopManager().activateFrame(nest);
        } else {
            JInternalFrame nest = (JInternalFrame) panel;
            add(nest);

            /*
             * Check if window is fully visible
             */
            if (hpos + hstep + 500 > getWidth()) {
                hpos = 50;
            } else {
                hpos += vstep;
            }

            if (vpos + vstep + 400 > getHeight()) {
                vpos = 50;
            } else {
                vpos += vstep;
            }

            if (nest.getWidth() == 0 || nest.getHeight() == 0) {
                nest.setBounds(hpos, vpos, 500, 400);
            } else {
                nest.setLocation(hpos, vpos);
            }

            nest.setVisible(true);
            getDesktopManager().activateFrame(nest);
        }
    }

    /**
     * Close all internal windows on desktop
     */
    public void closeAllFrames() {
        JInternalFrame[] internalFrames = getAllFrames();

        for (int i = 0; i < internalFrames.length; i++) {
            getDesktopManager().closeFrame(internalFrames[i]);
        }
    }

    /**
     * Get menu for desktop
     */
    public JMenu[] getMenu() {
        if (desktop_menu == null) {
            desktop_menu = new JMenu(LangResource.getString("Desktop.menu"));
            desktop_menu.setMnemonic
                (LangResource.getString("Desktop.mnemonic").charAt(0));
            desktop_menu.addMenuListener(new MenuListener() {
                /**
                 * Invoked when a menu is selected.
                 *
                 * @param e  a MenuEvent object
                 */
                public void menuSelected(MenuEvent e) {
                    desktop_menu.removeAll();
                    desktop_menu.add(desktop_popup_menu.create_shortcut);
                    desktop_menu.add(desktop_popup_menu.gradient_fill);
                    desktop_menu.add(desktop_popup_menu.paste);

                    desktop_menu.addSeparator();

                    desktop_menu.add(desktop_popup_menu.select_all);
                    desktop_menu.add(desktop_popup_menu.background);
                    desktop_menu.add(desktop_popup_menu.choose_bg_image);
                    desktop_menu.add(desktop_popup_menu.switch_cover);

                    desktop_menu.addSeparator();

                    desktop_menu.add(desktop_popup_menu.close_all_windows);

                    updateMenuItems();
                }

                /**
                 * Invoked when the menu is deselected.
                 *
                 * @param e  a MenuEvent object
                 */
                public void menuDeselected(MenuEvent e) {
                }

                /**
                 * Invoked when the menu is canceled.
                 *
                 * @param e  a MenuEvent object
                 */
                public void menuCanceled(MenuEvent e) {
                }
            });
            UIManager.addPropertyChangeListener(new UISwitchListener(desktop_menu));
        }
        return new JMenu[]{desktop_menu};
    }

    /**
     * Get option panel for desktop
     */
    public JPanel[] getOptionPanels() {
        return new KPanel[]{new DesktopBackgroundPanel(this),
            new DesktopOptionsPanel(this)};
    }

    /**
     * Copy selected icons to clipboard
     */
    public void copyIcons() {
        Vector selectedIconsData = new Vector();

        for (int i = 0; i < desktopIcons.size(); i++) {
            DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
            if (icon.isSelected()) {
                DesktopIconData data = icon.getIconData();
                selectedIconsData.addElement(data);
            }
        }
        DesktopIconData[] iconData = new DesktopIconData[selectedIconsData.size()];

        selectedIconsData.copyInto(iconData);

        DesktopIconSelectionData selectionData =
            new DesktopIconSelectionData(selectedIconsData.size(), iconData);

        Workspace.getUI().getClipboard().setContents(new DesktopIconSelection(selectionData), this);
    }

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create() {
    }

    /**
     * Cut selected desktop icons
     */
    public void cutIcons() {
        copyIcons();
        removeSelectedIcons();
    }

    /**
     * Deselect all icons
     */
    public void deselectAll() {
        for (int i = 0; i < desktopIcons.size(); i++) {
            ((DesktopIcon) desktopIcons.elementAt(i)).setSelected(false);
        }
        validate();
        repaint();
    }

    /**
     * Find frame with specified title.
     */
    public JInternalFrame findFrame(String title) {
        JInternalFrame[] internalFrames = getAllFrames();

        for (int i = 0; i < internalFrames.length; i++) {
            if (internalFrames[i].getTitle().equals(title)) {
                return internalFrames[i];
            }
        }

        return null;
    }

    /**
     * Returns background image of current desktop
     */
    public ImageIcon getCover() {
        if (cover == null) {
            cover = loadCover();
        }
        return cover;
    }

    /**
     * Sets cover image for desktop
     */
    public void setCover(String path) {
        path_to_image = path;
        this.cover = loadCover();
    }

    /**
     * Loads cover
     */
    protected ImageIcon loadCover() {
        if (path_to_image != null) {
            // cover = new ImageIcon(Jimi.getImage(path_to_image));
        }
        return cover;
    }

    /**
     * Get contrast color for desktop selection
     * borders and other marker components.
     */
    public Color getSelectionColor() {
        float[] comp = getBackground().getColorComponents(null);

        if (Math.sqrt(comp[1] * comp[1] + comp[2] * comp[2]) < 0.7) {
            return Color.white;
        } else {
            return Color.black;
        }
    }

    /**
     * Returns second color of gradient fill
     */
    public Color getSecondBackground() {
        return bg_color_2;
    }

    /**
     * Sets second color of gradient fill
     */
    public void setSecondBackground(Color color) {
        bg_color_2 = color;
    }

    /**
     * Returns desktop icons.
     */
    public DesktopIcon[] getDesktopIcons() {
        Object[] ret_obj = desktopIcons.toArray();
        DesktopIcon[] ret = new DesktopIcon[ret_obj.length];
        System.arraycopy(ret_obj, 0, ret, 0, ret_obj.length);
        return ret;
    }

    /**
     * Returns current desktop menu
     */
    public JPopupMenu getDesktopMenu() {
        return desktop_popup_menu;
    }

    /**
     * Returns group of currently selected icons.
     */
    protected DesktopIconsGroup getIconGroup() {
        if (iconGroup == null) {
            iconGroup = new DesktopIconsGroup();
        }
        return iconGroup;
    }

    /**
     * Sets desktop group of icons.
     */
    protected void setIconGroup(DesktopIconsGroup iconGroup) {
        this.iconGroup = iconGroup;
    }

    /**
     * Get rendering mode of background image
     */
    public int getRenderMode() {
        return render_mode;
    }

    /**
     * Set render mode
     */
    public void setRenderMode(int mode) {
        if (mode != Desktop.CENTER_IMAGE &&
            mode != Desktop.TILE_IMAGE &&
            mode != Desktop.STRETCH_IMAGE &&
            mode != Desktop.TOP_LEFT_CORNER_IMAGE &&
            mode != Desktop.TOP_RIGHT_CORNER_IMAGE &&
            mode != Desktop.BOTTOM_LEFT_CORNER_IMAGE &&
            mode != Desktop.BOTTOM_RIGHT_CORNER_IMAGE) {
            throw new IllegalArgumentException("Illegal render mode");
        }

        render_mode = mode;
        repaint();
    }

    /**
     * Returns relative path for saving component data.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path for saving component data.
     * This path is relative to user.home path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Calculates vector which poins direction for desktop
     * icons dragging.
     */
    private Point getTransferVector(int xshift, int yshift) {
        Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point bottomRight = new Point(0, 0);

        for (int i = 0; i < desktopIcons.size(); i++) {
            DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
            if (icon.isSelected()) {
                Point location = icon.getLocation();

                topLeft.x = Utils.min(topLeft.x, location.x +
                    getParent().getLocation().x + getLocation().x);
                topLeft.y = Utils.min(topLeft.y, location.y +
                    getParent().getLocation().y + getLocation().y);

                bottomRight.x = Utils.max(bottomRight.x, location.x +
                    getParent().getLocation().x + getLocation().x + icon.getWidth());
                bottomRight.y = Utils.max(bottomRight.y, location.y +
                    getParent().getLocation().y + getLocation().y + icon.getHeight());

            }
        }

        Rectangle groupBounds = new Rectangle(topLeft.x, topLeft.y,
            Utils.distance(topLeft.x, bottomRight.x),
            Utils.distance(topLeft.y, bottomRight.y));

        if (groupBounds.x + xshift < 0) {
            xshift = -groupBounds.x;
        } else if (groupBounds.x + xshift + groupBounds.width > getWidth()) {
            xshift = getWidth() - groupBounds.x - groupBounds.width;
        }

        if (groupBounds.y + yshift < 0) {
            yshift = -groupBounds.y;
        } else if (groupBounds.y + yshift + groupBounds.height > getHeight()) {
            yshift = getHeight() - groupBounds.y - groupBounds.height;
        }

        return new Point(xshift, yshift);
    }

    /**
     * Calculates vector which poins direction for desktop
     * icons dragging.
     */
    private Point getTransferVector(int finalx, int finaly, DesktopIconSelectionData icons) {
        Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point bottomRight = new Point(0, 0);

        for (int i = 0; i < icons.getSize().intValue(); i++) {
            DesktopIconData iconData = icons.getIconData()[i];

            int xlocation = iconData.getXPos().intValue();
            int ylocation = iconData.getYPos().intValue();

            topLeft.x = Utils.min(topLeft.x, xlocation +
                getParent().getLocation().x + getLocation().x);
            topLeft.y = Utils.min(topLeft.y, ylocation +
                getParent().getLocation().y + getLocation().y);

            bottomRight.x = Utils.max(bottomRight.x, xlocation +
                getParent().getLocation().x + getLocation().x + iconData.getWidth().intValue());
            bottomRight.y = Utils.max(bottomRight.y, ylocation +
                getParent().getLocation().y + getLocation().y + iconData.getHeight().intValue());
        }

        Rectangle groupBounds = new Rectangle(topLeft.x, topLeft.y,
            Utils.distance(topLeft.x, bottomRight.x),
            Utils.distance(topLeft.y, bottomRight.y));

        if (finalx + groupBounds.width > getWidth()) {
            finalx = getWidth() - groupBounds.width;
        }

        if (finaly + groupBounds.height > getHeight()) {
            finaly = getHeight() - groupBounds.height;
        }

        return new Point(finalx - groupBounds.x, finaly - groupBounds.y);
    }

    /**
     * Is desktop currently drags icons?
     */
    public boolean isDraggingState() {
        return this.isDraggingState;
    }

    /**
     * Sets dragging state for desktop
     */
    public void setDraggingState(boolean state) {
        this.isDraggingState = state;
        if (!state) {
            iconGroup = null;
        }
    }

    /**
     * Is gradient filled desktop
     */
    public boolean isGradientFill() {
        return gradientFill;
    }

    /**
     * Sets flag if desktop uses gradient fill for painting
     * background.
     */
    public void setGradientFill(boolean gradientFill) {
        this.gradientFill = gradientFill;
    }

    /**
     * Is cover visible
     */
    public boolean isCoverVisible() {
        return coverVisible;
    }

    /**
     * Sets visibility of cover
     */
    public void setCoverVisible(boolean coverVisible) {
        this.coverVisible = coverVisible;
    }

    /**
     * Is group selected.
     */
    protected boolean isGroupSelected() {
        int size = 0;
        for (int i = 0; i < desktopIcons.size(); i++) {
            if (((DesktopIcon) desktopIcons.elementAt(i)).isSelected()) {
                size++;
            }
        }

        return size > 1;
    }

    /**
     * Count selected icons.
     */
    protected DesktopIcon[] getSelectedIcons() {
        Vector temp = new Vector();
        for (int i = 0; i < desktopIcons.size(); i++) {
            if (((DesktopIcon) desktopIcons.elementAt(i)).isSelected()) {
                temp.addElement(desktopIcons.elementAt(i));
            }
        }

        DesktopIcon[] arr = new DesktopIcon[temp.size()];
        temp.copyInto(arr);
        return arr;
    }

    /**
     * Returns true, if some data is left in
     * script editors and should be saved. This
     * method seeks for isModified() method of
     * any internal window within this desktop.
     */
    public boolean isModified() {
        JInternalFrame[] internalFrames = getAllFrames();

        for (int i = 0; i < internalFrames.length; i++) {
            try {
                Method mod_meth = internalFrames[i].getClass().getMethod("isModified");
                Object result = mod_meth.invoke(internalFrames[i]);
                if (result instanceof Boolean &&
                    ((Boolean) result).booleanValue() == true) {
                    return true;
                }
            } catch (Exception ex) {
                // no method found
            }
        }
        return false;
    }

    /**
     * Returns true, if argument desktop ICON is
     * selected with group.
     */
    public boolean isSelectedInGroup(DesktopIcon icon) {
        return isGroupSelected() && icon.isSelected();
    }

    /**
     * Set this flag to true, if you want component
     * to be unique among all workspace views.
     * This component will be registered.
     *
     * @return boolean
     */
    public boolean isUnique() {
        return false;
    }

    /**
     * Loads desktop data.
     */
    public void load() {
        String fileName = Workspace.getUserHome() + getPath() + File.separator + "desktop.dat";
        Workspace.getLogger().info(">" + "Reading file" + " " + fileName + "...");
        try {
            FileInputStream inputFile = new FileInputStream(fileName);
            ObjectInputStream dataStream = new ObjectInputStream(inputFile);

            this.setName(dataStream.readUTF());

            int red = dataStream.readInt();
            int green = dataStream.readInt();
            int blue = dataStream.readInt();
            setBackground(new Color(red, green, blue));

            red = dataStream.readInt();
            green = dataStream.readInt();
            blue = dataStream.readInt();
            this.bg_color_2 = new Color(red, green, blue);

            this.gradientFill = dataStream.readBoolean();
            this.setOpaque(dataStream.readBoolean());
            this.hpos = dataStream.readInt();
            this.hstep = dataStream.readInt();
            this.render_mode = dataStream.readInt();
            this.vpos = dataStream.readInt();
            this.vstep = dataStream.readInt();
            this.coverVisible = dataStream.readBoolean();
            int size = dataStream.readInt();

            for (int i = 0; i < size; i++) {
                DesktopIcon icon = new DesktopIcon(this);
                icon.load(dataStream);
                this.addDesktopIcon(icon);
            }
            path_to_image = dataStream.readUTF();

            if (path_to_image == null || path_to_image.trim().equals("")
                || !(new File(path_to_image).exists())) {
                path_to_image = null;
            }

            boolean outline = dataStream.readBoolean();

            if (outline) {
                setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            } else {
                setDragMode(JDesktopPane.LIVE_DRAG_MODE);
            }
        } catch (Exception ex) {
            WorkspaceError.exception
                (LangResource.getString("Desktop.load.failed"), ex);
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        endingSelection.x = e.getX();
        endingSelection.y = e.getY();

        selectPane.setBounds(Utils.min(startingSelection.x, endingSelection.x),
            Utils.min(startingSelection.y, endingSelection.y),
            Utils.distance(endingSelection.x, startingSelection.x),
            Utils.distance(endingSelection.y, startingSelection.y));
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        deselectAll();
        startingSelection.x = e.getX();
        startingSelection.y = e.getY();
        /**
         * Hides desktop menu and makes
         * selection rectangle.
         */
        if (SwingUtilities.isLeftMouseButton(e) ||
            e.isControlDown()) {
            if (getDesktopMenu() != null) {
                ((Desktop) e.getComponent()).remove(getDesktopMenu());
                getDesktopMenu().setVisible(false);
            }

            setLayer(selectPane, Integer.MAX_VALUE);
            selectPane.setBounds(startingSelection.x,
                startingSelection.y, 0, 0);
            selectPane.setColor(getSelectionColor());
            add(selectPane);
        }
    }

    public void mouseReleased(MouseEvent e) {
        requestFocus();

        endingSelection.x = e.getX();
        endingSelection.y = e.getY();
        /**
         * Show desktop menu
         */
        if (SwingUtilities.isRightMouseButton(e) ||
            e.isControlDown()) {
            if (getDesktopMenu() != null) {
                ((Desktop) e.getComponent()).add(getDesktopMenu());
                getDesktopMenu().show(this, e.getX(), e.getY());
            }

            menuLeftTopCorner.x = e.getX() + getX();
            menuLeftTopCorner.y = e.getY() + getY();
        }
        /**
         * Select by rectangle.
         */
        else if (SwingUtilities.isLeftMouseButton(e) ||
            e.isControlDown()) {
            remove(selectPane);
            rectSelector();
            validate();
            repaint();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isGradientFill() && isOpaque()) {
            Graphics2D g2 = (Graphics2D) g;
            int w = getSize().width;
            int h = getSize().height;
            g2.setPaint(new GradientPaint(0, 0, getBackground(), 0, h, bg_color_2));
            g2.fill(new Rectangle(0, 0, w, h));
        }
        if (getCover() != null && coverVisible) {
            /**
             * Drawing of desktop image can occur in several rendering modes.
             */
            if (render_mode == Desktop.CENTER_IMAGE) {
                g.drawImage(getCover().getImage(), (getWidth() - getCover().getIconWidth()) / 2,
                    (getHeight() - getCover().getIconHeight()) / 2, this);
            } else if (render_mode == Desktop.STRETCH_IMAGE) {
                g.drawImage(getCover().getImage(), 0, 0, getWidth(), getHeight(), this);
            } else if (render_mode == Desktop.TILE_IMAGE) {
                int x = 0, y = 0;
                while (x < getWidth()) {
                    while (y < getHeight()) {
                        g.drawImage(getCover().getImage(), x, y, this);
                        y += getCover().getIconHeight();
                    }
                    x += getCover().getIconWidth();
                    y = 0;
                }
            } else if (render_mode == Desktop.TOP_LEFT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), 0, 0, this);
            } else if (render_mode == Desktop.BOTTOM_LEFT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), 0, this.getHeight() - getCover().getIconHeight(), this);
            } else if (render_mode == Desktop.TOP_RIGHT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), this.getWidth() - getCover().getIconWidth(), 0, this);
            } else if (render_mode == Desktop.BOTTOM_RIGHT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), this.getWidth() - getCover().getIconWidth(),
                    this.getHeight() - getCover().getIconHeight(), this);
            }
        }
    }

    /**
     * Paste desktop icons from clipboard
     */
    public void pasteIcons() {
        try {
            Transferable contents = Workspace.getUI().getClipboard().getContents(this);
            if (contents == null) {
                return;
            }

            DesktopIconSelectionData iconSelectionData = (DesktopIconSelectionData)
                contents.getTransferData(DesktopIconSelection.DesktopIconFlavor);

            int xShift = menuLeftTopCorner.x;
            int yShift = menuLeftTopCorner.y;

            Point point = getTransferVector(xShift, yShift, iconSelectionData);

            xShift = point.x;
            yShift = point.y;

            for (int i = 0; i < iconSelectionData.getSize().intValue(); i++) {
                DesktopIconData iconData = iconSelectionData.getIconData()[i];

                addDesktopIcon(iconData.getName(), iconData.getCommandLine(),
                    iconData.getWorkingDir(),
                    iconData.getMode(), iconData.getIcon(),
                    iconData.getXPos().intValue() + xShift,
                    iconData.getYPos().intValue() + yShift);
            }

            validate();
            repaint();
        } catch (IOException e) {
            WorkspaceError.exception
                (LangResource.getString("Desktop.pasteIcons.failed"), e);
            return;
        } catch (UnsupportedFlavorException e) {
            WorkspaceError.exception
                (LangResource.getString("Desktop.pasteIcons.failed"), e);
            return;
        }
    }

    /**
     * Select dektop icons by rectangular selector frame.
     */
    private void rectSelector() {
        Rectangle selector =
            new Rectangle(Utils.min(startingSelection.x, endingSelection.x),
                Utils.min(startingSelection.y, endingSelection.y),
                Utils.distance(endingSelection.x, startingSelection.x),
                Utils.distance(endingSelection.y, startingSelection.y));

        for (int i = 0; i < desktopIcons.size(); i++) {
            Rectangle iconPlace = ((DesktopIcon) desktopIcons.elementAt(i)).getBounds();
            if (iconPlace.intersects(selector)) {
                ((DesktopIcon) desktopIcons.elementAt(i)).setSelected(true);
            }
        }
    }

    /**
     * Key navigation handler. Select ICON depending
     * on argument cursor ICON.
     */
    protected void selectNextIcon(int direction, DesktopIcon cursor_icon) {
        /**
         * Get currently selected icons.
         */
        DesktopIcon[] selected_icons = getSelectedIcons();
        /**
         * If no icons are selected, select the top left ICON
         * on this desktop.
         */
        if (selected_icons.length == 0) {
            DesktopIcon top_left = getAtCorner(Desktop.TOP_LEFT_ICON);
            if (top_left != null) {
                top_left.setSelected(true);
                top_left.requestFocus();
            }
            return;
        }
        /**
         * The desktop ICON, nearest to ours.
         */
        DesktopIcon min = null;
        /**
         * Declare point to min ICON
         */
        Point min_icon_point = null;
        /**
         * Find cursor ICON coordinates
         */
        Point cursor_icon_point =
            new Point(cursor_icon.getXPos(), cursor_icon.getYPos());
        /**
         * Iterate throught all icons and find
         * closest. If there is only one ICON,
         * return it as a closest to itself.
         */
        for (int i = 0; i < desktopIcons.size(); i++) {
            /**
             * Take new desktop ICON
             */
            DesktopIcon current_icon = (DesktopIcon) desktopIcons.elementAt(i);
            /**
             * If this is a cursor ICON, continue
             */
            if (current_icon.equals(cursor_icon)) {
                continue;
            }
            /**
             * Measure its coordinates
             */
            Point current_icon_point = new Point(current_icon.getXPos(), current_icon.getYPos());
            /**
             * Divide all icons into groups
             */
            if ((direction == Desktop.ICON_ON_NORTH &&
                Utils.isNorthernQuadrant(cursor_icon_point, current_icon_point))
                || (direction == Desktop.ICON_ON_SOUTH &&
                Utils.isSouthernQuadrant(cursor_icon_point, current_icon_point))
                || (direction == Desktop.ICON_ON_EAST &&
                Utils.isEasternQuadrant(cursor_icon_point, current_icon_point))
                || (direction == Desktop.ICON_ON_WEST &&
                Utils.isWesternQuadrant(cursor_icon_point, current_icon_point))) {
                /**
                 * Min is becoming the first ICON in quadrant
                 */
                if (min_icon_point == null) {
                    min_icon_point = current_icon_point;
                }

                if (Utils.distance(cursor_icon_point, min_icon_point)
                    >= Utils.distance(cursor_icon_point, current_icon_point)) {
                    min = current_icon;
                    min_icon_point = current_icon_point;
                }
            }
        }
        /**
         * Finally, select chosen ICON
         */
        if (min != null) {
            deselectAll();
            min.setSelected(true);
            min.requestFocus();
        }
    }

    /**
     * Key navigation handler. Select ICON depending
     * on argument cursor ICON.
     */
    protected void selectNextIcon(int direction) {
        /**
         * Get selected ICON and make it start one.
         */
        selectNextIcon(direction, getAtCorner(Desktop.TOP_RIGHT_ICON));
    }

    /**
     * Get corner icons
     */
    private DesktopIcon getAtCorner(int corner) {
        DesktopIcon res = null;
        for (int i = 0; i < desktopIcons.size(); i++) {
            DesktopIcon desktop_icon = (DesktopIcon) desktopIcons.elementAt(i);
            switch (corner) {
                case Desktop.TOP_LEFT_ICON:
                    if (res == null) {
                        res = desktop_icon;
                    } else if (desktop_icon.getXPos() < res.getXPos()
                        && desktop_icon.getYPos() < res.getYPos()) {
                        res = desktop_icon;
                    }
                    break;
                case Desktop.TOP_RIGHT_ICON:
                    if (res == null) {
                        res = desktop_icon;
                    } else if (desktop_icon.getXPos() > res.getXPos()
                        && desktop_icon.getYPos() < res.getYPos()) {
                        res = desktop_icon;
                    }
                    break;
                case Desktop.BOTTOM_LEFT_ICON:
                    if (res == null) {
                        res = desktop_icon;
                    } else if (desktop_icon.getXPos() < res.getXPos()
                        && desktop_icon.getYPos() > res.getYPos()) {
                        res = desktop_icon;
                    }
                    break;
                case Desktop.BOTTOM_RIGHT_ICON:
                    if (res == null) {
                        res = desktop_icon;
                    } else if (desktop_icon.getXPos() > res.getXPos()
                        && desktop_icon.getYPos() > res.getYPos()) {
                        res = desktop_icon;
                    }
                    break;
            }
        }
        return res;
    }

    /**
     * Delete selected icons
     */
    public void removeSelectedIcons() {
        DesktopIcon[] irem = getSelectedIcons();

        if (irem.length == 1) {
            if (JOptionPane.showConfirmDialog(Workspace.getUI().getFrame(),
                LangResource.getString("Desktop.removeIcon.question")
                    + " \"" + irem[0].getName() + "\"?",
                LangResource.getString("Desktop.removeIcon.title"),
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        } else if (irem.length > 1) {
            if (JOptionPane.showConfirmDialog(Workspace.getUI().getFrame(),
                LangResource.getString("Desktop.removeIcons.question")
                    + " " + irem.length + " " +
                    LangResource.getString("Desktop.countIcon") + "?",
                LangResource.getString("Desktop.removeIcons.title"),
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        for (int i = 0; i < irem.length; i++) {
            desktopIcons.removeElement(irem[i]);
            irem[i].remove();
        }
        validate();
        repaint();
    }

    /**
     * Reset desktop by closing all frames
     */
    public void reset() {
        for (int i = 0; i < desktopIcons.size(); i++) {
            ((DesktopIcon) desktopIcons.elementAt(i)).remove();
        }
        desktopIcons.removeAllElements();
        validate();
        repaint();
        closeAllFrames();
    }

    /**
     * Write out all desktop data.
     */
    public void save() {
        /**
         * Save desktop title.
         */
        if (getName() == null) {
            setName(LangResource.getString("Desktop.defaultName"));
        }

        String fileName = Workspace.getUserHome() + getPath()
            + File.separator + "desktop.dat";
        Workspace.getLogger().info(">" + "Writing file" + " " + fileName + "...");

        File file = new File(Workspace.getUserHome() + getPath());

        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            FileOutputStream outputFile = new FileOutputStream(fileName);
            ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);

            outputStream.writeUTF(getName());

            outputStream.writeInt(getBackground().getRed());
            outputStream.writeInt(getBackground().getGreen());
            outputStream.writeInt(getBackground().getBlue());

            outputStream.writeInt(bg_color_2.getRed());
            outputStream.writeInt(bg_color_2.getGreen());
            outputStream.writeInt(bg_color_2.getBlue());

            outputStream.writeBoolean(gradientFill);
            outputStream.writeBoolean(isOpaque());
            outputStream.writeInt(hpos);
            outputStream.writeInt(hstep);
            outputStream.writeInt(render_mode);
            outputStream.writeInt(vpos);
            outputStream.writeInt(vstep);
            outputStream.writeBoolean(coverVisible);
            outputStream.writeInt(desktopIcons.size());

            java.util.Iterator i = desktopIcons.iterator();
            /**
             * Save desktop icons.
             */
            while (i.hasNext()) {
                ((DesktopIcon) i.next()).save(outputStream);
            }
            if (path_to_image != null) {
                outputStream.writeUTF(path_to_image);
            } else {
                outputStream.writeUTF("");
            }

            if (getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE) {
                outputStream.writeBoolean(true);
            } else {
                outputStream.writeBoolean(false);
            }

            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            WorkspaceError.exception
                (LangResource.getString("Desktop.save.failed"), ex);
        }
    }

    /**
     * Select all desktop icons
     */
    public void selectAll() {
        for (int i = 0; i < desktopIcons.size(); i++) {
            ((DesktopIcon) desktopIcons.elementAt(i)).setSelected(true);
        }
        validate();
        repaint();
    }

    /**
     * Update current view
     */
    public void update() {
        this.revalidate();
        this.repaint();
    }

    /**
     * Update menu items, then menu becomes visible
     */
    protected void updateMenuItems() {
        if (gradientFill) {
            desktop_popup_menu.gradient_fill.
                setText(LangResource.getString("Desktop.menu.hideGradient"));
        } else {
            desktop_popup_menu.gradient_fill.
                setText(LangResource.getString("Desktop.menu.showGradient"));
        }
        if (getCover() == null) {
            desktop_popup_menu.switch_cover.setEnabled(false);
            desktop_popup_menu.switch_cover.
                setText(LangResource.getString("Desktop.menu.noCover"));
        } else {
            desktop_popup_menu.switch_cover.setEnabled(true);
            if (isCoverVisible()) {
                desktop_popup_menu.switch_cover.
                    setText(LangResource.getString("Desktop.menu.hideCover"));
            } else {
                desktop_popup_menu.switch_cover.
                    setText(LangResource.getString("Desktop.menu.showCover"));
            }
        }
        Transferable contents = Workspace.getUI().getClipboard().getContents(this);
        if (contents == null) {
            desktop_popup_menu.paste.setEnabled(false);
        } else if (contents instanceof DesktopIconSelection) {
            desktop_popup_menu.paste.setEnabled(true);
        } else {
            desktop_popup_menu.paste.setEnabled(false);
        }
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        checkDesktopSize();
    }

    public Component add(JInternalFrame frame) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        int w;
        int h;

        Component retval = super.add(frame);
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        } else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);
        if (frame.isResizable()) {
            w = getWidth() - (getWidth() / 3);
            h = getHeight() - (getHeight() / 3);
            if (w < frame.getMinimumSize().getWidth()) {
                w = (int) frame.getMinimumSize().getWidth();
            }
            if (h < frame.getMinimumSize().getHeight()) {
                h = (int) frame.getMinimumSize().getHeight();
            }
            frame.setSize(w, h);
        }
        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            frame.toBack();
        }
        return retval;
    }

    public void remove(Component c) {
        super.remove(c);
        checkDesktopSize();
    }

    /**
     * Cascade all internal frames
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;
        JInternalFrame[] allFrames = getAllFrames();

        manager.setNormalSize();
        int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
        int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
        for (int i = allFrames.length - 1; i >= 0; i--) {
            allFrames[i].setSize(frameWidth, frameHeight);
            allFrames[i].setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }

    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        Component[] allFrames = getAllFrames();
        manager.setNormalSize();
        int frameHeight = getBounds().height / allFrames.length;
        int y = 0;
        for (int i = 0; i < allFrames.length; i++) {
            allFrames[i].setSize(getBounds().width, frameHeight);
            allFrames[i].setLocation(0, y);
            y = y + frameHeight;
        }
    }
//**********************************************************

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given dimension.
     */
    public void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given width and height.
     */
    public void setAllSize(int width, int height) {
        setAllSize(new Dimension(width, height));
    }

    private void checkDesktopSize() {
        if (getParent() != null && isVisible()) {
            manager.resizeDesktop();
        }
    }

    /**
     * Private class used to replace the standard DesktopManager for JDesktopPane.
     * Used to provide scrollbar functionality.
     */
    class XDesktopManager extends DefaultDesktopManager {
        private Desktop desktop;

        public XDesktopManager(Desktop desktop) {
            this.desktop = desktop;
        }

        public void endResizingFrame(JComponent f) {
            super.endResizingFrame(f);
            resizeDesktop();
        }

        public void endDraggingFrame(JComponent f) {
            super.endDraggingFrame(f);
            resizeDesktop();
        }

        public void setNormalSize() {
            JScrollPane scrollPane = getScrollPane();
            int x = 0;
            int y = 0;
            Insets scrollInsets = getScrollPaneInsets();

            if (scrollPane != null) {
                Dimension d = scrollPane.getVisibleRect().getSize();
                if (scrollPane.getBorder() != null) {
                    d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
                }
                d.setSize(d.getWidth() - 20, d.getHeight() - 20);
                desktop.setAllSize(x, y);
                scrollPane.invalidate();
                scrollPane.validate();
            }
        }

        private Insets getScrollPaneInsets() {
            JScrollPane scrollPane = getScrollPane();
            if (scrollPane == null) {
                return new Insets(0, 0, 0, 0);
            } else {
                return getScrollPane().getBorder().getBorderInsets(scrollPane);
            }
        }

        private JScrollPane getScrollPane() {
            if (desktop.getParent() instanceof JViewport) {
                JViewport viewPort = (JViewport) desktop.getParent();
                if (viewPort.getParent() instanceof JScrollPane) {
                    return (JScrollPane) viewPort.getParent();
                }
            }
            return null;
        }

        protected void resizeDesktop() {
            int x = 0;
            int y = 0;
            JScrollPane scrollPane = getScrollPane();
            Insets scrollInsets = getScrollPaneInsets();

            if (scrollPane != null) {
                JInternalFrame[] allFrames = desktop.getAllFrames();

                for (int i = 0; i < allFrames.length; i++) {
                    if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
                        x = allFrames[i].getX() + allFrames[i].getWidth();
                    }
                    if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
                        y = allFrames[i].getY() + allFrames[i].getHeight();
                    }
                }
                DesktopIcon[] allIcons = desktop.getDesktopIcons();
                for (int i = 0; i < allIcons.length; i++) {
                    if (allIcons[i].getX() + allIcons[i].getWidth() > x) {
                        x = allIcons[i].getX() + allIcons[i].getWidth();
                    }
                    if (allIcons[i].getY() + allIcons[i].getHeight() > y) {
                        y = allIcons[i].getY() + allIcons[i].getHeight();
                    }
                }
                Dimension d = scrollPane.getVisibleRect().getSize();
                if (scrollPane.getBorder() != null) {
                    d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
                }
                if (x <= d.getWidth()) {
                    x = ((int) d.getWidth()) - 20;
                }
                if (y <= d.getHeight()) {
                    y = ((int) d.getHeight()) - 20;
                }
                desktop.setAllSize(x, y);
                scrollPane.invalidate();
                scrollPane.validate();
            }
        }
    }

    /**
     * Icon group - this class manages group icons
     * operations. Inner drag panes are nessesary for
     * visual drag movement.
     */
    class DesktopIconsGroup implements Serializable {
        Vector dragPanes = new Vector();
        int xShift = 0;
        int yShift = 0;

        public DesktopIconsGroup() {
            super();
            /**
             * Create desktop icons group from selected
             * icons - this happens then
             */
            for (int i = 0; i < desktopIcons.size(); i++) {
                DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
                if (icon.isSelected()) {
                    GlassDragPane dragPane = new GlassDragPane();
                    dragPane.setColor(getSelectionColor());
                    setLayer(dragPane, Integer.MAX_VALUE - i);
                    dragPane.setBounds(icon.getX(), icon.getY(),
                        icon.getWidth(), icon.getHeight());
                    add(dragPane);
                    dragPanes.addElement(dragPane);
                }
            }
        }

        public void moveTo(int x, int y) {
            xShift = x;
            yShift = y;
            int counter = 0;
            for (int i = 0; i < desktopIcons.size(); i++) {
                DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
                if (icon.isSelected()) {
                    GlassDragPane dragPane = (GlassDragPane) dragPanes.elementAt(counter);
                    Point location = icon.getLocation();
                    dragPane.setLocation(location.x + xShift, location.y + yShift);
                    counter++;
                }
            }
        }

        public void destroy() {
            int counter = 0;
            Point point = getTransferVector(xShift, yShift);
            requestFocus();
            xShift = point.x;
            yShift = point.y;
            for (int i = 0; i < desktopIcons.size(); i++) {
                DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
                if (icon.isSelected()) {
                    GlassDragPane dragPane = (GlassDragPane) dragPanes.elementAt(counter);
                    remove(dragPane);
                    remove(icon);
                    icon.setXPos(icon.getXPos() + xShift);
                    icon.setYPos(icon.getYPos() + yShift);
                    add(icon);
                    counter++;
                }
            }
            dragPanes.removeAllElements();
            revalidate();
            repaint();
        }
    }

    /**
     * Desktop menu
     */
    class DesktopMenu extends JPopupMenu {
        public JMenuItem create_shortcut = null;
        public JMenuItem gradient_fill = null;
        public JMenuItem paste = null;
        public JMenuItem select_all = null;
        public JMenuItem background = null;
        public JMenuItem close_all_windows = null;
        public JMenuItem switch_cover = null;
        public JMenuItem choose_bg_image = null;
        ActionListener listener = null;

        public DesktopMenu(ActionListener listener) {
            super();
            this.listener = listener;

            create_shortcut = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.createShortcut") + "...",
                    Desktop.CREATE_SHORTCUT, null));

            paste = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.paste"),
                    Desktop.PASTE, null));
            paste.setAccelerator(KeyStroke.getKeyStroke
                (KeyEvent.VK_V, KeyEvent.CTRL_MASK));

            select_all = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.selectAll"),
                    Desktop.SELECT_ALL, null));
            select_all.setAccelerator(KeyStroke.getKeyStroke
                (KeyEvent.VK_A, KeyEvent.CTRL_MASK));
            this.addSeparator();
            gradient_fill = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.gradientFill"),
                    Desktop.GRADIENT_FILL, null));

            background = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.background"),
                    Desktop.BACKGROUND, null));

            choose_bg_image = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.chooseBgImage"),
                    Desktop.CHOOSE_BACKGROUND_IMAGE, null));
            switch_cover = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.switchCover"),
                    Desktop.SWITCH_COVER, null));

            this.addSeparator();

            close_all_windows = add(Utils.createMenuItem
                (listener, LangResource.getString("Desktop.menu.closeAllWindows"),
                    Desktop.CLOSE_ALL_WINDOWS, null));
            UIManager.addPropertyChangeListener(new UISwitchListener(this));
        }

        public void setVisible(boolean flag) {
            removeAll();
            add(create_shortcut);
            add(gradient_fill);
            add(paste);
            this.addSeparator();
            add(select_all);
            add(background);
            add(choose_bg_image);
            add(switch_cover);
            this.addSeparator();
            add(close_all_windows);

            updateMenuItems();

            super.setVisible(flag);
        }
    }

    /**
     * Layout class lays out desktop with icons.
     */
    class Layout implements LayoutManager, Serializable {
        public void layoutContainer(Container c) {
            for (int i = 0; i < desktopIcons.size(); i++) {
                DesktopIcon icon = (DesktopIcon) desktopIcons.elementAt(i);
                icon.setBounds(icon.getXPos(),
                    icon.getYPos(),
                    icon.getPreferredSize().width,
                    icon.getPreferredSize().height);
            }
        }

        public void addLayoutComponent(String str, Component c) {
        }

        public Dimension minimumLayoutSize(Container c) {
            return c.getPreferredSize();
        }

        public Dimension preferredLayoutSize(Container c) {
            return new Dimension(50, 50);//c.getPreferredSize();
        }

        public void removeLayoutComponent(Component c) {
        }
    }

    /**
     * Desktop key listener
     */
    protected class DesktopKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (e.getSource() instanceof DesktopIcon) {
                    selectNextIcon(Desktop.ICON_ON_NORTH, (DesktopIcon) e.getSource());
                } else {
                    selectNextIcon(Desktop.ICON_ON_NORTH);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (e.getSource() instanceof DesktopIcon) {
                    selectNextIcon(Desktop.ICON_ON_SOUTH, (DesktopIcon) e.getSource());
                } else {
                    selectNextIcon(Desktop.ICON_ON_SOUTH);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (e.getSource() instanceof DesktopIcon) {
                    selectNextIcon(Desktop.ICON_ON_WEST, (DesktopIcon) e.getSource());
                } else {
                    selectNextIcon(Desktop.ICON_ON_WEST);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (e.getSource() instanceof DesktopIcon) {
                    selectNextIcon(Desktop.ICON_ON_EAST, (DesktopIcon) e.getSource());
                } else {
                    selectNextIcon(Desktop.ICON_ON_EAST);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                removeSelectedIcons();
            } else if (e.getKeyCode() == KeyEvent.VK_V
                && e.getModifiers() == KeyEvent.CTRL_MASK) {
                if (e.getSource() instanceof Desktop) {
                    ((Desktop) e.getSource()).pasteIcons();
                } else if (e.getSource() instanceof DesktopIcon) {
                    ((DesktopIcon) e.getSource()).desktop.pasteIcons();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_A
                && e.getModifiers() == KeyEvent.CTRL_MASK) {
                if (e.getSource() instanceof Desktop) {
                    ((Desktop) e.getSource()).selectAll();
                } else if (e.getSource() instanceof DesktopIcon) {
                    ((DesktopIcon) e.getSource()).desktop.selectAll();
                }
            }
        }
    }
//**********************************************************
}