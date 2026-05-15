package jworkspace.ui.widgets;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
// CHECKSTYLE:OFF
@SuppressWarnings({
    "checkstyle:regexp",
    "checkstyle:MagicNumber",
    "checkstyle:MultipleStringLiterals",
    "checkstyle:HideUtilityClassConstructor"
})
public class MacPopupDebug {

    private static long lastPopupTime = 0;

    static void main(String[] args) {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        SwingUtilities.invokeLater(MacPopupDebug::createUi);
    }

    @SuppressWarnings({"checkstyle:regexp", "checkstyle:MagicNumber", "checkstyle:ReturnCount"})
    private static void createUi() {
        JFrame frame = new JFrame("Mac Popup Debug");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        JDesktopPane desktop = new JDesktopPane();
        desktop.setBackground(new Color(140, 140, 140));

        JLabel label = new JLabel("""
                Right click anywhere.
                
                Watch console output:
                - mousePressed
                - mouseReleased
                - popup trigger state
                - popup visibility
                
                Test:
                - normal right click
                - click and hold
                - quick click
                - Magic Mouse gestures
                """);

        label.setForeground(Color.WHITE);
        label.setBounds(20, 20, 600, 200);

        desktop.add(label);

        JPopupMenu popup = createPopup();

        MouseAdapter adapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                log(e, "PRESSED");
                maybeShowPopup(e, popup);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                log(e, "RELEASED");
                maybeShowPopup(e, popup);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                log(e, "CLICKED");
            }
        };

        desktop.addMouseListener(adapter);

        frame.setContentPane(desktop);

        Toolkit.getDefaultToolkit().addAWTEventListener(
            event -> {
                if (!(event instanceof MouseEvent e)) {
                    return;
                }

                String type = switch (e.getID()) {
                    case MouseEvent.MOUSE_PRESSED -> "PRESSED";
                    case MouseEvent.MOUSE_RELEASED -> "RELEASED";
                    case MouseEvent.MOUSE_CLICKED -> "CLICKED";
                    case MouseEvent.MOUSE_DRAGGED -> "DRAGGED";
                    //case MouseEvent.MOUSE_MOVED -> "MOVED";
                    default -> null;
                };

                if (type == null) {
                    return;
                }

                Component source = e.getComponent();

                System.out.printf(
                    "GLOBAL %-10s button=%d popup=%s consumed=%s source=%s x=%d y=%d%n",
                    type,
                    e.getButton(),
                    e.isPopupTrigger(),
                    e.isConsumed(),
                    source == null
                        ? "null"
                        : source.getClass().getSimpleName(),
                    e.getX(),
                    e.getY()
                );
            },
            AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK
        );

        frame.setVisible(true);
    }

    private static JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();

        popup.add(new JMenuItem("Open"));
        popup.add(new JMenuItem("Rename"));
        popup.add(new JMenuItem("Delete"));

        JMenu submenu = new JMenu("Advanced");
        submenu.add(new JMenuItem("Properties"));
        submenu.add(new JMenuItem("Settings"));

        popup.addSeparator();
        popup.add(submenu);

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                System.out.println("VISIBLE");
            }

            @Override
            public void popupMenuWillBecomeInvisible(
                PopupMenuEvent e
            ) {
                System.out.println("INVISIBLE");
            }

            @Override
            public void popupMenuCanceled(
                PopupMenuEvent e
            ) {
                System.out.println("CANCELED");
            }
        });

        return popup;
    }

    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MagicNumber"})
    private static void maybeShowPopup(
        MouseEvent e,
        JPopupMenu popup
    ) {
        if (!e.isPopupTrigger()) {
            return;
        }

        long now = System.currentTimeMillis();

        // Prevent double-open on macOS
        if (now - lastPopupTime < 150) {
            System.out.println("IGNORED DUPLICATE POPUP");
            return;
        }

        lastPopupTime = now;

        System.out.printf(
            "SHOW POPUP x=%d y=%d button=%d%n",
            e.getX(),
            e.getY(),
            e.getButton()
        );

        SwingUtilities.invokeLater(() -> {
            popup.setVisible(false); // defensive reset
            popup.show(
                e.getComponent(),
                e.getX(),
                e.getY()
            );
        });
    }

    private static void log(MouseEvent e, String phase) {
        System.out.printf(
            "%s | button=%d | popup=%s | x=%d y=%d%n",
            phase,
            e.getButton(),
            e.isPopupTrigger(),
            e.getX(),
            e.getY()
        );
    }
}
// CHECKSTYLE:ON