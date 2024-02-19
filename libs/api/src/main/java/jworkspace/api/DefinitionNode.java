package jworkspace.api;

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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.event.tree.KTreeModelListener;
import com.hyperrealm.kiwi.event.tree.KTreeModelSupport;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Definition node is a base class for a node within a tree like structure of nodes, backed up with files.
 * It provides hierarchical listener support, which includes adding, deleting and editing of node,
 * plus finding parents and children.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class DefinitionNode {

    public static final Icon OPEN_ICON, CLOSED_ICON, LOCKED_ICON, ROOT_ICON, LEAF_ICON;

    private static final Logger LOG = LoggerFactory.getLogger(DefinitionNode.class);

    private static final String FILE_EXTENSION = ".cfg";

    private static final String FOLDER_CLOSED_GIF = "folder-closed.gif";

    private static final String FOLDER_OPEN_GIF = "folder-open.gif";

    private static final String FOLDER_LOCKED_GIF = "folder-locked.gif";

    private static final String LEAF_GIF = "document.png";

    static {
        ResourceManager rm = KiwiUtils.getResourceManager();
        OPEN_ICON = rm.getIcon(FOLDER_OPEN_GIF);
        CLOSED_ICON = rm.getIcon(FOLDER_CLOSED_GIF);
        LOCKED_ICON = rm.getIcon(FOLDER_LOCKED_GIF);
        ROOT_ICON = rm.getIcon(FOLDER_CLOSED_GIF);
        LEAF_ICON = rm.getIcon(LEAF_GIF);
    }

    @EqualsAndHashCode.Exclude
    protected File file;

    private DefinitionNode parent;

    @EqualsAndHashCode.Exclude
    private List<DefinitionNode> children = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    private KTreeModelSupport hsupport;

    public DefinitionNode() {}

    /**
     * Public constructor of definition node.
     *
     * @param parent node {@link DefinitionNode}
     * @param file to hold node data {@link File}
     */
    public DefinitionNode(DefinitionNode parent, File file) {
        this.file = file;
        this.hsupport = new KTreeModelSupport(this);

        if (parent != null) {
            parent.add(this);
        } else {
            this.parent = null;
        }

    }

    /**
     * Create a node
     * @param file to hold node data {@link File}
     */
    public DefinitionNode(File file) {
        this(null, file);
    }

    /**
     * Public constructor of definition node.
     *
     * @param parent   node {@link DefinitionNode}
     * @param filename of file to hold node data
     */
    public DefinitionNode(DefinitionNode parent, String filename) {
        this(parent, new File(parent.getFile(), filename));
    }

    public static DefinitionNode makeFolderNode(File file) {
        return DefinitionNode.makeFolderNode(null, file);
    }

    public static DefinitionNode makeFolderNode(DefinitionNode parent, File file) {
        return new DefinitionNode(parent, file) {
            @Override
            public void load() {
                // directory doesn't load anything
            }

            @Override
            public void save() throws IOException {

                // create a directory if it doesn't exist in the parent's folder if the parent exists
                if (getFile() != null && !getFile().exists()) {

                    File dir = this.getParent() != null
                        ? Path.of(getParent().getFile().getAbsolutePath()
                              + File.separator
                              + getFile().getName()
                           ).toFile()
                        : getFile();

                    FileUtils.forceMkdir(dir);
                }
            }
        };
    }

    /**
     * Add child node
     *
     * @param node is a number in array of children int
     */
    public DefinitionNode add(DefinitionNode node) {
        try {
            if (node != null) {
                node.setParent(this);
                children.add(node);
                hsupport.fireNodeAdded(this, children.indexOf(node));
                node.save();
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return node;
    }

    /*
     * The mandatory listener methods; the Kiwi tree framework locates these
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
     * Delete this node if it not write-protected or locked.
     */
    public boolean delete() throws IOException {
        if (file.canRead()) {
            if (file.delete()) {
                hsupport.fireNodeRemoved(getParent(), getIndex());
                return this.parent.getChildren().remove(this);
            }
        }
        throw new IOException("The folder is locked or non-empty.");
    }

    /**
     * Returns a number of this node in array of parent's children nodes.
     */
    public int getIndex() {
        return this.parent.getChildren().indexOf(this);
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
        if (nm.endsWith(FILE_EXTENSION)) {
            nm = nm.substring(0, nm.length() - FILE_EXTENSION.length());
        }
        return nm;
    }

    /**
     * Returns open ICON
     */
    public Icon getOpenIcon() {
        return getIcon(OPEN_ICON);
    }

    /**
     * Returns closed ICON to represent node in tree control.
     */
    public Icon getClosedIcon() {
        return getIcon(CLOSED_ICON);
    }

    public Icon getIcon() {
        return getIcon(LEAF_ICON);
    }

    /**
     * Returns closed ICON to represent node in tree control.
     */
    private Icon getIcon(Icon icon) {
        if (isRoot()) {
            return ROOT_ICON;
        }
        return isExpandable() ? icon : LOCKED_ICON;
    }

    /**
     * Returns whether if this node is expandable
     */
    public boolean isExpandable() {
        return file.canRead() && file.isDirectory();
    }

    /**
     * Returns whether if this node is root
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Load data from the disk, child classed must override this
     */
    public abstract void load() throws IOException;

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
    public abstract void save() throws IOException;
}