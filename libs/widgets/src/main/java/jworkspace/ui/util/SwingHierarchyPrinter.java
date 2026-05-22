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
import java.awt.Component;
import java.awt.Container;

import lombok.extern.java.Log;

@Log
@SuppressWarnings({
    "checkstyle:regexp",
    "checkstyle:MagicNumber",
    "checkstyle:MultipleStringLiterals",
    "checkstyle:HideUtilityClassConstructor"
})
public class SwingHierarchyPrinter {

    /**
     * Prints the component tree structure with opacity states.
     * @param comp The starting component (e.g., your JFrame or KDesktopPane)
     * @param depth The current depth level for indentation formatting
     */
    public static void printHierarchy(Component comp, int depth) {
        // Create visual indentation based on tree depth
        String indent = "  ".repeat(depth);

        // Get the class name and current opacity status
        String className = comp.getClass().getName();
        boolean opaque = comp.isOpaque();

        // Print current component details
        System.out.printf("%s[%s] - Opaque: %b%n", indent, className, opaque);
        // If the component is a container, recursively print its children
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                printHierarchy(child, depth + 1);
            }
        }
    }
}

