package jworkspace.api;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000, 2024 Anton Troshin

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.ui.model.datasource.TreeDataSource;

import lombok.Getter;
import lombok.Setter;

/**
 * An implementation of {@link com.hyperrealm.kiwi.ui.model.datasource.TreeDataSource TreeDataSource},
 * a "smart" {@link DefinitionDataSource}, since it can traverse itself
 * looking for a node that matches a given link path. It is an instance of {@link TreeDataSource}, therefore
 * can be used as a source of hierarchical data for other components.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@Getter
public class DefinitionDataSource implements TreeDataSource<DefinitionNode> {

    public static final String EXPANDABLE_PROPERTY = "EXPANDABLE";

    public static final String LABEL_PROPERTY = "LABEL";

    public static final String ICON_PROPERTY = "ICON";

    private static final Logger LOG = LoggerFactory.getLogger(DefinitionDataSource.class);

    private static final DefinitionNode[] EMPTY_ARRAY = new DefinitionNode[0];

    /**
     * Name for the data source
     */
    @Setter
    private String name;

    /**
     *  Data hierarchy root.
     */
    private final DefinitionNode root;

    /**
     * Definition data source is based on file structure. Each object of this class should encapsulate separate file.
     *
     * @param root {@link DefinitionNode}
     */
    public DefinitionDataSource(DefinitionNode root) {
        this.root = root;
    }

    public DefinitionDataSource(File root) {
        this.root = DefinitionNode.makeFolderNode(null, root);
    }

    /**
     * Find node by its link path, which is actually a string like follows "/node/node1/node2".
     *
     * @param linkPath {@link String}
     */
    public DefinitionNode findNode(String linkPath) {

        if (linkPath != null) {
            StringTokenizer st = new StringTokenizer(linkPath, "/");
            if (st.hasMoreElements() && root.getNodeName().equals(st.nextToken())) {
                return doFindNode(st, root);
            }
        }
        return null;
    }

    /**
     * Iterates through the children until find first branch with given link path.
     */
    private DefinitionNode doFindNode(StringTokenizer st, DefinitionNode currentNode) {
        DefinitionNode ret = null;

        if (st.hasMoreTokens()) {
            String link = st.nextToken();
            Object[] children = getChildNodes(currentNode);

            for (Object child : children) {
                DefinitionNode node = (DefinitionNode) child;
                if (node.getNodeName().equals(link)) {
                    ret = !st.hasMoreTokens() ? node : doFindNode(st, node);
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Return ordered array of children for given definition node
     *
     * @param node {@link DefinitionNode}
     */
    public DefinitionNode[] getChildren(DefinitionNode node) {
        return getChildNodes(node);
    }

    /**
     * Returns ordered list of child nodes.
     */
    private DefinitionNode[] getChildNodes(DefinitionNode node) {

        File[] files;

        if (node == null
            || !node.isExpandable()
            || !node.getFile().exists()
            || node.getFile().listFiles() == null
        ) {
            return EMPTY_ARRAY;
        }

        files = node.getFile().listFiles();

        ArrayList<File> dirs = new ArrayList<>();
        ArrayList<File> sfiles = new ArrayList<>();

        Arrays.stream(files).forEach((file) -> {
            if (file.isDirectory()) {
                dirs.add(file);
            } else {
                sfiles.add(file);
            }
        });

        Collections.sort(dirs);
        Collections.sort(sfiles);
        dirs.addAll(sfiles);

        ArrayList<DefinitionNode> v = new ArrayList<>();
        for (File file : dirs) {
            v.add(DefinitionNode.makeFolderNode(node, file));
        }

        return v.toArray(DefinitionNode[]::new);
    }

    @Override
    public boolean isExpandable(DefinitionNode node) {
        return node.isExpandable();
    }

    @Override
    public Icon getIcon(DefinitionNode node, boolean expanded) {
        return node.isExpandable() ? (expanded ? node.getOpenIcon() : node.getClosedIcon()) : node.getIcon();
    }

    @Override
    public String getLabel(DefinitionNode node) {
        return node.getNodeName();
    }

    /**
     * Returns objects for the given properties:
     *
     * <ol>
     * <li>EXPANDABLE_PROPERTY - true if the node is a folder
     * <li>LABEL_PROPERTY - returns label for the node
     * <li>ICON_PROPERTY - returns icon for the node
     * <li>COLUMN_NAMES_PROPERTY - for the columns names in the table
     * <li>COLUMN_TYPES_PROPERTY - for the columns types in the table
     * </ol>
     *
     * Otherwise, returns null
     */
    public Object getValueForProperty(DefinitionNode node, String property) {

        return switch (property) {
            case EXPANDABLE_PROPERTY -> node.isExpandable() ? Boolean.TRUE : Boolean.FALSE;
            case LABEL_PROPERTY -> node.isRoot() ? getName() : node.getNodeName();
            case ICON_PROPERTY -> node.getIcon();
            case COLUMN_NAMES_PROPERTY -> new String[]{"Name"};
            case COLUMN_TYPES_PROPERTY -> new Class[]{String.class};
            default -> null;
        };
    }
}