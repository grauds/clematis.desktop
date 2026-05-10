package jworkspace.ui.desktop.actions;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;


public abstract class DesktopShortcutAction extends AbstractAction {

    protected final DesktopShortcutsLayer shortcutsLayer;
    private final String command;

    protected DesktopShortcutAction(
        String name,
        String command,
        KeyStroke accelerator,
        DesktopShortcutsLayer shortcutsLayer
    ) {
        super(name);
        this.shortcutsLayer = shortcutsLayer;
        this.command = command;
        putValue(ACTION_COMMAND_KEY, command);
        if (accelerator != null) {
            putValue(ACCELERATOR_KEY, accelerator);
        }
    }
}
