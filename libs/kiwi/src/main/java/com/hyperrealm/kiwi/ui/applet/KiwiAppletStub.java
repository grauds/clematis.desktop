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

package com.hyperrealm.kiwi.ui.applet;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Dimension;
import java.net.URL;
import java.util.Dictionary;

/**
 * @author Mark Lindner
 * @since Kiwi 1.4.2
 */

class KiwiAppletStub implements AppletStub {

    private AppletPanel panel;

    private AppletContext context;

    private Dictionary params;

    private URL url;

    /**
     *
     */

    KiwiAppletStub(AppletPanel panel, AppletContext context, URL url,
                   Dictionary params) {
        this.panel = panel;
        this.context = context;
        this.url = url;

        this.params = params;
    }

    /**
     *
     */

    public void appletResize(int w, int h) {
        Dimension dim = new Dimension(w, h);
        panel.setPreferredSize(dim);
        panel.setSize(dim);
    }

    /**
     *
     */

    public AppletContext getAppletContext() {
        return (context);
    }

    /**
     *
     */

    public URL getCodeBase() {
        return (url);
    }

    /**
     *
     */

    public URL getDocumentBase() {
        return (url);
    }

    /**
     *
     */

    public String getParameter(String name) {
        return ((String) params.get(name));
    }

    /**
     *
     */

    public boolean isActive() {
        return (panel.isVisible());
    }

}
