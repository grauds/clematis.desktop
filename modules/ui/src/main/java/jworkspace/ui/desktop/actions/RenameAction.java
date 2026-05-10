package jworkspace.ui.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class RenameAction extends DesktopShortcutAction {

    public RenameAction(DesktopShortcutsLayer shortcutsLayer) {
        super("Delete",
            IDesktopShortcutActions.DELETE,
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            shortcutsLayer
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showRenameDialog();
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
}
