package jworkspace.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.jar.Manifest;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;

import jworkspace.runtime.plugin.WorkspacePluginLocator;

/**
 * @author Anton Troshin
 */
public class ShellHelper {

    static final String SHELL_JAR = "shell.jar";
    static final String CHILD_SHELL_JAR = "child_shell.jar";

    private static final String TEST_SHELL_CLASS = "jworkspace/ui/TestShell.class";
    private static final String TEST_SHELL = "jworkspace.ui.TestShell";

    private static final String TEST_SHELL_NAME = "Test shell";
    private static final String TEST_SHELL_DESCRIPTION = "Test shell description";
    private static final String TEST_SHELL_VERSION = "1.0.0";
    private static final String TEST_SHELL_HELP_URL = "http://test.com";
    private static final String TEST_SHELL_ICON = "/dummy/path.png";

    private static final String CHILD_TEST_SHELL_CLASS = "jworkspace/ui/ChildTestShell.class";
    private static final String CHILD_TEST_SHELL = "jworkspace.ui.ChildTestShell";

    private static final String PLUGIN_LOCATOR_CLASS = "jworkspace/ui/TestPluginLocator.class";

    private static final String CHILD_TEST_SHELL_NAME = "Child test shell";

    private ShellHelper() {
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

        PluginDTO plugin = new PluginDTO(TEST_SHELL_CLASS,
            TEST_SHELL_NAME,
            PluginDTO.PLUGIN_TYPE_ANY,
            TEST_SHELL_DESCRIPTION,
            TEST_SHELL_ICON,
            TEST_SHELL_VERSION,
            TEST_SHELL_HELP_URL);

        return PluginDTO.getManifest(plugin);
    }

    private static Manifest getChildManifest() {

        PluginDTO plugin = new PluginDTO(CHILD_TEST_SHELL_CLASS,
            CHILD_TEST_SHELL_NAME,
            PluginDTO.PLUGIN_TYPE_ANY,
            TEST_SHELL_DESCRIPTION,
            TEST_SHELL_ICON,
            TEST_SHELL_VERSION,
            TEST_SHELL_HELP_URL);

        return PluginDTO.getManifest(plugin);
    }

    static void assertPluginEqualsManifest(Plugin testPlugin) {

        assert testPlugin.getName().equals(TEST_SHELL_NAME);
        assert testPlugin.getDescription().equals(TEST_SHELL_DESCRIPTION);
        assert testPlugin.getClassName().equals(TEST_SHELL);
        assert testPlugin.getVersion().equals(TEST_SHELL_VERSION);
        assert testPlugin.getHelpURL().toString().equals(TEST_SHELL_HELP_URL);
        assert testPlugin.getType().equals(PluginDTO.PLUGIN_TYPE_ANY);
    }

    static void assertPluginEqualsChildManifest(Plugin testPlugin) {

        assert testPlugin.getName().equals(CHILD_TEST_SHELL_NAME);
        assert testPlugin.getDescription().equals(TEST_SHELL_DESCRIPTION);
        assert testPlugin.getClassName().equals(CHILD_TEST_SHELL);
        assert testPlugin.getVersion().equals(TEST_SHELL_VERSION);
        assert testPlugin.getHelpURL().toString().equals(TEST_SHELL_HELP_URL);
        assert testPlugin.getType().equals(PluginDTO.PLUGIN_TYPE_ANY);
    }

    static void preparePlugins(File folder) throws IOException, URISyntaxException {
        preparePlugins(folder, folder);
    }

    private static void preparePlugins(File source, File target) throws IOException, URISyntaxException {

        WorkspacePluginLocator.compile(new File[] {
            new File(Objects.requireNonNull(ShellsTests.class.getResource("TestShell.java")).toURI()),
            new File(Objects.requireNonNull(ShellsTests.class.getResource("ChildTestShell.java")).toURI()),
            new File(Objects.requireNonNull(ShellsTests.class.getResource("TestPluginLocator.java")).toURI()),
        }, source);

        Manifest manifest = getManifest();
        WorkspacePluginLocator.writePluginJarFile(source.toPath().toFile(),
            new String[]{TEST_SHELL_CLASS, PLUGIN_LOCATOR_CLASS},
            manifest,
            target,
            SHELL_JAR);

        manifest = getChildManifest();
        WorkspacePluginLocator.writePluginJarFile(source.toPath().toFile(),
            new String[]{CHILD_TEST_SHELL_CLASS, TEST_SHELL_CLASS},
            manifest,
            target,
            CHILD_SHELL_JAR);
    }
}
