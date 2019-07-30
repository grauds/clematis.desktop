package jworkspace.ui.views;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import jworkspace.LangResource;
/**
 * This dialog holds a properties panel for a view. Each panel is placed as a component to a tab view.
 * After user selects to close dialog, this class invokes <code>syncData</code> method on every panel
 * in tabbed sequence, thus delegating of save procedure. If no such method is discovered, this class silently
 * ignores the situation.
 * @author Anton Troshin
 */
public class PropertiesHolderDlg extends ComponentDialog {

    private JTabbedPane tabbedPane;

    /**
     * Constructor for property viewer panel.
     *
     * @param parent        frame
     * @param optionPanels array of JPanels as an option panels for current view.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public PropertiesHolderDlg(Frame parent, JPanel[] optionPanels) {

        super(parent, LangResource.getString("PropertiesHolder.title"), true);

        if (optionPanels == null || optionPanels.length == 0) {

            KPanel emptyPanel = new KPanel();
            emptyPanel.setPreferredSize(new Dimension(250, 300));
            emptyPanel.setLayout(new BorderLayout());
            JLabel l = new JLabel(LangResource.getString("PropertiesHolder.empty.message"));
            l.setHorizontalAlignment(JLabel.CENTER);
            l.setOpaque(false);
            emptyPanel.add(l, BorderLayout.CENTER);

            tabbedPane.addTab(LangResource.getString("PropertiesHolder.empty.tab"), emptyPanel);
        } else {
            for (JPanel optionPanel : optionPanels) {
                if (optionPanel.getName() != null) {
                    tabbedPane.addTab(optionPanel.getName(), optionPanel);
                } else {
                    tabbedPane.addTab(LangResource.getString("PropertiesHolder.empty.tab.header"), optionPanel);
                }
            }
        }
        pack();
        setResizable(false);
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    protected boolean accept() {
        if (tabbedPane == null) {
            return true;
        }
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            try {
                Method syncData = tabbedPane.getComponentAt(i).getClass().
                    getMethod("syncData");
                syncData.invoke(tabbedPane.getComponentAt(i));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {

            }
        }
        return true;
    }

    protected boolean setData() {
        if (tabbedPane == null) {
            return true;
        }
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            try {
                Method syncData = tabbedPane.getComponentAt(i).getClass().
                    getMethod("setData");
                syncData.invoke(tabbedPane.getComponentAt(i));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        return true;
    }

    protected JComponent buildDialogUI() {
        setComment(null);
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        return tabbedPane;
    }
}
