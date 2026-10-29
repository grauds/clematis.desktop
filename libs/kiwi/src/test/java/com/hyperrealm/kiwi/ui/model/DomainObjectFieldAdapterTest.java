package com.hyperrealm.kiwi.ui.model;
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
import org.junit.Test;

/**
 * Unit tests for {@link DomainObjectFieldAdapter}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class DomainObjectFieldAdapterTest {

    /**
     * Test field access for DomainObjects.
     */
    @SuppressWarnings("checkstyle:AnonInnerLength")
    @Test
    public void testFieldAccess() {
        DomainObjectFieldAdapter<String> adapter = new DomainObjectFieldAdapter<String>() {
            @Override
            public int getFieldCount() {
                return 2;
            }

            @Override
            public String getFieldName(int field) {
                return field == 0 ? "name" : "value";
            }

            @Override
            public Object getField(String obj, int field) {
                return field == 0 ? obj : obj.length();
            }

            @Override
            public void setField(String obj, int field, Object value) {
            }

            @Override
            public javax.swing.table.TableCellRenderer getCellRenderer(int field) {
                return null;
            }

            @Override
            public javax.swing.table.TableCellEditor getCellEditor(int field) {
                return null;
            }

            @Override
            public boolean isFieldEditable(String obj, int field) {
                return true;
            }

            @Override
            public Class<?> getFieldClass(int field) {
                return String.class;
            }

            @Override
            public int getFieldPreferredWidth(int field) {
                return 100;
            }

            @Override
            public int getFieldMinWidth(int field) {
                return 50;
            }

            @Override
            public int getFieldMaxWidth(int field) {
                return 200;
            }

            @Override
            public String getLabel(String item) {
                return item;
            }

            @Override
            public javax.swing.Icon getIcon(String item) {
                return null;
            }
        };

        assertEquals(2, adapter.getFieldCount());
        assertEquals("name", adapter.getFieldName(0));
        assertEquals("value", adapter.getFieldName(1));
        assertEquals("test", adapter.getField("test", 0));
        assertEquals(4, adapter.getField("test", 1));
    }
}
