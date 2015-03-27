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

import java.io.*;

/** An interface for receiving <code>File</code> objects from a
 * <code>FilesystemTraverser</code>.
 *
 * @see com.hyperrealm.kiwi.io.FilesystemTraverser
 * @author Mark Lindner
 */

public interface FileConsumer
{

  /** Accept a file from a depth-first traversal.
   *
   * @param file The file.
   * @return <code>true</code> if the traversal should continue, and
   * <code>false</code> if it should be aborted.
   */

  public boolean accept(File file);

  /** Handle a file access error from a depth-first traversal.
   *
   * @param file The file that caused the access error.
   * @return <code>true</code> if the traversal should continue, and
   * <code>false</code> if it should be aborted.
   */

  public boolean accessError(File file);
}

/* end of source file */
