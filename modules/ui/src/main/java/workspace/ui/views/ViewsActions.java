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
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import jworkspace.LangResource;
import jworkspace.api.IConstants;
import jworkspace.kernel.Workspace;
import jworkspace.ui.action.AbstractStateAction;
import jworkspace.util.WorkspaceError;

/**
 * Actions of multidesktop manager
 */
public class ViewsActions implements IConstants {
    /**
     * Browse back action name
     */
    public static final String browseBackActionName =
        LangResource.getString("ViewsManager.back.name");
    /**
     * Browse forward action name
     */
    public static final String browseForwardActionName =
        LangResource.getString("ViewsManager.forward.name");
    /**
     * Add desktop action name
     */
    public static final String addDesktopActionName =
        LangResource.getString("ViewsManager.adesktop.name");
    /**
     * Remove view action name
     */
    public static final String removeViewActionName =
        LangResource.getString("ViewsManager.ddesktop.name");
    /**
     * Reload current view action name
     */
    public static final String reloadViewActionName =
        LangResource.getString("ViewsManager.reload.name");
    /**
     * Save current view action name
     */
    public static final String saveViewActionName =
        LangResource.getString("ViewsManager.saveview.name");
    /**
     * Properties action name
     */
    public static final String propertiesActionName =
        LangResource.getString("ViewsManager.vprefs.name") + "...";
    /**
     * Switch view
     */
    public static final String switchViewActionName = "SWITCH_TO";
    /**
     * Switch view header
     */
    public static final String switchHeaderActionName =
        LangResource.getString("ViewsManager.swheader.name");
    /**
     * All actions
     */
    protected Hashtable actions = new Hashtable();
    /**
     * ViewsManager manager instance
     */
    protected ViewsManager manager = null;
    /**
     * Browse forward action
     */
    protected Action browseForwardAction;
    /**
     * Browse back action
     */
    Action browseBackAction;
    /**
     * Add desktop action
     */
    Action addDesktopAction;
    /**
     * Remove view action
     */
    Action removeViewAction;
    /**
     * Reload view action
     */
    Action reloadViewAction;
    /**
     * Properties action
     */
    Action propertiesAction;
    /**
     * Save view action
     */
    Action saveViewAction;
    /**
     * Switch header action
     */
    Action switchHeaderAction;

    /**
     * Constructor actions.
     */
    public ViewsActions(ViewsManager manager) {
        super();
        this.manager = manager;
        createActions();
    }

    public Action getAction(String name) {
        return (Action) actions.get(name);
    }

    public SwitchViewAction createSwitchViewAction(int i) {
        return new SwitchViewAction(i);
    }

    public Action[] getActions() {
        Enumeration e = actions.elements();
        Action[] temp = new Action[actions.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (Action) e.nextElement();
        }
        return temp;
    }

    protected Hashtable createActions() {
        addDesktopAction = new AddDesktopAction();
        removeViewAction = new RemoveViewAction();
        reloadViewAction = new ReloadViewAction();
        propertiesAction = new PropertiesAction();

        browseBackAction = new BrowseBackAction();
        browseForwardAction = new BrowseForwardAction();

        saveViewAction = new SaveViewAction();
        switchHeaderAction = new SwitchHeaderAction();

        actions.put(addDesktopActionName, addDesktopAction);
        actions.put(removeViewActionName, removeViewAction);
        actions.put(reloadViewActionName, reloadViewAction);
        actions.put(propertiesActionName, propertiesAction);

        actions.put(browseBackActionName, browseBackAction);
        actions.put(browseForwardActionName, browseForwardAction);

        actions.put(saveViewActionName, saveViewAction);
        actions.put(switchHeaderActionName, switchHeaderAction);

        return actions;
    }

    public class BrowseBackAction extends AbstractAction {
        Image normal = Workspace.getResourceManager().
            getImage("cpanel/normal/back.png");
        Image hover = Workspace.getResourceManager().
            getImage("cpanel/hover/back.png");
        String description = LangResource.getString("ViewsManager.back.tooltip");

        BrowseBackAction() {
            super(browseBackActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.browseBack();
        }
    }

    public class BrowseForwardAction extends AbstractAction {
        Image normal = Workspace.getResourceManager().
            getImage("cpanel/normal/forward.png");
        Image hover = Workspace.getResourceManager().
            getImage("cpanel/hover/forward.png");
        String description = LangResource.
            getString("ViewsManager.forward.tooltip");

        BrowseForwardAction() {
            super(browseForwardActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.browseForward();
        }
    }

    public class AddDesktopAction extends AbstractAction {
        Image normal = Workspace.getResourceManager().
            getImage("cpanel/normal/adesktop.png");
        Image hover = Workspace.getResourceManager().
            getImage("cpanel/hover/adesktop.png");
        String description = LangResource.
            getString("ViewsManager.adesktop.tooltip");

        AddDesktopAction() {
            super(addDesktopActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.addDesktop();
        }
    }

    public class RemoveViewAction extends AbstractAction {
        Image normal = Workspace.getResourceManager().
            getImage("cpanel/normal/ddesktop.png");
        Image hover = Workspace.getResourceManager().
            getImage("cpanel/hover/ddesktop.png");
        String description = LangResource.
            getString("ViewsManager.ddesktop.tooltip");

        RemoveViewAction() {
            super(removeViewActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            Frame parent = Workspace.getUI().getFrame();
            if (parent != null) {
                int result;
                ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                    getImage("desktop/remove.png"));
                result = JOptionPane.showConfirmDialog(parent,
                    LangResource.getString("ViewsManager.deleteView.question") + "?",
                    LangResource.getString("ViewsManager.deleteView.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

                if (result == JOptionPane.YES_OPTION) {
                    manager.deleteCurrentView();
                }
            }
        }
    }

    public class ReloadViewAction extends AbstractAction {
        String description = LangResource.
            getString("ViewsManager.reload.tooltip");

        ReloadViewAction() {
            super(reloadViewActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            manager.reloadCurrentView();
        }
    }

    public class PropertiesAction extends AbstractAction {
        Image normal = Workspace.getResourceManager().
            getImage("cpanel/normal/vprefs.png");
        Image hover = Workspace.getResourceManager().
            getImage("cpanel/hover/vprefs.png");
        String description = LangResource.
            getString("ViewsManager.vprefs.tooltip");

        PropertiesAction() {
            super(propertiesActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
            this.putValue(Action.SMALL_ICON, new ImageIcon(normal));
        }

        public void actionPerformed(ActionEvent e) {
            manager.viewProperties();
        }
    }

    public class SaveViewAction extends AbstractAction {
        String description = LangResource.
            getString("ViewsManager.vsave.tooltip");

        SaveViewAction() {
            super(saveViewActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                manager.save();
            } catch (IOException ex) {
                WorkspaceError.exception
                    (LangResource.getString("ViewsManager.viewSave.failed"), ex);
            }
        }
    }

    /**
     * Switch view action
     */
    public class SwitchViewAction extends AbstractAction {
        int viewNo = 0;

        public SwitchViewAction(int viewNo) {
            super(switchViewActionName);
            this.viewNo = viewNo;
        }

        public void actionPerformed(ActionEvent e) {
            manager.switchView(viewNo);
        }
    }

    public class SwitchHeaderAction extends AbstractStateAction {
        String description = LangResource.getString("ViewsManager.swheader.tooltip");

        SwitchHeaderAction() {
            super(switchHeaderActionName);
            this.putValue(Action.SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
            manager.switchHeaderPanel();
            setSelected(manager.getHeaderPanel().isVisible());
        }
    }
}