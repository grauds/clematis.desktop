package jworkspace.ui.plaf.themes;
/*
 * Copyright (c) 2002 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)DemoMetalTheme.java	1.9 02/06/13
 */


import java.awt.Font;

import javax.swing.UIDefaults;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalIconFactory;

/**
 * This class describes a theme using large fonts.
 * It's great for giving demos of your software to a group
 * where people will have trouble seeing what you're doing.
 *
 * @author Steve Wilson
 * @version 1.9 06/13/02
 */
public class DemoMetalTheme extends DefaultMetalTheme {

    private static final String DIALOG_FONT_FACE = "Dialog";
    private final FontUIResource controlFont = new FontUIResource(DIALOG_FONT_FACE, Font.BOLD, 18);
    private final FontUIResource systemFont = new FontUIResource(DIALOG_FONT_FACE, Font.PLAIN, 18);
    private final FontUIResource userFont = new FontUIResource("SansSerif", Font.PLAIN, 18);
    private final FontUIResource smallFont = new FontUIResource(DIALOG_FONT_FACE, Font.PLAIN, 14);

    public String getName() {
        return "Presentation";
    }

    public FontUIResource getControlTextFont() {
        return controlFont;
    }

    public FontUIResource getSystemTextFont() {
        return systemFont;
    }

    public FontUIResource getUserTextFont() {
        return userFont;
    }

    public FontUIResource getMenuTextFont() {
        return controlFont;
    }

    public FontUIResource getWindowTitleFont() {
        return controlFont;
    }

    public FontUIResource getSubTextFont() {
        return smallFont;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);

        final int internalFrameIconSize = 22;
        table.put("InternalFrame.closeIcon", MetalIconFactory.getInternalFrameCloseIcon(internalFrameIconSize));
        table.put("InternalFrame.maximizeIcon", MetalIconFactory.getInternalFrameMaximizeIcon(internalFrameIconSize));
        table.put("InternalFrame.iconifyIcon", MetalIconFactory.getInternalFrameMinimizeIcon(internalFrameIconSize));
        table.put("InternalFrame.minimizeIcon",
            MetalIconFactory.getInternalFrameAltMaximizeIcon(internalFrameIconSize));

        table.put("ScrollBar.width", 21);


    }

}
