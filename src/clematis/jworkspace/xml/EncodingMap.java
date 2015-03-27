package jworkspace.xml;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import java.util.HashMap;
import java.util.Map;

/**
 * This provides a mapping of XML character encoding types to java
 * encoding types
 */
public class EncodingMap
{
    protected static Map _java2xml = new HashMap();
    protected static Map _xml2java = new HashMap();

    static
    {
        addEncoding("ISO8859_1", "ISO-8859-1");
        addEncoding("UTF8", "UTF-8");
        addEncoding("UTF16", "UTF-16");
        addEncoding("CP1252", "ISO-8859-1");
        addEncoding("CP1250", "windows-1250");
        addEncoding("ISO8859_1", "ISO-8859-1");
        addEncoding("ISO8859_2", "ISO-8859-2");
        addEncoding("ISO8859_3", "ISO-8859-3");
        addEncoding("ISO8859_4", "ISO-8859-4");
        addEncoding("ISO8859_5", "ISO-8859-5");
        addEncoding("ISO8859_6", "ISO-8859-6");
        addEncoding("ISO8859_7", "ISO-8859-7");
        addEncoding("ISO8859_8", "ISO-8859-8");
        addEncoding("ISO8859_9", "ISO-8859-9");
        addEncoding("JIS", "ISO-2022-JP");
        addEncoding("SJIS", "Shift_JIS");
        addEncoding("EUCJIS", "EUC-JP");
        addEncoding("GB2312", "GB2312");
        addEncoding("BIG5", "Big5");
        addEncoding("KSC5601", "EUC-KR");
        addEncoding("ISO2022KR", "ISO-2022-KR");
        addEncoding("KOI8_R", "KOI8-R");
        addEncoding("CP037", "EBCDIC-CP-US");
        addEncoding("CP037", "EBCDIC-CP-CA");
        addEncoding("CP037", "EBCDIC-CP-NL");
        addEncoding("CP277", "EBCDIC-CP-DK");
        addEncoding("CP277", "EBCDIC-CP-NO");
        addEncoding("CP278", "EBCDIC-CP-FI");
        addEncoding("CP278", "EBCDIC-CP-SE");
        addEncoding("CP280", "EBCDIC-CP-IT");
        addEncoding("CP284", "EBCDIC-CP-ES");
        addEncoding("CP285", "EBCDIC-CP-GB");
        addEncoding("CP297", "EBCDIC-CP-FR");
        addEncoding("CP420", "EBCDIC-CP-AR1");
        addEncoding("CP424", "EBCDIC-CP-HE");
        addEncoding("CP500", "EBCDIC-CP-CH");
        addEncoding("CP870", "EBCDIC-CP-ROECE");
        addEncoding("CP870", "EBCDIC-CP-YU");
        addEncoding("CP871", "EBCDIC-CP-IS");
        addEncoding("CP918", "EBCDIC-CP-AR2");
    }

    public static void addEncoding(String java, String xml)
    {
        _java2xml.put(java, xml);
        _xml2java.put(xml, java);
    }

    public static String getJavaFromXML(String xml)
    {
        // Default encoding is UTF-8
        if (xml == null)
        {
            xml = "UTF-8";
        }
        xml = xml.toUpperCase();
        String s = (String) _xml2java.get(xml);
        if (s == null)
        {
            s = xml;
        }
        return s;
    }

    public static String getXMLFromJava(String java)
    {
        java = java.toUpperCase();
        String s = (String) _java2xml.get(java);
        if (s == null)
        {
            s = java;
        }
        return s;
    }
}
