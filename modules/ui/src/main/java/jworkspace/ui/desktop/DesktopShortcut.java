package jworkspace.ui.desktop;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.api.Constants;
import jworkspace.ui.desktop.plaf.DesktopShortcutMenu;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a desktop shortcut component that includes an icon and associated text.
 * This class supports selection toggling, exclusive selection, and drag-and-drop functionality.
 * It extends {@link JComponent} to allow customization of its appearance and behavior.
 */
public class DesktopShortcut extends JComponent {

    private static final int DESKTOP_ICON_PREFERRED_SIZE = 96;

    private static final BrightnessReducer BRIGHTNESS_REDUCER = new BrightnessReducer();

    @Getter
    private boolean selected;

    private final JLabel iconLabel;

    private final DesktopIconLabel textLabel;

    @Getter
    @Setter
    private Supplier<Boolean> selectionProvider;

    @Getter
    @Setter
    private String commandLine = "";

    @Getter
    @Setter
    private String workingDirectory = "";

    @Setter
    @Getter
    private String comments = "";

    @Setter
    @Getter
    private int mode;

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:ParameterAssignment"})
    public DesktopShortcut(Icon icon, String text) {
        setLayout(new BorderLayout());
        setOpaque(false);

        textLabel = new DesktopIconLabel(text);
        textLabel.setAlignment(DesktopIconLabel.CENTER);
        textLabel.setFont(textLabel.getFont().deriveFont(12f));
        add(textLabel, BorderLayout.SOUTH);

        if (icon == null) {
            icon = new ImageIcon(WorkspaceGUI.getResourceManager().getImage(Constants.DEFAULT_ICON));
        }

        iconLabel = new JLabel(icon, SwingConstants.CENTER);
        add(iconLabel, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFocusable(true);

        setComponentPopupMenu(new DesktopShortcutMenu());
    }

    // Selection API
    public void setSelected(boolean selected) {
        this.selected = selected; repaint();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (selected) {
            g2.setColor(new Color(0, 120, 215, 80));
            g2.fillRoundRect(0, 0,  getWidth(),  getHeight(), 8, 8);
        }
        if (isFocusOwner()) {
            g2.setColor(Color.BLUE);
            g2.drawRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        }
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DESKTOP_ICON_PREFERRED_SIZE, DESKTOP_ICON_PREFERRED_SIZE);
    }

    public String getText() {
        return textLabel.getText();
    }

    public void setText(String text) {
        textLabel.setText(text);
    }

    public Icon getIcon() {
        return iconLabel.getIcon();
    }

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    public void load(ObjectInputStream dataStream) throws IOException, ClassNotFoundException {

        setText(dataStream.readUTF());
        setCommandLine(dataStream.readUTF());
        setToolTipText((getCommandLine() == null || getCommandLine().isEmpty()) ? getText() : getCommandLine());
        setWorkingDirectory(dataStream.readUTF());
        setMode(dataStream.readInt());
        setLocation(dataStream.readInt(), dataStream.readInt());
        setComments(dataStream.readUTF());

        Object obj = dataStream.readObject();
        if (obj instanceof ImageIcon) {
            setIcon((ImageIcon) obj);
        }
    }

    public void save(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeUTF(getText());
        outputStream.writeUTF(commandLine);
        outputStream.writeUTF(workingDirectory);
        outputStream.writeInt(mode);
        outputStream.writeInt(getX());
        outputStream.writeInt(getY());
        outputStream.writeUTF(comments);
        outputStream.writeObject(getIcon());
    }

    static class BrightnessReducer extends RGBImageFilter implements Serializable {
        @SuppressWarnings("checkstyle:MagicNumber")
        public int filterRGB(int x, int y, int rgb) {
            int a = (rgb >> 24) & 0xff;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;

            if (r > 50) {
                r = r - 50;
            } else {
                r = 0;
            }
            if (g > 50) {
                g = g - 50;
            } else {
                g = 0;
            }
            if (b > 50) {
                b = b - 50;
            } else {
                b = 0;
            }

            return (a << 24 | r << 16 | g << 8 | b);
        }
    }
}
