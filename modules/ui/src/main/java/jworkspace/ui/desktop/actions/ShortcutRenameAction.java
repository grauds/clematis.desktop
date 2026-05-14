package jworkspace.ui.desktop.actions;
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

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class ShortcutRenameAction extends AbstractDesktopShortcutAction {

    public ShortcutRenameAction(DesktopShortcutsLayer shortcutsLayer) {
        super("Rename",
            IDesktopShortcutActions.RENAME,
            null,
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
        String currentName = "";
        var selectedShortcuts = shortcutsLayer.getSelectedShortcuts();
        if (!selectedShortcuts.isEmpty()) {
            DesktopShortcut firstSelected = selectedShortcuts.getFirst();
            currentName = ((JLabel) firstSelected.getComponent(0)).getText();
        }
        String newName = (String) JOptionPane.showInputDialog(
            shortcutsLayer,
            "Enter new name:",
            "Rename Shortcut",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            currentName
        );
        if (newName != null && !newName.isBlank()) {
            for (DesktopShortcut s : shortcutsLayer.getSelectedShortcuts()) {
                ((JLabel) s.getComponent(0)).setText(newName);
                s.repaint();
            }
        }
    }
}
