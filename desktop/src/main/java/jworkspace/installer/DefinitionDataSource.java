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

import com.hyperrealm.kiwi.ui.model.TreeDataSource;
import jworkspace.util.sort.QuickSort;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This is a "smart" DefinitionDataSource,
 * since it can traverse itself
 * looking for a node that matches a given link path.
 */
public abstract class DefinitionDataSource implements TreeDataSource<DefinitionNode>
{
    private static final String EXPANDABLE_PROPERTY = "EXPANDABLE";
    private static final String LABEL_PROPERTY = "LABEL";
    private static final String ICON_PROPERTY = "ICON";

    private DefinitionNode root;
    private static final DefinitionNode emptyArray[] = new DefinitionNode[0];
    protected boolean hierarchy = true;

    /**
     * Public constuctor.
     * Definition data source is based on file
     * structure. Each object of this class should
     * encapsulate separate file.
     * @param root java.io.File
     */
    DefinitionDataSource(File root)
    {
        this.root = new DefinitionNode(null, root);
        if (!root.exists())
            root.mkdir();
    }

    /**
     * This helper routin iterates through
     * the children until find first branch
     * with given link path.
     */
    private DefinitionNode _findNode(StringTokenizer st,
                                     DefinitionNode currentNode)
    {
        if (!st.hasMoreTokens())
            return (null);

        String link = st.nextToken();

        // ok, let's look through the children...

        Object children[] = getChildNodes(currentNode);
        for (int i = 0; i < children.length; i++)
        {
            DefinitionNode node = (DefinitionNode) children[i];

            if (node.getNodeName().equals(link))
            {
                if (!st.hasMoreTokens())
                    return (node);
                else
                    return (_findNode(st, node));
            }
        }

        return (null);
    }

    /**
     * Find node by its link path, which is actually
     * a string like follows "/node/node1/node2".
     * Java Workspace Installer uses link path
     * then launching applications.
     * @param linkPath java.lang.String
     */
    public DefinitionNode findNode(String linkPath)
    {
        if (linkPath == null)
            return (null);
        StringTokenizer st = new StringTokenizer(linkPath, "/");
        if (!st.hasMoreElements())
            return (null);
        if (!root.getNodeName().equals(st.nextToken()))
            return (null);
        return (_findNode(st, root));
    }

    /**
     * Returns ordered list of child nodes.
     */
    private DefinitionNode[] getChildNodes(DefinitionNode defn)
    {
        if (!defn.isExpandable())
            return (emptyArray);

        File f = defn.getFile();
        File files[] = f.listFiles();
        Vector v = new Vector();
        /**
         * As there is no guarantee, that files will
         * be in alphabetical order, lets sort
         * directories and files.
         */
        Vector dirs = new Vector();
        Vector sfiles = new Vector();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
                dirs.addElement(files[i]);
            else
                sfiles.addElement(files[i]);
        }
        /**
         * Sorting
         */
        new QuickSort(true).sort(dirs);
        new QuickSort(true).sort(sfiles);

        dirs.addAll(sfiles);

        for (int i = 0; i < dirs.size(); i++)
        {
            File file = (File) dirs.elementAt(i);
            if (file.isDirectory() && hierarchy)
                v.addElement(new DefinitionNode(defn, file));

            else
            {
                try
                {
                    v.addElement(makeNode(defn, file));
                }
                catch (IOException ex)
                {
                    /* silently ignore stuff we can't read */
                }
            }
        }

        DefinitionNode children[] = new DefinitionNode[v.size()];
        v.copyInto(children);
        return (children);
    }

    /**
     * Return ordered array of children for given definition node
     * @param node jworkspace.installer.DefinitionNode
     */
    public DefinitionNode[] getChildren(DefinitionNode node)
    {
        return (getChildNodes(node));
    }

    /**
     * Returns root for data hierarchy.
     */
    public DefinitionNode getRoot()
    {
        return (root);
    }

    /**
     * Returns root name for data hierarchy.
     */
    public String getRootName()
    {
        return ("Root");
    }

    @Override
    public boolean isExpandable(DefinitionNode node) {
        return node.isExpandable();
    }

    @Override
    public Icon getIcon(DefinitionNode node, boolean expanded) {

        if (expanded) {
            return node.getOpenIcon();
        } else {
            return node.getClosedIcon();
        }
    }

    @Override
    public String getLabel(DefinitionNode node) {
        return node.getNodeName();
    }

    /**
     * Returns object for given property.
     * Properties are:
     * <ol>
     * <li>EXPANDABLE_PROPERTY - is this node expandable or not
     * <li>LABEL_PROPERTY - returns label for the node
     * <li>ICON_PROPERTY - returns icon for the node
     * </ol>
     * Otherwise returns null
     */
    public Object getValueForProperty(DefinitionNode node, String property)
    {
        if (property.equals(EXPANDABLE_PROPERTY)) {
            return (node.isExpandable() ? Boolean.TRUE : Boolean.FALSE);
        }
        else if (property.equals(LABEL_PROPERTY)) {
            return (node.isRoot() ? getRootName() : node.getNodeName());
        }
        else if (property.equals(COLUMN_NAMES_PROPERTY)) {
            return new String[] {"Name"};
        } else if (property.equals(COLUMN_TYPES_PROPERTY)) {
            return new Class[] {String.class};
        } else {
            return (null);
        }

    }

    protected abstract DefinitionNode makeNode(DefinitionNode parent, File file)
            throws IOException;
}