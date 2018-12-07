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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_FRAME_SIZE;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;

import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * An "About..." style window. The window displays an HTML document in a
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
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ResourceManager#getURL
 * @see com.hyperrealm.kiwi.util.ResourceLoader#getResourceAsURL
 */
@SuppressWarnings("unused")
public class AboutFrame extends KFrame {

    private KButton bOk, bHome = null;

    private JEditorPane edit;

    private URL startPage;

    private String defaultTitle;

    private LocaleData loc2;

    /**
     * Construct a new <code>AboutFrame</code> without a <i>Home</i> button.
     *
     * @param title  The title for the window.
     * @param source The URL of the initial HTML document to display.
     */

    public AboutFrame(String title, URL source) {
        this(title, source, false);
    }

    /**
     * Construct a new <code>AboutFrame</code>.
     *
     * @param title         The title for the window.
     * @param source        The URL of the initial HTML document to display.
     * @param hasHomeButton A flag specifying whether the dialog will be created
     *                      with a <i>Home</i> button for URL navigation.
     */

    public AboutFrame(String title, URL source, boolean hasHomeButton) {
        super(title);

        defaultTitle = title;
        startPage = source;

        ActionListener actionListener = new ActionListener();

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
        loc2 = LocaleManager.getDefault().getLocaleData("KiwiMisc");

        KPanel main = getMainContainer();

        main.setLayout(DEFAULT_BORDER_LAYOUT);
        main.setBorder(KiwiUtils.DEFAULT_BORDER);

        ButtonPanel bButtons = new ButtonPanel();

        if (hasHomeButton) {
            bHome = new KButton(loc.getMessage("kiwi.button.home"));
            bHome.addActionListener(actionListener);
            bHome.setEnabled(false);
            bButtons.addButton(bHome);
        }

        bOk = new KButton(loc.getMessage("kiwi.button.close"));
        bOk.addActionListener(actionListener);
        bButtons.addButton(bOk);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                doHide();
            }
        });

        edit = new JEditorPane();

        // the order here is IMPORTANT!

        edit.setEditable(false);
        edit.addHyperlinkListener(evt -> {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                doShowDocument(evt.getURL());
                if (bHome != null) {
                    bHome.setEnabled(true);
                }
            }
        });
        edit.setEditorKit(new HTMLEditorKit());

        KScrollPane scroll = new KScrollPane(edit);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        KPanel bContent = new KPanel();
        bContent.setLayout(DEFAULT_BORDER_LAYOUT);

        Component content = buildContentPanel();
        if (content != null) {
            bContent.add(SOUTH_POSITION, content);
        }

        bContent.add(CENTER_POSITION, scroll);

        main.add(CENTER_POSITION, bContent);

        main.add(SOUTH_POSITION, bButtons);

        setSize(DEFAULT_FRAME_SIZE);
    }

    /**
     * This method may be overridden to produce a component that will
     * be added below the HTML pane in the frame. This can be used to
     * provide additional content, in the frame. The default
     * implementation returns <code>null</code>.
     *
     * @return A component to add to the frame.
     * @since Kiwi 1.3
     */

    protected Component buildContentPanel() {
        return (null);
    }

    /* doHide the window */

    private void doHide() {
        setVisible(false);
        dispose();
    }

    /**
     * Show or doHide the window.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            doShowDocument(startPage);
        }

        super.setVisible(flag);
    }

    /**
     *
     */

    protected void startFocus() {
        bOk.requestFocus();
    }

    /**
     * show document
     */

    private void doShowDocument(URL url) {
        try {
            edit.setPage(url);
            String docTitle = (String) edit.getDocument()
                .getProperty(Document.TitleProperty);
            setTitle((docTitle != null) ? docTitle : defaultTitle);
        } catch (IOException ex) {
            doShowError(url);
        }
    }

    /* show error message */

    private void doShowError(URL url) {
        edit.setText(loc2.getMessage("kiwi.warning.html.doc_not_found",
            url.toString()));
    }

    /* handle button events */

    private class ActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            if (o == bOk) {
                doHide();
            } else if ((bHome != null) && (o == bHome)) {
                doShowDocument(startPage);
                bHome.setEnabled(false);
            }
        }
    }

}
