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

package com.hyperrealm.kiwi.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hyperrealm.kiwi.util.Config;

import lombok.EqualsAndHashCode;

/**
 * Persistent configuration object. This class extends
 * <code>com.hyperrealm.kiwi.util.Config</code>, adding convenience methods
 * for saving a property list to a file, and reading a property list from a
 * file.
 *
 * @author Mark Lindner
 * @see java.util.Properties
 * @see com.hyperrealm.kiwi.util.Config
 */
@EqualsAndHashCode(callSuper = true)
public class ConfigFile extends Config {

    private final File file;

    /**
     * Construct a new <code>ConfigFile</code>. Note that the object has to be
     * initialized by  explicitly loading the properties via a call to
     * <code>load()</code>; the constructor does not preload the file.
     *
     * @param file    The <code>File</code> object for this configuration file.
     * @param comment The top-of-file comment (one line).
     */

    public ConfigFile(File file, String comment) {
        super(comment);
        this.file = file;
    }

    /**
     * Construct a new <code>ConfigFile</code> with a default comment. Note that
     * the object has to be initialized by  explicitly loading the properties
     * via a call to <code>load()</code>; the constructor does not preload the
     * file.
     *
     * @param file The <code>File</code> object for this configuration file.
     */

    public ConfigFile(File file) {
        this(file, null);
    }

    /**
     * Load the configuration parameters from the file. Also fires a
     * <code>ChangeEvent</code> to notify listeners that the object
     * (potentially) changed.
     *
     * @throws java.io.FileNotFoundException If the associated file does not
     *                                       exist.
     * @throws java.io.IOException           If the file could not be read.
     * @see #store
     */

    public void load() throws FileNotFoundException, IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            super.load(fin);
            support.fireChangeEvent();
        }
    }

    /**
     * Save the configuration parameters to the file.
     *
     * @throws java.io.IOException If the file could not be written.
     * @see #load
     */

    public void store() throws IOException {
        if (file != null && file.getParentFile() != null) {
            Path path = Paths.get(file.getParentFile().getAbsolutePath());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            try (FileOutputStream fout = new FileOutputStream(file)) {
                super.store(fout, description);
            }
        }
    }

    /**
     * Get the absolute path of this configuration file.
     */

    public String getPath() {
        return file.getAbsolutePath();
    }

}
