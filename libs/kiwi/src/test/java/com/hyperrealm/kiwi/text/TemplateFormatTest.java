package com.hyperrealm.kiwi.text;
/* ----------------------------------------------------------------------------\
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
   ----------------------------------------------------------------------------\
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link TemplateFormat}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class TemplateFormatTest {

    /**
     * Test bind variables and format.
     */
    @Test
    public void testBindVariablesAndFormat() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("Hello, {name}!");
        tf.bind("name", "World");
        StringBuilder sb = new StringBuilder();
        tf.format(sb);
        assertEquals("Hello, World!", sb.toString());
    }

    /**
     * Test format with multiple variables.
     */
    @Test
    public void testMultipleVariables() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("{greeting}, {name}. You are {age} years old.");
        tf.bind("greeting", "Hello");
        tf.bind("name", "Alice");
        tf.bind("age", 30);
        StringBuilder sb = new StringBuilder();
        tf.format(sb);
        assertEquals("Hello, Alice. You are 30 years old.", sb.toString());
    }

    /**
     * Test same variable used multiple times.
     */
    @Test
    public void testSameVariableMultipleTimes() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("{x} + {x} = {x} + {x}");
        tf.bind("x", 5);
        StringBuilder sb = new StringBuilder();
        tf.format(sb);
        assertEquals("5 + 5 = 5 + 5", sb.toString());
    }

    /**
     * Test escaping with doubled braces.
     */
    @Test
    public void testEscaping() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("{{literal}} and {var}");
        tf.bind("var", "value");
        StringBuilder sb = new StringBuilder();
        tf.format(sb);
        assertEquals("{literal} and value", sb.toString());
    }

    /**
     * Test undefined variable throws exception.
     */
    @Test
    public void testUndefinedVariable() {
        TemplateFormat tf = new TemplateFormat("{undefined}");
        StringBuilder sb = new StringBuilder();
        assertThrows(ParsingException.class, () -> tf.format(sb));
    }

    /**
     * Test mismatched braces throws exception.
     */
    @Test
    public void testMismatchedBraces() {
        TemplateFormat tf = new TemplateFormat("{unclosed");
        StringBuilder sb = new StringBuilder();
        assertThrows(ParsingException.class, () -> tf.format(sb));
    }

    /**
     * Test clear variables.
     */
    @Test
    public void testClear() {
        TemplateFormat tf = new TemplateFormat("{name}");
        tf.bind("name", "World");
        tf.clear();
        StringBuilder sb = new StringBuilder();
        assertThrows(ParsingException.class, () -> tf.format(sb));
    }

    /**
     * Test getVariables.
     */
    @Test
    public void testGetVariables() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("{a} and {b}");
        String[] vars = tf.getVariables();
        assertTrue(vars.length >= 1);
    }

    /**
     * Test different value types.
     */
    @Test
    public void testDifferentValueTypes() throws ParsingException {
        TemplateFormat tf = new TemplateFormat("{s}{b}{i}{f}{d}");
        tf.bind("s", "text");
        tf.bind("b", true);
        tf.bind("i", 42);
        tf.bind("f", 3.14f);
        tf.bind("d", 2.718);
        StringBuilder sb = new StringBuilder();
        tf.format(sb);
        assertEquals("texttrue423.142.718", sb.toString());
    }
}
