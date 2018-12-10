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
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.Icon;

import com.hyperrealm.kiwi.event.KTreeModelListener;
import com.hyperrealm.kiwi.event.KTreeModelSupport;
import com.hyperrealm.kiwi.util.HashCodeComparator;
import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A default implementation of <code>KTreeModel</code>.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class DefaultKTreeModel<T> implements KTreeModel<T> {

    private static final Icon FOLDER_OPEN_ICON = KiwiUtils.getResourceManager()
        .getIcon("folder_page.png");

    private static final Icon FOLDER_CLOSED_ICON = KiwiUtils.getResourceManager()
        .getIcon("folder.png");

    private static final Icon DOCUMENT_ICON = KiwiUtils.getResourceManager()
        .getIcon("document_blank.png");

    protected final KTreeModelSupport support;
    /**
     * The model's root node.
     */
    TreeNode rootNode;
    /**
     * The object-to-node map.
     */
    private final TreeMap<T, TreeNode> nodeMap;
    /**
     * Construct a new, static <code>DefaultKTreeModel</code>.
     */

    protected DefaultKTreeModel() {
        support = new KTreeModelSupport(this);
        nodeMap = new TreeMap<>(new HashCodeComparator<>());
    }

    /**
     * Create a node for an object. This method is provided for
     * subclasses which maintain dynamic tree data.
     */

    TreeNode makeNode(T object, TreeNode parent) {
        TreeNode node = new TreeNode(object, parent);
        nodeMap.put(object, node);

        return (node);
    }

    /**
     * Destroy a node, and recursively destroy all its descendants. This method
     * is provided for subclasses which maintain dynamic tree data.
     */

    private void destroyNode(TreeNode node) {
        Iterator<TreeNode> iter = node.getChildren();
        if (iter != null) {
            while (iter.hasNext()) {
                destroyNode(iter.next());
            }
        }

        nodeMap.remove(node.getObject());
    }

    /*
     */

    public synchronized T getRoot() {
        return (rootNode == null ? null : rootNode.getObject());
    }

    /*
     */

    public synchronized void setRoot(T root) {
        nodeMap.clear();

        rootNode = makeNode(root, null);
        support.fireDataChanged();
    }

    /*
     */

    public synchronized int getChildCount(T node) {
        TreeNode n = nodeForObject(node);
        if (n == null) {
            return (0);
        }

        return (n.getChildCount());
    }

    /*
     */

    public synchronized T getChild(T parent, int index) {
        TreeNode p = nodeForObject(parent);
        if (p == null) {
            return (null);
        }

        TreeNode child = p.getChild(index);
        return child == null ? null : child.getObject();
    }

    /*
     */

    public synchronized Iterator<T> getChildren(T parent) {
        TreeNode p = nodeForObject(parent);
        if (p == null) {
            return (null);
        }

        Iterator<TreeNode> iter = p.getChildren();
        ArrayList<T> list = new ArrayList<T>();

        if (iter != null) {
            while (iter.hasNext()) {
                TreeNode node = iter.next();
                list.add(node.getObject());
            }
        }

        return (list.iterator());
    }

    /*
     */

    public synchronized int getIndexOfChild(T parent, T node) {
        TreeNode p = nodeForObject(parent);
        if (p == null) {
            return (-1);
        }

        TreeNode child = nodeForObject(node);

        return ((child == null) ? -1 : p.getIndexOfChild(child));
    }

    /*
     */

    public synchronized void removeChildren(T parent) {
        TreeNode p = nodeForObject(parent);
        if (p != null) {
            p.removeChildren();
        }
    }

    /*
     */

    public synchronized void removeChild(T parent, int index) {
        TreeNode p = nodeForObject(parent);
        if (p != null) {
            p.removeChild(index);
        }
    }

    /*
     */

    public synchronized void addChild(T parent, T node) {
        TreeNode p = nodeForObject(parent);
        if (p != null) {
            p.addChild(makeNode(node, p));
        }
    }

    /*
     */

    public synchronized void addChild(T parent, T node, int index) {
        TreeNode p = nodeForObject(parent);
        if (p != null) {
            p.addChild(makeNode(node, p), index);
        }
    }

    /*
     */

    public synchronized T getParent(T node) {
        TreeNode n = nodeForObject(node);
        if (n == null) {
            return (null);
        }

        TreeNode p = n.getParent();

        return ((p == null) ? null : p.getObject());
    }

    /*
     */

    public synchronized boolean isExpandable(T node) {
        TreeNode n = nodeForObject(node);

        return ((n != null) && n.isExpandable());
    }

    /**
     * May be overridden by subclasses to provide custom icons.
     */

    public synchronized Icon getIcon(T node, boolean isExpanded) {
        if (isExpandable(node)) {
            return (isExpanded ? FOLDER_OPEN_ICON : FOLDER_CLOSED_ICON);
        } else {
            return (DOCUMENT_ICON);
        }
    }

    /**
     * May be overridden by subclasses to provide custom labels.
     */

    public synchronized String getLabel(T node) {
        return (node.toString());
    }

    /**
     * @since Kiwi 2.3
     */

    public int getFieldCount() {
        return (1);
    }

    /**
     * @since Kiwi 2.3
     */

    public String getFieldLabel(int index) {
        return (null);
    }

    /**
     * @since Kiwi 2.3
     */

    public Class getFieldType(int index) {
        return (Object.class);
    }

    /**
     * @since Kiwi 2.3
     */

    public Object getField(T node, int index) {
        return (node);
    }

    /**
     * Remove a node (other than the root node) from the model.
     *
     * @param node The node to remove.
     * @since Kiwi 2.3
     */

    public synchronized void removeNode(T node) {
        T parent = getParent(node);
        if (parent != null) {
            int index = getIndexOfChild(parent, node);
            if (index >= 0) {
                removeChild(parent, index);
            }
        }
    }

    /*
     */

    public synchronized void updateNode(T node) {
        TreeNode n = nodeForObject(node);

        if (n == null) {
            return;
        }

        TreeNode parent = n.getParent();
        if (parent != null) {
            int index = parent.getIndexOfChild(n);

            if (index >= 0) {
                support.fireNodeChanged(parent.getObject(), index);
            }
        }

    }

    /*
     */

    public synchronized void updateChildren(T parent) {
        TreeNode p = nodeForObject(parent);
        if (p != null) {
            support.fireNodeStructureChanged(parent);
        }
    }

    /*
     */

    public synchronized void releaseChildren(T parent) {
        /* no-op */
    }

    /**
     * Add a <code>TreeModelListener</code> to this model's list of listeners.
     *
     * @param listener The listener to be added.
     * @see #removeTreeModelListener
     */

    public void addTreeModelListener(KTreeModelListener listener) {
        support.addTreeModelListener(listener);
    }

    /**
     * Remove a <code>TreeModelListener</code> from this model's list of
     * listeners.
     *
     * @param listener The listener to remove.
     * @see #addTreeModelListener
     */

    public void removeTreeModelListener(KTreeModelListener listener) {
        support.removeTreeModelListener(listener);
    }

    /**
     * Look up a node for a given object.
     */

    TreeNode nodeForObject(T item) {
        return nodeMap.get(item);
    }

    /*
     */

    public void preloadChildren(T item) {
        TreeNode node = nodeMap.get(item);
        if (node != null) {
            if (!node.hasChildrenLoaded()) {
                loadChildren(node);
            }
        }
    }

    /**
     * Load the children for a given node. This method is provided for
     * subclasses which obtain tree data from an external source. The default
     * implementation is a no-op.
     *
     * @param node The parent node.
     */

    protected void loadChildren(TreeNode node) {
        /* in this case, a no-op */
    }

    /**
     * Internal wrapper object for nodes.
     */

    protected class TreeNode {

        private T object;

        private TreeNode parent;

        private ArrayList<TreeNode> children = null; // null if not yet loaded

        private boolean expandable = false;

        private int childCount = 0;

        /* construct a new tree node */

        TreeNode(T object, TreeNode parent) {
            this.parent = parent;
            this.object = object;
        }

        /* get the index of the given child in the list of children */

        int getIndexOfChild(TreeNode child) {
            if (children == null) {
                return (-1);
            }

            return (children.indexOf(child));
        }

        /* add a new child to the list of children */

        void addChild(TreeNode child) {
            if (children == null) {
                children = new ArrayList<>();
            }

            int ct = children.size();
            children.add(child);
            support.fireNodeAdded(object, ct);
        }

        /* insert a given child at the given position in the list of children */

        void addChild(TreeNode child, int index) {
            if (children == null) {
                children = new ArrayList<>();
            }

            int indexInt = index;

            if (indexInt < 0) {
                indexInt = 0;
            } else if (indexInt > children.size()) {
                indexInt = children.size();
            }

            children.add(indexInt, child);
            support.fireNodeAdded(object, indexInt);
        }

        /* replace the children with a new list of children */

        void removeChild(TreeNode child) {
            int index = children.indexOf(child);

            if (index >= 0 && index < children.size()) {
                children.remove(index);
                destroyNode(child);
                childCount--;
                support.fireNodeRemoved(object, index);
            }
        }

        /* remove a child from the list of children */

        void removeChildren() {
            if (children == null) {
                return;
            }

            int ct = children.size();
            if (ct > 0) {
                for (TreeNode child : children) {
                    destroyNode(child);
                }

                children.clear();
                childCount = 0;
                support.fireNodesRemoved(object, 0, --ct);
            }
        }

        /* */

        void removeChild(int index) {

            TreeNode node = children.get(index);
            destroyNode(node);
            children.remove(index);
            childCount--;

            support.fireNodeRemoved(object, index);
        }

        /* remove the child at the given position in the list of children */

        void releaseChildren() {
            if (children == null) {
                return;
            }

            for (TreeNode child : children) {
                destroyNode(child);
            }

            children = null;
        }

        /* drop the children */

        T getObject() {
            return (object);
        }

        /* get the user object for this node */

        public boolean isExpandable() {
            return (expandable);
        }

        /* set the expandable flag */

        void setExpandable(boolean flag) {
            expandable = flag;
        }

        /* determine if this node is expandable */

        public TreeNode getParent() {
            return (parent);
        }

        /* get the parent of this node */

        int getChildCount() {
            // We need to remember the (most recent) child count even after
            // the children are dropped; otherwise we'd report child count
            // of 0 and the tree would render the parent without an expander
            // control.

            if (children != null) {
                childCount = children.size();
            }

            return (childCount);
        }

        /* get a child count for the node */

        TreeNode getChild(int index) {
            return ((children == null) ? null : children.get(index));
        }

        /* get the child at a given index */

        Iterator<TreeNode> getChildren() {
            return ((children == null) ? null : children.iterator());
        }

        /* get an iterator to the children */

        void setChildren(ArrayList<TreeNode> children) {
            this.children = children;
            support.fireNodeStructureChanged(object);
        }

        /* are children loaded? */

        boolean hasChildrenLoaded() {
            return (children != null);
        }

    }
}
