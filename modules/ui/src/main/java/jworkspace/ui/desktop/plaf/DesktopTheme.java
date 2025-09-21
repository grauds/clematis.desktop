package jworkspace.ui.desktop.plaf;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import jworkspace.ui.api.Constants;
import lombok.Data;

/**
 * The DesktopTheme class represents the appearance settings of a desktop.
 * It manages properties such as wallpaper, background colors, gradient filling, and rendering modes.
 */
@Data
public class DesktopTheme {

    private transient int hpos = 0;

    @SuppressWarnings("checkstyle:MagicNumber")
    private transient int hstep = 50;

    private transient int vpos = 0;

    @SuppressWarnings("checkstyle:MagicNumber")
    private transient int vstep = 50;
    /**
     * Image icon - desktop wallpaper.
     */
    private ImageIcon cover = null;
    /**
     * Path to image
     */
    private String pathToImage = null;
    /**
     * 2nd color of desktop.
     */
    private Color secondaryBackground = UIManager.getColor("desktop");
    /**
     * Gradient fill flag.
     */
    private boolean gradientFill = false;
    /**
     * Current image render mode
     */
    private int renderMode = 2;
    /**
     * Image cover is visible?
     */
    private boolean coverVisible = true;

    public void switchGradientFill() {
        this.gradientFill = !this.gradientFill;
    }

    public void switchCoverVisible() {
        this.coverVisible = !this.coverVisible;
    }

    /**
     * Returns background image of current desktop
     */
    public ImageIcon getCover() {
        if (cover == null) {
            cover = loadCover();
        }
        return cover;
    }

    /**
     * Sets cover image for desktop
     */
    public void setCover(String path) {
        pathToImage = path;
        this.cover = loadCover();
    }

    public void setCover(ImageIcon cover) {
        this.cover = cover;
    }

    private ImageIcon loadCover() {
        if (pathToImage != null) {
            cover = new ImageIcon(pathToImage);
        }
        return cover;
    }

    /**
     * Set render mode
     */
    public void setRenderMode(int mode) {
        if (mode != Constants.CENTER_IMAGE
            && mode != Constants.TILE_IMAGE
            && mode != Constants.STRETCH_IMAGE
            && mode != Constants.TOP_LEFT_CORNER_IMAGE
            && mode != Constants.TOP_RIGHT_CORNER_IMAGE
            && mode != Constants.BOTTOM_LEFT_CORNER_IMAGE
            && mode != Constants.BOTTOM_RIGHT_CORNER_IMAGE) {
            throw new IllegalArgumentException("Illegal render mode");
        }

        renderMode = mode;
    }

    /**
     * Get contrast color for desktop selection borders and other marker components.
     */
    public Color getSelectionColor(Color background) {
        float[] comp = background.getColorComponents(null);

        if (Math.sqrt(comp[1] * comp[1] + comp[2] * comp[2]) < 0.7) {
            return Color.white;
        } else {
            return Color.black;
        }
    }

}
