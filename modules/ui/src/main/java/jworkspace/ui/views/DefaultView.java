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

import javax.swing.JMenu;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.ui.api.IView;

/**
 * Default user gui view.
 * @author Anton Troshin
 */
public class DefaultView extends KPanel implements IView {
    /**
     * Show this window
     */
    static final String SHOW = "SHOW";
    /**
     * Save path. Relative to user.home
     */
    private String path = "";

    /**
     * Default public constructor
     */
    DefaultView() {
        super();
        setOpaque(false);
    }

    /**
     * View activated or deactivated
     */
    public void activated(boolean flag) {
    }

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create() {
    }

    /**
     * Returns path for saving component data.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path for saving component data.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get all option panels for this shell
     */
    public PropertiesPanel[] getOptionPanels() {
        return null;
    }

    /**
     * Get menu set for this View.
     * This menu set will be swithed off
     * every time view becomes inactive.
     */
    public JMenu[] getMenu() {
        return null;
    }

    /**
     * Returns modified flag for the view
     */
    public boolean isModified() {
        return false;
    }

    /**
     * Set this flag to true, if you want component
     * to be unique among all workspace views.
     */
    public boolean isUnique() {
        return true;
    }

    /**
     * Load view from disk
     */
    public void load() {
    }

    /**
     * Reset the state of view
     */
    public void reset() {
    }

    /**
     * Save all settings to default path
     */
    public void save() {
    }

    /**
     * Update view
     */
    public void update() {
    }
}
