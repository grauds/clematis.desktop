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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import static jworkspace.ui.desktop.IDesktopActions.NEW_SHORTCUT;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.IDesktopActions;

public class DesktopAction extends AbstractAction {

    public static DesktopAction createAction;
    public static DesktopAction pasteAction;
    public static DesktopAction selectAllAction;
    public static DesktopAction arrangeAllAction;

    public static DesktopAction gradientFillAction;
    public static DesktopAction switchCoverAction;
    public static DesktopAction chooseBgImageAction;
    public static DesktopAction chooseBackgroundColorAction;
    public static DesktopAction closeAllWindowsAction;

    private static Desktop desktop;
    private static DesktopShortcutsLayer shortcutsLayer;
    private final String command;

    public DesktopAction(String name,
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

    public static void initActions(Desktop desktop, DesktopShortcutsLayer shortcutsLayer) {
        
        DesktopAction.desktop = desktop;
        DesktopAction.shortcutsLayer = shortcutsLayer;
        
        // Use Toolkit's getMenuShortcutKeyMaskEx() for Mac support
        int menuMask = InputEvent.CTRL_DOWN_MASK;
        createAction = new DesktopAction(
            IDesktopActions.NEW_SHORTCUT, IDesktopActions.CREATE_SHORTCUT, null);

        pasteAction = new DesktopAction(
            "Paste", IDesktopActions.PASTE, KeyStroke.getKeyStroke(KeyEvent.VK_V, menuMask)
        );

        selectAllAction = new DesktopAction(
            "Select All", IDesktopActions.SELECT_ALL,
            KeyStroke.getKeyStroke(KeyEvent.VK_A, menuMask)
        );

        arrangeAllAction = new DesktopAction(
            "Arrange", IDesktopActions.ARRANGE, null
        );

        gradientFillAction = new DesktopAction(
            "Switch Gradient Fill", IDesktopActions.GRADIENT_FILL, null
        );

        switchCoverAction = new DesktopAction(
            "Switch Background Image", IDesktopActions.SWITCH_COVER, null
        );

        chooseBgImageAction = new DesktopAction(
            "Background Image...", IDesktopActions.CHOOSE_BACKGROUND_IMAGE, null
        );

        chooseBackgroundColorAction = new DesktopAction(
            "Background Colour...", IDesktopActions.BACKGROUND, null
        );

        closeAllWindowsAction = new DesktopAction(
            "Close All Windows",  IDesktopActions.CLOSE_ALL_WINDOWS, null
        );

        DesktopAction[] actions = new DesktopAction[] {
            createAction,
            pasteAction,
            selectAllAction,
            arrangeAllAction,
            gradientFillAction,
            switchCoverAction,
            chooseBgImageAction,
            chooseBackgroundColorAction,
            closeAllWindowsAction
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
            case IDesktopActions.CREATE_SHORTCUT -> {
                DesktopShortcut shortcut = new DesktopShortcut(null, NEW_SHORTCUT);
                shortcutsLayer.addShortcut(shortcut, new Point(50, 50));
                shortcutsLayer.getParent().repaint();
            }
            case IDesktopActions.ARRANGE -> shortcutsLayer.arrangeShortcuts();
            case IDesktopActions.SELECT_ALL -> shortcutsLayer.selectAllShortcuts();
            case IDesktopActions.PASTE -> shortcutsLayer.pasteClipboard();
            case IDesktopActions.GRADIENT_FILL -> desktop.switchGradientFill();
            case IDesktopActions.BACKGROUND -> desktop.changeBackground();
            case IDesktopActions.SWITCH_COVER -> desktop.switchCoverVisible();
            case IDesktopActions.CHOOSE_BACKGROUND_IMAGE ->  desktop.changeBackgroundImage();
            case IDesktopActions.CLOSE_ALL_WINDOWS -> desktop.closeAllFrames();
        }
    }

    public static void updateEnabledState() {
        
        // Paste depends on clipboard content
        pasteAction.setEnabled(shortcutsLayer.hasClipboardContent());

        if (desktop.getTheme().isGradientFill()) {
            gradientFillAction.putValue(Action.NAME, "Hide Gradient");
        } else {
            gradientFillAction.putValue(Action.NAME, "Show Gradient");
        }

        if (desktop.getTheme().getCover() == null) {
            switchCoverAction.setEnabled(false);
            switchCoverAction.putValue(Action.NAME, "No Background Image");
        } else {
            switchCoverAction.setEnabled(true);
            if (desktop.getTheme().isCoverVisible()) {
                switchCoverAction.putValue(Action.NAME, "Hide Background Image");
            } else {
                switchCoverAction.putValue(Action.NAME, "Show Background Image");
            }
        }
    }
}
