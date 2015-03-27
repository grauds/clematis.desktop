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
   $Log: ComponentDialog.java,v $
   Revision 1.11  2002/05/20 14:20 tysinsh
   Added key handler and improved layout.

   Revision 1.10  2001/03/20 00:54:54  markl
   Fixed deprecated calls.

   Revision 1.9  2001/03/12 09:56:55  markl
   KLabel/KLabelArea changes.

   Revision 1.8  2001/03/12 05:19:57  markl
   Source code cleanup.

   Revision 1.7  2000/12/18 23:37:29  markl
   Added new constructors.

   Revision 1.6  2000/10/11 10:44:26  markl
   Fixed close window logic.

   Revision 1.5  1999/06/03 06:47:15  markl
   Added canCancel() method.

   Revision 1.4  1999/04/19 06:00:12  markl
   I18N changes.

   Revision 1.3  1999/02/07 08:09:16  markl
   made setComment() public

   Revision 1.2  1999/01/10 03:22:17  markl
   added GPL header & RCS tag
   ----------------------------------------------------------------------------
*/

package kiwi.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.event.DialogDismissEvent;
import com.hyperrealm.kiwi.ui.ButtonPanel;
import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.KDialog;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import jworkspace.kernel.Workspace;
import kiwi.ui.*;

/** A base class for custom dialog windows. This class provides some base
  * functionality that can be useful across many different types of dialogs.
  * The class constructs a skeleton dialog consisting of an optional comment
  * line, an icon, <i>OK</i> and <i>Cancel</i> buttons, and a middle area that
  * must be filled in by subclassers.
  * <p>
  * A <code>ComponentDialog</code> is <i>accepted</i> by clicking the
  * <i>OK</i> button, though subclassers can determine the conditions under
  * which a dialog may be accepted by overriding the <code>accept()</code>
  * method; it is <i>cancelled</i> by clicking the <i>Cancel</i> button or
  * closing the window.
  *
  * @author Mark Lindner
  * @author PING Software Group
  */

public abstract class ComponentDialog extends KDialog
  {
  /** The OK button. */
  protected KButton b_ok;

  /** The Cancel button. */
  protected KButton b_cancel = null;

  private _ActionListener actionListener;
  private boolean cancelled = true;
  private KPanel main;
  protected KLabel iconLabel;
  protected KLabel commentLabel;
  private JTextField inputComponent = null;
  private ButtonPanel buttons;
  private int fixedButtons = 1;

  protected KeyHandler keyHandler;
  class KeyHandler extends KeyAdapter
  {
    public void keyPressed(KeyEvent evt)
    {
      if(evt.isConsumed())
        return;

      if(evt.getKeyCode() == KeyEvent.VK_ENTER)
      {
        // crusty workaround
        Component comp = getFocusOwner();
        while(comp != null)
        {
          if(comp instanceof JComboBox)
          {
            JComboBox combo = (JComboBox)comp;
            if(combo.isEditable())
            {
              Object selected = combo.getEditor().getItem();
              if(selected != null)
                combo.setSelectedItem(selected);
            }
            break;
          }

          comp = comp.getParent();
        }

        _accept();
        evt.consume();
      }
      else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
      {
        if(b_cancel != null)
        {
           if(canCancel())
                _cancel();
        }
        else
          _accept();
        evt.consume();
      }
    }
  }
  /** Construct a new <code>ComponentDialog</code>.
    *
    * @param parent The parent dialog for this dialog.
    * @param title The title for this dialog's window.
    * @param modal A flag specifying whether this dialog will be modal.
    */

  public ComponentDialog(Dialog parent, String title, boolean modal)
    {
    this(parent, title, modal, true);
    }

  /** Construct a new <code>ComponentDialog</code>.
    *
    * @param parent The parent dialog for this dialog.
    * @param title The title for this dialog's window.
    * @param modal A flag specifying whether this dialog will be modal.
    * @param hasCancel A flag specifying whether this dialog should have a
    * <i>Cancel</i> button.
    */

  public ComponentDialog(Dialog parent, String title, boolean modal,
                         boolean hasCancel)
    {
    super(parent, title, modal);

    _init(hasCancel);
    }

  /** Construct a new <code>ComponentDialog</code>.
    *
    * @param parent The parent frame for this dialog.
    * @param title The title for this dialog's window.
    * @param modal A flag specifying whether this dialog will be modal.
    */

  public ComponentDialog(Frame parent, String title, boolean modal)
    {
    this(parent, title, modal, true);
    }

  /** Construct a new <code>ComponentDialog</code>.
    *
    * @param parent The parent frame for this dialog.
    * @param title The title for this dialog's window.
    * @param modal A flag specifying whether this dialog will be modal.
    * @param hasCancel A flag specifying whether this dialog should have a
    * <i>Cancel</i> button.
    */

  public ComponentDialog(Frame parent, String title, boolean modal,
                         boolean hasCancel)
    {
    super(parent, title, modal);

        _init(hasCancel);
    }

  /*
   * Common initialization.
   */

  private void _init(boolean hasCancel)
    {
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    actionListener = new _ActionListener();

    main = getMainContainer();
    main.setBorder(getMainBorder());
    main.setLayout(new BorderLayout());

    commentLabel = new KLabel(loc.getMessage("kiwi.dialog.prompt"));
    commentLabel.setBorder(getCommentBorder());
    main.add("North", commentLabel);

    iconLabel = new KLabel();
    iconLabel.setBorder(getIconBorder());
    iconLabel.setVerticalAlignment(SwingConstants.CENTER);
    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

    buttons = new ButtonPanel();
    buttons.setBorder(getButtonsBorder());

    b_ok = new KButton(loc.getMessage("kiwi.button.ok"));
    b_ok.addActionListener(actionListener);
    buttons.addButton(b_ok);

    if(hasCancel)
      {
      b_cancel = new KButton(loc.getMessage("kiwi.button.cancel"));
      b_cancel.addActionListener(actionListener);
      buttons.addButton(b_cancel);
      fixedButtons++;
      }

    main.add("South", buttons);

    JComponent c = buildDialogUI();
    if(c != null)
    {
      main.add("Center", c);
      if (c.getBorder() == null ||
          c.getBorder() instanceof EmptyBorder)
      {
         c.setBorder(getUIBorder());
      }
      else
      {
         CompoundBorder cb = new CompoundBorder(getUIBorder(), c.getBorder() );
         c.setBorder(cb);
      }
    }
    pack();

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
      {
      public void windowClosing(WindowEvent evt)
      {
            if(b_cancel != null)
              {
              if(canCancel())
                _cancel();
              }
            else
              _accept();
      }
      });

     keyHandler = new KeyHandler();
     addKeyListener(keyHandler);
    }

    protected Border getMainBorder()
    {
        return new EmptyBorder(0, 0, 0, 0);
    }

    protected Border getCommentBorder()
    {
        return new EmptyBorder(0, 0, 0, 0);
    }

    protected Border getIconBorder()
    {
        return new EmptyBorder(5, 0, 5, 0);
    }

    protected Border getButtonsBorder()
    {
        return new EmptyBorder(0, 5, 5, 5);
    }

    protected Border getUIBorder()
    {
        return new EmptyBorder(0, 5, 5, 5);
    }

  /** Construct the component that will be displayed in the center of the
    * dialog window. Subclassers implement this method to customize the look of
    * the dialog.
    *
    * @return A <code>JComponent</code> to display in the dialog.
    */

  protected abstract JComponent buildDialogUI();

  /** Show or hide the dialog.
    */

  public void setVisible(boolean flag)
    {
    if(flag)
      {
          if (getParent()!=null) {
              setLocationRelativeTo(getParent());
          }
      validate();
      pack();
      }
    super.setVisible(flag);
    }

  /** Register a text field with this dialog. In some dialogs, most notably
    * <code>KInputDialog</code>, pressing <i>Return</i> in a text field is
    * equivalent to pressing the dialog's <i>OK</i> button. Subclassers may use
    * this method to register a text field that should function in this way.
    *
    * @param c The <code>JTextField</code> to register.
    */

  protected void registerTextInputComponent(JTextField c)
    {
    inputComponent = c;

    inputComponent.addActionListener(actionListener);
    }

  /** This is overridden to fix a Swing layout bug. */

  public void pack()
    {
    boolean old = isResizable();
    setResizable(true);
    super.pack();
    setResizable(old);
    }

  /** Change the dialog's comment.
    *
    * @param comment The new text to display in the comment portion of the
    * dialog.
    */

  public void setComment(String comment)
    {
    commentLabel.setText(comment);
    commentLabel.invalidate();
    commentLabel.validate();
    }

  /** Get the icon to display in the left part of the dialog window. By
    * default, this method returns <code>null</code>, which signifies that no
    * icon will be displayed. The method can be called by classes that extend
    * this class to provide an appropriate icon for the dialog.
    */

  public void setIcon(Icon icon)
    {
    if(icon != null)
      {
      iconLabel.setIcon(icon);
      iconLabel.setBorder(new CompoundBorder(new EmptyBorder(2,2,2,2),
                          new TitledBorder("") ));
      main.add("West", iconLabel);
      }
    else
      main.remove(iconLabel);
    }
    /** Get the icon to display in the top part of the dialog window. By
      * default, this method returns <b>null</b>, which signifies that no icon
      * will be displayed. The method can be called by classes that extend this
      * class to provide an appropriate icon for the dialog.
      */

      public void setTopIcon(Icon icon)
      {
        commentLabel.setIcon(icon);
      }
  /**
   * Set opaquity of main panel
   */
  public void setOpaque(boolean opaque)
  {
     main.setOpaque(opaque);
  }
  /** Get the <i>cancelled</i> state of the dialog. This method should be
    * called after the dialog is dismissed to determine if it was cancelled by
    * the user.
    *
    * @return <code>true</code> if the dialog was cancelled, and
    * <code>false</code> otherwise.
    */

  public boolean isCancelled()
    {
    return(cancelled);
    }

  /* hide the dialog */

  private void _hide()
    {
    setVisible(false);
    dispose();
    }

  /** Accept user input. The dialog calls this method in response to a
    * click on the dialog's <i>OK</i> button. If this method returns
    * <code>true</code>, the dialog disappears; otherwise, it remains
    * on the screen. This method can be overridden to check input in
    * the dialog before allowing it to be dismissed. The default
    * implementation of this method returns <code>true</code>.
    *
    * @return <code>true</code> if the dialog may be dismissed, and
    * <code>false</code> otherwise.
    */

  protected boolean accept()
    {
    return(true);
    }

  /** Cancel the dialog. The dialog calls this method in response to a click on
    * the dialog's <i>Cancel</i> button, or on a close of the dialog window
    * itself. Subclassers may override this method to provide any special
    * processing that is required when the dialog is cancelled. The default
    * implementation of this method does nothing.
    */

  protected void cancel()
    {
    }

  private void _accept()
    {
    if(accept())
      {
      cancelled = false;
      fireDialogDismissed(DialogDismissEvent.OK);
      _hide();
      }
    }

  /* action listener */

  private class _ActionListener implements ActionListener
    {
    public void actionPerformed(ActionEvent evt)
      {
      Object o = evt.getSource();

      if((o == b_ok) || (o == inputComponent))
  {
        _accept();
  }
      else if(o == b_cancel)
        {
  if(canCancel())
          _cancel();
        }
      }
    }

  /** Determine if the dialog can be cancelled. This method is called in
   * response to a click on the <i>Cancel</i> button or on the dialog
   * window's close icon/option. Subclassers may wish to override this
   * method to prevent cancellation of a window in certain circumstances.
   *
   * @return <code>true</code> if the dialog may be cancelled,
   * <code>false</code> otherwise. The default implementation returns
   * <code>true</code> if the dialog has a <i>Cancel</i> button and
   * <code>false</code> otherwise.
   */

  protected boolean canCancel()
    {
    return(b_cancel != null);
    }

  /* cancel the dialog */

  private void _cancel()
    {
    cancelled = true;
    cancel();
    _hide();
    fireDialogDismissed(DialogDismissEvent.CANCEL);
    }

  /* Set the opacity on all the buttons */

  void setButtonOpacity(boolean flag)
    {
    Component c[] = buttons.getComponents();

    for(int i = 0; i < c.length; i++)
      {
      if(c[i] instanceof JButton)
        ((JButton)c[i]).setOpaque(flag);
      }
    }

  /** Add a button to the dialog's button panel. The button is added
    * immediately before the <i>OK</i> button.
    *
    * @param button The button to add.
    */

  protected void addButton(JButton button)
    {
    addButton(button, -1);
    }

  /** Add a button to the dialog's button panel at the specified position.
    *
    * @param button The button to add.
    * @param pos The position at which to add the button. A value of 0 denotes
    * the first position, and -1 denotes the last position. The possible
    * range of values for <code>pos</code> excludes the <i>OK</i> and
    * (if present) <i>Cancel</i> buttons; buttons may not be added after
    * these "fixed" buttons.
    */

  protected void addButton(JButton button, int pos)
    throws IllegalArgumentException
    {
    int bc = buttons.getButtonCount();
    int maxpos = bc - fixedButtons;

    if(pos > maxpos)
      throw(new IllegalArgumentException("Position out of range."));
    else if(pos < 0)
      pos = maxpos;

    buttons.addButton(button, pos);
    }

  /** Remove a button from the dialog's button panel.
    *
    * @param button The button to remove. Neither the <i>OK</i> nor the
    * <i>Cancel</i> button may be removed.
    */

  protected void removeButton(JButton button)
    {
    if((button != b_ok) && (button != b_cancel))
      buttons.removeButton(button);
    }

  /** Remove a button from the specified position in the  dialog's button
    * panel.
    *
    * @param pos The position of the button to remove, where 0 denotes
    * the first position, and -1 denotes the last position. The possible
    * range of values for <code>pos</code> excludes the <i>OK</i> and
    * (if present) <i>Cancel</i> buttons; these "fixed" buttons may not be
    * removed.
    */

  protected void removeButton(int pos)
    {
    int bc = buttons.getButtonCount();
    int maxpos = bc - fixedButtons;

    if(pos > maxpos)
      throw(new IllegalArgumentException("Position out of range."));
    else if(pos < 0)
      pos = maxpos;

    buttons.removeButton(pos);
    }

  /** Set the label text for the <i>OK</i> button.
    *
    * @param text The text for the accept button (for example, "Yes").
    */

  public void setAcceptButtonText(String text)
    {
    b_ok.setText(text);
    }

  /** Set the label text for the <i>Cancel</i> button.
    *
    * @param text The text for the cancel button (for example, "No").
    */

  public void setCancelButtonText(String text)
    {
    if(b_cancel != null)
      b_cancel.setText(text);
    }

  }

/* end of source file */
