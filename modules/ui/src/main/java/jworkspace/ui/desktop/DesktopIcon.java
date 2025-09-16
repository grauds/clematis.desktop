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
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.utils.SwingUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * The desktop icon is a shortcut to command.
 */
@SuppressWarnings("MagicNumber")
public class DesktopIcon extends JComponent implements MouseListener, MouseMotionListener, FocusListener, Serializable {

    @Serial
    private static final long serialVersionUID = -14567890635540403L;
    private static final int DESKTOP_ICON_PREFERRED_SIZE = 130;

    private static JPopupMenu popupMenu = null;
    private static JMenuItem properties = null;
    private static JMenuItem delete = null;
    private static JMenuItem execute = null;
    private static JMenuItem cut = null;
    private static JMenuItem copy = null;

    protected transient Desktop desktop;

    private final BrightnessReducer br = new BrightnessReducer();

    private String command = "";

    @Setter
    @Getter
    private String workingDirectory;

    @Setter
    @Getter
    private int mode = Constants.JAVA_APP_MODE;

    @Getter
    private ImageIcon icon = null;

    @Getter
    private ImageIcon darkenedIcon = null;

    @Setter
    @Getter
    private boolean selected = false;
    // Internal components
    private final DesktopIconLabel textLabel = new DesktopIconLabel("", 11);

    @Getter
    private String comments;

    /**
     * Constructor for desktop icon.
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
    private DesktopIcon(String name,
                        String command,
                        String workingDir,
                        Desktop desktop,
                        ImageIcon icon,
                        String comments
    ) {
        super();

        this.setName(name);
        this.workingDirectory = workingDir;
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
     * Adds itself and text label to the desktop layout.
     */
    public void add() {
        add(desktop);
    }

    /**
     * Adds itself and text label to the desktop layout.
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
        DesktopIconDialog dlg = new DesktopIconDialog(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
        );
        dlg.setData(this);
        dlg.setVisible(true);
    }

    /**
     * Returns command line for this desktop icon
     */
    public String getCommandLine() {
        return command;
    }

    /**
     * Sets command line and HTML tooltip
     */
    public void setCommandLine(String command) {
        this.command = command;
        setTooltip(command, this.comments);
    }
    /**
     * Sets comments and HTML tooltip
     *
     * @param comments for this desktop icon
     */
    public void setComments(String comments) {
        this.comments = comments;
        setTooltip(this.command, comments);
    }

    private void setTooltip(String command, String comments) {
        this.setToolTipText("<html>" + (command != null ? command : " ") + "<br>" + " <i>" + comments + "</i></html>");
    }

    /**
     * Returns "copy" menu item
     */
    private JMenuItem getCopy() {
        if (copy == null) {
            copy = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Copy"));
            copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
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
            cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
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
     * Sets image for this desktop icon
     */
    public void setIcon(ImageIcon icon) {
        if (icon == null || icon.getIconHeight() == -1 || icon.getIconWidth() == -1 || icon.getImage() == null) {
            this.icon = (ImageIcon) WorkspaceGUI.getResourceManager().getIcon(Constants.DEFAULT_ICON);
        } else {
            this.icon = icon;
        }
        if (this.icon != null && this.icon.getImage() != null) {
            this.darkenedIcon = new ImageIcon(Toolkit.getDefaultToolkit().
                createImage(new FilteredImageSource(this.icon.getImage().getSource(), br)));
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
            getIcon(),
            getX(),
            getY(),
            getWidth(),
            getHeight(),
            getMode(),
            getComments());
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
        return new Dimension(DESKTOP_ICON_PREFERRED_SIZE, DESKTOP_ICON_PREFERRED_SIZE);
    }

    /**
     * Returns "properties" menu item
     */
    private JMenuItem getProperties() {
        if (properties == null) {
            properties = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Properties") + "...");
            properties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
            properties.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).edit();
                }
            });
        }
        return properties;
    }

    public void focusGained(FocusEvent e) {}

    public void focusLost(FocusEvent e) {
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            launch();
        }
    }

    /**
     * Execute the command line for this desktop icon
     */
    private void launch() {
      /*  if (mode == Constants.SCRIPTED_FILE_MODE) {
            // todo WorkspaceInterpreter.getInstance().sourceScriptFile(command);
        } else if (mode == Constants.SCRIPTED_METHOD_MODE) {
            // todo WorkspaceInterpreter.getInstance().executeScript(command);
        } else if (mode == Constants.JAVA_APP_MODE) {
            try {
                ServiceLocator.getInstance().getRuntimeManager().run(command);
            } catch (WorkspaceException | IOException ex) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("Desktop.cannotLaunch"), ex);
            }
        } else if (mode == Constants.NATIVE_COMMAND_MODE) {
            ServiceLocator.getInstance().getRuntimeManager().executeNativeCommand(command, workingDir);
        }*/
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
        setLocation(dataStream.readInt(), dataStream.readInt());
        setComments(dataStream.readUTF());
        
        Object obj = dataStream.readObject();
        if (obj instanceof ImageIcon) {
            setIcon((ImageIcon) obj);
        }
    }

    /**
     * Tells desktop to drag selected icons in the given direction.
     */
    public void mouseDragged(MouseEvent e) {
        System.out.println("mouseDragged: " + e.getX() + " " + e.getY());
        /*
         * Do not allow dragging with the right button.
         */
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        /*
         * If dragging occurs, set state isDragging() to true.
         */
        desktop.setDraggingState(true);
       // desktop.getIconGroup().moveTo(e.getX() - xPressed, e.getY() - yPressed);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("mousePressed: " + e.getX() + " " + e.getY());
        if (SwingUtilities.isRightMouseButton(e)) {
            /*
             * Hide a popup menu for this icon or group of icons.
             */
            remove(getPopupMenu());
            getPopupMenu().setVisible(false);
        } else if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
            /*
             * Select or deselect this icon from the group (Ctrl + left click).
             */
            setSelected(!isSelected());
        } else if (SwingUtilities.isLeftMouseButton(e)) {

            if (isSelected() || desktop.isSelectedInGroup(this)) {
                /*
                 * Start moving selected icons to a new location (left-click drag'n'drop).
                 */
                desktop.getGlassDragPane().activate(e.getPoint(), List.of(desktop.getDesktopIcons()));
            } else {
                /*
                 * Deselect all icons and select this one (left click).
                 */
                desktop.deselectAll();
                setSelected(true);
            }
        }
        requestFocus();
        desktop.repaint();
    }

    /**
     * Mouse released:
     * <ol>
     * <li> Move the selected group of icons to a new location if it was dragging operation.
     * <li> If the right mouse button or control is down - show a popup menu for this icon or group of icons.
     * <li> If no modifiers and this desktop icon is not a part of the group, deselect all and move focus to this icon.
     * </ol>
     */
    public void mouseReleased(MouseEvent e) {

        System.out.println("mouseReleased: " + e.getX() + " " + e.getY());
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
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (desktop.isDraggingState()) {
                System.out.println("mouseReleased: isDraggingState()");
                /*
                 * It is an end of dragging - move icons.
                 */
                desktop.getIconGroup().destroy();
                desktop.setDraggingState(false);
            } else {
                /*
                 * If not end of dragging, just deselect all and select this one
                 */
                desktop.deselectAll();
                setSelected(true);
            }
            requestFocus();
        }

        desktop.revalidate();
        desktop.repaint();
    }

    /**
     * Paints desktop icon depending on the state of icon - selected or not, has keyboard focus or not.
     */
    public void paintComponent(Graphics g) {
       /*
        * Paint the selected state
        */
        if (!isSelected()) {
            g.setColor(getBackground());
        } else {
            g.setColor(UIManager.getColor("textHighlight"));
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        /*
         * Paint the focus state if the icon has focus.
         */
        if (hasFocus()) {
            SwingUtils.drawDashedRect(g, 0, 0, getWidth(), getHeight());
        }
       /*
        * Draw icon or darkened icon if the icon is selected.
        */
        Dimension d = getSize();
        if (isSelected()) {
            if (darkenedIcon != null) {
                g.drawImage(darkenedIcon.getImage(),
                    (d.width - darkenedIcon.getIconWidth()) / 2,
                    (d.height - darkenedIcon.getIconHeight()) / 2,
                    this);
            }
        } else {
            if (icon != null) {
                g.drawImage(icon.getImage(), (d.width - icon.getIconWidth()) / 2,
                    (d.height - icon.getIconHeight()) / 2, this);
            }
        }

        Color desktopSelectionColor = desktop.getTheme().getSelectionColor(desktop.getBackground());
        textLabel.setForeground(desktopSelectionColor);
        textLabel.setSelected(isSelected());

        g.setColor(desktopSelectionColor);
        textLabel.setBackground(desktop.getBackground());
        textLabel.repaint();
    }

    /**
     * Removes itself and label from the layout.
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
        outputStream.writeUTF(workingDirectory);
        outputStream.writeInt(mode);
        outputStream.writeInt(getX());
        outputStream.writeInt(getY());
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
        textLabel.setBounds(
            x - (textLabel.getPreferredSize().width - width) / 2,
            y + height + 3,
            textLabel.getPreferredSize().width, textLabel.getPreferredSize().height
        );
    }

    /**
     * Base class method is overridden to set bounds of the label accordingly.
     */
    public void setBounds(@NonNull Rectangle dimensions) {
        super.setBounds(dimensions);
        textLabel.setBounds(
            dimensions.x - (textLabel.getPreferredSize().width - dimensions.width) / 2,
            dimensions.y + dimensions.height + 3,
            textLabel.getPreferredSize().width,
            textLabel.getPreferredSize().height
        );
    }

    /**
     * The basic method is overridden to set label text properly.
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
                && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                ((DesktopIcon) e.getSource()).desktop.copyIcons();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_X
                && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                ((DesktopIcon) e.getSource()).desktop.cutIcons();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_P
                && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                ((DesktopIcon) e.getSource()).edit();
                e.consume();
            }
            desktop.dispatchEvent(e);
        }
    }
}
