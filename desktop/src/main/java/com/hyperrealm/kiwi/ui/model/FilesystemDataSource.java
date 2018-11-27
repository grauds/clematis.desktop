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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Icon;

import com.hyperrealm.kiwi.util.*;

/** An implementation of
 * {@link com.hyperrealm.kiwi.ui.model.TreeDataSource TreeDataSource}
 * wherein tree nodes represent files in the local filesystem. The
 * <code>ignoreFiles</code> argument of some forms of the constructor allows
 * for the creation of directory-only data sources. These are useful for
 * driving a directory chooser, for example.
 *
 * @see java.io.File
 *
 * @author Mark Lindner
 */

public class FilesystemDataSource implements TreeDataSource<Object>
{
  private FileRoot root;
  private Date date = new Date();
  private static final Icon FOLDER_OPEN_ICON = KiwiUtils.getResourceManager()
    .getIcon("folder_page.png");
  private static final Icon FOLDER_CLOSED_ICON = KiwiUtils.getResourceManager()
    .getIcon("folder.png");
  private static final Icon FOLDER_LOCKED_ICON = KiwiUtils.getResourceManager()
    .getIcon("folder_locked.png");
  private static final Icon DOCUMENT_ICON = KiwiUtils.getResourceManager()
    .getIcon("document_blank.png");
  private static final Icon COMPUTER_ICON = KiwiUtils.getResourceManager()
    .getIcon("computer.png");
  private boolean ignoreFiles = false;
  private static final String columns[];
  private LocaleManager lm;
  private static final String[] emptyList = new String[0];
  private static final String FILE_COLUMN, SIZE_COLUMN, DATE_COLUMN,
    TIME_COLUMN;
  private static final Class types[] = new Class[] { String.class,
                                                     String.class,
                                                     String.class,
                                                     String.class };
  private static final String ALL_FILESYSTEMS;

  /*
   */
  
  static
  {
    LocaleManager lm = LocaleManager.getDefault();
    LocaleData loc = lm.getLocaleData("KiwiMisc");

    FILE_COLUMN = loc.getMessage("kiwi.column.file");
    SIZE_COLUMN = loc.getMessage("kiwi.column.size");
    DATE_COLUMN = loc.getMessage("kiwi.column.date");
    TIME_COLUMN = loc.getMessage("kiwi.column.time");
    ALL_FILESYSTEMS = loc.getMessage("kiwi.label.all_filesystems");

    columns = new String[] { FILE_COLUMN, SIZE_COLUMN, DATE_COLUMN,
                             TIME_COLUMN };
      
  }
  
  /** Construct a new <code>FilesystemDataSource</code> with roots
   * for all available filesystems.
   *
   * @since Kiwi 2.0
   */
  
  public FilesystemDataSource()
  {
    _init(new FileRoot(), false);
  }
  
  /** Construct a new <code>FilesystemDataSource</code> wiht the given roots.
   *
   * @param roots The filesystem roots for this datasource.
   * @since Kiwi 2.0
   */

  public FilesystemDataSource(File roots[])
  {
    _init(new FileRoot(roots), false);
  }

  /** Construct a new <code>FilesystemDataSource</code> with roots
   * for all available filesystems.
   *
   * @param ignoreFiles A flag specifying whether ordinary files
   * (non-directories) should be ignored or displayed.
   */

  public FilesystemDataSource(boolean ignoreFiles)
  {
    _init(new FileRoot(), ignoreFiles);
  }

  /** Construct a new <code>FilesystemDataSource</code>.
   *
   * @param root The root directory.
   * @param ignoreFiles A flag specifying whether ordinary files
   * (non-directories) should be ignored or displayed.
   *
   * @exception IllegalArgumentException if <code>root</code> is not
   * a directory.
   */

  public FilesystemDataSource(File root, boolean ignoreFiles)
  {
    if(root != null && !root.isDirectory())
      throw(new IllegalArgumentException("Root must be a directory!"));

    _init(new FileRoot(root), ignoreFiles);
  }

  /** Construct a new <code>FilesystemDataSource</code>.
   *
   * @param roots The root directories.
   * @param ignoreFiles A flag specifying whether ordinary files
   * (non-directories) should be ignored or displayed.
   *
   * @exception IllegalArgumentException if <code>root</code> is not
   * a directory.
   *
   * @since Kiwi 2.0
   */

  public FilesystemDataSource(File roots[], boolean ignoreFiles)
  {
    for(int i = 0; i < roots.length; i++)
      if(!roots[i].isDirectory())
        throw(new IllegalArgumentException("Roots must be directories!"));

    _init(new FileRoot(roots), ignoreFiles);
  }
  
  /*
   */
  
  private void _init(FileRoot root, boolean ignoreFiles)
  {
    this.ignoreFiles = ignoreFiles;
    this.root = root;

    lm = LocaleManager.getDefault();
  }

  /** Get the root object.
   *
   * @return The <code>FileRoot</code> "virtual root" object which is
   * the parent of the root files with which this data source was
   * created.
   */

  public Object getRoot()
  {
    return(root);
  }

  /** Get the children of a given node. */

  public Object[] getChildren(Object node)
  {
    if(node.getClass() == FileRoot.class)
      return(((FileRoot)node).getRoots());
      
    File f = (File)node;
    String[] children = emptyList;

    try { children = f.list(); } catch(Exception e) { }

    Arrays.sort(children);
    
    return(makeNodes(f, children));
  }

  /* create an array of Files from an array of filenames */

  private File[] makeNodes(File parent, String[] list)
  {
    File f;
    ArrayList<File> v = new ArrayList<File>();

    for(int i = 0; i < list.length; i++)
    {
      if(parent == null)
        f = new File(list[i]);
      else
        f = new File(parent, list[i]);

      if(ignoreFiles && !f.isDirectory())
        continue;
      v.add(f);
    }

    File nodes[] = new File[v.size()];
    return(v.toArray(nodes));
  }

  /*
   */

  public String getLabel(Object node)
  {
    if(node.getClass() == FileRoot.class)
      return(ALL_FILESYSTEMS);

    File f = (File)node;
    if(root.isRoot(f))
      return(f.getPath());
    else
      return(f.getName());
  }

  /*
   */

  public Icon getIcon(Object node, boolean isExpanded)
  {
    if(node.getClass() == FileRoot.class)
      return(COMPUTER_ICON);

    File f = (File)node;

    if(f.isDirectory())
    {
      if(! f.canRead())
        return(FOLDER_LOCKED_ICON);

      return(isExpanded ? FOLDER_OPEN_ICON : FOLDER_CLOSED_ICON);
    }
    else
      return(DOCUMENT_ICON);
  }

  /*
   */
  
  public boolean isExpandable(Object node)
  {
    if(node.getClass() == FileRoot.class)
      return(true);

    File f = (File)node;

    return(f.isDirectory() && f.canRead());
  }

  /** Get the value for a given property. */

  public Object getValueForProperty(Object node, String property)
  {
    if(node == null)
    {
      if(property.equals(COLUMN_NAMES_PROPERTY))
        return(columns);
      else if(property.equals(COLUMN_TYPES_PROPERTY))
        return(types);
      else
        return(null);
    }

    if(! (node instanceof File))
      return(null);

    File f = (File)node;

    if(property.equals(FILE_COLUMN))
      return(f);
    
    else if(property.equals(SIZE_COLUMN))
    {
      long len = f.length();
      
      len = (len + 1023) / 1024;
      if(len < 1024)
        return(lm.formatInteger(len, true) + " Kb");

      len = (len + 1023) / 1024;
      return(lm.formatInteger(len, true) + " Mb");
    }
    
    else if(property.equals(DATE_COLUMN))
    {
      date.setTime(f.lastModified());
      return(lm.formatDate(date, lm.MEDIUM));
    }
    
    else if(property.equals(TIME_COLUMN))
    {
      date.setTime(f.lastModified());
      return(lm.formatTime(date, lm.SHORT));
    }
    
    else
      return(null);
  }

  /** A virtual filesystem root. This object encapsulates the
   * collection of filesystem roots. (Some platforms, namely Windows, have
   * more than one root directory in the filesystem.)
   */

  public final class FileRoot
  {
    File roots[];
    
    FileRoot()
    {
      roots = File.listRoots();
    }

    FileRoot(File roots[])
    {
      this.roots = roots;
    }

    FileRoot(File root)
    {
      if(root != null)
      {
        roots = new File[1];
        roots[0] = root;
      }
      else
        roots = File.listRoots();
    }

    public boolean isRoot(File file)
    {
      for(int i = 0; i < roots.length; i++)
      {
        if(roots[i] == file)
          return(true);
      }

      return(false);
    }

    /** Get the list of roots. */
    
    public File[] getRoots()
    {
      return(roots);
    }

    /** Get the string representation of this object.
     *
     * @return The empty string.
     */
    
    public String toString()
    {
      return("");
    }
  }
  
}

/* end of source file */
