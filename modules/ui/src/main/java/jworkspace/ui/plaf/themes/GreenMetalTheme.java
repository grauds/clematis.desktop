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
 * @(#)GreenMetalTheme.java	1.7 02/06/13
 */


import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class describes a theme using "green" colors.
 *
 * @author Steve Wilson
 * @version 1.7 06/13/02
 */
public class GreenMetalTheme extends DefaultMetalTheme {

    // greenish colors
    private final ColorUIResource primary1 = new ColorUIResource(51, 102, 51);
    private final ColorUIResource primary2 = new ColorUIResource(102, 153, 102);
    private final ColorUIResource primary3 = new ColorUIResource(153, 204, 153);

    public String getName() {
        return "Emerald";
    }

    protected ColorUIResource getPrimary1() {
        return primary1;
    }

    protected ColorUIResource getPrimary2() {
        return primary2;
    }

    protected ColorUIResource getPrimary3() {
        return primary3;
    }

}
