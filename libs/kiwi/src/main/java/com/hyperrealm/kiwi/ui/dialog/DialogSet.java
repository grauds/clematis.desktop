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
import java.beans.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.util.*;

/** This class is the Kiwi analogy to the Swing <i>JOptionPane</i>. It provides
 * a simple interface for displaying some of the more commonly-used Kiwi
 * dialogs without requiring any <code>Dialog</code> objects to be
 * instantiated.
 * <p>
 * A placement policy may be assigned to a <code>DialogSet</code>; this
 * policy defines where dialogs will appear relative to their owner
 * <code>Window</code>. If there is no owner, dialogs will appear in the
 * center of the screen.
 * <p>
 * Although each instance of <code>DialogSet</code> may have its own owner
 * <code>Window</code> and placement policy, they will all share singleton
 * instances of <code>KMessageDialog</code>, <code>KQuestionDialog</code>, and
 * <code>KInputDialog</code>.
 * <p>
 * @see com.hyperrealm.kiwi.ui.dialog.KMessageDialog
 * @see com.hyperrealm.kiwi.ui.dialog.KQuestionDialog
 * @see com.hyperrealm.kiwi.ui.dialog.KInputDialog
 *
 * @author Mark Lindner
 */

public class DialogSet implements PropertyChangeListener
{
  private static KInputDialog d_input;
  private static KMessageDialog d_message;
  private static KQuestionDialog d_question;
  private static PropertyChangeSupport support;
  private String s_input, s_message, s_question = "Question";
  private Window _owner = null;
  /** Placement policy. Display dialogs centered within the bounds of their
   * owner <code>Window</code>.
   */
  public static final int CENTER_PLACEMENT = 0;

  /** Placement policy. Display dialogs cascaded off the top-left corner of
   * their owner <code>Window</code>.
   */
  public static final int CASCADE_PLACEMENT = 1;

  private int placement = CENTER_PLACEMENT;
  private static DialogSet defaultSet;
  
  static
  {
    d_input = new KInputDialog(KiwiUtils.getPhantomFrame());
    d_question = new KQuestionDialog(KiwiUtils.getPhantomFrame());
    d_message = new KMessageDialog(KiwiUtils.getPhantomFrame());
    defaultSet = new DialogSet();
  }

  /** Get a reference to the default instance of <code>DialogSet</code>. This
   * instance has no owner <code>Window</code>, and hence its dialogs appear
   * centered on the screen.
   *
   * @return The default (singleton) instance.
   */
  
  public static DialogSet getInstance()
  {
    return(defaultSet);
  }

  /** Set the owner window for this <code>DialogSet</code>.
   *
   * @param owner The new owner window.
   *
   * @since Kiwi 2.0
   */

  public void setOwner(Window owner)
  {
    _owner = owner;
  }
  
  /* Construct a new <code>DialogSet</code> with a phantom frame owner and
   * centered-on-screen placement policy.
   */

  private DialogSet()
  {
    this(null, CENTER_PLACEMENT);
    UIChangeManager.getInstance().addPropertyChangeListener(this);    
  }
  
  /** Construct a new <code>DialogSet</code> with the given owner and
   * placement policy.
   *
   * @param owner The <code>Window</code> that is the owner of this
   * <code>DialogSet</code>.
   * @param placement The placement for dialogs in this
   * <code>DialogSet</code>; one of the numeric constants defined above.
   */
  
  public DialogSet(Window owner, int placement) throws IllegalArgumentException
  {
    if((placement < 0) || (placement > 1))
      throw(new IllegalArgumentException());
    
    _owner = owner;
    this.placement = placement;

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    s_input = loc.getMessage("kiwi.dialog.title.input");
    s_message = loc.getMessage("kiwi.dialog.title.message");
    s_question = loc.getMessage("kiwi.dialog.title.question");
  }
  
  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param prompt The prompt to display in the dialog.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   */

  public String showInputDialog(String prompt)
  {
    return(showInputDialog(s_input, null, prompt, null));
  }

  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param prompt The prompt to display in the dialog.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   */
  
  public String showInputDialog(Window parent, String prompt)
  {
    return(_showInputDialog(parent, s_input, null, prompt, null));
  }
      
  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param prompt The prompt to display in the dialog.
   * @param defaultValue The default value to display in the input field.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   */
  
  public String showInputDialog(String prompt, String defaultValue)
  {
    return(showInputDialog(s_input, null, prompt, defaultValue));
  }

  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The prompt to display in the dialog.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   */

  public synchronized String showInputDialog(String title, Icon icon,
                                             String prompt)
  {
    return(showInputDialog(title, icon, prompt, null));
  }

  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The prompt to display in the dialog.
   * @param defaultValue The default value to display in the input field.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   */
  
  public synchronized String showInputDialog(String title, Icon icon,
                                             String prompt,
                                             String defaultValue)
  {
    return(_showInputDialog(_owner, title, icon, prompt, defaultValue));
  }

  /** Show a <code>KInputDialog</code>. Displays an input dialog and returns
   * when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The prompt to display in the dialog.
   * @param defaultValue The default value to display in the input field.
   * @return The text that was entered in the dialog, or <code>null</code>
   * if the dialog was cancelled.
   *
   * @since Kiwi 2.0
   */
  
  public synchronized String showInputDialog(Window parent, String title,
                                             Icon icon, String prompt,
                                             String defaultValue)
  {
    return(_showInputDialog(parent, title, icon, prompt, defaultValue));
  }

  /* show an input dialog */

  private String _showInputDialog(Window owner, String title, Icon icon,
                                  String prompt, String defaultValue)
  {
    d_input.setTitle((title == null) ? s_input : title);
    d_input.setPrompt(prompt);
    if(icon != null)
      d_input.setIcon(icon);
    KiwiUtils.centerWindow(d_input);
    d_input.setText((defaultValue == null) ? "" : defaultValue);
    _positionDialog(owner, d_input);
    d_input.setVisible(true);
    if(d_input.isCancelled())
      return(null);
    return(d_input.getText());
  }

  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param prompt The prompt to display in the dialog.
   * @param type The question dialog type to display; one of the symbolic
   * constants defined in <code>KQuestionDialog</code>.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   */

  public boolean showQuestionDialog(String prompt, int type)
  {
    return(showQuestionDialog(s_question, null, prompt, type));
  }
  
  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param prompt The prompt to display in the dialog.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   */
  
  public boolean showQuestionDialog(String prompt)
  {
    return(showQuestionDialog(s_question, null, prompt,
                              KQuestionDialog.OK_CANCEL_DIALOG));
  }

  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param prompt The prompt to display in the dialog.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   *
   * @since Kiwi 2.0
   */
  
  public boolean showQuestionDialog(Window parent, String prompt)
  {
    return(showQuestionDialog(parent, s_question, null, prompt,
                              KQuestionDialog.OK_CANCEL_DIALOG));
  }

  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param prompt The prompt to display in the dialog.
   * @param type The question dialog type to display; one of the symbolic
   * constants defined in <code>KQuestionDialog</code>.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   *
   * @since Kiwi 2.0
   */
  
  public boolean showQuestionDialog(Window parent, String prompt, int type)
  {
    return(showQuestionDialog(parent, s_question, null, prompt, type));
  }
  
  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The promopt to display in the dialog.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   */

  public synchronized boolean showQuestionDialog(String title, Icon icon,
                                                 String prompt)
  {
    return(showQuestionDialog(title, icon, prompt,
                              KQuestionDialog.OK_CANCEL_DIALOG));
  }

  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The promopt to display in the dialog.
   * @param type The question dialog type to display; one of the symbolic
   * constants defined in <code>KQuestionDialog</code>.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   */
  
  public synchronized boolean showQuestionDialog(String title, Icon icon,
                                                 String prompt, int type)
  {
    return(_showQuestionDialog(_owner, title, icon, prompt, type));
  }

  /** Show a <code>KQuestionDialog</code>. Displays a question dialog and
   * returns when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param prompt The promopt to display in the dialog.
   * @param type The question dialog type to display; one of the symbolic
   * constants defined in <code>KQuestionDialog</code>.
   * @return The status of the dialog; <code>true</code> if the dialog was
   * accepted or <code>false</code> if it was cancelled.
   *
   * @since Kiwi 2.0
   */
  
  public synchronized boolean showQuestionDialog(Window parent, String title,
                                                 Icon icon, String prompt,
                                                 int type)
  {
    return(_showQuestionDialog(parent, title, icon, prompt, type));
  }
  
  /* show a question dialog */

  private boolean _showQuestionDialog(Window parent, String title, Icon icon,
                                      String prompt, int type)
  {
    d_question.setTitle((title == null) ? s_input : title);
    d_question.setType(type);
    d_question.setMessage(prompt);
    if(icon != null)
      d_question.setIcon(icon);
    KiwiUtils.centerWindow(d_question);
    _positionDialog(parent, d_question);
    d_question.setVisible(true);
    if(d_question.isCancelled())
      return(false);
    return(d_question.getStatus());
  }

  /** Show a <code>KMessageDialog</code>. Displays a message dialog and returns
   * when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param message The prompt to display in the dialog.
   *
   * @since Kiwi 2.0
   */

  public void showMessageDialog(Window parent, String message)
  {
    showMessageDialog(parent, s_message, null, message);
  }

  /** Show a <code>KMessageDialog</code>. Displays a message dialog and returns
   * when the dialog is dismissed.
   *
   * @param message The prompt to display in the dialog.
   */

  public void showMessageDialog(String message)
  {
    showMessageDialog(s_message, null, message);
  }
  
  /** Show a <code>KMessageDialog</code>. Displays a message dialog and returns
   * when the dialog is dismissed.
   *
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param message The prompt to display in the dialog.
   */

  public synchronized void showMessageDialog(String title, Icon icon,
                                             String message)
  {
    showMessageDialog(_owner, title, icon, message);
  }

  /** Show a <code>KMessageDialog</code>. Displays a message dialog and returns
   * when the dialog is dismissed.
   *
   * @param parent The parent window.
   * @param title The title for the dialog window.
   * @param icon The icon to display in the dialog.
   * @param message The prompt to display in the dialog.
   *
   * @since Kiwi 2.0
   */
  
  public synchronized void showMessageDialog(Window parent, String title,
                                             Icon icon, String message)
  {
    _showMessageDialog(parent, title, icon, message);
  }
  
  /* show a message dialog */

  private void _showMessageDialog(Window owner, String title, Icon icon,
                                  String message)
  {
    d_message.setTitle((title == null) ? s_message : title);
    d_message.setMessage(message);
    KiwiUtils.centerWindow(d_message);
    if(icon != null)
      d_message.setIcon(icon);
    _positionDialog(owner, d_message);
    d_message.setVisible(true);
  }

  /* position a dialog */

  private void _positionDialog(Window owner, Dialog dialog)
  {
    if(owner == null)
      KiwiUtils.centerWindow(dialog);
    else
    {
      switch(placement)
      {
        case CENTER_PLACEMENT:
          KiwiUtils.centerWindow(owner, dialog);
          break;
        case CASCADE_PLACEMENT:
          KiwiUtils.cascadeWindow(owner, dialog);
          break;
      }
    }
  }

  /** Respond to property change events.
   */
  
  public void propertyChange(PropertyChangeEvent evt)
  {
    String prop = evt.getPropertyName();

    if(prop.equals(UIChangeManager.TEXTURE_PROPERTY))
    {
      Image img = (Image)evt.getNewValue();
      d_input.setTexture(img);
      d_message.setTexture(img);
      d_question.setTexture(img);
    }
    else if(prop.equals(UIChangeManager.BUTTON_OPACITY_PROPERTY))
    {
      boolean flag = ((Boolean)evt.getNewValue()).booleanValue();
      d_input.setButtonOpacity(flag);
      d_message.setButtonOpacity(flag);
      d_question.setButtonOpacity(flag);
    }
  }

}

/* end of source file */
