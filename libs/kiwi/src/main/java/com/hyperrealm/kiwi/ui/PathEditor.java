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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_ROW_HEIGHT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.NORTH_POSITION;
import com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog;
import com.hyperrealm.kiwi.util.DirectoryPath;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * This class represents a component for editing a path (a list of
 * directories). It could be used for editing a binary search path, or a
 * Java classpath, for example. It provides <i>New</i>, <i>Delete</i>,
 * <i>Move Up</i>, and <i>Move Down</i> buttons.
 *
 * <p><center>
 * <img src="snapshot/PathEditor.gif"><br>
 * <i>An example PathEditor.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class PathEditor extends KPanel implements ActionListener, ListSelectionListener {

    private static final int DEFAULT_COLUMN_WIDTH = 100;

    private static final Dimension DEFAULT_SIZE = new Dimension(400, 400);

    private JTable paths;

    private KButton bDelete, bUp, bDown, bNew;

    private DefaultTableModel tmodel;

    private ListSelectionModel sel;

    private LocaleData loc;

    /**
     * Construct a new <code>PathEditor</code>.
     */

    public PathEditor() {

        setBackground(SystemColor.control);
        setLayout(DEFAULT_BORDER_LAYOUT);

        KPanel panel1 = new KPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);

        ResourceManager rm = KiwiUtils.getResourceManager();
        LocaleManager lm = LocaleManager.getDefault();

        loc = lm.getLocaleData("KiwiDialogs");

        bNew = new KButton(rm.getIcon("plus.png"));
        bNew.addActionListener(this);
        bNew.setToolTipText(loc.getMessage("kiwi.tooltip.new"));
        toolBar.add(bNew);

        bDelete = new KButton(rm.getIcon("minus.png"));
        bDelete.addActionListener(this);
        bDelete.setToolTipText(loc.getMessage("kiwi.tooltip.delete"));
        bDelete.setEnabled(false);
        toolBar.add(bDelete);

        bUp = new KButton(rm.getIcon("arrow_up.png"));
        bUp.addActionListener(this);
        bUp.setToolTipText(loc.getMessage("kiwi.tooltip.move_up"));
        bUp.setEnabled(false);
        toolBar.add(bUp);

        bDown = new KButton(rm.getIcon("arrow_down.png"));
        bDown.addActionListener(this);
        bDown.setToolTipText(loc.getMessage("kiwi.tooltip.move_down"));
        bDown.setEnabled(false);
        toolBar.add(bDown);

        panel1.add(toolBar);

        add(NORTH_POSITION, panel1);

        String[] data = {};
        paths = new KTable();
        paths.setRowHeight(DEFAULT_ROW_HEIGHT);
        tmodel = new DefaultTableModel();
        paths.setModel(tmodel);
        // paths.setTableHeader(null);
        paths.setAutoCreateColumnsFromModel(false);
        paths.setColumnSelectionAllowed(false);
        paths.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        paths.setShowGrid(false);
        sel = paths.getSelectionModel();
        sel.addListSelectionListener(this);

        PathListCellEditor cellEditor = new PathListCellEditor();

        cellEditor.addActionListener(this);

        tmodel.addColumn(loc.getMessage("kiwi.label.directory_list"));
        TableColumn col = new TableColumn(0, DEFAULT_COLUMN_WIDTH);

        paths.addColumn(col);
        col.setCellEditor(cellEditor);

        for (int i = 0; i < data.length; i++) {
            tmodel.addRow(new Object[]{data[i]});
        }

        KScrollPane scrollPane = new KScrollPane(paths);
        scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scrollPane.setBackground(Color.white);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants
            .VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants
            .HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ev) {
                if (paths.isEditing()) {
                    paths.getCellEditor().cancelCellEditing();
                }

                KScrollPane sp = (KScrollPane) ev.getComponent();
                Dimension sz = sp.getViewport().getSize();
                paths.setSize(sz);
            }
        });

        add(CENTER_POSITION, scrollPane);

        setSize(DEFAULT_SIZE);
    }

    /**
     * Handle events. This method is public as an implementation side-effect.
     */
    @SuppressWarnings("ReturnCount")
    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();
        boolean editing = paths.isEditing();

        if (o == bNew && !editing) {
            int i = paths.getRowCount();
            tmodel.addRow(new Object[]{""}); // root path???
            paths.editCellAt(i, 0);
            sel.setSelectionInterval(i, i);
        } else if (o == bDelete && !editing) {
            int i = paths.getSelectedRow();

            if (i < 0) {
                return;
            }
            if (sel.isSelectionEmpty()) {
                return;
            }

            tmodel.removeRow(i);
            if (i >= paths.getRowCount()) {
                i = 0;
            }

            sel.setSelectionInterval(i, i);
        } else if (o == bUp && !editing) {
            int i = paths.getSelectedRow();
            if (i < 1) {
                return;
            }

            tmodel.moveRow(i, i, i - 1);
            sel.setSelectionInterval(i - 1, i - 1);
        } else if (o == bDown && !editing) {
            int i = paths.getSelectedRow();
            if (i < 0 || i > (paths.getRowCount() - 2)) {
                return;
            }

            tmodel.moveRow(i, i, i + 1);
            sel.setSelectionInterval(i + 1, i + 1);
        }
    }

    /**
     * Return an array of the paths currently displayed by this view.
     */

    public DirectoryPath getDirectoryPath() {
        int c = tmodel.getRowCount();
        String[] strings = new String[c];
        for (int i = 0; i < c; i++) {
            strings[i] = (String) tmodel.getValueAt(i, 0);
        }

        return (new DirectoryPath(strings));
    }

    /**
     * Set the list of paths to be displayed by this view.
     */

    public void setDirectoryPath(DirectoryPath path) {
        String[] dirs = path.getDirectories();

        tmodel.setNumRows(0);
        for (String dir : dirs) {
            tmodel.addRow(new Object[]{dir});
        }
    }

    /* the custom cell editor */

    public void valueChanged(ListSelectionEvent evt) {
        boolean empty = sel.isSelectionEmpty();

        bUp.setEnabled(!empty);
        bDown.setEnabled(!empty);
        bDelete.setEnabled(!empty);
    }

    private class PathListCellEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

        private KPanel jp;

        private JTextField text;

        private JButton bBrowse;

        // todo: action listeners are not working?
        private final ArrayList<ActionListener> actionListeners;

        private int row = -1;

        private KFileChooserDialog dSelect = null;

        PathListCellEditor() {
            jp = new KPanel();
            jp.setLayout(new BorderLayout(2, 2));

            text = new JTextField();
            text.addActionListener(this);
            jp.add(CENTER_POSITION, text);

            bBrowse = new KButton(loc.getMessage("kiwi.button.browse") + "...");
            bBrowse.setMargin(new Insets(0, 2, 0, 2));
            bBrowse.addActionListener(this);
            jp.add(EAST_POSITION, bBrowse);

            actionListeners = new ArrayList<>();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int columnID, int rowIndex) {
            text.setText((String) value);
            return (jp);
        }

        public Object getCellEditorValue() {
            return (text.getText());
        }

        public boolean isCellEditable(EventObject ev) {
            boolean ok = false;

            if (ev == null) {
                text.requestFocus();
                return (true);
            }

            if (ev instanceof MouseEvent) {

                MouseEvent mev = (MouseEvent) ev;
                int newrow = ((JTable) mev.getSource())
                    .rowAtPoint(new Point(mev.getX(), mev.getY()));

                if (newrow == row) {
                    text.requestFocus();
                    ok = true;
                } else {
                    row = newrow;
                }

            }
            return (ok);
        }

        public boolean shouldSelectCell(EventObject evt) {
//      text.requestFocus();
            return (true);
        }

        public boolean stopCellEditing() {
            // if input is valid...
            fireEditingStopped();
            return (true);
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public void addActionListener(ActionListener l) {
            synchronized (actionListeners) {
                actionListeners.add(l);
            }
        }

        public void removeActionListener(ActionListener l) {
            synchronized (actionListeners) {
                actionListeners.remove(l);
            }
        }

        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            if (o == text) {
                // if input is valid...
                fireEditingStopped();
            } else if (o == bBrowse) {
                if (dSelect == null) {
                    dSelect = new KFileChooserDialog(KiwiUtils.getPhantomFrame(),
                        "Directory Selection",
                        KFileChooser.OPEN_DIALOG);

                    dSelect.setFileSelectionMode(KFileChooser.DIRECTORIES_ONLY);
                }
                KiwiUtils.centerWindow(dSelect);
                dSelect.setVisible(true);
                if (!dSelect.isCancelled()) {
                    String p = dSelect.getSelectedFile().getAbsolutePath();
                    text.setText(p);
                    fireEditingStopped();
                }
            }
        }
    }

}
