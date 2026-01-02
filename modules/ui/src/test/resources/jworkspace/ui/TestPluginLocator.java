package jworkspace.ui;

import com.hyperrealm.kiwi.plugin.PluginLocator;
import jworkspace.runtime.plugin.WorkspacePluginLocator;

/**
 * @author Anton Troshin
 */
public class TestPluginLocator extends WorkspacePluginLocator {

    /**
     * Construct a new <code>PluginLocator</code> with the specified plugin context.
     */
    public TestPluginLocator() {
        super();

        addRestrictedPackage("jworkspace.kernel.*");
        addRestrictedPackage("jworkspace.ui.api.*");
    }


    public PluginLocator excludeParentClassLoader(boolean exclude) {
        setExcludeParentClassLoader(exclude);
        return this;
    }
}
