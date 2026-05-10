package jworkspace.ui.desktop.plaf;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.dialog.DesktopShortcutDialog;

public class DesktopShortcutAction extends AbstractAction {

    public static Action openAction;
    public static Action cutAction;
    public static Action copyAction;
    public static Action deleteAction;
    public static Action renameAction;
    public static Action propertiesAction;

    private static DesktopShortcutsLayer shortcutsLayer;
    private final String command;


    public DesktopShortcutAction(String name,
                                 String command,
                                 KeyStroke accelerator
    ) {
        super(name);
        this.command = command;
        putValue(ACTION_COMMAND_KEY, command);
        if (accelerator != null) {
            putValue(ACCELERATOR_KEY, accelerator);
        }
    }

    public static void initActions(DesktopShortcutsLayer shortcutsLayer) {

        DesktopShortcutAction.shortcutsLayer = shortcutsLayer;

        // Use Toolkit's getMenuShortcutKeyMaskEx() for Mac support
        int menuMask = InputEvent.CTRL_DOWN_MASK;

        openAction = new DesktopShortcutAction("Open", IDesktopShortcutActions.OPEN, null);
        cutAction = new DesktopShortcutAction(
            "Cut", IDesktopShortcutActions.CUT, KeyStroke.getKeyStroke(KeyEvent.VK_X, menuMask)
        );
        copyAction = new DesktopShortcutAction(
            "Copy", IDesktopShortcutActions.COPY, KeyStroke.getKeyStroke(KeyEvent.VK_C, menuMask)
        );
        deleteAction = new DesktopShortcutAction(
            "Delete", IDesktopShortcutActions.DELETE, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)
        );
        renameAction = new DesktopShortcutAction(
            "Rename", IDesktopShortcutActions.RENAME, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)
        );
        propertiesAction = new DesktopShortcutAction(
            "Properties", IDesktopShortcutActions.PROPERTIES, null
        );

        Action[] actions = new Action[] {
            openAction,
            cutAction,
            copyAction,
            deleteAction,
            renameAction,
            propertiesAction
        };

        initKeyBindings(shortcutsLayer, actions);
    }

    private static void initKeyBindings(DesktopShortcutsLayer shortcutsLayer, Action[] actions) {
        InputMap im = shortcutsLayer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = shortcutsLayer.getActionMap();

        for (Action a : actions) {
            Object key = a.getValue(Action.ACTION_COMMAND_KEY);
            KeyStroke ks = (KeyStroke) a.getValue(Action.ACCELERATOR_KEY);

            am.put(key, a);
            if (ks != null) {
                im.put(ks, key);
            }
        }
    }

    @SuppressWarnings({"checkstyle:MissingSwitchDefault", "checkstyle:MagicNumber"})
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (command) {
            case IDesktopShortcutActions.OPEN ->
                JOptionPane.showMessageDialog(shortcutsLayer, "Open shortcut", "Info", JOptionPane.INFORMATION_MESSAGE);

            case IDesktopShortcutActions.CUT -> shortcutsLayer.cutSelection();
            case IDesktopShortcutActions.COPY -> shortcutsLayer.copySelection();
            case IDesktopShortcutActions.DELETE -> shortcutsLayer.deleteSelection();
            case IDesktopShortcutActions.RENAME -> showRenameDialog();

            case IDesktopShortcutActions.PROPERTIES -> {
                if (!shortcutsLayer.getSelectedShortcuts().isEmpty()) {
                    DesktopShortcutDialog dlg = new DesktopShortcutDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
                    );
                    dlg.setData(shortcutsLayer.getSelectedShortcuts().getFirst());
                    dlg.setVisible(true);
                }
            }
        }
    }

    private void showRenameDialog() {
        if (shortcutsLayer.getSelectedShortcuts().isEmpty()) {
            return;
        }
        String newName = JOptionPane.showInputDialog(
            shortcutsLayer,
            "Enter new name:",
            "Rename Shortcut",
            JOptionPane.PLAIN_MESSAGE
        );
        if (newName != null && !newName.isBlank()) {
            for (DesktopShortcut s : shortcutsLayer.getSelectedShortcuts()) {
                ((JLabel) s.getComponent(0)).setText(newName);
                s.repaint();
            }
        }
    }

    public static void updateEnabledState() {
        boolean hasSelection = !shortcutsLayer.getSelectedShortcuts().isEmpty();
        boolean oneSelected = shortcutsLayer.getSelectedShortcuts().size() == 1;

        openAction.setEnabled(oneSelected);
        copyAction.setEnabled(hasSelection);
        cutAction.setEnabled(hasSelection);
        deleteAction.setEnabled(hasSelection);
        renameAction.setEnabled(oneSelected);
        propertiesAction.setEnabled(oneSelected);
    }

}
