package jworkspace.ui.widgets;

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.hyperrealm.kiwi.ui.KPanel;

public class InternalToolBar extends KPanel
    implements MouseListener, MouseMotionListener {
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    protected int orientation = 0;
    protected boolean floatable = false;
    protected boolean docked = false;
    protected JDesktopPane desktop = null;
    private String title = null;
    private GridBagLayout g = null;
    private GridBagConstraints c = null;
    private ImageIcon icon = null;
    private Thumb thumb = null;
    private InternalFrame frame = null;
    private JPanel panel = null;
    private Border border = null;
    private Rectangle dockSpace = null;
    public InternalToolBar(String title, int orientation, JDesktopPane desktop) {
        super(new BorderLayout());
        floatable = false;
        docked = true;
        g = new GridBagLayout();
        c = new GridBagConstraints();
        c.anchor = 17;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        panel = new JPanel(g);
        border = LineBorder.createBlackLineBorder();
        setBorder(border);
        if (orientation == 1) {
            c.fill = 3;
            super.add("West", panel);
        } else if (orientation == 0) {
            c.fill = 2;
            super.add("North", panel);
        }
        this.title = title;
        this.orientation = orientation;
        this.desktop = desktop;
        setOpaque(false);
    }

    public void mouseDragged(MouseEvent e) {
        if ((e.getSource() instanceof Thumb) && !floatable) {
            floatable = true;
        }
    }

    public void mouseMoved(MouseEvent mouseevent) {
    }

    public void mouseClicked(MouseEvent mouseevent) {
    }

    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof Thumb) {
            thumb.showPressed();
        }
    }

    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof Thumb) {
            thumb.showReleased();
        }
    }

    public void mousePressed(MouseEvent mouseevent) {
    }

    public void mouseReleased(MouseEvent e) {
        if ((e.getSource() instanceof Thumb) && floatable && docked) {
            Point p = e.getPoint();
            int xHeight = panel.getSize().height;
            unDock();
            if (p.x < 0) {
                p.x += getToolkit().getScreenSize().width;
                p.x -= frame.getSize().width;
            }
            if (p.y < 0) {
                p.y += getToolkit().getScreenSize().height;
                p.y -= frame.getSize().height + xHeight;
            }
            frame.setLocation(p);
        }
    }

    public void setToolBarIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public void addSeparator(int size) {
        Separator separator = new Separator(size, this);
        panel.add(separator);
        if (orientation == 1) {
            c.gridx++;
        } else if (orientation == 0) {
            c.gridy++;
        }
        g.setConstraints(separator, c);
    }

    public JButton add(JButton button) {
        if (c.gridx == 0 && c.gridy == 0) {
            thumb = new Thumb(this);
            thumb.addMouseListener(this);
            thumb.addMouseMotionListener(this);
            panel.add(thumb);
            g.setConstraints(thumb, c);
        }
        panel.add(button);
        if (orientation == 1) {
            c.gridx++;
        } else if (orientation == 0) {
            c.gridy++;
        }
        g.setConstraints(button, c);
        return button;
    }

    public void setToolBarBorder(Border b) {
        border = b;
        setBorder(b);
    }

    protected void unDock() {
        dockSpace = new Rectangle(getLocationOnScreen().x, getLocationOnScreen().y, getSize().width, getSize().height);
        remove(panel);
        setBorder(null);
        Component p;
        for (p = getParent(); !(p instanceof Frame); p = p.getParent()) {
        }
        p.validate();
        panel.remove(thumb);
        panel.validate();
        frame = new InternalFrame(title, panel, desktop, this);
        frame.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent e) {
                Rectangle r = new Rectangle(frame.getLocationOnScreen().x, frame.getLocationOnScreen().y, frame.getSize().width, frame.getSize().height);
                if (dockSpace.intersects(r)) {
                    frame.setVisible(false);
                    dock();
                    frame.dispose();
                }
            }

        });
        frame.addMouseListener(this);
        if (icon != null) {
            frame.setFrameIcon(icon);
        }
        docked = false;
    }

    protected void dock() {
        frame.getContentPane().remove(panel);
        panel.add(thumb);
        thumb.showReleased();
        c.gridx = 0;
        c.gridy = 0;
        g.setConstraints(thumb, c);
        panel.validate();
        if (orientation == 1) {
            super.add("West", panel);
        } else if (orientation == 0) {
            super.add("North", panel);
        }
        setBorder(border);
        Component p;
        for (p = getParent(); !(p instanceof Frame); p = p.getParent()) {
        }
        p.validate();
        floatable = false;
        docked = true;
    }

    public void setFrameIcon(ImageIcon icon) {
        this.icon = icon;
    }

    class Thumb extends JComponent {

        InternalToolBar jit = null;
        Color paint = null;
        Color bg = null;
        Color highlight = null;

        Thumb(InternalToolBar jit) {
            this.jit = jit;
            UIDefaults ui = UIManager.getLookAndFeel().getDefaults();
            bg = ui.getColor("control");
            highlight = ui.getColor("controlShadow");
            paint = bg;
        }

        public void paint(Graphics g) {
            g.setColor(paint);
            g.fillRect(0, 0, getSize().width, getSize().height);
            int x = 0;
            int y = 0;
            boolean even = true;
            for (; y < getSize().height; y += 2) {
                for (; x < getSize().width; x += 4) {
                    g.setColor(paint.brighter());
                    g.fillRect(x, y, 1, 1);
                    g.setColor(paint.darker());
                    g.fillRect(x + 1, y + 1, 1, 1);
                }

                if (!even) {
                    x = 0;
                } else {
                    x = 2;
                }
                even = !even;
            }

        }

        public void showPressed() {
            paint = highlight;
            repaint();
        }

        public void showReleased() {
            paint = bg;
            repaint();
        }

        public Dimension getMinimumSize() {
            if (jit.orientation == 0) {
                return new Dimension(jit.getSize().width - 2, 20);
            } else {
                return new Dimension(20, jit.getSize().height - 2);
            }
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public void updateUI() {
            UIDefaults ui = UIManager.getLookAndFeel().getDefaults();
            bg = ui.getColor("control");
            highlight = ui.getColor("controlShadow");
            paint = bg;
            repaint();
        }
    }

    class InternalFrame extends JInternalFrame {

        private InternalToolBar tb = null;


        InternalFrame(String title, JPanel c, JDesktopPane d, InternalToolBar toolbar) {
            super(title, false, true, false, true);
            tb = toolbar;
            getContentPane().add("Center", c);
            pack();
            d.add(this, JLayeredPane.MODAL_LAYER);
            d.moveToFront(this);
            setVisible(true);
            addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosing(InternalFrameEvent e) {
                    setVisible(false);
                    tb.dock();
                    dispose();
                }
            });
        }
    }

    class Separator extends JComponent {

        private int size = 0;
        private InternalToolBar parent = null;

        Separator(int size, InternalToolBar p) {
            this.size = size;
            parent = p;
            addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    e.consume();
                }

            });
        }

        public Dimension getPreferredSize() {
            if (parent.orientation == 1) {
                return new Dimension(size, 1);
            }
            if (parent.orientation == 0) {
                return new Dimension(1, size);
            } else {
                return new Dimension(size, size);
            }
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
    }
}
