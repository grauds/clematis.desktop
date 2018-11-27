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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.util.*;

/** A dialog for presenting an exception to the user. The dialog displays a
 * message as well as the detail of the exception, including its stack trace.
 *
 * <p><center>
 * <img src="snapshot/ExceptionDialog.gif"><br>
 * <i>An example ExceptionDialog.</i>
 * </center>
 *
 * <p><center>
 * <img src="snapshot/ExceptionDialog_2.gif"><br>
 * <i>An example ExceptionDialog with the detail expanded.</i>
 * </center>
 * 
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ExceptionDialog extends ComponentDialog
{
  private KButton b_detail, b_copy;
  private JList l_trace;
  private KPanel p_main, p_detail;
  private JTextField t_exception;
  private KLabelArea l_message;
  private boolean detailShown = false;
  private KScrollPane scroll;
  private static Dimension listSize = new Dimension(350, 150);
  private boolean expandable = true;
  private Throwable throwable;
  
  /** Construct a new modal <code>ExceptionDialog</code> with the specified
   * parent window.
   *
   * @param parent The parent window for the dialog.
   * @param title The title for the dialog window.
   */
  
  public ExceptionDialog(Frame parent, String title)
  {
    super(parent, title, true, false);
  }

  /** Construct a new modal <code>ExceptionDialog</code> with the specified
   * parent window.
   *
   * @param parent The parent window for the dialog.
   * @param title The title for the dialog window.
   * @since Kiwi 2.1
   */
  
  public ExceptionDialog(Dialog parent, String title)
  {
    super(parent, title, true, false);
  }
  
  /** Set the "expandable" mode of the dialog. If the mode is enabled, the
   * dialog will include a "detail" button, which, when clicked, will
   * expand the dialog to display the exception type and stack trace.
   * This mode is enabled by default.
   */

  public void setExpandable(boolean expandable)
  {
    this.expandable = expandable;
    
    b_detail.setVisible(expandable);
  }
  
  /** Build the dialog user interface. */
  
  protected Component buildDialogUI()
  {
    setComment(null);

    ResourceManager resmgr = KiwiUtils.getResourceManager();
    
    setIcon(resmgr.getIcon("dialog_alert.png"));
    setIconPosition(SwingConstants.TOP);

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    
    b_detail = new KButton(loc.getMessage("kiwi.button.detail"),
                           resmgr.getIcon("arrow_expand.png"));

    b_detail.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Object o = evt.getSource();
          
          if(o == b_detail)
          {
            detailShown = !detailShown;
            
            if(detailShown)
              p_main.add("Center", p_detail);
            else
              p_main.remove(p_detail);
            
            pack();
            
            if(detailShown)
            {
              b_detail.setVisible(false);
              addButton(b_copy);
            }
          }
        }
      });
    addButton(b_detail);

    b_copy = new KButton(loc.getMessage("kiwi.button.copy"));
    b_copy.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Object o = evt.getSource();
          
          if(o == b_copy)
          {
            KiwiUtils.setClipboardText(
              KiwiUtils.stackTraceToString(throwable));
          }
        }
      });
    
    p_main = new KPanel();
    p_main.setLayout(new BorderLayout(5, 5));
    
    l_message = new KLabelArea(1, 30);
    p_main.add("North", l_message);
    
    p_detail = new KPanel();
    p_detail.setLayout(new BorderLayout(5, 5));
    
    t_exception = new JTextField();
    t_exception.setFont(KiwiUtils.boldFont);
    t_exception.setOpaque(false);
    t_exception.setEditable(false);
    
    p_detail.add("North", t_exception);
    
    l_trace = new JList();
    l_trace.setFont(KiwiUtils.plainFont);
    scroll = new KScrollPane(l_trace);
    scroll.setSize(listSize);
    scroll.setPreferredSize(listSize);
    
    p_detail.add("Center", scroll);
    
    return(p_main);
  }
    
  /** Set a textual error message and the throwable object to be displayed in
   *  the dialog.
   *
   * @param message The message.
   * @param throwable The throwable.
   */
  
  public void setException(String message, Throwable throwable)
  {
    String tmsg = throwable.getMessage();
    if(tmsg != null)
      message += "\n" + tmsg;
    else
      message += "\n(no message)";

    l_message.setText(message);
    
    l_trace.setListData(throwable.getStackTrace());
    t_exception.setText(throwable.getClass().getName());
    this.throwable = throwable;
  }
  
  /** Show or hide the dialog. */
  
  public void setVisible(boolean flag)
  {
    if(flag)
    {
      detailShown = false;
      p_main.remove(p_detail);
      b_detail.setVisible(expandable);
      removeButton(b_copy);
      pack();

      b_ok.requestFocus();
    }
    
    super.setVisible(flag);
  }
  
}

/* end of source file */
