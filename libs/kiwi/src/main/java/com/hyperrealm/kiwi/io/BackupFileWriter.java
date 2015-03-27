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

/** A file writer that safely overwrites an existing file. When
 * the stream is created, a temporary file is opened for writing. The name
 * of the temporary file is simply <b><i>filename</i>.tmp</b>, where
 * <i>filename</i> is the file to be written. When the stream is closed,
 * the following steps are taken:
 *
 * <ol>
 * <li>If the original file exists, it is renamed to
 * <b><i>filename</i>.bak</b>.
 * <li>The temporary file is renamed to the original filename.
 * </ol>
 *
 * These steps ensure that the original file is not clobbered during the
 * update, and that a previous version of the file is always available.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 * @see com.hyperrealm.kiwi.io.BackupFileOutputStream
 */

public class BackupFileWriter extends FileWriter
{
  private File tempFile, backupFile;
  private File file;
  private boolean closed = false;

  /** Create a new <code>BackupFileWriter</code> for the given file.
   *
   * @param file The file to write.
   * @throws java.io.IOException If the temporary file could not be opened for
   * writing.
   */
  
  public BackupFileWriter(File file) throws IOException
  {
    super(getTempFile(file));
    
    this.file = file;
    
    String path = file.getAbsolutePath();
    tempFile = new File(path + ".tmp");
    backupFile = new File(path + ".bak");
  }

  /** Create a new <code>BackupFileWriter</code> for the given file.
   *
   * @param file The path of the file to write.
   * @throws java.io.IOException If the temporary file could not be opened for
   * writing.
   */
  
  public BackupFileWriter(String file) throws IOException
  {
    this(new File(file));
  }

  /** Close the writer.
   *
   * @throws java.io.IOException If an I/O error occurs.
   */

  public final void close() throws IOException
  {
    if(closed)
      return;
    
    super.close();

    if(backupFile.exists())
      backupFile.delete();

    if(file.exists())
      if(! file.renameTo(backupFile))
        throw(new IOException("Unable to move file to backup file"));

    if(! tempFile.renameTo(file))
      throw(new IOException("Unable to move temp file to original file"));

    closed = true;
  }

  /*
   */

  private static File getTempFile(File file)
  {
    String path = file.getAbsolutePath();
    return(new File(path + ".tmp"));
  }
  
}

/* end of source file */
