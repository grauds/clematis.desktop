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

import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.dialog.DesktopShortcutDialog;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class ShortcutPropertiesAction extends AbstractDesktopShortcutAction {

    public ShortcutPropertiesAction(DesktopShortcutsLayer shortcutsLayer) {
        super("Properties", IDesktopShortcutActions.PROPERTIES, null, shortcutsLayer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!shortcutsLayer.getSelectedShortcuts().isEmpty()) {
            DesktopShortcutDialog dlg = new DesktopShortcutDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
            );
            dlg.setData(shortcutsLayer.getSelectedShortcuts().getFirst());
            dlg.setVisible(true);
        }
    }
}
