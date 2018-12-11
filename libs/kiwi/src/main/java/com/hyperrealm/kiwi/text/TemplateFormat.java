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
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A formatter somewhat similar to <code>java.text.MessageFormat</code>.
 * <code>TemplateFormat</code> formats a template string, replacing variable
 * placeholders with the corresponding values. There is no limit on the
 * number of variables or placeholders, and the relationship between variables
 * and placeholders does not need to be one-to-one, though each placeholder
 * must have a corresponding variable definition.
 * <p>
 * Here is an example template string:
 * <pre>
 * My name is {name}, and I am {age} years old. Are you also {age} years old?
 * </pre>
 * A placeholder is a variable name surrounded by braces. Since the parser is
 * lenient, a variable name can consist of a sequence of any characters except
 * '{' or '}'. A null variable placeholder (<tt>{}</tt>) is not allowed.
 * <p>
 * Literal braces can be inserted into the template by doubling
 * them. So for example, <tt>{{</tt> will be interpreted as a literal single
 * open brace.
 * <p>
 * Variables may appear in more than one place in a template; every occurrence
 * of a given variable placeholder is replaced with the value of that variable.
 * A <code>ParsingException</code> is thrown if a variable placeholder is
 * encountered for which there is no binding.
 * <p>
 * The <code>bind()</code> method, in its various forms, may be used to bind
 * a variable name to its value. Though values of various types are supported,
 * all values are converted to strings during substitution; the string form of
 * the value is substituted in place of the variable's placeholder.
 *
 * @author Mark Lindner
 * @see java.text.MessageFormat
 * @see com.hyperrealm.kiwi.text.ParsingException
 */
@SuppressWarnings("unused")
public class TemplateFormat {

    private HashMap<String, Object> varmap;

    private String text;

    private StreamTokenizer st = null;

    /**
     * Construct a new <code>TemplateFormat</code> for the given template
     * string. The string is not actually parsed until the <code>format()</code>
     * method is called. Once a <code>TemplateFormat</code> is created, it can
     * be used over and over, with different variable bindings each time, if
     * so desired.
     * <p>
     * <b>This class is unsynchronized</b>. Instances of this class should not
     * be accessed concurrently by multiple threads without explicit
     * synchronization.
     *
     * @param text The template string to use.
     */

    public TemplateFormat(String text) {
        this.text = text;
        varmap = new HashMap<>();
    }

    /**
     * Bind a variable to a string value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, String value) {
        varmap.put(var, value);
    }

    /**
     * Bind a variable to a boolean value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, boolean value) {
        varmap.put(var, value);
    }

    /**
     * Bind a variable to an integer value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, int value) {
        varmap.put(var, value);
    }

    /**
     * Bind a variable to a long value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, long value) {
        varmap.put(var, value);
    }

    /**
     * Bind a variable to a float value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, float value) {
        varmap.put(var, value);
    }

    /**
     * Bind a variable to a double value. If the named variable is already
     * defined, the old value is replaced with the new value.
     *
     * @param var   The variable name.
     * @param value The value.
     */

    public void bind(String var, double value) {
        varmap.put(var, value);
    }

    /**
     * Clear all variable bindings.
     */

    public void clear() {
        varmap.clear();
    }

    /**
     * Format the template, substituting the variable values into their
     * placeholders.
     *
     * @param dest The <code>StringBuilder</code> to write the formatted
     *             template to.
     * @throws com.hyperrealm.kiwi.text.ParsingException If an error occurred during parsing,
     *                                                   such as a mismatched brace, unterminated placeholder,
     *                                                   or undefined variable.
     */

    public void format(StringBuilder dest) throws ParsingException {
        try {
            StringReader source = new StringReader(text);
            format(source, dest, null);
            source.close();
        } catch (IOException ex) {
            throw new ParsingException(ex.getMessage());
        }
    }

    /**
     * Compile a list of all variables referenced in the template.
     *
     * @return An array of variable names.
     * @throws com.hyperrealm.kiwi.text.ParsingException If an error occurred during parsing,
     *                                                   such as a mismatched brace, unterminated placeholder,
     *                                                   or undefined variable.
     * @since Kiwi 1.3
     */

    public String[] getVariables() throws ParsingException {
        ArrayList<String> v = new ArrayList<>();

        try {
            StringReader source = new StringReader(text);
            format(source, null, v);
            source.close();
        } catch (IOException ex) { /* won't happen */ }

        String[] s = new String[v.size()];

        v.toArray(s);

        return (s);
    }

    /* parsing engine */
    @SuppressWarnings({"CyclomaticComplexity", "MagicNumber"})
    private void format(StringReader source, StringBuilder dest, ArrayList<String> vars)
        throws ParsingException, IOException {

        st = new StreamTokenizer(source);
        st.resetSyntax();
        st.wordChars(0, 255);
        st.ordinaryChar('{');
        st.ordinaryChar('}');
        int lastTok = 0;
        String var = null;
        boolean forget;

        loop:
        for (;;) {
            int tok = st.nextToken();
            forget = false;

            switch (tok) {
                case StreamTokenizer.TT_EOF:
                    break loop;

                case StreamTokenizer.TT_WORD:
                    if (lastTok == '{') {
                        var = st.sval;
                    } else if (lastTok == '}') {
                        flagMismatch(lastTok);
                    } else {
                        if (dest != null) {
                            dest.append(st.sval);
                        }
                    }
                    break;

                case '{':
                    if (lastTok == '{') {
                        if (dest != null) {
                            dest.append('{');
                        }
                        forget = true;
                    } else if ((lastTok != StreamTokenizer.TT_WORD) && (lastTok != 0)) {
                        flagMismatch(tok);
                    }

                    break;

                case '}':
                    if (lastTok == '}') {
                        if (dest != null) {
                            dest.append('}');
                        }
                        forget = true;
                    } else {
                        if (var != null) {
                            if (dest != null) {
                                dest.append(lookup(var));
                            }
                            if (vars != null) {
                                vars.add(var);
                            }
                            forget = true;
                            var = null;
                        }
                    }
                    break;

                default:
                    throw (new ParsingException("Unrecognized token " + tok,
                        st.lineno()));
            }

            lastTok = (forget ? 0 : tok);
        }

        // check to see if something is left

        if (var != null) {
            throw (new ParsingException("Premature end of input", st.lineno()));
        }
    }

    /* catch mismatched braces */

    private void flagMismatch(int c) throws ParsingException {
        throw new ParsingException("Misplaced :" + (char) c, st.lineno());
    }

    /* look up a variable, & catch undefined vars */

    private String lookup(String var) throws ParsingException {
        Object val = varmap.get(var);
        if (val == null) {
            throw (new ParsingException("Unbound variable :" + var, st.lineno()));
        }

        return val.toString();
    }

}
