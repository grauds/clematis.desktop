package jworkspace.ui.api;

import com.hyperrealm.kiwi.util.plugin.PluginLocator;

import jworkspace.kernel.WorkspacePluginLocator;

/**
 * @author Anton Troshin
 */
public class ViewPluginLocator extends WorkspacePluginLocator {

    /**
     * Construct a new <code>PluginLocator</code> with the specified plugin context.
     */
    public ViewPluginLocator() {
        super();

        addRestrictedPackage("jworkspace.kernel.*");
        addRestrictedPackage("jworkspace.ui.api.*");
    }


    public PluginLocator excludeParentClassLoader(boolean exclude) {
        setExcludeParentClassLoader(exclude);
        return this;
    }
}
