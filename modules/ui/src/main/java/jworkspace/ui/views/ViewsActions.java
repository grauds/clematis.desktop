package jworkspace.ui.views;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.AbstractStateAction;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.widgets.WorkspaceError;

/**
 * Actions of desktop manager
 * @author Anton Troshin
 */
public class ViewsActions implements Constants {

    /**
     * Browse back action name
     */
    static final String BROWSE_BACK_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.back.name");
    /**
     * Browse forward action name
     */
    static final String BROWSE_FORWARD_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.forward.name");
    /**
     * Add desktop action name
     */
    static final String ADD_DESKTOP_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.adesktop.name");
    /**
     * Remove view action name
     */
    static final String REMOVE_VIEW_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.ddesktop.name");
    /**
     * Reload current view action name
     */

    static final String RELOAD_VIEW_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.reload.name");
    /**
     * Save current view action name
     */
    static final String SAVE_VIEW_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.saveview.name");
    /**
     * Properties action name
     */
    static final String PROPERTIES_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.vprefs.name") + "...";
    /**
     * Switch view header
     */
    static final String SWITCH_HEADER_ACTION_NAME =
        WorkspaceResourceAnchor.getString("ViewsManager.swheader.name");
    /**
     * Switch view
     */
    private static final String SWITCH_VIEW_ACTION_NAME = "SWITCH_TO";
    /**
     * All actions
     */
    private final Map<String, Action> actions = new HashMap<>();
    /**
     * ViewsManager manager instance
     */
    private final ViewsManager manager;

    /**
     * Constructor actions.
     */
    ViewsActions(ViewsManager manager) {
        super();
        this.manager = manager;
        createActions();
    }

    Action getAction(String name) {
        return actions.get(name);
    }

    SwitchViewAction createSwitchViewAction(int i) {
        return new SwitchViewAction(i);
    }

    private void createActions() {
        /*
         * Add desktop action
         */
        Action addDesktopAction = new AddDesktopAction();
        /*
         * Remove view action
         */
        Action removeViewAction = new RemoveViewAction();
        /*
         * Reload view action
         */
        Action reloadViewAction = new ReloadViewAction();
        /*
         * Properties action
         */
        Action propertiesAction = new PropertiesAction();
        /*
         * Browse back action
         */
        Action browseBackAction = new BrowseBackAction();
        /*
         * Browse forward action
         */
        Action browseForwardAction = new BrowseForwardAction();
        /*
         * Save view action
         */
        Action saveViewAction = new SaveViewAction();
        /*
         * Switch header action
         */
        Action switchHeaderAction = new SwitchHeaderAction();

        actions.put(ADD_DESKTOP_ACTION_NAME, addDesktopAction);

        actions.put(REMOVE_VIEW_ACTION_NAME, removeViewAction);

        actions.put(RELOAD_VIEW_ACTION_NAME, reloadViewAction);

        actions.put(PROPERTIES_ACTION_NAME, propertiesAction);

        actions.put(BROWSE_BACK_ACTION_NAME, browseBackAction);

        actions.put(BROWSE_FORWARD_ACTION_NAME, browseForwardAction);

        actions.put(SAVE_VIEW_ACTION_NAME, saveViewAction);

        actions.put(SWITCH_HEADER_ACTION_NAME, switchHeaderAction);
    }

    /**
     * @author Anton Troshin
     */
    public class BrowseBackAction extends AbstractAction {

        Image normal = WorkspaceGUI.getResourceManager().getImage("cpanel/normal/back.png");
        Image hover = WorkspaceGUI.getResourceManager().getImage("cpanel/hover/back.png");
        String description = WorkspaceResourceAnchor.getString("ViewsManager.back.tooltip");

        BrowseBackAction() {
            super(BROWSE_BACK_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.browseBack();
        }
    }

    /**
     * @author Anton Troshin
     */
    public class BrowseForwardAction extends AbstractAction {

        Image normal = WorkspaceGUI.getResourceManager().getImage("cpanel/normal/forward.png");
        Image hover = WorkspaceGUI.getResourceManager().getImage("cpanel/hover/forward.png");
        String description = WorkspaceResourceAnchor.getString("ViewsManager.forward.tooltip");

        BrowseForwardAction() {
            super(BROWSE_FORWARD_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.browseForward();
        }
    }

    /**
     * @author Anton Troshin
     */
    public class AddDesktopAction extends AbstractAction {

        Image normal = WorkspaceGUI.getResourceManager().getImage("cpanel/normal/adesktop.png");
        Image hover = WorkspaceGUI.getResourceManager().getImage("cpanel/hover/adesktop.png");
        String description = WorkspaceResourceAnchor.getString("ViewsManager.adesktop.tooltip");

        AddDesktopAction() {
            super(ADD_DESKTOP_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.addDesktop();
        }
    }

    /**
     * @author Anton Troshin
     */
    public class RemoveViewAction extends AbstractAction {

        Image normal = WorkspaceGUI.getResourceManager().getImage("cpanel/normal/ddesktop.png");
        Image hover = WorkspaceGUI.getResourceManager().getImage("cpanel/hover/ddesktop.png");
        String description = WorkspaceResourceAnchor.getString("ViewsManager.ddesktop.tooltip");

        RemoveViewAction() {
            super(REMOVE_VIEW_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            Frame parent = DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame();
            if (parent != null) {
                int result;
                ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().getImage("desktop/remove.png"));
                result = JOptionPane.showConfirmDialog(parent,
                    WorkspaceResourceAnchor.getString("ViewsManager.deleteView.question") + "?",
                    WorkspaceResourceAnchor.getString("ViewsManager.deleteView.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

                if (result == JOptionPane.YES_OPTION) {
                    manager.deleteCurrentView();
                }
            }
        }
    }

    /**
     * @author Anton Troshin
     */
    public class ReloadViewAction extends AbstractAction {
        String description = WorkspaceResourceAnchor.
            getString("ViewsManager.reload.tooltip");

        ReloadViewAction() {
            super(RELOAD_VIEW_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            manager.reloadCurrentView();
        }
    }

    /**
     * @author Anton Troshin
     */
    public class PropertiesAction extends AbstractAction {
        Image normal = WorkspaceGUI.getResourceManager().getImage("cpanel/normal/vprefs.png");
        Image hover = WorkspaceGUI.getResourceManager().getImage("cpanel/hover/vprefs.png");
        String description = WorkspaceResourceAnchor.getString("ViewsManager.vprefs.tooltip");

        PropertiesAction() {
            super(PROPERTIES_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.viewProperties();
        }
    }

    /**
     * @author Anton Troshin
     */
    public class SaveViewAction extends AbstractAction {
        String description = WorkspaceResourceAnchor.
            getString("ViewsManager.vsave.tooltip");

        SaveViewAction() {
            super(SAVE_VIEW_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                manager.save();
            } catch (IOException ex) {
                WorkspaceError.exception(WorkspaceResourceAnchor.getString("ViewsManager.viewSave.failed"), ex);
            }
        }
    }

    /**
     * Switch view action
     * @author Anton Troshin
     */
    public class SwitchViewAction extends AbstractAction {

        int viewNo;

        SwitchViewAction(int viewNo) {
            super(SWITCH_VIEW_ACTION_NAME);
            this.viewNo = viewNo;
        }

        public void actionPerformed(ActionEvent e) {
            manager.switchView(viewNo);
        }
    }
    /**
     * @author Anton Troshin
     */
    public class SwitchHeaderAction extends AbstractStateAction {
        String description = WorkspaceResourceAnchor.getString("ViewsManager.swheader.tooltip");

        SwitchHeaderAction() {
            super(SWITCH_HEADER_ACTION_NAME);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            manager.switchHeaderPanel();
            setSelected(manager.getHeaderPanel().isVisible());
        }
    }
}