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
import javax.swing.Action;

import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.desktop.IDesktopActions;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;

public class DesktopActions extends AbstractActionsCollection {

    private final DesktopShortcutsLayer layer;
    private final Desktop desktop;

    public DesktopActions(DesktopShortcutsLayer layer,
                          Desktop desktop
    ) {
        this.layer = layer;
        this.desktop = desktop;
        register(new ShortcutCreateAction(layer, desktop));
        register(new ShortcutPasteAction(layer, desktop));
        register(new SelectAllAction(layer, desktop));
        register(new ArrangeShortcutsAction(layer, desktop));
        register(new GradientFillAction(layer, desktop));
        register(new SwitchWallpaperAction(layer, desktop));
        register(new WallpaperAction(layer, desktop));
        register(new BackgroundAction(layer, desktop));
        register(new CloseAllWindowsAction(layer, desktop));

        initKeyBindings(desktop);
    }

    public void updateEnabledState() {
        
        // Paste depends on clipboard content
        get(IDesktopActions.PASTE).setEnabled(layer.hasClipboardContent());

        if (desktop.getTheme().isGradientFill()) {
            get(IDesktopActions.GRADIENT_FILL).putValue(Action.NAME, "Hide Gradient");
        } else {
            get(IDesktopActions.GRADIENT_FILL).putValue(Action.NAME, "Show Gradient");
        }

        if (desktop.getTheme().getCover() == null) {
            get(IDesktopActions.TOGGLE_WALLPAPER).setEnabled(false);
            get(IDesktopActions.TOGGLE_WALLPAPER).putValue(Action.NAME, "No Background Image");
        } else {
            get(IDesktopActions.TOGGLE_WALLPAPER).setEnabled(true);
            if (desktop.getTheme().isCoverVisible()) {
                get(IDesktopActions.TOGGLE_WALLPAPER).putValue(Action.NAME, "Hide Background Image");
            } else {
                get(IDesktopActions.TOGGLE_WALLPAPER).putValue(Action.NAME, "Show Background Image");
            }
        }
    }
}
