/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

import com.hyperrealm.kiwi.util.Config;

/**
 * A color theme object that can be constructed from a <code>Config</code>
 * object. This allows themes to be read from data files.
 *
 * @author Mark Lindner
 */

public class ColorTheme extends DefaultMetalTheme {

    private String name;

    private ColorUIResource primary1, primary2, primary3;

    private ColorUIResource secondary1, secondary2, secondary3;

    private ColorUIResource black, white;

    private ColorUIResource acceleratorForegroundColor,
        acceleratorSelectedForegroundColor,
        controlColor,
        controlDarkShadowColor,
        controlDisabledColor,
        controlHighlightColor,
        controlInfoColor,
        controlShadowColor,
        controlTextColor,
        desktopColor,
        focusColor,
        highlightedTextColor,
        inactiveControlTextColor,
        inactiveSystemTextColor,
        menuBackgroundColor,
        menuDisabledForegroundColor,
        menuForegroundColor,
        menuSelectedBackgroundColor,
        menuSelectedForegroundColor,
        primaryControlColor,
        primaryControlDarkShadowColor,
        primaryControlHighlightColor,
        primaryControlInfoColor,
        primaryControlShadowColor,
        separatorBackgroundColor,
        separatorForegroundColor,
        systemTextColor,
        textHighlightColor,
        userTextColor,
        windowBackgroundColor,
        windowTitleBackgroundColor,
        windowTitleForegroundColor,
        windowTitleInactiveBackgroundColor,
        windowTitleInactiveForegroundColor;

    private FontUIResource controlTextFont,
        menuTextFont,
        subTextFont,
        systemTextFont,
        userTextFont,
        windowTitleFont;

    /**
     * Construct a new <code>ColorTheme</code>.
     *
     * @param config The <code>Config</code> object from which color and font
     *               properties will be read.
     */

    public ColorTheme(Config config) {

        primary1 = super.getPrimary1();
        primary2 = super.getPrimary2();
        primary3 = super.getPrimary3();

        secondary1 = super.getSecondary1();
        secondary2 = super.getSecondary2();
        secondary3 = super.getSecondary3();

        black = super.getBlack();
        white = super.getWhite();

        init(config);
    }

    /**
     * Get the name of this color theme.
     *
     * @return The name of the theme.
     */

    public String getName() {
        return (name);
    }

    /* primary and secondary colors */

    protected ColorUIResource getPrimary1() {
        return (primary1);
    }

    protected ColorUIResource getPrimary2() {
        return (primary2);
    }

    protected ColorUIResource getPrimary3() {
        return (primary3);
    }

    protected ColorUIResource getSecondary1() {
        return (secondary1);
    }

    protected ColorUIResource getSecondary2() {
        return (secondary2);
    }

    protected ColorUIResource getSecondary3() {
        return (secondary3);
    }

    protected ColorUIResource getBlack() {
        return (black);
    }

    protected ColorUIResource getWhite() {
        return (white);
    }

    /* specific colors */

    public ColorUIResource getAcceleratorForeground() {
        return (acceleratorForegroundColor);
    }

    public ColorUIResource getAcceleratorSelectedForeground() {
        return (acceleratorSelectedForegroundColor);
    }

    public ColorUIResource getControl() {
        return (controlColor);
    }

    public ColorUIResource getControlDarkShadow() {
        return (controlDarkShadowColor);
    }

    public ColorUIResource getControlDisabled() {
        return (controlDisabledColor);
    }

    public ColorUIResource getControlHighlight() {
        return (controlHighlightColor);
    }

    public ColorUIResource getControlInfo() {
        return (controlInfoColor);
    }

    public ColorUIResource getControlShadow() {
        return (controlShadowColor);
    }

    public ColorUIResource getControlTextColor() {
        return (controlTextColor);
    }

    public ColorUIResource getDesktopColor() {
        return (desktopColor);
    }

    public ColorUIResource getFocusColor() {
        return (focusColor);
    }

    public ColorUIResource getHighlightedTextColor() {
        return (highlightedTextColor);
    }

    public ColorUIResource getInactiveControlTextColor() {
        return (inactiveControlTextColor);
    }

    public ColorUIResource getInactiveSystemTextColor() {
        return (inactiveSystemTextColor);
    }

    public ColorUIResource getMenuBackground() {
        return (menuBackgroundColor);
    }

    public ColorUIResource getMenuDisabledForeground() {
        return (menuDisabledForegroundColor);
    }

    public ColorUIResource getMenuForeground() {
        return (menuForegroundColor);
    }

    public ColorUIResource getMenuSelectedBackground() {
        return (menuSelectedBackgroundColor);
    }

    public ColorUIResource getMenuSelectedForeground() {
        return (menuSelectedForegroundColor);
    }

    public ColorUIResource getPrimaryControl() {
        return (primaryControlColor);
    }

    public ColorUIResource getPrimaryControlDarkShadow() {
        return (primaryControlDarkShadowColor);
    }

    public ColorUIResource getPrimaryControlHighlight() {
        return (primaryControlHighlightColor);
    }

    public ColorUIResource getPrimaryControlInfo() {
        return (primaryControlInfoColor);
    }

    public ColorUIResource getPrimaryControlShadow() {
        return (primaryControlShadowColor);
    }

    public ColorUIResource getSeparatorBackground() {
        return (separatorBackgroundColor);
    }

    public ColorUIResource getSeparatorForeground() {
        return (separatorForegroundColor);
    }

    public ColorUIResource getSystemTextColor() {
        return (systemTextColor);
    }

    public ColorUIResource getTextHighlightColor() {
        return (textHighlightColor);
    }

    public ColorUIResource getUserTextColor() {
        return (userTextColor);
    }

    public ColorUIResource getWindowBackground() {
        return (windowBackgroundColor);
    }

    public ColorUIResource getWindowTitleBackground() {
        return (windowTitleBackgroundColor);
    }

    public ColorUIResource getWindowTitleForeground() {
        return (windowTitleForegroundColor);
    }

    public ColorUIResource getWindowTitleInactiveBackground() {
        return (windowTitleInactiveBackgroundColor);
    }

    public ColorUIResource getWindowTitleInactiveForeground() {
        return (windowTitleInactiveForegroundColor);
    }

    /* fonts */

    public FontUIResource getControlTextFont() {
        return (controlTextFont);
    }

    public FontUIResource getMenuTextFont() {
        return (menuTextFont);
    }

    public FontUIResource getSubTextFont() {
        return (subTextFont);
    }

    public FontUIResource getSystemTextFont() {
        return (systemTextFont);
    }

    public FontUIResource getUserTextFont() {
        return (userTextFont);
    }

    public FontUIResource getWindowTitleFont() {
        return (windowTitleFont);
    }

    /* Read the properties from the Config object. */
    @SuppressWarnings("")
    private void init(Config config) {

        name = config.getString("name", "Unnamed Color Theme");

        // primary color resources

        primary1 = new ColorUIResource(config.getColor("color.primary1", super.getPrimary1()));
        primary2 = new ColorUIResource(config.getColor("color.primary2", super.getPrimary2()));
        primary3 = new ColorUIResource(config.getColor("color.primary3", super.getPrimary3()));

        // secondary color resources

        secondary1 = new ColorUIResource(config.getColor("color.secondary1", super.getSecondary1()));
        secondary2 = new ColorUIResource(config.getColor("color.secondary2", super.getSecondary2()));
        secondary3 = new ColorUIResource(config.getColor("color.secondary3", super.getSecondary3()));

        // black & white color resources

        black = new ColorUIResource(config.getColor("color.black", super.getBlack()));
        white = new ColorUIResource(config.getColor("color.white", super.getWhite()));

        // specific color resources

        acceleratorForegroundColor
            = new ColorUIResource(config.getColor("color.acceleratorForeground",
            super.getAcceleratorForeground()));

        acceleratorSelectedForegroundColor
            = new ColorUIResource(config.getColor("color.acceleratorSelectedForeground",
            super.getAcceleratorSelectedForeground()));

        controlColor
            = new ColorUIResource(config.getColor("color.control", super.getControl()));


        controlDarkShadowColor
            = new ColorUIResource(config.getColor("color.controlDarkShadow", super.getControlDarkShadow()));

        controlDisabledColor
            = new ColorUIResource(config.getColor("color.controlDisabled", super.getControlDisabled()));
        controlHighlightColor
            = new ColorUIResource(config.getColor("color.controlHighlight", super.getControlHighlight()));

        controlInfoColor
            = new ColorUIResource(config.getColor("color.controlInfo", super.getControlInfo()));

        controlShadowColor
            = new ColorUIResource(config.getColor("color.controlShadow", super.getControlShadow()));

        controlTextColor = new ColorUIResource(config.getColor("color.controlText", super.getControlTextColor()));

        desktopColor = new ColorUIResource(config.getColor("color.desktop", super.getDesktopColor()));

        focusColor = new ColorUIResource(config.getColor("color.focus", super.getFocusColor()));

        highlightedTextColor
            = new ColorUIResource(config.getColor("color.highlightedText", super.getHighlightedTextColor()));

        inactiveControlTextColor
            = new ColorUIResource(config.getColor("color.inactiveControlText",
            super.getInactiveControlTextColor()));

        inactiveSystemTextColor
            = new ColorUIResource(config.getColor("color.inactiveSystemText", super.getInactiveSystemTextColor()));

        menuBackgroundColor
            = new ColorUIResource(config.getColor("color.menuBackground", super.getMenuBackground()));

        menuDisabledForegroundColor
            = new ColorUIResource(config.getColor("color.menuDisabledForeground",
            super.getMenuDisabledForeground()));

        menuForegroundColor
            = new ColorUIResource(config.getColor("color.menuForeground", super.getMenuForeground()));

        menuSelectedBackgroundColor
            = new ColorUIResource(config.getColor("color.menuSelectedBackground",
            super.getMenuSelectedBackground()));

        menuSelectedForegroundColor
            = new ColorUIResource(config.getColor("color.menuSelectedForeground",
            super.getMenuSelectedForeground()));

        primaryControlColor
            = new ColorUIResource(config.getColor("color.primaryControl", super.getPrimaryControl()));

        primaryControlDarkShadowColor
            = new ColorUIResource(config.getColor("color.primaryControlDarkShadow",
            super.getPrimaryControlDarkShadow()));

        primaryControlHighlightColor
            = new ColorUIResource(config.getColor("color.primaryControlHighlight",
            super.getPrimaryControlHighlight()));

        primaryControlInfoColor
            = new ColorUIResource(config.getColor("color.primaryControlInfo", super.getPrimaryControlInfo()));

        primaryControlShadowColor
            = new ColorUIResource(config.getColor("color.primaryControlShadow",
            super.getPrimaryControlShadow()));

        separatorBackgroundColor
            = new ColorUIResource(config.getColor("color.separatorBackground", super.getSeparatorBackground()));

        separatorForegroundColor
            = new ColorUIResource(config.getColor("color.separatorForeground",
            super.getSeparatorForeground()));

        systemTextColor
            = new ColorUIResource(config.getColor("color.systemText", super.getSystemTextColor()));

        textHighlightColor
            = new ColorUIResource(config.getColor("color.textHighlight",
            super.getTextHighlightColor()));

        userTextColor
            = new ColorUIResource(config.getColor("color.userText", super.getUserTextColor()));

        windowBackgroundColor
            = new ColorUIResource(config.getColor("color.windowBackground",
            super.getWindowBackground()));

        windowTitleBackgroundColor
            = new ColorUIResource(config.getColor("color.windowTitleBackground",
            super.getWindowTitleBackground()));

        windowTitleForegroundColor
            = new ColorUIResource(config.getColor("color.windowTitleForeground",
            super.getWindowTitleForeground()));

        windowTitleInactiveBackgroundColor
            = new ColorUIResource(config.getColor("color.windowTitleInactiveBackground",
            super.getWindowTitleInactiveBackground()));

        windowTitleInactiveForegroundColor
            = new ColorUIResource(config.getColor("color.windowTitleInactiveForeground",
            super.getWindowTitleInactiveForeground()));

        // font resources

        controlTextFont = new FontUIResource(config.getFont("font.controlText", super.getControlTextFont()));
        menuTextFont = new FontUIResource(config.getFont("font.menuText", super.getMenuTextFont()));
        subTextFont = new FontUIResource(config.getFont("font.subText", super.getSubTextFont()));
        systemTextFont = new FontUIResource(config.getFont("font.systemText", super.getSystemTextFont()));
        userTextFont = new FontUIResource(config.getFont("font.userText", super.getUserTextFont()));
        windowTitleFont = new FontUIResource(config.getFont("font.windowTitle", super.getWindowTitleFont()));
    }

}
