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
import javax.swing.filechooser.*;

import com.hyperrealm.kiwi.ui.dialog.*;
import com.hyperrealm.kiwi.util.*;

/** A direct replacement for <code>JFileChooser</code> that supports background
 * texturing. All methods behave equivalently to the superclass, with the
 * exception of <code>getSelectedFile()</code>, which now returns
 * <code>null</code> if the <i>Cancel</i> button was pressed.
 *
 * <p><center>
 * <img src="snapshot/KFileChooser.gif"><br>
 * <i>An example KFileChooser.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 1.4
 * @see com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog
 */

public class KFileChooser extends JFileChooser
{
  protected KDialog _dialog = null;
  protected int _returnValue = ERROR_OPTION;

  /**
   */

  public KFileChooser()
  {
    super();

    _init();
  }

  /**
   */
  
  public KFileChooser(File currentDirectory)
  {
    super(currentDirectory);

    _init();
  }

  /**
   */
  
  public KFileChooser(File currentDirectory, FileSystemView fsv)
  {
    super(currentDirectory, fsv);

    _init();
  }

  /**
   */
  
  public KFileChooser(FileSystemView fsv)
  {
    super(fsv);

    _init();
  }

  /**
   */
  
  public KFileChooser(String currentDirectoryPath)
  {
    super(currentDirectoryPath);

    _init();
  }

  /**
   */
  
  public KFileChooser(String currentDirectoryPath, FileSystemView fsv)
  {
    super(currentDirectoryPath, fsv);

    _init();
  }

  /*
   */
  
  private void _init()
  {
    _removeOpacity(this);
  }

  /*
   */
  
  private void _removeOpacity(Component c)
  {
    if(!(c instanceof JComponent))
      return;
    
    if((c instanceof JPanel) || (c instanceof AbstractButton)
       || (c instanceof JComboBox))
      ((JComponent)c).setOpaque(false);

    Container cont = (Container)c;
    
    for(int i = 0; i < cont.getComponentCount(); i++)
      _removeOpacity(cont.getComponent(i));
  }

  /**
   */

  public int showDialog(Component parent, String approveButtonText)
  {
    if(approveButtonText != null)
    {
      setApproveButtonText(approveButtonText);
      setDialogType(CUSTOM_DIALOG);
    }

    Frame frame = ((parent instanceof Frame) ? (Frame)parent
                   : (Frame)SwingUtilities.getAncestorOfClass(Frame.class,
                                                              parent));

    String title = null;

    if(getDialogTitle() != null)
      title = getDialogTitle(); //dialogTitle;
    else
      title = getUI().getDialogTitle(this);

    if(_dialog == null)
      _dialog = new _Dialog(frame, title, true, this);
    
    _dialog.setLocationRelativeTo(parent);

    rescanCurrentDirectory();
 
    _dialog.setVisible(true);

    return(_returnValue);
  }

  /*
   */

  private class _Dialog extends KDialog
  {
    public _Dialog(Frame parent, String title, boolean modal,
                   KFileChooser chooser)
    {
      super(parent, title, modal);

      KPanel p = getMainContainer();
      p.setLayout(new GridLayout(1, 0));
      p.add(chooser);

      pack();
    }
  }

  /**
   */
  
  public void approveSelection()
  {
    // ugly hack because "returnValue" and "dialog" are private in the
    // superclass
    
    super.approveSelection();
    if(_dialog != null)
      _dialog.setVisible(false);
    
    _returnValue = APPROVE_OPTION;
  }

  /**
   */
  
  public void cancelSelection()
  {
    // ugly hack because "returnValue" and "dialog" are private in the
    // superclass

    super.cancelSelection();
    if(_dialog != null)
      _dialog.setVisible(false);

    setSelectedFile(null); // how did they overlook this???
    setSelectedFiles(null);
    _returnValue = CANCEL_OPTION;    
  }

}

/* end of source file */
