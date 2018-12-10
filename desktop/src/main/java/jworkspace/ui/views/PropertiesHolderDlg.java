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
import jworkspace.LangResource;
import kiwi.ui.dialog.ComponentDialog;

/**
 * This dialog holds a properties panel for a view.
 * Each panel is placed as a component to a tab view.
 * After user selects to close dialog, this class
 * invokes <code>syncData</code> method on every panel
 * in tabbed sequence, thus delegating of save procedure.
 * If no such method is discovered, this class silently
 * ignores the situation.
 */
public class PropertiesHolderDlg extends ComponentDialog {
    private JTabbedPane tabbed_pane;

    /**
     * Constructor for property viewer panel.
     *
     * @param parent        frame
     * @param option_panels array of JPanels as an option panels for current view.
     */
    public PropertiesHolderDlg(Frame parent, JPanel[] option_panels) {
        super(parent, LangResource.getString("PropertiesHolder.title"), true);
        if (option_panels == null || option_panels.length == 0) {
            KPanel empty_panel = new KPanel();
            empty_panel.setPreferredSize(new Dimension(250, 300));
            empty_panel.setLayout(new BorderLayout());
            JLabel l = new JLabel(LangResource.getString("PropertiesHolder.empty.message"));
            l.setHorizontalAlignment(JLabel.CENTER);
            l.setOpaque(false);
            empty_panel.add(l, BorderLayout.CENTER);
            tabbed_pane.addTab(LangResource.getString("PropertiesHolder.empty.tab"), empty_panel);
        } else {
            for (int i = 0; i < option_panels.length; i++) {
                if (option_panels[i].getName() != null) {
                    tabbed_pane.addTab(option_panels[i].getName(), option_panels[i]);
                } else {
                    tabbed_pane.addTab(LangResource.getString("PropertiesHolder.empty.tab.header"), option_panels[i]);
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
        if (tabbed_pane == null) {
            return true;
        }
        for (int i = 0; i < tabbed_pane.getTabCount(); i++) {
            try {
                Method sync_data = tabbed_pane.getComponentAt(i).getClass().
                    getMethod("syncData");
                sync_data.invoke(tabbed_pane.getComponentAt(i));
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    protected boolean setData() {
        if (tabbed_pane == null) {
            return true;
        }
        for (int i = 0; i < tabbed_pane.getTabCount(); i++) {
            try {
                Method sync_data = tabbed_pane.getComponentAt(i).getClass().
                    getMethod("setData");
                sync_data.invoke(tabbed_pane.getComponentAt(i));
            } catch (NoSuchMethodException ex) {
            } catch (InvocationTargetException ex) {
            } catch (IllegalAccessException ex) {
            }
        }
        return true;
    }

    protected JComponent buildDialogUI() {
        setComment(null);
        tabbed_pane = new JTabbedPane();
        tabbed_pane.setOpaque(false);
        return tabbed_pane;
    }
}
