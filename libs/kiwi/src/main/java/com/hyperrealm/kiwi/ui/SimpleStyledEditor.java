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

import java.io.StringReader;
import java.util.Enumeration;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import static com.hyperrealm.kiwi.ui.ConsolePanel.DEFAULT_FONT_SIZE;

import com.hyperrealm.kiwi.text.XMLElement;
import com.hyperrealm.kiwi.text.XMLParser;

/**
 * A simple text editor that supports styles like bold, italic, and underline,
 * and that is able to render embedded markup using proxies.
 *
 * <p><center>
 * <img src="snapshot/SimpleStyledEditor.gif"><br>
 * <i>An example SimpleStyledEditor.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.SimpleEditor
 */

public class SimpleStyledEditor extends JTextPane {

    private static final String BOLD_TAG = "</b>";

    private static final String ITALIC_TAG = "</i>";

    private static final String UNDERLINE_TAG = "</u>";

    private static final String UNDERLINE_TAG_START = "<u>";

    private static final String ITALIC_TAG_START = "<i>";

    private static final String BOLD_TAG_START = "<b>";

    private SimpleAttributeSet aItalic, aPlain, aBold, aUnderline;

    private DefaultStyledDocument doc;

    private StringBuilder buffer;

    private boolean italicf;

    private boolean boldf;

    private boolean underlinef;

    private MarkupProxyFactory proxyFactory;

    /**
     * Construct a new <code>SimpleStyledEditor</code>.
     *
     * @param proxyFactory The <code>MarkupProxyFactory</code> that will provide
     *                     proxies for rendering markup content.
     */

    public SimpleStyledEditor(MarkupProxyFactory proxyFactory) {
        super();

        StyleContext sc = new StyleContext();
        Style def = sc.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(def, "Serif");
        StyleConstants.setFontSize(def, DEFAULT_FONT_SIZE);
        doc = new DefaultStyledDocument(sc);
        setDocument(doc);

        aItalic = new SimpleAttributeSet();
        StyleConstants.setItalic(aItalic, true);

        aBold = new SimpleAttributeSet();
        StyleConstants.setBold(aBold, true);

        aUnderline = new SimpleAttributeSet();
        StyleConstants.setUnderline(aUnderline, true);

        aPlain = new SimpleAttributeSet();

        this.proxyFactory = proxyFactory;
    }

    /**
     * Get the text currently displayed in the editor.
     *
     * @return The text currently displayed in the editor.
     * @see #setText
     **/

    public synchronized String getText() {
        buffer = new StringBuilder();

        Element[] e = doc.getRootElements();
        for (Element element : e) {
            decode(element);
        }

        if (boldf) {
            buffer.append(BOLD_TAG);
        }
        if (italicf) {
            buffer.append(ITALIC_TAG);
        }
        if (underlinef) {
            buffer.append(UNDERLINE_TAG);
        }

        return (buffer.toString());
    }

    /**
     * Set the text to be displayed in the editor.
     *
     * @param text The text to display.
     * @see #getText
     */

    public synchronized void setText(String text) {
        new TextReader(text);
    }

    // --- decoder
    @SuppressWarnings("CyclomaticComplexity")
    private void decode(Element el) {
        // decode the element

        AttributeSet attrs = el.getAttributes();
        Enumeration e = attrs.getAttributeNames();

        if (el.isLeaf() && el.getName().equals("component")) {
            while (e.hasMoreElements()) {
                Object name = e.nextElement();
                Object value = attrs.getAttribute(name);

                if (name == StyleConstants.ComponentAttribute) {
                    buffer.append(((MarkupProxy) (StyleConstants.getComponent(attrs)))
                        .getMarkup());
                }
            }
        } else if (el.isLeaf() && el.getName().equals("content")) {
            boolean cboldf = false, citalicf = false, cunderlinef = false;

            while (e.hasMoreElements()) {
                Object name = e.nextElement();
                Object value = attrs.getAttribute(name);

                if ((name == StyleConstants.Bold) && (Boolean) value) {
                    cboldf = true;
                } else if ((name == StyleConstants.Italic)
                    && (Boolean) value) {
                    citalicf = true;
                } else if ((name == StyleConstants.Underline)
                    && (Boolean) value) {
                    cunderlinef = true;
                }
            }

            if (boldf && !cboldf) {
                buffer.append(BOLD_TAG);
            } else if (!boldf && cboldf) {
                buffer.append(BOLD_TAG_START);
            } else if (italicf && !citalicf) {
                buffer.append(ITALIC_TAG);
            } else if (!italicf && citalicf) {
                buffer.append(ITALIC_TAG_START);
            } else if (underlinef && !cunderlinef) {
                buffer.append(UNDERLINE_TAG);
            } else if (!underlinef && cunderlinef) {
                buffer.append(UNDERLINE_TAG_START);
            }

            boldf = cboldf;
            italicf = citalicf;
            underlinef = cunderlinef;

            int start = el.getStartOffset();
            int end = el.getEndOffset();
            try {
                buffer.append(escapeText(doc.getText(start, (end - start))));
            } catch (Exception ignored) {
            }
        } else {
            if (el.getName().equals("paragraph")) {
                while (e.hasMoreElements()) {
                    Object name = e.nextElement();
                    Object value = attrs.getAttribute(name);

                    /* ignore these for the time being */
                }
            }

            // decode the element's children

            int ct = el.getElementCount();
            for (int i = 0; i < ct; i++) {
                Element ee = el.getElement(i);
                decode(ee);
            }
        }
    }

    // --- public style/content modification methods

    /**
     * Insert text at the current insertion point.
     *
     * @param text The text to insert.
     */

    public synchronized void insertText(String text) {
        String t = escapeText(text);
        replaceSelection(t);
    }

    /**
     * Insert a markup proxy at the current insertion point.
     *
     * @param proxy The proxy to insert.
     */

    public synchronized void insertMarkupProxy(MarkupProxy proxy) {
        insertComponent(proxy);
    }

    /**
     * Italicize the currently-selected content.
     */

    public synchronized void setItalic() {
        setCharacterAttributes(aItalic, false);
    }

    /**
     * Boldify the currently-selected content.
     */

    public synchronized void setBold() {
        setCharacterAttributes(aBold, false);
    }

    /**
     * Underline the currently-selected content.
     */

    public synchronized void setUnderline() {
        setCharacterAttributes(aUnderline, false);
    }

    /**
     * Remove all style attributes from the currently-selected content.
     */

    public synchronized void setPlain() {
        setCharacterAttributes(aPlain, true);
    }

    /* escape < and > */

    private String escapeText(String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '<') {
                sb.append("<lt>");
            } else if (c == '>') {
                sb.append("<gt>");
            } else {
                sb.append(c);
            }
        }
        return (sb.toString());
    }

    // xml consumer

    private class TextReader extends XMLParser {
        private String lastString = null;
        private boolean italicf = false, boldf = false, underlinef = false;

        TextReader(String text) {
            super(new StringReader(text));
            try {
                doc.remove(0, doc.getLength());

                parse();

                if (lastString != null) {
                    insertString();
                }
            } catch (Exception ignored) {
            }
        }

        protected void consumeText(String s) {
            if (lastString != null) {
                insertString();
            }
            lastString = s;
        }

        private void insertString() {
            insertString(lastString);
        }

        private void insertString(String s) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();

            if (italicf) {
                StyleConstants.setItalic(attrs, true);
            }

            if (boldf) {
                StyleConstants.setBold(attrs, true);
            }

            if (underlinef) {
                StyleConstants.setUnderline(attrs, true);
            }

            try {
                doc.insertString(doc.getLength(), s, attrs);
            } catch (/*BadLocation*/Exception ignored) {

            }
        }

        protected void consumeEntity(String entity) {
            // no-op
        }

        protected void consumeElement(XMLElement e) {
            // dump out last string, if any

            if (lastString != null) {
                insertString();
            }

            // adjust style state

            String tag = e.getTag();
            switch (tag) {
                case "b":
                    boldf = !e.isEnd();
                    break;
                case "i":
                    italicf = !e.isEnd();
                    break;
                case "u":
                    underlinef = !e.isEnd();
                    break;
                case "lt":
                    insertString("<");
                    break;
                case "gt":
                    insertString(">");
                    break;
                default:
                    MarkupProxy pxy = proxyFactory.getMarkupProxy(e);
                    if (pxy != null) {
                        SimpleAttributeSet attrs2 = new SimpleAttributeSet();
                        StyleConstants.setComponent(attrs2, pxy);
                        try {
                            doc.insertString(doc.getLength(), " ", attrs2);
                        } catch (BadLocationException ignored) {
                        }
                    }
                    break;
            }
            lastString = null;
        }
    }

}
