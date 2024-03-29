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

import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;

/**
 * A chooser component for <code>UIElement</code>s. The component consists of
 * a scrollable list box to the right of a display area which is used to
 * display the currently-selected element.
 *
 * <p><center>
 * <img src="snapshot/UIElementChooser.gif"><br>
 * <i>An example UIElementChooser (using a TextureViewer).</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class UIElementChooser extends KPanel {

    private static final BevelBorder BEVEL_BORDER = new BevelBorder(BevelBorder.LOWERED);

    private UIElementViewer elementViewer = null;

    private DefaultListModel model;

    private JList jlist;

    private KPanel viewpane;

    /**
     * Construct a new <code>UIElementChooser</code> with the specified viewer
     * and element list.
     *
     * @param viewer A viewer component for displaying the element.
     * @param list   A List of elements that can be picked from.
     */

    public UIElementChooser(UIElementViewer viewer, List<UIElement> list) {
        this();

        setElementViewer(viewer);
        setElementList(list);
    }

    /**
     * Construct a new <code>UIElementChooser</code>.
     */

    public UIElementChooser() {

        setLayout(DEFAULT_BORDER_LAYOUT);

        model = new DefaultListModel();

        jlist = new JList();
        jlist.setModel(model);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(EAST_POSITION, new KScrollPane(jlist));

        ListSelectionListener selListener = new ListSelectionListener();
        jlist.addListSelectionListener(selListener);

        viewpane = new KPanel();
        viewpane.setLayout(new GridLayout(1, 0));
        viewpane.setBorder(BEVEL_BORDER);
        add(CENTER_POSITION, viewpane);
    }

    /**
     * Set the viewer component to be used to display elements.
     *
     * @param viewer The new viewer.
     */

    public void setElementViewer(UIElementViewer viewer) {
        if (elementViewer != null) {
            viewpane.remove(elementViewer.getViewerComponent());
        }

        JComponent c = viewer.getViewerComponent();
        viewpane.add(c);
        elementViewer = viewer;
        c.invalidate();
        validate();
    }

    /**
     * Set the list of elements to allow selection from.
     *
     * @param list The list of elements.
     */

    public void setElementList(List<UIElement> list) {
        model.removeAllElements();

        for (UIElement uiElement : list) {
            model.addElement(uiElement);
        }

        if (model.getSize() > 0) {
            jlist.setSelectedIndex(0);
        }
    }

    /**
     * Get the currently selected item.
     *
     * @return The currently selected <code>UIElement</code>, or
     * <code>null</code> if there is no selection.
     */

    public UIElement getSelectedItem() {
        return ((UIElement) jlist.getSelectedValue());
    }

    /**
     * Set the currently selected item.
     *
     * @param element The <code>UIElement</code> to select.
     */

    public void setSelectedItem(UIElement element) {
        jlist.setSelectedValue(element, true);
    }

    /**
     * Set the currently selected item by name.
     *
     * @param name The name of the element to select; the name of each
     *             <code>UIElement</code> in the list is compared to the specified name; if
     *             the names match, the corresponding item in the list is selected.
     * @see com.hyperrealm.kiwi.ui.UIElement#getName
     */

    public void setSelectedItem(String name) {
        for (int i = 0; i < model.getSize(); i++) {
            UIElement e = (UIElement) model.getElementAt(i);
            if (e.getName().equals(name)) {
                jlist.setSelectedIndex(i);
            }
        }
    }

    /**
     * Request focus for this component.
     */

    public void requestFocus() {
        jlist.requestFocus();
    }

    /**
     * ListSelectionListener
     */

    private class ListSelectionListener implements javax.swing.event.ListSelectionListener {
        public void valueChanged(ListSelectionEvent evt) {
            Object elem = jlist.getSelectedValue();
            if ((elem != null) && (elementViewer != null)) {
                elementViewer.showElement((UIElement) elem);
            }
        }
    }

}
