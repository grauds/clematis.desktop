package jworkspace.ui.installer;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 - 2002 Anton Troshin

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
import java.util.*;
import java.util.zip.*;
import javax.swing.*;
import javax.swing.table.*;
import java.text.*;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.Task;
import jworkspace.kernel.*;
import jworkspace.ui.*;

import kiwi.ui.dialog.*;
/**
 * Jar inspector gets a source as a jar path or jar object and
 * fills table with jar entries. Can also pack and unpack archives,
 * edit manifest files.
 */
public class JarInspector extends KPanel
{
  /**
   * Table to hold jar entries.
   */
  private JTable table = null;
  /**
   * The SortedTableHelper used to set up the sorting and filtering.
   */
  private SortedTableHelper helper = null;
  /**
   * Toolbar for this jar inspector
   */
  private JToolBar jtoolbar = new JToolBar();
  /**
   * The name of jar file or directory, that should be inspected.
   */
  private String source = null;
  /**
   * Inspector thread
   */
  private Task inspector = null;
  /**
   * Custom table cell renderer
   */
  protected class InspectorTableRenderer extends DefaultTableCellRenderer
  {
   public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column)
    {
       if (! isSelected)
       {
         if ( Math.IEEEremainder(row, 2) == 0 )
         {
           setBackground(new Color(225, 225, 225));
         }
         else
         {
           setBackground(Color.white);
         }
       }
       Component ret = super.getTableCellRendererComponent(table, value,
                           isSelected, hasFocus, row, column);
       return ret;
    }
  }
  /**
   * Inner class to scan jars and directories
   */
  class Inspector extends Task
  {
    String source = null;

    public Inspector(String source)
    {
      super();
      this.source = source;
      addProgressObserver(pr);
    }
    public void run()
    {
      if (source != null)
      {
        try
        {
          if ((new File(source)).isDirectory())
          {
             inspect();
          }
          else if (source.endsWith("jar") || source.endsWith("zip"))
          {
             inspectZip();
          }
          pr.setProgress(100);
        }
        catch(java.io.IOException ex)
        {
           pr.setProgress(100);
           JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
              LangResource.getString("Error_inspecting_jar") +
                       ": "+ ex.getMessage(),
              LangResource.getString("Message"),
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
  /**
   * The model, that is filled each time inpection occurs
   */
  private DefaultTableModel new_model;
  /**
   * The names of columns for jar
   */
  protected Object[] jarColumnNames = new Object[]
  {
        LangResource.getString("Name"), LangResource.getString("Modified"),
        LangResource.getString("Size"), LangResource.getString("Ratio_"),
        LangResource.getString("Packed"), LangResource.getString("Path")
  };
  /**
   * The names of columns for directory
   */
  protected Object[] dirColumnNames = new Object[]
  {
        LangResource.getString("Name"), LangResource.getString("Modified"),
        LangResource.getString("Size"), LangResource.getString("Path")
  };
  private ProgressDialog pr = null;
  /**
   * Toolbar for jar inspector
   */
  protected JToolBar toolbar = null;
  /**
   * Paths of current directory or jar file.
   */
  protected Vector paths = new Vector();
  /**
   * Combobox with selection paths
   */
  protected JComboBox c_paths = null;
  /**
   * The filter for the table data.
   */
  protected Filter filter = new Filter ();
  /**
   * An Object of this class will be used to filter the
   * rows of the table.
   */
  protected class Filter implements TableModelFilter
  {
    /**
     * Selector that decides what rows to filter.
     */
    private String filter;
    /**
     * The registered TableModelFilterListeners.
     */
    private Vector listeners = new Vector ();
    /**
     * Create a new Filter.
     */
    public Filter () {}
    /**
     * Select which rows to filter.
     *
     * @param       filterType      The selector that determines the
     *                              filtered rows.
     */
    public void setFilter(String filter)
    {
       this.filter = filter;
       fireFilterChanged();
    }
   /**
    * Decide whether a row should be displayed or not. The result depends
    * on the selector set with {@link #setFilterType setFilterType}.
    *
    * @param       model   The original table model.
    * @param       row     The row to filter.
    *
    * @return      True to display the row, false to hide it.
    */
    public boolean filter (TableModel model, int row)
    {
       if (filter == null || filter.equals("None"))
       {
         return true;
       }
       else
       {
         /**
          * Trim filter. If it ends with / - remove the last char
          */
         if (filter.endsWith(File.separator) ||
              filter.endsWith("/"))
           filter = filter.substring(0, filter.length() - 1);
         int col = 0;
         for (int i = 0; i < model.getColumnCount(); i++)
         {
           if (model.getColumnName(i).equalsIgnoreCase("path"))
           {
              col = i;
              break;
           }
         }
         return ( ((String) model.getValueAt(row, col)).startsWith(filter));
       }
    }
    /**
     * Add a TableModelFilterListener to the TableModelFilter.
     *
     * @param   listener        The TableModelFilterListener to add.
     */
     public synchronized void addTableModelFilterListener
         (TableModelFilterListener listener)
     {
        if (! listeners.contains(listener))
        {
           listeners.addElement(listener);
        }
     }
    /**
     * Remove a TableModelFilterListener from the TableModelFilter.
     *
     * @param   listener        The TableModelFilterListener to remove.
     */
     public synchronized void removeTableModelFilterListener
         (TableModelFilterListener listener)
     {
        listeners.removeElement(listener);
     }
    /**
     * Notify the registered listeners of a change in the filter.
     */
     private void fireFilterChanged()
     {
        Vector tmp;
        synchronized(this)
        {
           tmp = (Vector) listeners.clone();
        }
        TableModelFilterEvent event = new TableModelFilterEvent (this);
        for (Enumeration e = tmp.elements();
                         e.hasMoreElements(); )
        {
          ((TableModelFilterListener) e.nextElement()).filterChanged(event);
        }
     }
   }
  /**
   * Constructor.
   */
  public JarInspector()
  {
    super();
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(150, 200));
    add(getToolBar(), BorderLayout.NORTH);
    JScrollPane scroller = new JScrollPane(createTable
               (new DefaultTableModel(dirColumnNames, 0)));
    scroller.setOpaque(false);
    scroller.getViewport().setOpaque(false);
    add(scroller, BorderLayout.CENTER);
  }
  /**
   * Set source and inspect entries
   */
  public void setSource(String path)
  {
    this.source = path;
    this.paths = new Vector();
    this.paths.addElement(LangResource.getString("None"));
    filter.setFilter(LangResource.getString("None"));
    if (source != null)
    {
       inspector = new Inspector(source);
       pr = new ProgressDialog(Workspace.getUI().getFrame(),
                       LangResource.getString("Scanning_Installation"), true);
       pr.centerDialog();
       pr.track(inspector);
    }
    else
    {
       new_model = null;
    }
    updateTable();
  }
 /**
  * Inspects zip file to find
  * packages and classes.
  */
  private synchronized void inspectZip() throws IOException
  {
    if (source != null)
    {
       File file = new File(source);
       ZipFile jar = new ZipFile(file);
       new_model = getJarTableModel();
       for (Enumeration e = jar.entries(); e.hasMoreElements();)
       {
         ZipEntry entry = (ZipEntry) e.nextElement();
         /**
          * Zip and Jar files contain directories.
          */
         if (entry.isDirectory())
         {
            paths.addElement(entry.getName());
            continue;
         }
         addRow(new_model, entry);
       }
       jar.close();
     }
  }
  /**
   * Inspects directory and recursively loads all stuff from it.
   */
  private synchronized void inspect()
  {
    if (source != null)
    {
      File file = new File(source);
      if (file.exists())
      {
        new_model = getDirTableModel();
        scanDirectory(file, new_model);
      }
      else
      {
        JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
          LangResource.getString("Error_inspecting"));
      }
    }
  }
  private void scanDirectory(File file,  DefaultTableModel model)
  {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++)
      {
         if (children[i].isDirectory())
         {
           paths.addElement(children[i].getPath());
           scanDirectory(children[i], model);
         }
         else if (children[i].isFile())
         {
           addRow(model, children[i]);
         }
      }
  }
  /**
   * Get table
   */
  public JTable getTable()
  {
      if (table == null)
      {
          table = new JTable();
          table.getTableHeader().setReorderingAllowed(false);
          table.setShowGrid(false);
          table.setShowVerticalLines(true);
          table.setOpaque(false);
          table.setDefaultRenderer( String.class, new InspectorTableRenderer());
          table.setDefaultRenderer( Long.class, new InspectorTableRenderer());
          table.setDefaultRenderer( Double.class, new InspectorTableRenderer());
      }
      return table;
  }
  /**
   * Create table
   */
  protected JTable createTable(DefaultTableModel model)
  {
      if (model != null)
        getTable().setModel(model);
      return getTable();
  }
  /**
   * Create model for the jar file
   */
  public DefaultTableModel getJarTableModel()
  {
      DefaultTableModel jarModel = new DefaultTableModel(jarColumnNames, 0)
        {
            // Set up the column classes
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Long.class;
                    case 3:
                        return Double.class;
                    case 4:
                        return Long.class;
                    case 5:
                        return String.class;
                }
                return Object.class;
            }
            // Only the String and the Checkbox columns are editable
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
     return jarModel;
  }
  /**
   * Returns current source
   */
  public String getSource()
  {
    return source;
  }
  /**
   * Create model for the jar file
   */
  public DefaultTableModel getDirTableModel()
  {
        DefaultTableModel dirModel = new DefaultTableModel(dirColumnNames, 0)
        {
            // Set up the column classes
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Long.class;
                    case 3:
                        return String.class;
                }
                return Object.class;
            }
            // Only the String and the Checkbox columns are editable
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
     return dirModel;
  }
  /**
   * Insert jar entry as a row to a table data model
   */
  protected void addRow(DefaultTableModel model, ZipEntry entry)
  {
     Vector row = new Vector();
     /**
      * Entry name
      */
     int index = entry.getName().lastIndexOf("/");

     String name = null;
     String path = null;
     if (index != -1)
     {
      name = entry.getName().substring(index + 1);
      path = entry.getName().substring(0, index);
     }
     else
     {
      name = entry.getName();
      path = "";
     }
     row.addElement(name);
     /**
      * Entry date
      */
     SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy hh:mm");
     Date date = new Date(entry.getTime());
     StringBuffer sb = new StringBuffer();
     sb = formatter.format(date, sb, new FieldPosition(0));
     row.addElement(sb.toString());
     /**
      * Entry size
      */
     row.addElement(new Long(entry.getSize()));
     /**
      * Compression ratio
      */
     float ratio = (entry.getSize() - entry.getCompressedSize()) * 100
                                 / entry.getSize();

     row.addElement(new Double(ratio));
     /**
      * Compressed entry size
      */
     row.addElement(new Long(entry.getCompressedSize()));
     /**
      * Entry path
      */
     row.addElement(path);
     model.addRow(row);
  }
  /**
   * Insert jar entry as a row to a table data model
   */
  protected void addRow(DefaultTableModel model, File entry)
  {
     Vector row = new Vector();
     /**
      * Entry name
      */
     String name = entry.getName();
     String path = entry.getParentFile().getPath();

     row.addElement(name);
     /**
      * Entry date
      */
     SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy hh:mm");
     Date date = new Date(entry.lastModified());
     StringBuffer sb = new StringBuffer();
     sb = formatter.format(date, sb, new FieldPosition(0));
     row.addElement(sb.toString());
     /**
      * Entry size
      */
     row.addElement(new Long(entry.length()));
     row.addElement(path);
     model.addRow(row);
  }
  /**
   * Create toolbar for filters and etc
   */
  public JToolBar getToolBar()
  {
     if (toolbar == null)
     {
        toolbar = new JToolBar();
        toolbar.setOpaque(false);
        WorkspaceGUI gui = null;
        if (Workspace.getUI() instanceof WorkspaceGUI)
        {
          gui = (WorkspaceGUI) Workspace.getUI();
        }
        toolbar.setFloatable(false);

        if (gui != null)
        {
          toolbar.addSeparator();
          JLabel l = new JLabel(LangResource.getString("Directory_") + ":");
          toolbar.add(l);
          toolbar.addSeparator();
          toolbar.add(getFilterBox());
        }
     }
     return toolbar;
  }
  public JComboBox getFilterBox()
  {
     if (c_paths == null)
     {
       c_paths = new JComboBox();
       c_paths.addItemListener(new ItemListener ()
       {
          public void itemStateChanged(ItemEvent e)
          {
              filter.setFilter( (String) c_paths.getSelectedItem() );
          }
       });
     }
     return c_paths;
  }
  protected void updateTable()
  {
     if (new_model != null && paths != null && filter != null)
     {
        /**
         * Set new model to table
         */
        createTable(new_model);
        /**
         * Create helper
         */
        helper = new SortedTableHelper (table);
        /**
         * Set model to combo box
         */
        getFilterBox().setModel(new DefaultComboBoxModel(paths));
        /**
         * Set filter
         */
        helper.setTableModelFilter(filter);
        /**
         * Select first filter
         */
        getFilterBox().setSelectedIndex(0);
        /**
         * Prepare table
         */
        helper.prepareTable();
        /**
         * This try block should avoid all unexected errors
         * from autoresizing. In fact, qflib is really dumb
         * at this point still.
         */
        try
        {
          /**
           * Resize columns
           */
          SwingUtil.autoSizeTableColumn(table, 0);
          SwingUtil.autoSizeTableColumn(table, 1);
          SwingUtil.autoSizeTableColumn(table, 2);
          SwingUtil.autoSizeTableColumn(table, 3);
        }
        catch(Exception ex)
        { // silently ignore
        }
     }
     else
     {
        createTable(new DefaultTableModel(dirColumnNames, 0));
        /**
         * Set model to combo box
         */
        getFilterBox().setModel(new DefaultComboBoxModel(paths));
     }
  }
  public void updateUI()
  {
    super.updateUI();
    if ( helper != null )
    {
      helper.updateUI();
    }
  }
}