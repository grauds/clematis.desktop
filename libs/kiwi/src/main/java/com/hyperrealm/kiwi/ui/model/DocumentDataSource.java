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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.Icon;

import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * An implementation of
 * {@link com.hyperrealm.kiwi.ui.model.TreeDataSource TreeDataSource}
 * in which tree nodes represent HTML documents that are loaded as system
 * resources.
 * <p>
 * Documents are loaded using the provided <code>ResourceManager</code>. The
 * default base path for the documents is "docs/", and since they are loaded
 * as URL resources, they should be located relative to "html/docs/" which in
 * turn will be relative to the <code>ResourceManager</code>'s resource anchor
 * class. One form of the constructor allows an alternate base path to be
 * specified.
 * <p>
 * Documents may be organized in a hierarchy; each expandable node in the
 * hierarchy corresponds to a directory in the resource file tree. Each
 * directory (including the root directory, "docs/") is expected to contain
 * an index file named "_index.txt" describing the child nodes of that
 * directory. Nodes are listed in this file, one per line. Each line should
 * include the following fields, delimited by vertical bar characters (|):
 * <p>
 * <dl>
 * <dt>Icon name
 * <dd>The name of the ICON for this node. If an asterisk (*) is specified
 * for the name, a default ICON will be used.
 * <dt>Alternate Icon name
 * <dd>The name of an alternate ICON for this node. This is the ICON that
 * is used to display expanded nodes. If an asterisk (*) is specified for the
 * name, a default ICON will be used. If a dash (-) is specified for the name,
 * the alternate ICON will be the same as the normal ICON (above).
 * <dt>Expandability flag
 * <dd>One of the characters '+' or '-', denoting expandable and
 * non-expandable, respectively.
 * <dt>Node label
 * <dd>A human-readable, brief textual description of the node.
 * <dt>File
 * <dd>The file corresponding to this resource--either a directory name if
 * this is an expandable node, or a filename if this is a leaf (document)
 * node.
 * </dl>
 * <p>
 * Here is an example entry:
 * <p>
 * <tt>book.png|book_open.png|+|API Specification|api</tt>
 * <p>
 * <b>This class is unsynchronized</b>. Instances of this class should not
 * be accessed concurrently by multiple threads without explicit
 * synchronization.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ResourceManager
 * @see com.hyperrealm.kiwi.ui.DocumentBrowserView
 * @see com.hyperrealm.kiwi.ui.DocumentBrowserFrame
 */

public class DocumentDataSource implements TreeDataSource<DocumentDataSource.DocumentNode> {
    /**
     * The default description of this set of documents.
     */
    private static final String DEFAULT_DESCRIPTION = "Help Topics";
    /**
     * The default relative document base path.
     */
    private static final String DEFAULT_BASEPATH = "docs/";

    private static final String INDEX_FILE = "_index.txt";

    private static final Icon DOCUMENT_ICON = KiwiUtils.getResourceManager()
        .getIcon("document.png");

    private static final Icon BOOKS_ICON = KiwiUtils.getResourceManager()
        .getIcon("books.png");

    private static final Icon UNKNOWN_ICON = KiwiUtils.getResourceManager()
        .getIcon("document_blank.png");

    private static final Icon BOOK_OPEN_ICON = KiwiUtils.getResourceManager()
        .getIcon("book_open.png");

    private static final Icon BOOK_CLOSED_ICON = KiwiUtils.getResourceManager()
        .getIcon("book.png");

    private static final String SUFFIX_SLASH = "/";

    private static final String STAR = "*";

    private static final int TOKENS_NUMBER = 5;

    private ResourceManager resmgr;

    private DocumentNode root;

    /**
     * Construct a new <code>DocumentDataSource</code> with a default
     * description and base path.
     *
     * @param manager the <code>ResourceManager</code> that will be used to load
     *                index files for the tree.
     */

    public DocumentDataSource(ResourceManager manager) {
        this(manager, DEFAULT_DESCRIPTION, DEFAULT_BASEPATH);
    }

    /**
     * Construct a new <code>DocumentDataSource</code> with the specified
     * description and base path.
     *
     * @param manager     The <code>ResourceManager</code> that will be used to load
     *                    index files for the tree.
     * @param description A brief description of this set of documents; this
     *                    becomes the label for the root node in the tree.
     */

    public DocumentDataSource(ResourceManager manager, String description,
                              String basePath) {
        this.resmgr = manager;
        String basePathInt = basePath;

        if (!basePathInt.endsWith(SUFFIX_SLASH)) {
            basePathInt += SUFFIX_SLASH;
        }

        URL rootURL = resmgr.getURL(basePathInt + INDEX_FILE);
        root = new DocumentNode(description, BOOKS_ICON, null, rootURL, true, "");
    }

    /**
     * Get the root object.
     */

    public DocumentNode getRoot() {
        return (root);
    }

    /**
     * Get the children of a given node.
     */

    public DocumentNode[] getChildren(DocumentNode node) {
        ArrayList<DocumentNode> children = new ArrayList<DocumentNode>();

        try (InputStream is = node.getURL().openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String s;
            DocumentNode sect;

            while ((s = reader.readLine()) != null) {
                sect = parseLine(node, s);
                if (sect != null) {
                    children.add(sect);
                }
            }
        } catch (IOException ignored) {

        }

        DocumentNode[] childlist = new DocumentNode[children.size()];
        children.toArray(childlist);

        return (childlist);
    }

    public boolean isExpandable(DocumentNode node) {
        return (node.isExpandable());
    }

    public Icon getIcon(DocumentNode node, boolean isExpanded) {
        return (isExpanded ? node.getAltIcon() : node.getIcon());
    }

    public String getLabel(DocumentNode node) {
        return (node.getLabel());
    }

    /**
     * Get the value for a given property.
     */

    public Object getValueForProperty(DocumentNode node, String property) {
        return (null);
    }

    /* parse a line of an index file */

    private DocumentNode parseLine(DocumentNode parent, String line) {

        StringTokenizer st = new StringTokenizer(line, "|");

        DocumentNode ret = null;
        URL url;
        Icon icon, alticon = null;

        if (st.countTokens() == TOKENS_NUMBER) {

            String file;
            String iconName = st.nextToken();
            String altIconName = st.nextToken();
            boolean expandable = st.nextToken().equals("+");

            if (iconName.equals(STAR)) {
                icon = (expandable ? BOOK_CLOSED_ICON : DOCUMENT_ICON);
            } else {
                icon = resmgr.getIcon(iconName);
            }

            if (altIconName.equals(STAR)) {
                alticon = (expandable ? BOOK_OPEN_ICON : DOCUMENT_ICON);
            } else if (!altIconName.equals("-")) {
                alticon = resmgr.getIcon(altIconName);
            }

            String label = st.nextToken();

            try {

                String parentURL = parent.getURL().toString();
                int idx = parentURL.lastIndexOf(SUFFIX_SLASH);
                file = st.nextToken();
                String temp = parentURL.substring(0, idx) + SUFFIX_SLASH + file;
                if (expandable) {
                    temp += SUFFIX_SLASH + INDEX_FILE;
                }
                url = new URL(temp);
                ret = new DocumentNode(label, icon, alticon, url, expandable, file);

            } catch (MalformedURLException ignored) {

            }
        }

        return ret;
    }

    /**
     *  Internal class for managing help nodes.
     * @author Anton Troshin
     */
    public class DocumentNode {

        private String label, file;

        private Icon icon, alticon;

        private URL url;

        private boolean expandable;

        private DocumentNode parent = null;

        DocumentNode(String label, Icon icon, Icon alticon,
                     URL url, boolean expandable, String file) {
            this.label = label;
            this.icon = ((icon != null) ? icon : UNKNOWN_ICON);
            this.alticon = ((alticon != null) ? alticon : this.icon);
            this.url = url;
            this.expandable = expandable;
            this.file = file;
        }

        public DocumentNode getParent() {
            return (parent);
        }

        public String getFile() {
            return (file);
        }

        public boolean isExpandable() {
            return (expandable);
        }

        public String getLabel() {
            return (label);
        }

        public Icon getIcon() {
            return (icon);
        }

        public Icon getAltIcon() {
            return (alticon);
        }

        public URL getURL() {
            return (url);
        }
    }

}
