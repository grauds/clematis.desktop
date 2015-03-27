package jworkspace.network.user;
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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import com.sun.media.jsdt.Client;
import com.sun.media.jsdt.AuthenticationInfo;
import com.sun.media.jsdt.event.ClientAdaptor;
import jworkspace.kernel.Workspace;
/**
 * This class is used to represent clients that participate
 * in JSDT sessions.
 */
public class DefaultAgent extends ClientAdaptor implements Client
{
    /**
     * Boolean flag to denote whether if to use an alternative nickname or
     * just a workspace credentials
     */
    static boolean useAltNick = false;
    /**
     * Public constructor
     */
	public DefaultAgent () { }
   /**
    * This method just returns the jw user credentials to automate
    * login procedure into collaboration server.
    */
    public Object authenticate(AuthenticationInfo ai)
    {
        return getAgentName() + "$" + getPassword();
    }

    /**
     * Returns user agent password
     * @return user agent password
     */
    public String getPassword()
    {
        if (useAltNick)
        {
            return UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_PASSWORD);
        }
        else
        {
            /**
             * todo
             */
            byte[] pwd = Workspace.getProfilesEngine().getCipherPassword();
            return new String(pwd);
        }
    }
    /**
     * Returns user agent name
     * @return user agent name
     */
    public static String getAgentName()
    {
        if (useAltNick)
        {
            return UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_NAME);
        }
        else
        {
            return UserInfo.getPreferences().getString(UserConstants.CK_USER_NAME);
        }
    }
    /**
     * Returns name for jsdt interface
     * @return this users name
     */
    public String getName()
    {
        return DefaultAgent.getAgentName();
    }
    /**
     * Is this agent instructed to use alternative credentials?
     * @return true if instructed to use alternative credentials
     */
    public boolean isUseAltNick()
    {
        return useAltNick;
    }
    /**
     * Instruct user agent to use alternative credentials
     * @param useAltNick
     */
    public void setUseAltNick(boolean useAltNick)
    {
        this.useAltNick = useAltNick;
    }
}
