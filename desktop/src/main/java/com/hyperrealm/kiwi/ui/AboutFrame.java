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
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.hyperrealm.kiwi.util.*;

/** An "About..." style window. The window displays an HTML document in a
 * scroll pane and provides <i>Home</i> and <i>Close</i> buttons. This
 * component is hyperlink sensitive and will follow links embedded in the
 * documents it displays. Clicking the optional <i>Home</i> button causes the
 * component to redisplay the initial document.
 * <p>
 * URLs may point to resources on the network or to system resources loaded
 * using a <code>ResourceManager</code> or <code>ResourceLoader</code>.
 *
 * <p><center>
 * <img src="snapshot/AboutFrame.gif"><br>
 * <i>An example AboutFrame.</i>
 * </center>
 *
 * @see com.hyperrealm.kiwi.util.ResourceManager#getURL
 * @see com.hyperrealm.kiwi.util.ResourceLoader#getResourceAsURL
 *
 * @author Mark Lindner
 */

public class AboutFrame extends KFrame
{
  private KButton b_ok, b_home = null;
  private JEditorPane edit;
  private URL startPage;
  private String defaultTitle = null;
  private _ActionListener actionListener;
  private LocaleData loc, loc2;
  private KPanel p_content;

  /** Construct a new <code>AboutFrame</code> without a <i>Home</i> button.
   *
   * @param title The title for the window.
   * @param source The URL of the initial HTML document to display.
   */
  
  public AboutFrame(String title, URL source)
  {
    this(title, source, false);
  }

  /** Construct a new <code>AboutFrame</code>.
   *
   * @param title The title for the window.
   * @param source The URL of the initial HTML document to display.
   * @param hasHomeButton A flag specifying whether the dialog will be created
   * with a <i>Home</i> button for URL navigation.
   */

  public AboutFrame(String title, URL source, boolean hasHomeButton)
  {
    super(title);

    defaultTitle = title;
    startPage = source;

    actionListener = new _ActionListener();

    loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    loc2 = LocaleManager.getDefault().getLocaleData("KiwiMisc");
    
    KPanel main = getMainContainer();    

    main.setLayout(new BorderLayout(5, 5));
    main.setBorder(KiwiUtils.defaultBorder);
    
    ButtonPanel p_buttons = new ButtonPanel();

    if(hasHomeButton)
    {
      b_home = new KButton(loc.getMessage("kiwi.button.home"));
      b_home.addActionListener(actionListener);
      b_home.setEnabled(false);
      p_buttons.addButton(b_home);
    }

    b_ok = new KButton(loc.getMessage("kiwi.button.close"));
    b_ok.addActionListener(actionListener);
    p_buttons.addButton(b_ok);

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent evt)
        {
          _hide();
        }
      });

    edit = new JEditorPane();

    // the order here is IMPORTANT!

    edit.setEditable(false);
    edit.addHyperlinkListener(new HyperlinkListener()
      {
        public void hyperlinkUpdate(HyperlinkEvent evt)
        {
          if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
          {
            _showDocument(evt.getURL());
            if(b_home != null)
              b_home.setEnabled(true);
          }
        }
      });
    edit.setEditorKit(new HTMLEditorKit());
    
    KScrollPane scroll = new KScrollPane(edit);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants
                                        .HORIZONTAL_SCROLLBAR_NEVER);

    p_content = new KPanel();
    p_content.setLayout(new BorderLayout(5, 5));

    Component content = buildContentPanel();
    if(content != null)
      p_content.add("South", content);

    p_content.add("Center", scroll);
    
    main.add("Center", p_content);

    main.add("South", p_buttons);

    setSize(500, 600);
  }

  /** This method may be overridden to produce a component that will
   * be added below the HTML pane in the frame. This can be used to
   * provide additional content, in the frame. The default
   * implementation returns <code>null</code>.
   *
   * @return A component to add to the frame.
   *
   * @since Kiwi 1.3
   */

  protected Component buildContentPanel()
  {
    return(null);
  }
  
  /* hide the window */

  private void _hide()
  {
    setVisible(false);
    dispose();
  }

  /** Show or hide the window. */

  public void setVisible(boolean flag)
  {
    if(flag)
      _showDocument(startPage);

    super.setVisible(flag);
  }

  /**
   */

  protected void startFocus()
  {
    b_ok.requestFocus();
  }

  /** show document */

  private void _showDocument(URL url)
  {
    try
    {
      edit.setPage(url);
      String docTitle = (String)edit.getDocument()
        .getProperty(Document.TitleProperty);
      setTitle((docTitle != null) ? docTitle : defaultTitle);
    }
    catch(IOException ex)
    {
      _showError(url);
    }
  }
  
  /* show error message */

  private void _showError(URL url)
  {
    edit.setText(loc2.getMessage("kiwi.warning.html.doc_not_found",
                                 url.toString()));
  }

  /* handle button events */
  
  private class _ActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent evt)
    {
      Object o = evt.getSource();
      
      if(o == b_ok)
        _hide();
      
      else if((b_home != null) && (o == b_home))
      {
        _showDocument(startPage);
        b_home.setEnabled(false);
      }
    }
  }

}

/* end of source file */
