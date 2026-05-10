package jworkspace.ui.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class DeleteAction extends DesktopShortcutAction {

    public DeleteAction(DesktopShortcutsLayer desktopShortcutsLayer) {
        super(
            "Delete",
            IDesktopShortcutActions.DELETE,
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            desktopShortcutsLayer
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        shortcutsLayer.deleteSelection();
    }
}
