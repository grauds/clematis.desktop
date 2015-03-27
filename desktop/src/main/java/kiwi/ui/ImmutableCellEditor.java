package kiwi.ui;

/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-99 Mark A. Lindner

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
   
   frenzy@ix.netcom.com
   ----------------------------------------------------------------------------

   $Log: ImmutableCellEditor.java,v $
   Revision 1.1  2003/04/02 10:56:13  tysinsh
   1.4.1 based edition

   Revision 1.2  1999/01/10 02:56:53  markl
   added GPL header & RCS tag

   ----------------------------------------------------------------------------
*/

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/** This class is a workaround for a bug in JFC. It is basically an
  * implementation of <code>TableCellEditor</code> that uses a
  * <code>JLabel</code> so that the cell will not be editable.
  * <p>
  * <b>This class will be deprecated in Kiwi 1.2.</b>
  *
  * @see javax.swing.table.TableCellEditor
  *
  * @author Mark Lindner
  * @author PING Software Group
  * @version 1.1 (10/98)
  */

public class ImmutableCellEditor extends AbstractCellEditor
  implements TableCellEditor
  {
  private JLabel label = new JLabel();

  
  /** Get the value of this editor.
	*
	* @return The text displayed by the <code>JLabel</code>.
	*/

  public Object getCellEditorValue()
	{
	return(label.getText());
	}
  /** Construct a new <code>ImmutableCellEditor</code>.
  
  public ImmutableCellEditor()
	{
	}
  
  /** Get the editor component.
	*
	* @return The <code>JLabel</code>.
	*/

  public Component getTableCellEditorComponent(JTable table, Object value,
											   boolean isSelected, int row,
											   int column)
	{
	label.setText(value.toString());
	if(isSelected)
	  {
	  label.setBackground(table.getSelectionBackground());
	  label.setForeground(table.getSelectionForeground());
	  }
	else
	  {
	  label.setBackground(table.getBackground());
	  label.setForeground(table.getForeground());
	  }
	return(label);
	}
  /** Check if this cell is editable.
	*
	* @return Always <b>false</b>.
	*/

  public boolean isCellEditable(EventObject anEvent)
	{
	return(false);
	}
}