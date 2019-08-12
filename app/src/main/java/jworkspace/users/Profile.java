package jworkspace.users;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.Config;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.Workspace;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/**
 * This class represents a single user in workspace.
 *
 * @author Anton Troshin
 */
@Data
@EqualsAndHashCode(doNotUseGetters = true, exclude = {"cipherpass", "messageDigest"})
@SuppressWarnings("unused")
class Profile {

    private static final String VAR_CFG = "var.cfg";

    private static final String PWD_DAT = "pwd.dat";

    private static final String PROFILE_DAT = "profile.dat";

    private static final String USERS = "users";

    private static final String PROFILE_PASSWD_CHECK_FAILED = "Profile.passwd.check.failed";
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Profile.class);
    /**
     * Algorithm name to cipher passwords
     */
    private static final String ALGORITHM = "MD5";
    /**
     * User name
     */
    private String userName = "";
    /**
     * Password is encrypted using DMD5
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private byte[] cipherpass = new byte[]{};

    private MessageDigest messageDigest;

    private String userFirstName = "";

    private String userLastName = "";

    private String email = "";

    private final Config parameters = new Config();

    private String description = "";

    /**
     * Empty constructor
     */
    Profile() {
        super();
        setPasswordAndDigest("");
    }

    /**
     * Profile cannot be created directly.
     */
    protected Profile(String userName,
                      String password,
                      String userFirstName,
                      String userLastName,
                      String email) {

        this.userName = userName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.email = email;

        setPasswordAndDigest(password);
    }

    Profile(String name) {
        this(name, "", "", "", "");
    }

    /**
     * Use this static method to create profile.
     *
     * @param userName   java.lang.String
     * @param firstName  java.lang.String
     * @param secondName java.lang.String
     * @param email      java.lang.String
     */
    public static Profile create(String userName, String password,
                                 String firstName, String secondName,
                                 String email)
        throws ProfileOperationException {
        if (userName == null) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString("Profile.userName.null"));
        }

        if (password == null) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString("Profile.passwd.null"));
        }

        return new Profile(userName, password, firstName, secondName, email);
    }

    String getProfileRelativeFolder() throws IOException {

        String path = getProfilePath();

        if (!Files.exists(Paths.get(path))) {
            Files.createDirectories(Paths.get(path));
        }

        return path;
    }

    String getProfilePath() {
        return Workspace.getBasePath() + USERS + File.separator + userName + File.separator;
    }

    /**
     * Set user email (secure public version)
     *
     * @param email java.lang.String
     */
    public void setEmail(String password, String email)
        throws ProfileOperationException {
        if (!checkPassword(password)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILE_PASSWD_CHECK_FAILED));
        }

        this.email = email;
    }

    /**
     * Set user password (secure public version)
     */
    void setPassword(String oldPassword, String newPassword, String confirmPassword)
        throws ProfileOperationException {

        if (!checkPassword(oldPassword)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILE_PASSWD_CHECK_FAILED));
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString("Profile.passwd.confirm.failed"));
        }
        setPassword(newPassword);
    }

    void setPassword(String newPassword) {
        if (this.messageDigest != null) {
            if (newPassword != null) {
                this.cipherpass = this.messageDigest.digest(newPassword.getBytes(StandardCharsets.UTF_8));
            } else {
                this.cipherpass = this.messageDigest.digest("".getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void setPasswordAndDigest(String password) {
        try {
            this.messageDigest = MessageDigest.getInstance(ALGORITHM);
            setPassword(password);
        } catch (Exception e) {
            this.messageDigest = null;
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Set user first name (secure public version)
     *
     * @param userFirstName java.lang.String
     */
    public void setUserFirstName(String password, String userFirstName)
        throws ProfileOperationException {
        if (!checkPassword(password)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILE_PASSWD_CHECK_FAILED));
        }
        this.userFirstName = userFirstName;
    }

    /**
     * Set user last name (secure public version)
     *
     * @param userLastName java.lang.String
     */
    public void setUserLastName(String password, String userLastName)
        throws ProfileOperationException {
        if (!checkPassword(password)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILE_PASSWD_CHECK_FAILED));
        }
        this.userLastName = userLastName;
    }

    /**
     * Load profile from disk
     */
    public void load() throws IOException {

        /*
         * Read user variables
         */
        try (FileInputStream inputFile = new FileInputStream(getProfilePath() + VAR_CFG)) {
            getParameters().load(inputFile);
        } catch (FileNotFoundException ex) {
            LOG.warn("Configuration is not found for " + userName);
        }
        /*
         * Read password
         */
        try (FileInputStream inputFile = new FileInputStream(getProfilePath() + PROFILE_DAT);
             DataInputStream dis = new DataInputStream(inputFile)) {
            /*
             * Read all data
             */
            userName = dis.readUTF();
            userFirstName = dis.readUTF();
            userLastName = dis.readUTF();
            email = dis.readUTF();
            description = dis.readUTF();
            cipherpass = StreamUtils.readStreamToByteArray(dis);
        } catch (FileNotFoundException ex) {
            LOG.warn("Saved data is not found for " + userName);
        }
    }

    /**
     * Save profile on disk
     */
    protected void save() throws IOException {
        /*
         * Write user variables
         */
        try (FileOutputStream outputFile = new FileOutputStream(getProfileRelativeFolder() + VAR_CFG)) {
            getParameters().store(outputFile, "USER VARIABLES");
        }
        /*
         * Write password
         */
        try (FileOutputStream outputFile = new FileOutputStream(getProfileRelativeFolder() + PROFILE_DAT);
             DataOutputStream dos = new DataOutputStream(outputFile)) {

            /*
             * Write all other data
             */
            dos.writeUTF(userName);
            dos.writeUTF(userFirstName);
            dos.writeUTF(userLastName);
            dos.writeUTF(email);
            dos.writeUTF(description);
            dos.write(cipherpass);
        }
    }

    /**
     * Set new user name. We have to rename existing directory.
     */
    public void setUserName(String password, String userName)
        throws ProfileOperationException {

        if (!checkPassword(password)) {
            throw new ProfileOperationException(WorkspaceResourceAnchor.getString(PROFILE_PASSWD_CHECK_FAILED));
        }

        setUserName(userName);
    }

    /**
     * Set user name.
     */
    void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Check whether supplied password is correct.
     */
    boolean checkPassword(String passwordCandidate) {

        if (this.messageDigest != null) {
            return Arrays.equals(this.messageDigest
                .digest(passwordCandidate.getBytes(StandardCharsets.UTF_8)), cipherpass);
        } else {
            return passwordCandidate != null
                && Arrays.equals(passwordCandidate.getBytes(StandardCharsets.UTF_8), cipherpass);
        }
    }
}