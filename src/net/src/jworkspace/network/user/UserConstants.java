package jworkspace.network.user;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
   Authors may be contacted at:
  larsyde@diku.dk
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
 */

import jworkspace.network.*;

/**
 * This class contains constants specific to the
 * client application as well as those derived from
 * the SharedConstants class.
 */
public class UserConstants extends SharedConstants
{
    /**
     * User name
     */
    public static final String CK_USER_NAME = "user.name";
    /**
     * Alternative user name
     */
    public static final String CK_ALT_USER_NAME = "user.alt.name";
    /**
     * Alternative user password
     */
    public static final String CK_ALT_USER_PASSWORD = "user.alt.pwd";
    /**
     * User id
     */
    public static final String CK_USER_ID = "user.id";
    /**
     * Last active group
     */
    public static final String CK_LAST_ACTIVE_GROUP = "group.last.active";

    public final static java.lang.String PLAIN_TEXT = "text/plain";
    public final static java.lang.String HTML = "text/html";
    public final static java.lang.String XML = "text/xml";
    public final static java.lang.String RTF = "text/rtf";
    public final static java.lang.String BITMAP = "bitmap";
    public final static java.lang.String ALL_FILES = "All files";
    public final static java.lang.String[][] FileTypes = {
        {
            ".txt", "Plain text (.txt)", PLAIN_TEXT}
        , {
            ".html", "HTML (.html, .htm)", HTML}
        , {
            ".xml", "XML (.xml )", XML}
        , {
            ".rtf", "RTF (.rtf)", RTF}
        , {
            ".cpp", "C++ source (.cpp, .cc)", PLAIN_TEXT}
        , {
            ".h", "C++ header (.h)", PLAIN_TEXT}
        , {
            ".java", "Java source (.java)", PLAIN_TEXT}
        , {
            ".gif", "GIF (.gif)", BITMAP}
        , {
            ".jpg", "JPG (.jpg)", BITMAP}
    };

    public final static java.lang.String[][] FILE_TYPE_SYNONYM_TABLE = {
        {
            ".txt"}
        , {
            ".html", ".htm"}
        , {
            ".xml"}
        , {
            ".rtf"}
        , {
            ".cpp", ".cc"}
        , {
            ".h"}
        , {
            ".java"}
        , {
            ".gif"}
        , {
            ".jpg"}
    };
}
