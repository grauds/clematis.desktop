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

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * A class that represents a web browser and provides URL opening capability.
 * This class allows a Java program to open a URL in a web browser. It
 * currently works on Windows and UNIX systems.
 * <p>
 * On Windows, the URL is opened in the default browser by invoking the
 * command:
 * <pre>
 * rundll32 url.dll,FileProtocolHandler <i>url</i>
 * </pre>
 * This calling convention works on most variants of Windows, from Windows 98
 * to Windows 2000. It has not been tested on Windows XP.
 * <p>
 * On UNIX, this class can launch (or send a remote command to) any
 * Netscape-compatible browser which supports the ``-remote'' switch; this
 * includes Netscape itself, Mozilla, and possibly other browsers. An attempt
 * is first made to open the URL in a running browser, via a command like:
 * <pre>
 * netscape -remote openURL(<i>url</i>)
 * </pre>
 * and if that fails, an attempt is then made to launch the browser, via a
 * command like:
 * <pre>
 * netscape <i>url</i>
 * </pre>
 * In the case of an error, an exception is thrown.
 * <p>
 * On MacOS, the equivalent result can be achieved by using the method:
 *
 * <pre>
 * com.apple.mrj.MRJUtils.openURL(String url) throws IOException
 * </pre>
 * <p>
 * Since this call relies on an OS-specific class library, MacOS support has
 * not been added to this class, as doing so would break the compilation on
 * non-MacOS systems.
 *
 * @author Mark Lindner
 * @since Kiwi 1.3.4
 */

public class WebBrowser {

    private static final String[] UNIX_COMMANDS = {"{0} -remote openURL({1})", "{0} {1} &"};

    private static final String[] UNIX_BROWSERS = {"mozilla", "firefox", "netscape"};

    private static final String[] WINDOWS_COMMANDS = {"rundll32 url.dll,FileProtocolHandler {1}"};

    private static final String[] WINDOWS_BROWSERS = {""};

    private boolean windows;

    private String browserPath = null;

    /**
     * Construct a new <code>WebBrowser</code>, with no browser path specified.
     * An attempt will be made to use mozilla or netscape, in that order. It
     * will be assumed that a browser binary can be found in the command
     * search path.
     */

    public WebBrowser() {
        windows = isWindows();
    }

    /**
     * Specify the path to a Netscape-compatible browser (such as Netscape,
     * Mozilla, or any other browser that supports the ``-remote'' switch). It
     * is assumed (but not required) that <i>path</i> is an absolute path to
     * a browser executable.
     * <p>
     * This method has no effect on Windows systems.
     *
     * @param path The path to the browser.
     */

    public void setBrowserPath(String path) {
        if (!windows) {
            browserPath = path;
        }
    }

    /**
     * Open the specified URL in the web browser.
     *
     * @param url The URL to open.
     * @throws java.io.IOException If the browser could not be launched.
     */

    public void openURL(URL url) throws IOException {
        openURL(url.toString());
    }

    /**
     * Open the specified URL in the web browser.
     *
     * @param url The URL to open.
     * @throws java.io.IOException If the browser could not be launched.
     */

    public void openURL(String url) throws IOException {

        String[] commands = (windows ? WINDOWS_COMMANDS : UNIX_COMMANDS);
        String[] browsers = (windows ? WINDOWS_BROWSERS : UNIX_BROWSERS);

        if (!windows && (browserPath != null)) {
            openURL(url, browserPath, commands);
        } else {
            for (String browser : browsers) {
                try {
                    openURL(url, browser, commands);
                    browserPath = browser; // save for next time
                    break;
                } catch (IOException ex) { /* ignore */ }
            }

            throw (new IOException("Unable to launch browser."));
        }

    }

    /*
     */

    private void openURL(String url, String browser,
                         String[] commandList) throws IOException {
        String cmd;
        int exitCode;
        Process p;
        boolean wait = true;

        for (String s : commandList) {

            MessageFormat mf = new MessageFormat(s);
            cmd = mf.format(new Object[]{browser, url});

            if (cmd.endsWith("&")) {
                wait = false;
                cmd = cmd.substring(0, cmd.length() - 1);
            }

            p = Runtime.getRuntime().exec(cmd);

            if (wait) {
                for (;;) {
                    try {
                        exitCode = p.waitFor();
                        break;
                    } catch (InterruptedException ex) { /* ignore */ }
                }

                if (exitCode == 0) {
                    break;
                }
            }
        }
    }

    /*
     */

    private boolean isWindows() {
        String os = System.getProperty("os.name");

        return (os != null && os.startsWith("Windows"));
    }

    /*
     * For testing...
     */

  /*

  public static void main(String args[])
  {
  try
  {
  WebBrowser browser = new WebBrowser();
  browser.setBrowserPath("/usr/local/bin/netscape");
  browser.openURL("http://www.slashdot.org/");
  }
  catch(Exception ex)
  {

  }
  }

  */

}
