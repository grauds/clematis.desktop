package jworkspace.ui.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class CopyAction extends DesktopShortcutAction {

    public CopyAction(DesktopShortcutsLayer shortcutsLayer) {
        super(
            "Copy",
            IDesktopShortcutActions.COPY,
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
            shortcutsLayer
        );
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        shortcutsLayer.copySelection();
    }
}
