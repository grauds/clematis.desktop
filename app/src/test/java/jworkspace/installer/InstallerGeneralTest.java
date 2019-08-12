package jworkspace.installer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jworkspace.kernel.Workspace;
import jworkspace.kernel.WorkspaceException;
import jworkspace.ui.WorkspaceResourceManager;
/**
 * @author Anton Troshin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Workspace.class)
public class InstallerGeneralTest {

    public static final String ARGUMENT_TEST = "--argument test";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();

        mockStatic(Workspace.class);
        when(Workspace.getBasePath()).thenReturn(testFolder.getRoot().getPath() + File.separator);
        when(Workspace.getResourceManager()).thenReturn(new WorkspaceResourceManager());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testSampleInstallation() throws IOException, InstallationException, WorkspaceException {

        File dataRoot = new File(Workspace.getBasePath());

// create tree data models
        ApplicationDataSource applicationData
            = new ApplicationDataSource(new File(dataRoot, ApplicationDataSource.ROOT));
        LibraryDataSource libraryData = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
        JVMDataSource jvmData = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));

// create jvm node
        JVM jvm = new JVM(jvmData.getRoot(), "current_jvm");
        jvm.setDescription("the jvm this instance of workspace is currently running");
        jvm.setPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        jvm.setVersion(System.getProperty("java.version"));
        jvm.setArguments("-cp %c %m %a");
        jvm.save();

// add jvm node to root
        jvmData.getRoot().add(jvm);

// create library node
        Library testLibrary = new Library(libraryData.getRoot(), "test_library");
        testLibrary.setVersion("1");
        testLibrary.setDescription("test_description");
        testLibrary.setDocs("Sample library docs");
        testLibrary.setPath("./library_dir/lib.jar;./library_dir/optional.jar;./library_dir_2/another_jar.jar");
        testLibrary.setSource("./library_dir/libsrc.jar;");
        testLibrary.save();

// add library node to root
        libraryData.getRoot().add(testLibrary);

// create library node
        Application testApplication = new Application(applicationData.getRoot(), "test_application");
        testApplication.setArchive("./application.jar");
        testApplication.setArguments(ARGUMENT_TEST);
        testApplication.setDescription("test java standalone application");
        testApplication.setDocs("path_to_docs");
        testApplication.setJVM(jvm.getLinkString());
        testApplication.setLibraryList(Collections.singletonList(testLibrary));
        testApplication.setMainClass("jworkspace.testapp");
        testApplication.setSource("path_to_source");
        testApplication.setVersion("1.0.0");
        testApplication.setWorkingDirectory("./");
        testApplication.save();

        Application testApplicationCopy = new Application(applicationData.getRoot(), testApplication.getName());
        testApplicationCopy.load();
        assert testApplication.equals(testApplicationCopy);

// add applications to root
        applicationData.getRoot().add(testApplication);
        applicationData.getRoot().add(testApplicationCopy);
        applicationData.getRoot().save();

        ApplicationDataSource applicationDataSourceCopy
            = new ApplicationDataSource(new File(dataRoot, ApplicationDataSource.ROOT));
        applicationDataSourceCopy.getRoot().load();
        assert applicationData.equals(applicationDataSourceCopy);

        Library testLibraryCopy = new Library(libraryData.getRoot(), testLibrary.getName());
        testLibraryCopy.load();
        assert testLibraryCopy.equals(testLibrary);
        libraryData.getRoot().add(testLibraryCopy);
        libraryData.getRoot().save();

        LibraryDataSource libraryDataCopy = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
        libraryDataCopy.getRoot().load();

        assert libraryData.equals(libraryDataCopy);

        JVM jvmCopy = new JVM(jvmData.getRoot(), jvm.getName());
        jvmCopy.load();
        assert jvm.equals(jvmCopy);
        jvmData.getRoot().add(jvmCopy);
        jvmData.getRoot().save();

        JVMDataSource jvmDataCopy = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));
        libraryDataCopy.getRoot().load();
        assert jvmDataCopy.equals(jvmData);

        WorkspaceInstaller workspaceInstaller = new WorkspaceInstaller();

        workspaceInstaller.setApplicationData(applicationData);
        workspaceInstaller.setLibraryData(libraryData);
        workspaceInstaller.setJvmData(jvmData);

        String[] args = workspaceInstaller.getInvocationArgs(testApplication.getLinkString());

        assert args.length == 6;
        assert Arrays.stream(args).anyMatch(t -> t.contains("library_dir"));
        assert Arrays.stream(args).noneMatch(t -> t.contains(ARGUMENT_TEST));
        assert Arrays.stream(args).anyMatch(t -> t.contains("--argument"));
        assert Arrays.stream(args).anyMatch(t -> t.contains("-cp")
            || t.contains("application.jar"));
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
