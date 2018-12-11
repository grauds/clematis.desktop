package jworkspace.ui.desktop;

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

import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * Desktop ICON data is needed for copy/paste
 * operations and dragndrop.
 */

/**
 * Log:
 * Added working directory field
 * 29.05.02
 */
class DesktopIconData
    implements Serializable {
    // Icon data
    private String name = null;
    private String commandLine = null;
    private String working_dir = null;
    private int mode = 0;
    private ImageIcon icon = null;
    private Integer xPos = new Integer(0);
    private Integer yPos = new Integer(0);
    private Integer height = new Integer(0);
    private Integer width = new Integer(0);
    private String comments = null;

    public DesktopIconData(String name,
                           String commandLine, int xPos, int yPos,
                           int width, int height, int mode, ImageIcon icon) {
        this.name = name;
        this.commandLine = commandLine;
        this.xPos = new Integer(xPos);
        this.yPos = new Integer(yPos);
        this.width = new Integer(width);
        this.height = new Integer(height);
        this.icon = icon;
        this.mode = mode;
    }

    public DesktopIconData(String name,
                           String commandLine, String working_dir,
                           int xPos, int yPos,
                           int width, int height, int mode, ImageIcon icon,
                           String comments) {
        this.name = name;
        this.commandLine = commandLine;
        this.working_dir = working_dir;
        this.xPos = new Integer(xPos);
        this.yPos = new Integer(yPos);
        this.width = new Integer(width);
        this.height = new Integer(height);
        this.icon = icon;
        this.mode = mode;
        this.comments = comments;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(java.lang.Integer width) {
        this.width = width;
    }

    public String getWorkingDir() {
        return working_dir;
    }

    public void setWorkingDir(String working_dir) {
        this.working_dir = working_dir;
    }

    public Integer getXPos() {
        return xPos;
    }

    public void setXPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getYPos() {
        return yPos;
    }

    public void setYPos(Integer yPos) {
        this.yPos = yPos;
    }
}