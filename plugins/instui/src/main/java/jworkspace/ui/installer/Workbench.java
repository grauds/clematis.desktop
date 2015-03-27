package jworkspace.ui.installer;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

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
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;


import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.ui.widgets.HelpViewer;
import jworkspace.installer.*;
import jworkspace.kernel.Workspace;
import jworkspace.ui.installer.actions.*;
import jworkspace.util.*;
/**
 * Base class for all workbenches in installer manager.
 */
public abstract class Workbench extends KPanel
                                       implements TreeSelectionListener
{
  /**
   * Installer window
   */
  protected InstallerWindow installer = null;
  /**
   * Workbench actions
   */
  protected WorkbenchActions actions = null;
  /**
   * Information label
   */
  protected JLabel info = null;
  /**
   * Content tree
   */
  protected JTree contentTree = null;
  /**
   * Toolbar with action for content
   */
  protected JToolBar ctoolbar = null;
  /**
   * Splitter for content tree and other stuff
   */
  protected JSplitPane csplit = new JSplitPane();
  /**
   * Build-in Jar Inspector (c)
   */
  protected JarInspector jarInspector = null;
  /**
   * Second splitter for jar inspector
   */
  protected JSplitPane jsplit = new JSplitPane();
  /**
   * Image icon for a workbench
   */
  protected ImageIcon icon = null;
  /**
   * The name of workbench
   */
  protected String text = null;
  /**
   * Comment text
   */
  protected String comment = null;
  /**
   * Current node.
   */
  protected DefinitionNode currentNode = null;
  /**
   * Build-in tabbed resource viewer.
   */
  protected TabbedViewer viewer = null;
 /**
   * Thread to load an image
   */
  class ImageLoader extends Thread
  {
    String f = null;
    String entry = null;

    ImageLoader(String file, String entry)
    {
        setPriority(4);
        this.f = file;
        this.entry = entry;
    }
    ImageLoader(String file)
    {
        setPriority(4);
        this.f = file;
    }
    public synchronized void run()
    {
//    installer.beginProcess();
      installer.setProgress(0);
      Image image = null;
      /**
       * Load image from jar or from file
       */
      if (entry != null)
      {
        // try to start reading
        image = new JarResourceLoader(installer).getJarImage(f, entry);
      }
      else
      {
        try
        {
          java.io.InputStream is = new java.io.FileInputStream(f);
          installer.setProgress(20);
          byte data[] = StreamUtils.readStreamToByteArray(is);
          installer.setProgress(80);
          is.close();
          image = (new ImageIcon(data)).getImage();
        }
        catch(java.io.IOException ex)
        {
          JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
            LangResource.getString("message#162") + ex.getMessage());
        }
      }

      if (image != null && entry != null)
      {
        viewer.addPropPage(getImageViewer(image, entry));
      }
      else if (image != null)
      {
        viewer.addPropPage(getImageViewer(image, f));
      }
      installer.setProgress(100);
   // installer.endProcess();
    }
  }
  /**
   * Thread to load a file into the text storage model
   */
  class StringLoader extends Thread
  {
    String f = null;
    String entry = null;

    StringLoader(String file, String entry)
    {
        setPriority(4);
        this.f = file;
        this.entry = entry;
    }
    StringLoader(String file)
    {
        setPriority(4);
        this.f = file;
    }
    public synchronized void run()
    {
      installer.setProgress(0);
      String text = null;
      /**
       * We are loading from jar file.
       */
      if (entry != null)
      {
        // try to start reading
        text = new jworkspace.util.JarResourceLoader(installer).
            getJarString(f, entry);
      }
      else
      {
        try
          {
            java.io.InputStream is = new java.io.FileInputStream(f);
            byte data[] = kiwi.util.KiwiUtils.readStreamToByteArray(is);
            is.close();
            text = new String(data);
          }
        catch(java.io.IOException ex) {}
      }

      if (text != null && entry != null)
      {
        viewer.addPropPage(getSourceViewer(text, entry));
      }
      else if (text != null)
      {
        viewer.addPropPage(getSourceViewer(text, f));
      }
      installer.setProgress(100);
    }
  }
  /**
   * Contructor
   */
  public Workbench(InstallerWindow installer)
  {
    super();
    this.installer = installer;
    this.setLayout(new BorderLayout());
    this.actions = new WorkbenchActions(this);
  }
  /**
   * Layout workbench skeleton with full-height content tree.
   */
  protected void layoutFullHeightContent()
  {
    /**
     * Holder for content tree, label and toolbar
     */
    KPanel contentHolder = new KPanel();
    contentHolder.setLayout(new BorderLayout());
    contentHolder.add(createInfoLabel(), BorderLayout.NORTH);
    contentHolder.add(new JScrollPane(getContentTree()),
                                         BorderLayout.CENTER);
    contentHolder.add(getToolBar(), BorderLayout.SOUTH);
    /**
     * Layout Jar Inspector
     */
    JSplitPane splitter =
        new JSplitPane(JSplitPane.VERTICAL_SPLIT,
         getJarInspector(), getViewer() );
    splitter.setOpaque(false);
    splitter.setOneTouchExpandable(true);

    csplit.setLeftComponent(contentHolder);
    csplit.setRightComponent(splitter);
    csplit.setOpaque(false);
    csplit.setOneTouchExpandable(true);

    this.add(csplit, BorderLayout.CENTER);
  }
  /**
   * Create info label on the top of content tree
   */
  protected JLabel createInfoLabel()
  {
    JLabel l = new JLabel();

    l.setBackground(Color.white);
    l.setOpaque(true);
    l.setIcon(icon);

    StringBuffer sb = new StringBuffer();
    sb.append("<html><font color=black>");
    sb.append(text);
    sb.append("</font><br>");
    sb.append("<font size=\"-2\" color=black><i>");
    sb.append(comment);
    sb.append("</i></font></html>");

    l.setText(sb.toString());

    l.setPreferredSize(new Dimension(150, 70));
    l.setMinimumSize(l.getPreferredSize());
    l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    l.setHorizontalAlignment(JLabel.CENTER);
    return l;
  }
  /**
   * Layout workbench skeleton with full-height viewer.
   */
  protected void layoutFullHeightViewer()
  {
    /**
     * Holder for content tree, label and toolbar
     */
    KPanel contentHolder = new KPanel();
    contentHolder.setLayout(new BorderLayout());
    contentHolder.add(createInfoLabel(), BorderLayout.NORTH);
    contentHolder.add(getContentTree(), BorderLayout.CENTER);
    contentHolder.add(getToolBar(), BorderLayout.SOUTH);
    /**
     * Layout Jar Inspector
     */
    JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
         contentHolder, getJarInspector());
    splitter.setOpaque(false);
    splitter.setOneTouchExpandable(true);

    csplit.setLeftComponent(splitter);
    csplit.setRightComponent(getViewer());
    csplit.setOpaque(false);
    csplit.setOneTouchExpandable(true);

    this.add(csplit, BorderLayout.CENTER);
  }
  /**
   * Create content tree and fill data if tree is not
   * created.
   */
  protected JTree initContentTree(DynamicTreeModel model)
  {
    if (contentTree == null)
    {
      contentTree = new JTree();
      contentTree.addTreeSelectionListener(this);
      contentTree.getSelectionModel()
        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      TreeModelTreeAdapter adapter = new TreeModelTreeAdapter(contentTree);
      adapter.setTreeModel(model);
      contentTree.setModel(adapter);

      ModelTreeCellRenderer renderer = new ModelTreeCellRenderer(model);
      renderer.setHighlightBackground(UIManager.getColor("control"));
      renderer.setHighlightForeground(UIManager.getColor("textText"));
      contentTree.setCellRenderer(renderer);
    }
    return contentTree;
  }
  /**
   * Create toolbar for a specified type of content
   */
  public abstract JToolBar getToolBar();
  /**
   * Get a reference to content tree
   */
  public abstract JTree getContentTree();
  /**
   * Add entry
   */
  public abstract void add();
  /**
   * Edit entry
   */
  public abstract void edit();
  /**
   * Create and return text and image viewer
   */
  public KPanel getViewer()
  {
    if (viewer == null)
    {
      viewer = new TabbedViewer();
    }
    return viewer;
  }
  /**
   * Creates image viewer
   * @return javax.swing.JEditorPane
   */
  public ImageView getImageViewer(Image image, String name)
  {
    ImageView viewer = new ImageView(name, image);
    viewer.setName(name);
    return viewer;
  }
  /**
   * Creates text viewer
   * @return javax.swing.JEditorPane
   */
  public JSourceViewer getSourceViewer(String text, String name)
  {
    JSourceViewer viewer = new JSourceViewer();
    viewer.setText(text);
    viewer.setName(name);
    return viewer;
  }
  /**
   * Create and return Jar Inspector
   */
  public JarInspector getJarInspector()
  {
    if (jarInspector == null)
    {
      jarInspector = new JarInspector();
      jarInspector.getTable().addMouseListener(new MouseInputAdapter()
      {
        public void mouseClicked(MouseEvent evt)
        {
            if (evt.getClickCount() == 2)
            {
               int row = jarInspector.getTable().getSelectedRow();

               String file = (String)
                      jarInspector.getTable().getValueAt(row, 0);

               TableColumn path_col = jarInspector.getTable().getColumn("Path");
               int path_col_index = path_col.getModelIndex();

               String path = (String) jarInspector.getTable().getValueAt(row,
                                                             path_col_index);
               loadResource(jarInspector.getSource(), path, file);
            }
        }
      });
    }
    return jarInspector;
  }
  /**
   * Construct source path and load file from jar file or directory.
   */
  protected void loadResource(String source, String path, String name)
  {
     if (source == null || path == null || name == null) return;

     if ((new File(source)).isDirectory())
     {
       /**
        * Get graphics from directory.
        */
        if (installer.isGraphics(name))
        {
          /**
           * Load contents.
           */
           Thread loader = new ImageLoader(path + File.separator + name);
           loader.start();
        }
       /**
        * Get text from directory.
        */
        else if (installer.isSource(name))
        {
          /**
           * Load contents.
           */
           Thread loader = new StringLoader(path + File.separator + name);
           loader.start();
        }
     }
     /**
      * Jar source file
      */
     else
     {
       /**
        * Get graphics from jar.
        */
        if (installer.isGraphics(name))
        {
          /**
           * Load contents.
           */
           Thread loader = new ImageLoader(source, path
                                    + "/" + name);
           loader.start();
        }
       /**
        * Get text from jar
        */
        else if (installer.isSource(name))
        {
          /**
           * Load contents.
           */
           Thread loader = new StringLoader(source, path
                                    + "/" + name);
           loader.start();
         }
     }
   }
  /**
   * Validates name for installation.
   * @param name java.lang.String
   * @param title java.lang.String
   */
  protected boolean validateInstName(DefinitionNode parent,
                                        String name, String title)
  {
    if (name == null || name.trim().equals("")) return false;
    try
    {
      File file = File.createTempFile(name + "_check", "tmp");
      file.delete();
    }
    catch(IOException ex)
    {
      JOptionPane.showMessageDialog( this,
          LangResource.getString("workbench.name.incorrect"),
          LangResource.getString("workbench.name.incorrect.title"),
           JOptionPane.ERROR_MESSAGE, icon);
       return false;
    }
    if(new File(parent.getFile(),name + WorkspaceInstaller.FILE_EXTENSION).exists())
    {
      JOptionPane.showMessageDialog(Workspace.
          getUI().getFrame(),
          LangResource.getString("message#188") +
          " " + title + ". " + LangResource.getString("message#1881") + "",
          LangResource.getString("message#198"),
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }
  /**
   * This method tracks down
   * tree selection events and sends data
   * to source inspector.
   */
  public void valueChanged(TreeSelectionEvent e)
  {
    JTree source_tree = (JTree) e.getSource();

    if (source_tree == contentTree)
    {
      TreePath p = source_tree.getSelectionPath();
      boolean nosel = (p == null);
      currentNode = (nosel ? null
               : (DefinitionNode)(((ITreeNode)p.getLastPathComponent())
                        .getObject()));
      /**
       * Now we should pass the full qualified name of jar or
       * zip file to Installer Inspector
       */
       String source = null;

       if (currentNode instanceof Application)
       {
        source = ((Application) currentNode).getSource();
       }
       else if (currentNode instanceof Library)
       {
        source = ((Library) currentNode).getSource();
       }
       else if (currentNode instanceof JVM)
       {
        source = ((JVM) currentNode).getSource();
       }
       jarInspector.setSource(source);
    }
  }
  /**
   * Add folder to any of installations trees
   */
  public void addFolder()
  {
     String name = JOptionPane
         .showInputDialog(this,LangResource.getString("message#200"),
          LangResource.getString("message#198"),
             JOptionPane.QUESTION_MESSAGE);
     if(name != null && !name.trim().equals("") &&
       currentNode.getClass() != Application.class
       && currentNode.getClass() != Library.class
       && currentNode.getClass() != JVM.class)
     {
       currentNode.add(name, 0);
     }
     else if (name == null || name.trim().equals(""))
     {
      // do nothing here
     }
     else if (currentNode.getClass() == Application.class
        || currentNode.getClass() == Library.class
        || currentNode.getClass() == JVM.class)
     {
      JOptionPane.showMessageDialog(this,
       LangResource.getString("message#194"),
        LangResource.getString("message#198"), JOptionPane.INFORMATION_MESSAGE);
     }
  }
  /**
   * Deletes current node.
   */
  public void delete()
  {
    /**
     * Cannot delete root node.
     */
    if (currentNode == null || currentNode.isRoot()) return;

    int result = JOptionPane.showConfirmDialog(this,
                              LangResource.getString("message#193"),
                              LangResource.getString("message#177"),
                              JOptionPane.YES_NO_OPTION);
    if(result == JOptionPane.YES_OPTION)
    {
      try
      {
        currentNode.delete();
        contentTree.clearSelection();
      }
      catch(IOException ex)
      {
        WorkspaceError.exception(LangResource.getString("message#203"), ex);
      }
    }
  }
  /**
   * View documentation for the current node
   */
  public void viewDocumentation()
  {
    String url = "blank.html";
    if (currentNode instanceof Application)
         url = ((Application)currentNode).getDocs();
    else if (currentNode instanceof Library)
         url = ((Library)currentNode).getDocs();
    else if (currentNode instanceof JVM)
         url = ((JVM)currentNode).getDocs();

    try
    {
       HelpViewer hviewer = new HelpViewer(new URL("file:" + url));
       hviewer.setName(url);
       viewer.addPropPage(hviewer);
    }
    catch(MalformedURLException ex)
    {
      JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                    LangResource.getString("message#167") + "file:" + url,
                    LangResource.getString("message#184"),
                    JOptionPane.INFORMATION_MESSAGE);
    }
  }
}
