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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.util.*;

/** A GUI console window. This class implements the 
 * <code>LoggingEndpoint</code> interface and as such can be used as the
 * destination of log messages sent using that interface.
 *
 * <p><center>
 * <img src="snapshot/ConsoleFrame.gif"><br>
 * <i>An example ConsoleFrame.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class ConsoleFrame extends KFrame implements LoggingEndpoint
{
  private KButton b_clear, b_dismiss;
  private ConsolePanel console;


  /** Construct a new <code>ConsoleFrame</code> with a default title.
   *
   */
  
  public ConsoleFrame()
  {
    this("");
  }
  
  /** Construct a new <code>ConsoleFrame</code>.
   *
   * @param title The title for the console window.
   */
  
  public ConsoleFrame(String title)
  {
    super(title);

    ActionListener actionListener = new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Object o = evt.getSource();
        
          if(o == b_clear)
            console.clear();
          else if(o == b_dismiss)
            setVisible(false);
        }
      };

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    
    KPanel main = getMainContainer();

    main.setLayout(new BorderLayout(5, 5));
    main.setBorder(KiwiUtils.defaultBorder);

    console = new ConsolePanel();
    main.add("Center", console);

    // buttons

    ButtonPanel buttons = new ButtonPanel();

    b_clear = new KButton(loc.getMessage("kiwi.button.clear"));
    b_clear.addActionListener(actionListener);
    buttons.addButton(b_clear);

    b_dismiss = new KButton(loc.getMessage("kiwi.button.close"));
    b_dismiss.addActionListener(actionListener);
    buttons.addButton(b_dismiss);

    main.add("South", buttons);

    if(getTitle().length() == 0)
      setTitle(loc.getMessage("kiwi.dialog.title.console"));
    
    pack();
  }

  /** Set the console buffer size.
   *
   * @param bufSize The buffer size, in characters.
   */

  public void setBufferSize(int bufSize)
  {
    console.setBufferSize(bufSize);
  }

  /** Get the console buffer size.
   *
   * @return The buffer size, in characters.
   */

  public int getBufferSize()
  {
    return(console.getBufferSize());
  }

  /** Log a message to the console.
   *
   * @param type The message type
   * @param message The message proper.
   *
   * @see com.hyperrealm.kiwi.util.LoggingEndpoint
   */

  public void logMessage(int type, String message)
  {
    console.logMessage(type, message);
  }

  /** Close the console.
   *
   * @see com.hyperrealm.kiwi.util.LoggingEndpoint
   */

  public void close()
  {
    setVisible(false);
    dispose();
  }

}

/* end of source file */
