package jworkspace.installer;

import java.io.File;
import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.hyperrealm.kiwi.ui.model.DefaultKTreeModel;
import com.hyperrealm.kiwi.ui.model.ExternalKTreeModel;

import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceResourceManager;
/**
 * @author Anton Troshin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Workspace.class)
public class InstallerGeneralTest {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.delete();
        testFolder.create();

        mockStatic(Workspace.class);
        when(Workspace.getBasePath()).thenReturn(testFolder.getRoot().getPath() + File.separator);
        when(Workspace.getResourceManager()).thenReturn(new WorkspaceResourceManager());
    }

    @Test
    public void testSampleInstallation() throws IOException, InstallationException {

        File dataRoot = new File(Workspace.getBasePath());

        ApplicationDataSource applicationData
            = new ApplicationDataSource(new File(dataRoot, ApplicationDataSource.ROOT));
        DefaultKTreeModel applicationModel = new ExternalKTreeModel<>(applicationData);

        LibraryDataSource libraryData = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
        DefaultKTreeModel libraryModel = new ExternalKTreeModel<>(libraryData);

        JVMDataSource jvmData = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));

        JVM jvm = new JVM(jvmData.getRoot(), "current_jvm");
        jvm.setName("default jvm");
        jvm.setDescription("the jvm this instance of workspace is currently running");
        jvm.setPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        jvm.setVersion(System.getProperty("java.version"));
        jvm.setArguments("-cp %c %m %a");
        jvm.save();

        jvmData.getRoot().add(jvm);

        Library testLibrary = new Library(libraryData.getRoot(), "test_library");

        testLibrary.setVersion("1");
        testLibrary.setDescription("test_description");
    }
}
