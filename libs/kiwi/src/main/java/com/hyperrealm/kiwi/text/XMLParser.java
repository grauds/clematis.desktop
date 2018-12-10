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

package com.hyperrealm.kiwi.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * A very simple XML parser. <code>XMLParser</code> tokenizes XML
 * into a series of <i>tags</i> and <i>strings</i>. The parser does
 * not support namespaces or CDATA sections. It also does not care if
 * the document is well-formed. The abstract methods
 * <code>consumeElement()</code>, <code>consumeText()</code>, and
 * <code>consumeEntity()</code> must be implemented to process the parsed
 * data.
 *
 * <p> Here is a trivial example that recognizes a few HTML tags read
 * from standard input:
 *
 * <pre>
 * class HTMLParser extends XMLParser
 * {
 *   HTMLParser()
 *   {
 *     super(new InputStreamReader(System.in));
 *   }
 *
 *   protected void consumeText(String text)
 *   {
 *     LOG.println("Text: " + text);
 *   }
 *
 *   protected boolean consumeEntity(String entity)
 *   {
 *     LOG.println("Entity: " + entity);
 *   }
 *
 *   protected void consumeElement(XMLElement e)
 *   {
 *     String tag = e.getTag();
 *     if(tag.equalsIgnoreCase("b"))
 *       LOG.println("Bold " + (tag.isEnd() ? "end" : "begin"));
 *     else if(tag.equalsIgnoreCase("center"))
 *       LOG.println("Centering " + (tag.isEnd() ? "end" : "begin"));
 *   }
 * }
 * </pre>
 *
 * @author Mark Lindner
 */

public abstract class XMLParser {

    private static final int STATE_NONE = 0, STATE_TAG = 1, STATE_NAME = 2,
            STATE_EQUALS = 3, STATE_VALUE = 4, STATE_COMMENT = 5, STATE_ENTITY = 6;

    private StreamTokenizer st;

    private boolean collapseWhitespace;

    private StringBuilder text, entity;

    /**
     * Construct a new <code>XMLParser</code>.
     *
     * @param reader The reader to parse input from.
     */

    public XMLParser(Reader reader) {
        this(reader, true);
    }

    /**
     * Construct a new <code>XMLParser</code>.
     *
     * @param reader             The reader to parse input from.
     * @param collapseWhitespace A flag indicating whether whitespace
     *                           within text should be collapsed.
     * @since Kiwi 2.1.1
     */
    @SuppressWarnings({"checkstyle:magicnumber", "CheckStyle"})
    public XMLParser(Reader reader, boolean collapseWhitespace) {

        st = new StreamTokenizer(reader);
        this.collapseWhitespace = collapseWhitespace;

        text = new StringBuilder();
        entity = new StringBuilder();

        st.slashStarComments(false);
        st.slashSlashComments(false);
        st.eolIsSignificant(false);

        st.ordinaryChars(0x00, 0x20);
        st.ordinaryChars(0x7F, 0xFF);
        st.ordinaryChar('<');
        st.ordinaryChar('>');
        st.ordinaryChar('!');
        st.ordinaryChars('#', ';');
        st.ordinaryChars('?', '@');
        st.ordinaryChars('[', '~');
        st.ordinaryChar('&');
        st.ordinaryChar(';');
        st.ordinaryChar('\"');

        st.wordChars('#', '%');
        st.wordChars('\'', ':');
        st.wordChars('?', '@');
        st.wordChars('[', '~');
    }

    /**
     * Parse the input. Data is read from the input stream and
     * tokenized streams and tags are passed to the processing methods
     * until there is no more data available on the stream.
     *
     * @throws java.io.IOException If an error occurs on the input stream.
     */
    @SuppressWarnings({"checkstyle:magicnumber", "CheckStyle"})
    public void parse() throws IOException {

        boolean quote = false;
        String pname = null, pvalue = "";
        XMLElement e = null;
        int state = STATE_NONE;
        int lastTok = -999;

        out:
        for (;;) {
            int tok = st.nextToken();

            switch (tok) {
                case StreamTokenizer.TT_EOF:
                    break out;

                case StreamTokenizer.TT_WORD:
                    if (quote) {
                        pvalue = st.sval;
                    } else {
                        switch (state) {
                            case STATE_NONE:
                                text.append(st.sval);
                                break;

                            case STATE_TAG:
                                if (st.sval.charAt(0) == '/') {
                                    e.setEnd(true);
                                    e.setTag(st.sval.substring(1));
                                } else if (st.sval.charAt(st.sval.length() - 1) == '/') {
                                    e.setEmpty(true);
                                    e.setTag(st.sval.substring(0, st.sval.length() - 1));
                                } else e.setTag(st.sval);
                                state = STATE_NAME;
                                break;

                            case STATE_NAME:
                                pname = st.sval;
                                state = STATE_EQUALS;
                                break;

                            case STATE_VALUE:
                                e.addAttribute(pname, st.sval);
                                state = STATE_NAME;
                                break;

                            case STATE_EQUALS:
                                e.addAttribute(pname, null);
                                pname = st.sval;
                                state = STATE_EQUALS;
                                break;

                            case STATE_ENTITY:
                                entity.append(st.sval);
                                break;

                            default:
                        }
                    }
                    break;

                case '>':
                    if ((state == STATE_TAG) || (state == STATE_NAME))
                        consumeElement(e);
                    else if ((state == STATE_EQUALS) || (state == STATE_VALUE)) {
                        e.addAttribute(pname, null);
                        consumeElement(e);
                    } else if (state == STATE_NONE)
                        text.append('>');

                    st.wordChars('"', '"');
                    st.wordChars('!', '!');
                    state = STATE_NONE;
                    break;

                case '<':
                    flushText();
                    e = new XMLElement();
                    st.ordinaryChar('\"');
                    st.ordinaryChar('!');
                    state = STATE_TAG;
                    break;

                case '\"':
                    if (state == STATE_VALUE) {
                        if (!quote) {
                            quote = true;
                            pvalue = "";
                            st.wordChars(' ', ' ');
                            st.wordChars('\t', '\t');
                            st.wordChars('<', '>');
                            st.wordChars('&', '&');
                            st.wordChars(';', ';');
                        } else {
                            quote = false;
                            e.addAttribute(pname, pvalue);
                            st.ordinaryChar(' ');
                            st.ordinaryChar('\t');
                            st.ordinaryChars('<', '>');
                            st.ordinaryChar('&');
                            st.ordinaryChar(';');
                            state = STATE_NAME;
                        }
                    }
                    break;

                case '=':
                    if (state == STATE_EQUALS)
                        state = STATE_VALUE;
                    break;

                case '!':
                    if (state == STATE_TAG)
                        state = STATE_COMMENT;
                    else if (state != STATE_COMMENT)
                        text.append('!');
                    break;

                case '&':
                    if (state == STATE_NONE) {
                        flushText();
                        state = STATE_ENTITY;
                    }
                    break;

                case ';':
                    if (state == STATE_NONE)
                        text.append(';');
                    else if (state == STATE_ENTITY) {
                        consumeEntity(entity.toString());
                        entity.setLength(0);
                        state = STATE_NONE;
                    }
                    break;

                case ' ':
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case '\b':
                    if (state == STATE_NONE) {
                        if (!collapseWhitespace || lastTok != ' ')
                            text.append(' ');
                    }

                    tok = ' ';
                    break;

                default:
            }

            lastTok = tok;
        }

        flushText();
    }

    /* flush the text buffer to the consumer */

    private void flushText() {
        if (text.length() > 0) {
            consumeText(text.toString());
            text.setLength(0);
        }
    }

    /**
     * @since Kiwi 2.1.1
     */

    public void setCollapseWhitespace(boolean flag) {
        collapseWhitespace = flag;
    }

    /**
     * Process an XML element. This method is called by the parser for
     * each complete XML element in the input.
     *
     * @param e The <code>XMLElement</code> to process.
     * @since Kiwi 2.1.1
     */

    protected abstract void consumeElement(XMLElement e);

    /**
     * Process text. This method is called by the parser for each
     * maximally contiguous sequence of text in the input.
     *
     * @param text The text to process.
     * @since Kiwi 2.1.1
     */

    protected abstract void consumeText(String text);

    /**
     * Process an entity.
     *
     * @param entity The entity.
     * @since Kiwi 2.1.1
     */

    protected abstract void consumeEntity(String entity);

}
