package jworkspace.ui.desktop.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import jworkspace.ui.api.Constants;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.desktop.Desktop;
import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.dialog.DesktopShortcutDialog;
import jworkspace.ui.utils.FileUtils;

/**
 * The DesktopInteractionLayer class is a Swing component responsible for managing the interaction layer
 * of a desktop UI, allowing for interactions such as selecting, copying, pasting, renaming, deleting, and
 * arranging desktop shortcuts. It provides features such as selection handling, context menu building,
 * and keyboard shortcuts for basic operations.
 * <p>
 * It implements functionalities like clipboard management, drag-and-drop handling,
 * and popup menu interactions. This class cooperates with an underlying layer to display
 * and operate on desktop shortcuts.
 * <p>
 * Features:
 * - Select, copy, paste, and delete shortcuts.
 * - Rename shortcuts through a dialog.
 * - Arrange shortcuts automatically.
 * - Display context menus for shortcuts and desktop background.
 * - Handle keyboard shortcuts for core operations.
 * - Track and manage selection rectangle and dragging behavior.
 */
public class DesktopInteractionLayer extends JComponent implements ActionListener {

    public static final String OPEN = "Open";
    public static final String RENAME = "Rename";
    public static final String CUT = "Cut";
    public static final String COPY = "Copy";
    public static final String PASTE = "Paste";
    public static final String SELECT_ALL = "Select All";
    public static final String ARRANGE = "Arrange Icons";
    public static final String DELETE = "Delete";
    public static final String PROPERTIES = "Properties";

    public static final String COPY_KEY = "copy";
    public static final String PASTE_KEY = "paste";
    public static final String SELECT_ALL_KEY = "selectAll";
    public static final String DELETE_KEY = "delete";
    public static final String RENAME_KEY = "rename";

    private final Desktop desktop;
    private final DesktopShortcutsLayer shortcutsLayer;

    private Rectangle selectionRect;
    private Point bandStart;
    private Point dragStart;

    private final Map<DesktopShortcut, Point> initialPositions = new HashMap<>();
    private boolean draggingIcons = false;

    private final JPopupMenu shortcutMenu;
    private final JPopupMenu desktopMenu;

    private final List<DesktopShortcut> clipboard = new ArrayList<>();

    private long lastPopupTime = 0;

    public DesktopInteractionLayer(DesktopShortcutsLayer layer, Desktop desktop) {
        this.shortcutsLayer = layer;
        this.desktop = desktop;
        setOpaque(false);

        // Build popup menus
        shortcutMenu = buildShortcutMenu();
        desktopMenu = buildDesktopMenu();

        installMouseHandlers();
        installKeyBindings();

        new DesktopShortcutNavigator(shortcutsLayer, this);
    }

    private JPopupMenu buildShortcutMenu() {
        return new DesktopShortcutMenu(this);
    }

    private JPopupMenu buildDesktopMenu() {
        return new DesktopMenu(this.desktop.getTheme(), this.desktop, this);
    }

    private void installKeyBindings() {

        InputMap im = getInputMap();
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), COPY_KEY);
        am.put(COPY_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelection();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), PASTE_KEY);
        am.put(PASTE_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteClipboard();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), SELECT_ALL_KEY);
        am.put(SELECT_ALL_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAllShortcuts();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_KEY);
        am.put(DELETE_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelection();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), RENAME_KEY);
        am.put(RENAME_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRenameDialog();
            }
        });
    }


    // === clipboard and selection helpers ===

    private void cutSelection() {
        copySelection();
        deleteSelection();
    }

    private void copySelection() {
        clipboard.clear();
        clipboard.addAll(shortcutsLayer.getSelectedShortcuts());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void pasteClipboard() {
        if (clipboard.isEmpty()) {
            return;
        }

        int offset = 20;
        List<DesktopShortcut> newShortcuts = new ArrayList<>();

        for (DesktopShortcut original : clipboard) {
            // extract text + icon
            String text = original.getText();
            Icon icon = original.getIcon();

            // clone icon if possible
            Icon clonedIcon = FileUtils.cloneIcon(icon);

            // create new shortcut
            DesktopShortcut copy = new DesktopShortcut(clonedIcon, text);

            Point pos = original.getLocation();
            copy.setLocation(pos.x + offset, pos.y + offset);

            shortcutsLayer.addShortcut(copy, new Point(copy.getX(), copy.getY()));
            newShortcuts.add(copy);
        }

        shortcutsLayer.clearSelection();
        for (DesktopShortcut s : newShortcuts) {
            shortcutsLayer.addToSelection(s);
        }
        repaint();
    }

    private void selectAllShortcuts() {
        shortcutsLayer.clearSelection();
        for (DesktopShortcut s : shortcutsLayer.getShortcuts()) {
            shortcutsLayer.addToSelection(s);
        }
        repaint();
    }

    private void deleteSelection() {
        for (DesktopShortcut s : new ArrayList<>(shortcutsLayer.getSelectedShortcuts())) {
            shortcutsLayer.removeShortcut(s);
        }
        shortcutsLayer.clearSelection();
        repaint();
    }

    private void showRenameDialog() {
        if (shortcutsLayer.getSelectedShortcuts().isEmpty()) {
            return;
        }
        String newName = JOptionPane.showInputDialog(
            this,
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

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // === arrange icons ===
    @SuppressWarnings("checkstyle:MagicNumber")
    private void arrangeShortcuts() {
        int x = 20;
        int y = 20;
        int spacing = 90;

        for (DesktopShortcut s : shortcutsLayer.getShortcuts()) {
            s.setLocation(x, y);
            y += spacing;
            if (y + spacing > getHeight()) {
                y = 20;
                x += spacing;
            }
        }
        repaint();
    }

    // === selection band and drag handling ===
    @SuppressWarnings("checkstyle:AnonInnerLength")
    private void installMouseHandlers() {
        MouseAdapter adapter = new MouseAdapter() {

            @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:ReturnCount"})
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point pToShortcuts = SwingUtilities.convertPoint(
                        DesktopInteractionLayer.this,    // from
                        e.getPoint(),                    // point in this layer
                        shortcutsLayer                   // to
                    );
                    Component c = shortcutsLayer.getComponentAt(pToShortcuts);
                    if (c instanceof DesktopShortcut shortcut) {
                        if (e.isControlDown()) {
                            if (shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                                shortcutsLayer.removeFromSelection(shortcut);
                            } else {
                                shortcutsLayer.addToSelection(shortcut);
                            }
                        } else {
                            if (!shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                                shortcutsLayer.clearSelection();
                                shortcutsLayer.addToSelection(shortcut);
                            }
                        }

                        // prepare drag
                        dragStart = e.getPoint();
                        initialPositions.clear();
                        for (DesktopShortcut s : shortcutsLayer.getSelectedShortcuts()) {
                            initialPositions.put(s, s.getLocation());
                        }
                        draggingIcons = true;
                    } else {
                        // empty space → rubber band
                        shortcutsLayer.clearSelection();
                        bandStart = e.getPoint();
                        selectionRect = new Rectangle(bandStart);
                        draggingIcons = false;
                    }
                    repaint();
                }
            }

            @SuppressWarnings("checkstyle:NestedIfDepth")
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (draggingIcons && dragStart != null) {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;

                        for (Map.Entry<DesktopShortcut, Point> entry : initialPositions.entrySet()) {
                            DesktopShortcut s = entry.getKey();
                            Point startPos = entry.getValue();
                            s.setLocation(startPos.x + dx, startPos.y + dy);
                        }
                    } else if (bandStart != null) {
                        selectionRect.setBounds(
                            Math.min(bandStart.x, e.getX()),
                            Math.min(bandStart.y, e.getY()),
                            Math.abs(bandStart.x - e.getX()),
                            Math.abs(bandStart.y - e.getY())
                        );

                        for (DesktopShortcut s : shortcutsLayer.getShortcuts()) {
                            boolean inside = selectionRect.intersects(s.getBounds());
                            if (inside) {
                                shortcutsLayer.addToSelection(s);
                            } else {
                                shortcutsLayer.removeFromSelection(s);
                            }
                        }
                    }
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStart = null;
                    bandStart = null;
                    initialPositions.clear();
                    selectionRect = null;
                    draggingIcons = false;
                    repaint();
                }
            }
        };

        // Right click (popup)
        MouseAdapter popupHandler = new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:MagicNumber"})
            private void showPopup(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    e.consume();

                    if (System.getProperty("os.name").startsWith("Mac")) {
                        long now = System.currentTimeMillis();
                        if (now - lastPopupTime < 500) {
                            return; // ignore events too close to previous
                        }
                        lastPopupTime = now;
                    }

                    Point p = SwingUtilities.convertPoint(
                        DesktopInteractionLayer.this,    // from
                        e.getPoint(),                    // point in this layer
                        shortcutsLayer                   // to
                    );
                    Component c = shortcutsLayer.getComponentAt(p);
                    if (c instanceof DesktopShortcut shortcut) {
                        shortcutsLayer.clearSelection();
                        shortcutsLayer.addToSelection(shortcut);
                        SwingUtilities.invokeLater(() -> {
                            if (!shortcutMenu.isVisible()) {
                                shortcutMenu.show(DesktopInteractionLayer.this, p.x, p.y);
                            }
                        });
                    } else {
                        if (!desktopMenu.isVisible()) {
                            SwingUtilities.invokeLater(() -> desktopMenu.show(DesktopInteractionLayer.this, p.x, p.y));
                        }
                    }
                }
            }
        };

        addMouseListener(adapter);
        addMouseListener(popupHandler);
        addMouseMotionListener(adapter);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void paintComponent(Graphics g) {
        if (selectionRect != null) {
            g.setColor(new Color(0,  120,  215,  50));
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            g.setColor(Color.BLUE);
            g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {

            case OPEN:
                showMessage("Open shortcut");
                break;

            case CUT:
                cutSelection();
                break;

            case COPY:
                copySelection();
                break;

            case PASTE:
                pasteClipboard();
                break;

            case DELETE:
                deleteSelection();
                break;

            case SELECT_ALL:
                selectAllShortcuts();
                break;

            case ARRANGE:
                arrangeShortcuts();
                break;

            case RENAME:
                showRenameDialog();
                break;

            case PROPERTIES:
                if (!this.shortcutsLayer.getSelectedShortcuts().isEmpty()) {
                    DesktopShortcutDialog dlg = new DesktopShortcutDialog(
                        DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame()
                    );
                    dlg.setData(this.shortcutsLayer.getSelectedShortcuts().get(0));
                    dlg.setVisible(true);
                }
                break;

            case Constants.CREATE_SHORTCUT:
                DesktopShortcut shortcut = new DesktopShortcut(null, "New Shortcut");
                shortcutsLayer.addShortcut(shortcut,  new Point(50,  50));
                getParent().repaint();
                break;

            default:
                break;
        }
    }
}