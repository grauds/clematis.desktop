package jworkspace.ui.desktop.plaf;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jworkspace.ui.desktop.DesktopShortcut;

/**
 * The DesktopShortcutNavigator class is responsible for enabling keyboard navigation
 * and interaction with desktop-style shortcuts managed by a {@code DesktopShortcutsLayer}.
 * This class binds custom keyboard actions to a target {@code JComponent}, allowing the
 * user to navigate, select, and interact with shortcuts using defined key bindings.
 * <p>
 * This class supports the following functionalities:
 * <p>
 * 1. Arrow key navigation (Left, Right, Up, Down), with support for extending selection
 *    using the Shift key.
 * 2. Home/End key navigation for selecting the first or last shortcut, respectively.
 * 3. Enter key interaction to simulate the "open" action on the selected shortcut(s).
 * <p>
 * Features:
 * - Automatically installs key bindings into the specified target component.
 * - Supports single or extended selection modes for easier navigation and manipulation of shortcuts.
 * - Provides interaction callbacks for operations such as selecting a shortcut,
 *   navigating between shortcuts, and invoking actions on selected shortcuts.
 * <p>
 * Keyboard Bindings:
 * - Arrow keys: Move focus between shortcuts based on spatial arrangement.
 * - Shift + Arrow keys: Extend selection while navigating.
 * - Home: Select the first shortcut.
 * - End: Select the last shortcut.
 * - Enter: Open the selected shortcut(s).
 * <p>
 * Usage Notes:
 * - The class requires a {@code DesktopShortcutsLayer} instance to manage the collection of shortcuts.
 * - A target {@code JComponent} is used to bind the keyboard actions.
 * - Ensure the target component is focusable and has appropriate focus behavior.
 */
public class DesktopShortcutNavigator {

    public static final String LEFT_KEY = "left";
    public static final String RIGHT_KEY = "right";
    public static final String UP_KEY = "up";
    public static final String DOWN_KEY = "down";
    public static final String SHIFT_LEFT_KEY = "shiftLeft";
    public static final String SHIFT_RIGHT_KEY = "shiftRight";
    public static final String SHIFT_UP_KEY = "shiftUp";
    public static final String SHIFT_DOWN_KEY = "shiftDown";
    public static final String HOME_KEY = "home";
    public static final String END_KEY = "end";
    public static final String ENTER_KEY = "enter";
    private final DesktopShortcutsLayer shortcutsLayer;
    private final JComponent targetComponent; // the component to bind keys to

    public DesktopShortcutNavigator(DesktopShortcutsLayer layer, JComponent target) {
        this.shortcutsLayer = layer;
        this.targetComponent = target;

        installBindings();
        targetComponent.setFocusable(true);
        targetComponent.requestFocusInWindow();
    }

    private void installBindings() {
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap im = targetComponent.getInputMap(condition);
        ActionMap am = targetComponent.getActionMap();

        // Arrow navigation
        im.put(KeyStroke.getKeyStroke("LEFT"), LEFT_KEY);
        im.put(KeyStroke.getKeyStroke("RIGHT"), RIGHT_KEY);
        im.put(KeyStroke.getKeyStroke("UP"), UP_KEY);
        im.put(KeyStroke.getKeyStroke("DOWN"), DOWN_KEY);

        im.put(KeyStroke.getKeyStroke("shift LEFT"), SHIFT_LEFT_KEY);
        im.put(KeyStroke.getKeyStroke("shift RIGHT"), SHIFT_RIGHT_KEY);
        im.put(KeyStroke.getKeyStroke("shift UP"), SHIFT_UP_KEY);
        im.put(KeyStroke.getKeyStroke("shift DOWN"), SHIFT_DOWN_KEY);

        // Home / End
        im.put(KeyStroke.getKeyStroke("HOME"), HOME_KEY);
        im.put(KeyStroke.getKeyStroke("END"), END_KEY);

        // Enter → open
        im.put(KeyStroke.getKeyStroke("ENTER"), ENTER_KEY);

        am.put(LEFT_KEY, new NavigateAction(-1, 0, false));
        am.put(RIGHT_KEY, new NavigateAction(1, 0, false));
        am.put(UP_KEY, new NavigateAction(0, -1, false));
        am.put(DOWN_KEY, new NavigateAction(0, 1, false));

        am.put(SHIFT_LEFT_KEY, new NavigateAction(-1, 0, true));
        am.put(SHIFT_RIGHT_KEY, new NavigateAction(1, 0, true));
        am.put(SHIFT_UP_KEY, new NavigateAction(0, -1, true));
        am.put(SHIFT_DOWN_KEY, new NavigateAction(0, 1, true));

        am.put(HOME_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectShortcutAtIndex(0, false);
            }
        });

        am.put(END_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectShortcutAtIndex(shortcutsLayer.getShortcuts().size() - 1, false);
            }
        });

        am.put(ENTER_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<DesktopShortcut> sel = shortcutsLayer.getSelectedShortcuts();
                if (!sel.isEmpty()) {
                    JOptionPane.showMessageDialog(targetComponent,
                        "Open shortcut: " + ((JLabel) sel.get(0).getComponent(0)).getText(),
                        "Open", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void selectShortcutAtIndex(int index, boolean extend) {
        if (index < 0 || index >= shortcutsLayer.getShortcuts().size()) {
            return;
        }
        DesktopShortcut s = shortcutsLayer.getShortcuts().get(index);
        if (!extend) {
            shortcutsLayer.clearSelection();
        }
        shortcutsLayer.addToSelection(s);
        s.scrollRectToVisible(s.getBounds());
        shortcutsLayer.repaint();
    }

    private class NavigateAction extends AbstractAction {
        private final int dx;
        private final int dy;
        private final boolean extend;

        NavigateAction(int dx, int dy, boolean extend) {
            this.dx = dx;
            this.dy = dy;
            this.extend = extend;
        }

        @SuppressWarnings("checkstyle:ReturnCount")
        @Override
        public void actionPerformed(ActionEvent e) {
            if (shortcutsLayer.getShortcuts().isEmpty()) {
                return;
            }

            List<DesktopShortcut> selected = shortcutsLayer.getSelectedShortcuts();
            DesktopShortcut current;

            if (selected.isEmpty()) {
                // no selection → pick top-left (smallest x + y)
                current = shortcutsLayer.getShortcuts().stream()
                    .min((a, b) -> {
                        Point pa = a.getLocation();
                        Point pb = b.getLocation();
                        int cmp = Integer.compare(pa.y, pb.y); // first by row (y)
                        if (cmp == 0) {
                            cmp = Integer.compare(pa.x, pb.x); // then by column (x)
                        }
                        return cmp;
                    }).orElse(shortcutsLayer.getShortcuts().get(0));
                if (!extend) {
                    shortcutsLayer.clearSelection();
                }
                shortcutsLayer.addToSelection(current);
                current.scrollRectToVisible(current.getBounds());
                shortcutsLayer.repaint();
                return; // done
            }

            // if we already have a selection, use the last selected
            current = selected.get(selected.size() - 1);

            DesktopShortcut target = findNearest(current, dx, dy);
            if (target != null) {
                if (!extend) {
                    shortcutsLayer.clearSelection();
                }
                shortcutsLayer.addToSelection(target);
                target.scrollRectToVisible(target.getBounds());
                shortcutsLayer.repaint();
            }
        }

        private DesktopShortcut findNearest(DesktopShortcut from, int dx, int dy) {
            Point base = from.getLocation();
            List<DesktopShortcut> all = shortcutsLayer.getShortcuts();

            DesktopShortcut best = null;
            double bestDist = Double.MAX_VALUE;

            for (DesktopShortcut s : all) {
                if (s == from) {
                    continue;
                }
                Point loc = s.getLocation();
                int ddx = loc.x - base.x;
                int ddy = loc.y - base.y;

                if (dx != 0 && Integer.signum(ddx) != dx) {
                    continue;
                }
                if (dy != 0 && Integer.signum(ddy) != dy) {
                    continue;
                }

                double dist = Math.sqrt(ddx * ddx + ddy * ddy);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = s;
                }
            }
            return best;
        }
    }
}
