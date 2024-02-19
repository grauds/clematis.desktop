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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;

import com.hyperrealm.kiwi.ui.model.datasource.DocumentDataSource;
import com.hyperrealm.kiwi.ui.model.tree.ExternalKTreeModel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class represents a document browser window. It displays a
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

public class DocumentBrowserFrame extends KFrame {

    /**
     * Construct a new <code>DocumentBrowserFrame</code>.
     *
     * @param title      The window title.
     * @param comment    A comment string for the top portion of the window.
     * @param dataSource The data source for the browser.
     */

    public DocumentBrowserFrame(String title, String comment,
                                DocumentDataSource dataSource) {
        super(title);

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        KPanel panel = getMainContainer();

        panel.setBorder(KiwiUtils.DEFAULT_BORDER);
        panel.setLayout(DEFAULT_BORDER_LAYOUT);

        KLabel l = new KLabel(comment);
        panel.add("North", l);

        ExternalKTreeModel model = new ExternalKTreeModel(dataSource);
        DocumentBrowserView browser = new DocumentBrowserView(model);
        panel.add(CENTER_POSITION, browser);

        ButtonPanel buttons = new ButtonPanel();

        KButton bClose = new KButton(loc.getMessage("kiwi.button.close"));
        bClose.addActionListener(evt -> hide());
        buttons.addButton(bClose);

        panel.add(SOUTH_POSITION, buttons);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                hide();
            }
        });

        pack();
    }

    /* hide the window */
    @SuppressWarnings("deprecation")
    public void hide() {
        setVisible(false);
        dispose();
    }

}
