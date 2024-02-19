/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.hyperrealm.kiwi.event.workspace.WorkspaceEvent;
import com.hyperrealm.kiwi.event.workspace.WorkspaceListener;
import com.hyperrealm.kiwi.ui.dialog.DialogSet;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class represents a workspace manager. The manager controls a set of
 * <code>WorkspaceEditor</code>s and their relationship to a
 * <code>JDesktopPane</code>. Each <code>WorkspaceEditor</code> is associated
 * with an arbitrary user object (the object that it is editing) and is
 * represented by a <code>JInternalFrame</code> in the desktop. The manager's
 * duties include saving unsaved changes in editors when their windows are
 * closed and providing a high-level interface for retrieving information
 * about the editors in the desktop.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.WorkspaceEditor
 * @see javax.swing.JDesktopPane
 */
@SuppressWarnings("unused")
public class WorkspaceManager {

    private static final String KIWI_DIALOG_MESSAGE_SAVE_TO = "kiwi.dialog.message.save_to";

    private static final String KIWI_DIALOG_MESSAGE_SAVE_FAILED = "kiwi.dialog.message.save_failed";

    private static final String UNTITLED = "(Untitled)";

    private ArrayList<WorkspaceEditor> editors;

    private final ArrayList<WorkspaceListener> listeners;

    private JDesktopPane desktop;

    private WorkspaceEditor activeEditor = null;

    private FrameListener frameListener;

    private DialogSet dialogs;

    private final HashMap<String, JMenu> menus;

    private LocaleData loc;

    /**
     * Construct a new <code>WorkspaceManager</code>.
     *
     * @param desktop The <code>JDesktopPane</code> that is associated with this
     *                workspace. This <code>JDesktopPane</code> must have already been added to
     *                the application's component hierarchy before this constructor is called.
     */

    public WorkspaceManager(JDesktopPane desktop) {

        editors = new ArrayList<>();
        listeners = new ArrayList<>();
        menus = new HashMap<>();

        this.desktop = desktop;

        frameListener = new FrameListener();

        dialogs = new DialogSet(KiwiUtils.getFrameForComponent(desktop), DialogSet.CENTER_PLACEMENT);

        loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    }

    /**
     * Get an iterator to all editors existing in this workspace.
     *
     * @return An iterator to the editors.
     */

    public Iterator<WorkspaceEditor> getAllEditors() {
        return (editors.iterator());
    }

    /**
     * Get the currently active editor.
     *
     * @return The currently active editor, or <code>null</code> if no editor is
     * currently active.
     */

    public WorkspaceEditor getActiveEditor() {
        JInternalFrame[] f = desktop.getAllFramesInLayer(0);
        if (f.length > 0) {
            return ((WorkspaceEditor) f[0]);
        } else {
            return (null);
        }
    }

    /**
     * Add an editor to the workspace. The editor's window becomes visible in
     * the workspace.
     *
     * @param editor The <code>WorkspaceEditor</code> to add to the workspace.
     * @see #removeEditor
     * @see #closeEditor
     */

    public void addEditor(WorkspaceEditor editor) {
        editors.add(editor);
        editor.setWorkspaceManager(this);
        desktop.add(editor);
        editor.addInternalFrameListener(frameListener);
        activateEditor(editor);
    }

    /**
     * Activate a specific editor. The editor is brought to the foreground in
     * the desktop, deiconified if necessary, and given mouse focus. This method
     * results in a call to the editor's <code>startEditing()</code> method.
     *
     * @param editor The editor to activate.
     * @see com.hyperrealm.kiwi.ui.WorkspaceEditor#startEditing
     */

    public void activateEditor(WorkspaceEditor editor) {
        editor.moveToFront();

        try {
            editor.setSelected(true);

            if (editor.isIcon()) {
                editor.setIcon(false);
            }
        } catch (PropertyVetoException ignored) {
        }

        editor.beginFocus();
        startEditing(editor);
    }

    /**
     * Remove an editor from the workspace. The editor's window becomes
     * invisible. This method does <i>not</i> attempt a save on the editor. If
     * the editor is currently active, its <code>stopEditing()</code> method is
     * called before it is removed.
     *
     * @param editor The <code>WorkspaceEditor</code> to remove from the
     *               workspace.
     * @see #addEditor
     * @see #closeEditor
     * @see #removeAllEditors
     * @see com.hyperrealm.kiwi.ui.WorkspaceEditor#stopEditing
     */

    public void removeEditor(WorkspaceEditor editor) {
        stopEditing(editor);
        editors.remove(editor);
        desktop.remove(editor);
        editor.removeInternalFrameListener(frameListener);
        desktop.repaint();
        fireEditorClosed(editor);
    }

    /**
     * Determine if there are any editors in the workspace that have unsaved
     * changes.
     *
     * @return <code>true</code> if there is at least one editor with unsaved
     * changes, <code>false</code> otherwise.
     */

    public boolean areUnsavedEditors() {
        for (WorkspaceEditor editor : editors) {
            if (editor.hasUnsavedChanges()) {
                return (true);
            }
        }

        return (false);
    }

    /**
     * Get the editor for a specific object. Each editor can be associated with
     * an arbitrary user object (the object that it is editing). This method
     * searches for an editor that is associated with the given object. Objects
     * are compared by calling <code>Object.equals()</code>, not by comparing
     * references.
     *
     * @param object The object whose editor is desired.
     * @return The editor associated with <code>object</code>, or
     * <code>null</code> if there is no editor for this object in the workspace.
     * @see Object#equals
     */

    public WorkspaceEditor getEditorForObject(Object object) {
        for (WorkspaceEditor editor : editors) {
            Object o = editor.getObject();
            if (o != null) {
                if (o.equals(object)) {
                    return (editor);
                }
            }
        }

        return (null);
    }

    /**
     * Register a <code>WorkspaceListener</code> with this
     * <code>WorkspaceManager</code>. Listeners are notified about events
     * relating to the editors currently being managed by this manager.
     *
     * @param listener The listener to register.
     * @see #removeWorkspaceListener
     */

    public void addWorkspaceListener(WorkspaceListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Unregister a <code>WorkspaceListener</code> from this
     * <code>WorkspaceManager</code>.
     *
     * @param listener The listener to unregister.
     * @see #addWorkspaceListener
     */

    public void removeWorkspaceListener(WorkspaceListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Notify listeners that an editor has been selected in the workspace.
     *
     * @param editor The editor that was selected.
     */

    protected void fireEditorSelected(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorSelected(evt);
            }
        }
    }

    /**
     * Notify listeners that an editor has been deselected in the workspace.
     *
     * @param editor The editor that was deselected.
     */

    protected void fireEditorDeselected(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorDeselected(evt);
            }
        }
    }

    /**
     * Notify listeners that an editor has been maximized in the workspace.
     *
     * @param editor The editor that was maximized.
     */

    protected void fireEditorRestored(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorRestored(evt);
            }
        }
    }

    /**
     * Notify listeners that an editor has been minimized (iconified) in the
     * workspace.
     *
     * @param editor The editor that was minimized.
     */

    protected void fireEditorIconified(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorIconified(evt);
            }
        }
    }

    /**
     * Notify listeners that an editor has been closed in the workspace.
     *
     * @param editor The editor that was closed.
     */

    protected void fireEditorClosed(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorClosed(evt);
            }
        }
    }

    /**
     * Notify listeners that an editor's state has changed.
     *
     * @param editor The editor whose state has changed.
     */

    protected void fireEditorStateChanged(WorkspaceEditor editor) {
        WorkspaceEvent evt = null;

        synchronized (listeners) {
            Iterator<WorkspaceListener> iter = listeners.iterator();

            while (iter.hasNext()) {
                WorkspaceListener l = iter.next();
                if (evt == null) {
                    evt = new WorkspaceEvent(this, editor);
                }
                l.editorStateChanged(evt);
            }
        }
    }

    /**
     * Count the editors in the workspace.
     *
     * @return The number of editors currently in the workspace (and being
     * managed by this <code>WorkspaceManager</code>).
     */

    public int getEditorCount() {
        return (editors.size());
    }

    /**
     * Close an editor. If the editor has unsaved changes, the user will be
     * prompted with a dialog to that effect. If this editor is currently
     * active, its <code>stopEditing()</code> method is called before it is
     * closed.
     *
     * @param editor The editor to close.
     * @see #removeEditor
     * @see #closeAllEditors
     * @see com.hyperrealm.kiwi.ui.WorkspaceEditor#stopEditing
     */
    @SuppressWarnings({"NestedIfDepth", "ReturnCount"})
    public boolean closeEditor(WorkspaceEditor editor) {
        if (editor.hasUnsavedChanges()) {
            activateEditor(editor);
            Object o = editor.getObject();

            String msg = loc.getMessage(KIWI_DIALOG_MESSAGE_SAVE_TO,
                ((o != null) ? o.toString() : UNTITLED));

            if (dialogs.showQuestionDialog(msg)) {
                if (!editor.save()) {
                    dialogs.showMessageDialog(
                        loc.getMessage(KIWI_DIALOG_MESSAGE_SAVE_FAILED));
                    return (false);
                } else {
                    editor.setChangesMade(false);
                    removeEditor(editor);
                    return (true);
                }
            } else {
                removeEditor(editor);
                return (true);
            }
        } else {
            removeEditor(editor);
        }

        return (true);
    }

    /**
     * Remove all editors from the workspace. Unconditionally removes each
     * editor from the workspace, without attempting to save unsaved changes.
     *
     * @see #closeAllEditors
     */

    public void removeAllEditors() {
        for (int i = editors.size() - 1; i >= 0; i--) {
            WorkspaceEditor editor = editors.get(i);
            removeEditor(editor);
        }
    }

    /**
     * Close all of the editors in the workspace. For each editor with unsaved
     * changes, the user will be prompted with a dialog asking whether that
     * editor should save its changes.
     *
     * @return <code>true</code> if all editors were closed successfully,
     * <code>false</code> otherwise.
     * @see #removeAllEditors
     */

    public boolean closeAllEditors() {
        for (int i = editors.size() - 1; i >= 0; i--) {
            WorkspaceEditor editor = editors.get(i);

            // save changes

            if (!closeEditor(editor)) {
                return (false);
            }
        }

        return (true);
    }

    /* frame event listener */

    /**
     * Notify the manager that an editor's state has changed. A
     * <code>WorkspaceEditor</code> uses this method to broadcast a change event
     * to all <code>WorkspaceListener</code>s.
     *
     * @param editor The editor whose state has changed.
     */

    public void notifyStateChanged(WorkspaceEditor editor) {
        fireEditorStateChanged(editor);
    }

    /**
     * Update the look and feel of all of the components being managed by this
     * <code>WorkspaceManager</code>.
     */

    public synchronized void updateLookAndFeel() {
        for (WorkspaceEditor ed : editors) {
            SwingUtilities.updateComponentTreeUI(ed);
            SwingUtilities.updateComponentTreeUI(ed.getDesktopIcon());
        }
    }

    /**
     * Register a menu with this <code>WorkspaceManager</code>. This convenience
     * method provides a means for a <code>WorkspaceEditor</code> to manipulate
     * one or more menus in an external menubar (such as the main application's
     * menubar).
     *
     * @param name A symbolic name for the menu.
     * @param menu The <code>JMenu</code> to register.
     * @see #unregisterMenu
     * @see #getMenu
     */

    public void registerMenu(String name, JMenu menu) {
        synchronized (menus) {
            menus.put(name, menu);
        }
    }

    /**
     * Unregister a menu from this <code>WorkspaceManager</code>.
     *
     * @param name The name of the menu to unregister.
     * @see #registerMenu
     */

    public void unregisterMenu(String name) {
        synchronized (menus) {
            menus.remove(name);
        }
    }

    /**
     * Get a reference to a <code>JMenu</code> associated with this
     * <code>WorkspaceManager</code>.
     *
     * @param name The name under which the menu was registered.
     * @return The <code>JMenu</code> reference, or <code>null</code> if there is
     * no menu registered under the specified name.
     * @see #registerMenu
     * @see #unregisterMenu
     */

    public JMenu getMenu(String name) {
        synchronized (menus) {
            return (menus.get(name));
        }
    }

    private void updateMenuHooks(WorkspaceEditor editor, boolean unhook) {
        synchronized (menus) {
            for (JMenu menu : menus.values()) {
                int c = menu.getItemCount();
                for (int i = 0; i < c; i++) {
                    JMenuItem item = menu.getItem(i);
                    if (unhook) {
                        item.removeActionListener(editor);
                    } else {
                        item.addActionListener(editor);
                    }
                }
            }
        }
    }

    /* Attach (or detach) an editor as a listener of all menu item action
     * events.
     */

    private void stopEditing(WorkspaceEditor editor) {
        editor.stopEditing();
        updateMenuHooks(editor, true);
    }

    /* Stop editing in a given editor.
     *
     * @param editor The editor to stop editing in.  The editor is detached
     * as a listener for action events on all menu items of all registered
     * manus, and then the editor's <code>stopEditing()</code> method is
     * called.
     */

    private void startEditing(WorkspaceEditor editor) {
        updateMenuHooks(editor, false);
        editor.startEditing();
    }

    /* Start editing for a given editor.
     *
     * @param editor The editor to start editing for. The editor is attached
     * as a listener for action events on all menu items of all registered
     * manus, and then the editor's <code>startEditing()</code> method is
     * called.
     */

    private class FrameListener extends InternalFrameAdapter {

        public void internalFrameActivated(InternalFrameEvent evt) {
            WorkspaceEditor e = (WorkspaceEditor) evt.getSource();

            if (activeEditor != null) {
                fireEditorDeselected(activeEditor);
            }

            fireEditorSelected(e);
            activeEditor = e;
            startEditing(e);
        }

        public void internalFrameIconified(InternalFrameEvent evt) {
            WorkspaceEditor e = (WorkspaceEditor) evt.getSource();

            stopEditing(e);
            fireEditorIconified(e);
        }

        public void internalFrameDeiconified(InternalFrameEvent evt) {
            WorkspaceEditor e = (WorkspaceEditor) evt.getSource();

            fireEditorRestored(e);
        }

        public void internalFrameClosed(InternalFrameEvent evt) {
        }

        @SuppressWarnings("NestedIfDepth")
        public void internalFrameClosing(InternalFrameEvent evt) {
            WorkspaceEditor e = (WorkspaceEditor) evt.getSource();

            if (e.hasUnsavedChanges()) {
                Object o = e.getObject();

                String msg = loc.getMessage(KIWI_DIALOG_MESSAGE_SAVE_TO,
                    ((o != null) ? o.toString()
                        : UNTITLED));

                if (dialogs.showQuestionDialog(msg)) {
                    if (!e.save()) {
                        dialogs.showMessageDialog(
                            loc.getMessage(KIWI_DIALOG_MESSAGE_SAVE_FAILED));
                    } else {
                        e.setChangesMade(false);
                        removeEditor(e);
                    }
                }
            } else {
                removeEditor(e);
            }
        }
    }

}
