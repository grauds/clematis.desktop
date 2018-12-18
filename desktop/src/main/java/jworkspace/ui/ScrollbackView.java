package jworkspace.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTextArea;

/**
 * Scrollback view automatically scrolls view as new entries are added
 *
 * @author Anton Troshin
 */
public class ScrollbackView extends JTextArea {

    private static final int DEFAULT_FONT_SIZE = 13;

    public void setFont(Font font) {
        super.setFont(new Font("Monospaced", Font.PLAIN, DEFAULT_FONT_SIZE));
    }

    public void append(String str) {
        super.append(str + "\n");
        Rectangle b = new Rectangle(0, getHeight() - DEFAULT_FONT_SIZE, getWidth(), getHeight());
        scrollRectToVisible(b);
    }

    public void setBackground(Color bg) {
        super.setBackground(Color.lightGray);
    }

    public void setForeground(Color fg) {
        super.setForeground(Color.black);
    }
}
