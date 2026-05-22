package jworkspace.ui.util;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

@Deprecated
public class GraphicsUtils {

    private GraphicsUtils() {}

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, int x, int y, int width, int height) {
        drawUpperLowerDashes(g, x, y, width, height);
        drawLeftRightDashes(g, x, y, width, height);
    }

    private static void drawUpperLowerDashes(Graphics g, int x, int y, int width, int height) {

        int vx;

        // draw upper and lower horizontal dashes
        for (vx = x; vx < (x + width); vx += 2) {
            g.drawLine(vx, y, vx, y);
            g.drawLine(vx, y + height - 1, vx, y + height - 1);
        }
    }

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, Rectangle rect) {

        int x = rect.getLocation().x;
        int y = rect.getLocation().y;
        int width = rect.getSize().width;
        int height = rect.getSize().height;

        drawUpperLowerDashes(g, x, y, width, height);
        drawLeftRightDashes(g, x, y, width, height);
    }

    private static void drawLeftRightDashes(Graphics g, int x, int y, int width, int height) {

        int vy;

        // draw left and right vertical dashes
        for (vy = y; vy < (y + height); vy += 2) {
            g.drawLine(x, vy, x, vy);
            g.drawLine(x + width - 1, vy, x + width - 1, vy);
        }
    }

    /**
     * Finds distance between two points
     */
    public static int distance(int i, int j) {
        return max(i, j) - min(i, j);
    }

    /**
     * Finds distance between two 2D points
     */
    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * 2D routine, that checks whether if given point is
     * on north from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isNorthernQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) <= distance(center.y, b.y)) && (center.y >= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on south from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isSouthernQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) <= distance(center.y, b.y)) && (center.y <= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isEasternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) && (center.x <= b.x);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isWesternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) && (center.x >= b.x);
    }

    /**
     * Finds maximum of two values
     */
    public static int max(int i, int j) {
        return Math.max(i, j);
    }

    /**
     * Finds the minimum of two values
     */
    public static int min(int i, int j) {
        return Math.min(i, j);
    }
}
