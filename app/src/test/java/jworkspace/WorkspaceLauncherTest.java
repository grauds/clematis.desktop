package jworkspace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
public class WorkspaceLauncherTest {

    private static final String LIB_DIR = "lib";
    private static final String ARTIFACT = "jworkspace-2.0.0-SNAPSHOT.jar";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();

        Files.createDirectories(Paths.get(testFolder.getRoot().getAbsolutePath(), LIB_DIR));

        try (OutputStream os = new FileOutputStream(Paths.get(testFolder.getRoot().getAbsolutePath(),
            LIB_DIR, ARTIFACT).toFile())) {

            os.write(1);
        }
    }

    @Test
    public void testCommandLine() {

        String cmd = "java -Djava.library.path="
            + Paths.get(LIB_DIR)
            + " -classpath .:"
            + Paths.get(ARTIFACT);
        Assertions.assertEquals(cmd, ConsoleLauncher.getCommandLine().toString());
    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
