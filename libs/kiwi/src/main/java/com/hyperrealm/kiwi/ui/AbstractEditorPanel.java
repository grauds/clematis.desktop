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

import com.hyperrealm.kiwi.util.DomainObject;

/** This class defines methods common to components that display (and
 * allow the editing of) a <i>content object</i>. Typically, the
 * component is filled with editing fields and other input components
 * that correspond to fields in the content object. Data is
 * synchronized between the content object and these fields via calls
 * to the methods <code>commit()</code> and <code>update()</code>.
 *
 * @author Mark Lindner
 * @since Kiwi 2.1
 */

public abstract class AbstractEditorPanel<T> extends KPanel
  implements Editor<T>
{
  /** The current content object. */
  protected T content;
  
  /** Construct a new <code>AbstractEditorPanel</code>.
   */
  
  public AbstractEditorPanel()
  {
  }
  
  /** Synchronize a <code>DomainObject</code> with the
   *  <code>ContentPanel</code>. Subclassers should define this method to
   *  copy the appropriate values from the domain object to the corresponding
   *  input elements in the interface. The reference to the domain object must
   *  also be saved so that its state may be updated in the
   *  <code>syncData()</code> method.
   *
   * @param data The <code>DomainObject</code> to associate with this
   * <code>ContentPanel</code>.
   * @deprecated Replaced by setContent().
   */

  /** Equivalent to <code>setContent()</code>.
   */
  
  public void setObject(T object)
  {
    setContent(object);
  }

  /** Equivalent to <code>getContent()</code>.
   */
  
  public T getObject()
  {
    return(getContent());
  }

  /** Set the content object for this panel.
   */

  public final void setContent(T content)
  {
    this.content = content;
  }

  /** Get the content object for this panel.
   */

  public final T getContent()
  {
    return(content);
  }

  /** Commit the user input supplied in this panel to the content
   * object that was provided with the most recent call to
   * <code>setContent()</code> or
   * <code>setObject()</code>. Subclassers should define this method
   * to copy the appropriate values from the input elements in the
   * panel to the corresponding fields in the content object.
   *
   * @throws DataValidationException If there was an input validation
   * error.
   */
  
  public abstract void commit() throws DataValidationException;

  /** Update this panel to reflect the data in the content object that
   * was provided with the most recent call to
   * <code>setContent()</code> or
   * <code>setObject()</code>. Subclassers should define this method
   * to copy fields from the content object into the corresponding
   * input elements in the panel.
   */

  public abstract void update();

  /** Set the editable property for this panel. Some interfaces have
   * both an editable (<i>edit</i>) and a non-editable (<i>view</i>)
   * mode, but some subclasses may not implement both; in this case
   * the method can be a <i>no-op</i>.
   *
   * @param editable A flag specifying whether the component will be editable.
   */
  
  public abstract void setEditable(boolean editable);

  /** Activate this content panel. The meaning of <i>activate</i> is
   * context-specific. If this is one of a sequence of panels in a
   * tabbed pane, for example, this method might be called when the
   * user selects the corresponding tab to make this panel visible.
   *
   * @return A flag specifying whether this panel may be
   * activated. The default implementation returns <code>true</code>.
   */
  
  public boolean activate()
  {
    return(true);
  }

  /** Deactivate this content panel. The meaning of <i>deactivate</i>
   * is context-specific. If this is one of a sequence of panels in a
   * tabbed pane, for example, this method might be called when the
   * user selects another tab to make this panel invisible.
   *
   * @return A flag specifying whether this panel may be
   * deactivated. The default implementation returns <code>true</code>.
   */
  
  public boolean deactivate()
  {
    return(true);
  }

  /** Get the title for this panel. The usage of this method will
   * vary; if this is one of a sequence of panels in a tabbed pane,
   * for example, this method might return the string that should
   * appear in the tab corresponding to this panel.
   *
   * @return The title for the panel. The default implementation returns an
   * empty string.
   */
  
  public String getTitle()
  {
    return("");
  }

}

/* end of source file */
