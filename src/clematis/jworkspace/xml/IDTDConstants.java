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



/**
 * Provides constant values for DTD
 */
public interface IDTDConstants
{
    // these numbers are all pretty much arbitrary

    // Attribute list stuff
    public static final int NONE = -1;
    public static final int IMPLIED = 0;
    public static final int REQUIRED = 1;
    public static final int FIXED = 2;
    public static final int VALUE = 3;

    public static final int ANY = 10;
    public static final int EMPTY = 11;


    public static final int TOKEN_GROUP = 12;
    public static final int NMTOKEN = 13;
    public static final int NMTOKENS = 14;
    public static final int ID = 15;
    public static final int IDREF = 16;


    public static final int CDATA = 20;
    public static final int PCDATA = 21;
    public static final int COMMENT = 22;
    public static final int PROCESSING_INSTRUCTION = 23;

    public static final int GROUP = 30;

    // SpecNode stuff

    public static final int CONTENT_LEAF = '-';

    public static final int CONTENT_GROUP = '(';
    public static final int CONTENT_OR = '|';
    public static final int CONTENT_CONCAT = ',';

    public static final int CONTENT_ONEMAX = '?';
    public static final int CONTENT_SINGLE = '=';
    public static final int CONTENT_STAR = '*';
    public static final int CONTENT_PLUS = '+';

    public static final String PCDATA_KEY = "#text";
    public static final String COMMENT_KEY = "#comment";
    public static final String PROCESSING_INSTRUCTION_KEY = "#processing_instruction";
}
