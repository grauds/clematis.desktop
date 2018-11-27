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

package com.hyperrealm.kiwi.ui.propeditor;

import com.hyperrealm.kiwi.text.FormatConstants;
import com.hyperrealm.kiwi.util.*;

/** A property type representing numeric properties.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class NumericPropertyType extends PropertyType
  implements FormatConstants
{
  private int format;
  private boolean hasMaxValue = false, hasMinValue = false;
  private double maxValue, minValue;
  private int decimals = 2;
  private boolean grouping = true;

  /** Construct a new <code>NumericPropertyType</code> for integer values.
   */

  public NumericPropertyType()
  {
    this(FormatConstants.INTEGER_FORMAT);
  }

  /** Construct a new <code>NumericPropertyType</code> for numeric values
   * of the given format.
   *
   * @param format The format type; one of the symbolic constants defined in
   * {@link com.hyperrealm.kiwi.text.FormatConstants}.
   */
  
  public NumericPropertyType(int format)
  {
    this.format = format;
  }

  /** Set the maximum value that is valid for this property.
   *
   * @param maxValue the maximum allowed value.
   */

  public void setMaximumValue(double maxValue)
  {
    this.maxValue = maxValue;
    hasMaxValue = true;
  }

  /** Get the maximum value that is valid for this property.
   *
   * @return The maximum allowed value.
   */
  
  public double getMaximumValue()
  {
    return(maxValue);
  }

  /** Check if a maximum value has been set for this property.
   */
  
  public boolean hasMaximumValue()
  {
    return(hasMaxValue);
  }

  /** Set the minimum value that is valid for this property.
   *
   * @param minValue the maximum allowed value.
   */

  public void setMinimumValue(double minValue)
  {
    this.minValue = minValue;
    hasMinValue = true;
  }

  /** Get the minimum value that is valid for this property.
   *
   * @return The minimum allowed value.
   */
  
  public double getMinimumValue()
  {
    return(minValue);
  }

  /** Check if a minimum value has been set for this property.
   */
  
  public boolean hasMinimumValue()
  {
    return(hasMinValue);
  }

  /** Clear the maximum value for this property. This removes any existing
   * maximum value constraint on the property.
   */
  
  public void clearMaximumValue()
  {
    hasMaxValue = false;
  }
  
  /** Clear the minimum value for this property. This removes any existing
   * minimum value constraint on the property.
   */
  
  public void clearMinimumValue()
  {
    hasMinValue = false;
  }

  /** Set the number of significant decimal digits.
   *
   * @since Kiwi 2.4
   */

  public void setDecimals(int digits)
  {
    decimals = digits;
  }

  /** Get the number of significant decimal digits.
   *
   * @since Kiwi 2.4
   */

  public int getDecimals()
  {
    return(decimals);
  }

  /** Enable or disable grouping.
   *
   * @since Kiwi 2.4
   */

  public void setGrouping(boolean grouping)
  {
    this.grouping = grouping;
  }

  /** Determine if grouping is enabled or disabled.
   *
   * @since Kiwi 2.4
   */

  public boolean getGrouping()
  {
    return(grouping);
  }

  /** Get the numeric format that is set for this object.
   */
  
  public int getFormat()
  {
    return(format);
  }

  /**
   */
  
  public String formatValue(Object value)
  {
    LocaleManager lm = LocaleManager.getDefault();
    String s = null;
    double dval = ((DoubleHolder)value).getValue();
    
    switch(format)
    {
      case CURRENCY_FORMAT:
        s = lm.formatCurrency(dval, decimals, grouping);
        break;

      case PERCENTAGE_FORMAT:
        s = lm.formatPercentage(dval, decimals, grouping);
        break;

      case INTEGER_FORMAT:
        s = lm.formatInteger((long)dval, grouping);
        break;
        
      case DECIMAL_FORMAT:
      default:
        s = lm.formatDecimal(dval, decimals, grouping);
        break;
    }
    
    return(s);
  }
  
}

/* end of source file */
