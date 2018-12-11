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

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import com.hyperrealm.kiwi.event.KTreeModelListener;
import com.hyperrealm.kiwi.event.KTreeModelSupport;
import com.hyperrealm.kiwi.util.DomainObject;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * Definition node is a base class for application, library and jvm definition nodes.
 * It provides data with hierarchical support, which includes adding, deleting and editing of node,
 * plus finding parents and children of given node.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
public class DefinitionNode extends DomainObject {

    private static final Icon OPEN_ICON, CLOSED_ICON, LOCKED_ICON, ROOT_ICON;

    private static final String FOLDER_CLOSED_GIF = "folder-closed.gif";

    private static final String FOLDER_OPEN_GIF = "folder-open.gif";

    private static final String FOLDER_LOCKED_GIF = "folder-locked.gif";

    static {
        ResourceManager rm = KiwiUtils.getResourceManager();
        OPEN_ICON = rm.getIcon(FOLDER_OPEN_GIF);
        CLOSED_ICON = rm.getIcon(FOLDER_CLOSED_GIF);
        LOCKED_ICON = rm.getIcon(FOLDER_LOCKED_GIF);
        ROOT_ICON = rm.getIcon(FOLDER_CLOSED_GIF);
    }

    protected File file;

    protected DefinitionNode parent;

    private KTreeModelSupport hsupport;

    /**
     * Public constructor of definition node.
     *
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file   to hold node data java.io.File
     */
    public DefinitionNode(DefinitionNode parent, File file) {
        this.parent = parent;
        this.file = file;
        hsupport = new KTreeModelSupport(this);
    }

    /**
     * Public constructor of definition node.
     *
     * @param parent   node jworkspace.installer.DefinitionNode
     * @param filename of file to hold node data java.lang.String
     */
    public DefinitionNode(DefinitionNode parent, String filename) {
        this(parent, new File(parent.getFile(), filename));
    }

    /**
     * Add child node
     *
     * @param name  of child node java.lang.String
     * @param index is a number in array of children int
     */
    public void add(String name, int index) {
        DefinitionNode newNode = new DefinitionNode(this, name);
        newNode.getFile().mkdirs();
        add(newNode);
    }

    /**
     * Add child node
     *
     * @param node is a number in array of children int
     */
    public void add(DefinitionNode node) {
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
    public void addHierarchicalAssociationListener(KTreeModelListener listener) {
        hsupport.addTreeModelListener(listener);
    }

    /**
     * Delete this node if it not write protected or
     * locked.
     */
    public void delete() throws IOException {
        if (file.canRead()) {
            if (file.delete()) {
                hsupport.fireNodeRemoved(getParent(), getIndex());
                return;
            }
        }
        throw new IOException("The folder is locked or non-empty.");
    }

    /**
     * Returns closed ICON to represent
     * node in tree control.
     */
    public Icon getClosedIcon() {
        if (isRoot()) {
            return ROOT_ICON;
        }
        return isExpandable() ? CLOSED_ICON : LOCKED_ICON;
    }

    /**
     * Returns file which is encapsulated by this node
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns a number of this node in array of children
     * nodes.
     */
    public int getIndex() {
        return 0;
    }

    /**
     * Returns full link string to this node.
     */
    public String getLinkString() {

        // trace our heritage, and create a path.
        Stack<DefinitionNode> stack = getNodePath();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (!stack.isEmpty()) {
            if (!first) {
                sb.append('/');
            }
            sb.append(stack.pop().getNodeName());
            first = false;
        }
        return sb.toString();
    }

    /**
     * Returns full link path to this node.
     *
     * @return path of definition nodes.
     */
    public TreePath getLinkPath() {

        // trace our heritage, and create a path.
        Stack<DefinitionNode> stack = getNodePath();
        TreePath path = new TreePath(stack.pop());
        while (!stack.isEmpty()) {
            path = path.pathByAddingChild(stack.pop());
        }
        return path;
    }

    private Stack<DefinitionNode> getNodePath() {

        Stack<DefinitionNode> stack = new Stack<>();
        DefinitionNode currentNode = this;
        while (currentNode != null) {
            stack.push(currentNode);
            currentNode = currentNode.getParent();
        }
        return stack;
    }

    /**
     * Returns node name
     */
    public String getNodeName() {
        String nm = file.getName();
        if (nm.endsWith(WorkspaceInstaller.FILE_EXTENSION)) {
            nm = nm.substring(0, nm.length() - WorkspaceInstaller.FILE_EXTENSION.length());
        }
        return nm;
    }

    /**
     * Returns open ICON to represent
     * application in tree control.
     */
    public Icon getOpenIcon() {
        if (isRoot()) {
            return ROOT_ICON;
        }
        return isExpandable() ? OPEN_ICON : LOCKED_ICON;
    }

    /**
     * Returns parent node for this node if any.
     */
    public DefinitionNode getParent() {
        return parent;
    }

    /**
     * Returns whether if this node is expandable
     */
    public boolean isExpandable() {
        return file.canRead();
    }

    /**
     * Returns whether if this node is root
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * This class does not hold any data,
     * thus it does not load itself from disk.
     */
    public void load() throws IOException {
    }

    /**
     * Remove hierarchical listener.
     */
    public void removeHierarchicalAssociationListener(KTreeModelListener listener) {
        hsupport.removeTreeModelListener(listener);
    }

    /**
     * This class does not hold any data,
     * thus it does not save itself to disk.
     */
    public void save() throws IOException {
    }
}