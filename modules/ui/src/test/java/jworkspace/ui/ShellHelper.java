package jworkspace.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.jar.Manifest;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginDTO;

import jworkspace.kernel.WorkspacePluginLocator;

/**
 * @author Anton Troshin
 */
public class ShellHelper {

    static final String SHELL_JAR = "shell.jar";
    
    private static final String TEST_SHELL_CLASS_PACKAGE = "jworkspace.ui.";
    private static final String TEST_SHELL_CLASS = "TestShell.class";
    private static final String TEST_SHELL = "jworkspace.ui.TestShell";

    private static final String TEST_SHELL_NAME = "Test shell";
    private static final String TEST_SHELL_DESCRIPTION = "Test shell description";
    private static final String TEST_SHELL_VERSION = "1.0.0";
    private static final String TEST_SHELL_HELP_URL = "http://test.com";
    private static final String TEST_SHELL_ICON = "/dummy/path.png";

    private ShellHelper() {
    }

    static void writePluginJarFile(File testPluginClassPath,
                                   String testPluginClass,
                                   Manifest manifest,
                                   String pluginJar)
            throws IOException {

        WorkspacePluginLocator.writePluginJarFile(testPluginClassPath,
            testPluginClass,
            TEST_SHELL_CLASS_PACKAGE,
            manifest,
            testPluginClassPath,
            pluginJar);
    }

    private static Manifest getManifest() {

        PluginDTO plugin = new PluginDTO(TEST_SHELL_CLASS_PACKAGE + TEST_SHELL_CLASS,
            TEST_SHELL_NAME,
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

    static void preparePlugins(File folder) throws IOException {
        preparePlugins(folder, folder);
    }

    static void preparePlugins(File testPluginClassPath, File target) throws IOException {

        File sourceFile = WorkspacePluginLocator.getPluginFile(testPluginClassPath, TEST_SHELL_CLASS);
        Files.write(sourceFile.toPath(),
            StreamUtils.readStreamToByteArray(ShellsTests.class.getResourceAsStream(TEST_SHELL_CLASS)));
        Manifest manifest = getManifest();

        WorkspacePluginLocator.writePluginJarFile(testPluginClassPath,
            TEST_SHELL_CLASS,
            TEST_SHELL_CLASS_PACKAGE,
            manifest,
            target,
            SHELL_JAR);
    }
}
