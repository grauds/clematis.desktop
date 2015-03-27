package jworkspace.ui.widgets;

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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import jworkspace.ui.action.UISwitchListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Check list control is combining JCheckBox
 * boxes with JList via column and row headers
 * in scroll viewport.
 */
public class CheckList extends JPanel
{
    private JList listCheckBox = null;
    private JList listDescription = null;

/* Inner class to hold data for JList with checkboxes */
    class CheckBoxItem
    {
        private boolean isChecked;

        public CheckBoxItem()
        {
            isChecked = false;
        }

        public boolean isChecked()
        {
            return isChecked;
        }

        public void setChecked(boolean value)
        {
            isChecked = value;
        }
    }

    /**
     *  Inner class that renders JCheckBox to JList
     */
    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer
    {

        public CheckBoxRenderer()
        {
            setBackground(UIManager.getColor("List.textBackground"));
            setForeground(UIManager.getColor("List.textForeground"));
        }

        public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex,
                                                      boolean isChecked, boolean hasFocus)
        {
            setSelected(((CheckBoxItem) obj).isChecked());
            return this;
        }

    }

    public CheckList()
    {
        super();
        setLayout(new BorderLayout());

        String[] listData = {};

        //This listbox holds only the checkboxes
        listCheckBox = new JList(buildCheckBoxItems(listData.length));

        //This listbox holds the actual descriptions of list items.
        listDescription = new JList(listData);

        listDescription.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDescription.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent me)
            {
                if (me.getClickCount() != 2)
                    return;
                int selectedIndex = listDescription.locationToIndex(me.getPoint());
                if (selectedIndex < 0)
                    return;
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                listCheckBox.repaint();
            }
        });
        listCheckBox.setCellRenderer(new CheckBoxRenderer());
        listCheckBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCheckBox.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent me)
            {
                int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
                if (selectedIndex < 0)
                    return;
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                listDescription.setSelectedIndex(selectedIndex);
                listCheckBox.repaint();
            }
        });

        setBorders();

        // Now create a scrollpane;

        JScrollPane scrollPane = new JScrollPane();

        //Make the listBox with Checkboxes look like a rowheader.
        //This will place the component on the left corner of the scrollpane
        scrollPane.setRowHeaderView(listCheckBox);

        //Now, make the listbox with actual descriptions as the main view
        scrollPane.setViewportView(listDescription);

        // Align both the checkbox height and widths
        listDescription.setFixedCellHeight(20);
        listCheckBox.setFixedCellHeight(listDescription.getFixedCellHeight());
        listCheckBox.setFixedCellWidth(20);
        add(scrollPane, BorderLayout.CENTER);

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    private CheckBoxItem[] buildCheckBoxItems(int totalItems)
    {
        CheckBoxItem[] checkboxItems = new CheckBoxItem[totalItems];
        for (int counter = 0; counter < totalItems; counter++)
        {
            checkboxItems[counter] = new CheckBoxItem();
        }
        return checkboxItems;
    }

    private CheckBoxItem[] buildCheckBoxItems(int totalItems,
                                              int[] selected)
    {
        CheckBoxItem[] checkboxItems = new CheckBoxItem[totalItems];
        for (int counter = 0; counter < totalItems; counter++)
        {
            checkboxItems[counter] = new CheckBoxItem();
            for (int i = 0; i < selected.length; i++)
            {
                if (counter == selected[i])
                {
                    checkboxItems[counter].setChecked(true);
                    break;
                }
            }
        }
        return checkboxItems;
    }

    public String[] getSelectedListData()
    {
        ListModel model = listCheckBox.getModel();
        ListModel sm = listDescription.getModel();
        Vector names = new Vector();

        for (int i = 0; i < model.getSize(); i++)
        {
            CheckBoxItem item = (CheckBoxItem) model.getElementAt(i);
            if (item.isChecked())
            {
                names.addElement(sm.getElementAt(i));
            }
        }

        String[] str = new String[names.size()];
        names.copyInto(str);
        return str;
    }

    public void selectAll()
    {
        ListModel model = listCheckBox.getModel();
        for (int i = 0; i < model.getSize(); i++)
        {
            CheckBoxItem item = (CheckBoxItem) model.getElementAt(i);
            item.setChecked(true);
        }
        listCheckBox.repaint();
    }

    public void clearAll()
    {
        ListModel model = listCheckBox.getModel();
        for (int i = 0; i < model.getSize(); i++)
        {
            CheckBoxItem item = (CheckBoxItem) model.getElementAt(i);
            item.setChecked(false);
        }
        listCheckBox.repaint();
    }

    protected void setBorders()
    {
        listDescription.setBorder(new EmptyBorder(3, 0, 3, 3));
        listCheckBox.setBorder(new EmptyBorder(3, 6, 3, 3));
    }

    public void setListData(String[] data, int[] selected)
    {
        listCheckBox.setListData(buildCheckBoxItems(data.length, selected));
        listDescription.setListData(data);
    }
}