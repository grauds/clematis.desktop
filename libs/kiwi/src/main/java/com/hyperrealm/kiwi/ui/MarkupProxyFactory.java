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

import com.hyperrealm.kiwi.text.XMLElement;

/**
 * This class defines a markup proxy; an object that generates
 * <code>MarkupProxy</code> objects appropriate for rendering given
 * <code>XMLElement</code> objects.
 *
 * @author Mark Lindner
 */

public interface MarkupProxyFactory {

    /**
     * Return a <code>MarkupProxy</code> appropriate for rendering an element.
     *
     * @param element The <code>XMLElement</code> to create a proxy for.
     */

    MarkupProxy getMarkupProxy(XMLElement element);

}
