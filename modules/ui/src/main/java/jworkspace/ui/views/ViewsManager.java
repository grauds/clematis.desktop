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
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.AbstractViewsManager;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IView;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.api.cpanel.CButton;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.utils.SwingUtils;
import lombok.extern.java.Log;

/**
 * Default desktop manager drives views in common JFrame environment.
 * It may add/delete/switch the view in a card layout fashion.
 *
 * @author Anton Troshin
 */
@Log
public class ViewsManager extends AbstractViewsManager {
    /**
     * The name for the configuration saving file
     */
    private static final String DESKTOP_CONFIG = "desktops.dat";
    /**
     * The prefix for view
     */
    private static final String VIEW = "view_";
    /*
     * Actions list
     */
    private final ViewsActions actions;
    /**
     * The collection of views in current system.
     */
    private final List<IView> views = new Vector<>();
    /**
     * The number of current panel.
     */
    private int currentView = 0;
    /**
     * Main menu
     */
    private final JMenu go = new JMenu(WorkspaceResourceAnchor.getString("ViewsManager.menu.go"));
    /**
     * Menu for Workspace system menubar.
     */
    private JRadioButtonMenuItem[] viewsMenu = new JRadioButtonMenuItem[]{};
    /**
     * Group of buttons
     */
    private final ButtonGroup group = new ButtonGroup();
    /**
     * Header panel is included for information
     * purposes.
     */
    private HeaderPanel headerPanel = null;
    /**
     * Menu item for header panel toggle.
     */
    private JCheckBoxMenuItem toggleHeader = null;
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
        /*
         * Create actions
         */
        this.actions = new ViewsActions(this);
        /*
         * Add scroller
         */
        add(getScroller(), BorderLayout.CENTER);
    }

   /**
     * Add desktop
     */
    void addDesktop() {
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
        if (parent != null) {

            ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().
                getImage("desktop/desktop_big.png"));

            String result = (String) JOptionPane.showInputDialog(
                parent,
                WorkspaceResourceAnchor.getString("ViewsManager.newDesktop.message"),
                WorkspaceResourceAnchor.getString("ViewsManager.newDesktop.title"),
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

        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();

        if (parent != null) {
            int result;

            ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().
                getImage("desktop/remove.png"));

            result = JOptionPane.showConfirmDialog(parent,
                WorkspaceResourceAnchor.getString("ViewsManager.removeView.message"),
                WorkspaceResourceAnchor.getString("ViewsManager.removeView.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
            if (result == JOptionPane.YES_OPTION) {
                deleteCurrentView();
            }
        }
    }

    /**
     * View properties
     */
    void viewProperties() {
        Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
        PropertiesHolderDlg prhg = new PropertiesHolderDlg(parent, getCurrentView().getOptionPanels());
        prhg.setVisible(true);
        update();
    }

    /**
     * Creates and adds new desktop.
     */
    private void addDesktop(String title) {
        Desktop desktop = new Desktop();
        desktop.setName(title);
        addDesktop(desktop, true);
        UIManager.addPropertyChangeListener(new UISwitchListener(desktop));
    }

    /**
     * Adds desktop and displays it as current view if
     * appropriate flag is set.
     */
    private void addDesktop(Desktop desktop, boolean displayImmediately) {
        if (desktop == null) {
            return;
        }

        views.add(desktop);

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
        /*
         * Back button.
         */
        CButton bBack = SwingUtils.createCButtonFromAction(actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME));
        /*
         * Forward button.
         */
        CButton bForward = SwingUtils.createCButtonFromAction(
            actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME)
        );
        /*
         * Add desktop.
         */
        CButton bAdd = SwingUtils.createCButtonFromAction(actions.getAction(ViewsActions.ADD_DESKTOP_ACTION_NAME));
        /*
         * View desktop properties.
         */
        CButton bProp = SwingUtils.createCButtonFromAction(actions.getAction(ViewsActions.PROPERTIES_ACTION_NAME));
        /*
         * Delete desktop.
         */
        CButton bDelete = SwingUtils.createCButtonFromAction(actions.getAction(ViewsActions.REMOVE_VIEW_ACTION_NAME));

        return new CButton[]{bBack, bForward, bAdd, bProp, bDelete};
    }

    /**
     * Get menu for installer.
     * Still there is no menus.
     */
    public JMenu[] getMenu() {

        go.setMnemonic(WorkspaceResourceAnchor.getString("ViewsManager.go.key").charAt(0));
        /*
         * Back action.
         */
        JMenuItem goBack = SwingUtils.createMenuItem(actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME));
        go.add(goBack);
        /*
         * Forward action.
         */
        JMenuItem goForward = SwingUtils.createMenuItem(actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME));
        go.add(goForward);
        go.addSeparator();
        /*
         * Toggle header panel
         */
        go.add(getToggleHeaderMenuItem());
        go.addSeparator();
        /*
         * Add desktop.
         */
        JMenuItem newDesktop = SwingUtils.createMenuItem(actions.getAction(ViewsActions.ADD_DESKTOP_ACTION_NAME));
        go.add(newDesktop);
        /*
         * Delete desktop.
         */
        JMenuItem deleteView = SwingUtils.createMenuItem(actions.getAction(ViewsActions.REMOVE_VIEW_ACTION_NAME));
        go.add(deleteView);
        go.addSeparator();
        /*
         * Reload current view.
         */
        JMenuItem reloadView = SwingUtils.createMenuItem(actions.getAction(ViewsActions.RELOAD_VIEW_ACTION_NAME));
        go.add(reloadView);
        /*
         * Saves current view.
         */
        JMenuItem saveView = SwingUtils.createMenuItem(actions.getAction(ViewsActions.SAVE_VIEW_ACTION_NAME));
        go.add(saveView);
        /*
         * View desktop properties.
         */
        JMenuItem properties = SwingUtils.createMenuItem(actions.getAction(ViewsActions.PROPERTIES_ACTION_NAME));
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
    void browseBack() {
        if (canBrowseBack()) {
            setCurrentView(currentView - 1);
        }

        enableNavigationActions();
    }

    /**
     * Browses one step forward.
     */
    void browseForward() {
        if (canBrowseForward()) {
            setCurrentView(currentView + 1);
        }

        enableNavigationActions();
    }

    /**
     * Checks if navigation can browse one step back.
     */
    private boolean canBrowseBack() {
        return currentView != 0;
    }

    /**
     * Checks if navigation can browse one step forward.
     */
    private boolean canBrowseForward() {
        return currentView != views.size() - 1;
    }

    /**
     * Closes all frames on all desktop objects.
     */
    public void closeAllFrames() {
        for (IView view : views) {
            if (view instanceof Desktop) {
                ((Desktop) view).closeAllFrames();
            }
        }
    }

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create() {
        /*
         * Assemble default configuration.
         */
        addDesktop(WorkspaceResourceAnchor.getString("Desktop.defaultName"));
        add(getHeaderPanel(), getHeaderPanel().getOrientation());
    }

    /**
     * Deletes current desktop.
     */
    public void deleteCurrentView() {

        if (views.size() == 1) {
            JOptionPane.showMessageDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                WorkspaceResourceAnchor.getString("ViewsManager.cannotRmView.message"),
                WorkspaceResourceAnchor.getString("ViewsManager.cannotRmView.title"),
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        /*
         * Remember view to delete
         */
        int indexToDelete = currentView;

        if (canBrowseForward()) {
            browseForward();
            views.remove(indexToDelete);
            currentView--;
        } else if (canBrowseBack()) {
            browseBack();
            views.remove(indexToDelete);
        }

        update();
    }

    /**
     * Get all views
     */
    public IView[] getAllViews() {
        IView[] all = new IView[views.size()];
        for (int i = 0; i < all.length; i++) {
            all[i] = views.remove(i);
        }
        return all;
    }

    /**
     * Returns current desktop.
     */
    private Desktop getCurrentDesktop() {
        if (views.get(currentView) instanceof Desktop) {
            return (Desktop) views.get(currentView);
        }
        return null;
    }

    /**
     * Returns current component.
     */
    public IView getCurrentView() {
        return views.get(currentView);
    }

    /**
     * Sets desktop with number as current and displays it.
     */
    private void setCurrentView(int index) {

        int localIndex = index;

        if (views.isEmpty()) {
            return;
        }

        if (index < 0) {
            localIndex = 0;
        } else if (index > views.size() - 1) {
            localIndex = views.size() - 1;
        }

        /*
         * Notify component that it became inactive.
         */
        if (views.get(currentView) != null) {
            try {
                views.get(currentView).activated(false);
            } catch (Throwable err) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("ViewsManager.deactivate.fault"), err);
                // do nothing, just go on
            }
            /*
             * Ask for menus
             */
            JMenu[] menus = views.get(currentView).getMenu();
            if (menus != null) {
                Map<String, Object> lparam = new HashMap<>();
                List<JMenu> vmenus = new Vector<>(Arrays.asList(menus));
                lparam.put(Constants.MENUS_PARAMETER, vmenus);
                lparam.put(Constants.FLAG_PARAMETER, Boolean.FALSE);
                ServiceLocator
                    .getInstance()
                    .getEventsDispatcher()
                    .fireEvent(WorkspaceGUI.SwitchMenuListener.CODE, lparam, null);
            }
        }
        currentView = localIndex;
        getScroller().setViewportView((Component) views.get(index));
        /*
         * Notify component that it became active.
         */
        if (views.get(localIndex) != null) {
            /*
             * Notify component that it became active.
             */
            try {
                views.get(index).activated(true);
            } catch (Throwable err) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("ViewsManager.activate.fault"), err);
                // do nothing, just go on
            }
            /*
             * Ask for menus
             */
            JMenu[] menus = views.get(index).getMenu();
            if (menus != null) {
                Map<String, Object> lparam = new HashMap<>();

                List<JMenu> vmenus = new Vector<>();
                Collections.addAll(vmenus, menus);

                lparam.put(Constants.MENUS_PARAMETER, vmenus);
                lparam.put(Constants.FLAG_PARAMETER, Boolean.TRUE);
                ServiceLocator
                    .getInstance()
                    .getEventsDispatcher()
                    .fireEvent(WorkspaceGUI.SwitchMenuListener.CODE, lparam, null);
            }
        }
        update();
    }

    /**
     * Returns number of current view.
     */
    private int getCurrentViewNo() {
        return currentView;
    }

    /**
     * Finds desktop by title.
     */
    public Desktop getDesktop(String title) {
        for (IView view : views) {
            if (((Component) view).getName().equals(title)
                && view instanceof Desktop) {
                return (Desktop) view;
            }
        }

        return null;
    }

    /**
     * Returns desktop by zero-based position.
     */
    public Desktop getDesktopAt(int i) {

        if (i > 0 && i <= views.size() - 1) {
            if (views.get(i) instanceof Desktop) {
                return (Desktop) views.get(i);
            }
        }
        return null;
    }

    /**
     * Safely returns header panel.
     */
    HeaderPanel getHeaderPanel() {
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
        if (toggleHeader == null) {
            toggleHeader = SwingUtils.createCheckboxMenuItem(actions.getAction(ViewsActions.SWITCH_HEADER_ACTION_NAME));
            toggleHeader.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        }
        return toggleHeader;
    }

    /**
     * Returns scroller on which all views reside
     *
     * @return JScrollPane
     */
    private JScrollPane getScroller() {
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
    private IView getView(IView newView) {
        for (IView view : views) {
            if (view.equals(newView)) {
                return view;
            }
        }
        return null;
    }

    /**
     * Check whether any of views are modified.
     */
    public boolean isModified() {
        for (IView view : views) {
            if (view.isModified()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns number of desktop denoted by its title.
     */
    private int getViewNo(JComponent comp) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i).equals(comp)) {
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
            temp[i] = ((JComponent) views.get(i)).getName();
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
    public void setDesktopList(List<IView> views) {
        this.views.addAll(views);
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
    void switchView(int viewNo) {
        setCurrentView(viewNo);
    }

    /**
     * Switches control panel on and off.
     */
    void switchHeaderPanel() {
        getHeaderPanel().setVisible(!getHeaderPanel().isVisible());
        validate();
        repaint();
    }

    /**
     * Set navigation
     */
    private void enableNavigationActions() {
        /*
         * Determine the state of browse buttons
         */
        if (views.size() == 1) {
            actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME).setEnabled(false);
            actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME).setEnabled(false);
            actions.getAction(ViewsActions.REMOVE_VIEW_ACTION_NAME).setEnabled(false);
        } else if (views.size() > 1) {
            if (currentView == 0) {
                actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME).setEnabled(true);
                actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME).setEnabled(false);
            } else if (currentView == views.size() - 1) {
                actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME).setEnabled(false);
                actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME).setEnabled(true);
            } else {
                actions.getAction(ViewsActions.BROWSE_FORWARD_ACTION_NAME).setEnabled(true);
                actions.getAction(ViewsActions.BROWSE_BACK_ACTION_NAME).setEnabled(true);
            }
            actions.getAction(ViewsActions.REMOVE_VIEW_ACTION_NAME).setEnabled(true);
        }
    }

    /**
     * Updates UI components for the manager.
     */
    public void update() {
        if (getCurrentView() instanceof JComponent
            && ((JComponent) getCurrentView()).getName() != null) {
            getHeaderPanel().setHeaderLabelText(((JComponent) getCurrentView()).getName());
        } else {
            getHeaderPanel().setHeaderLabelText(WorkspaceResourceAnchor.getString("ViewsManager.header.default"));
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
    public void addView(IView view, boolean displayImmediately, boolean register) {

        if (!(view instanceof JComponent)) {
            throw new IllegalArgumentException(WorkspaceResourceAnchor.getString("View should be a JComponent"));
        }

        if (getView(view) == null || !view.isUnique()) {
            /*
             * If view is not added to collection.
             */
            views.add(view);
            getScroller().setViewportView((JComponent) view);
            if (displayImmediately) {
                setCurrentView(views.size() - 1);
            }
        } else {
            /*
             * Already added to collection.
             */
            if (displayImmediately) {
                setCurrentView(getViewNo((JComponent) view));
            }
        }

        UIManager.addPropertyChangeListener(new UISwitchListener((JComponent) view));
        update();
    }

    /**
     * Resets all views to initial state
     */
    public void reset() {
        for (IView view : views) {
            if (view != null) {
                view.reset();
            }
        }
    }

    /**
     * Reloads current view from disk
     */
    void reloadCurrentView() {
        try {
            if (getCurrentView() != null) {
                getCurrentView().reset();
                getCurrentView().load();
                ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().
                    getImage("desktop/reload.png"));
                JOptionPane.showMessageDialog(
                    DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                    WorkspaceResourceAnchor.getString("ViewsManager.reload.message"),
                    WorkspaceResourceAnchor.getString("ViewsManager.reload.title"),
                    JOptionPane.INFORMATION_MESSAGE, icon
                );
            }
        } catch (IOException ex) {
            WorkspaceError.exception(WorkspaceResourceAnchor.getString("ViewsManager.reload.failed"), ex);
        }
    }

    /**
     * Loads profile data from desktop.dat.
     */
    public void load() throws IOException {

        // Read header panel orientation and visibility.
        File file = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(getPath())
            .resolve(DESKTOP_CONFIG)
            .toFile();

        try (FileInputStream inputFile = new FileInputStream(file);
             DataInputStream dataStream = new DataInputStream(inputFile)) {

            boolean visible = dataStream.readBoolean();
            String orientation = dataStream.readUTF();

            getHeaderPanel().setVisible(visible);
            getHeaderPanel().setOrientation(orientation);

            int size = dataStream.readInt();

            for (int i = 0; i < size; i++) {

                // get desktop data path
                String viewSavePath = getPath() + File.separator + VIEW + i;

                // load a desktop
                Desktop desktop = new Desktop();
                desktop.setPath(viewSavePath);
                desktop.load();

                // add desktop to the layout
                addDesktop(desktop, false);
            }

            // Add header panel
            setCurrentView(0); // todo save current view number
            add(getHeaderPanel(), getHeaderPanel().getOrientation());

            update();

        } catch (IOException e) {
            // Assemble default configuration.
            create();
            update();
        }
    }

    /**
     * Writes down configuration on disk.
     */
    public void save() throws IOException {

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
        } else {
            KiwiUtils.deleteTree(file);
        }

        file = ServiceLocator
            .getInstance()
            .getProfilesManager()
            .ensureUserHomePath()
            .resolve(getPath())
            .resolve(DESKTOP_CONFIG)
            .toFile();

        try (FileOutputStream outputFile = new FileOutputStream(file);
            DataOutputStream outputStream = new DataOutputStream(outputFile)) {

            outputStream.writeBoolean(getHeaderPanel().isVisible());
            outputStream.writeUTF(getHeaderPanel().getOrientation());

            int counter = 0;

            for (IView view : views) {
                if (view instanceof Desktop) {

                    File viewSavePath = Paths.get(getPath(), VIEW + counter).toFile();

                    Path file1 = ServiceLocator
                        .getInstance()
                        .getProfilesManager()
                        .ensureUserHomePath()
                        .resolve(getPath())
                        .resolve(VIEW)
                        .resolve(String.valueOf(counter));

                    if (!Files.exists(file1)) {
                        Files.createDirectories(file1);
                    }

                    view.setPath(viewSavePath.toPath().toString());
                    counter++;
                }
                view.save();
            }
            outputStream.writeInt(counter);
        }
    }

    /**
     * Sets the list of desktop items
     */
    private void setViewsList(String[] names, int selected) {
        if (names == null) {
            return;
        }
        /*
         * Remove all items
         */
        if (viewsMenu != null) {
            /*
             * Else remove stuff, that is already in menu
             */
            for (JRadioButtonMenuItem menu : viewsMenu) {
                go.remove(menu);
            }
        }

        viewsMenu = new JRadioButtonMenuItem[names.length];

        for (int i = 0; i < names.length; i++) {
            String itemname = names[i];

            if (itemname == null || itemname.isEmpty()) {
                itemname = Integer.toString(i);
            }

            JRadioButtonMenuItem item = new JRadioButtonMenuItem(itemname);

            group.add(item);
            go.add(item);
            viewsMenu[i] = item;

            item.setAction(actions.createSwitchViewAction(i));
            item.setText(itemname);
            if (i == selected) {
                item.setSelected(true);
            }
        }
    }
}
