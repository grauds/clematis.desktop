package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Loads a Java class from specified JAR(ZIP) file.
 * This class is useful in Java applications only (by JVM's security reasons).
 * To keep compatibility with JDK 1.1 the implementation is using java.util.zip
 * package instead of java.util.jar from JDK 1.2. So signed JAR files can't be
 * used with this version.
 * Example:
 * <pre>
 * ...
 * try
 * {
 *     MyClass myObj = JARClassLoader.createInstance("mypack.jar", "my.pack.MyClass");
 *     ...
 * }
 * catch (java.io.IOException ex)
 * {
 *     // Error: JAR file not found!
 *     ...
 * }
 * catch (ClassNotFoundException ex)
 * {
 *     // Error: Class not found in the JAR file!
 *     ...
 * }
 * ...
 * OR
 * ...
 * try
 * {
 *     ClassLoader jarLoader = JARClassLoader.createLoader("mypack.jar");
 *     Class       myClass   = jarLoader.loadClass("my.pack.MyClass");
 *     MyClass     myObj     = myClass.newInstance();
 *     ...
 * }
 * catch (java.io.IOException ex)
 * {
 *     // Error: JAR file not found!
 *     ...
 * }
 * catch (ClassNotFoundException ex)
 * {
 *     // Error: Class not found in the JAR file!
 *     ...
 * }
 * ...
 * </pre>
 *
 * @author  Alexey Zhukov
 * @version 1.0, 20-Mar-2000
 * @since   JDK 1.1
 */
public final class JarClassLoader extends ClassLoader
{
    /**
     * JAR files / class loaders.
     */
    private static Hashtable s_loaders = new Hashtable();

    /**
     * JAR classes: class names / classes.
     */
    private Hashtable m_classes = new Hashtable();

    /**
     * JAR file.
     */
    private ZipFile m_jar;

    /**
     * Creates new class loader for a given JAR file.
     * @param in_jar the JAR file name.
     * @exception java.io.IOException if I/O error occured.
     */
    protected JarClassLoader(String in_jar) throws IOException
    {
        try
        {
            m_jar = new ZipFile(in_jar);
        }
        catch (Exception ex)
        {
            throw (new IOException("Can't load " + in_jar +
                                   " file or bad ZIP format."));
        }
    }

    /**
     * Returns new instance for the calss with a given name that is loaded
     * from a given JAR file.
     * @param in_jar the JAR file name.
     * @param in_name the class name. Use '.' symbol as package separator
     * instead of '/'.
     * @exception java.io.IOException if I/O error occured.
     * @exception ClassNotFoundException if the class not found or it can't
     * be instantiated.
     */
    public static Object createInstance(String in_jar, String in_name)
            throws IOException,
            ClassNotFoundException
    {
        Class theClass = loadClass(in_jar, in_name);
        Object theObj = null;

        try
        {
            theObj = theClass.newInstance();
        }
        catch (Exception ex)
        {
            throw (new ClassNotFoundException("Can't instantiate " + in_name +
                                              " class (" + in_jar + ")."));
        }

        return (theObj);
    }

    /**
     * Returns JarClassLoader for a given JAR file.
     * @param in_jar the JAR file name.
     * @return JarClassLoader associated with the given JAR file.
     * @exception java.io.IOException if I/O error occured.
     */
    public static JarClassLoader createLoader(String in_jar) throws IOException
    {
        JarClassLoader ret = null;

        synchronized (s_loaders)
        {
            ret = (JarClassLoader) s_loaders.get(in_jar);
            if (ret == null)
            {
                ret = new JarClassLoader(in_jar);
                s_loaders.put(in_jar, ret);
            }
        }

        return (ret);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * Cleses JAR file associated with this class loader.
     */
    public void finalize() throws Throwable
    {
        try
        {
            m_jar.close();
        }
        catch (Exception ex)
        {
            // Ignore...
        }
    }

    /**
     * Returns a class with a given name.
     * @param in_jar the JAR file name.
     * @param in_name the class name. Use '.' symbol as package separator
     * instead of '/'.
     * @exception java.io.IOException if I/O error occured.
     * @exception ClassNotFoundException if the class not found.
     */
    public static Class loadClass(String in_jar, String in_name)
            throws IOException,
            ClassNotFoundException
    {
        return (createLoader(in_jar).loadClass(in_name));
    }

    /**
     * Resolves the specified name to a Class. This method is called
     * by the virtual machine.
     * @param in_name the name of the desired Class.
     * @param in_resolve true if the Class needs to be resolved.
     * @return the resulting Class, or null if it was not found.
     * @exception ClassNotFoundException if the class loader cannot find
     * a definition for the class.
     */
    protected synchronized Class loadClass(String in_name, boolean in_resolve)
            throws ClassNotFoundException
    {
        if (in_resolve == false && in_name.startsWith("java")) // System class check
        {
            Class theClass = null;
            // Get system class.
            try
            {
                theClass = findSystemClass(in_name);
            }
            catch (ClassNotFoundException ex)
            {
                throw (new ClassNotFoundException("Class " + in_name +
                                                  " not found, bad class format or " +
                                                  "I/O error occured."));
            }
            return (theClass);
        }
        Class theClass = (Class) m_classes.get(in_name);
        if (theClass == null)
        {
            // Get from the JAR file.
            String name = in_name.replace('.', '/') + ".class";
            InputStream is = null;
            try
            {
                ZipEntry zentry = m_jar.getEntry(name);
                is = m_jar.getInputStream(zentry);
                int avail = is.available();
                int zsize = (int) zentry.getSize();
                int size = avail > zsize ? avail : zsize;
                byte[] bytes = new byte[size];
                int inc = 0;
                while ((inc += is.read(bytes, inc, size - inc)) < size) ;

                theClass = defineClass(in_name, bytes, 0, size);
                m_classes.put(in_name, theClass);
            }
            catch (Throwable t)
            {
                // Get system class.
                try
                {
                    theClass = findSystemClass(in_name);
                }
                catch (ClassNotFoundException ex)
                {
                    throw (new ClassNotFoundException("Class " + in_name +
                                                      " not found (" +
                                                      m_jar.getName() +
                                                      "), bad class format or " +
                                                      "I/O error occured."));
                }
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (Exception ex)
                    {
                        // Ignore...
                    }
                }
            }
        }

        if (in_resolve)
            resolveClass(theClass);

        return (theClass);
    }
}