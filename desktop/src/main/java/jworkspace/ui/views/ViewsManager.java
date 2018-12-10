package jworkspace.ui.views;

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
import java.awt.Component;
import java.awt.Event;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.AbstractViewsManager;
import jworkspace.ui.IView;
import jworkspace.ui.action.UISwitchListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.desktop.Desktop;
import jworkspace.util.WorkspaceError;
import jworkspace.ui.Utils;

/**
 * Multidesktop manager is component for
 * managing gui views in common JFrame
 * environment. It adds special means for
 * adding, deleting, renaming, browsing of views.
 */
public class ViewsManager extends AbstractViewsManager {
    /**
     * Actions of multidesktop
     */
    protected ViewsActions actions;
    /**
     * The collection of views in current system.
     */
    private Vector views = new Vector();
    /**
     * The number of current panel.
     */
    private int currentView = 0;
    /**
     * Main menu
     */
    private JMenu go = new JMenu
        (LangResource.getString("ViewsManager.menu.go"));
    /**
     * Go back.
     */
    private JMenuItem go_back = null;
    /**
     * Go forward.
     */
    private JMenuItem go_forward = null;
    /**
     * New desktop.
     */
    private JMenuItem new_desktop = null;
    /**
     * Rename desktop
     */
    private JMenuItem properties = null;
    /**
     * Delete desktop
     */
    private JMenuItem delete_view = null;
    /**
     * Reload view
     */
    private JMenuItem reload_view = null;
    /**
     * Save view
     */
    private JMenuItem save_view = null;
    /**
     * Menu for Workspace system menubar.
     */
    private JRadioButtonMenuItem[] views_menu = new JRadioButtonMenuItem[]{};
    /**
     * Group of buttons
     */
    private ButtonGroup group = new ButtonGroup();
    /**
     * Header panel is included for information
     * purposes.
     */
    private HeaderPanel headerPanel = null;
    /**
     * Menu item for header panel toggle.
     */
    private JCheckBoxMenuItem toggle_header = null;
    /**
     * Scroller
     */
    private JScrollPane scroller = null;

    /**
     * Empty constructor.
     */
    public ViewsManager() {
        super();
        setOpaque(false);
        setLayout(new BorderLayout());
        /**
         * Create actions
         */
        this.actions = new ViewsActions(this);
        /**
         * Add scroller
         */
        add(getScroller(), BorderLayout.CENTER);
    }

    /**
     * Manager activated or deactivated
     */
    public void activated(boolean flag) {
        // does nothing, as this is not used in current
        // workspace x windows.
    }

    /**
     * Add desktop
     */
    public void addDesktop() {
        Frame parent = jworkspace.kernel.Workspace.getUI().getFrame();
        if (parent != null) {
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                getImage("desktop/desktop_big.png"));
            String result = (String) JOptionPane.showInputDialog(
                parent,
                LangResource.getString("ViewsManager.newDesktop.message"),
                LangResource.getString("ViewsManager.newDesktop.title"),
                JOptionPane.QUESTION_MESSAGE,
                icon, null, null);
            if (result == null) {
                return;
            }
            addDesktop(result);
        }
    }

    /**
     * Remove view
     */
    public void removeView() {
        Frame parent = jworkspace.kernel.Workspace.getUI().getFrame();
        if (parent != null) {
            int result;
            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                getImage("desktop/remove.png"));
            result = JOptionPane.showConfirmDialog(parent,
                LangResource.getString("ViewsManager.removeView.message"),
                LangResource.getString("ViewsManager.removeView.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
            if (result == JOptionPane.YES_OPTION) {
                deleteCurrentView();
            }
        }
    }

    /**
     * View properties
     */
    public void viewProperties() {
        Frame parent = jworkspace.kernel.Workspace.getUI().getFrame();
        PropertiesHolderDlg prhg = new PropertiesHolderDlg(parent,
            getCurrentView().getOptionPanels());
        prhg.setVisible(true);
        update();
    }

    /**
     * Creates and adds new desktop.
     */
    public void addDesktop(String title) {
        Desktop desktop = new Desktop();
        desktop.setName(title);
        addDesktop(desktop, true);
        UIManager.addPropertyChangeListener(new UISwitchListener(desktop));
    }

    /**
     * Adds desktop and displays it as current view if
     * appropriate flag is set.
     */
    public void addDesktop(Desktop desktop, boolean displayImmediately) {
        if (desktop == null) {
            return;
        }

        views.addElement(desktop);

        getScroller().setViewportView(desktop);

        if (displayImmediately) {
            setCurrentView(views.size() - 1);
        }
        UIManager.addPropertyChangeListener(new UISwitchListener(desktop));
        update();
    }

    /**
     * Return buttons for control panel
     */
    public CButton[] getButtons() {
        /**
         * Back button.
         */
        CButton b_back = Utils.createCButtonFromAction
            (actions.getAction(ViewsActions.browseBackActionName));
        /**
         * Forward button.
         */
        CButton b_forward = Utils.createCButtonFromAction
            (actions.getAction(ViewsActions.browseForwardActionName));
        /**
         * Add desktop.
         */
        CButton b_add = Utils.createCButtonFromAction
            (actions.getAction(ViewsActions.addDesktopActionName));
        /**
         * View desktop properties.
         */
        CButton b_prop = Utils.createCButtonFromAction
            (actions.getAction(ViewsActions.propertiesActionName));
        /**
         * Delete desktop.
         */
        CButton b_delete = Utils.createCButtonFromAction
            (actions.getAction(ViewsActions.removeViewActionName));

        return new CButton[]{b_back, b_forward, b_add, b_prop, b_delete};
    }

    /**
     * Get menu for installer.
     * Still there is no menus.
     */
    public JMenu[] getMenu() {
        go.setMnemonic
            (LangResource.getString("ViewsManager.go.key").charAt(0));
        /**
         * Back action.
         */
        go_back = Utils.createMenuItem
            (actions.getAction(ViewsActions.browseBackActionName));
        go.add(go_back);
        /**
         * Forward action.
         */
        go_forward = Utils.createMenuItem
            (actions.getAction(ViewsActions.browseForwardActionName));
        go.add(go_forward);
        go.addSeparator();
        /**
         * Toggle header panel
         */
        go.add(getToggleHeaderMenuItem());
        go.addSeparator();
        /**
         * Add desktop.
         */
        new_desktop = Utils.createMenuItem
            (actions.getAction(ViewsActions.addDesktopActionName));
        go.add(new_desktop);
        /**
         * Delete desktop.
         */
        delete_view = Utils.createMenuItem
            (actions.getAction(ViewsActions.removeViewActionName));
        go.add(delete_view);
        go.addSeparator();
        /**
         * Reload current view.
         */
        reload_view = Utils.createMenuItem
            (actions.getAction(ViewsActions.reloadViewActionName));
        go.add(reload_view);
        /**
         * Saves current view.
         */
        save_view = Utils.createMenuItem
            (actions.getAction(ViewsActions.saveViewActionName));
        go.add(save_view);
        /**
         * View desktop properties.
         */
        properties = Utils.createMenuItem
            (actions.getAction(ViewsActions.propertiesActionName));
        go.add(properties);
        go.addSeparator();

        setViewsList(getNames(), getCurrentViewNo());
        UIManager.addPropertyChangeListener(new UISwitchListener(go));

        return new JMenu[]{go};
    }

    /**
     * Get option panel for content manager
     */
    public JPanel[] getOptionPanels() {
        return null;
    }

    /**
     * Browses one step back.
     */
    public void browseBack() {
        if (canBrowseBack()) {
            setCurrentView(currentView - 1);
        }

        enableNavigationActions();
    }

    /**
     * Browses one step forward.
     */
    public void browseForward() {
        if (canBrowseForward()) {
            setCurrentView(currentView + 1);
        }

        enableNavigationActions();
    }

    /**
     * Checks if navigation can browse one step back.
     */
    public boolean canBrowseBack() {
        return currentView != 0;
    }

    /**
     * Checks if navigation can browse one step forward.
     */
    public boolean canBrowseForward() {
        return currentView != views.size() - 1;
    }

    /**
     * Closes allframes  on all desktop objects.
     */
    public void closeAllFrames() {
        for (int i = 0; i < views.size(); i++) {
            if (views.elementAt(i) instanceof Desktop) {
                ((Desktop) views.elementAt(i)).closeAllFrames();
            }
        }
    }

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create() {
        /**
         * Assemble default configuration.
         */
        addDesktop(LangResource.getString("Desktop.defaultName"));
        add(getHeaderPanel(), getHeaderPanel().getOrientation());
    }

    /**
     * Deletes current desktop.
     */
    public void deleteCurrentView() {
        if (views.size() == 1) {
            JOptionPane.showMessageDialog
                (Workspace.getUI().getFrame(),
                    LangResource.getString("ViewsManager.cannotRmView.message"),
                    LangResource.getString("ViewsManager.cannotRmView.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        /**
         * Remember view to delete
         */
        int index_to_delete = currentView;

        if (canBrowseForward()) {
            browseForward();
            views.removeElementAt(index_to_delete);
            currentView--;
        } else if (canBrowseBack()) {
            browseBack();
            views.removeElementAt(index_to_delete);
        }

        update();
    }

    /**
     * Get all views
     */
    public IView[] getAllViews() {
        IView[] all = new IView[views.size()];
        for (int i = 0; i < all.length; i++) {
            all[i] = (IView) views.elementAt(i);
        }
        return all;
    }

    /**
     * Returns current desktop.
     */
    public Desktop getCurrentDesktop() {
        if (views.elementAt(currentView)
            instanceof Desktop) {
            return (Desktop) views.elementAt(currentView);
        }
        return null;
    }

    /**
     * Returns current component.
     */
    public IView getCurrentView() {
        return (IView) views.elementAt(currentView);
    }

    /**
     * Sets desktop with number ascurrent and displays it.
     */
    public void setCurrentView(int index) {
        if (views.size() == 0) {
            return;
        }

        if (index < 0) {
            index = 0;
        } else if (index > views.size() - 1) {
            index = views.size() - 1;
        }

        /**
         * Notify component that it became inactive.
         */
        if (views.elementAt(currentView) instanceof IView) {
            try {
                ((IView) views.elementAt(currentView)).activated(false);
            } catch (Throwable err) {
                WorkspaceError.exception(LangResource.getString("ViewsManager.deactivate.fault"), err);
                // do nothing, just go on
            }
            /**
             * Ask for menus
             */
            JMenu[] menus = ((IView) views.elementAt(currentView)).getMenu();
            if (menus != null) {
                Hashtable lparam = new Hashtable();
                Vector vmenus = new Vector();
                for (int m = 0; m < menus.length; m++) {
                    vmenus.addElement(menus[m]);
                }
                lparam.put("menus", vmenus);
                lparam.put("flag", new Boolean(false));
                Workspace.fireEvent(new Integer(1002), lparam, null);
            }
        }
        currentView = index;
        getScroller().setViewportView((Component) views.elementAt(index));
        /**
         * Notify component that it became active.
         */
        if (views.elementAt(index) instanceof IView) {
            /**
             * Notify component that it became active.
             */
            try {
                ((IView) views.elementAt(index)).activated(true);
            } catch (Throwable err) {
                WorkspaceError.exception(LangResource.getString("ViewsManager.activate.fault"), err);
                // do nothing, just go on
            }
            /**
             * Ask for menus
             */
            JMenu[] menus = ((IView) views.elementAt(index)).getMenu();
            if (menus != null) {
                Hashtable lparam = new Hashtable();
                Vector vmenus = new Vector();
                for (int m = 0; m < menus.length; m++) {
                    vmenus.addElement(menus[m]);
                }
                lparam.put("menus", vmenus);
                lparam.put("flag", new Boolean(true));
                Workspace.fireEvent(new Integer(1002), lparam, null);
            }
        }
        update();
    }

    /**
     * Returns number of current view.
     */
    public int getCurrentViewNo() {
        return currentView;
    }

    /**
     * Returns desktop by title.
     */
    public Desktop getDesktop(String title) {
        for (int i = 0; i < views.size(); i++) {
            if (((Component) views.elementAt(i)).getName().equals(title)
                && views.elementAt(i) instanceof Desktop) {
                return (Desktop) views.elementAt(i);
            }
        }

        return null;
    }

    /**
     * Returns desktop by zero-based position.
     */
    public Desktop getDesktopAt(int i) {
        if (i < 0 || i > views.size() - 1) {
            return null;
        }

        if (views.elementAt(i) instanceof Desktop) {
            return (Desktop) views.elementAt(i);
        }

        return null;
    }

    /**
     * Safely returns header panel.
     */
    protected HeaderPanel getHeaderPanel() {
        if (headerPanel == null) {
            headerPanel = new HeaderPanel("");
        }
        return headerPanel;
    }

    /**
     * Returns orientation of header panel.
     */
    public String getHeaderPanelLocation() {
        return headerPanel.getOrientation();
    }

    /**
     * Header panel management
     */
    private JCheckBoxMenuItem getToggleHeaderMenuItem() {
        if (toggle_header == null) {
            toggle_header = Utils.createCheckboxMenuItem
                (actions.getAction(ViewsActions.switchHeaderActionName));
            toggle_header.setAccelerator
                (KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
        }
        return toggle_header;
    }

    /**
     * Returns scroller on which all views reside
     *
     * @return JScrollPane
     */
    public JScrollPane getScroller() {
        if (scroller == null) {
            scroller = new JScrollPane();
            scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        return scroller;
    }

    /**
     * Finds equal view and returns found.
     */
    public IView getView(IView newView) {
        for (int i = 0; i < views.size(); i++) {
            if (views.elementAt(i).equals(newView)) {
                return (IView) views.elementAt(i);
            }
        }
        return null;
    }

    /**
     * Check whether any of views are modified.
     */
    public boolean isModified() {
        for (int i = 0; i < views.size(); i++) {
            if (((IView) views.elementAt(i)).isModified()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns number of desktop denoted by its title.
     */
    public int getViewNo(JComponent comp) {
        for (int i = 0; i < views.size(); i++) {
            if (views.elementAt(i).equals(comp)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the list of all views.
     */
    public String[] getNames() {
        String[] temp = new String[views.size()];
        for (int i = 0; i < views.size(); i++) {
            temp[i] = ((JComponent) views.elementAt(i)).getName();
        }
        return temp;
    }

    /**
     * Renames current desktop.
     */
    public void renameCurrentDesktop(String name) {
        if (getCurrentDesktop() == null) {
            return;
        }

        getCurrentDesktop().setName(name);
        getHeaderPanel().setHeaderLabelText(name);
        setCurrentView(currentView);
    }

    /**
     * Sets the list of objects for management.
     */
    public void setDesktopList(Vector views) {
        for (int i = 0; i < views.size(); i++) {
            this.views.addElement(views.elementAt(i));
        }
    }

    /**
     * Sets header position.
     */
    public void setHeaderPosition(String position) {
        headerPanel.setOrientation(position);
    }

    /**
     * Switch view
     */
    public void switchView(int viewNo) {
        setCurrentView(viewNo);
    }

    /**
     * Switches control panel on and off.
     */
    protected void switchHeaderPanel() {
        getHeaderPanel().setVisible(!getHeaderPanel().isVisible());
        validate();
        repaint();
    }

    /**
     * Set navigation
     */
    private void enableNavigationActions() {
        /**
         * Determine the state of browse buttons
         */
        if (views.size() == 1) {
            actions.getAction
                (ViewsActions.browseBackActionName).setEnabled(false);
            actions.getAction
                (ViewsActions.browseForwardActionName).setEnabled(false);
            actions.getAction
                (ViewsActions.removeViewActionName).setEnabled(false);
        } else if (views.size() > 1) {
            if (currentView == 0) {
                actions.getAction
                    (ViewsActions.browseForwardActionName).setEnabled(true);
                actions.getAction
                    (ViewsActions.browseBackActionName).setEnabled(false);
            } else if (currentView == views.size() - 1) {
                actions.getAction
                    (ViewsActions.browseForwardActionName).setEnabled(false);
                actions.getAction
                    (ViewsActions.browseBackActionName).setEnabled(true);
            } else {
                actions.getAction
                    (ViewsActions.browseForwardActionName).setEnabled(true);
                actions.getAction
                    (ViewsActions.browseBackActionName).setEnabled(true);
            }
            actions.getAction
                (ViewsActions.removeViewActionName).setEnabled(true);
        }
    }

    /**
     * Updates UI components for the manager.
     */
    public void update() {
        if (getCurrentView() instanceof JComponent
            && ((JComponent) getCurrentView()).getName() != null) {
            getHeaderPanel().setHeaderLabelText
                (((JComponent) getCurrentView()).getName());
        } else {
            getHeaderPanel().setHeaderLabelText
                (LangResource.getString("ViewsManager.header.default"));
        }

        setViewsList(getNames(), getCurrentViewNo());
        getToggleHeaderMenuItem().setSelected(getHeaderPanel().isVisible());
        enableNavigationActions();
        revalidate();
        repaint();
    }

    /**
     * Adds view to collection.
     */
    public void addView(IView view, boolean displayImmediately,
                        boolean register) {
        if (view == null || !(view instanceof JComponent)) {
            throw new IllegalArgumentException
                (LangResource.getString("View should be a JComponent"));
        }

        if (register) {
            /**
             * Check if there is a registered but not nesesarily displayed
             * component.
             */
            Object existing = Workspace.getUI().isRegistered(view.getClass().getName());

            if (existing == null) {
                Workspace.getUI().register(view);
            }
        }
        if (getView(view) == null || view.isUnique() == false) {
            /**
             * If view is not added to collection.
             */
            views.addElement(view);
            //add((JComponent) view, BorderLayout.CENTER);
            getScroller().setViewportView((JComponent) view);
            if (displayImmediately) {
                setCurrentView(views.size() - 1);
            }
        } else {
            /**
             * Already added to collection.
             */
            if (displayImmediately) {
                setCurrentView(getViewNo((JComponent) view));
            }
        }

        UIManager.addPropertyChangeListener
            (new UISwitchListener((JComponent) view));
        update();
    }

    /**
     * Resets all views to initial state
     */
    public void reset() {
        for (int i = 0; i < views.size(); i++) {
            if (views.elementAt(i) instanceof IView) {
                ((IView) views.elementAt(i)).reset();
            }
        }
    }

    /**
     * Reloads current view from disk
     */
    public void reloadCurrentView() {
        try {
            if (getCurrentView() instanceof IView) {
                getCurrentView().reset();
                getCurrentView().load();
                ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                    getImage("desktop/reload.png"));
                JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                    LangResource.getString("ViewsManager.reload.message"),
                    LangResource.getString("ViewsManager.reload.title"),
                    JOptionPane.INFORMATION_MESSAGE, icon);
            }
        } catch (IOException ex) {
            WorkspaceError.exception
                (LangResource.getString("ViewsManager.reload.failed"), ex);
        }
    }

    /**
     * Loads profile data from multidesktop.dat.
     */
    public void load() {
        /**
         * Read header panel orientation and
         * visibility.
         */
        String fileName = Workspace.getUserHome()
            + getPath() + File.separator + "multidesktop.dat";
        Workspace.getLogger().info(">" + "Reading file" + " " + fileName + "...");
        try {
            FileInputStream inputFile = new FileInputStream(fileName);
            DataInputStream dataStream = new DataInputStream(inputFile);

            boolean visible = dataStream.readBoolean();
            String orientation = dataStream.readUTF();

            getHeaderPanel().setVisible(visible);
            getHeaderPanel().setOrientation(orientation);

            int size = dataStream.readInt();

            if (size >= 1) {
                for (int i = 0; i < size; i++) {
                    /**
                     * Construct relative path for loading shells
                     */
                    String viewSavePath = getPath() + File.separator +
                        "view_" + i;
                    /**
                     * Loads shell via GUI method.
                     */
                    Desktop desktop = new Desktop();
                    desktop.setPath(viewSavePath);
                    desktop.load();
                    addDesktop(desktop, false);
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            /**
             * Assemble default configuration.
             */
            create();
            update();
            return;
        }
        /**
         * Add header panel
         */
        setCurrentView(0);
        add(getHeaderPanel(), getHeaderPanel().getOrientation());
        update();
    }

    /**
     * Writes down configuration on disk.
     */
    public void save() throws IOException {
        String fileName = Workspace.getUserHome() +
            getPath() + File.separator + "multidesktop.dat";
        Workspace.getLogger().info(">" + "Writing file" + " " + fileName + "...");

        File file = new File(Workspace.getUserHome() + getPath());

        /**
         * Create or delete directory "multidesktop"
         */
        if (!file.exists()) {
            file.mkdirs();
        } else {
            KiwiUtils.deleteTree(file);
        }

        FileOutputStream outputFile = new FileOutputStream(fileName);
        DataOutputStream outputStream = new DataOutputStream(outputFile);

        outputStream.writeBoolean(getHeaderPanel().isVisible());
        outputStream.writeUTF(getHeaderPanel().getOrientation());
        /**
         * How many view are now opened?
         */
        int counter = 0;

        for (int i = 0; i < views.size(); i++) {
            if (views.elementAt(i) instanceof Desktop) {
                String viewSavePath = getPath() +
                    File.separator + "view_" + counter;
                File file1 = new File(Workspace.getUserHome() +
                    getPath() + File.separator +
                    "view_" + counter);
                if (!file1.exists()) {
                    file1.mkdirs();
                }

                ((Desktop) views.elementAt(i)).setPath(viewSavePath);
                ((Desktop) views.elementAt(i)).save();
                counter++;
            } else if (views.elementAt(i) instanceof IView) {
                ((IView) views.elementAt(i)).save();
            }
        }
        outputStream.writeInt(counter);
    }

    /**
     * Sets the list of desktop items
     */
    public void setViewsList(String[] names, int selected) {
        if (names == null) {
            return;
        }
        /**
         * Remove all items
         */
        if (views_menu != null) {
            /**
             * Else remove stuff, that is already in menu
             */
            for (int i = 0; i < views_menu.length; i++) {
                go.remove(views_menu[i]);
            }
            /**
             * Remove last separator
             */
            if (go.getMenuComponent(go.getMenuComponentCount() - 1)
                instanceof JSeparator) {
                go.remove(go.getMenuComponentCount() - 1);
            }
        }

        views_menu = new JRadioButtonMenuItem[names.length];

        if (names.length > 0) {
            go.addSeparator();
        }

        for (int i = 0; i < names.length; i++) {
            String itemname = names[i];

            if (itemname == null || itemname.equals("")) {
                itemname = new Integer(i).toString();
            }

            JRadioButtonMenuItem item = new JRadioButtonMenuItem(itemname);

            group.add(item);
            go.add(item);
            views_menu[i] = item;

            item.setAction(actions.createSwitchViewAction(i));
            item.setText(itemname);
            if (i == selected) {
                item.setSelected(true);
            }
        }
    }
}
