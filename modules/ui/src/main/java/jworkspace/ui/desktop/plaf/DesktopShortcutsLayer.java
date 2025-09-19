package jworkspace.ui.desktop.plaf;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jworkspace.ui.desktop.DesktopShortcut;

/**
 * The DesktopShortcutsLayer class represents a specialized {@code JComponent} for managing
 * and displaying a collection of desktop-like shortcuts. It provides functionalities for
 * managing shortcut placement, selection, and interactions in a layered context.
 * <p>
 * This class supports multiple selection modes, including toggling individual shortcut
 * selections and enabling exclusive selection for a single shortcut. Shortcuts are
 * interactable and designed to mimic the behavior of desktop icons.
 */
public class DesktopShortcutsLayer extends JComponent {
    private final List<DesktopShortcut> shortcuts = new ArrayList<>();
    private final List<DesktopShortcut> selected = new ArrayList<>();

    public DesktopShortcutsLayer() {
        setLayout(null);
        setOpaque(false);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void addShortcut(DesktopShortcut s, Point location) {
        shortcuts.add(s);
        add(s);
        s.setBounds(location.x, location.y, 80, 80);

        s.addSelectionHandler(() -> toggleSelection(s));
        s.addExclusiveSelectionHandler(() -> selectOnly(s));
        s.setSelectionProvider(() -> selected.contains(s));

        revalidate();
        repaint();
    }

    private void toggleSelection(DesktopShortcut s) {
        if (selected.contains(s)) {
            selected.remove(s);
            s.setSelected(false);
        } else {
            selected.add(s);
            s.setSelected(true);
        }
        repaint();
    }

    private void selectOnly(DesktopShortcut s) {
        clearSelection();
        selected.add(s);
        s.setSelected(true);
        repaint();
    }

    public void clearSelection() {
        for (DesktopShortcut s : new ArrayList<>(selected)) {
            s.setSelected(false);
        }
        selected.clear();
        repaint();
    }

    public void addToSelection(DesktopShortcut s) {
        if (!selected.contains(s)) {
            selected.add(s); s.setSelected(true);
        }
    }

    public List<DesktopShortcut> getShortcuts() {
        return new ArrayList<>(shortcuts);
    }

    public List<DesktopShortcut> getSelectedShortcuts() {
        return new ArrayList<>(selected);
    }

    public void removeFromSelection(DesktopShortcut s) {
        if (selected.remove(s)) {
            s.setSelected(false);
        }
    }

    public void removeShortcut(DesktopShortcut s) {
        removeFromSelection(s);
        remove(s); // remove from parent
        shortcuts.remove(s); // remove from the shortcut list
        revalidate();
        repaint();
    }
}
