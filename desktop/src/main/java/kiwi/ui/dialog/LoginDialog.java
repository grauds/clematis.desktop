/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-2001 Mark A. Lindner

   This file is part of Kiwi.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   mark_a_lindner@yahoo.com
   ----------------------------------------------------------------------------
   $Log: LoginDialog.java,v $
   Revision 1.8  2002/03/08 22:50:15  markl
   Added new methods.

   Revision 1.7  2001/03/20 00:54:55  markl
   Fixed deprecated calls.

   Revision 1.6  2001/03/12 09:56:55  markl
   KLabel/KLabelArea changes.

   Revision 1.5  2001/03/12 05:19:59  markl
   Source code cleanup.

   Revision 1.4  1999/06/03 06:48:58  markl
   Minor tweaks.

   Revision 1.3  1999/04/19 06:06:24  markl
   I18N changes, new constructor.

   Revision 1.2  1999/01/10 03:22:17  markl
   added GPL header & RCS tag
   ----------------------------------------------------------------------------
*/

package kiwi.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/** A general-purpose login dialog. The login dialog includes text fields for
  * the entry of a username and password and <i>OK</i> and <i>Cancel</i>
  * buttons. Validation is performed using the provided
  * <code>LoginValidator</code>. If a validation succeeds, the login dialog
  * disappears. Otherwise, a warning dialog is displayed, and once it's
  * dismissed by the user, control returns to the login dialog. The dialog is
  * unconditionally modal.
  *
  * <p><center>
  * <img src="snapshot/LoginDialog.gif"><br>
  * <i>An example LoginDialog.</i>
  * </center>
  *
  * @author Mark Lindner
  * @author PING Software Group
  */

public class LoginDialog extends ComponentDialog
  {
  private LoginValidator validator;
  private JTextField t_user;
  private JPasswordField t_passwd;
  private String loginFailedMessage;
  private KLabel l_username, l_password;

  /** Construct a new <code>LoginDialog</code> with a default title.
    *
    * @param parent The parent frame.
    * @param comment A comment string to display in the upper portion of the
    * window.
    * @param validator The validator to check username/password pairs against.
    */

  public LoginDialog(Frame parent, String comment, LoginValidator validator)
    {
    this(parent, "", comment, null, validator, true);
    }

  /** Construct a new <code>LoginDialog</code>.
    *
    * @param parent The parent frame.
    * @param title The dialog window's title.
    * @param comment A comment string to display in the upper portion of the
    * window.
    * @param validator The validator to check username/password pairs against.
    */

  public LoginDialog(Frame parent, String title, String comment,
                     LoginValidator validator)
   {
     this(parent, title, comment, null, validator, true);
   }
  /*
   */
  public LoginDialog(Frame parent, String title, String comment,
             Icon icon,
           LoginValidator validator)
  {
     this(parent, title, comment, icon, validator, true);
  }
  /*
   */
  public LoginDialog(Frame parent, String title, String comment,
             Icon icon,
           LoginValidator validator, boolean has_cancel)
  {
    super(parent, title, true, has_cancel);
    this.validator = validator;
    setResizable(false);
    if ( icon != null )
    {
        setTopIcon(icon);
    }
    setComment(comment);
    setIcon(KiwiUtils.getResourceManager().getIcon("keylock.gif"));
    setOpaque(true);
  }
  protected JComponent buildDialogUI()
    {
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    loginFailedMessage = loc.getMessage("kiwi.dialog.message.login_failed");

    KPanel main = new KPanel();
    GridBagLayout gb = new GridBagLayout();
    main.setLayout(gb);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = 1;
    Insets insets0 = new Insets(0, 0, 5, 5);
    Insets insets1 = new Insets(0, 0, 5, 0);

    l_username = new KLabel(loc.getMessage("kiwi.dialog.prompt.username"));
    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbc.insets = insets0;
    main.add(l_username, gbc);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    t_user = new JTextField(10);
    t_user.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent evt)
        {
        t_passwd.requestFocus();
        }
      });
    gbc.insets = insets1;
    main.add(t_user, gbc);

    gbc.gridwidth = GridBagConstraints.RELATIVE;
    gbc.weightx = 0;
    gbc.insets = insets0;
    l_password = new KLabel(loc.getMessage("kiwi.dialog.prompt.password"));
    main.add(l_password, gbc);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    gbc.insets = insets1;
    t_passwd = new JPasswordField(10);
    registerTextInputComponent(t_passwd);
    main.add(t_passwd, gbc);

    if(getTitle().length() == 0)
      setTitle(loc.getMessage("kiwi.dialog.title.login"));

    return(main);
    }

  /*
   */

  protected boolean accept()
    {
    boolean ok = validator.validate(t_user.getText(),
                                    new String(t_passwd.getPassword()));
    if(ok)
      return(true);

    JOptionPane.showMessageDialog(this, loginFailedMessage);
    return(false);
    }

  /*
   */

  protected void cancel()
    {
    validator.validationCancelled();
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
