package jworkspace.ui.desktop.plaf;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.desktop.DesktopIcon;

public class DesktopShortcutMenu {

    private static JPopupMenu popupMenu = null;
    private static JMenuItem properties = null;
    private static JMenuItem delete = null;
    private static JMenuItem execute = null;
    private static JMenuItem cut = null;
    private static JMenuItem copy = null;

    /**
     * Returns "copy" menu item
     */
    private JMenuItem getCopy() {
        if (copy == null) {
            copy = new JMenuItem(WorkspaceResourceAnchor.getString("DesktopIcon.Copy"));
            copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            copy.addActionListener(e -> {
                if (popupMenu.getInvoker() instanceof DesktopIcon) {
                    ((DesktopIcon) popupMenu.getInvoker()).getDesktop().copyIcons();
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
                    ((DesktopIcon) popupMenu.getInvoker()).getDesktop().cutIcons();
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
                    ((DesktopIcon) popupMenu.getInvoker()).getDesktop().removeSelectedIcons();
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

    /**
     * Returns popup menu for this desktop icon
     */
    public JPopupMenu getPopupMenu() {
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
     * Returns a popup menu, depending on is there a selected group
     * of icons on the desktop and is this icon selected together
     * with others in that group.
     */
    public JPopupMenu getPopupMenu(boolean flag) {
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
}
