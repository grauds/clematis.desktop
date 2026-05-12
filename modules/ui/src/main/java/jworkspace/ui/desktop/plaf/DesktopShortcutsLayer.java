package jworkspace.ui.desktop.plaf;
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
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jworkspace.config.ServiceLocator;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.actions.DesktopShortcutActions;
import lombok.Getter;

/**
 * The DesktopShortcutsLayer class represents a specialized {@code JComponent} for managing
 * and displaying a collection of desktop-like shortcuts. It provides functionalities for
 * managing shortcut placement, selection, copying and pasting.
 * <p>
 * This class supports multiple selection modes, including toggling individual shortcut
 * selections and enabling exclusive selection for a single shortcut. Shortcuts are
 * interactable and designed to mimic the behavior of desktop icons.
 */
public class DesktopShortcutsLayer extends JComponent {

    private static final DataFlavor SHORTCUT_FLAVOR =
        new DataFlavor(List.class, "Desktop Shortcuts List");

    private static final Clipboard CLIPBOARD = new Clipboard("Desktop clipboard");

    private final List<DesktopShortcut> shortcuts = new ArrayList<>();

    private final List<DesktopShortcut> selected = new ArrayList<>();

    @Getter
    private final DesktopShortcutActions desktopShortcutActions;

    public DesktopShortcutsLayer() {
        setLayout(null);
        setOpaque(false);
        this.desktopShortcutActions = new DesktopShortcutActions(this);
    }

    public void addShortcut(DesktopShortcut shortcut) {
        addShortcut(shortcut, null);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void addShortcut(DesktopShortcut s, Point location) {
        shortcuts.add(s);
        add(s);

        if (location != null) {
            s.setBounds(location.x, location.y, s.getPreferredSize().width, s.getPreferredSize().height);
        } else {
            s.setBounds(s.getLocation().x, s.getLocation().y, s.getPreferredSize().width, s.getPreferredSize().height);
        }

        s.setSelectionProvider(() -> selected.contains(s));
        s.setComponentPopupMenu(new DesktopShortcutMenu(this.desktopShortcutActions));

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

    public void deleteSelection() {
        for (DesktopShortcut s : new ArrayList<>(getSelectedShortcuts())) {
            removeShortcut(s);
        }
        clearSelection();
        repaint();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void arrangeShortcuts() {
        int x = 20;
        int y = 20;
        int spacing = 90;

        for (DesktopShortcut s : getShortcuts()) {
            s.setLocation(x, y);
            y += spacing;
            if (y + spacing > getHeight()) {
                y = 20;
                x += spacing;
            }
        }
        repaint();
    }

    public void selectAllShortcuts() {
        clearSelection();
        for (DesktopShortcut s : getShortcuts()) {
            addToSelection(s);
        }
        repaint();
    }

    public void cutSelection() {
        copySelection();
        deleteSelection();
    }

    public void copySelection() {
        // Create a snapshot of current selection
        final List<DesktopShortcut> selectionCopy = new ArrayList<>(selected);

        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{SHORTCUT_FLAVOR};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return SHORTCUT_FLAVOR.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return selectionCopy;
            }
        };

        // Set to system clipboard (null means no owner needed)
        CLIPBOARD.setContents(transferable, null);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void pasteClipboard() {
        try {
            if (!CLIPBOARD.isDataFlavorAvailable(SHORTCUT_FLAVOR)) {
                return;
            }

            // 1. Get the list of shortcuts from the clipboard
            List<?> data = (List<?>) CLIPBOARD.getData(SHORTCUT_FLAVOR);
            List<DesktopShortcut> newShortcuts = new ArrayList<>();
            int offset = 30;

            for (Object obj : data) {
                if (obj instanceof DesktopShortcut original) {
                    // 2. Clone the shortcut data
                    DesktopShortcut copy = new DesktopShortcut(original.getIcon(), original.getText());
                    // 3. Position with offset
                    Point pos = original.getLocation();
                    addShortcut(copy, new Point(pos.x + offset, pos.y + offset));
                    newShortcuts.add(copy);
                }
            }

            // 4. Update selection to the new items
            clearSelection();
            newShortcuts.forEach(this::addToSelection);

        } catch (Exception ex) {
            DesktopServiceLocator.getInstance().getWorkspaceGUI().showError(ex.getMessage(), ex);
        }
        repaint();
    }

    public boolean hasClipboardContent() {
        try {
            return CLIPBOARD.isDataFlavorAvailable(SHORTCUT_FLAVOR);
        } catch (Exception e) {
            return false;
        }
    }

    public void runShortcutAction() {
        if (this.selected.size() != 1) {
            return;
        }
        DesktopShortcut shortcut = this.selected.getFirst();
        try {
            ServiceLocator
                .getInstance()
                .getRuntimeManager()
                .run(
                    shortcut.getCommandLine()
                );
        } catch (IOException e) {
            WorkspaceError.exception("Open shortcut " + shortcut.getCommandLine(), e);
        }
    }
}
