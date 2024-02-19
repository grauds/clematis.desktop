package jworkspace.kernel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Anton Troshin
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"jdk.internal.reflect.*"})
@PrepareForTest(Workspace.class)
public class WorkspaceLauncherTest {

    private static final String LIB_DIR = "lib";
    private static final String ARTIFACT = "jworkspace-2.0.0-SNAPSHOT.jar";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();

        mockStatic(Workspace.class);
        when(Workspace.getBasePath()).thenReturn(testFolder.getRoot().toPath());
        Files.createDirectories(Paths.get(testFolder.getRoot().getAbsolutePath(), LIB_DIR));

        try (OutputStream os = new FileOutputStream(Paths.get(testFolder.getRoot().getAbsolutePath(),
            LIB_DIR, ARTIFACT).toFile())) {

            os.write(1);
        }
    }

    @Test
    public void testCommandLine() {

        String cmd = "java -Djava.library.path="
            + Paths.get(testFolder.getRoot().getAbsolutePath(), LIB_DIR)
            + " -classpath "
            + Paths.get(testFolder.getRoot().getAbsolutePath(), LIB_DIR, ARTIFACT);
        assertEquals(WorkspaceLauncher.getCommandLine().toString(), cmd);
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
