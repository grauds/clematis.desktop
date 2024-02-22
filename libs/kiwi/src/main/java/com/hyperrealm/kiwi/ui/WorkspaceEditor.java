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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.text.JTextComponent;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * An abstract class that defines common behavior for all workspace editors.
 * This class should be extended to provide the appropriate user interface and
 * behavior for editing a specific type of problem domain object. Editors
 * are managed by a <code>WorkspaceManager</code>, which provides some
 * rudimentary inter-editor coordination, persistence support,  and other
 * useful facilities.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.WorkspaceManager
 */

public abstract class WorkspaceEditor extends JInternalFrame implements ActionListener {
    /**
     * The problem domain object associated with this editor.
     */
    protected Object object = null;

    private KButton bSave, bCancel;

    private boolean changesMade = false;

    private KeyListener keyAdapter;

    private MouseListener mouseAdapter;

    private FocusListener focusAdapter;

    private Component firstComponent = null;

    private boolean editable;

    private JTextComponent curFocus = null;

    private WorkspaceManager manager = null;

    private KLabel lComment;

    private KPanel pane;

    /**
     * Construct a new <code>WorkspaceEditor</code> with a default window title.
     * It is created as editable.
     */

    public WorkspaceEditor() {
        this("");
    }

    /**
     * Construct a new editable <code>WorkspaceEditor</code> with the given
     * window title.
     *
     * @param title The title for the editor's window.
     */

    public WorkspaceEditor(String title) {
        this(title, true);
    }

    /**
     * Construct a new <code>WorkspaceEditor</code> with the given window
     * title and editable mode.
     *
     * @param title    The title for the editor's window.
     * @param editable A flag specifying whether the editor will be editable.
     */

    public WorkspaceEditor(String title, boolean editable) {
        super(title, true, true, true, true);

        this.editable = editable;

        keyAdapter = new KeyListener();
        mouseAdapter = new MouseListener();

        ButtonListener actionListener = new ButtonListener();
        focusAdapter = new FocusListener();

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Container main = getContentPane();

        pane = new KPanel(UIChangeManager.getInstance().getDefaultTexture());
        pane.setBorder(KiwiUtils.DEFAULT_BORDER);
        pane.setLayout(new BorderLayout(0, 0));

        main.setLayout(new GridLayout(1, 0));
        main.add(pane);

        lComment = new KLabel();
        lComment.setBorder(KiwiUtils.DEFAULT_BORDER);

        pane.add(CENTER_POSITION, buildEditingUI());

        ButtonPanel buttons = new ButtonPanel();

        if (isEditable()) {
            bSave = new KButton(loc.getMessage("kiwi.button.save"));
            bSave.addActionListener(actionListener);
            bSave.setEnabled(false);
            buttons.addButton(bSave);

            bCancel = new KButton(loc.getMessage("kiwi.button.cancel"));
        } else {
            bCancel = new KButton(loc.getMessage("kiwi.button.close"));
        }

        bCancel.addActionListener(actionListener);
        buttons.addButton(bCancel);

        pane.add(SOUTH_POSITION, buttons);

        JMenuBar mb = buildMenuBar();
        if (mb != null) {
            setJMenuBar(mb);
        }
    }

    /* Set the workspace manager for this editor. */

    /**
     * Get the <code>WorkspaceManager</code> for this editor. The method will
     * return <code>null</code> if the editor has not yet been added to a
     * workspace (that is, a <code>JDesktopPane</code>).
     */

    protected final WorkspaceManager getWorkspaceManager() {
        return (manager);
    }

    final void setWorkspaceManager(WorkspaceManager manager) {
        this.manager = manager;
    }

    /**
     * Build the editing UI. Subclasses must implement this method to build the
     * user interface for the editor.
     */

    abstract Component buildEditingUI();

    /**
     * Update the editing UI. Subclasses must implement this method to update
     * the state of the user interface when a new problem domain object is
     * associated with this editor.
     */

    abstract void updateEditingUI();

    /**
     * Set the comment that appears in the top portion of the editor.
     */

    protected void setComment(String comment) {
        if (comment != null) {
            lComment.setText(comment);
            pane.add("North", lComment);
        } else {
            if (lComment.getText() != null) {
                pane.remove(lComment);
            }
            lComment.setText(null);
        }
    }

    /**
     * Construct the menu bar for this editor. The default implementation
     * returns <code>null</code>, which signifies that no menubar is needed.
     */

    private JMenuBar buildMenuBar() {
        return (null);
    }

    /**
     * Give keyboard focus to the first text input component in this editor.
     * These components are registered via
     * <code>registerTextInputComponent()</code>.
     */

    public final void beginFocus() {
        if ((firstComponent != null) && isEditable()) {
            firstComponent.requestFocus();
        }
    }

    /**
     * Handle events. The default implementation of this method does nothing;
     * subclassers may override it to handle specific user interface events.
     */

    public void actionPerformed(ActionEvent evt) {
    }

    /**
     * Get the current problem domain object associated with this editor, or
     * <code>null</code> if there is no object associated with the editor.
     *
     * @return The object currently associated with this editor.
     * @see #setObject
     */

    public final Object getObject() {
        return (object);
    }

    /**
     * Set the problem domain object to be associated with this editor. This
     * method ultimately results in a call to <code>updateEditingUI()</code>.
     *
     * @param object The new object to be associated with this editor.
     * @see #getObject
     */

    public final void setObject(Object object) {
        this.object = object;
        updateEditingUI();
    }

    /**
     * Determine if this editor is displaying unsaved changes.
     *
     * @return <code>true</code> if there are unsaved changes, and
     * <cod>false</code> otherwise.
     * @see #setChangesMade
     */

    public final boolean hasUnsavedChanges() {
        return (changesMade);
    }

    /**
     * Set the <i>changes made</i> flag on this editor. The editor checks this
     * flag at close time to determine if changes need to be saved.
     *
     * @param flag The new value for the flag.
     * @see #hasUnsavedChanges
     */

    final void setChangesMade(boolean flag) {
        if (isEditable()) {
            changesMade = flag;
            bSave.setEnabled(flag);
        }
    }

    /**
     * Persist the edits made in this editor.
     *
     * @return <code>true</code> if the save was successful and
     * <code>false</code> otherwise.
     */

    public abstract boolean save();

    /**
     * Determine if the editor is editable.
     *
     * @return <code>true</code> if the editor is editable, and
     * <code>false</code> otherwise.
     */

    public final boolean isEditable() {
        return (editable);
    }

    /**
     * Register a text input component. This method is used by subclassers to
     * notify the editor of input fields in the user interface. The editor uses
     * this information when requesting focus, and also listens for keypress
     * events on all of these components, setting the <i>changes made</i> flag
     * to <code>true</code> when one occurs.
     *
     * @param c The component to register.
     * @see #registerMouseInputComponent
     */

    private void registerTextInputComponent(JTextComponent c) {
        if (firstComponent == null) {
            firstComponent = c;
        }

        c.addKeyListener(keyAdapter);
        c.addFocusListener(focusAdapter);
    }

    /**
     * Register a mouse input component. This method is used by subclassers to
     * notify the editor of components that change the state of the data being
     * edited when they are clicked. The editor listens for mouse events on all
     * of these components, setting the <i>changes</i> made flag to
     * <code>true</code> when one occurs.
     *
     * @param c The component to register.
     * @see #registerTextInputComponent
     */

    protected final void registerMouseInputComponent(Component c) {
        c.addMouseListener(mouseAdapter);
    }


    /**
     * Start editing in this editor. This method is called whenever the editor
     * is activated in the workspace. The default implementation does nothing.
     *
     * @see #stopEditing
     */

    protected void startEditing() {
    }

    /**
     * Start editing in this editor. This method is called whenever the editor
     * is deactivated in the workspace. The default implementation does nothing.
     *
     * @see #startEditing
     */

    protected void stopEditing() {
    }

    /* key listener */

    /**
     * Invoke a <i>copy</i> action on this editor. The text in the text input
     * component that currently has focus (if any) is copied to the system
     * clipboard.
     */

    public final void copy() {
        if (curFocus != null) {
            curFocus.copy();
        }
    }

    /* focus listener */

    /**
     * Invoke a <i>cut</i> action on this editor. The text in the text input
     * component that currently has focus (if any) is moved to the system
     * clipboard.
     */

    public final void cut() {
        if (curFocus != null) {
            curFocus.cut();
        }
    }

    /* mouse listener */

    /**
     * Invoke a <i>paste</i> action on this editor. The text in the system
     * clipboard is copied to the text input component that currently has focus
     * (if any).
     */

    public final void paste() {
        if (curFocus != null) {
            curFocus.paste();
        }
    }

    /* button listener */

    /**
     * Fire an <i>editor state changed</i> event. Notifies listeners that the
     * internal state of this editor has changed in some way.
     */

    protected final void fireStateChanged() {
        if (manager != null) {
            manager.fireEditorStateChanged(this);
        }
    }

    public void hide() {
        try {
            setClosed(true);
        } catch (PropertyVetoException ignored) {
        }
    }

    private class KeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent evt) {
            setChangesMade(true);
        }
    }

    private class FocusListener extends FocusAdapter {
        public void focusGained(FocusEvent evt) {
            Component c = (Component) evt.getSource();
            if (c instanceof JTextComponent) {
                curFocus = (JTextComponent) c;
            }
        }

        public void focusLost(FocusEvent evt) {
            Component c = (Component) evt.getSource();
            if (c == curFocus) {
                curFocus = null;
            }
        }
    }

    private class MouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            setChangesMade(true);
        }

        public void mouseReleased(MouseEvent evt) {
            setChangesMade(true);
        }
    }

    /* hide the editor */

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            if (o == bSave) {
                boolean ok = save();
                if (ok) {
                    hide();
                }
            } else if (o == bCancel) {
                setChangesMade(false);
                hide();
            }
        }
    }

}
