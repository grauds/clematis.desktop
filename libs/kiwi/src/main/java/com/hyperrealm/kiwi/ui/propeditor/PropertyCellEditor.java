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

package com.hyperrealm.kiwi.ui.propeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.WEST_POSITION;

import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;

/*
 *
 * @author Mark Lindner
 */

class PropertyCellEditor implements TreeCellEditor, TreeCellRenderer,
    ActionListener {

    private static Color highlightBackground
        = UIManager.getColor("Tree.selectionBackground");

    private static Color highlightForeground
        = UIManager.getColor("Tree.selectionForeground");

    private EventListenerList listenerList = new EventListenerList();

    private transient ChangeEvent changeEvent = null;

    private Border border, noborder;

    private KLabel label, editorLabel;

    private PropertyValueEditor editor = null;

    private KPanel editorPanel;

    private PropertyValueEditor fixedEditor = new FixedValueEditor();

    private HashMap<PropertyType, PropertyValueEditor> editors;

    private PropertyEditorFactory factory;

    /*
     */

    PropertyCellEditor(PropertyEditorFactory factory) {
        this.factory = factory;

        editorPanel = new KPanel();
        editorPanel.setLayout(DEFAULT_BORDER_LAYOUT);

        border = new UnderlineBorder();
        noborder = new EmptyBorder(0, 0, 1, 0);

        editorLabel = new KLabel();
        editorLabel.setBorder(noborder);
        MouseListener mouseListener = new MouseListener();
        editorLabel.addMouseListener(mouseListener);
        editorPanel.add(WEST_POSITION, editorLabel);

        editors = new HashMap<PropertyType, PropertyValueEditor>();

        label = new KLabel();
        label.setBorder(noborder);
    }

    /*
     */

    void setEditorFactory(PropertyEditorFactory factory) {
        editors.clear();
        this.factory = factory;
    }

    /*
     */

    private PropertyValueEditor getEditor(PropertyType type) {
        PropertyValueEditor ed = editors.get(type);
        if (ed == null) {
            ed = factory.createEditor(type);
            if (ed != null) {
                editors.put(type, ed);
            }
        }

        return (ed);
    }

    /*
     */

    public synchronized Component getTreeCellEditorComponent(JTree tree,
                                                             Object value,
                                                             boolean selected,
                                                             boolean expanded,
                                                             boolean leaf,
                                                             int row) {
        JComponent c;
        Property prop = null;

        if (editor != null) {
            c = editor.getEditorComponent();
            editor.removeActionListener(this);
            editorPanel.remove(c);
        }

        editor = null;

        if (value instanceof Property) {
            prop = (Property) value;

            PropertyType type = prop.getType();

            if (type != null && prop.isEditable()) {
                editor = getEditor(type);
            }

            if (editor == null) {
                editor = fixedEditor;
            }

            editor.setProperty(prop);
            editorLabel.setIcon(prop.getIcon());
            editorLabel.setText(prop.getName() + (prop.isEditable() ? ":" : ""));
        }

        // do we actually have an editor after all those checks?

        if (editor != null) {
            c = editor.getEditorComponent();
            editor.addActionListener(this);
            editorPanel.add(CENTER_POSITION, c);
//      c.setBorder(noborder);
            editor.startFocus();
        }

        //selected = true;
        editorPanel.setOpaque(selected);

        if (selected) {
            editorPanel.setBackground(highlightBackground);
            editorPanel.setForeground(highlightForeground);
        } else {
            editorPanel.setBackground(tree.getBackground());
            editorPanel.setForeground(tree.getForeground());
        }

        return (editorPanel);
    }

    /*
     */

    public synchronized Component getTreeCellRendererComponent(JTree tree,
                                                               Object value,
                                                               boolean selected,
                                                               boolean expanded,
                                                               boolean leaf,
                                                               int row,
                                                               boolean hasFocus) {
        if (value instanceof Property) {
            Property prop = (Property) value;

            label.setIcon(prop.getIcon());
            label.setText(prop.toString());
            label.setBorder(prop.isEditable() ? border : noborder);
        } else {
            label.setText("?");
            label.setIcon(null);
            label.setBorder(null);
        }

        label.setOpaque(selected);

        if (selected) {
            label.setBackground(highlightBackground);
            label.setForeground(highlightForeground);
        } else {
            label.setBackground(tree.getBackground());
            label.setForeground(tree.getForeground());
        }

        label.updateUI();

        return (label);
    }

    /*
     */

    public boolean isCellEditable(EventObject evt) {
        return (true);
    }

    /*
     */

    public boolean shouldSelectCell(EventObject evt) {
        if (editor != null) {
            editor.startFocus();
        }

        return (true);
    }

    /*
     */

    public void addCellEditorListener(CellEditorListener listener) {
        listenerList.add(CellEditorListener.class, listener);
    }

    /*
     */

    public void removeCellEditorListener(CellEditorListener listener) {
        listenerList.remove(CellEditorListener.class, listener);
    }

    /*
     */

    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array

        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    /*
     */

    protected void fireEditingCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
            }
        }
    }

    /*
     */

    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    /*
     */

    public boolean stopCellEditing() {
        if (editor == null) {
            return (true);
        }

        boolean ok = editor.validateInput();

        if (ok) {
            editor.commitInput();
            fireEditingStopped();
        }

        return (ok);
    }

    /*
     */

    public Object getCellEditorValue() {
        return ((editor == null) ? null : editor.getProperty().getValue());
    }

    /*
     */

    public void actionPerformed(ActionEvent evt) {
        stopCellEditing();
    }

    /*
     */

    private class UnderlineBorder extends AbstractBorder {
        private Insets insets = new Insets(0, 0, 1, 0);

        UnderlineBorder() {
            super();
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Color oldColor = g.getColor();

            g.setColor(Color.gray);
            g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

            g.setColor(oldColor);
        }

        public Insets getBorderInsets() {
            return (insets);
        }

        public Insets getBorderInsets(Component c, Insets insets) {

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
            insets.bottom = 1;

            return (insets);
        }
    }

    /*
     */

    private class MouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (editor != null) {
                editor.startFocus();
            }
        }
    }
}
