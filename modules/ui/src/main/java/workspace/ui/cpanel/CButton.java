package jworkspace.ui.cpanel;

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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A special kind of buttons that are to be inserted into
 * control panel. This pecularity is expressed in
 * boolean reverse field, that is used by control panel's
 * layout algorythm. Use <code>addLayoutComponent()</code>
 * method with specified parameters to insert buttons
 * and separators into control panel.
 */
public class CButton extends JButton {
    /**
     * The highlighted color.
     */
    private Color highlightColor;
    /**
     * The default color.
     */
    private Color defaultColor;

    /**
     * ControlPanelButton is rollover button, so
     * we should provide two images.
     */
    public CButton(Icon image, Icon rolloverImage) {
        super(image);
        setRolloverIcon(rolloverImage);
        setPressedIcon(rolloverImage);
        setRolloverEnabled(true);
        setDefaultCapable(false);
        /**
         * These buttons are always transparent
         */
        setOpaque(false);

        setMargin(new Insets(2, 2, 2, 2));

        initButton(Color.white);
    }

    /**
     * ControlPanelButton is rollover button, so
     * we should provide two images and highlight color.
     */
    public CButton(Icon image, Icon rolloverImage, Color highlight) {
        this(image, rolloverImage);
        initButton(highlight);
    }

    /**
     * Create button
     */
    public static CButton create(ActionListener listener,
                                 Icon image, Icon hoverImage,
                                 String command, String toolTipText) {
        CButton button = new CButton(image, hoverImage);
        button.setActionCommand(command);
        button.setToolTipText(toolTipText);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Create button with highlight color
     */
    public static CButton create(ActionListener listener,
                                 Icon image, Icon hoverImage,
                                 Color highlight,
                                 String command, String toolTipText) {
        CButton button = CButton.create(listener, image, hoverImage,
            command, toolTipText);
        button.initButton(highlight);
        return button;
    }

    public void paintBorder(Graphics g) {
        ButtonModel model = getModel();
        if (model.isRollover() && !(model.isPressed() && !model.isArmed())) {
            super.paintBorder(g);
        }
    }

    /**
     * Does the extra initialisations.
     *
     * @param highlightColor The highlight color.
     */
    protected void initButton(Color highlightColor) {
        this.highlightColor = highlightColor;
        defaultColor = getBackground();

        addMouseListener(new MouseHandler());
    }

    public float getAlignmentX() {
        return CENTER_ALIGNMENT;
    }

    public float getAlignmentY() {
        return CENTER_ALIGNMENT;
    }

    /**
     * Overriden to ensure that the button won't stay highlighted if it had the
     * mouse over it.
     *
     * @param b Button state.
     */
    public void setEnabled(boolean b) {
        reset();
        super.setEnabled(b);
    }

    /**
     * Forces the button to unhighlight.
     */
    protected void reset() {
        setBackground(defaultColor);
    }

    /**
     * Update UI method is overriden to keep button
     * transparent.
     */
    public void updateUI() {
        super.updateUI();
        defaultColor = getBackground();
        setOpaque(false);
    }

    /**
     * The mouse handler which makes the highlighting.
     *
     * @author julien
     */
    private class MouseHandler extends MouseAdapter {
        /**
         * When the mouse passes over the button.
         *
         * @param e The event.
         */
        public void mouseEntered(MouseEvent e) {
            if (isEnabled()) {
                setBackground(highlightColor);
            }
        }

        /**
         * When the mouse passes out of the button.
         *
         * @param e The event.
         */
        public void mouseExited(MouseEvent e) {
            if (isEnabled()) {
                setBackground(defaultColor);
            }
        }
    }
}