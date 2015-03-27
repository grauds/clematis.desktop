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

package com.hyperrealm.kiwi.util;

import java.util.*;

/** A convenience class for maintaining a directory path (that is, an ordered
 * list of directories).
 *
 * @author Mark Lindner
 */

public class DirectoryPath
{
  private String psep;
  private ArrayList<String> _dirs;

  /** Construct a new, empty <code>DirectoryPath</code>. */
      
  public DirectoryPath()
  {
    this(null);
  }

  /** Construct a new <code>DirectoryPath</code> for the given directories.
   *
   * @param dirs An array of directory names.
   */
  
  public DirectoryPath(String dirs[])
  {
    psep = System.getProperty("path.separator");
    _dirs = new ArrayList<String>();
    
    if(dirs != null)
      for(int i = 0; i < dirs.length; i++)
        _dirs.add(dirs[i]);
  }

  /** Prepend a directory to the beginning of the path.
   *
   * @param dir The directory to add.
   */

  public synchronized void prepend(String dir)
  {
    _dirs.add(0, dir);
  }

  /** Prepend a list directories to the beginning of the path. The order of
   * the directories is preserved.
   *
   * @param dirs The directories to add.
   */
  
  public synchronized void prepend(String dirs[])
  {
    for(int i = 0; i < dirs.length; i++)
      _dirs.add(i, dirs[i]);
  }
  
  /** Append a directory to the end of the path.
   *
   * @param dir The directory to add.
   */
  
  public synchronized void append(String dir)
  {
    _dirs.add(dir);
  }

  /** Append a list directories to the end of the path. The order of the
   * directories is preserved.
   *
   * @param dirs The directories to add.
   */

  public synchronized void append(String dirs[])
  {
    for(int i = 0; i < dirs.length; i++)
      _dirs.add(dirs[i]);
  }
  
  /** Get the list of directories for this path.
   *
   * @return An array of directory names.
   */
  
  public synchronized String[] getDirectories()
  {
    String s[] = new String[_dirs.size()];

    return(_dirs.toArray(s));
  }

  /** Convert this path to a string, using the appropriate path separator for
   * this platform.
   */
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    int l = _dirs.size();
    for(int i = 0; i < l; i++)
    {
      if(i > 0)
        sb.append(psep);

      sb.append(_dirs.get(i));
    }

    return(sb.toString());
  }
  
}

/* end of source file */
