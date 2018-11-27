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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.ui.model.KDocument;

/** A specialization of <code>JTextArea</code> that can constrain the
 * length of its input. The text area is preconfigured to have
 * word-style line wrapping.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KTextArea extends JTextArea
{
  private ChangeSupport csupport;
  private _DocumentListener documentListener = null;
  private boolean adjusting = false;

  /** Construct a new <code>KTextArea</code>.
   */
  
  public KTextArea()
  {
    super();
    _init(KDocument.NO_LIMIT);
  }

  /** Construct a new <code>KTextArea</code> with the specified size.
   *
   * @param rows The number of rows.
   * @param columns The number of columns.
   */
  
  public KTextArea(int rows, int columns)
  {
    super(rows, columns);
    _init(KDocument.NO_LIMIT);
  }

  /** Construct a new <code>KTextArea</code> with the specified text.
   *
   * @param text The text to display.
   */
  
  public KTextArea(String text)
  {
    super(text);
    _init(KDocument.NO_LIMIT);
  }

  /** Construct a new <code>KTextArea</code> with the specified size and
   * text.
   *
   * @param text The text to display.
   * @param rows The number of rows.
   * @param columns The number of columns.
   */
  
  public KTextArea(String text, int rows, int columns)
  {
    super(text, rows, columns);
    _init(KDocument.NO_LIMIT);
  }

  /*
   */

  private void _init(int maxLength)
  {
    documentListener = new _DocumentListener();
    csupport = new ChangeSupport(this);
    KDocument doc = new KDocument(maxLength);
    setDocument(doc);
    doc.addDocumentListener(documentListener);

    setLineWrap(true);
    setWrapStyleWord(true);

    setMinimumSize(getPreferredSize());
  }

  /** Set the editable state of this text area.
   *
   * @param flag A flag specifying whether this text area should be editable.
   * Non-editable text areas are made transparent.
   */
  
  public void setEditable(boolean flag)
  {
    super.setEditable(flag);
    setOpaque(flag);
  }

  /** Set the maximum number of characters that may be entered into
   * this text area.  This method will have no effect if the document
   * has been changed from a <code>KDocument</code> via a call to
   * <code>setDocument()</code>.
   *
   * @param length The new maximum length, or <code>KDocument.NO_LIMIT</code>
   * for unlimited length.
   * @see com.hyperrealm.kiwi.ui.model.KDocument
   */

  public void setMaximumLength(int length)
  {
    Document d = getDocument();
    if(d instanceof KDocument)
      ((KDocument)d).setMaximumLength(length);
  }

  /** Get the maxmium number of characters that may be entered into this text
   * area.
   *
   * @return The maximum length, or <code>KDocument.NO_LIMIT</code> if there
   * is no limit.
   */
  
  public int getMaximumLength()
  {
    Document d = getDocument();
    if(d instanceof KDocument)
      return(((KDocument)d).getMaximumLength());
    else
      return(KDocument.NO_LIMIT);
  }

  /** Set the document model for this text area.
   *
   * @param doc The new document model.
   */
  
  public void setDocument(Document doc)
  {
    super.setDocument(doc);

    Document oldDoc = getDocument();

    if((documentListener != null) && (oldDoc != null))
    {
      oldDoc.removeDocumentListener(documentListener);
      doc.addDocumentListener(documentListener);
    }
  }

  /** Add a <code>ChangeListener</code> to this component's list of listeners.
   * <code>ChangeEvent</code>s are fired when this text area's document model
   * changes.
   *
   * @param listener The listener to add.
   */
  
  public void addChangeListener(ChangeListener listener)
  {
    csupport.addChangeListener(listener);
  }

  /** Remove a <code>ChangeListener</code> from this component's list
   * of listeners.
   *
   * @param listener The listener to remove.
   */
  
  public void removeChangeListener(ChangeListener listener)
  {
    csupport.removeChangeListener(listener);
  }

  /* document listener */
  
  private class _DocumentListener implements DocumentListener
  {
    public void changedUpdate(DocumentEvent evt)
    {
      _fireChange();
    }

    public void insertUpdate(DocumentEvent evt)
    {
      _fireChange();
    }

    public void removeUpdate(DocumentEvent evt)
    {
      _fireChange();
    }
  }

  /* Delay-fire a change event, but only if the current DocumentEvent is not
   * the result of a call to setText().
   */

  private void _fireChange()
  {
    if(adjusting)
      return;

    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          csupport.fireChangeEvent();
        }
      });
  }

  /** Set the text to be displayed by this text area. A
   * <code>ChangeEvent</code> will <i>not</i> be fired when the data
   * in the text area is modified via this call.
   *
   * @param text The text to set.
   */
  
  public final synchronized void setText(String text)
  {
    adjusting = true;
    super.setText(text);
    adjusting = false;
  }
  
}

/* end of source file */
