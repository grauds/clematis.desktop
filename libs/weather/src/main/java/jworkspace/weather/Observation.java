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

import java.util.Date;

import lombok.Data;

/**
 * A weather observation in accordance with Moscow VDNH station logs.
 * The data is provided by the website 'Reliable Prognosis', rp5.ru
 *
 * @author Anton Troshin
 */
@Data
public class Observation {
    /**
     * Weather station unique id (27612 for VDNH)
     */
    private Integer weatherStationId;
    /**
     * Local time in this location. Summer time (Daylight Saving Time) is taken into consideration
     */
    private Date date;
    /**
     * Air temperature (degrees Celsius) at 2 metre height above the earth's surface
     */
    private Float t;
    /**
     * Atmospheric pressure at weather station level (millimeters of mercury)
     */
    private Float pO;
    /**
     * Atmospheric pressure reduced to mean sea level (millimeters of mercury)
     */
    private Float p;
    /**
     * Pressure tendency: changes in atmospheric pressure over the last three hours (millimeters of mercury)
     */
    private Float pA;
    /**
     * Relative humidity (%) at a height of 2 metres above the earth's surface'
     */
    private Float u;
    /**
     * Mean wind direction (compass points) at a height of 10-12 metres
     * above the earth’s surface over the 10-minute period immediately preceding the observation
     */
    private WindDirection dd;
    /**
     * Mean wind speed at a height of 10-12 metres above the earth’s surface over the
     * 10-minute period immediately preceding the observation (meters per second)
     */
    private String ff;
    /**
     * Maximum gust value at a height of 10-12 metres above the earth’s surface over the 10-minute
     * period immediately preceding the observation (meters per second)
     */
    private String ff10;
    /**
     * Maximum gust value at a height of 10-12 metres above the earth’s surface between the periods
     * of observations (meters per second)
     */
    private String ff3;
    /**
     * Total cloud cover
     */
    private String n;
    /**
     * Present weather reported from a weather station
     */
    private String ww;
    /**
     * Past weather (weather between the periods of observation) 1
     */
    private String w1;
    /**
     * Past weather (weather between the periods of observation) 2
     */
    private String w2;
    /**
     * Minimum air temperature (degrees Celsius) during the past period (not exceeding 12 hours)
     */
    private Float tn;
    /**
     * Maximum air temperature (degrees Celsius) during the past period (not exceeding 12 hours)
     */
    private Float tx;
    /**
     * Clouds of the genera Stratocumulus, Stratus, Cumulus and Cumulonimbus
     */
    private String cl;
    /**
     * Amount of all the CL cloud present or, if no CL cloud is present, the amount of all the CM cloud present
     */
    private String nh;
    /**
     * Height of the base of the lowest clouds (m)
     */
    private String h;
    /**
     * Clouds of the genera Altocumulus, Altostratus and Nimbostratus
     */
    private String cm;
    /**
     * Clouds of the genera Cirrus, Cirrocumulus and Cirrostratus
     */
    private String ch;
    /**
     * Horizontal visibility (km)
     */
    private Float vv;
    /**
     * Dew point temperature at a height of 2 metres above the earth's surface (degrees Celsius)
     */
    private Float td;
    /**
     * Amount of precipitation (millimeters)
     */
    private Float rrr;
    /**
     * The period of time during which the specified amount of precipitation was accumulated
     */
    private Float tr;
    /**
     * State of the ground without snow or measurable ice cover
     */
    private String e;
    /**
     * The minimum soil surface temperature at night (degrees Celsius)
     */
    private Float tg;
    /**
     * State of the ground with snow or measurable ice cover.
     */
    private String eApostrophe;
    /**
     * Snow depth (cm)
     */
    private Float sss;
}
