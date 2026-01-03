package jworkspace.ui.desktop.plaf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import static jworkspace.ui.util.SwingUtils.createMenuItem;
import jworkspace.ui.api.action.UISwitchListener;

public class DesktopShortcutMenu extends JPopupMenu {

    private final JMenuItem properties;
    private final JMenuItem delete;
    private final JMenuItem cut;
    private final JMenuItem copy;
    private final JMenuItem paste;
    private final JMenuItem open;
    private final JMenuItem rename;
    private final JMenuItem selectAll;

    private final ActionListener[] listeners;

    public DesktopShortcutMenu(ActionListener... listeners) {
        super();
        this.listeners = listeners;

        open = createMenuItem(DesktopInteractionLayer.OPEN,
            this::fireActionEvent,
            DesktopInteractionLayer.OPEN,
            null
        );

        rename = createMenuItem(DesktopInteractionLayer.RENAME,
            this::fireActionEvent,
            DesktopInteractionLayer.RENAME,
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)
        );

        cut = createMenuItem(DesktopInteractionLayer.CUT,
            this::fireActionEvent,
            DesktopInteractionLayer.CUT,
            KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)
        );

        copy = createMenuItem(DesktopInteractionLayer.COPY,
            this::fireActionEvent,
            DesktopInteractionLayer.COPY,
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)
        );

        paste = createMenuItem(DesktopInteractionLayer.PASTE,
            this::fireActionEvent,
            DesktopInteractionLayer.PASTE,
            KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)
        );

        delete = createMenuItem(DesktopInteractionLayer.DELETE,
            this::fireActionEvent,
            DesktopInteractionLayer.DELETE,
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)
        );

        selectAll = createMenuItem(DesktopInteractionLayer.SELECT_ALL,
            this::fireActionEvent,
            DesktopInteractionLayer.SELECT_ALL,
            KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)
        );

        properties = createMenuItem("Properties...",
            this::fireActionEvent,
            DesktopInteractionLayer.PROPERTIES,
            KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)
        );

        add(open);
        addSeparator();
        add(cut);
        add(copy);
        add(paste);
        add(selectAll);
        addSeparator();
        add(delete);
        addSeparator();
        add(rename);
        add(properties);

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    private void fireActionEvent(ActionEvent e) {
        for (ActionListener listener : listeners) {
            listener.actionPerformed(e);
        }
    }
}
