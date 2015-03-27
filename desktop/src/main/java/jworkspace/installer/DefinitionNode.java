package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Authors may be contacted at:

   frenzy@ix.netcom.com
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import com.hyperrealm.kiwi.event.KTreeModelListener;
import com.hyperrealm.kiwi.event.KTreeModelSupport;
import com.hyperrealm.kiwi.util.DomainObject;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

/**
 * Definition node is a base class for
 * application, library and jvm definition nodes.
 * It provides data with hierarchical support,
 * which includes adding, deleting and editing of node,
 * plus finding parents and children of given node.
 */
public class DefinitionNode extends DomainObject
{
    protected File file;
    private static final Icon openIcon, closedIcon, lockedIcon, rootIcon;
    protected KTreeModelSupport hsupport;
    protected DefinitionNode parent = null;
    protected int index = 0;

    static
    {
        ResourceManager rm = KiwiUtils.getResourceManager();
        openIcon = rm.getIcon("folder-open.gif");
        closedIcon = rm.getIcon("folder-closed.gif");
        lockedIcon = rm.getIcon("folder-locked.gif");
        rootIcon = rm.getIcon("folder-closed.gif");
    }

    /**
     * Public constructor of definition node.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file to hold node data java.io.File
     */
    public DefinitionNode(DefinitionNode parent, File file)
    {
        this.parent = parent;
        this.file = file;
        hsupport = new KTreeModelSupport(this);
    }

    /**
     * Public constructor of definition node.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param filename of file to hold node data java.lang.String
     */
    public DefinitionNode(DefinitionNode parent, String filename)
    {
        this(parent, new File(parent.getFile(), filename));
    }

    /**
     * Add child node
     * @param name of child node java.lang.String
     * @param index is a number in array of children int
     */
    public void add(String name, int index)
    {
        DefinitionNode newNode = new DefinitionNode(this, name);
        newNode.getFile().mkdir();
        add(newNode);
    }

    /**
     * Add child node
     * @param node is a number in array of children int
     */
    public void add(DefinitionNode node)
    {
        hsupport.fireNodeAdded(node, 0);
    }

    /* The mandatory listener methods; the Kiwi tree framework locates these
     * through introspection. For each domain object in the hierarchy, a Kiwi
     * tree model maintains a corresponding TreeNode object (which is used
     * internally and is not directly accessible). Each TreeNode is a
     * HierarchicalAssociationListener for its corresponding domain object.
     * As TreeNodes are added to and dropped from the tree model, they are
     * attached and detached from their domain objects, respectively.
     * Make sure that these methods are public and that the class itself is
     * public; otherwise the introspection will silently fail.
     */
    public void addHierarchicalAssociationListener(KTreeModelListener listener)
    {
        hsupport.addTreeModelListener(listener);
    }

    /**
     * Delete this node if it not write protected or
     * locked.
     */
    public void delete() throws IOException
    {
        if (file.canRead())
            if (file.delete())
            {
                hsupport.fireNodeRemoved(getParent(), getIndex());
                return;
            }
        throw (new IOException("The folder is locked or non-empty."));
    }

    /**
     * Returns closed icon to represent
     * node in tree control.
     */
    public Icon getClosedIcon()
    {
        if (isRoot())
            return (rootIcon);
        return (isExpandable() ? closedIcon : lockedIcon);
    }

    /**
     * Returns file which is encapsulated by this node
     */
    public File getFile()
    {
        return (file);
    }

    /**
     * Returns a number of this node in array of children
     * nodes.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Returns full link string to this node.
     */
    public String getLinkString()
    {
        // trace our heritage, and create a path.

        Stack stack = new Stack();
        DefinitionNode currentNode = this;
        while (currentNode != null)
        {
            stack.push(currentNode);
            currentNode = currentNode.getParent();
        }
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        while (!stack.isEmpty())
        {
            if (!first)
                sb.append('/');
            sb.append(((DefinitionNode) stack.pop()).getNodeName());
            first = false;
        }
        return (sb.toString());
    }

    /**
     * Returns full link path to this node.
     *
     * @return path of definition nodes.
     */
    public TreePath getLinkPath()
    {
        // trace our heritage, and create a path.
        Stack stack = new Stack();
        DefinitionNode currentNode = this;
        while (currentNode != null)
        {
            stack.push(currentNode);
            currentNode = currentNode.getParent();
        }
        TreePath path = new TreePath(stack.pop());
        while (!stack.isEmpty())
        {
            path = path.pathByAddingChild(stack.pop());
        }
        return (path);
    }

    /**
     * Returns node name
     */
    public String getNodeName()
    {
        String nm = file.getName();
        if (nm.endsWith(WorkspaceInstaller.FILE_EXTENSION))
            nm = nm.substring(0, nm.length() - 4);

        return (nm);
    }

    /**
     * Returns open icon to represent
     * application in tree control.
     */
    public Icon getOpenIcon()
    {
        if (isRoot())
            return (rootIcon);
        return (isExpandable() ? openIcon : lockedIcon);
    }

    /**
     * Returns parent node for this node if any.
     */
    public DefinitionNode getParent()
    {
        return (parent);
    }

    /**
     * Returns whether if this node is expandable
     */
    public boolean isExpandable()
    {
        return (file.canRead());
    }

    /**
     * Returns whether if this node is root
     */
    public boolean isRoot()
    {
        return (parent == null);
    }

    /**
     * This class does not hold any data,
     * thus it does not load itself from disk.
     */
    public void load() throws IOException
    {
    }

    /**
     * Remove hierarchical listener.
     */
    public void removeHierarchicalAssociationListener(KTreeModelListener listener)
    {
        hsupport.removeTreeModelListener(listener);
    }

    /**
     * This class does not hold any data,
     * thus it does not save itself to disk.
     */
    public void save() throws IOException
    {
    }
}