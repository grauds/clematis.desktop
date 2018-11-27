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

import com.hyperrealm.kiwi.ui.model.KDocument;
import com.hyperrealm.kiwi.util.*;

/** A simple text editor for entering unformatted text. The editor consists of
 * a scrollable <code>JTextArea</code> and <i>Cut</i>, <i>Copy</i>, and
 * <i>Paste</i> buttons.
 *
 * <p><center>
 * <img src="snapshot/SimpleEditor.gif"><br>
 * <i>An example SimpleEditor.</i>
 * </center>
 *
 * @see com.hyperrealm.kiwi.ui.SimpleStyledEditor
 *
 * @author Mark Lindner
 */

public class SimpleEditor extends KPanel
{
  private KButton b_cut, b_copy, b_paste;
  /** The <code>JTextArea</code> that holds the text for this component. */
  protected JTextArea t_text;
  private JToolBar toolBar;
  private KDocument doc;
  
  /** Construct a new <code>SimpleEditor</code>. The editor is created with
   * a default <code>JTextArea</code> size of 10 rows by 60 columns.
   */

  public SimpleEditor()
  {
    this(10, 60);
  }

  /** Construct a new <code>SimpleEditor</code> with the specified number of
   * rows and columns.
   *
   * @param rows The number of rows for the <code>JTextArea</code>.
   * @param columns The number of columns for the <code>JTextArea</code>.
   */
  
  public SimpleEditor(int rows, int columns)
  {
    setLayout(new BorderLayout(5, 5));

    ResourceManager rm = KiwiUtils.getResourceManager();

    KPanel p_top = new KPanel();
    p_top.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    add("North", p_top);

    ActionListener al = new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Object o = evt.getSource();
        
          if(o == b_cut)
            cut();
          else if(o == b_copy)
            copy();
          else if(o == b_paste)
            paste();

          t_text.requestFocus();
        }
      };

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

    toolBar = new JToolBar();
    toolBar.setOpaque(false);
    toolBar.setFloatable(false);

    p_top.add(toolBar);
    
    b_cut = new KButton(rm.getIcon("cut.png"));
    b_cut.addActionListener(al);
    b_cut.setToolTipText(loc.getMessage("kiwi.tooltip.cut"));
    toolBar.add(b_cut);

    b_copy = new KButton(rm.getIcon("copy.png"));
    b_copy.addActionListener(al);
    b_copy.setToolTipText(loc.getMessage("kiwi.tooltip.copy"));
    toolBar.add(b_copy);

    b_paste = new KButton(rm.getIcon("paste.png"));
    b_paste.addActionListener(al);
    b_paste.setToolTipText(loc.getMessage("kiwi.tooltip.paste"));
    toolBar.add(b_paste);

    doc = new KDocument();
    
    t_text = new JTextArea(doc, null, rows, columns);
    add("Center", new KScrollPane(t_text));
    t_text.setLineWrap(true);
    t_text.setWrapStyleWord(true);
  }

  /** Request focus for the editor.
   */
  
  public void requestFocus()
  {
    if(t_text != null)
      t_text.requestFocus();
    else
      super.requestFocus();
  }
  
  /** Get the <code>JTextArea</code> used by this <code>SimpleEditor</code>.
   *
   * @return The <code>JTextArea</code> for this editor.
   */
  
  public JTextArea getJTextArea()
  {
    return(t_text);
  }

  /** Insert text into the editor, replacing the current selection (if any).
   *
   * @param text The text to insert.
   */
  
  public synchronized void insertText(String text)
  {
    t_text.replaceSelection(text);
  }

  /** Set the editable state of the editor.
   *
   * @param flag If <code>true</code>, the editor will be editable, otherwise
   * it will be non-editable.
   */
  
  public void setEditable(boolean flag)
  {
    t_text.setEditable(flag);

    for(int i = 0; i < toolBar.getComponentCount(); i++)
    {
      Component c = toolBar.getComponent(i);

      if(c instanceof AbstractButton)
        ((AbstractButton)c).setEnabled(flag);
    }
  }

  /** Set the text in the editor.
   *
   * @param text The text to display in the editor.
   */
  
  public synchronized void setText(String text)
  {
    t_text.setText(text);
    doc.clearModified();
  }

  /** Get the text in the editor.
   *
   * @return The text currently in the editor.
   */
  
  public synchronized String getText()
  {
    return(t_text.getText());
  }

  /**
   * Determine if the text was modified interactively since the last call
   * to <code>setText()</code>.
   * @since Kiwi 2.0.1
   */

  public boolean isModified()
  {
    return(doc.isModified());
  }

  /** Perform a <i>cut</i> operation on the editor. Removes the selected text
   * from the editor, and stores it in the system clipboard.
   */

  public void cut()
  {
    t_text.cut();
  }

  /** Perform a <i>copy</i> operation on the editor. Copies the selected text
   * from the editor to the system clipboard.
   */

  public void copy()
  {
    t_text.copy();
  }

  /** Perform a <i>paste</i> operation on the editor. Inserts text from the
   * system clipboard into the editor.
   */
  
  public void paste()
  {
    t_text.paste();
  }

  /** Add a button to the editor's tool bar. The button is added to the right
   * of the last button in the toolbar. This method does <i>not</i> register
   * this editor as an <code>ActionListener</code> for the button. 
   *
   * @param button The button to add.
   */
  
  public void addButton(KButton button)
  {
    toolBar.add(button);
  }

  /** Add a separator to the editor's tool bar. The separator is added to the
   * right of the last button in the toolbar.
   *
   * @since Kiwi 2.0
   */

  public void addSeparator()
  {
    toolBar.addSeparator();
  }

  /** Set the caret position on the text area.
   *
   * @param pos The new caret (insert) position.
   *
   * @since Kiwi 2.0
   */

  public void setCaretPosition(int pos)
  {
    t_text.setCaretPosition(pos);
  }

  /** Set the font on the text area.
   *
   * @param font The new font.
   *
   * @since Kiwi 2.0
   */

  public void setTextFont(Font font)
  {
    t_text.setFont(font);
  }

}

/* end of source file */
