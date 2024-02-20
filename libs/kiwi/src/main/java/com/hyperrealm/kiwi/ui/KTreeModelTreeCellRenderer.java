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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.hyperrealm.kiwi.ui.model.tree.KTreeModel;
import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeAdapter;

/**
 * An implementation of <code>TreeCellRenderer</code> for use with
 * <code>JTree</code>s that are connected to a <code>KTreeModel</code> via a
 * <code>KTreeModelTreeAdapter</code>. This cell renderer consults the tree
 * model for a cell's rendering information, such as its label and ICON.
 *
 * @author Mark Lindner
 * @see javax.swing.JTree
 * @see KTreeModel
 * @see KTreeModelTreeAdapter
 */

public class KTreeModelTreeCellRenderer extends JLabel
    implements TreeCellRenderer {

    private KTreeModel model = null;

    private Color highlightBackground
        = UIManager.getColor("Tree.selectionBackground");

    private Color highlightForeground
        = UIManager.getColor("Tree.selectionForeground");

    /**
     * Construct a new <code>ModelTreeCellRenderer</code>.
     */

    public KTreeModelTreeCellRenderer() {
    }

    /**
     * Construct a new <code>ModelTreeCellRenderer</code>.
     *
     * @param model The tree model that will be used with this renderer.
     */

    public KTreeModelTreeCellRenderer(KTreeModel model) {
        this();
        setModel(model);
    }

    /**
     * Set the data model for this renderer.
     *
     * @param model The model.
     */

    public void setModel(KTreeModel model) {
        this.model = model;
    }

    /**
     * Return the component (in this case a <code>JLabel</code> that is used as
     * a "rubber stamp" for drawing items in the <code>JTree</code>. The
     * renderer will consult the tree model for each node's rendering
     * information.
     *
     * @param tree       The associated <code>JTree</code> instance.
     * @param value      The object to draw (assumed to be an
     *                   <code>ITreeNode</code>).
     * @param isSelected <code>true</code> if this item is currently selected
     *                   in the tree.
     * @param hasFocus   <code>true</code> if this item currently has focus in
     *                   the tree.
     * @param expanded   <code>true</code> if this item is currently expanded in
     *                   the tree.
     * @param row        The row number for this item in the tree.
     * @param leaf       <code>true</code> if this item is a leaf.
     */

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean isSelected,
                                                  boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        if (model != null) {
            setIcon(model.getIcon(value, expanded));
            setText(model.getLabel(value));
        }

        setFont(tree.getFont());
        setOpaque(isSelected);

        if (isSelected) {
            this.setBackground(highlightBackground);
            this.setForeground(highlightForeground);
        } else {
            this.setBackground(tree.getBackground());
            this.setForeground(tree.getForeground());
        }

        return (this);
    }

    /**
     * Set the background color for a highlighted item. This method will be
     * deprecated once <code>JTree.getSelectionBackground()</code> is
     * implemented.
     *
     * @param bg The new background color.
     */

    public void setHighlightBackground(Color bg) {
        highlightBackground = bg;
    }

    /**
     * Set the foreground color for a highlighted item.  This method will be
     * deprecated once <code>JTree.getSelectionForeground()</code> is
     * implemented.
     *
     * @param fg The new foreground color.
     */

    public void setHighlightForeground(Color fg) {
        highlightForeground = fg;
    }

}
