package com.hyperrealm.kiwi.ui.model.list;
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

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.DomainObjectFieldAdapter;

/**
 * Unit tests for {@link DomainObjectListModel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class DomainObjectListModelTest {

    /**
     * Test DomainObject field access.
     */
    @SuppressWarnings("checkstyle:AnonInnerLength")
    @Test
    public void testDomainObjectFieldAccess() {
        DomainObjectFieldAdapter<String> adapter = new DomainObjectFieldAdapter<>() {
            @Override
            public int getFieldCount() {
                return 1;
            }

            @Override
            public String getFieldName(int field) {
                return "field";
            }

            @Override
            public Object getField(String obj, int field) {
                return obj;
            }

            @Override
            public void setField(String obj, int field, Object value) {
            }

            @Override
            public TableCellRenderer getCellRenderer(int field) {
                return null;
            }

            @Override
            public TableCellEditor getCellEditor(int field) {
                return null;
            }

            @Override
            public boolean isFieldEditable(String obj, int field) {
                return false;
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
        DomainObjectListModel<String> model = new DomainObjectListModel<>(adapter);
        model.addItem("test");
        assertEquals(1, model.getItemCount());
        assertEquals(1, model.getFieldCount());
        assertEquals("field", model.getFieldLabel(0));
    }
}
