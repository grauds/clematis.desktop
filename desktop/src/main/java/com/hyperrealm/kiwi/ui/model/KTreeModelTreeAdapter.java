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

package com.hyperrealm.kiwi.ui.model;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.hyperrealm.kiwi.event.KTreeModelEvent;
import com.hyperrealm.kiwi.event.KTreeModelListener;

/**
 * An adapter that allows a <code>KTreeModel</code> to be used with a Swing
 * <code>JTree</code> component.
 * The adapter translates messages and events between the <code>JTree</code>
 * and the <code>ITreeModel</code> implementation.
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Mark Lindner
 * @see javax.swing.JTree
 * @see com.hyperrealm.kiwi.ui.model.KTreeModel
 * @since Kiwi 2.0
 */

public class KTreeModelTreeAdapter implements TreeModel, KTreeModelListener {

    private static final int[] EMPTY_INDEX_LIST = new int[0];

    private static final Object[] EMPTY_OBJECT_LIST = new Object[0];

    private KTreeModel model = null;

    private final ArrayList<TreeModelListener> listeners;

    private JTree jtree;

    private TreeWillExpandListener treeWillExpandListener;

    private TreeExpansionListener treeExpansionListener;

    /**
     * Construct a new tree model tree adapter. Creates a new adapter for the
     * specified JFC tree component.
     *
     * @param jtree The JFC tree component to be associated with this adapter.
     */

    public KTreeModelTreeAdapter(JTree jtree) {

        this.jtree = jtree;

        jtree.setShowsRootHandles(true);

        listeners = new ArrayList<>();

        treeWillExpandListener = new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent evt) {
                Object node = evt.getPath().getLastPathComponent();

                if (model != null) {
                    model.preloadChildren(node);
                }
            }

            public void treeWillCollapse(TreeExpansionEvent evt) {
            }
        };

        jtree.addTreeWillExpandListener(treeWillExpandListener);

        treeExpansionListener = new TreeExpansionListener() {

            public void treeExpanded(TreeExpansionEvent evt) {
            }

            public void treeCollapsed(TreeExpansionEvent evt) {
                Object node = evt.getPath().getLastPathComponent();

                if (model != null) {
                    model.releaseChildren(node);
                }
            }
        };

        jtree.addTreeExpansionListener(treeExpansionListener);
    }

    /**
     * Get the tree model in use by this adapter. Returns the tree model
     * currently associated with this adapter (may be <code>null</code> if no
     * model has been set).
     *
     * @see #setTreeModel
     */

    public KTreeModel getTreeModel() {
        return (model);
    }

    /**
     * Set the tree model to be used by this adapter. The adapter adds itself
     * as a listener of the tree model. If a model was already set prior to this
     * call, it is replaced, and the adapter removes itself as a listener from
     * that model.
     *
     * @param model The model to set.
     * @see #getTreeModel
     */

    public void setTreeModel(KTreeModel model) {
        if (this.model != null) {
            this.model.removeTreeModelListener(this);
        }

        this.model = model;
        model.addTreeModelListener(this);

//    model.preloadChildren(model.getRoot()); // should this be here?

        fireTreeStructureChanged(model.getRoot());
    }

    /**
     * Add a tree model listener. Adds a <code>TreeModelListener</code> to this
     * adapter's list of tree model listeners.
     *
     * @param listener The listener to add.
     * @see #removeTreeModelListener
     */

    public void addTreeModelListener(TreeModelListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a tree model listener. Removes a <code>TreeModelListener</code>
     * from this adapter's list of tree model listeners.
     *
     * @param listener The listener to remove.
     * @see #addTreeModelListener
     */

    public void removeTreeModelListener(TreeModelListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Invoked when nodes are added to the model.
     */

    public void nodesAdded(KTreeModelEvent evt) {
        fireTreeNodesInserted(evt.getNode(), evt.getStartIndex(),
            evt.getEndIndex());
    }

    /**
     * Invoked when nodes are removed from the model.
     */

    public void nodesRemoved(KTreeModelEvent evt) {
        fireTreeNodesRemoved(evt.getNode(), evt.getStartIndex(),
            evt.getEndIndex());
    }

    /**
     * Invoked when nodes are changed in the model.
     */

    public void nodesChanged(KTreeModelEvent evt) {
        fireTreeNodesChanged(evt.getNode(), evt.getStartIndex(),
            evt.getEndIndex());
    }

    /**
     * Invoked when the structure of a portion of the model is changed.
     */

    public void structureChanged(KTreeModelEvent evt) {
        fireTreeStructureChanged(evt.getNode());
    }

    /**
     * Invoked when the structure of the entire model has changed.
     */

    public void dataChanged(KTreeModelEvent evt) {
        fireTreeStructureChanged(null);
    }

    /**
     * Get the child of a node. (Implementation of <code>TreeModel</code>.)
     */

    public Object getChild(Object parent, int index) {
        if (model == null) {
            return (null);
        }

        return (model.getChild(parent, index));
    }

    /**
     * Get the child count of a node. (Implementation of
     * <code>TreeModel</code>.)
     */

    public int getChildCount(Object parent) {
        if (model == null) {
            return (0);
        }

        return (model.getChildCount(parent));
    }

    /**
     * Determine if a node is a leaf. (Implementation of
     * <code>TreeModel</code>.)
     */

    public boolean isLeaf(Object node) {
        if (model == null) {
            return (true);
        }

        return (!model.isExpandable(node));
    }

    /**
     * Handle <code>JTree</code> events. (Implementation of
     * <code>TreeModel</code>.)
     */

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Get the index of a child within its parent. (Implementation of
     * <code>TreeModel</code>.)
     */

    public int getIndexOfChild(Object parent, Object child) {
        if (model == null) {
            return (-1);
        }

        return (model.getIndexOfChild(parent, child));
    }

    /**
     * Get the root of the tree. (Implementation of <code>TreeModel</code>.)
     */

    public Object getRoot() {
        if (model == null) {
            return (null);
        }

        return (model.getRoot());
    }

    /**
     * Compute a <code>TreePath</code> for a given node.
     *
     * @param node The node.
     * @return A <code>TreePath</code> to the given node.
     * @see javax.swing.tree.TreePath
     */

    public TreePath getPathForNode(Object node) {
        ArrayList<Object> v = new ArrayList<Object>();
        Object n = node;

        if (n != null) {
            v.add(0, n);
            while ((n = model.getParent(n)) != null) {
                v.add(0, n);
            }
        }

        Object[] path = new Object[v.size()];
        v.toArray(path);

        return (new TreePath(path));
    }

    /**
     * Fire a <i>tree nodes inserted</i> event. This event notifies the
     * <code>JTree</code> that new nodes have been added to the tree data model.
     *
     * @param parent     The parent of the new nodes.
     * @param startIndex The offset of the first node in the range of nodes that
     *                   were added.
     * @param endIndex   The offset of the last node in the range of nodes that
     *                   were added.
     */

    protected void fireTreeNodesInserted(Object parent, int startIndex,
                                         int endIndex) {
        synchronized (listeners) {
            for (TreeModelListener listener : listeners) {
                listener.treeNodesInserted(getTreeModelEvent(parent, startIndex, endIndex));
            }
        }
    }

    /**
     * Fire a <i>tree nodes removed</i> event. This event notifies the
     * <code>JTree</code> that a node has been removed from the tree data model.
     *
     * @param parent     The parent of the nodes that were removed.
     * @param startIndex The offset of the first node in the range of nodes that
     *                   were removed.
     * @param endIndex   The offset of the last node in the range of nodes that
     *                   were removed.
     */

    protected void fireTreeNodesRemoved(Object parent, int startIndex,
                                        int endIndex) {
        TreeModelEvent evt = null;

        synchronized (listeners) {
            for (TreeModelListener listener : listeners) {
                if (evt == null) {
                    int[] indices = EMPTY_INDEX_LIST;

                    if ((startIndex >= 0) && (endIndex >= 0)) {
                        int len = endIndex - startIndex + 1;
                        indices = new int[len];

                        for (int i = 0, pos = startIndex; i < len; i++, pos++) {
                            indices[i] = pos;
                        }
                    }

                    evt = new TreeModelEvent(this, getPathForNode(parent), indices,
                        null);
                }
                listener.treeNodesRemoved(evt);
            }
        }
    }

    /**
     * Fire a <i>tree nodes changed</i> event. This event notifies the
     * <code>JTree</code> that a node has changed in some way.
     *
     * @param parent     The parent of the nodes that changed.
     * @param startIndex The offset of the first node in the range of nodes that
     *                   changed.
     * @param endIndex   The offset of the last node in the range of nodes that
     *                   changed.
     */

    protected void fireTreeNodesChanged(Object parent, int startIndex,
                                        int endIndex) {


        synchronized (listeners) {
            for (TreeModelListener listener : listeners) {
                listener.treeNodesChanged(getTreeModelEvent(parent, startIndex, endIndex));
            }
        }
    }

    private TreeModelEvent getTreeModelEvent(Object parent, int startIndex, int endIndex) {

        int[] indices = EMPTY_INDEX_LIST;
        Object[] objects = EMPTY_OBJECT_LIST;

        if ((startIndex >= 0) && (endIndex >= 0)) {
            int len = endIndex - startIndex + 1;
            indices = new int[len];
            objects = new Object[len];

            for (int i = 0, pos = startIndex; i < len; i++, pos++) {
                indices[i] = pos;
                objects[i] = model.getChild(parent, pos);
            }
        }

        return new TreeModelEvent(this, getPathForNode(parent), indices, objects);
    }

    /**
     * Fire a <i>tree structure changed</i> event. This event notifies the
     * <code>JTree</code> that the subtree rooted at a given node has changed in
     * some major way.
     *
     * @param node The node that is the root of the subtree that changed.
     */

    protected void fireTreeStructureChanged(Object node) {
        TreeModelEvent evt = null;

        synchronized (listeners) {
            for (TreeModelListener listener : listeners) {
                if (evt == null) {
                    evt = new TreeModelEvent(this, (node == null) ? null
                        : getPathForNode(node));
                }

                listener.treeStructureChanged(evt);
            }
        }
    }

    /**
     * Dispose of the adapter. Causes the adapter to detach its listeners from
     * its associated <code>JTree</code> component, and then null out its
     * references to the <code>JTree</code> and to the associated
     * <code>KTreeModel</code>.
     */

    public void dispose() {
        if (treeWillExpandListener != null) {
            jtree.removeTreeWillExpandListener(treeWillExpandListener);
        }
        if (treeExpansionListener != null) {
            jtree.removeTreeExpansionListener(treeExpansionListener);
        }
        treeWillExpandListener = null;
        treeExpansionListener = null;

        if (model != null) {
            model.removeTreeModelListener(this);
        }

        model = null;
        jtree = null;
    }

}
