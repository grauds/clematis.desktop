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

package com.hyperrealm.kiwi.event;

import java.lang.reflect.*;
import java.util.*;

/** A class that implements a reflection-based event dispatcher. The class
 * is meant to be used as a private member of an event handler object. This
 * event handler object is assumed to have an event handler method (whose
 * name is arbitrary) which may be overloaded to handle different subclasses of
 * a given base event class. Given an event object, the dispatcher
 * selects the most appropriate form of the event handler method and invokes
 * it on that event.
 * <p>
 * For example, consider the following event hierarchy:
 * <p>
 * <pre>
 * BaseEvent
 *   <img src="../../../../resources/inherit.gif">InputEvent
 *       <img src="../../../../resources/inherit.gif">MouseEvent
 * </pre>
 * We can declare an object <code>EventProcessor</code> for this hierarchy
 * as follows:
 * <pre><code>
 * public class EventProcessor
 * {
 *   private EventDispatcher dispatcher;
 *
 *   public EventProcessor()
 *   {
 *     dispatcher = new EventDispatcher(this, "handleEvent", BaseEvent.class);
 *   }
 *
 *   public void handleEvent(MouseEvent event)
 *   {
 *     // process MouseEvents...
 *   }
 *
 *   public void handleEvent(InputEvent event)
 *   {
 *     // process InputEvents...
 *   }
 *
 *   public void handleEvent(BaseEvent event)
 *   {
 *     // process BaseEvents...
 *   }
 *
 *   public void processEvent(BaseEvent event)
 *   {
 *     dispatcher.dispatch(event);
 *   }
 * }
 * </code></pre>
 * When the <code>processEvent()</code> method is called, the event is
 * automatically passed to the most appropriate <code>handleEvent()</code>
 * method. If we now defined a new <code>KeyboardEvent</code> which is a
 * subclass of <code>InputEvent</code>, and called <code>processEvent()</code>
 * with an instance of this new event, it would be passed to the
 * <code>handleEvent()</code> method that takes an <code>InputEvent</code>,
 * since that would be the most specific handler method available.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1.1
 */

public final class EventDispatcher
{
  private HashMap<Class, Method> callMap = new HashMap<Class, Method>();
  private Object owner;

  /** Construct a new <code>EventDispatcher</code>.
   *
   * @param owner The object that will own this dispatcher instance.
   * @param methodName The name of the event handler method(s).
   * @param baseClass The common ancestor class for all events that will be
   * delivered using this dispatcher.
   */
  
  public EventDispatcher(Object owner, String methodName, Class baseClass)
  {
    this.owner = owner;
    
    Method methods[] = owner.getClass().getMethods();
    for(int i = 0; i < methods.length; i++)
    {      
      if(! methods[i].getName().equals(methodName))
        continue;

      int mod = methods[i].getModifiers();
      if(! (Modifier.isPublic(mod)))
        continue;

      if(methods[i].getReturnType() != void.class)
        continue;

      Class[] params = methods[i].getParameterTypes();

      if((params.length != 1) || ! baseClass.isAssignableFrom(params[0]))
        continue;

//      System.out.println("method: " + methods[i].getName() + "("
//                         + params[0].getName() + ")");

      callMap.put(params[0], methods[i]);
    }
  }

  /** Dispatch an event to the most appropriate handler method.
   *
   * @param event The event object to dispatch.
   * @throws java.lang.reflect.InvocationTargetException If the handler method
   * threw an uncaught exception.
   */
  
  public void dispatch(EventObject event) throws InvocationTargetException
  {
    Method m = null;

    for(Class clz = event.getClass(); clz != EventObject.class;
        clz = clz.getSuperclass())
    {
      m = callMap.get(clz);
      if(m != null)
        break;
    }

    if(m != null)
    {
      try
      {
        m.invoke(owner, event);
      }
      catch(IllegalAccessException ex) { /* won't happen */ }
    }
  }
  
}

/* end of source file */
