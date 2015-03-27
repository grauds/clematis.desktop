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

package com.hyperrealm.kiwi.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.ui.dialog.*;
import com.hyperrealm.kiwi.util.*;

/** A text entry component for entering a filename. The component consists of
 * a text field and a ``browse'' button which activates a file chooser dialog
 * when clicked.
 *
 * <p><center>
 * <img src="snapshot/FileChooserField.gif"><br>
 * <i>An example FileChooserField.</i>
 * </center>
 * <p>
 *
 * @author Mark Lindner
 *
 * @since Kiwi 2.0
 *
 * @see com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog
 */

public class FileChooserField extends KPanel implements Editor<File>
{
  protected KTextField t_file;
  private KButton b_browse;
  private KFileChooserDialog d_file;

  /** Construct a new <code>FileChooserField</code>.
   *
   * @param width The width for the field.
   * @param maxLength The maximum length of input allowed in the field.
   * @param dialog The file chooser dialog to use when browsing for a file.
   */
  
  public FileChooserField(int width, int maxLength, KFileChooserDialog dialog)
  {
    d_file = dialog;
    
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    setLayout(new BorderLayout(5, 5));
    
    t_file = new KTextField(width);
    t_file.setMaximumLength(maxLength);
    add("Center", t_file);

    b_browse = new KButton(KiwiUtils.getResourceManager()
                           .getIcon("folder_magnify.png"));
    b_browse.setMargin(new Insets(3, 3, 3, 3));
    b_browse.setToolTipText(loc.getMessage("kiwi.tooltip.select_file"));
    
    b_browse.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          browse();
        }
      });

    add("East", b_browse);
  }

  /**
   */

  public final void requestFocus()
  {
    t_file.requestFocus();
  }

  /**
   */

  public final boolean validateInput()
  {
    return(t_file.validateInput());
  }

  /**
   */

  public final void setEnabled(boolean enabled)
  {
    t_file.setEnabled(enabled);
    b_browse.setEnabled(enabled);
  }

  /**
   */

  public final JComponent getEditorComponent()
  {
    return(this);
  }

  /**
   */

  private void browse()
  {
    KiwiUtils.centerWindow(this, d_file);
    d_file.setVisible(true);

    if(d_file.isCancelled())
      return;

    File file = d_file.getSelectedFile();
    t_file.setText(file.getAbsolutePath());
  }

  /**
   * Get the file that is currently displayed in the field.
   *
   * @return The file.
   */
  
  public File getFile()
  {
    String s = t_file.getText().trim();
    if(s.equals(""))
      return(null);
    
    return(new File(s));
  }

  /**
   * Set the file to be displayed in the field.
   *
   * @param file The new file.
   */
  
  public void setFile(File file)
  {
    t_file.setText((file == null) ? null : file.getAbsolutePath());
  }

  /** Get the object being edited. Equivalent to <code>getFile()</code>.
   */

  public File getObject()
  {
    return(getFile());
  }

  /** Set the object being edited. Equivalent to <code>setFile()</code>.
   */

  public void setObject(File obj)
  {
    setFile(obj);
  }

  /**
   * Clear the field.
   */

  public void clear()
  {
    setFile(null);
  }

  /** Add a <code>ChangeListener</code> to this component's list of listeners.
   * <code>ChangeEvent</code>s are fired when the text field's document model
   * changes.
   *
   * @param listener The listener to add.
   * @since Kiwi 2.1.1
   */

  public void addChangeListener(ChangeListener listener)
  {
    t_file.addChangeListener(listener);
  }

  /** Remove a <code>ChangeListener</code> from this component's list
   * of listeners.
   *
   * @param listener The listener to remove.
   * @since Kiwi 2.1.1
   */
  
  public void removeChangeListener(ChangeListener listener)
  {
    t_file.removeChangeListener(listener);
  }

}

/* end of source file */
