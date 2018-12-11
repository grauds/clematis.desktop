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

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.KScrollPane;
import com.hyperrealm.kiwi.ui.model.KTreeModelTreeAdapter;

/**
 * A component for editing a hierarchy of properties. There are two types
 * of properties: grouping properties, which serve merely as containers for
 * other properties, and editable properties, which have values that can be
 * edited. The hierarchy is displayed as an expandable tree, in which editable
 * properties are underscored. Clicking on an editable property invokes a
 * property editor of the appropriate type, as provided by a property editor
 * factory.
 *
 * <p><center>
 * <img src="snapshot/PropertyEditor.gif"><br>
 * <i>An example PropertyEditor.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */
@SuppressWarnings("MagicNumber")
public class PropertyEditor extends KPanel {

    private JTree tree;

    private KTreeModelTreeAdapter adapter;

    private PropertyEditorFactory factory = DefaultPropertyEditorFactory.getInstance();

    private PropertyCellEditor cellEditor;

    private ArrayList<PropertySelectionListener> listeners = new ArrayList<PropertySelectionListener>();

    /**
     * Construct a new, empty <code>PropertyEditor</code>.
     */

    public PropertyEditor() {
        setLayout(new BorderLayout(3, 3));

        tree = new JTree();
        tree.setRootVisible(false);

        tree.setEditable(true);
        tree.setRowHeight(20);

        cellEditor = new PropertyCellEditor(factory);

        tree.setCellRenderer(cellEditor);
        tree.setCellEditor(cellEditor);

        KScrollPane sp = new KScrollPane(tree);
        add(CENTER_POSITION, sp);

        adapter = new KTreeModelTreeAdapter(tree);
        tree.setModel(adapter);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                Property prop = getSelectedProperty();

                for (PropertySelectionListener listener : listeners) {
                    listener.selectedPropertyChanged(PropertyEditor.this);
                }
            }
        });
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.setInvokesStopCellEditing(true);
    }

    /**
     * Get the property editor factory that is used by this property editor.
     *
     * @return The current <code>PropertyEditorFactory</code>.
     */

    public PropertyEditorFactory getEditorFactory() {
        return (factory);
    }

    /**
     * Set the property editor factory to be used by this property editor.
     *
     * @param factory The new <code>PropertyEditorFactory</code>.
     */

    public void setEditorFactory(PropertyEditorFactory factory) {
        cellEditor.setEditorFactory(factory);
    }

    /**
     * Set the data model for this editor.
     *
     * @param model The model.
     * @since Kiwi 2.3
     */

    public void setModel(PropertyModel model) {
        adapter.setTreeModel(model);
    }

    /**
     * Show or hide the root Property.
     *
     * @since Kiwi 2.3
     */

    public void setRootVisible(boolean flag) {
        tree.setRootVisible(flag);
    }

    /**
     * Get the currently selected Property.
     *
     * @return The selected Property, or <b>null</b> if the selection is
     * empty.
     * @since Kiwi 2.3
     */
    @SuppressWarnings("ReturnCount")
    public Property getSelectedProperty() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return (null);
        }

        Object o = path.getLastPathComponent();

        if (!(o instanceof Property)) {
            return (null);
        }

        return ((Property) o);
    }

    /**
     * Expand the given Property (if it has child Properties).
     *
     * @since Kiwi 2.3
     */

    public void expand(Property property) {
        TreePath path = adapter.getPathForNode(property);
        tree.expandPath(path);
    }

    /**
     * Collapse the given Property (if it has child Properties).
     *
     * @since Kiwi 2.3
     */

    public void collapse(Property property) {
        TreePath path = adapter.getPathForNode(property);
        tree.collapsePath(path);
    }

    /**
     * Expand all Properties in the tree.
     *
     * @since Kiwi 2.3
     */

    public void expand() {
        Property root = (Property) tree.getModel().getRoot();
        if (root != null) {
            tree.expandPath(new TreePath(root));

            for (int i = 0; i < tree.getRowCount(); ++i) {
                tree.expandRow(i);
            }
        }
    }

    /**
     * Collapse all Properties in the tree.
     *
     * @since Kiwi 2.3
     */

    public void collapse() {
        for (int i = tree.getRowCount() - 1; i > 0; --i) {
            tree.collapseRow(i);
        }
    }

    /**
     * Get the editable state of the widget.
     *
     * @since Kiwi 2.3
     */

    public boolean isEditable() {
        return (tree.isEditable());
    }

    /**
     * Set the editable state of the widget.
     *
     * @since Kiwi 2.3
     */

    public void setEditable(boolean flag) {
        tree.setEditable(flag);
    }

    /**
     * Add a PropertySelectionListener to this component's list of listeners.
     *
     * @param listener The listener.
     * @since Kiwi 2.4
     */

    public void addPropertySelectionListener(PropertySelectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a PropertySelectionListener from this component's list of
     * listeners.
     *
     * @param listener The listener.
     * @since Kiwi 2.4
     */

    public void removePropertySelectionListener(
        PropertySelectionListener listener) {
        listeners.remove(listener);
    }

}
