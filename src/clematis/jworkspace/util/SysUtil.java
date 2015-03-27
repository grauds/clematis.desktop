package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

/**
 * System utility collection.
 * The class is defined as <code>final</code> and all methods of this
 * class are declared as <code>static</code>, so it gives to a Java
 * compiler a way to fastest code generation (an optimizing compiler may
 * be able to "inline" the method, that is, replace a call to the method
 * with the code in its body).
 *
 * @version     2.51    20-Jan-2000
 * @author      Alexey Zhukov (alexz)
 */
public final class SysUtil
{
    /**
     * Unknown OS flag.
     *
     * @see #getOS
     */
    public static final int OS_UNKNOWN = 0;

    /**
     * Generic Windows flag (for systems other then 3.1, 95 or NT).
     *
     * @see #getOS
     */
    public static final int OS_WINDOWS = 1;

    /**
     * Windows 3.1 flag.
     *
     * @see #getOS
     */
    public static final int OS_WINDOWS_31 = 2;

    /**
     * Windows 95 flag.
     *
     * @see #getOS
     */
    public static final int OS_WINDOWS_95 = 3;

    /**
     * Windows NT flag.
     *
     * @see #getOS
     */
    public static final int OS_WINDOWS_NT = 4;

    /**
     * MacOS flag.
     *
     * @see #getOS
     */
    public static final int OS_MACOS = 100;

    /**
     * SunOS flag (equals to {@link #OS_SOLARIS} flag).
     *
     * @see #getOS
     */
    public static final int OS_SUNOS = 200;

    /**
     * Solaris flag (equals to {@link #OS_SUNOS} flag).
     *
     * @see #getOS
     */
    public static final int OS_SOLARIS = 200;

    /**
     * Current OS.
     */
    private static int s_osFlag = OS_UNKNOWN;

    /**
     * Unknown browser flag.
     *
     * @see #getBrowser
     */
    public static final int BROWSER_UNKNOWN = 0;

    /**
     * Microsoft Internet Explorer / JView flag.
     *
     * @see #getBrowser
     */
    public static final int BROWSER_IE = 1;

    /**
     * Netscape Navigator flag.
     *
     * @see #getBrowser
     */
    public static final int BROWSER_NETSCAPE = 100;

    /**
     * AppletViewer flag.
     *
     * @see #getBrowser
     */
    public static final int BROWSER_APPLETVIEWER = 200;

    /**
     * Current browser.
     */
    private static int s_browserFlag = BROWSER_UNKNOWN;

    /**
     * Acquiring OS and browser information while initializing the class
     */
    static
    {
        try
        {
            // Get the OS name.
            String osName = System.getProperty("os.name");

            // Select the one of ids.
            if (osName.indexOf("Windows") > -1)
            {
                s_osFlag = OS_WINDOWS;
                if (osName.indexOf("Windows 95") > -1)
                {
                    s_osFlag = OS_WINDOWS_95;
                }
                else if (osName.indexOf("Windows NT") > -1)
                {
                    s_osFlag = OS_WINDOWS_NT;
                }
                else if (osName.indexOf("Windows 3.1") > -1)
                {
                    s_osFlag = OS_WINDOWS_31;
                }
            }
            else if (osName.indexOf("Mac") > -1)    // "MacOS" for Microsoft,
            {                                       // "Mac OS" for Netscape.
                s_osFlag = OS_MACOS;
            }
            else if (osName.indexOf("Sun") > -1)
            {
                s_osFlag = OS_SUNOS;
            }
            else if (osName.indexOf("Solaris") > -1)
            {
                s_osFlag = OS_SOLARIS;
            }

            // Get the Java vendor name.
            String javaVendor = System.getProperty("java.vendor");

            // Select the one of ids.
            if (javaVendor.indexOf("Microsoft") > -1)
            {
                s_browserFlag = BROWSER_IE;           // MS JView.exe too.
            }
            else if (javaVendor.indexOf("Netscape") > -1)
            {
                s_browserFlag = BROWSER_NETSCAPE;
            }
            else if (javaVendor.indexOf("Sun") > -1 ||
                    javaVendor.indexOf("Symantec") > -1)
            {
                s_browserFlag = BROWSER_APPLETVIEWER;
            }
        }
        catch (Throwable _ex)
        {
            // Security, etc...
        }
    }

    /**
     * Don't let anyone to instantiate this object.
     */
    private SysUtil()
    {
    }

    /**
     * Returns the browser id.
     *
     * @return  the browser id.
     * @see     #BROWSER_UNKNOWN
     * @see     #BROWSER_IE
     * @see     #BROWSER_NETSCAPE
     * @see     #BROWSER_APPLETVIEWER
     * @see     #isBrowserIE
     * @see     #isBrowserNetscape
     * @see     #isBrowserAppletViewer
     */
    public static final int getBrowser()
    {
        return (s_browserFlag);
    }

    /**
     * Returns the OS id.
     *
     * @return  the OS id.
     * @see     #OS_UNKNOWN
     * @see     #OS_WINDOWS
     * @see     #OS_WINDOWS_31
     * @see     #OS_WINDOWS_95
     * @see     #OS_WINDOWS_NT
     * @see     #OS_MACOS
     * @see     #OS_SUNOS
     * @see     #OS_SOLARIS
     * @see     #isWindows
     * @see     #isWindows31
     * @see     #isWindows95
     * @see     #isWindowsNT
     * @see     #isMacOS
     * @see     #isSunOS
     * @see     #isSolaris
     */
    public static final int getOS()
    {
        return (s_osFlag);
    }

    /**
     * Returns <code>true</code> if a current browser is AppletViewer.
     *
     * @return <code>true</code> if a current browser is AppletViewer,
     * <code>false</code> otherwise.
     */
    public static final boolean isBrowserAppletViewer()
    {
        return (s_browserFlag == BROWSER_APPLETVIEWER);
    }

    /**
     * Returns <code>true</code> if a current browser is Microsoft Internet
     * Explorer or Microsoft AppletViewer (JView).
     *
     * @return <code>true</code> if a current browser is Microsoft Internet
     * Explorer or Microsoft AppletViewer (JView), <code>false</code>
     * otherwise.
     */
    public static final boolean isBrowserIE()
    {
        return (s_browserFlag == BROWSER_IE);
    }

    /**
     * Returns <code>true</code> if a current browser is Netscape Navigator.
     *
     * @return <code>true</code> if a current browser is Netscape Navigator,
     * <code>false</code> otherwise.
     */
    public static final boolean isBrowserNetscape()
    {
        return (s_browserFlag == BROWSER_NETSCAPE);
    }

    /**
     * Returns <code>true</code> if a current system is MacOS.
     *
     * @return <code>true</code> if a current system is MacOS,
     * <code>false</code> otherwise.
     */
    public static final boolean isMacOS()
    {
        return (s_osFlag == OS_MACOS);
    }

    /**
     * Returns <code>true</code> if a current system is Solaris.
     *
     * @return <code>true</code> if a current system is Solaris,
     * <code>false</code> otherwise.
     */
    public static final boolean isSolaris()
    {
        return (s_osFlag == OS_SOLARIS);
    }

    /**
     * Returns <code>true</code> if a current system is SunOS.
     *
     * @return <code>true</code> if a current system is SunOS,
     * <code>false</code> otherwise.
     */
    public static final boolean isSunOS()
    {
        return (s_osFlag == OS_SUNOS);
    }

    /**
     * Returns <code>true</code> if a current system is from the
     * Windows family.
     *
     * @return <code>true</code> if a current system is from the
     * Windows family, <code>false</code> otherwise.
     */
    public static final boolean isWindows()
    {
        return (s_osFlag == OS_WINDOWS ||
                s_osFlag == OS_WINDOWS_31 ||
                s_osFlag == OS_WINDOWS_95 ||
                s_osFlag == OS_WINDOWS_NT);
    }

    /**
     * Returns <code>true</code> if a current system is Windows 3.1.
     *
     * @return <code>true</code> if a current system is Windows 3.1,
     * <code>false</code> otherwise.
     */
    public static final boolean isWindows31()
    {
        return (s_osFlag == OS_WINDOWS_31);
    }

    /**
     * Returns <code>true</code> if a current system is Windows 95.
     *
     * @return <code>true</code> if a current system is Windows 95,
     * <code>false</code> otherwise.
     */
    public static final boolean isWindows95()
    {
        return (s_osFlag == OS_WINDOWS_95);
    }

    /**
     * Returns <code>true</code> if a current system is Windows NT.
     *
     * @return <code>true</code> if a current system is Windows NT,
     * <code>false</code> otherwise.
     */
    public static final boolean isWindowsNT()
    {
        return (s_osFlag == OS_WINDOWS_NT);
    }
}