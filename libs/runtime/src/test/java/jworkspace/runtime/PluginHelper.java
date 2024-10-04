package jworkspace.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.jar.Manifest;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;

/**
 * @author Anton Troshin
 */
public class PluginHelper {

    static final String PLUGIN_JAR = "plugin.jar";
    static final String PLUGIN_JAR_2 = "plugin2.jar";
    static final String ANOTHER_PLUGIN_JAR = "another_plugin.jar";

    static final String TEST_PLUGIN_CLASS = "jworkspace/kernel/TestPlugin.class";
    static final String TEST_PLUGIN_CLASS_2 = "jworkspace/kernel/TestPlugin2.class";

    private static final String TEST_PLUGIN_CLASS_PACKAGE = "jworkspace.kernel.";
    private static final String TEST_PLUGIN = "jworkspace.kernel.TestPlugin";

    private static final String TEST_PLUGIN_NAME = "Test plugin";
    private static final String TEST_PLUGIN_DESCRIPTION = "Test plugin description";
    private static final String TEST_PLUGIN_VERSION = "1.0.0";
    private static final String TEST_PLUGIN_HELP_URL = "http://test.com";
    private static final String TEST_PLUGIN_ICON = "/dummy/path.png";

    private static final String TEST_PLUGIN_2 = "jworkspace.kernel.TestPlugin2";
    private static final String TEST_PLUGIN_NAME_2 = "Test plugin 2";
    private static final String TEST_PLUGIN_DESCRIPTION_2 = "Test plugin 2 description";
    private static final String TEST_PLUGIN_VERSION_2 = "2.0.0";
    private static final String TEST_PLUGIN_ICON_2 = "/dummy/path2.png";

    private PluginHelper() {
    }

    static void writePluginJarFile(File testPluginClassPath,
                                   String[] classes,
                                   Manifest manifest,
                                   String pluginJar)
        throws IOException {

        WorkspacePluginLocator.writePluginJarFile(testPluginClassPath,
            classes,
            manifest,
            testPluginClassPath,
            pluginJar);
    }

    private static Manifest getManifest() {

        PluginDTO plugin = new PluginDTO(TEST_PLUGIN_CLASS,
            TEST_PLUGIN_NAME,
            PluginDTO.PLUGIN_TYPE_ANY,
            TEST_PLUGIN_DESCRIPTION,
            TEST_PLUGIN_ICON,
            TEST_PLUGIN_VERSION,
            TEST_PLUGIN_HELP_URL);

        return PluginDTO.getManifest(plugin);
    }

    static Manifest getManifest2() {

        PluginDTO plugin = new PluginDTO(TEST_PLUGIN_CLASS_2,
            TEST_PLUGIN_NAME_2,
            PluginDTO.PLUGIN_TYPE_ANY,
            TEST_PLUGIN_DESCRIPTION_2,
            TEST_PLUGIN_ICON_2,
            TEST_PLUGIN_VERSION_2,
            TEST_PLUGIN_HELP_URL);

        return PluginDTO.getManifest(plugin);
    }

    static void assertPluginEqualsManifest(Plugin testPlugin) {

        assert testPlugin.getName().equals(TEST_PLUGIN_NAME);
        assert testPlugin.getDescription().equals(TEST_PLUGIN_DESCRIPTION);
        assert testPlugin.getClassName().equals(TEST_PLUGIN);
        assert testPlugin.getVersion().equals(TEST_PLUGIN_VERSION);
        assert testPlugin.getHelpURL().toString().equals(TEST_PLUGIN_HELP_URL);
        assert testPlugin.getType().equals(PluginDTO.PLUGIN_TYPE_ANY);
    }

    static void preparePlugins(File folder) throws IOException {
        preparePlugins(folder, folder);
    }

    static void preparePlugins(File source, File target) throws IOException {

        WorkspacePluginLocator.compile(new File[] {
            Paths.get(PluginTests.class.getResource("TestPlugin2.java").getPath()).toFile(),
            Paths.get(PluginTests.class.getResource("TestPlugin.java").getPath()).toFile()
        }, source);

        Manifest manifest = getManifest();
        WorkspacePluginLocator.writePluginJarFile(source.toPath().toFile(),
            new String[]{
                TEST_PLUGIN_CLASS
            },
            manifest,
            target,
            PLUGIN_JAR
        );

        WorkspacePluginLocator.writePluginJarFile(source.toPath().toFile(),
            new String[]{
                TEST_PLUGIN_CLASS, TEST_PLUGIN_CLASS_2
            },
            manifest,
            target,
            ANOTHER_PLUGIN_JAR
        );

        Manifest manifest2 = getManifest2();

        WorkspacePluginLocator.writePluginJarFile(source.toPath().toFile(),
            new String[]{
                TEST_PLUGIN_CLASS_2
            },
            manifest2,
            target,
            PLUGIN_JAR_2
        );
    }

}
