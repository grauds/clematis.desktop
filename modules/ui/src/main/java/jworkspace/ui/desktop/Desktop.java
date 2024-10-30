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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.hyperrealm.kiwi.ui.KDesktopPane;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IView;
import jworkspace.ui.api.PropertiesPanel;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.utils.SwingUtils;
import jworkspace.ui.widgets.ClassCache;
import jworkspace.ui.widgets.GlassDragPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * Java Desktop
 * @author Anton Troshin
 */
@SuppressWarnings({"MagicNumber"})
@Log
public class Desktop extends KDesktopPane implements IView, MouseListener, MouseMotionListener,
    ActionListener, ClipboardOwner {
    /**
     * Image icon - desktop wallpaper.
     */
    private ImageIcon cover = null;
    /**
     * Path to image
     */
    private String pathToImage = null;
    /**
     * List of desktopIcons.
     */
    private final List<DesktopIcon> desktopIcons = new Vector<>();
    /**
     * Selector frame.
     */
    private final transient GlassDragPane selectPane = new GlassDragPane();
    /**
     * Starting selection point.
     */
    private final Point startingSelection = new Point();
    /**
     * 2nd color of desktop.
     */
    private Color bgColor2 = UIManager.getColor("desktop");
    /**
     * Ending selection point.
     */
    private final Point endingSelection = new Point();
    /**
     * Coordinates of top left menu corner.
     */
    private final Point menuLeftTopCorner = new Point();
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
    private final DesktopMenu desktopPopupMenu = new DesktopMenu(this, this);
    /**
     * Desktop menu
     */
    private JMenu desktopMenu = null;
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
    private int renderMode = 2;
    /**
     * Image cover is visible?
     */
    private boolean coverVisible = true;
    /**
     * New desktop manager
     */
    private final ScrollingDesktopManager manager;
    /**
     * Save path. Relative to user.home
     * -- GETTER --
     *  Returns relative path for saving component data.
     * -- SETTER --
     *  Sets path for saving component data.
     *  This path is relative to user.home path.


     */
    @Setter
    @Getter
    private String path = "";

    /**
     * Java Desktop constructor.
     */
    public Desktop() {
        super();

        addMouseListener(this);
        addMouseMotionListener(this);
        setLayout(new DesktopLayout(this));
        addKeyListener(new DesktopKeyAdapter(this));
        setName(Constants.DESKTOP_NAME_DEFAULT);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        /*
         * Install desktop manager
         */
        manager = new ScrollingDesktopManager(this);
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

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        switch (command) {

            case Constants.GRADIENT_FILL:
                gradientFill = !gradientFill;
                repaint();
                break;

            case Constants.CREATE_SHORTCUT:
                DesktopIcon icon = new DesktopIcon(
                    WorkspaceResourceAnchor.getString("Desktop.defaultIconName"),
                    WorkspaceResourceAnchor.getString("Desktop.defaultIconCommand"),
                    "", this, null);

                DesktopIconDialog dlg =
                    new DesktopIconDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
                    );

                dlg.setData(icon);
                dlg.setVisible(true);

                if (!dlg.isCancelled()) {
                    icon.setXPos(50);
                    icon.setYPos(100);
                    addDesktopIcon(icon);
                }
                break;

            case Constants.BACKGROUND:
                if (!isGradientFill()) {
                    Color color = JColorChooser.showDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                        WorkspaceResourceAnchor.getString("Desktop.chooseBg.title"),
                        getBackground()
                    );

                    if (color != null) {
                        setBackground(color);
                        bgColor2 = color;
                        repaint();
                    }
                } else {
                    Color color1 = JColorChooser.showDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                        WorkspaceResourceAnchor.getString("Desktop.chooseBg1.title"),
                        getBackground()
                    );
                    Color color2 = JColorChooser.showDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                        WorkspaceResourceAnchor.getString("Desktop.chooseBg2.title"),
                        bgColor2
                    );
                    if (color1 != null) {
                        setBackground(color1);
                    }
                    if (color2 != null) {
                        bgColor2 = color2;
                    }
                    repaint();
                }
                break;

            case Constants.PASTE:
                pasteIcons();
                break;

            case Constants.SELECT_ALL:
                selectAll();
                break;

            case Constants.CLOSE_ALL_WINDOWS:
                this.closeAllFrames();
                break;

            case Constants.SWITCH_COVER:
                setCoverVisible(!coverVisible);
                repaint();
                break;

            case Constants.CHOOSE_BACKGROUND_IMAGE:
                JFileChooser fch = ClassCache.getIconChooser();
                if (fch.showOpenDialog(
                    DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()) != JFileChooser.APPROVE_OPTION
                ) {
                    return;
                }
                File imf = fch.getSelectedFile();
                if (imf != null) {
                    String testPath = imf.getAbsolutePath();
                    ImageIcon testCover;
                    try {
                        testCover = new ImageIcon(ImageIO.read(imf));

                        if (testCover.getIconHeight() != -1 && testCover.getIconWidth() != -1) {
                            pathToImage = testPath;
                            cover = testCover;
                            repaint();
                        }
                    } catch (IOException e1) {
                        log.severe(e1.getMessage());
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected action: " + command);
        }
    }

    /**
     * Create and add desktop icon to desktop.
     *
     * @param name        The name of desktop icon
     * @param commandLine Command, that will this desktop
     *                    icon launch, depending on execution mode.
     * @param mode        Execution mode can be the follows
     *                    <ol>
     *                    <li>SCRIPTED_METHOD_MODE = 0 - Executes scripted BSH method;
     *                    <li>SCRIPTED_FILE_MODE = 1 - Executes scripted BSH file;
     *                    <li>NATIVE_COMMAND_MODE = 2 - Executes native command;
     *                    <li>JAVA_APP_MODE = 3 - Executes installed application;
     *                    </li>
     *                    </ol>
     * @param icon        Picture for this desktop icon
     * @param xPos        Initial x coordinate of desktop icon
     * @param yPos        Initial y coordinate of desktop icon
     */
    private void addDesktopIcon(String name,
                                String commandLine,
                                String workingDir,
                                int mode,
                                ImageIcon icon,
                                int xPos,
                                int yPos) {
        DesktopIcon desktopIcon = new DesktopIcon(name,
            commandLine, workingDir, this, icon);
        desktopIcons.add(desktopIcon);
        desktopIcon.setXPos(xPos);
        desktopIcon.setMode(mode);
        desktopIcon.setYPos(yPos);
        desktopIcon.add();
        validate();
        repaint();
    }

    /**
     * Add desktop icon to desktop.
     */
    private void addDesktopIcon(DesktopIcon icon) {
        desktopIcons.add(icon);
        icon.add();
        validate();
        repaint();
    }

    String getPathToImage() {
        return pathToImage;
    }

    /**
     * Adds view to desktop and nest it into JInternalFrame.
     * Desktop places each new view into JInternalFrame.
     * There can be only one frame with given title by now.
     */
    public void addView(javax.swing.JComponent panel, boolean displayImmediately, boolean unique) {
        if (panel.getName() == null) {
            panel.setName(Constants.DESKTOP_NAME_DEFAULT);
        }

        JInternalFrame existing = findFrame(panel.getName());
        if (unique && existing != null) {
            getDesktopManager().activateFrame(existing);
            return;
        }
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

        JInternalFrame nest;

        if (!(panel instanceof JInternalFrame)) {
            nest = new JInternalFrame(panel.getName(), true, true, true, true);
            add(nest);

            if (nest.getWidth() == 0 || nest.getHeight() == 0) {
                nest.setBounds(hpos, vpos, 500, 400);
            } else {
                nest.setLocation(hpos, vpos);
            }

            nest.getContentPane().add(panel);

        } else {
            nest = (JInternalFrame) panel;
            add(nest);

            if (nest.getWidth() == 0 || nest.getHeight() == 0) {
                nest.setBounds(hpos, vpos, 500, 400);
            } else {
                nest.setLocation(hpos, vpos);
            }

        }

        nest.setVisible(true);
        getDesktopManager().activateFrame(nest);
    }

    /**
     * Close all internal windows on desktop
     */
    public void closeAllFrames() {
        JInternalFrame[] internalFrames = getAllFrames();

        for (JInternalFrame internalFrame : internalFrames) {
            getDesktopManager().closeFrame(internalFrame);
        }
    }

    /**
     * Get menu for desktop
     */
    public JMenu[] getMenu() {
        if (desktopMenu == null) {
            desktopMenu = new JMenu(WorkspaceResourceAnchor.getString("Desktop.menu"));
            desktopMenu.setMnemonic(WorkspaceResourceAnchor.getString("Desktop.mnemonic").charAt(0));
            desktopMenu.addMenuListener(new MenuListener() {

                public void menuSelected(MenuEvent e) {
                    desktopMenu.removeAll();
                    desktopMenu.add(desktopPopupMenu.getCreateShortcut());
                    desktopMenu.add(desktopPopupMenu.getGradientFill());
                    desktopMenu.add(desktopPopupMenu.getPaste());

                    desktopMenu.addSeparator();

                    desktopMenu.add(desktopPopupMenu.getSelectAll());
                    desktopMenu.add(desktopPopupMenu.getChangeBackgroundColour());
                    desktopMenu.add(desktopPopupMenu.getChooseBgImage());
                    desktopMenu.add(desktopPopupMenu.getSwitchCover());

                    desktopMenu.addSeparator();

                    desktopMenu.add(desktopPopupMenu.getCloseAllWindows());

                    updateMenuItems();
                }
                public void menuDeselected(MenuEvent e) {}
                public void menuCanceled(MenuEvent e) {}
            });
            UIManager.addPropertyChangeListener(new UISwitchListener(desktopMenu));
        }
        return new JMenu[]{desktopMenu};
    }

    /**
     * Get option panel for desktop
     */
    public PropertiesPanel[] getOptionPanels() {
        return new PropertiesPanel[]{new DesktopBackgroundPanel(this),
            new DesktopOptionsPanel(this)};
    }

    /**
     * Copy selected icons to clipboard
     */
    void copyIcons() {

        List<DesktopIconData> selectedIconsData = new Vector<>();

        for (DesktopIcon desktopIcon : desktopIcons) {
            if (desktopIcon.isSelected()) {
                DesktopIconData data = desktopIcon.getIconData();
                selectedIconsData.add(data);
            }
        }
        DesktopIconData[] iconData = new DesktopIconData[selectedIconsData.size()];
        DesktopIconSelectionData selectionData =
            new DesktopIconSelectionData(selectedIconsData.size(), selectedIconsData.toArray(iconData));

        DesktopServiceLocator
            .getInstance()
            .getWorkspaceGUI()
            .getClipboard()
            .setContents(new DesktopIconSelection(selectionData), this);
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
    void cutIcons() {
        copyIcons();
        removeSelectedIcons();
    }

    /**
     * Deselect all icons
     */
    void deselectAll() {
        for (DesktopIcon desktopIcon : desktopIcons) {
            desktopIcon.setSelected(false);
        }
        validate();
        repaint();
    }

    /**
     * Find frame with specified title.
     */
    private JInternalFrame findFrame(String title) {
        JInternalFrame[] internalFrames = getAllFrames();

        for (JInternalFrame internalFrame : internalFrames) {
            if (internalFrame.getTitle().equals(title)) {
                return internalFrame;
            }
        }

        return null;
    }

    /**
     * Returns background image of current desktop
     */
    ImageIcon getCover() {
        if (cover == null) {
            cover = loadCover();
        }
        return cover;
    }

    /**
     * Sets cover image for desktop
     */
    void setCover(String path) {
        pathToImage = path;
        this.cover = loadCover();
    }

    private ImageIcon loadCover() {
        if (pathToImage != null) {
            cover = new ImageIcon(pathToImage);
        }
        return cover;
    }

    /**
     * Get contrast color for desktop selection
     * borders and other marker components.
     */
    Color getSelectionColor() {
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
    Color getSecondBackground() {
        return bgColor2;
    }

    /**
     * Sets second color of gradient fill
     */
    void setSecondBackground(Color color) {
        bgColor2 = color;
    }

    /**
     * Returns desktop icons.
     */
    DesktopIcon[] getDesktopIcons() {
        return desktopIcons.toArray(new DesktopIcon[0]);
    }

    /**
     * Returns current desktop menu
     */
    private JPopupMenu getDesktopMenu() {
        return desktopPopupMenu;
    }

    /**
     * Returns group of currently selected icons.
     */
    DesktopIconsGroup getIconGroup() {
        if (iconGroup == null) {
            iconGroup = new DesktopIconsGroup(this);
        }
        return iconGroup;
    }

    /**
     * Get rendering mode of background image
     */
    int getRenderMode() {
        return renderMode;
    }

    /**
     * Set render mode
     */
    void setRenderMode(int mode) {
        if (mode != Constants.CENTER_IMAGE
            && mode != Constants.TILE_IMAGE
            && mode != Constants.STRETCH_IMAGE
            && mode != Constants.TOP_LEFT_CORNER_IMAGE
            && mode != Constants.TOP_RIGHT_CORNER_IMAGE
            && mode != Constants.BOTTOM_LEFT_CORNER_IMAGE
            && mode != Constants.BOTTOM_RIGHT_CORNER_IMAGE) {
            throw new IllegalArgumentException("Illegal render mode");
        }

        renderMode = mode;
        repaint();
    }

    /**
     * Calculates vector which points to direction for desktop icons dragging.
     */
    Point getTransferVector(int xshift, int yshift) {
        Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point bottomRight = new Point(0, 0);

        for (DesktopIcon icon : desktopIcons) {
            if (icon.isSelected()) {
                Point location = icon.getLocation();

                topLeft.x = SwingUtils.min(topLeft.x, location.x + getParent().getLocation().x + getLocation().x);
                topLeft.y = SwingUtils.min(topLeft.y, location.y + getParent().getLocation().y + getLocation().y);

                bottomRight.x = SwingUtils.max(bottomRight.x, location.x
                    + getParent().getLocation().x + getLocation().x + icon.getWidth());

                bottomRight.y = SwingUtils.max(bottomRight.y, location.y
                    + getParent().getLocation().y + getLocation().y + icon.getHeight());

            }
        }

        Rectangle groupBounds = new Rectangle(topLeft.x, topLeft.y,
            SwingUtils.distance(topLeft.x, bottomRight.x),
            SwingUtils.distance(topLeft.y, bottomRight.y));

        int localXShift = xshift;
        int localYShift = yshift;

        if (groupBounds.x + xshift < 0) {
            localXShift = -groupBounds.x;
        } else if (groupBounds.x + xshift + groupBounds.width > getWidth()) {
            localXShift = getWidth() - groupBounds.x - groupBounds.width;
        }

        if (groupBounds.y + yshift < 0) {
            localYShift = -groupBounds.y;
        } else if (groupBounds.y + yshift + groupBounds.height > getHeight()) {
            localYShift = getHeight() - groupBounds.y - groupBounds.height;
        }

        return new Point(localXShift, localYShift);
    }

    /**
     * Calculates vector which points to direction for desktop icons dragging.
     */
    private Point getTransferVector(int finalx, int finaly, DesktopIconSelectionData icons) {

        Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point bottomRight = new Point(0, 0);

        for (int i = 0; i < icons.getSize(); i++) {
            DesktopIconData iconData = icons.getIconData()[i];

            int xlocation = iconData.getXPos();
            int ylocation = iconData.getYPos();

            topLeft.x = SwingUtils.min(topLeft.x, xlocation + getParent().getLocation().x + getLocation().x);
            topLeft.y = SwingUtils.min(topLeft.y, ylocation + getParent().getLocation().y + getLocation().y);

            bottomRight.x = SwingUtils.max(bottomRight.x, xlocation
                + getParent().getLocation().x + getLocation().x + iconData.getWidth());

            bottomRight.y = SwingUtils.max(bottomRight.y, ylocation
                + getParent().getLocation().y + getLocation().y + iconData.getHeight());
        }

        Rectangle groupBounds = new Rectangle(topLeft.x, topLeft.y,
            SwingUtils.distance(topLeft.x, bottomRight.x),
            SwingUtils.distance(topLeft.y, bottomRight.y));

        int localFinalX = finalx;
        int localFinalY = finaly;

        if (finalx + groupBounds.width > getWidth()) {
            localFinalX = getWidth() - groupBounds.width;
        }

        if (finaly + groupBounds.height > getHeight()) {
            localFinalY = getHeight() - groupBounds.height;
        }

        return new Point(localFinalX - groupBounds.x, localFinalY - groupBounds.y);
    }

    /**
     * Is desktop currently drags icons?
     */
    boolean isDraggingState() {
        return this.isDraggingState;
    }

    /**
     * Sets dragging state for desktop
     */
    void setDraggingState(boolean state) {
        this.isDraggingState = state;
        if (!state) {
            iconGroup = null;
        }
    }

    /**
     * Is gradient filled desktop
     */
    boolean isGradientFill() {
        return gradientFill;
    }

    /**
     * Sets flag if desktop uses gradient fill for painting
     * background.
     */
    void setGradientFill(boolean gradientFill) {
        this.gradientFill = gradientFill;
    }

    /**
     * Is cover visible
     */
    boolean isCoverVisible() {
        return coverVisible;
    }

    /**
     * Sets visibility of cover
     */
    void setCoverVisible(boolean coverVisible) {
        this.coverVisible = coverVisible;
    }

    /**
     * Is group selected.
     */
    boolean isGroupSelected() {

        int size = 0;
        for (DesktopIcon desktopIcon : desktopIcons) {
            if (desktopIcon.isSelected()) {
                size++;
            }
        }

        return size > 1;
    }

    /**
     * Count selected icons.
     */
    private DesktopIcon[] getSelectedIcons() {

        List<DesktopIcon> temp = new Vector<>();
        for (DesktopIcon desktopIcon : desktopIcons) {
            if (desktopIcon.isSelected()) {
                temp.add(desktopIcon);
            }
        }

        return temp.toArray(new DesktopIcon[0]);
    }

    /**
     * Returns true, if some data is left in editors and should be saved. This
     * method seeks for isModified() method of any internal window within this desktop.
     */
    public boolean isModified() {

        JInternalFrame[] internalFrames = getAllFrames();

        for (JInternalFrame internalFrame : internalFrames) {
            try {
                Method modMeth = internalFrame.getClass().getMethod("isModified");
                Object result = modMeth.invoke(internalFrame);
                if (result instanceof Boolean && (Boolean) result) {
                    return true;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception ex) {
                // no method found
            }
        }
        return false;
    }

    /**
     * Returns true, if argument desktop icon is selected with group.
     */
    boolean isSelectedInGroup(DesktopIcon icon) {
        return isGroupSelected() && icon.isSelected();
    }

    /**
     * Set this flag to true, if you want component to be unique among all workspace views.
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
    public void load() throws IOException {

        File file = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(getPath())
            .resolve(Constants.DESKTOP_DAT)
            .toFile();

        try (FileInputStream inputFile = new FileInputStream(file);
             ObjectInputStream dataStream = new ObjectInputStream(inputFile)) {

            this.setName(dataStream.readUTF());

            int red = dataStream.readInt();
            int green = dataStream.readInt();
            int blue = dataStream.readInt();
            setBackground(new Color(red, green, blue));

            red = dataStream.readInt();
            green = dataStream.readInt();
            blue = dataStream.readInt();
            this.bgColor2 = new Color(red, green, blue);

            this.gradientFill = dataStream.readBoolean();
            this.setOpaque(dataStream.readBoolean());
            this.hpos = dataStream.readInt();
            this.hstep = dataStream.readInt();
            this.renderMode = dataStream.readInt();
            this.vpos = dataStream.readInt();
            this.vstep = dataStream.readInt();
            this.coverVisible = dataStream.readBoolean();
            int size = dataStream.readInt();

            for (int i = 0; i < size; i++) {
                DesktopIcon icon = new DesktopIcon(this);
                icon.load(dataStream);
                this.addDesktopIcon(icon);
            }
            pathToImage = dataStream.readUTF();

            if (pathToImage.trim().isEmpty() || !new File(pathToImage).exists()) {
                pathToImage = null;
            }

            boolean outline = dataStream.readBoolean();

            if (outline) {
                setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            } else {
                setDragMode(JDesktopPane.LIVE_DRAG_MODE);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        endingSelection.x = e.getX();
        endingSelection.y = e.getY();

        selectPane.setBounds(SwingUtils.min(startingSelection.x, endingSelection.x),
            SwingUtils.min(startingSelection.y, endingSelection.y),
            SwingUtils.distance(endingSelection.x, startingSelection.x),
            SwingUtils.distance(endingSelection.y, startingSelection.y));
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
        /*
         * Hides desktop menu and makes
         * selection rectangle.
         */
        if (SwingUtilities.isLeftMouseButton(e) || e.isControlDown()) {

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

        if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {

            if (getDesktopMenu() != null) {
                ((Desktop) e.getComponent()).add(getDesktopMenu());
                getDesktopMenu().show(this, e.getX(), e.getY());
            }

            menuLeftTopCorner.x = e.getX() + getX();
            menuLeftTopCorner.y = e.getY() + getY();
        } else if (SwingUtilities.isLeftMouseButton(e) || e.isControlDown()) {

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
            g2.setPaint(new GradientPaint(0, 0, getBackground(), 0, h, bgColor2));
            g2.fill(new Rectangle(0, 0, w, h));
        }
        if (getCover() != null && coverVisible) {
            /*
             * Drawing of desktop image can occur in several rendering modes.
             */
            if (renderMode == Constants.CENTER_IMAGE) {
                g.drawImage(getCover().getImage(), (getWidth() - getCover().getIconWidth()) / 2,
                    (getHeight() - getCover().getIconHeight()) / 2, this);
            } else if (renderMode == Constants.STRETCH_IMAGE) {
                g.drawImage(getCover().getImage(), 0, 0, getWidth(), getHeight(), this);
            } else if (renderMode == Constants.TILE_IMAGE) {
                int x = 0, y = 0;
                while (x < getWidth()) {
                    while (y < getHeight()) {
                        g.drawImage(getCover().getImage(), x, y, this);
                        y += getCover().getIconHeight();
                    }
                    x += getCover().getIconWidth();
                    y = 0;
                }
            } else if (renderMode == Constants.TOP_LEFT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), 0, 0, this);
            } else if (renderMode == Constants.BOTTOM_LEFT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), 0, this.getHeight() - getCover().getIconHeight(), this);
            } else if (renderMode == Constants.TOP_RIGHT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), this.getWidth() - getCover().getIconWidth(), 0, this);
            } else if (renderMode == Constants.BOTTOM_RIGHT_CORNER_IMAGE) {
                g.drawImage(getCover().getImage(), this.getWidth() - getCover().getIconWidth(),
                    this.getHeight() - getCover().getIconHeight(), this);
            }
        }
    }

    /**
     * Paste desktop icons from clipboard
     */
    void pasteIcons() {
        try {
            Transferable contents = DesktopServiceLocator
                .getInstance()
                .getWorkspaceGUI()
                .getClipboard()
                .getContents(this);

            if (contents == null) {
                return;
            }

            DesktopIconSelectionData iconSelectionData = (DesktopIconSelectionData)
                contents.getTransferData(DesktopIconSelection.desktopIconFlavor);

            int xShift = menuLeftTopCorner.x;
            int yShift = menuLeftTopCorner.y;

            Point point = getTransferVector(xShift, yShift, iconSelectionData);

            xShift = point.x;
            yShift = point.y;

            for (int i = 0; i < iconSelectionData.getSize(); i++) {
                DesktopIconData iconData = iconSelectionData.getIconData()[i];

                addDesktopIcon(iconData.getName(), iconData.getCommandLine(),
                    iconData.getWorkingDir(),
                    iconData.getMode(), iconData.getIcon(),
                    iconData.getXPos() + xShift,
                    iconData.getYPos() + yShift);
            }

            validate();
            repaint();
        } catch (IOException | UnsupportedFlavorException e) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("Desktop.pasteIcons.failed"), e);
        }
    }

    /**
     * Select dektop icons by rectangular selector frame.
     */
    private void rectSelector() {
        Rectangle selector =
            new Rectangle(SwingUtils.min(startingSelection.x, endingSelection.x),
                SwingUtils.min(startingSelection.y, endingSelection.y),
                SwingUtils.distance(endingSelection.x, startingSelection.x),
                SwingUtils.distance(endingSelection.y, startingSelection.y));

        for (DesktopIcon desktopIcon : desktopIcons) {
            Rectangle iconPlace = desktopIcon.getBounds();
            if (iconPlace.intersects(selector)) {
                desktopIcon.setSelected(true);
            }
        }
    }

    /**
     * Key navigation handler. Select icon depending
     * on argument cursor icon.
     */
    void selectNextIcon(int direction, DesktopIcon desktopIcon) {

        /*
         * Get currently selected icons.
         */
        DesktopIcon[] selectedIcons = getSelectedIcons();
        /*
         * If no icons are selected, select the top left icon
         * on this desktop.
         */
        if (selectedIcons.length == 0) {
            DesktopIcon topLeft = getAtCorner(Constants.TOP_LEFT_ICON);
            if (topLeft != null) {
                topLeft.setSelected(true);
                topLeft.requestFocus();
            }
            return;
        }
        /*
         * The desktop icon, nearest to ours.
         */
        DesktopIcon min = null;
        /*
         * Declare point to min icon
         */
        Point minIconPoint = null;
        /*
         * Find cursor icon coordinates
         */
        Point cursorIconPoint = new Point(desktopIcon.getXPos(), desktopIcon.getYPos());
        /*
         * Iterate through all icons and find the closest. If there is only one icon,
         * return it as a closest to itself.
         */
        for (DesktopIcon icon : desktopIcons) {
            /*
             * If this is a cursor icon, continue
             */
            if (icon.equals(desktopIcon)) {
                continue;
            }
            /*
             * Measure its coordinates
             */
            Point currentIconPoint = new Point(icon.getXPos(), icon.getYPos());
            /*
             * Divide all icons into groups
             */
            if ((direction == Constants.ICON_ON_NORTH
                && SwingUtils.isNorthernQuadrant(cursorIconPoint, currentIconPoint))
                || (direction == Constants.ICON_ON_SOUTH
                && SwingUtils.isSouthernQuadrant(cursorIconPoint, currentIconPoint))
                || (direction == Constants.ICON_ON_EAST
                && SwingUtils.isEasternQuadrant(cursorIconPoint, currentIconPoint))
                || (direction == Constants.ICON_ON_WEST
                && SwingUtils.isWesternQuadrant(cursorIconPoint, currentIconPoint))) {
                /*
                 * Min is becoming the first icon in quadrant
                 */
                if (minIconPoint == null) {
                    minIconPoint = currentIconPoint;
                }

                if (SwingUtils.distance(cursorIconPoint, minIconPoint)
                    >= SwingUtils.distance(cursorIconPoint, currentIconPoint)) {
                    min = icon;
                    minIconPoint = currentIconPoint;
                }
            }
        }
        /*
         * Finally, select chosen icon
         */
        if (min != null) {
            deselectAll();
            min.setSelected(true);
            min.requestFocus();
        }
    }

    void selectNextIcon(int direction) {
        selectNextIcon(direction, getAtCorner(Constants.TOP_RIGHT_ICON));
    }

    /**
     * Get corner icons
     */
    @SuppressWarnings("CyclomaticComplexity")
    private DesktopIcon getAtCorner(int corner) {
        DesktopIcon res = null;
        for (DesktopIcon desktopIcon : desktopIcons) {
            switch (corner) {
                case Constants.TOP_LEFT_ICON:
                    if (res == null) {
                        res = desktopIcon;
                    } else if (desktopIcon.getXPos() < res.getXPos()
                        && desktopIcon.getYPos() < res.getYPos()) {
                        res = desktopIcon;
                    }
                    break;
                case Constants.TOP_RIGHT_ICON:
                    if (res == null) {
                        res = desktopIcon;
                    } else if (desktopIcon.getXPos() > res.getXPos()
                        && desktopIcon.getYPos() < res.getYPos()) {
                        res = desktopIcon;
                    }
                    break;
                case Constants.BOTTOM_LEFT_ICON:
                    if (res == null) {
                        res = desktopIcon;
                    } else if (desktopIcon.getXPos() < res.getXPos()
                        && desktopIcon.getYPos() > res.getYPos()) {
                        res = desktopIcon;
                    }
                    break;
                case Constants.BOTTOM_RIGHT_ICON:
                    if (res == null) {
                        res = desktopIcon;
                    } else if (desktopIcon.getXPos() > res.getXPos()
                        && desktopIcon.getYPos() > res.getYPos()) {
                        res = desktopIcon;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + corner);
            }
        }
        return res;
    }

    /**
     * Delete selected icons
     */
    void removeSelectedIcons() {

        DesktopIcon[] irem = getSelectedIcons();

        String dialogMessage = irem.length == 1
            ? (WorkspaceResourceAnchor.getString("Desktop.removeIcon.question") + " \"" + irem[0].getName() + "\"?")
            : WorkspaceResourceAnchor.getString("Desktop.removeIcons.question") + Constants.LOG_SPACE
               + irem.length
               + Constants.LOG_SPACE
               + WorkspaceResourceAnchor.getString("Desktop.countIcon") + "?";

        String dialogTitle = irem.length == 1
            ? WorkspaceResourceAnchor.getString("Desktop.removeIcon.title")
            : WorkspaceResourceAnchor.getString("Desktop.removeIcons.title");

        if (JOptionPane.showConfirmDialog(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
            dialogMessage, dialogTitle, JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) {
            return;
        }

        for (DesktopIcon desktopIcon : irem) {
            desktopIcons.remove(desktopIcon);
            desktopIcon.remove();
        }
        validate();
        repaint();
    }

    /**
     * Reset desktop by closing all frames
     */
    public void reset() {
        for (DesktopIcon desktopIcon : desktopIcons) {
            desktopIcon.remove();
        }
        desktopIcons.clear();
        validate();
        repaint();
        closeAllFrames();
    }

    public void save() throws IOException {

        if (getName() == null) {
            setName(Constants.DESKTOP_NAME_DEFAULT);
        }

        File file = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(getPath())
            .toFile();

        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.severe("Can't create directories, not saving: " + file.getAbsolutePath());
                return;
            }
        }

        file = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(getPath())
            .resolve(Constants.DESKTOP_DAT)
            .toFile();

        try (FileOutputStream outputFile = new FileOutputStream(file);
             ObjectOutputStream outputStream = new ObjectOutputStream(outputFile)) {

            outputStream.writeUTF(getName());

            outputStream.writeInt(getBackground().getRed());
            outputStream.writeInt(getBackground().getGreen());
            outputStream.writeInt(getBackground().getBlue());

            outputStream.writeInt(bgColor2.getRed());
            outputStream.writeInt(bgColor2.getGreen());
            outputStream.writeInt(bgColor2.getBlue());

            outputStream.writeBoolean(gradientFill);
            outputStream.writeBoolean(isOpaque());
            outputStream.writeInt(hpos);
            outputStream.writeInt(hstep);
            outputStream.writeInt(renderMode);
            outputStream.writeInt(vpos);
            outputStream.writeInt(vstep);
            outputStream.writeBoolean(coverVisible);
            outputStream.writeInt(desktopIcons.size());

            for (DesktopIcon desktopIcon : desktopIcons) {
                (desktopIcon).save(outputStream);
            }

            outputStream.writeUTF(Objects.requireNonNullElse(pathToImage, ""));
            outputStream.writeBoolean(getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE);
            outputStream.flush();
        }
    }

    void selectAll() {
        for (DesktopIcon desktopIcon : desktopIcons) {
            desktopIcon.setSelected(true);
        }
        validate();
        repaint();
    }

    public void update() {
        this.revalidate();
        this.repaint();
    }

    void updateMenuItems() {

        if (gradientFill) {
            desktopPopupMenu.getGradientFill().setText(WorkspaceResourceAnchor.getString("Desktop.menu.hideGradient"));
        } else {
            desktopPopupMenu.getGradientFill().setText(WorkspaceResourceAnchor.getString("Desktop.menu.showGradient"));
        }
        if (getCover() == null) {
            desktopPopupMenu.getSwitchCover().setEnabled(false);
            desktopPopupMenu.getSwitchCover().setText(WorkspaceResourceAnchor.getString("Desktop.menu.noCover"));
        } else {
            desktopPopupMenu.getSwitchCover().setEnabled(true);
            if (isCoverVisible()) {
                desktopPopupMenu.getSwitchCover().setText(WorkspaceResourceAnchor.getString("Desktop.menu.hideCover"));
            } else {
                desktopPopupMenu.getSwitchCover().setText(WorkspaceResourceAnchor.getString("Desktop.menu.showCover"));
            }
        }
        Transferable contents = DesktopServiceLocator.getInstance().getWorkspaceGUI().getClipboard().getContents(this);
        if (contents == null) {
            desktopPopupMenu.getPaste().setEnabled(false);
        } else {
            desktopPopupMenu.getPaste().setEnabled(contents instanceof DesktopIconSelection);
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
            p.x = p.x + Constants.FRAME_OFFSET;
            p.y = p.y + Constants.FRAME_OFFSET;
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
        int frameHeight = (getBounds().height - 5) - allFrames.length * Constants.FRAME_OFFSET;
        int frameWidth = (getBounds().width - 5) - allFrames.length * Constants.FRAME_OFFSET;
        for (int i = allFrames.length - 1; i >= 0; i--) {
            allFrames[i].setSize(frameWidth, frameHeight);
            allFrames[i].setLocation(x, y);
            x = x + Constants.FRAME_OFFSET;
            y = y + Constants.FRAME_OFFSET;
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
        for (Component allFrame : allFrames) {
            allFrame.setSize(getBounds().width, frameHeight);
            allFrame.setLocation(0, y);
            y = y + frameHeight;
        }
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given dimension.
     */
    private void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given width and height.
     */
    void setAllSize(int width, int height) {
        setAllSize(new Dimension(width, height));
    }

    private void checkDesktopSize() {
        if (getParent() != null && isVisible()) {
            manager.resizeDesktop();
        }
    }
}