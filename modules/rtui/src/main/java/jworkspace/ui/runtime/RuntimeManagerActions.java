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

import com.hyperrealm.kiwi.util.ResourceLoader;
import jworkspace.kernel.Workspace;
import jworkspace.ui.dialog.ApplicationChooserDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * All plugin actions
 */
public class RuntimeManagerActions {
    /**
     * Action property label
     */
    public static final String ACTION_TYPE = "ACTION_TYPE";
    /**
     * Action property - this action work for editor
     */
    public static final String PROCESS_ALIVE_ACTION = "PROCESS_ALIVE_ACTION";
    /**
     * Action property - this action work for editor
     */
    public static final String PERSISTENT_ACTION = "PERSISTENT_ACTION";
    /**
     * Runtime manager window
     */
    protected RuntimeManagerWindow manager = null;
    /**
     * All actions
     */
    protected Hashtable actions = new Hashtable();
    /**
     * Kill application action name
     */
    public final static String killActionName = "Kill";
    /**
     * Kill all applications action name
     */
    public final static String killAllActionName = "Kill All";
    /**
     * Kill and remove application action name
     */
    public final static String killAndRemoveActionName = "Kill And Remove";
    /**
     * Kill all and remove all applications action name
     */
    public final static String killAndRemoveAllActionName = "Kill And Remove All";
    /**
     * Copy log of selected applications action name
     */
    public final static String copyLogActionName = "Copy Log";
    /**
     * Start a new application action name
     */
    public final static String startActionName = "Start";
    /**
     * Kill application action
     */
    public static Action killAction;

    protected class KillAction extends AbstractAction {
        public KillAction() {
            super(killActionName);
            putValue(Action.SMALL_ICON,
                    new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/stop.png")));
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("Kill"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.kill();
            setEnabled(false);
        }
    }

    /**
     * Kill all applications action
     */
    public static Action killAllAction;

    protected class KillAllAction extends AbstractAction {
        public KillAllAction() {
            super(killAllActionName);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#249"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAll();
            setEnabled(false);
        }
    }

    /**
     * Kill and remove application action
     */
    public static Action killAndRemoveAction;

    protected class KillAndRemoveAction extends AbstractAction {
        public KillAndRemoveAction() {
            super(killAndRemoveActionName);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#250"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAndRemove();
            setEnabled(false);
        }
    }

    /**
     * Kill all and remove all applications action
     */
    public static Action killAndRemoveAllAction;

    protected class KillAndRemoveAllAction extends AbstractAction {
        public KillAndRemoveAllAction() {
            super(killAndRemoveAllActionName);
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("message#238"));
            putValue(ACTION_TYPE, PROCESS_ALIVE_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            manager.killAllAndRemove();
            setEnabled(false);
        }
    }

    /**
     * Copy log of selected applications action
     */
    public static Action copyLogAction;

    protected class CopyLogAction extends AbstractAction {
        public CopyLogAction() {
            super(copyLogActionName);
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

    /**
     * Start a new application action
     */
    public static Action startAction;

    protected class StartAction extends AbstractAction {
        public StartAction() {
            super(startActionName);
            putValue(Action.SMALL_ICON,
                    new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                            .getResourceAsImage("images/go.png")));
            putValue(Action.SHORT_DESCRIPTION, LangResource.getString("Start"));
            putValue(ACTION_TYPE, PERSISTENT_ACTION);
        }

        public void actionPerformed(ActionEvent evt) {
            ApplicationChooserDialog dlg =
                    new ApplicationChooserDialog(Workspace.getUI().getFrame());
            dlg.setVisible(true);
            if (dlg.getSelectedApplication() != null) {
                Workspace.getRuntimeManager().
                        run(dlg.getSelectedApplication().getLinkString());
            }
            manager.update();
        }
    }

    /**
     * Default public constructor
     */
    public RuntimeManagerActions(RuntimeManagerWindow manager) {
        super();
        this.manager = manager;
        createActions();
        enableActions(false);
    }

    public Action getAction(String name) {
        return (Action) actions.get(name);
    }

    public Action[] getActions() {
        Enumeration e=actions.elements();
        Action[] temp = new Action[actions.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (Action) e.nextElement();
        }
        return temp;
    }

    /**
     * Enable actions of specified type
     */
    public void enableActions(boolean flag, String type) {
        Action[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            String stype = (String) actions[i].getValue(ACTION_TYPE);
            if (stype == null) continue;
            else if (stype.equals(type)) {
                actions[i].setEnabled(flag);
            }
        }
    }

    public void enableActions(boolean flag) {
        Action[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            if (!actions[i].getValue(Action.NAME).
                    equals(startActionName))
                actions[i].setEnabled(flag);
            else
                actions[i].setEnabled(true);
        }
    }

    /**
     * Create actions
     */
    protected Hashtable createActions() {
        killAction = new KillAction();
        killAllAction = new KillAllAction();
        killAndRemoveAction = new KillAndRemoveAction();
        killAndRemoveAllAction = new KillAndRemoveAllAction();
        copyLogAction = new CopyLogAction();
        startAction = new StartAction();

        actions.put(killActionName, killAction);
        actions.put(killAllActionName, killAllAction);
        actions.put(killAndRemoveActionName, killAndRemoveAction);
        actions.put(killAndRemoveAllActionName, killAndRemoveAllAction);
        actions.put(copyLogActionName, copyLogAction);
        actions.put(startActionName, startAction);

        return actions;
    }
}