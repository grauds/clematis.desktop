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

package com.hyperrealm.kiwi.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

/**
 * A class for printing arbitrary components. To print a given component,
 * simply use the class as follows:
 *
 * <pre>
 * ComponentPrinter cp = new ComponentPrinter(someComponent);
 * cp.print();
 * </pre>
 * <p>
 * The <code>print()</code> method may be invoked multiple times.
 *
 * @author <a href="http://www.apl.jhu.edu/~hall/java/">Marty Hall</a>
 * @author John N. Kostaras
 * @author Mark Lindner
 * @since Kiwi 1.3.4
 */

public class ComponentPrinter implements Printable {
    private Component component;

    /**
     * Construct a new <code>ComponentPrinter</code> for printing the specified
     * component.
     *
     * @param component The component to be printed.
     */

    public ComponentPrinter(Component component) {
        this.component = component;
    }

    /**
     * Specify a different component to be printed.
     *
     * @param component The new component.
     */

    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     * Print the component.
     *
     * @throws java.awt.print.PrinterException If an error occurred during
     *                                         printing.
     */

    public void print() throws PrinterException {

        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            printJob.print();
        }
    }

    /**
     * Implementation of the <code>Printable</code> interface; this method
     * should not be called directly.
     */

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex != 0) {
            return (NO_SUCH_PAGE);
        }

        RepaintManager repmgr = RepaintManager.currentManager(component);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        boolean flag = repmgr.isDoubleBufferingEnabled();
        repmgr.setDoubleBufferingEnabled(false);
        component.paint(g2d);
        repmgr.setDoubleBufferingEnabled(flag);

        return (PAGE_EXISTS);
    }

}
