package jworkspace.users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;
/**
 * @author Anton Troshin
 */
public class ProfileTest {

    @Test
    public void create() throws ProfileOperationException, IOException {

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

      // todo  assert anotherCopy.equals(profile);
    }
}