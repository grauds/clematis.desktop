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
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.model.*;
import com.hyperrealm.kiwi.util.*;

/** This class represents a document browser window. It displays a
 * <code>DocumentBrowserView</code> in a dedicated frame and handles all
 * window-related events.
 *
 * <p><center>
 * <img src="snapshot/DocumentBrowserFrame.gif"><br>
 * <i>An example DocumentBrowserFrame.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class DocumentBrowserFrame extends KFrame
{
  private KButton b_close;
  private DocumentDataSource dataSource;
  private DocumentBrowserView browser;

  /** Construct a new <code>DocumentBrowserFrame</code>.
   *
   * @param title The window title.
   * @param comment A comment string for the top portion of the window.
   * @param dataSource The data source for the browser.
   */

  public DocumentBrowserFrame(String title, String comment,
                              DocumentDataSource dataSource)
  {
    super(title);

    this.dataSource = dataSource;

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    
    KPanel panel = getMainContainer();

    panel.setBorder(KiwiUtils.defaultBorder);
    panel.setLayout(new BorderLayout(5, 5));

    KLabel l = new KLabel(comment);
    panel.add("North", l);

    ExternalKTreeModel model = new ExternalKTreeModel(dataSource);
    panel.add("Center", browser = new DocumentBrowserView(model));

    ButtonPanel buttons = new ButtonPanel();

    b_close = new KButton(loc.getMessage("kiwi.button.close"));
    b_close.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          _hide();
        }
      });
    buttons.addButton(b_close);
    
    panel.add("South", buttons);

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent evt)
        {
          _hide();
        }
      });
    
    pack();
  }

  /* hide the window */

  private void _hide()
  {
    setVisible(false);
    dispose();
  }

}

/* end of source file */
