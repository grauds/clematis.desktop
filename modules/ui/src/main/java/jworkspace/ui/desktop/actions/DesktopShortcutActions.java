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

import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class DesktopShortcutActions extends AbstractActionsCollection {

    private final DesktopShortcutsLayer layer;

    public DesktopShortcutActions(DesktopShortcutsLayer layer) {
        this.layer = layer;
        register(new ShortcutOpenAction(layer));
        register(new ShortcutCutAction(layer));
        register(new ShortcutCopyAction(layer));
        register(new ShortcutDeleteAction(layer));
        register(new ShortcutRenameAction(layer));
        register(new ShortcutPropertiesAction(layer));

        initKeyBindings(layer);
    }

    public void updateEnabledState() {
        boolean hasSelection = !layer.getSelectedShortcuts().isEmpty();
        boolean oneSelected = layer.getSelectedShortcuts().size() == 1;

        get(IDesktopShortcutActions.OPEN).setEnabled(oneSelected);
        get(IDesktopShortcutActions.COPY).setEnabled(hasSelection);
        get(IDesktopShortcutActions.CUT).setEnabled(hasSelection);
        get(IDesktopShortcutActions.DELETE).setEnabled(hasSelection);
        get(IDesktopShortcutActions.RENAME).setEnabled(oneSelected);
        get(IDesktopShortcutActions.PROPERTIES).setEnabled(oneSelected);
    }
}
