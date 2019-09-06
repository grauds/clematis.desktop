package jworkspace.ui;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.hyperrealm.kiwi.util.plugin.PluginException;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginDTO;

import jworkspace.ui.api.ViewPluginLocator;
import jworkspace.ui.cpanel.CButton;

/**
 * @author Anton Troshin
 */
public class TestShell implements ITestShell {

    private File testFolder;
    /**
     * Get all Control Panel buttons for this shell
     */
    public CButton[] getButtons() {
        return new CButton[0];
    }

    /**
     * Load shell from disk
     */
    public void load() {
        try {
            Plugin testPlugin = new TestPluginLocator()
                .loadPlugin(TestPluginLocator.getPluginFile(new File(getPath()),
                    ShellHelper.CHILD_SHELL_JAR),
                    PluginDTO.PLUGIN_TYPE_ANY);
            Object obj = testPlugin.newInstance();

            assertNotEquals(ClassLoader.getSystemClassLoader(), obj.getClass().getClassLoader());

            testPlugin.reload();
            assertEquals("jworkspace.ui.ChildTestShell", obj.getClass().getName());
        } catch (PluginException ex) {
            System.out.println(ex);
        }

    }

    /**
     * Reset the state of shell
     */
    public void reset() {

    }

    /**
     * Save all settings to default path
     */
    public void save() throws IOException {

    }

    /**
     * Returns a relative path for saving component data.
     */
    public String getPath() {
        return testFolder.toPath().toString();
    }

    /**
     * Sets a relative path for saving component data.
     *
     * @param path
     */
    public void setPath(String path) {
        testFolder = new File(path);
    }
}
