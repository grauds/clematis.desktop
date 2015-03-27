package jworkspace.network.server;
/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
   Authors may be contacted at:
   larsyde@diku.dk
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
 */

public class ServerOperationException extends Exception
{
    public ServerOperationException(String message)
    {
        super(message);
    }
    public ServerOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }
    public ServerOperationException(Throwable cause)
    {
        super(cause);
    }
}
