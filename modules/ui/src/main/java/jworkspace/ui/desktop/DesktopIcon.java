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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceException;
import jworkspace.kernel.WorkspaceInterpreter;
import jworkspace.ui.Utils;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.action.UISwitchListener;

/**
 * Desktop icon is a shortcut to command.
 */
@SuppressWarnings("MagicNumber")
class DesktopIcon extends JComponent implements MouseListener, MouseMotionListener, FocusListener, Serializable {

    private static final long serialVersionUID = -14567890635540403L;

    private static JPopupMenu popupMenu = null;
    private static JMenuItem properties = null;
    private static JMenuItem delete = null;
    private static JMenuItem execute = null;
    private static JMenuItem cut = null;
    private static JMenuItem copy = null;

    protected transient Desktop desktop;

    private BrightnessReducer br = new BrightnessReducer();

    private String command = "";

    private String workingDir;

    private int mode = DesktopConstants.JAVA_APP_MODE;

    private ImageIcon icon = null;

    private ImageIcon darkenedIcon = null;

    private boolean selected = false;
    // DesktopLayout data
    private int xPos = 0;
    private int yPos = 0;
    private int xPressed = 0;
    private int yPressed = 0;
    // Internal components
    private DesktopIconLabel textLabel = new DesktopIconLabel("", 11);
    private String comments;

    /**
     * Constructor for desktop icon.
     *
     * @param name    Desktop icon name
     * @param command Command line for new desktop icon
     * @param desktop Parent desktop
     * @param icon    Image for desktop icon
     */
    DesktopIcon(String name, String command, String workingDir, Desktop desktop, ImageIcon icon) {
        this(name, command, workingDir, desktop, icon, "");
    }

    /**
     * Constructor for desktop icon.
     *
     * @param name     Desktop icon name
     * @param command  Command line for new desktop icon
     * @param desktop  Parent desktop
     * @param icon     Image for desktop icon
     * @param comments Optional comments string
     */
    private DesktopIcon(String name, String command, String workingDir,
                        Desktop desktop, ImageIcon icon, String comments) {
        super();

        this.setName(name);
        this.workingDir = workingDir;
        this.desktop = desktop;
        this.setIcon(icon);
        this.comments = comments;
        this.addKeyListener(new DesktopIconKeyAdapter());
        setCommandLine(command);

        textLabel.setFont(new Font("sans serif", Font.PLAIN, 11));
        textLabel.setOpaque(true);
        textLabel.setBackground(desktop.getBackground());
        textLabel.setAlignment(DesktopIconLabel.CENTER_ALIGNMENT);
        textLabel.setText(name);

        addMouseListener(this);
        addMouseMotionListener(this);
        addFocusListener(this);
    }

    /**
     * Default constructor.
     *
     * @param desktop Parent desktop
     */
    DesktopIcon(Desktop desktop) {
        this("", "", "", desktop, null, "");
    }

    /**
     * Adds itself and text label to layout.
     */
    public void add() {
        if (desktop != null) {
            desktop.add(this);
            desktop.add(this.textLabel);
        }
    }

    /**
     * Adds itself and text label to layout.
     */
    public void add(Desktop d) {
        if (d != null) {
            d.add(this);
            d.add(this.textLabel);
        }
    }

    /**
     * Edit this label.
     */
    private void edit() {
        DesktopIconDialog dlg = new DesktopIconDialog(Workspace.getUi().getFrame());

        dlg.setData(this);
        dlg.setVisible(true);
    }

    /**
     * Returns command line for this desktop icon
     */
    String getCommandLine() {
        return command;
    }

    /**
     * Sets command line and html tooltip
     */
    void setCommandLine(String command) {
        this.command = command;
        setTooltip(command, this.comments);
    }
    /**
     * Sets comments and html tooltip
     *
     * @param comments for this desktop icon
     */
    void setComments(String comments) {
        this.comments = comments;
        setTooltip(this.command, comments);
    }

    private void setTooltip(String command, String comments) {
        this.setToolTipText("<html>" + (command != null ? command : " ") + "<br>" + " <i>" + comments + "</i></html>");
    }

    /**
     * Returns comments.
     *
     * @return name java.lang.String
     */
    String getComments() {
        return this.comments;
    }

    /**
     * Returns "copy" menu item
     */
    private JMenuItem getCopy() {
        if (copy == null) {
            copy = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Copy"));
            copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            copy.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).desktop.copyIcons();
                }
            });
        }
        return copy;
    }

    /**
     * Returns "cut" menu item
     */
    private JMenuItem getCut() {
        if (cut == null) {
            cut = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Cut"));
            cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
            cut.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).desktop.cutIcons();
                }
            });
        }
        return cut;
    }

    /**
     * Returns "delete" menu item
     */
    private JMenuItem getDelete() {
        if (delete == null) {
            delete = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Delete"));
            delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            delete.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).desktop.removeSelectedIcons();
                }
            });
        }
        return delete;
    }

    /**
     * Returns "execute" menu item
     */
    private JMenuItem getExecute() {
        if (execute == null) {
            execute = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Launch"));
            execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
            execute.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).launch();
                }
            });

        }
        return execute;
    }

    /**
     * Returns image for this desktop icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Sets image for this desktop icon
     */
    public void setIcon(ImageIcon icon) {
        if (icon == null || icon.getIconHeight() == -1 || icon.getIconWidth() == -1) {
            this.icon = (ImageIcon) Workspace.getResourceManager().getIcon(DesktopConstants.DEFAULT_ICON);
        } else {
            this.icon = icon;
        }
        if (icon != null) {
            this.darkenedIcon = new ImageIcon(Toolkit.getDefaultToolkit().
                createImage(new FilteredImageSource(icon.getImage().getSource(), br)));
        } else {
            this.darkenedIcon = null;
        }
        this.repaint();
    }

    /**
     * Returns data, necessary for drag and drop or clipboard operations with desktop icons.
     */
    DesktopIconData getIconData() {
        return new DesktopIconData(this.getName(),
            getCommandLine(),
            getWorkingDirectory(),
            getXPos(),
            getYPos(),
            getIcon(),
            getWidth(),
            getHeight(),
            getMode(),
            getComments());
    }

    /**
     * Returns execution mode for desktop icon
     */
    int getMode() {
        return mode;
    }

    /**
     * Sets execution mode for desktop icon
     */
    void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Returns popup menu for this desktop icon
     */
    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            getMenuItems();
            UIManager.addPropertyChangeListener(new UISwitchListener(popupMenu));
        }
        return popupMenu;
    }

    private void getMenuItems() {
        popupMenu.add(getExecute());
        popupMenu.addSeparator();
        popupMenu.add(getCut());
        popupMenu.add(getCopy());
        popupMenu.addSeparator();
        popupMenu.add(getDelete());
        popupMenu.addSeparator();
        popupMenu.add(getProperties());
    }

    /**
     * Returns popup menu, depending on is there a selected group
     * of icons on desktop and is this icon selected together
     * with others in that group.
     */
    private JPopupMenu getPopupMenu(boolean flag) {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
        } else {
            popupMenu.removeAll();
        }
        if (flag) {
            popupMenu.add(getCut());
            popupMenu.add(getCopy());
            popupMenu.addSeparator();
            popupMenu.add(getDelete());
        } else {
            getMenuItems();
        }
        UIManager.addPropertyChangeListener(new UISwitchListener(popupMenu));
        return popupMenu;
    }

    public Dimension getPreferredSize() {
        return new Dimension(icon.getIconWidth() + 5, icon.getIconHeight() + 5);
    }

    /**
     * Returns "properties" menu item
     */
    private JMenuItem getProperties() {
        if (properties == null) {
            properties = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Properties") + "...");
            properties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
            properties.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).edit();
                }
            });
        }
        return properties;
    }

    /**
     * Returns x coordinate of desktop icon,
     * relative to top left corner of parent desktop.
     */
    int getXPos() {
        return xPos;
    }

    /**
     * Sets x position of desktop icon
     */
    void setXPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Sets working directory
     */
    String getWorkingDirectory() {
        return this.workingDir;
    }

    /**
     * Sets working directory
     */
    void setWorkingDirectory(String workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * Returns y coordinate of desktop icon,
     * relative to top left corner of parent desktop.
     */
    int getYPos() {
        return yPos;
    }

    /**
     * Sets y position of desktop icon
     */
    void setYPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * Is this desktop icon selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets selected flag for desktop icon
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            launch();
        }
    }

    /**
     * Execute command line for this desktop icon
     */
    private void launch() {
        if (mode == DesktopConstants.SCRIPTED_FILE_MODE) {
            WorkspaceInterpreter.getInstance().sourceScriptFile(command);
        } else if (mode == DesktopConstants.SCRIPTED_METHOD_MODE) {
            WorkspaceInterpreter.getInstance().executeScript(command);
        } else if (mode == DesktopConstants.JAVA_APP_MODE) {
            try {
                Workspace.getRuntimeManager().run(command);
            } catch (WorkspaceException ex) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("Desktop.cannotLaunch"), ex);
            }
        } else if (mode == DesktopConstants.NATIVE_COMMAND_MODE) {
            Workspace.getRuntimeManager().executeNativeCommand(command, workingDir);
        }
    }

    /**
     * Loads icon from disk
     */
    public void load(ObjectInputStream dataStream) throws IOException, ClassNotFoundException {
        
        setName(dataStream.readUTF());
        setCommandLine(dataStream.readUTF());
        setToolTipText(getCommandLine());
        setWorkingDirectory(dataStream.readUTF());
        setMode(dataStream.readInt());
        setXPos(dataStream.readInt());
        setYPos(dataStream.readInt());
        setComments(dataStream.readUTF());
        
        Object obj = dataStream.readObject();
        if (obj instanceof ImageIcon) {
            setIcon((ImageIcon) obj);
        }
    }

    /**
     * Tells desktop to drag selected
     * icons in given direction.
     */
    public void mouseDragged(MouseEvent e) {
        /*
         * Do not allow dragging with
         * right button.
         */
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        /*
         * If dragging occurs, set state isDragging()
         * to true.
         */
        desktop.setDraggingState(true);
        desktop.getIconGroup().moveTo(e.getX() - xPressed,
            e.getY() - yPressed);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        /*
         * Default operations. Remove desktop
         * menu if any, or remember pressed points.
         */
        if (SwingUtilities.isRightMouseButton(e)) {
            //Workspace.getLogger().info("popup menu removed");
            remove(getPopupMenu());
            getPopupMenu().setVisible(false);
        } else if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
            /*
             * Select or deselect this icon.
             */
            setSelected(!isSelected());
            requestFocus();
            desktop.repaint();
        } else if (SwingUtilities.isLeftMouseButton(e) && !desktop.isSelectedInGroup(this)) {
            //Workspace.getLogger().info("pressed with left button on " + this.getName());
            xPressed = e.getX();
            yPressed = e.getY();
            desktop.deselectAll();
            setSelected(true);
            requestFocus();
            //Workspace.getLogger().info("selected " + getName());
            desktop.repaint();
        } else if (SwingUtilities.isLeftMouseButton(e) && desktop.isSelectedInGroup(this)) {
            //Workspace.getLogger().info("pressed with left button on " + this.getName());
            xPressed = e.getX();
            yPressed = e.getY();
            setSelected(true);
            requestFocus();
            //Workspace.getLogger().info("selected " + getName());
            desktop.repaint();
        }
    }

    /**
     * Mouse released:
     * <ol>
     * <li> Move selected group of icons to new location, if it was dragging operation.
     * <li> If right mouse button or control is down - show popup menu for this icon or group of icons.
     * <li> If no modifiers and this desktop icon is not a part of group, deselect all and move focus to this icon.
     * </ol>
     */
    public void mouseReleased(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
            if (!desktop.isSelectedInGroup(this)) {
                desktop.deselectAll();
                setSelected(true);
                requestFocus();
                desktop.repaint();
            }
            add(getPopupMenu(desktop.isGroupSelected()));
            getPopupMenu().updateUI();
            getPopupMenu().show(this, e.getX(), e.getY());
        } else if (SwingUtilities.isLeftMouseButton(e) && desktop.isDraggingState()) {
            /*
             * It is an end of dragging - move icons.
             */
            desktop.getIconGroup().destroy();
            desktop.setDraggingState(false);
            requestFocus();
        } else if (SwingUtilities.isLeftMouseButton(e) && !desktop.isDraggingState()) {
            /*
             * If not end of dragging, just deselect all
             * and select this one
             */
            xPressed = e.getX();
            yPressed = e.getY();
            desktop.deselectAll();
            setSelected(true);
            requestFocus();
            desktop.repaint();
        }
    }

    /**
     * Paints desktop icon depending on
     * state of icon - selected or not,
     * has keyboard focus or not.
     */
    public void paintComponent(Graphics g) {
        Dimension d = getSize();
        if (icon != null && !isSelected()) {
            g.drawImage(icon.getImage(), (d.width - icon.getIconWidth()) / 2,
                (d.height - icon.getIconHeight()) / 2, this);
        } else if (icon != null) {
            g.drawImage(darkenedIcon.getImage(),
                (d.width - darkenedIcon.getIconWidth()) / 2,
                (d.height - darkenedIcon.getIconHeight()) / 2,
                this);
        }

        Color desktopSelectionColor = desktop.getSelectionColor();
        textLabel.setForeground(desktopSelectionColor);
        textLabel.setSelected(isSelected());
        g.setColor(desktopSelectionColor);

        if (hasFocus()) {
            Utils.drawDashedRect(g, 0, 0, getWidth() - 1, getHeight() - 1);
        }

        textLabel.setBackground(desktop.getBackground());
        textLabel.repaint();
    }

    /**
     * Removes itself and label from layout.
     */
    public void remove() {
        if (desktop != null) {
            desktop.remove(this);
            desktop.remove(this.textLabel);
        }
    }

    /**
     * Writes desktop icon to disk
     */
    public void save(ObjectOutputStream outputStream) throws java.io.IOException {

        outputStream.writeUTF(getName());
        outputStream.writeUTF(command);
        outputStream.writeUTF(workingDir);
        outputStream.writeInt(mode);
        outputStream.writeInt(xPos);
        outputStream.writeInt(yPos);
        outputStream.writeUTF(comments);
        outputStream.writeObject(getIcon());
    }

    /**
     * Base class method overriden in order
     * to set bounds of label accordingly.
     *
     * @param x      int
     * @param y      int
     * @param height int
     * @param width  int
     */
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        textLabel.setBounds(x - (textLabel.getPreferredSize().width - width) / 2,
            y + height + 3, textLabel.getPreferredSize().width, textLabel.getPreferredSize().
                height);
    }

    /**
     * Base class method overriden in order
     * to set bounds of label accordingly.
     */
    public void setBounds(Rectangle dimensions) {
        super.setBounds(dimensions);
        textLabel.setBounds(dimensions.x - (textLabel.getPreferredSize().width - dimensions.width) / 2,
            dimensions.y + dimensions.height + 3, textLabel.getPreferredSize().width, textLabel.getPreferredSize().
                height);
    }

    /**
     * Basic method is overriden to
     * set label text properly.
     *
     * @param name java.lang.String
     */
    public void setName(String name) {
        if (name == null) {
            super.setName(WorkspaceResourceAnchor.getString("DesktopIcon.defaultName"));
        } else {
            super.setName(name);
        }
        textLabel.setText(name);
    }

    static class BrightnessReducer extends RGBImageFilter implements Serializable {
        public int filterRGB(int x, int y, int rgb) {
            int a = (rgb >> 24) & 0xff;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;

            if (r > 50) {
                r = r - 50;
            } else {
                r = 0;
            }
            if (g > 50) {
                g = g - 50;
            } else {
                g = 0;
            }
            if (b > 50) {
                b = b - 50;
            } else {
                b = 0;
            }

            return (a << 24 | r << 16 | g << 8 | b);
        }
    }

    /**
     * Desktop icon key listener
     */
    class DesktopIconKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                ((DesktopIcon) e.getSource()).launch();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                ((DesktopIcon) e.getSource()).desktop.removeSelectedIcons();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_C
                && e.getModifiers() == KeyEvent.CTRL_MASK) {
                ((DesktopIcon) e.getSource()).desktop.copyIcons();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_X
                && e.getModifiers() == KeyEvent.CTRL_MASK) {
                ((DesktopIcon) e.getSource()).desktop.cutIcons();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_P
                && e.getModifiers() == KeyEvent.CTRL_MASK) {
                ((DesktopIcon) e.getSource()).edit();
                e.consume();
            }
            desktop.dispatchEvent(e);
        }
    }
}
