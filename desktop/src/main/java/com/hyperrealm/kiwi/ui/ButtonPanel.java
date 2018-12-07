/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

/**
 * This class is a simple extension of <code>KPanel</code> that arranges
 * buttons in a row, in such a way that the buttons are all of equal size,
 * and justified flush with the right or left edge of the panel. Many Kiwi
 * dialogs and frames provide buttons in their lower-right areas or toolbars
 * in their upper-left areas that are positioned in just this way; this
 * class eliminates the need to perform the layout explicitly in code each
 * time this effect is desired.
 * <p>
 * The row of buttons is anchored at either the left or the right edge of
 * the panel. If the sum of the preferred widths of the buttons exceeds the
 * available horizontal space, the buttons are compressed horizontally to
 * fit.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KPanel
 */

public class ButtonPanel extends KPanel {
    /**
     * The default horizontal spacing.
     */
    private static final int DEFAULT_SPACING = 5;

    private static final int LEFT = 0, RIGHT = 1;

    private KPanel bButtons;

    /**
     * Construct a new <code>ButtonPanel</code> with the default horizontal
     * spacing and right alignment.
     */

    public ButtonPanel() {
        this(SwingConstants.RIGHT, DEFAULT_SPACING);
    }

    /**
     * Construct a new <code>ButtonPanel</code> with default horizontal
     * spacing and the given alignment.
     *
     * @param alignment The alignment of the buttons within their containing
     *                  panel; one of <code>SwingConstants.LEFT</code> or
     *                  <code>SwingConstants.RIGHT</code>.
     */

    public ButtonPanel(int alignment) {
        this(alignment, DEFAULT_SPACING);
    }

    /**
     * Construct a new <code>ButtonPanel</code> with the given horizontal
     * spacing and alignment.
     *
     * @param spacing   The size of the gap (in pixels) to place between buttons
     *                  horizontally.
     * @param alignment The alignment of the buttons within their containing
     *                  panel; one of <code>SwingConstants.LEFT</code> or
     *                  <code>SwingConstants.RIGHT</code>.
     */

    public ButtonPanel(int alignment, int spacing) {
        setOpaque(false);

        int anchor = ((alignment == SwingConstants.RIGHT) ? RIGHT : LEFT);
        setLayout(new AnchorLayout(anchor));

        bButtons = new KPanel();
        bButtons.setLayout(new GridLayout(1, 0, spacing, 0));

        add(bButtons);
    }

    /**
     * Add a button to the <code>ButtonPanel</code>.
     *
     * @param button The button to add.
     * @see #removeButton
     */

    public void addButton(AbstractButton button) {
        bButtons.add(button);
    }

    /**
     * Add a button to the <code>ButtonPanel</code> at the specified position.
     *
     * @param button The button to add.
     * @param pos    The position at which to add the button. The value 0 denotes
     *               the first position, and -1 denotes the last position.
     * @throws IllegalArgumentException If the value of
     *                                  <code>pos</code> is invalid.
     */

    public void addButton(AbstractButton button, int pos)
        throws IllegalArgumentException {
        bButtons.add(button, pos);
    }

    /**
     * Get a reference to the button at the specified position in the
     * <code>ButtonPanel</code>.
     *
     * @param pos The position of the button to retrieve.
     * @return The button.
     */

    public AbstractButton getButton(int pos) {
        return ((AbstractButton) bButtons.getComponent(pos));
    }

    /**
     * Remove a button from the <code>ButtonPanel</code>.
     *
     * @param button The button to remove.
     * @see #addButton
     */

    public void removeButton(AbstractButton button) {
        bButtons.remove(button);
    }

    /**
     * Remove a button from the specified position in the
     * <code>ButtonPanel</code>.
     *
     * @param pos The position of the button to remove, where 0 denotes the
     *            first position.
     */

    public void removeButton(int pos) {
        bButtons.remove(pos);
    }

    /**
     * Remove all buttons from the <code>ButtonPanel</code>.
     *
     * @since Kiwi 2.0
     */

    public void removeAllButtons() {
        bButtons.removeAll();
    }

    /**
     * Get the number of buttons in this <code>ButtonPanel</code>.
     *
     * @return The number of buttons.
     */

    public int getButtonCount() {
        return (bButtons.getComponentCount());
    }

    /* The custom layout manager for this component. Normally I would frown upon
     * writing a layout manager, but in this case, none of the existing layout
     * managers did the job, and there was no other elegant and foolproof way to
     * achieve this effect.
     *
     * This layout manager can only handle one child component in a container.
     * It anchors the child either to the right or the left edge of its parent.
     * The child's size is set to its preferred size, if the parent is big
     * enough. Otherwise, the child's size is set to the size of the parent.
     */

    private class AnchorLayout implements LayoutManager2 {

        static final float DEFAULT_LAYOUT_ALIGNMENT = 0.5f;

        private int anchor;

        private Component c = null;

        private Dimension noSize = new Dimension(0, 0);

        AnchorLayout(int anchor) {
            this.anchor = anchor;
        }

        public float getLayoutAlignmentX(Container cont) {
            return DEFAULT_LAYOUT_ALIGNMENT;
        }

        public float getLayoutAlignmentY(Container cont) {
            return DEFAULT_LAYOUT_ALIGNMENT;
        }

        public void addLayoutComponent(String name, Component comp) {
            if (c != null) {
                return;
            }

            c = comp;
        }

        public void addLayoutComponent(Component comp, Object constraints) {
            addLayoutComponent((String) null, comp);
        }

        public void removeLayoutComponent(Component comp) {
            if (comp == c) {
                c = null;
            }
        }

        private Dimension layoutSize(Dimension size, Insets insets) {
            if (c == null) {
                return (noSize);
            }

            return (new Dimension((insets.left + size.width + insets.right),
                (insets.top + size.height + insets.bottom)));
        }

        public Dimension maximumLayoutSize(Container cont) {
            return (c.getMaximumSize());
        }

        public void invalidateLayout(Container cont) {
            // nothing to do
        }

        // The minimum layout size is based on the component's minimum size.

        public Dimension minimumLayoutSize(Container cont) {
            return (layoutSize(c.getMinimumSize(), cont.getInsets()));
        }

        // The preferred layout size is based on the component's preferred size.

        public Dimension preferredLayoutSize(Container cont) {
            return (layoutSize(c.getPreferredSize(), cont.getInsets()));
        }

        // Lay out the container.

        public void layoutContainer(Container cont) {
            if (c == null) {
                return;
            }

            Dimension size = cont.getSize();
            Insets insets = cont.getInsets();

            int w = size.width - (insets.left + insets.right);
            int h = size.height - (insets.top + insets.bottom);
            Dimension p = c.getPreferredSize();

            int cw = p.width;

            if (cw > w) {
                cw = w;
                c.setSize(cw, h);
            } else {
                c.setSize(p);
            }

            int offset = ((anchor == LEFT) ? 0 : (w - cw));

            c.setLocation(insets.left + offset, insets.top);
        }
    }

}
