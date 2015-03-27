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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/
import kiwi.ui.KPanel;
import javax.swing.*;
import java.io.IOException;

import jworkspace.ui.IView;
/**
 * Default user gui view.
 */
public class DefaultView extends KPanel implements IView
{
    /**
     * Show this window
     */
    public final static String SHOW = "SHOW";
    /**
     * Modified flag is open to all descendants
     * of this class.
     */
    protected boolean modified_flag = false;
    /**
     * If this view active?
     */
    protected boolean active = false;
    /**
     * Save path. Relative to user.home
     */
    private String path = "";
    /**
     * View activated or deactivated
     */
    public void activated(boolean flag) { active = flag; }

    /**
     * Default public constructor
     */
    public DefaultView()
    {
        super();
        setOpaque(false);
    }

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create()  { }

    /**
     * Returns path for saving component data.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets path for saving component data.
     */
    public void setPath(String path)
    {
        this.path = new String(path);
    }

    /**
     * Get all option panels for this shell
     */
    public JPanel[] getOptionPanels()
    {
        return null;
    }

    /**
     * Get menu set for this View.
     * This menu set will be swithed off
     * every time view becomes inactive.
     */
    public JMenu[] getMenu()
    {
        return null;
    }

    /**
     * Returns modified flag for the view
     */
    public boolean isModified()
    {
        return modified_flag;
    }

    /**
     * Set this flag to true, if you want component
     * to be unique among all workspace views.
     */
    public boolean isUnique()
    {
        return true;
    }

    /**
     * Load view from disk
     */
    public void load() throws IOException { }

    /**
     * Reset the state of view
     */
    public void reset() { }

    /**
     * Save all settings to default path
     */
    public void save() throws IOException { }

    /**
     * Update view
     */
    public void update() { }
}
