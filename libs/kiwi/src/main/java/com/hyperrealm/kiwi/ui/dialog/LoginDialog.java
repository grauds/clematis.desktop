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
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.util.*;
import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.event.*;

/** A general-purpose login dialog. The login dialog includes text
 * fields for the entry of a username and password and <i>OK</i> and
 * <i>Cancel</i> buttons. Validation is performed in the method
 * <code>validate()</code>, which should be overridden by subclassers
 * to provide the desired validation logic. If a validation succeeds,
 * the login dialog disappears. Otherwise, a warning dialog is
 * displayed, and once it's dismissed by the user, control returns to
 * the login dialog. The dialog is unconditionally modal.
 *
 * <p><center>
 * <img src="snapshot/LoginDialog.gif"><br>
 * <i>An example LoginDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class LoginDialog extends ComponentDialog
{
  /** The username text field. */
  protected JTextField t_user;
  /** The password text field. */
  protected JPasswordField t_passwd;

  private DialogSet dialogs;
  private String loginFailedMessage;
  private KLabel l_username, l_password;

  /** Construct a new <code>LoginDialog</code> with a default title.
   *
   * @param parent The parent frame.
   * @param comment A comment string to display in the upper portion of the
   * window.
   */

  public LoginDialog(Frame parent, String comment)
  {
    this(parent, "", comment);
  }
  
  /** Construct a new <code>LoginDialog</code>.
   *
   * @param parent The parent frame.
   * @param title The dialog window's title.
   * @param comment A comment string to display in the upper portion of the
   * window.
   */

  public LoginDialog(Frame parent, String title, String comment)
  {
    super(parent, title, true);
    setResizable(false);

    setComment(comment);
    
    setIcon(KiwiUtils.getResourceManager().getIcon("key.png"));
    
    dialogs = new DialogSet(this, DialogSet.CENTER_PLACEMENT);
  }

  /*
   */

  protected Component buildDialogUI()
  {
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    loginFailedMessage = loc.getMessage("kiwi.dialog.message.login_failed");
    
    KPanel main = new KPanel();
    GridBagLayout gb = new GridBagLayout();
    main.setLayout(gb);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = gbc.NORTHWEST;
    gbc.fill = gbc.HORIZONTAL;
    gbc.gridwidth = 1;
    Insets insets0 = new Insets(0, 0, 5, 5);
    Insets insets1 = new Insets(0, 0, 5, 0);
    
    l_username = new KLabel(loc.getMessage("kiwi.dialog.prompt.username"));
    gbc.gridwidth = gbc.RELATIVE;
    gbc.insets = insets0;
    main.add(l_username, gbc);

    gbc.gridwidth = gbc.REMAINDER;
    gbc.weightx = 1;
    t_user = new JTextField(15);
    t_user.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          t_passwd.requestFocus();
        }
      });
    gbc.insets = insets1;
    main.add(t_user, gbc);

    gbc.gridwidth = gbc.RELATIVE;
    gbc.weightx = 0;
    gbc.insets = insets0;    
    l_password = new KLabel(loc.getMessage("kiwi.dialog.prompt.password"));
    main.add(l_password, gbc);

    gbc.gridwidth = gbc.REMAINDER;
    gbc.weightx = 1;
    gbc.insets = insets1;    
    t_passwd = new JPasswordField(15);
    registerTextInputComponent(t_passwd);
    main.add(t_passwd, gbc);

    if(getTitle().length() == 0)
      setTitle(loc.getMessage("kiwi.dialog.title.login"));
    
    return(main);
  }

  /** Perform the login validation. The default implementation returns
   * <b>true</b>.
   *
   * @param username The username that was entered.
   * @param password The password that was entered.
   * @return <b>true</b> if the validation succeeded, <b>false</b> otherwise.
   */

  protected boolean validate(String username, String password)
  {
    return(true);
  }

  /*
   */
  
  protected boolean accept()
  {
    boolean ok = validate(t_user.getText(),
                          new String(t_passwd.getPassword()));
    if(ok)
      return(true);

    dialogs.showMessageDialog(loginFailedMessage);
    t_user.requestFocus();
    return(false);
  }

  /** Show or hide the dialog. */

  public void setVisible(boolean flag)
  {
    if(flag)
    {
      t_user.setText("");
      t_passwd.setText("");
      t_user.requestFocus();
    }
    super.setVisible(flag);
  }

  /** Set the text for the username prompt label.
   *
   * @param text The new text for the label
   * @since Kiwi 1.3.3
   */

  public void setUsernamePromptText(String text)
  {
    if(text == null)
      return;

    l_username.setText(text);
    pack();
  }
  
  /** Set the text for the password prompt label.
   *
   * @param text The new text for the label
   * @since Kiwi 1.3.3
   */

  public void setPasswordPromptText(String text)
  {
    if(text == null)
      return;
    
    l_password.setText(text);
    pack();
  }  
  
}

/* end of source file */
