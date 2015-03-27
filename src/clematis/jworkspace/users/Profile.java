package jworkspace.users;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.util.WorkspaceUtils;
import kiwi.util.Config;
import kiwi.util.StreamUtils;

import java.io.*;

/**
 * This class represents a single user in
 * workspace.
 */
public class Profile
{
// Profile attributes

    private String userName = "";
    /**
     * Password is encrypted using DES
     * encryption algorythm.
     */
    private byte[] cipherpass = new byte[]{};
// Real name attributes
    private String userFirstName = "";
    private String userLastName = "";
    private String email = "";
// User params
    private Config params = new Config();
// Description
    private String description = "";

    /**
     * Empty constructor
     */
    public Profile()
    {
        super();
    }

    /**
     * Profile cannot be created directly.
     */
    protected Profile(String userName, String password,
                      String userFirstName, String userLastName,
                      String email)
    {
        this.userName = userName;

        byte[] password_bytes = WorkspaceUtils.encrypt(userName, password);
        this.cipherpass = new byte[password_bytes.length];
        System.arraycopy(password_bytes, 0, cipherpass, 0, password_bytes.length);

        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.email = email;
    }

    /**
     * Use this static method to create profile.
     * @param userName java.lang.String
     * @param firstName java.lang.String
     * @param secondName java.lang.String
     * @param email java.lang.String
     */
    public static Profile create(String userName, String password,
                                 String firstName, String secondName,
                                 String email)
            throws ProfileOperationException
    {
        if (userName == null)
            throw new ProfileOperationException
                    (LangResource.getString("Profile.userName.null"));

        if (password == null)
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.null"));

        return new Profile(userName, password, firstName,
                           secondName, email);
    }

    /**
     * Get users email address.
     * @return java.lang.String
     */
    public java.lang.String getEmail()
    {
        return email;
    }

    /**
     * Gets custom user parameter
     */
    public Config getParameters()
    {
        if (params == null)
        {
            params = new Config();
        }
        return params;
    }

    public String getProfileFolder()
    {
        return "users" + File.separator + userName;
    }

    /**
     * Returns encrypted password
     */
    public byte[] getCipherPassword()
    {
        return cipherpass;
    }

    /**
     * Get first name of user
     * @return java.lang.String
     */
    public String getUserFirstName()
    {
        return userFirstName;
    }

    /**
     * Get last name of user
     * @return java.lang.String
     */
    public String getUserLastName()
    {
        return userLastName;
    }

    /**
     * Get user nickname
     * @return java.lang.String
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Get description
     */
    public String getDecription()
    {
        return description;
    }

    /**
     * Set user email (secure public version)
     * @param email java.lang.String
     */
    public void setEmail(String password, String email)
            throws ProfileOperationException
    {
        if (!checkPassword(password))
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.check.failed"));

        this.email = email;
    }

    /**
     * Set user email (nonsecure protected version)
     * @param email java.lang.String
     */
    protected void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Sets description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Set user password (secure public version)
     */
    public void setPassword(String oldPassword, String newPassword,
                            String confirmPassword)
            throws ProfileOperationException
    {
        if (!checkPassword(oldPassword))
        {
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.check.failed"));
        }
        if (!newPassword.equals(confirmPassword))
        {
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.confirm.failed"));
        }
        byte[] password = WorkspaceUtils.encrypt(userName, newPassword);
        this.cipherpass = new byte[password.length];
        System.arraycopy(password, 0, cipherpass, 0, password.length);
    }

    /**
     * Set user first name (secure public version)
     * @param userFirstName java.lang.String
     */
    public void setUserFirstName(String password, String userFirstName)
            throws ProfileOperationException
    {
        if (!checkPassword(password))
        {
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.check.failed"));
        }
        this.userFirstName = userFirstName;
    }

    /**
     * Set user first name (nonsecure protected version)
     * @param userFirstName java.lang.String
     */
    protected void setUserFirstName(String userFirstName)
    {
        this.userFirstName = userFirstName;
    }

    /**
     * Set user last name (secure public version)
     * @param userLastName java.lang.String
     */
    public void setUserLastName(String password, String userLastName)
            throws ProfileOperationException
    {
        if (!checkPassword(password))
        {
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.check.failed"));
        }
        this.userLastName = userLastName;
    }

    /**
     * Set user last name (nonsecure protected version)
     * @param userLastName java.lang.String
     */
    protected void setUserLastName(String userLastName)
    {
        this.userLastName = userLastName;
    }

    /**
     * Load profile from disk
     */
    public void load(DataInputStream is) throws FileNotFoundException, IOException
    {
        /**
         * Read all data
         */
        userName = is.readUTF();
        userFirstName = is.readUTF();
        userLastName = is.readUTF();
        email = is.readUTF();
        description = is.readUTF();
        /**
         * Read user variables
         */
        FileInputStream inputFile = new FileInputStream(
                System.getProperty("user.dir") + File.separator +
                getProfileFolder() + File.separator + "var.cfg");
        getParameters().load(inputFile);
        inputFile.close();
        /**
         * Read password
         */
        inputFile = new FileInputStream(
                System.getProperty("user.dir") + File.separator +
                getProfileFolder() + File.separator + "pwd.dat");
        DataInputStream dis = new DataInputStream(inputFile);
        cipherpass = StreamUtils.readStreamToByteArray(dis);
        dis.close();
        inputFile.close();
    }

    /**
     * Save profile on disk
     */
    public void save(DataOutputStream os)
            throws IOException
    {
        /**
         * Write user variables
         */
        FileOutputStream outputFile = new FileOutputStream(
                System.getProperty("user.dir") + File.separator +
                getProfileFolder() + File.separator + "var.cfg");
        getParameters().store(outputFile, "USER VARIABLES");
        outputFile.close();
        /**
         * Write all other data
         */
        os.writeUTF(userName);
        os.writeUTF(userFirstName);
        os.writeUTF(userLastName);
        os.writeUTF(email);
        os.writeUTF(description);
        /**
         * Write password
         */
        outputFile = new FileOutputStream(
                System.getProperty("user.dir") + File.separator +
                getProfileFolder() + File.separator + "pwd.dat");
        DataOutputStream dos = new DataOutputStream(outputFile);
        dos.write(cipherpass);
        dos.close();
        outputFile.close();
    }

    /**
     * Set new user name. We have to rename existing directory.
     */
    public boolean setUserName(String password, String userName)
            throws ProfileOperationException
    {
        if (!checkPassword(password))
        {
            throw new ProfileOperationException
                    (LangResource.getString("Profile.passwd.check.failed"));
        }

        if (this.userName.equals(userName)) return true;
        /**
         * Rename before
         */
        String old_path = System.getProperty("user.dir") + File.separator +
                "users" + File.separator + this.userName;
        String new_path = System.getProperty("user.dir") + File.separator +
                "users" + File.separator + userName;

        if (new File(old_path).renameTo(new File(new_path)))
        {
            this.userName = userName;
            /**
             * Change system user path
             */
            System.setProperty("user.home", new_path);
            Workspace.getLogger().info(">" + "Changing user home path to"
                              + " " + System.getProperty("user.home"));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Set user name.
     */
    protected boolean setUserName(String userName)
    {
        /**
         * Do not make any changes if names are equal
         */
        if (this.userName.equals(userName))
        {
            return true;
        }
        /**
         * Rename directories with user profile.
         */
        else if (!getUserName().trim().equals(""))
        {
            String old_path = System.getProperty("user.dir") + File.separator +
                    "users" + File.separator + this.userName;
            String new_path = System.getProperty("user.dir") + File.separator +
                    "users" + File.separator + userName;
            if (!(new File(old_path).renameTo(new File(new_path))))
            {
                return false;
            }
        }
        /**
         * If an old name was a senseless thing (i.e. profile was created
         * for the first time) just make a directories
         */
        else if (getUserName().trim().equals(""))
        {
            String new_path = System.getProperty("user.dir") + File.separator +
                    "users" + File.separator + userName;
            File userDir = new File(new_path);
            userDir.mkdirs();
        }
        /**
         * Finally set user name
         */
        this.userName = userName;
        /**
         * Change system user path
         */
        System.setProperty("user.home", System.getProperty("user.dir") +
                                        File.separator + "users" + File.separator + this.userName);
        Workspace.getLogger().info(">" + "Changing user home path to"
                          + " " + System.getProperty("user.home"));
        return true;
    }

    /**
     * Check whether supplied password is correct.
     */
    public boolean checkPassword(String passwordCandidate)
    {
        byte[] test = WorkspaceUtils.encrypt(userName, passwordCandidate);

        if (test.length != cipherpass.length) return false;

        for (int i = 0; i < Math.min(test.length, cipherpass.length); i++)
        {
            if (test[i] != cipherpass[i])
            {
                return false;
            }
        }

        return true;
    }
}