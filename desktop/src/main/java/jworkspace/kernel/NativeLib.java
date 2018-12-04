package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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
 * Native library.
 * @deprecated should be replaced with some alternatives
 */
public class NativeLib {

    // Native library
    static {
        System.loadLibrary("jw");
    }

    /**
     * Sets working directory for Java Workspace. This feature is essential for launching external Java
     * programms and applets, installed in system. But current JDK version does not provide any means
     * for such operation. This native method, however, bridges this gap.
     */
    public static native boolean setCurrentDir(String dir);
}