package jworkspace.weather;
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

/**
 * @author Anton Troshin
 */
public enum WindDirection {

    NO_WIND("Calm, no wind"),

    NORTH("Wind blowing from the north"),
    NORTH_EAST("Wind blowing from the north-east"),
    NORTH_WEST("Wind blowing from the north-west"),
    NORTH_NORTHEAST("Wind blowing from the north-northeast"),
    NORTH_NORTHWEST("Wind blowing from the north-northwest"),

    EAST("Wind blowing from the east"),
    EAST_NORTHEAST("Wind blowing from the east-northeast"),
    EAST_NORTHWEST("Wind blowing from the east-northwest"),
    EAST_SOUTHEAST("Wind blowing from the east-southeast"),
    EAST_SOUTHWEST("Wind blowing from the east-southwest"),

    WEST("Wind blowing from the west"),
    WEST_NORTHEAST("Wind blowing from the west-northeast"),
    WEST_NORTHWEST("Wind blowing from the west-northwest"),
    WEST_SOUTHEAST("Wind blowing from the west-southeast"),
    WEST_SOUTHWEST("Wind blowing from the west-southwest"),

    SOUTH("Wind blowing from the south"),
    SOUTH_EAST("Wind blowing from the south-east"),
    SOUTH_WEST("Wind blowing from the south-west"),
    SOUTH_SOUTHEAST("Wind blowing from the south-southeast"),
    SOUTH_SOUTHWEST("Wind blowing from the south-southwest"),

    VARIABLE_WIND("variable wind direction");

    private final String description;

    WindDirection(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static WindDirection fromString(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (WindDirection windDirection : WindDirection.values()) {
            if (windDirection.getDescription().equalsIgnoreCase(text)) {
                return windDirection;
            }
        }
        throw new IllegalArgumentException("No wind direction for string: " + text);
    }

}
