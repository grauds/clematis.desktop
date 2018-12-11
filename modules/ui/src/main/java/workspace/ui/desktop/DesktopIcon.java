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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceInterpreter;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.util.WorkspaceError;
import jworkspace.ui.Utils;

/**
 * Desktop Icon is a shortcut to command.
 */
class DesktopIcon extends JComponent implements MouseListener,
    MouseMotionListener, FocusListener, Serializable {
    public static final int SCRIPTED_METHOD_MODE = 0;
    public static final int SCRIPTED_FILE_MODE = 1;
    public static final int NATIVE_COMMAND_MODE = 2;
    public static final int JAVA_APP_MODE = 3;
    public static final String DEFAULT_ICON = "desktop/default.png";
    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = -14567890635540403L;
    private static JPopupMenu popupMenu = null;
    private static JMenuItem properties = null;
    private static JMenuItem delete = null;
    private static JMenuItem execute = null;
    private static JMenuItem cut = null;
    private static JMenuItem copy = null;
    // Parent desktop
    protected transient Desktop desktop = null;
    /**
     * Image filter. Reduces brightness of desktop
     * ICON.
     */
    BrightnessReducer br = new BrightnessReducer();
    private String command = "";
    private String working_dir = "";
    private int mode = DesktopIcon.JAVA_APP_MODE;
    private ImageIcon icon = null;
    private ImageIcon darkened_icon = null;
    private boolean selected = false;
    // Layout data
    private int xPos = 0;
    private int yPos = 0;
    private int xPressed = 0;
    private int yPressed = 0;
    // Internal components
    private DesktopIconLabel textLabel = new DesktopIconLabel("", 11);
    private String comments = "";

    /**
     * Constructor for desktop ICON.
     *
     * @param name    Desktop ICON name
     * @param command Command line for new desktop ICON
     * @param desktop Parent desktop
     * @param icon    Image for desktop ICON
     */
    public DesktopIcon(String name, String command,
                       String working_dir, Desktop desktop, ImageIcon icon) {
        this(name, command, working_dir, desktop, icon, "");
    }

    /**
     * Constructor for desktop ICON.
     *
     * @param name     Desktop ICON name
     * @param command  Command line for new desktop ICON
     * @param desktop  Parent desktop
     * @param icon     Image for desktop ICON
     * @param comments Optional comments string
     */
    public DesktopIcon(String name, String command,
                       String working_dir, Desktop desktop, ImageIcon icon, String comments) {
        super();

        this.setName(name);
        this.working_dir = working_dir;
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
    public DesktopIcon(Desktop desktop) {
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
    public void edit() {
        DesktopIconDialog dlg =
            new DesktopIconDialog(Workspace.getUI().getFrame());

        dlg.setData(this);
        dlg.setVisible(true);
    }

    /**
     * Returns command line for this desktop ICON
     */
    public String getCommandLine() {
        return command;
    }

    /**
     * Sets command line and html tooltip
     */
    public void setCommandLine(String command) {
        if (command != null) {
            this.command = command;
        } else {
            this.command = " ";
        }
        if (comments == null) {
            comments = " ";
        }
        this.setToolTipText("<html>" + command + "<br>" + " <i>" + comments
            + "</i></html>");
    }

    /**
     * Returns comments.
     *
     * @return name java.lang.String
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * Sets comments and html tooltip
     *
     * @param comments for this desktop ICON
     */
    public void setComments(String comments) {
        if (comments != null) {
            this.comments = comments;
        } else {
            this.comments = " ";
        }
        if (command == null) {
            command = " ";
        }

        this.setToolTipText("<html>" + command + "<br>" + " <i>" + comments
            + "</i></html>");
    }

    /**
     * Returns "copy" menu item
     */
    protected JMenuItem getCopy() {
        if (copy == null) {
            copy = new JMenuItem(LangResource.getString("DesktopIcon.Copy"));
            copy.setAccelerator
                (KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            copy.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.getInvoker() instanceof DesktopIcon) {
                        ((DesktopIcon) popupMenu.getInvoker()).desktop.copyIcons();
                    }
                }
            });
        }
        return copy;
    }

    /**
     * Returns "cut" menu item
     */
    protected JMenuItem getCut() {
        if (cut == null) {
            cut = new JMenuItem(LangResource.getString("DesktopIcon.Cut"));
            cut.setAccelerator
                (KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
            cut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.getInvoker() instanceof DesktopIcon) {
                        ((DesktopIcon) popupMenu.getInvoker()).desktop.cutIcons();
                    }
                }
            });
        }
        return cut;
    }

    /**
     * Returns "delete" menu item
     */
    protected JMenuItem getDelete() {
        if (delete == null) {
            delete = new JMenuItem(LangResource.getString("DesktopIcon.Delete"));
            delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.getInvoker() instanceof DesktopIcon) {
                        ((DesktopIcon) popupMenu.getInvoker()).desktop.removeSelectedIcons();
                    }
                }
            });
        }
        return delete;
    }

    /**
     * Returns "execute" menu item
     */
    protected JMenuItem getExecute() {
        if (execute == null) {
            execute = new JMenuItem(LangResource.getString("DesktopIcon.Launch"));
            execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
            execute.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.getInvoker() instanceof DesktopIcon) {
                        ((DesktopIcon) popupMenu.getInvoker()).launch();
                    }
                }
            });

        }
        return execute;
    }

    /**
     * Returns image for this desktop ICON
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Sets image for this desktop ICON
     */
    public void setIcon(ImageIcon icon) {
        if (icon == null ||
            icon.getIconHeight() == -1
            || icon.getIconWidth() == -1) {
            icon = (ImageIcon) Workspace.getResourceManager().
                getIcon(DesktopIcon.DEFAULT_ICON);
        }
        this.icon = icon;
        this.darkened_icon = new ImageIcon(Toolkit.getDefaultToolkit().
            createImage(new FilteredImageSource(icon.getImage().getSource(), br)));
        this.repaint();
    }

    /**
     * Returns data, nessesary for drag and drop or
     * clipboard operations with desktop icons.
     */
    public DesktopIconData getIconData() {
        return new DesktopIconData(this.getName(), getCommandLine(),
            getWorkingDirectory(), getXPos(), getYPos(),
            getWidth(), getHeight(), getMode(), getIcon(),
            getComments());
    }

    /**
     * Returns execution mode for desktop ICON
     */
    public int getMode() {
        return mode;
    }

    /**
     * Sets execution mode for desktop ICON
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Returns popup menu for this desktop ICON
     */
    public JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.add(getExecute());
            popupMenu.addSeparator();
            popupMenu.add(getCut());
            popupMenu.add(getCopy());
            popupMenu.addSeparator();
            popupMenu.add(getDelete());
            popupMenu.addSeparator();
            popupMenu.add(getProperties());
            UIManager.addPropertyChangeListener(new UISwitchListener(popupMenu));
        }
        return popupMenu;
    }

    /**
     * Sets popup menu for desktop ICON
     */
    public void setPopupMenu(JPopupMenu popupMenu) {
        DesktopIcon.popupMenu = popupMenu;
    }

    /**
     * Returns popup menu, depending on is there a selected group
     * of icons on desktop and is this ICON selected together
     * with others in that group.
     */
    public JPopupMenu getPopupMenu(boolean flag) {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
        } else if (popupMenu != null) {
            popupMenu.removeAll();
        }
        if (flag) {
            popupMenu.add(getCut());
            popupMenu.add(getCopy());
            popupMenu.addSeparator();
            popupMenu.add(getDelete());
        } else {
            popupMenu.add(getExecute());
            popupMenu.addSeparator();
            popupMenu.add(getCut());
            popupMenu.add(getCopy());
            popupMenu.addSeparator();
            popupMenu.add(getDelete());
            popupMenu.addSeparator();
            popupMenu.add(getProperties());
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
    protected JMenuItem getProperties() {
        if (properties == null) {
            properties = new JMenuItem(LangResource.getString("DesktopIcon.Properties") + "...");
            properties.setAccelerator
                (KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
            properties.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.getInvoker() instanceof DesktopIcon) {
                        ((DesktopIcon) popupMenu.getInvoker()).edit();
                    }
                }
            });
        }
        return properties;
    }

    /**
     * Returns x coordinate of desktop ICON,
     * relative to top left corner of parent desktop.
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Sets x position of desktop ICON
     */
    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Sets working directory
     */
    public String getWorkingDirectory() {
        return this.working_dir;
    }

    /**
     * Sets working directory
     */
    public void setWorkingDirectory(String working_dir) {
        this.working_dir = working_dir;
    }

    /**
     * Returns y coordinate of desktop ICON,
     * relative to top left corner of parent desktop.
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * Sets y position of desktop ICON
     */
    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * Is this desktop ICON selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets selected flag for desktop ICON
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
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
     * Execute command line for this desktop ICON
     */
    public void launch() {
        if (mode == DesktopIcon.SCRIPTED_FILE_MODE) {
            WorkspaceInterpreter.getInstance().sourceScriptFile(command);
        } else if (mode == DesktopIcon.SCRIPTED_METHOD_MODE) {
            WorkspaceInterpreter.getInstance().executeScript(command);
        } else if (mode == DesktopIcon.JAVA_APP_MODE) {
            Workspace.getRuntimeManager().run(command);
        } else if (mode == DesktopIcon.NATIVE_COMMAND_MODE) {
            try {
                Workspace.getRuntimeManager().executeNativeCommand(command, working_dir);
            } catch (IOException ex) {
                WorkspaceError.exception
                    (LangResource.getString("Desktop.cannotLaunch"), ex);
            }
        }
    }

    /**
     * Loads ICON from disk
     */
    public void load(ObjectInputStream dataStream)
        throws IOException, ClassNotFoundException {
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
        /**
         * Do not allow dragging with
         * right button.
         */
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        /**
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
        /**
         * Default operations. Remove desktop
         * menu if any, or remember pressed points.
         */
        if (SwingUtilities.isRightMouseButton(e)) {
            //Workspace.getLogger().info("popup menu removed");
            remove(getPopupMenu());
            getPopupMenu().setVisible(false);
        } else if (SwingUtilities.isLeftMouseButton(e) &&
            e.isControlDown()) {
            /**
             * Select or deselect this ICON.
             */
            setSelected(!isSelected());
            requestFocus();
            desktop.repaint();
            return;
        } else if (SwingUtilities.isLeftMouseButton(e) &&
            !desktop.isSelectedInGroup(this)) {
            //Workspace.getLogger().info("pressed with left button on " + this.getName());
            xPressed = e.getX();
            yPressed = e.getY();
            desktop.deselectAll();
            setSelected(true);
            requestFocus();
            //Workspace.getLogger().info("selected " + getName());
            desktop.repaint();
        } else if (SwingUtilities.isLeftMouseButton(e) &&
            desktop.isSelectedInGroup(this)) {
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
     * <li> Move selected group of icons to
     * new location, if it was dragging
     * operation.
     * <li> If right mouse button or control
     * is down - show popup menu for this
     * ICON or group of icons.
     * <li> If no modifiers and this desktop
     * ICON is not a part of group, deselect all
     * and move focus to this ICON.
     * </ol>
     */
    public void mouseReleased(MouseEvent e) {
        /**
         * Show menu.
         */
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
            return;
        } else if (SwingUtilities.isLeftMouseButton(e) &&
            e.isControlDown()) {
            return;
        }
        /**
         * It is an end of dragging - move icons.
         */
        else if (SwingUtilities.isLeftMouseButton(e)
            && desktop.isDraggingState()) {
            desktop.getIconGroup().destroy();
            desktop.setDraggingState(false);
            requestFocus();
            return;
        }
        /**
         * If not end of dragging, just deselect all
         * and select this one
         */
        else if (SwingUtilities.isLeftMouseButton(e)
            && !desktop.isDraggingState()) {
            xPressed = e.getX();
            yPressed = e.getY();
            desktop.deselectAll();
            setSelected(true);
            requestFocus();
            desktop.repaint();
        }
    }

    /**
     * Paints desktop ICON depending on
     * state of ICON - selected or not,
     * has keyboard focus or not.
     */
    public void paintComponent(Graphics g) {
        Dimension d = getSize();
        if (icon != null && !isSelected()) {
            g.drawImage(icon.getImage(), (d.width - icon.getIconWidth()) / 2,
                (d.height - icon.getIconHeight()) / 2, this);
        } else if (icon != null && isSelected()) {
            g.drawImage(darkened_icon.getImage(),
                (d.width - darkened_icon.getIconWidth()) / 2,
                (d.height - darkened_icon.getIconHeight()) / 2,
                this);
        }

        Color sel_color = desktop.getSelectionColor();
        textLabel.setForeground(sel_color);
        textLabel.setSelected(isSelected());
        g.setColor(sel_color);

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
     * Writes desktop ICON to disk
     */
    public void save(ObjectOutputStream outputStream)
        throws java.io.IOException {
        outputStream.writeUTF(getName());
        outputStream.writeUTF(command);
        outputStream.writeUTF(working_dir);
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
     *
     * @param dimensions
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
            name = LangResource.getString("DesktopIcon.defaultName");
        }
        super.setName(name);
        textLabel.setText(name);
    }

    class BrightnessReducer extends RGBImageFilter implements Serializable {
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
     * Desktop ICON key listener
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
