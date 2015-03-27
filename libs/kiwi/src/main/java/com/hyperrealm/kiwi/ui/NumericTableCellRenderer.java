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
import javax.swing.table.*;

import com.hyperrealm.kiwi.text.*;
import com.hyperrealm.kiwi.util.*;

/** A table cell renderer for displaying numeric fields, including integer,
 * decimal, percentage, and currency amounts, formatted according to the
 * rules of the current locale.
 *
 * @author Mark Lindner
 *
 * @see com.hyperrealm.kiwi.text.FormatConstants
 * @see com.hyperrealm.kiwi.util.LocaleManager
 * @see com.hyperrealm.kiwi.ui.NumericField
 * @see com.hyperrealm.kiwi.ui.NumericCellEditor
 * @see com.hyperrealm.kiwi.ui.model.DomainObjectFieldAdapter
 */

public class NumericTableCellRenderer extends DefaultTableCellRenderer
{
  /** A string representation of the "unknown value"; a value that is either
   * of the wrong type or for which there is no available format.
   */
  public static final String VALUE_UNKNOWN = "--";

  private LocaleManager lm = LocaleManager.getDefault();
  private boolean grouping;
  private int decimals, type;

  /** Construct a new <code>NumericTableCellRenderer</code> of the specified
   * type.
   *
   * @param type The data type to be rendered by this field; one of the
   * constants <code>CURRENCY_FORMAT</code>, <code>DECIMAL_FORMAT</code>,
   * <code>INTEGER_FORMAT</code> or <code>PERCENTAGE_FORMAT</code>, defined in
   * <code>com.hyperrealm.kiwi.text.FormatConstants</code>.
   */
  
  public NumericTableCellRenderer(int type)
  {
    this(type, 2, true);
  }

  /** Construct a new <code>NumericTableCellRenderer</code> of the specified
   * type, number of decimal places displayed, and grouping flag.
   *
   * @param type The data type to be rendered by this field; one of the
   * constants <code>CURRENCY_FORMAT</code>, <code>DECIMAL_FORMAT</code>,
   * <code>INTEGER_FORMAT</code< or <code>PERCENTAGE_FORMAT</code>, defined in
   * <code>com.hyperrealm.kiwi.text.FormatConstants</code>.
   * @param decimals The number of decimal places to be displayed (for
   * non-integer values only).
   * @param grouping A flag specifying whether grouping should be turned on.
   */
  
  public NumericTableCellRenderer(int type, int decimals, boolean grouping)
  {
    this.type = type;
    this.decimals = decimals;
    this.grouping = grouping;
    
    setHorizontalAlignment(SwingConstants.RIGHT);
  }

  /** Set the formatting type.
   *
   * @param type The data type to be rendered by this cell renderer. See the
   * constructor for more information.
   */
  
  public void setType(int type)
  {
    this.type = type;
  }

  /** Get the formatting type.
   *
   * @return The data type being rendered by this cell renderer.
   */
  
  public int getType()
  {
    return(type);
  }

  /** Set the number of decimal places to display for non-integer values.
   *
   * @param decimals The number of decimal places.
   * @exception IllegalArgumentException If <code>decimals</code>
   * is less than 0.
   */
  
  public void setDecimals(int decimals) throws IllegalArgumentException
  {
    if(decimals < 0)
      throw(new IllegalArgumentException("decimals must be >= 0"));

    this.decimals = decimals;
  }

  /** Get the number of decimal places being displayed by this cell renderer.
   *
   * @return The number of decimal places.
   */
  
  public int getDecimals()
  {
    return(decimals);
  }

  /** Enable or disable grouping for this cell renderer.
   *
   * @param grouping A flag that specifies whether grouping should be turned on
   * or off.
   */

  public void setGrouping(boolean grouping)
  {
    this.grouping = grouping;
  }

  /** Determine whether this cell renderer is performing grouping.
   *
   * @return <code>true</code> if grouping is turned on and <code>false</code>
   * otherwise.
   */

  public boolean isGrouping()
  {
    return(grouping);
  }

  /** Set the value to be displayed by this cell renderer. It is
   * assumed that the object passed in is an instance of a subclass of
   * <code>Number</code>; if any other type of object is passed in,
   * the <code>VALUE_UNKNOWN</code> string will be rendered in the
   * cell.
   *
   * @param value The value to render (must be a <code>Double</code> or
   * <code>Long</code>).
   */
  
  protected void setValue(Object value)
  {
    double val = 0.0;
    String s = VALUE_UNKNOWN;

    if(value instanceof Number)
      val = ((Number)value).doubleValue();
    else
    {
      setText(VALUE_UNKNOWN);
      return;
    }
    
    switch(type)
    {
      case FormatConstants.CURRENCY_FORMAT:
        s = lm.formatCurrency(val, decimals, grouping);
        break;

      case FormatConstants.INTEGER_FORMAT:
        s = lm.formatInteger((long)val, grouping);
        break;

      case FormatConstants.PERCENTAGE_FORMAT:
        s = lm.formatPercentage(val, decimals, grouping);
        break;

      case FormatConstants.DECIMAL_FORMAT:
      default:
        s = lm.formatDecimal(val, decimals, grouping);
        break;
    }

    setText(s);
  }

}

/* end of source file */
