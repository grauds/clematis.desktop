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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** A wizard-style component. <code>WizardView</code> essentially displays a
 * sequence of panels (or "cards") to the user;  each panel typically
 * contains messages and/or input elements. A <code>WizardPanelSequence</code>
 * object functions as the source of these panels, and determines the order
 * in which the panels are presented to the user, and the conditions under
 * which forward and backward movement is allowed between consecutive panels.
 * <p>
 * This component is arranged as follows. The leftmost portion of the
 * component is used to display an image (which for best results should be
 * transparent). Animated GIFs are acceptable. The bottom portion of the
 * component displays the <i>Back</i>, <i>Next</i>, and <i>Cancel</i>
 * buttons. The remaining space is occupied by the current
 * <code>WizardPanel</code> provided by the <code>WizardPanelSequence</code>
 * object.
 * <p>
 * The <code>WizardPanelSequence</code> determines when the user may move to
 * the next or previous panel. Whenever these conditions change, the
 * <code>WizardPanelSequence</code> fires a <code>ChangeEvent</code> to
 * notify the <code>WizardView</code>, which responds by dimming or
 * undimming the <i>Next</i> and <i>Back</i> buttons, as appropriate. When
 * the final panel in the sequence is reached, the <i>Next</i> button changes
 * to a <i>Finish</i> button.
 * <p>
 * If the <i>Cancel</i> button is pressed, an <code>ActionEvent</code> is
 * fired with an action command of "cancel". If the <i>Finish</i> button is
 * pressed, an <code>ActionEvent</code> is fired with an action command of
 * "finish".
 *
 * <p><center>
 * <img src="snapshot/WizardView.gif"><br>
 * <i>An example WizardView.</i>
 * </center>
 *
 * @see javax.swing.event.ChangeEvent
 * @see com.hyperrealm.kiwi.ui.WizardPanelSequence
 *
 * @author Mark Lindner
 */

public class WizardView extends KPanel
{
  private KButton b_prev, b_next, b_cancel;
  private KLabel iconLabel;
  private WizardPanelSequence sequence;
  private KPanel content;
  private WizardPanel curPanel = null;
  private int pos = 0, count = 0;
  private Icon i_next;
  private boolean finish = false;
  private ActionSupport support;
  private String s_next, s_back, s_finish, s_cancel;
  private _ActionListener actionListener;
  private ButtonPanel p_buttons;

  /** Construct a new <code>WizardView</code>. The <code>buildSequence()</code>
   * method will be called to construct the <code>WizardPanel</code> sequence.
   *
   * @since Kiwi 2.0
   *
   */

  public WizardView()
  {
    this(null);
  }
  
  /** Construct a new <code>WizardView</code>.
   *
   * @param sequence The <code>WizardPanelSequence</code> that will provide
   * <code>WizardPanel</code>s for the wizard.
   */
  
  public WizardView(WizardPanelSequence sequence)
  {
    if(sequence == null)
      sequence = buildSequence();
    
    this.sequence = sequence;

    sequence.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent evt)
        {
          refresh();
        }
      });

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    s_next = loc.getMessage("kiwi.button.next");
    s_back = loc.getMessage("kiwi.button.back");
    s_finish = loc.getMessage("kiwi.button.finish");
    s_cancel = loc.getMessage("kiwi.button.cancel");
    
    support = new ActionSupport(this);
    
    setLayout(new BorderLayout(5, 5));

    content = new KPanel();
    content.setLayout(new GridLayout(1, 0));

    iconLabel = new KLabel(KiwiUtils.getResourceManager()
                           .getIcon("wizard_panel.png"));
    add("West", iconLabel);

    add("Center", content);

    Insets margin = new Insets(1, 5, 1, 5);
    
    p_buttons = new ButtonPanel();

    actionListener = new _ActionListener();

    b_prev = new KButton(s_back, KiwiUtils.getResourceManager()
                         .getIcon("wizard_left.png"));
    b_prev.setFocusPainted(false);
    b_prev.addActionListener(actionListener);
    b_prev.setEnabled(false);
    b_prev.setMargin(margin);
    p_buttons.addButton(b_prev);

    i_next = KiwiUtils.getResourceManager().getIcon("wizard_right.png");
    
    b_next = new KButton("");
    b_next.setHorizontalTextPosition(SwingConstants.LEFT);
    b_next.setFocusPainted(false);
    b_next.addActionListener(actionListener);
    b_next.setMargin(margin);
    p_buttons.addButton(b_next);

    b_cancel = new KButton(s_cancel);
    b_cancel.setFocusPainted(false);
    b_cancel.addActionListener(actionListener);
    b_cancel.setMargin(margin);
    p_buttons.addButton(b_cancel);

    KPanel p_bottom = new KPanel();
    p_bottom.setLayout(new BorderLayout(5, 5));

    p_bottom.add("Center", new JSeparator());
    p_bottom.add("South", p_buttons);
    
    add("South", p_bottom);

    reset();
  }

  /** A method for constructing the wizard panel sequence. This method may be
   * overridden by subclasses to provide the sequence for the view instead of
   * having it supplied via the constructor. The default implementation returns
   * <b>null</b>.
   *
   * @since Kiwi 2.0
   */

  protected WizardPanelSequence buildSequence()
  {
    return(null);
  }

  /** Get a reference to the <i>Cancel</i> button.
   *
   * @return The <i>Cancel</i> button.
   */
  
  public JButton getCancelButton()
  {
    return(b_cancel);
  }

  /** Get a reference to the <i>Finish</i> button.
   *
   * @return The <i>Finish</i> button.
   */
  
  public JButton getFinishButton()
  {
    return(b_next);
  }

  /** Add a button to the <code>WizardView</code> at the specified position.
   *
   * @param button The button to add.
   * @param pos The position at which to add the button. The value 0 denotes
   * the first position, and -1 denotes the last position.
   * @exception IllegalArgumentException If the value of
   * <code>pos</code> is invalid.
   */

  public void addButton(JButton button, int pos)
    throws IllegalArgumentException
  {
    p_buttons.addButton(button, pos);
  }

  /** Remove a button from the specified position in the
   * <code>ButtonPanel</code>.
   *
   * @param pos The position of the button to remove, where 0 denotes the
   * first position.
   * @exception IllegalArgumentException If an attempt is made
   * to remove one of the predefined wizard buttons.
   */
  
  public void removeButton(int pos) throws IllegalArgumentException
  {
    JButton b = (JButton)p_buttons.getButton(pos);
    if((b == b_cancel) || (b == b_prev) || (b == b_next))
      throw(new IllegalArgumentException("Can't remove predefined buttons."));
    else
      p_buttons.removeButton(pos);
  }

  /** Set the component's icon. Animated and/or transparent GIF images add a
   * professional touch when used with <code>WizardView</code>s.
   *
   * @param icon The new icon to use, or <code>null</code> if no icon is
   * needed.
   */

  public void setIcon(Icon icon)
  {
    iconLabel.setIcon(icon);
  }
  
  /* show a panel */
  
  private void showPanel(WizardPanel panel)
  {
    if(curPanel != null)
    {
      content.remove(curPanel);
      curPanel.syncData();
    }
    
    content.add(curPanel = panel);
    content.validate();
    content.repaint();
    curPanel.syncUI();
    refresh();
    curPanel.beginFocus();
  }

  /** Reset the <code>WizardView</code>. Resets the component so that the first
   * panel is displayed. This method also calls the
   * <code>WizardPanelSequence</code>'s <code>reset()</code> method.
   *
   * @see com.hyperrealm.kiwi.ui.WizardPanelSequence#reset
   */
  
  public void reset()
  {
    sequence.reset();
    b_next.setText(s_next);
    b_next.setIcon(i_next);
    showPanel(sequence.getNextPanel());
  }

  /* refresh the buttons based on what the sequence tells us */
  
  private void refresh()
  {
    finish = sequence.isLastPanel();
    b_next.setEnabled(sequence.canMoveForward());
    b_prev.setEnabled(sequence.canMoveBackward());

    if(!finish)
    {
      b_next.setText(s_next);
      b_next.setIcon(i_next);
    }
    else
    {
      b_next.setText(s_finish);
      b_next.setIcon(null);
    }
  }

  /** Add an <code>ActionListener</code> to this component's list of listeners.
   *
   * @param listener The listener to add.
   */

  public void addActionListener(ActionListener listener)
  {
    support.addActionListener(listener);
  }

  /** Add an <code>ActionListener</code> to this component's list of listeners.
   *
   * @param listener The listener to add.
   */

  public void removeActionListener(ActionListener listener)
  {
    support.removeActionListener(listener);
  }

  /* Handle events. */

  private class _ActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent evt)
    {
      Object o = evt.getSource();

      if(o == b_next)
      {
        if(curPanel.accept())
        {
          if(finish)
          {
            curPanel.syncData();
            support.fireActionEvent("finish");
          }
          else
            showPanel(sequence.getNextPanel());
        }
        else
          curPanel.beginFocus();
      }

      else if(o == b_prev)
        showPanel(sequence.getPreviousPanel());

      else if(o == b_cancel)
        support.fireActionEvent("cancel");
    }
  }

}

/* end of source file */
