package jworkspace.users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Anton Troshin
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ProfileTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ProfilesManager profilesManager = new ProfilesManager();

    @Before
    public void before() {

    }

    @Test
    public void testSimpleCreation() throws ProfileOperationException, IOException {

        Profile profile = new Profile();

        profile.setUserName("test");
        profile.setEmail("test@test.com");
        profile.setUserFirstName("First Name");
        profile.setUserLastName("Second Name");
        profile.setDescription("Description");

        String newPassword = "password";
        profile.setPassword("", newPassword, newPassword);

        ByteArrayOutputStream inMemoryStream = new ByteArrayOutputStream();
        profile.save(new DataOutputStream(inMemoryStream));

        Profile anotherCopy = new Profile();
        anotherCopy.load(new DataInputStream(new ByteArrayInputStream(inMemoryStream.toByteArray())));

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

        thrown.expect(ProfileOperationException.class);
        profilesManager.add(anotherCopy);

    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testUserProfileEngine() throws ProfileOperationException, IOException {

        Profile profile = new Profile("test", "password", "First Name", "Second Name", "test@test.com");

        UserProfileEngine userProfileEngine = new UserProfileEngine();
        userProfileEngine.addProfile(profile.getUserName());
        userProfileEngine.login(profile.getUserName(), "");

        assert userProfileEngine.getUserName().equals(profile.getUserName());
    }

    @After
    public void after() {

    }
}