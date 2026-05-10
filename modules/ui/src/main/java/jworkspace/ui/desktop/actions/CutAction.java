package jworkspace.ui.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;


public class CutAction extends DesktopShortcutAction {

    public CutAction(DesktopShortcutsLayer shortcutsLayer) {
        super(
            "Cut",
            IDesktopShortcutActions.CUT,
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
            shortcutsLayer
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        shortcutsLayer.cutSelection();
    }
}
