package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2019 Anton Troshin

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

import jworkspace.LangResource;

/**
 * Java Desktop constants
 * @author Anton Troshin
 */
interface DesktopConstants {
    /**
     * Tile image
     */
    int TILE_IMAGE = 1;
    /**
     * Center image
     */
    int CENTER_IMAGE = 2;
    /**
     * Stretch image
     */
    int STRETCH_IMAGE = 3;
    /**
     * Top left corner image
     */
    int TOP_LEFT_CORNER_IMAGE = 4;
    /**
     * Bottom left corner image
     */
    int BOTTOM_LEFT_CORNER_IMAGE = 5;
    /**
     * Top right corner image
     */
    int TOP_RIGHT_CORNER_IMAGE = 6;
    /**
     * Bottom right corner image
     */
    int BOTTOM_RIGHT_CORNER_IMAGE = 7;
    /**
     * Create new shortcut
     */
    String CREATE_SHORTCUT = "CREATE_SHORTCUT";
    /**
     * Grafient fill of background
     */
    String GRADIENT_FILL = "GRADIENT_FILL";
    /**
     * Paste icons
     */
    String PASTE = "PASTE";
    /**
     * Select all icons
     */
    String SELECT_ALL = "SELECT_ALL";
    /**
     * Select all icons
     */
    String BACKGROUND = "BACKGROUND";
    /**
     * Close all windows
     */
    String CLOSE_ALL_WINDOWS = "CLOSE_ALL_WINDOWS";
    /**
     * Show or hide cover
     */
    String SWITCH_COVER = "SWITCH_COVER";
    /**
     * Choose background image
     */
    String CHOOSE_BACKGROUND_IMAGE = "CHOOSE_BACKGROUND_IMAGE";
    /**
     *
     */
    String TOGGLE_WALLPAPER = "TOGGLE_WALLPAPER";
    /**
     *
     */
    String TOGGLE_GRADIENT = "TOGGLE_GRADIENT";
    /**
     *
     */
    String CHOOSE_GRADIENT_COLOR_1 = "CHOOSE_GRADIENT_COLOR_1";
    /**
     *
     */
    String CHOOSE_GRADIENT_COLOR_2 = "CHOOSE_GRADIENT_COLOR_2";
    /**
     *
     */
    String DESKTOP_DAT = "desktop.dat";
    /**
     * Icon on NORTH
     */
    int ICON_ON_NORTH = 0;
    /**
     * Icon on SOUTH
     */
    int ICON_ON_SOUTH = 1;
    /**
     * Icon on WEST
     */
    int ICON_ON_WEST = 2;
    /**
     * Icon on EAST
     */
    int ICON_ON_EAST = 3;
    /**
     * Top Left Icon
     */
    int TOP_LEFT_ICON = 0;
    /**
     * Top Right Icon
     */
    int TOP_RIGHT_ICON = 1;
    /**
     * Bottom Left Icon
     */
    int BOTTOM_LEFT_ICON = 2;
    /**
     * Bottom Right Icon
     */
    int BOTTOM_RIGHT_ICON = 3;
    /**
     * Frame offset
     */
    int FRAME_OFFSET = 20;
    /**
     *
     */
    String DESKTOP_NAME_DEFAULT = LangResource.getString("Desktop.defaultName");
    /**
     *
     */
    int SCRIPTED_METHOD_MODE = 0;
    /**
     *
     */
    int SCRIPTED_FILE_MODE = 1;
    /**
     *
     */
    int NATIVE_COMMAND_MODE = 2;
    /**
     *
     */
    int JAVA_APP_MODE = 3;
    /**
     *
     */
    String DEFAULT_ICON = "desktop/default.png";
    /**
     *
     */
    String DESKTOP_ICON_PANEL_NATIVE_COMMAND_BROWSE = "DesktopIconPanel.native.command.browse";
    /**
     *
     */
    String DESKTOP_ICONS_REPOSITORY_PARAMETER = "DESKTOP_ICONS_REPOSITORY";
    /**
     *
     */
    String CENTER = "Center";
    /**
     *
     */
    String EAST = "East";
    /**
     *
     */
    String NORTH = "North";
    /**
     *
     */
    String DOTS = "...";
}
