package jworkspace.users;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class LoginTest {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.delete();
        testFolder.create();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testUserProfileEngine() throws ProfileOperationException, IOException {

        Profile profile = new Profile("test", "password", "First Name", "Second Name", "test@test.com");
        ProfilesManager userManager = new ProfilesManager(testFolder.getRoot().toPath());

        // this adds incomplete profile -> to disk
        userManager.add(profile);

        assert userManager.getCurrentProfile().getUserName().equals("default");

        // this selects incomplete profile -> from disk
        userManager.login(profile.getUserName(), "password");

        assert userManager.getCurrentProfile().getUserName().equals(profile.getUserName());
        assert userManager.getCurrentProfile().getEmail().equals(profile.getEmail());
        assert userManager.getCurrentProfile().getUserFirstName().equals(profile.getUserFirstName());
        assert userManager.getCurrentProfile().getUserLastName().equals(profile.getUserLastName());

        // deselect incomplete profile -> to disk
        userManager.logout();

        // save complete to disk
        profile.save(testFolder.getRoot().toPath());

        // selects complete profile -> from disk
        userManager.login(profile.getUserName(), "password");

        assert userManager.getCurrentProfile().getUserName().equals(profile.getUserName());
        assert userManager.getCurrentProfile().getDescription().equals(profile.getDescription());

        //assert userManager.ensureCurrentProfilePath(Workspace.getBasePath())
       //     .equals(profile.getProfilePath(Workspace.getBasePath()));

        assert userManager.getCurrentProfile().getEmail().equals(profile.getEmail());
        assert userManager.getCurrentProfile().getParameters().equals(profile.getParameters());
        assert userManager.getCurrentProfile().getUserFirstName().equals(profile.getUserFirstName());
        assert userManager.getCurrentProfile().getUserLastName().equals(profile.getUserLastName());

        userManager.logout();
        assert !userManager.userLogged();

        userManager.removeProfile(profile.getUserName(), "password");
    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
