package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.config.ServiceLocator;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.dialog.ApplicationChooserDialog;
import lombok.extern.java.Log;

/**
 * All plugin actions
 * @author Anton Troshin
 */
@Log
class RuntimeManagerActions {
    /**
     * Action property - this action work for editor
     */
    static final String PROCESS_ALIVE_ACTION = "PROCESS_ALIVE_ACTION";
    /**
     * Action property label
     */
    static final String ACTION_TYPE = "ACTION_TYPE";
    /**
     * Action property - this action work for editor
     */
    static final String PERSISTENT_ACTION = "PERSISTENT_ACTION";
    /**
     * Kill application action name
     */
    static final String KILL_ACTION_NAME = "Kill";
    /**
     * Kill all applications action name
     */
    static final String KILL_ALL_ACTION_NAME = "Kill All";
    /**
     * Kill and remove application action name
     */
    static final String KILL_AND_REMOVE_ACTION_NAME = "Kill And Remove";
    /**
     * Kill all and remove all applications action name
     */
    static final String KILL_AND_REMOVE_ALL_ACTION_NAME = "Kill And Remove All";
    /**
     * Copy log of selected applications action name
     */
    static final String COPY_LOG_ACTION_NAME = "Copy Log";
    /**
     * Start a new application action name
     */
    static final String START_ACTION_NAME = "Start";
    /**
     * Runtime manager window
     */
    private final RuntimeManagerWindow manager;
    /**
     * All actions
     */
    private final Map<String, Action> actions = new HashMap<>();

    /**
     * Default public constructor
     */
    RuntimeManagerActions(RuntimeManagerWindow manager) {
        super();
        this.manager = manager;
        createActions();
        enableActions(false);
    }

    public Action getAction(String name) {
        return actions.get(name);
    }

    /**
     * Enable actions of specified type
     */
    void enableActions(boolean flag, String type) {
        for (Action action : actions.values()) {
            String stype = (String) action.getValue(ACTION_TYPE);
            if (stype != null && stype.equals(type)) {
                action.setEnabled(flag);
            }
        }
    }

    void enableActions(boolean flag) {
        for (Action action : actions.values()) {
            if (!action.getValue(Action.NAME).
                equals(START_ACTION_NAME)) {
                action.setEnabled(flag);
            } else {
                action.setEnabled(true);
            }
        }
    }

    /**
     * Create actions
     */
    private void createActions() {

        Action killAction = new KillAction();
        Action killAllAction = new KillAllAction();
        Action killAndRemoveAction = new KillAndRemoveAction();
        Action killAndRemoveAllAction = new KillAndRemoveAllAction();
        Action copyLogAction = new CopyLogAction();
        Action startAction = new StartAction();

        actions.put(KILL_ACTION_NAME, killAction);
        actions.put(KILL_ALL_ACTION_NAME, killAllAction);
        actions.put(KILL_AND_REMOVE_ACTION_NAME, killAndRemoveAction);
        actions.put(KILL_AND_REMOVE_ALL_ACTION_NAME, killAndRemoveAllAction);
        actions.put(COPY_LOG_ACTION_NAME, copyLogAction);
        actions.put(START_ACTION_NAME, startAction);

    }

    protected class KillAction extends AbstractAction {
        KillAction() {
            super(KILL_ACTION_NAME);
            putValue(Action.SMALL_ICON,
                new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                    .getResourceAsImage("images/stop.png")));
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString(KILL_ACTION_NAME));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.kill();
            setEnabled(false);
        }
    }

    protected class KillAllAction extends AbstractAction {
        KillAllAction() {
            super(KILL_ALL_ACTION_NAME);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#249"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAll();
            setEnabled(false);
        }
    }

    protected class KillAndRemoveAction extends AbstractAction {
        KillAndRemoveAction() {
            super(KILL_AND_REMOVE_ACTION_NAME);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#250"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAndRemove();
            setEnabled(false);
        }
    }

    protected class KillAndRemoveAllAction extends AbstractAction {
        KillAndRemoveAllAction() {
            super(KILL_AND_REMOVE_ALL_ACTION_NAME);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#238"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAllAndRemove();
            setEnabled(false);
        }
    }

    protected class CopyLogAction extends AbstractAction {
        CopyLogAction() {
            super(COPY_LOG_ACTION_NAME);
            putValue(Action.SMALL_ICON,
                new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                    .getResourceAsImage("images/save.gif")));
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#253"));
            putValue(ACTION_TYPE, PERSISTENT_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.copyLog();
        }
    }

    protected class StartAction extends AbstractAction {
        StartAction() {
            super(START_ACTION_NAME);
            putValue(Action.SMALL_ICON,
                new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                    .getResourceAsImage("images/go.png")));
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString(START_ACTION_NAME));
            putValue(ACTION_TYPE, PERSISTENT_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            ApplicationChooserDialog dlg = new ApplicationChooserDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
            );
            dlg.setVisible(true);
            if (dlg.getSelectedApplication() != null) {
                try {
                    ServiceLocator
                        .getInstance()
                        .getRuntimeManager()
                        .run(
                            dlg.getSelectedApplication().getLinkString()
                        );
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
            manager.update();
        }
    }
}