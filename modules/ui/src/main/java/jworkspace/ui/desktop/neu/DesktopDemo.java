package jworkspace.ui.desktop.neu;

import java.awt.Point;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jworkspace.ui.desktop.DesktopShortcut;
import jworkspace.ui.desktop.plaf.ClematisDesktopPaneUI;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;


public class DesktopDemo {
   public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
           JFrame frame = new JFrame("Desktop Shortcuts Demo");
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.setSize(800, 600);

           JDesktopPane desktop = new JDesktopPane();
           desktop.setUI(new ClematisDesktopPaneUI());
           DesktopShortcutsLayer shortcutsLayer = ((ClematisDesktopPaneUI) desktop.getUI()).getShortcutsLayer();

           for (int i = 0; i < 12; i++) {
               DesktopShortcut shortcut = new DesktopShortcut( UIManager.getIcon("FileView.computerIcon"),
                   "App " + (i + 1)
               );
               int x = 40 + (i % 4) * 100;
               int y = 40 + (i / 4) * 100;
               shortcutsLayer.addShortcut(shortcut, new Point(x, y));
           }

           frame.setContentPane(desktop);
           frame.setVisible(true);
       });
   }
}
