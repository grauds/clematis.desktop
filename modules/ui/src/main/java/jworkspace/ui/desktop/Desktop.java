package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2025 Anton Troshin

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.ui.KDesktopPane;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IView;
import jworkspace.ui.api.PropertiesPanel;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.dialog.DesktopBackgroundPanel;
import jworkspace.ui.desktop.dialog.DesktopOptionsPanel;
import jworkspace.ui.desktop.plaf.DesktopInteractionLayer;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;
import jworkspace.ui.desktop.plaf.DesktopTheme;
import jworkspace.ui.widgets.ClassCache;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * The Desktop class represents a customizable desktop interface for managing
 * internal frames, views, and interactions. It provides functionality to add
 * views, handle internal frames, cascade or tile frames, manage themes, change
 * background images, and interact with other components.
 * <p>
 * This class supports activating or deactivating the desktop, resetting the
 * desktop by closing all frames, managing menu and option panels, and ensuring
 * unique views within the workspace.
 */
@SuppressWarnings({"MagicNumber"})
@Log
public class Desktop extends KDesktopPane implements IView, ActionListener, ClipboardOwner {

    @Getter
    private final DesktopShortcutsLayer shortcutsLayer;

    @Getter
    private final DesktopInteractionLayer interactionLayer;

    private final ScrollingDesktopManager manager;
    /**
     * Save path. Relative to user home
     */
    @Setter
    @Getter
    private String path = "";
    /**
     * Desktop visual properties.
     */
    @Getter
    private final DesktopTheme theme = new DesktopTheme();

    public Desktop() {
        super();

        manager = new ScrollingDesktopManager(this);
        setDesktopManager(manager);

        shortcutsLayer = new DesktopShortcutsLayer();
        add(shortcutsLayer, JLayeredPane.DEFAULT_LAYER);
        shortcutsLayer.setBounds(0, 0, getWidth(), getHeight());

        interactionLayer = new DesktopInteractionLayer(shortcutsLayer, this);
        interactionLayer.setBounds(0, 0, getWidth(), getHeight());
        add(interactionLayer, JLayeredPane.DRAG_LAYER);

        // Keep it sized with the desktop
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                shortcutsLayer.setBounds(0, 0, getWidth(), getHeight());
                interactionLayer.setBounds(0, 0, getWidth(), getHeight());
            }
        });

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    public Desktop(String desktopTitle) {
        this();
        setName(desktopTitle);
    }

    /**
     * Desktop activated or deactivated
     */
    public void activated(boolean flag) {}

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        switch (command) {

            case Constants.GRADIENT_FILL:
                this.theme.switchGradientFill();
                repaint();
                break;

            case Constants.BACKGROUND:
                changeBackground();
                break;

            case Constants.CLOSE_ALL_WINDOWS:
                this.closeAllFrames();
                break;

            case Constants.SWITCH_COVER:
                this.theme.switchCoverVisible();
                repaint();
                break;

            case Constants.CHOOSE_BACKGROUND_IMAGE:
                changeBackgroundImage();
                break;

            default:
                break;
        }
    }

    public void changeBackgroundImage() {
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
                    this.theme.setPathToImage(testPath);
                    this.theme.setCover(testCover);
                    repaint();
                }
            } catch (IOException e1) {
                log.severe(e1.getMessage());
            }
        }
    }

    public void changeBackground() {
        if (!this.theme.isGradientFill()) {
            Color color = JColorChooser.showDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                WorkspaceResourceAnchor.getString("Desktop.chooseBg.title"),
                getBackground()
            );

            if (color != null) {
                setBackground(color);
                this.theme.setSecondaryBackground(color);
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
                this.theme.getSecondaryBackground()
            );
            if (color1 != null) {
                setBackground(color1);
            }
            if (color2 != null) {
                this.theme.setSecondaryBackground(color2);
            }
            repaint();
        }
    }

    /**
     * Adds view to the desktop and nest it into JInternalFrame.
     * Desktop places each new view into JInternalFrame.
     * There can be only one frame with a given title by now.
     */
    public void addView(JComponent panel, boolean displayImmediately, boolean unique) {
        if (panel.getName() == null) {
            panel.setName(Constants.DESKTOP_NAME_DEFAULT);
        }

        JInternalFrame existing = findFrame(panel.getName());
        if (unique && existing != null && displayImmediately) {
            getDesktopManager().activateFrame(existing);
            return;
        }
        /*
         * Check if the window is fully visible
         */
        if (this.theme.getHpos() + this.theme.getHstep() + 500 > getWidth()) {
            this.theme.setHpos(50);
        } else {
            this.theme.setHpos(this.theme.getHpos() + this.theme.getHstep());
        }

        if (this.theme.getVpos() + this.theme.getVstep() + 400 > getHeight()) {
            this.theme.setVpos(50);
        } else {
            this.theme.setVpos(this.theme.getVpos() + this.theme.getVstep());
        }

        JInternalFrame nest;

        if (!(panel instanceof JInternalFrame)) {
            nest = new JInternalFrame(panel.getName(), true, true, true, true);
            nest.getContentPane().add(panel);

        } else {
            nest = (JInternalFrame) panel;
        }

        if (nest.getWidth() == 0 || nest.getHeight() == 0) {
            nest.setBounds(this.theme.getHpos(), this.theme.getVpos(), 500, 400);
        } else {
            nest.setLocation(this.theme.getHpos(), this.theme.getVpos());
        }
        add(nest);
        nest.setVisible(true);

        if (displayImmediately) {
            getDesktopManager().activateFrame(nest);
        }
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
      /*  if (desktopMenu == null) {
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
        }*/
        return new JMenu[]{};
    }

    /**
     * Get option panel for desktop
     */
    public PropertiesPanel[] getOptionPanels() {
        return new PropertiesPanel[]{
            new DesktopBackgroundPanel(this),
            new DesktopOptionsPanel(this)
        };
    }

    /**
     * Create the component from scratch. Used for default assembly of ui components.
     */
    public void create() {}

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
     * Returns true if some data is left in editors and should be saved. This
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
     * Set this flag to true if you want the component to be unique among all workspace views.
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
    @SuppressWarnings("checkstyle:EmptyBlock")
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
            this.theme.setSecondaryBackground(new Color(red, green, blue));

            this.theme.setGradientFill(dataStream.readBoolean());
            this.setOpaque(dataStream.readBoolean());
            this.theme.setHpos(dataStream.readInt());
            this.theme.setHstep(dataStream.readInt());
            this.theme.setRenderMode(dataStream.readInt());
            this.theme.setVpos(dataStream.readInt());
            this.theme.setVstep(dataStream.readInt());
            this.theme.setCoverVisible(dataStream.readBoolean());
            int size = dataStream.readInt();

            for (int i = 0; i < size; i++) {
                DesktopShortcut shortcut = new DesktopShortcut(null, "");
                shortcut.load(dataStream);
                this.shortcutsLayer.addShortcut(shortcut);
            }

            String pathToImage = dataStream.readUTF();
            if (pathToImage.trim().isEmpty() || !new File(pathToImage).exists()) {
                pathToImage = null;
            }
            this.theme.setPathToImage(pathToImage);

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

    public void lostOwnership(Clipboard clipboard, Transferable contents) {}

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        if (this.theme.isGradientFill() && isOpaque()) {
            Graphics2D g2 = (Graphics2D) g;
            int w = getSize().width;
            int h = getSize().height;
            g2.setPaint(new GradientPaint(0, 0, getBackground(), 0, h, this.theme.getSecondaryBackground()));
            g2.fill(new Rectangle(0, 0, w, h));
        }
        if (this.theme.getCover() != null && this.theme.isCoverVisible()) {
            /*
             * Drawing of a desktop image can occur in several rendering modes.
             */
            if (this.theme.getRenderMode() == Constants.CENTER_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    (getWidth() - this.theme.getCover().getIconWidth()) / 2,
                    (getHeight() - this.theme.getCover().getIconHeight()) / 2,
                    this
                );
            } else if (this.theme.getRenderMode() == Constants.STRETCH_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
                );
            } else if (this.theme.getRenderMode() == Constants.TILE_IMAGE) {
                int x = 0, y = 0;
                while (x < getWidth()) {
                    while (y < getHeight()) {
                        g.drawImage(
                            this.theme.getCover().getImage(),
                            x,
                            y,
                            this
                        );
                        y += this.theme.getCover().getIconHeight();
                    }
                    x += this.theme.getCover().getIconWidth();
                    y = 0;
                }
            } else if (this.theme.getRenderMode() == Constants.TOP_LEFT_CORNER_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    0,
                    0,
                    this
                );
            } else if (this.theme.getRenderMode() == Constants.BOTTOM_LEFT_CORNER_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    0,
                    this.getHeight() - this.theme.getCover().getIconHeight(),
                    this
                );
            } else if (this.theme.getRenderMode() == Constants.TOP_RIGHT_CORNER_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    this.getWidth() - this.theme.getCover().getIconWidth(),
                    0,
                    this
                );
            } else if (this.theme.getRenderMode() == Constants.BOTTOM_RIGHT_CORNER_IMAGE) {
                g.drawImage(
                    this.theme.getCover().getImage(),
                    this.getWidth() - this.theme.getCover().getIconWidth(),
                    this.getHeight() - this.theme.getCover().getIconHeight(),
                    this
                );
            }
        }
    }

    /**
     * Reset desktop by closing all frames
     */
    public void reset() {
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

            outputStream.writeInt(this.theme.getSecondaryBackground().getRed());
            outputStream.writeInt(this.theme.getSecondaryBackground().getGreen());
            outputStream.writeInt(this.theme.getSecondaryBackground().getBlue());

            outputStream.writeBoolean(this.theme.isGradientFill());
            outputStream.writeBoolean(isOpaque());
            outputStream.writeInt(this.theme.getHpos());
            outputStream.writeInt(this.theme.getHstep());
            outputStream.writeInt(this.theme.getRenderMode());
            outputStream.writeInt(this.theme.getVpos());
            outputStream.writeInt(this.theme.getVstep());
            outputStream.writeBoolean(this.theme.isCoverVisible());
            outputStream.writeInt(this.shortcutsLayer.getShortcuts().size());

            for (DesktopShortcut desktopShortcut : this.shortcutsLayer.getShortcuts()) {
                desktopShortcut.save(outputStream);
            }

            outputStream.writeUTF(Objects.requireNonNullElse(this.theme.getPathToImage(), ""));
            outputStream.writeBoolean(getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE);
            outputStream.flush();
        }
    }

    public void update() {
        this.revalidate();
        this.repaint();
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
     * Sets all component size properties (the maximum, minimum, preferred) to the given dimension.
     */
    private void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties (the maximum, minimum, preferred)
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