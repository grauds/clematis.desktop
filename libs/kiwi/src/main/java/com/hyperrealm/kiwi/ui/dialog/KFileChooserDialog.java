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

package com.hyperrealm.kiwi.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.event.*;

/** A standard Kiwi dialog wrapper for the <code>KFileChooser</code>
 * component. This dialog behaves like other Kiwi dialogs and should be used
 * in place of <code>KFileChooser.showDialog()</code>.
 *
 * <p><center>
 * <img src="snapshot/KFileChooserDialog.gif"><br>
 * <i>An example KFileChooserDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KFileChooser
 * @since Kiwi 1.4
 */

public class KFileChooserDialog extends KDialog implements ActionListener
{
  /** The <code>KFileChooser</code> used by this dialog. */
  protected KFileChooser fileChooser;

  /** Construct a new modal <code>KFileChooserDialog</code> with the specified
   * parent window and title.
   *
   * @param parent The parent window for the dialog.
   * @param title The title for the dialog.
   * @param type The dialog type; one of the constants
   * <code>OPEN_DIALOG</code>, <code>SAVE_DIALOG</code>, or
   * <code>CUSTOM_DIALOG</code>, which are defined in
   * <code>KFileChooser</code>.
   */
  
  public KFileChooserDialog(Frame parent, String title, int type)
  {
    super(parent, title, true);

    _init(type);
  }

  /** Construct a new modal <code>KFileChooserDialog</code> with the specified
   * parent dialog and title.
   *
   * @param parent The parent dialog.
   * @param title The title for the dialog.
   * @param type The dialog type; one of <code>OPEN_DIALOG</code>,
   * <code>SAVE_DIALOG</code>, or <code>CUSTOM_DIALOG</code>.
   */
  
  public KFileChooserDialog(Dialog parent, String title, int type)
  {
    super(parent, title, true);

    _init(type);
  }

  /** Get a reference to the <code>KFileChooser</code> used by this dialog.
   *
   * @return The <code>KFileChooser</code> instance.
   */
  
  public KFileChooser getFileChooser()
  {
    return(fileChooser);
  }

  /*
   */
  
  private void _init(int type)
  {
    fileChooser = new KFileChooser();

    KPanel panel = getMainContainer();
    panel.setLayout(new GridLayout(1, 0));
    panel.add(fileChooser);

    fileChooser.setDialogType(type);
    fileChooser.addActionListener(this);

    // pack();  // this causes deadlock on OS X
  }

  /**
   */

  public void setVisible(boolean flag)
  {
    if(flag)
      pack();

    super.setVisible(flag);
  }
  

  /** A convenience method to set the current directory. Delegates to the
   * embedded <code>KFileChooser</code>.
   *
   * @since Kiwi 2.0
   */

  public void setCurrentDirectory(File dir)
  {
    fileChooser.setCurrentDirectory(dir);
  }
  
  /** A convenience method to enable or disable multiple selection. Delegates
   * to the embedded <code>KFileChooser</code>.
   *
   * @since Kiwi 2.0
   */

  public void setMultiSelectionEnabled(boolean flag)
  {
    fileChooser.setMultiSelectionEnabled(flag);
  }

  /** A convenience method to set the file selection mode. Delegates to the
   * embedded <code>KFileChooser</code>.
   *
   * @since Kiwi 2.0
   */

  public void setFileSelectionMode(int mode)
  {
    fileChooser.setFileSelectionMode(mode);
  }
  
  /** A convenience method to get the currently selected file. Delegates to
   * the embedded <code>KFileChooser</code>.
   */

  public File getSelectedFile()
  {
    return(fileChooser.getSelectedFile());
  }

  /** A convenience method to get the currently selected files. Delegates to
   * the embedded <code>KFileChooser</code>.
   *
   * @since Kiwi 2.0
   */

  public File[] getSelectedFiles()
  {
    if(fileChooser.isMultiSelectionEnabled())
      return(fileChooser.getSelectedFiles());
    else
    {
      File files[] = new File[1];
      files[0] = fileChooser.getSelectedFile();
      return(files);
    }
  }
  
  /** A convenience method to set the currently selected file. Delegates to
   * the embedded <code>KFileChooser</code>.
   */

  public void setSelectedFile(File file)
  {
    fileChooser.setSelectedFile(file);
  }

  /** This method is public as an implementation side-effect.
   */
  
  public void actionPerformed(ActionEvent evt)
  {
    String command = evt.getActionCommand();

    if(command.equals(KFileChooser.APPROVE_SELECTION))
      doAccept();
    else if(command.equals(KFileChooser.CANCEL_SELECTION))
      doCancel();
  }
  
}

/* end of source file */
