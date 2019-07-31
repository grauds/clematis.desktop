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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesHolderDlg.class);

    private JTabbedPane tabbedPane;

    private List<PropertiesPanel> propertiesPanelList = new ArrayList<>();
    /**
     * Constructor for property viewer panel.
     *
     * @param parent        frame
     * @param optionPanels array of JPanels as an option panels for current view.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    PropertiesHolderDlg(Frame parent, PropertiesPanel[] optionPanels) {

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
            for (PropertiesPanel optionPanel : optionPanels) {
                if (!(optionPanel instanceof Component)) {
                    continue;
                }
                propertiesPanelList.add(optionPanel);

                // add to layout
                if (optionPanel.getName() != null) {
                    tabbedPane.addTab(optionPanel.getName(), (Component) optionPanel);
                } else {
                    tabbedPane.addTab(LangResource.getString("PropertiesHolder.empty.tab.header"),
                        (Component) optionPanel);
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
        boolean result = true;
        for (PropertiesPanel propertiesPanel : propertiesPanelList) {
            result &= propertiesPanel.syncData();
        }
        return result;
    }

    protected boolean setData() {
        boolean result = true;
        for (PropertiesPanel propertiesPanel : propertiesPanelList) {
            result &= propertiesPanel.setData();
        }
        return result;
    }

    protected JComponent buildDialogUI() {
        setComment(null);
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        return tabbedPane;
    }
}
