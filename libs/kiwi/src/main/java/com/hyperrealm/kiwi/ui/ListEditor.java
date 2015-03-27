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
import java.lang.reflect.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.model.*;
import com.hyperrealm.kiwi.util.*;

/** A simple editor for entering a list of text items. The editor consists of
 * an input field for entry of new items and a scrollable list for the display
 * of currently entered items. Pressing the <i>Return</i> key in the text field
 * accepts the input in the input field and adds it to the list. Selecting
 * items in the list and pressing the <i>Delete</i> or <i>Backspace</i> key
 * removes those items from the list.
 *
 * <p><center>
 * <img src="snapshot/ListEditor.gif"><br>
 * <i>An example ListEditor.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */ 

public class ListEditor extends KPanel implements ActionListener
{
  protected JList list;
  private Editor editor;
  private MutableListModel _model;
  private KButton b_add, b_remove;
  private KScrollPane sp;
  private static final Insets margin = new Insets(3, 3, 3, 3);

  /** Construct a new <code>ListEditor</code> with a text field item editor.
   *
   * @param fieldWidth The width for the input field.
   * @param maxItemLength The maximum input length, in characters.
   * @param model The data model for the editor.
   */
  
  public ListEditor(int fieldWidth, int maxItemLength, MutableListModel model)
  {
    this(new KTextField(fieldWidth), model);

    KTextField f = (KTextField)editor;
    f.setMaximumLength(maxItemLength);
    f.setInputRequired(true);
  }

  /** Construct a new <code>ListEditor</code> with the given item editor.
   *
   * @param editor The item editor.
   * @param model The data model for the editor.
   */
  
  public ListEditor(Editor editor, MutableListModel model)
  {
    this.editor = editor;
    _model = model;

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    ResourceManager resmgr = KiwiUtils.getResourceManager();
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    gbc.gridwidth = 1;
    gbc.fill = gbc.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = gbc.NORTHWEST;

    JComponent editComponent = editor.getEditorComponent();

    try
    {
      Class clazz = editComponent.getClass();
      Method method = clazz.getMethod("addActionListener",
                                      new Class[] { ActionListener.class });

      method.invoke(editComponent, new Object[] { this });
    }
    catch(Exception ex) { }
    
    
    gbc.insets = KiwiUtils.firstBottomInsets;
    add(editComponent, gbc);

    b_add = new KButton(resmgr.getIcon("plus.png"));
    b_add.setMargin(margin);
    b_add.setToolTipText(loc.getMessage("kiwi.tooltip.add_item"));
    b_add.addActionListener(this);

    gbc.gridwidth = gbc.REMAINDER;
    gbc.fill = gbc.NONE;
    gbc.insets = KiwiUtils.lastBottomInsets;
    gbc.weightx = 0;
    add(b_add, gbc);
    
    list = new JList(_model);

    list.addKeyListener(new KeyAdapter()
      {
        public void keyTyped(KeyEvent evt)
        {
          char code = evt.getKeyChar();
          if((code == KeyEvent.VK_DELETE) || (code == KeyEvent.VK_BACK_SPACE))
            removeItems();
        }
      });

    sp = new KScrollPane(list);
    sp.setOpaque(true);
    sp.setBackground(Color.white);
    Dimension sz = new Dimension(10, 125);
    sp.setSize(sz);
    sp.setPreferredSize(sz);
    sp.setMinimumSize(sz);

    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.fill = gbc.BOTH;
    gbc.insets = KiwiUtils.firstBottomInsets;
    add(sp, gbc);

    b_remove = new KButton(resmgr.getIcon("minus.png"));
    b_remove.setMargin(margin);
    b_remove.setToolTipText(loc.getMessage("kiwi.tooltip.remove_sel_items"));
    b_remove.addActionListener(this);

    gbc.gridwidth = gbc.REMAINDER;
    gbc.fill = gbc.NONE;
    gbc.insets = KiwiUtils.lastBottomInsets;
    gbc.weightx = 0;
    add(b_remove, gbc);
  }

  /*
   */

  private void addItem()
  {
    if(editor.validateInput())
    {
      _model.addElement(editor.getObject());
      editor.setObject(null);
    }
  }
  
  /*
   */

  private void removeItems()
  {
    int sel[] = list.getSelectedIndices();
    for(int i = sel.length - 1; i >= 0; i--)
      _model.removeElementAt(sel[i]);
  }

  /** Set the cell renderer for the list.
   *
   * @param renderer The new cell renderer.
   */

  public void setCellRenderer(ListCellRenderer renderer)
  {
    list.setCellRenderer(renderer);
  }

  /** Get the <code>JList</code> component for this <code>ListEditor</code>.
   */

  public JList getJList()
  {
    return(list);
  }

  /** Set the size of the list box.
   */

  public void setListSize(Dimension size)
  {
    sp.setPreferredSize(size);
    sp.setMinimumSize(size);
  }

  /**
   */

  public void requestFocus()
  {
    editor.getEditorComponent().requestFocus();
  }

  /** Enable or disable the component.
   */

  public void setEnabled(boolean enabled)
  {
    editor.getEditorComponent().setEnabled(enabled);
    list.setEnabled(enabled);
    b_add.setEnabled(enabled);
    b_remove.setEnabled(enabled);
  }

  /**
   */

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();

    if((o == editor.getEditorComponent()) || (o == b_add))
    {
      addItem();
      editor.clear();
    }
    else if(o == b_remove)
      removeItems();
  }
  
}

/* end of source file */
