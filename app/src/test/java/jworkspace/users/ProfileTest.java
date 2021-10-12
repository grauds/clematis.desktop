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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jworkspace.kernel.Workspace;

/**
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"jdk.internal.reflect.*"})
@PrepareForTest(Workspace.class)
public class ProfileTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ProfilesManager profilesManager;

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();

        mockStatic(Workspace.class);
        when(Workspace.getBasePath()).thenReturn(testFolder.getRoot().toPath());
        profilesManager = new ProfilesManager(testFolder.getRoot().toPath());
    }

    @Test
    public void testSimpleCreation() throws ProfileOperationException, IOException {

        Profile profile = new Profile();

        assert profile.getUserName().equals("default");

        profile.setUserName("test");
        profile.setEmail("test@test.com");
        profile.setUserFirstName("First Name");
        profile.setUserLastName("Second Name");
        profile.setDescription("Description");

        String newPassword = "password";
        profile.setPassword("", newPassword, newPassword);
        profile.save(testFolder.getRoot().toPath());

        Profile anotherCopy = new Profile();
        anotherCopy.setUserName("test");
        anotherCopy.load(testFolder.getRoot().toPath());

        assert anotherCopy.equals(profile);

        profilesManager.delete(profile, "password");
        profilesManager.delete(anotherCopy, "password");
        profilesManager.delete(new Profile("root"), "");

        assert profilesManager.getProfilesList().size() == 1;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testFactoryCreation() throws ProfileOperationException, IOException {

        Profile profile = Profile.create("test", "password", "First Name", "Second Name", "test@test.com");
        Profile anotherCopy = Profile.create("test", "password", "First Name", "Second Name", "test@test.com");

        profile.setUserName("test2");
        profilesManager.add(profile);

        //thrown.expect(ProfileOperationException.class);
        profilesManager.add(anotherCopy);
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
