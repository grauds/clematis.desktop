package jworkspace.ui.config;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import jworkspace.ui.WorkspaceGUI;
import lombok.Getter;
import lombok.Setter;
/**
 * Singleton service locator for desktop components.
 * <p>
 * Provides access to the main {@link WorkspaceGUI} and configuration
 * through {@link UIConfig}. This class uses the
 * <a href="https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom">
 * Initialization-on-demand holder idiom</a> to implement a thread-safe,
 * lazy-loaded singleton without explicit synchronization.
 * </p>
 */
@Getter
public class DesktopServiceLocator {

    /**
     * Reference to the workspace GUI. This can be set externally
     * when the workspace is initialized.
     */
    @Setter
    private WorkspaceGUI workspaceGUI;

    /**
     * UI configuration for the application. Automatically initialized.
     */
    private final UIConfig uiConfig = new UIConfig();

    /**
     * Private constructor to prevent external instantiation.
     * <p>
     * Enforces singleton pattern: only this class itself can create
     * an instance.
     * </p>
     */
    private DesktopServiceLocator() {}

    /**
     * Returns the singleton instance of {@link DesktopServiceLocator}.
     * <p>
     * The instance is lazily created the first time this method is called.
     * The JVM guarantees thread-safety during class initialization, so
     * no explicit synchronization is needed.
     * </p>
     *
     * @return the single shared instance of DesktopServiceLocator
     */
    public static DesktopServiceLocator getInstance() {
        // Access the nested InstanceHolder class, which triggers
        // its class initialization and creates the singleton instance
        return InstanceHolder.SERVICE_LOCATOR;
    }

    /**
     * Private static nested class that holds the singleton instance.
     * <p>
     * This is the key to the "Initialization-on-demand holder idiom".
     * The INSTANCE is only created when the JVM loads this class,
     * which happens the first time {@link #getInstance()} is called.
     * This ensures:
     * <ul>
     *     <li>Lazy initialization: instance not created until needed</li>
     *     <li>Thread safety: JVM class initialization is atomic</li>
     *     <li>No explicit synchronization needed</li>
     * </ul>
     * </p>
     */
    private static final class InstanceHolder {
        /**
         * The single instance of {@link DesktopServiceLocator}.
         * <p>
         * JVM ensures this is initialized only once in a thread-safe manner.
         * Access via {@link #getInstance()} only.
         * </p>
         */
        private static final DesktopServiceLocator SERVICE_LOCATOR = new DesktopServiceLocator();
    }
}
