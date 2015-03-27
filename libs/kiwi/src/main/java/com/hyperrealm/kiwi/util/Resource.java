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

package com.hyperrealm.kiwi.util;

/** An interface that describes a <i>resource</i>. A resource is an exclusive
 * entity that may be in use by one thread at a time; a thread must reserve a
 *  resource from a resource pool, use the resource, and then release the
 * resource so that it may be used by another thread.
 *
 * @see com.hyperrealm.kiwi.util.ResourcePool
 * 
 * @author Mark Lindner
 */

public interface Resource
{
  /** Reserve the resource. */
  
  public void reserve();

  /** Release the resource. */
  
  public void release();
}

/* end of source file */
