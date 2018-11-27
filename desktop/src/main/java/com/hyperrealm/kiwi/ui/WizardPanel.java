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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** This class represents a single user interface panel for a
 * <code>WizardView</code> component. Subclassers should not construct the
 * panel's user interface in the constructor, but rather, should provide an
 * implementation for <code>buildUI()</code> that fills this purpose.
 *
 * @author Mark Lindner
 *
 * @see com.hyperrealm.kiwi.ui.WizardView
 * @see com.hyperrealm.kiwi.ui.WizardPanelSequence
 */

public abstract class WizardPanel extends KPanel
{
  private ChangeSupport support;
  private KLabel l_title;
  /** The configuration object for the <code>WizardPanelSequence</code> that
   * owns this <code>WizardPanel</code>.
   */
  protected Config config;
  private static Font defaultFont = new Font("Dialog", Font.BOLD, 14);

  /** Construct a new <code>WizardPanel</code>.
   */
  
  public WizardPanel()
  {
    support = new ChangeSupport(this);
    setOpaque(false);
    
    setLayout(new BorderLayout(5, 5));

    KPanel p_top = new KPanel();
    p_top.setLayout(new BorderLayout(5, 5));

    l_title = new KLabel("Untitled");
    p_top.add("Center", l_title);

    add("North", p_top);

    KPanel p_center = new KPanel();
    p_center.setBorder(
      new CompoundBorder(new SoftBevelBorder(BevelBorder.LOWERED),
                         KiwiUtils.defaultBorder));
    p_center.setLayout(new GridLayout(1, 0));
    
    p_center.add(buildUI());
    
    add("Center", p_center);
  }

  /** Build the user interface for this <code>WizardPanel</code>. This method
   * must build and return the component that will be displayed in this
   * <code>WizardPanel</code>. Typically the implementor will instantiate
   * a container such as <code>KPanel</code>, add interface components to it,
   * and then return that container.
   *
   * @return The component that will be displayed in this panel.
   */
  
  protected abstract Component buildUI();

  /** Synchronize this <code>WizardPanel</code>'s user interface. This method
   * is called by the <code>WizardView</code> immediately before this panel is
   * made visible to the user. This allows the implementor to update the
   * state of the components that make up this panel's interface, perhaps
   * based on the current values of the <code>WizardPanelSequence</code>'s
   * <code>Config</code> properties.
   */
  
  public abstract void syncUI();

  /** Synchronize this <code>WizardPanel</code>'s data. This method is called
   * by the <code>WizardView</code> immediately after this panel is made
   * invisible (such as when the user moves to the next or previous panel).
   * This allows the implementor to update the <code>Config</code> properties
   * based on the values entered in the panel's user interface.
   */
  
  public abstract void syncData();

  /** Set the title for this <code>WizardPanel</code>. The title is displayed
   * at the top of the panel.
   *
   * @param title The new title, or <code>null</code> if no title is needed.
   */
  
  protected final void setTitle(String title)
  {
    l_title.setText(title);
  }
  
  /* attach a reference to the global configuration object */
  
  final void setConfig(Config config)
  {
    this.config = config;
  }

  /** Begin focus in this component. This method is called by the
   * <code>WizardView</code> immediately after this panel is made visible to
   * the user. It allows the panel to give input focus to the appropriate
   * component in its user interface. The default implementation requests
   * focus for the panel's first child component.
   */
  
  public void beginFocus()
  {
    if(getComponentCount() > 0)
      getComponent(0).requestFocus();
  }

  /** Add a <code>ChangeListener</code> to this object's list of listeners.
   *
   * @param listener The listener to add.
   */  
  
  public void addChangeListener(ChangeListener listener)
  {
    support.addChangeListener(listener);
  }

  /** Remove a <code>ChangeListener</code> from this object's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */
  
  public void removeChangeListener(ChangeListener listener)
  {
    support.removeChangeListener(listener);
  }

  /** Fire a change event. <code>WizardPanel</code>s should fire
   * <code>ChangeEvent</code>s whenever a change in their internal state
   * would affect the appearance of the <code>WizardView</code>. For example,
   * the user may complete data entry in a panel, which should undim the
   * <code>WizardView</code>'s <i>Next</i> button to allow the user to proceed
   * to the next panel.
   */
  
  protected void fireChangeEvent()
  {
    support.fireChangeEvent();
  }

  /** Determine if the user can move forward to the next panel.
   *
   * @return <code>true</code> if the next panel can be shown, and
   * <code>false</code> otherwise. The default implementation returns
   * <code>true</code>.
   */

  public boolean canMoveForward()
  {
    return(true);
  }

  /** Determine if the user can move backward to the previous panel.
   *
   * @return <code>true</code> if the previous panel can be shown, and
   * <code>false</code> otherwise. The default implementation returns
   * <code>true</code>.
   */

  public boolean canMoveBackward()
  {
    return(true);
  }

  /** Accept the current panel. This method is called when the wizard view's
   * <i>Next</i> or <i>Finish</i> button is clicked. It allows the panel to
   * perform any final input validation.
   *
   * @return <b>true</b> if the validation succeeded, <b>false</b> otherwise.
   * @since Kiwi 2.1.1
   */

  public boolean accept()
  {
    return(true);
  }

}

/* end of source file */
