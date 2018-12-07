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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Dictionary;

import javax.swing.JApplet;
import javax.swing.JTextField;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A component for displaying Java applets in a standalone Java
 * application.  This class provides a basic execution context for
 * Java applets; this context is fully functional with one exception:
 * requests from the applet to open a document are ignored.
 * <p>
 * Only applets which subclass <code>JApplet</code> are supported by this
 * class; an attempt to load a "heavyweight" applet (one that extends
 * <code>Applet</code>) will result in an exception.
 * <p>
 * The applet must be provided in the form of a self-contained JAR
 * file, which includes the applet's classes as well as any resources
 * (such as images and audio clips) that are needed by the
 * applet. These resources may be fetched by the applet using the
 * customary <code>getAudioClip()</code> and <code>getImage()</code>
 * methods. The <code>getCodeBase()</code> and <code>getDocumentBase()</code>
 * methods return a URL representing the applet JAR file itself, and can
 * thus be used to construct URLs to the applet's resources.
 * <p>
 * The panel will attempt to resize itself to accommodate the size of its
 * applet.
 *
 * <p><center>
 * <img src="snapshot/AppletPanel.gif"><br>
 * <i>An example AppletPanel.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 1.4.2
 */
@SuppressWarnings("unused")
public class AppletPanel extends KPanel {

    private static final int HGAP = 5;

    private static final int VGAP = 5;

    private KiwiAppletContext ctx;

    private JApplet applet;

    private KPanel appletPanel;

    private JTextField statusField;

    private String appletName = null;

    /**
     * Construct a new <code>AppletPanel</code>. The panel consists of the
     * applet itself (if one has been set) and a status area in which
     * status messages from the applet are displayed.
     * <p>
     * In the current implementation, each <code>AppletPanel</code> has
     * its own applet context; therefore applets cannot interact with
     * each other. This may be changed in a future release.
     */
    public AppletPanel() {

        setLayout(new BorderLayout(HGAP, VGAP));

        ctx = new KiwiAppletContext() {
            public void showStatus(String msg) {
                statusField.setText(msg);
            }
        };

        appletPanel = new KPanel();
        appletPanel.setOpaque(true);
        appletPanel.setBackground(Color.black);
        appletPanel.setLayout(new GridLayout(1, 0));

        add(CENTER_POSITION, appletPanel);

        statusField = new JTextField();
        statusField.setOpaque(false);
        statusField.setEditable(false);
        statusField.setFocusable(false);

        add(SOUTH_POSITION, statusField);

        setBorder(KiwiUtils.defaultBorder);
    }

    /**
     * Request focus for this component. Overridden to transfer focus to the
     * applet.
     */
    public void requestFocus() {
        if (applet != null) {
            applet.requestFocus();
        } else {
            super.requestFocus();
        }
    }

    /*
     */

    void setStatus(String text) {
        statusField.setText(text);
    }

    /**
     * Remove the current applet (if any) from the panel. The applet will be
     * stopped, destroyed, and removed from the panel.
     */

    private void removeApplet() {
        if (applet == null) {
            return;
        }
        if (appletName != null) {
            ctx.removeApplet(appletName);
        }
        stopApplet();
        applet.setStub(null);
        appletPanel.removeAll();
        applet = null;
    }

    /**
     * Set the applet to be displayed by this panel.
     *
     * @param archive   The path to the JAR file containing the applet's classes
     *                  and resources.
     * @param className The fully-qualified name of the class which implements
     *                  the <code>JApplet</code> interface.
     * @param width     The preferred width of the applet.
     * @param height    The preferred height of the applet.
     * @param params    The parameters for the applet.
     * @throws AppletException If the applet could not be loaded. This is a
     *                         chained exception; the <code>getCause()</code> method will return the
     *                         original exception.
     */

    public void setApplet(String archive, String className, int width,
                          int height, Dictionary params) throws AppletException {
        setApplet(new File(archive), className, width, height, params);
    }

    /**
     * Set the applet to be displayed by this panel.
     *
     * @param archive   The JAR file containing the applet's classes and
     *                  resources.
     * @param className The fully-qualified name of the class which implements
     *                  the <code>JApplet</code> interface.
     * @param width     The preferred width of the applet.
     * @param height    The preferred height of the applet.
     * @param params    The parameters for the applet.
     * @throws AppletException If the applet could not be loaded. This
     *                         is a chained exception; the <code>getCause()</code> method will
     *                         return the original exception.
     */

    public void setApplet(File archive, String className, int width,
                          int height, Dictionary params) throws AppletException {
        URL url;

        removeApplet();

        try {
            url = new URL("jar:file:" + archive.getAbsolutePath() + "!/");
        } catch (MalformedURLException ex) {
            throw new AppletException(ex);
        }

        ClassLoader classLoader = new URLClassLoader(new URL[]{url});

        Class clazz = null;

        try {
            clazz = classLoader.loadClass(className);
            applet = (JApplet) clazz.newInstance();
            KiwiAppletStub stub = new KiwiAppletStub(this, ctx, url, params);
            applet.setStub(stub);
            applet.setFocusable(true);

            appletPanel.setPreferredSize(new Dimension(width, height));
            appletPanel.add(applet);

            appletName = className;
            ctx.addApplet(appletName, applet);
        } catch (Exception ex) {
            throw new AppletException(ex);
        }
    }

    /**
     * Start execution of the applet (if one has been set). The
     * applet's <code>init()</code> and <code>start()</code> methods are
     * invoked, in that order.
     */

    public void startApplet() {
        if (applet != null) {
            applet.init();
            applet.start();

            setStatus("Applet started.");
        }
    }

    /**
     * Stop execution of the applet (if one has been set). The applet's
     * <code>stop()</code> and <code>destroy()</code> methods are invoked, in
     * that order.
     */

    public void stopApplet() {
        if (applet != null) {
            applet.stop();
            applet.destroy();

            setStatus("Applet stopped.");
        }
    }

}
