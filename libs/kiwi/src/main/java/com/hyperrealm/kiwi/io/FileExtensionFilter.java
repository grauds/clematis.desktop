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

package com.hyperrealm.kiwi.io;

import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * A convenience implementation of <code>FileFilter</code> that filters files
 * based on their filename extensions.
 * <p>
 * Example: Create a new filter that filters out all files except those
 * whose filenames end in '.gif' or '.jpg'.
 * <p>
 * <pre>
 *     JFileChooser chooser = new JFileChooser();
 *     FileExtensionFilter filter = new FileExtensionFilter(
 *       new String[] {"gif", "jpg"}, "GIF & JPEG Images");
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 * </pre>
 *
 * @author Mark Lindner
 */

public class FileExtensionFilter extends FileFilter
{
  private HashMap<String, FileFilter> filters = null;
  private String description = null;
  private String fullDescription = null;
  private boolean useExtensionsInDescription = true;

  /**
   * Construct a new <code>FileExtensionFilter</code>. If no extensions are
   * added to this filter, then all files will be accepted.
   */

  public FileExtensionFilter()
  {
    filters = new HashMap<String, FileFilter>();
  }

  /**
   * Construct a new <code>FileExtensionFilter</code> that accepts files with
   * the given extension. For example:
   * <p>
   * <code>new FileExtensionFilter("jpg");</code>
   *
   * @param extension The extension.
   */
  
  public FileExtensionFilter(String extension)
  {
    this(extension, null);
  }

  /** Construct a new <code>FileExtensionFilter</code> that accepts the given
   * file type. For example:
   * <p>
   * <code>new FileExtensionFilter("jpg", "JPEG Image Images");</code>
   * <p>
   * Note that the '.' is not part of the extension and should not be
   * included.
   *
   * @param extension The extension.
   * @param description A description of the extension.
   */
  
  public FileExtensionFilter(String extension, String description)
  {
    this();
    if(extension != null)
      addExtension(extension);

    this.description = description;
  }

  /** Construct a new <code>FileExtensionFilter</code> that accepts the given
   * extensions. For example:
   * <p>
   * <code>
   * new FileExtensionFilter(String {"gif", "jpg"});
   * </code>
   * <p>
   * Note that the '.' is not part of the extension and should not be
   * included.
   *
   * @param extensions An array of extensions.
   */
  
  public FileExtensionFilter(String[] extensions)
  {
    this(extensions, null);
  }

  /** Construct a new <code>FileExtensionFilter</code> that accepts the given
   * extensions. For example:
   * <p>
   * <code>
   * new FileExtensionFilter(new String[] {"gif", "jpg"}, "Image Files");
   * </code>
   * <p>
   * Note that the '.' is not part of the extension and should not be
   * included.
   *
   * @param extensions An array of extensions.
   * @param description A description for these extensions.
   */
  
  public FileExtensionFilter(String[] extensions, String description)
  {
    this();
    for(int i = 0; i < extensions.length; i++)
      addExtension(extensions[i]);

    this.description = description;
  }

  /** Filter a file. Determines if the file ends in one of the extensions
   * that this object is filtering on.
   *
   * @param f The <code>File</code> to filter.
   * @return <code>true</code> if the file ends in one of the
   * extensions that this object is filtering, and <code>false</code>
   * if it does not or if it is a hidden file (a file whose name
   * begins with '.').
   */
  
  public boolean accept(File f)
  {
    if(f != null)
    {
      if(f.isDirectory())
        return(true);

      String extension = getExtension(f);
      if((extension != null) && (filters.get(extension) != null))
        return(true);
    }
    
    return(false);
  }

  /** Get the extension portion of a file's name.
   *
   * @param f The file.
   * @return The extension (not including the '.').
   */
  
  private String getExtension(File f)
  {
    if(f != null)
    {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');
      if((i > 0) && (i < filename.length() - 1))
        return(filename.substring(i + 1).toLowerCase());
    }
    
    return(null);
  }

  /** Adds an extension to filter against.
   * <p>
   * For example, the following code will create a filter that accepts only
   * files whose names end with ".jpg" or ".gif":
   * <p>
   * <code>
   *   FileExtensionFilter filter = new FileExtensionFilter();
   *   filter.addExtension("jpg");
   *   filter.addExtension("gif");
   * </code>
   * <p>
   * Note that the '.' is not part of the extension and should not be
   * included.
   *
   * @param extension The extension to add.
   */
  
  public void addExtension(String extension)
  {
    filters.put(extension.toLowerCase(), this);
    fullDescription = null;
  }

  /** Get the description of this filter
   *
   * @return The description.
   */
  
  public String getDescription()
  {
    if(fullDescription == null)
    {
      if(description == null || isExtensionListInDescription())
      {
        StringBuilder sb = new StringBuilder();
        if(description != null)
        {
          sb.append(description);
          sb.append(' ');
        }

        sb.append('(');
        Iterator<String> iter = filters.keySet().iterator();
        boolean first = true;

        while(iter.hasNext())
        {
          String ext = iter.next();
          if(! first)
            sb.append(", ");

          first = false;
          sb.append('.');
          sb.append(ext);
        }

        sb.append(')');

        fullDescription = sb.toString();
      }
      else
        fullDescription = description;
    }
    
    return(fullDescription);
  }

  /** Set the description for this filter.
   *
   * @param description The new description.
   */
  
  public void setDescription(String description)
  {
    this.description = description;
    fullDescription = null;
  }

  /** Specify whether the extension list should appear as part of the
   * description. Only relevant if a description was provided in the
   * constructor or via <code>setDescription()</code>.
   *
   * @param flag A flag specifying whether or not the extensions should be
   * listed in the description.
   */
  
  public void setExtensionListInDescription(boolean flag)
  {
    useExtensionsInDescription = flag;
    fullDescription = null;
  }

  /** Determine whether the extension list will appear as part of the
   * description.
   *
   * @return <code>true</code> if the list will appear in the description, and
   * <code>false</code> otherwise.
   */
  
  public boolean isExtensionListInDescription()
  {
    return(useExtensionsInDescription);
  }

}

/* end of source file */
