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
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.util.StringUtils;

/** This class represents a combo box for selecting a locale. Locales may be
 * presented in their own specific localized form (the default), or in the
 * current locale.
 *
 * <p><center>
 * <img src="snapshot/LocaleChooser.gif"><br>
 * <i>An example LocaleChooser.</i>
 * </center>
 * <p>
 *
 * @author Mark Lindner
 */

public class LocaleChooser extends JComboBox
{
  private ArrayList<Locale> supportedLocales = new ArrayList<Locale>();

  /** Construct a new <code>LocaleChooser</code>.
   *
   * @param locales The list of locales to display.
   * @since Kiwi 2.1
   */
  
  public LocaleChooser(Locale locales[])
  {
    this(locales, true);
  }
  
  /** Construct a new <code>LocaleChooser</code>.
   *
   * @param locales The list of locales to display.
   * @param localizeDisplay If <b>true</b>, each locale entry is
   * localized to itself. Otherwise, all entries are displayed in the current
   * locale.
   * @since Kiwi 2.1
   */
   
  public LocaleChooser(Locale locales[], boolean localizeDisplay)
  {
    for(int i = 0; i < locales.length; i++)
    {
      String country = (localizeDisplay ? locales[i].getDisplayCountry()
                        : locales[i].getCountry());
      String variant = (localizeDisplay ? locales[i].getDisplayVariant()
                        : locales[i].getVariant());
      String name = (localizeDisplay ? locales[i].getDisplayName(locales[i])
                     : locales[i].getDisplayName());
      
      supportedLocales.add(locales[i]);

      String item = StringUtils.uppercaseFirst(name);
      if(country != null)
        name += " (" + country + ")";

      addItem(item);
    }
  }

  /** Get the currently selected locale.
   *
   * @return A <code>Locale</code> object corresponding to the currently-
   * selected locale, or <code>null</code> if there is no selection.
   */
   
  public Locale getSelectedLocale()
  {
    int x = getSelectedIndex();
    if(x < 0)
      return(null);
    
    return(supportedLocales.get(x));
  }

  /** Set the currently selected locale.
   *
   * @param locale The locale.
   * @since Kiwi 2.1
   */

  public void setSelectedLocale(Locale locale)
  {
    int index = supportedLocales.indexOf(locale);

    setSelectedIndex(index);
  }

}

/* end of source file */
