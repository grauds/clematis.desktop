package jworkspace.ui.desktop.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class OpenAction extends DesktopShortcutAction {

    public OpenAction(DesktopShortcutsLayer shortcutsLayer) {
        super("Open", IDesktopShortcutActions.OPEN, null, shortcutsLayer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
            shortcutsLayer, "Open shortcut", "Info", JOptionPane.INFORMATION_MESSAGE
        );
    }
}
