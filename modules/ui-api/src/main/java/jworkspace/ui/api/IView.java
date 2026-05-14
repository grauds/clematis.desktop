package jworkspace.ui.api;

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

import jworkspace.api.IWorkspaceComponent;

/**
 * User gui view. Such shell have to load itself, restore state
 * or save itself on disk. It also has to provide option panels,
 * menus and buttons to manage state of view.
 *
 * @author Anton Troshin
 */
public interface IView extends IWorkspaceComponent {

    /**
     * Event to show a component as a view
     */
    int ADD_VIEW_EVENT = 1000;
    /**
     * Event to show a component in internal frame on the desktop.
     */
    int DISPLAY_IN_DESKTOP_EVENT = 1001;
    /**
     * Switch menu in the menu bar
     */
    int SWITCH_MENU_EVENT = 1002;
    /**
     * Event to show a view as a controlled frame
     */
    int DISPLAY_AS_FRAME = 1003;
    /**
     *
     */
    String FRAME_PARAMETER = "frame";
    /**
     *
     */
    String MENUS_PARAMETER = "menus";
    /**
     *
     */
    String FLAG_PARAMETER = "flag";
    /**
     *
     */
    String VIEW_PARAMETER = "view";
    /**
     *
     */
    String DISPLAY_PARAMETER = "display";
    /**
     *
     */
    String REGISTER_PARAMETER = "register";

    /**
     * View activated or deactivated
     */
    void activated(boolean flag);

    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    void create();

    /**
     * Returns path for saving component data.
     */
    String getPath();

    /**
     * Sets path for saving component data.
     */
    void setPath(String path);

    /**
     * Get all option panels for this shell
     */
    PropertiesPanel[] getOptionPanels();

    /**
     * Get menu set for this View.
     * This menu set will be swithed off
     * every time view becomes inactive.
     */
    JMenu[] getMenu();

    /**
     * Returns modified flag for the view
     */
    boolean isModified();

    /**
     * Set this flag to true, if you want component
     * to be unique among all workspace views.
     */
    boolean isUnique();

    /**
     * Update view
     */
    void update();
}