package jworkspace.users;

import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jworkspace.kernel.Workspace;
/**
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Workspace.class)
public class LoginTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.delete();
        testFolder.create();

        mockStatic(Workspace.class);
        when(Workspace.getBasePath()).thenReturn(testFolder.getRoot().toPath());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testUserProfileEngine() throws ProfileOperationException, IOException {

        Profile profile = new Profile("test", "password", "First Name", "Second Name", "test@test.com");

        WorkspaceUserManager userManager = WorkspaceUserManager.getInstance();

        // this adds incomplete profile -> to disk
        userManager.addProfile(profile.getUserName(), "password");
        // this selects incomplete profile -> from disk
        userManager.login(profile.getUserName(), "password");
        // incomplete profile is not equals to one in memory
        assert userManager.getUserName().equals(profile.getUserName());
        assert !userManager.getEmail().equals(profile.getEmail());
        assert !userManager.getUserFirstName().equals(profile.getUserFirstName());
        assert !userManager.getUserLastName().equals(profile.getUserLastName());
        // deselect incomplete profile -> to disk
        userManager.logout();
        // save complete to disk
        profile.save(Workspace.getBasePath());
        // selects complete profile -> from disk
        userManager.login(profile.getUserName(), "password");

        assert userManager.getUserName().equals(profile.getUserName());
        assert userManager.getDescription().equals(profile.getDescription());
        assert userManager.ensureCurrentProfilePath(Workspace.getBasePath())
            .equals(profile.getProfilePath(Workspace.getBasePath()));
        assert userManager.getEmail().equals(profile.getEmail());
        assert userManager.getParameters().equals(profile.getParameters());
        assert userManager.getUserFirstName().equals(profile.getUserFirstName());
        assert userManager.getUserLastName().equals(profile.getUserLastName());

        userManager.logout();
        assert !userManager.userLogged();

        userManager.removeProfile(profile.getUserName(), "password");
    }

    @After
    public void after() {
        testFolder.delete();
    }
}