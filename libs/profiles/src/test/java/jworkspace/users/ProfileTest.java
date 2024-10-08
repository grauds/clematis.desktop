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
public class ProfileTest {

    private ProfilesManager profilesManager;

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();
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
    public void testFactoryCreation() throws IOException, ProfileOperationException {

        Profile profile = Profile.create("test", "password", "First Name", "Second Name", "test@test.com");
        Profile anotherCopy = Profile.create("test", "password", "First Name", "Second Name", "test@test.com");

        profile.setUserName("test2");
        profilesManager.add(profile);

        //thrown.expect(ProfileOperationException.class);
        profilesManager.add(anotherCopy);
    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
