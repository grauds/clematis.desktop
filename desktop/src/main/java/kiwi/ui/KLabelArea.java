/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-2003 Mark A. Lindner

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
   $Log: KLabelArea.java,v $
   Revision 1.1  2003/04/02 10:56:13  tysinsh
   1.4.1 based edition

   Revision 1.2  2003/01/19 09:50:53  markl
   Javadoc & comment header updates.

   Revision 1.1  2001/03/12 09:27:18  markl
   Renamed from KLabel.

   Revision 1.7  1999/07/05 08:19:56  markl
   Made not focus traversable.

   Revision 1.6  1999/06/28 08:17:08  markl
   Fixed font.

   Revision 1.5  1999/06/14 00:50:52  markl
   Fixed font to be 12 point, not 10.

   Revision 1.4  1999/06/03 08:06:48  markl
   Added import statement.

   Revision 1.3  1999/06/03 06:17:40  markl
   Rewritten to extend JTextArea; other implementation had problems.

   Revision 1.2  1999/02/28 11:44:08  markl
   Minor fixes.
   ----------------------------------------------------------------------------
*/

package kiwi.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import com.hyperrealm.kiwi.util.KiwiUtils;
import kiwi.util.*;

/** A multi-line label. This class renders a string as one or more lines,
  * breaking text on whitespace and producing a left-justified paragraph. This
  * class is basically a <code>JTextArea</code> that is transparent, non-
  * editable, non-scrollable, and non-highlightable.
  *
  * <p><center>
  * <img src="snapshot/KLabelArea.gif"><br>
  * <i>An example KLabelArea.</i>
  * </center>
  *
  * @since Kiwi 1.3
  *
  * @author Mark Lindner
  */

public class KLabelArea extends JTextArea
  {
  /** Construct a new <code>KLabelArea</code> with the specified text and rows
   * and columns.
   *
   * @param text The text to display.
   * @param rows The height of the label in rows.
   * @param cols The width of the label in columns.
   */
  
  public KLabelArea(String text, int rows, int cols)
    {
    super(text, rows, cols);

    _init();
    }

  /** Construct a new <code>KLabelArea</code> with the specified rows and
   * columns.
   *
   * @param rows The height of the label in rows.
   * @param cols The width of the label in columns.
   */
  
  public KLabelArea(int rows, int cols)
    {
    super(rows, cols);

    _init();
    }

  /* initialize component */
  
  private void _init()
    {
    setEditable(false);
    setOpaque(false);
    setHighlighter(null);
    setFont(KiwiUtils.boldFont);
    setLineWrap(true);
    setWrapStyleWord(true);
    }

  /* The following are overridden to counteract Swing bugs. */
  
  public void setUI(ComponentUI newUI)
    {
    super.setUI(newUI);
    _init();
    }

  public void updateUI()
    {
    super.updateUI();
    _init();
    }

  /** Overridden to return <code>false</code>. (As of JDK 1.4, this method is
   * deprecated.)
   *
   * @deprecated Replaced by isFocusable().
   */
  
  public final boolean isFocusTraversable()
    {
    return(false);
    }

  /** Overriden to return <code>false</code>.
   */

  public final boolean isFocusable()
    {
    return(false);
    }

  }

/* end of source file */
