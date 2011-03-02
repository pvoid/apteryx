/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryxaustralis.types;

import android.text.TextUtils;

public class Account
{
  private long _mId;
  private String _mTitle;
  private String _mLogin;
  private String _mPasswordHash;
  private long _mTerminal;
  
  public Account(long id, String title, String login,String password,long terminal)
  {
    _mId = id;
    _mTitle = title;
    _mLogin = login;
    _mPasswordHash = password;
    _mTerminal = terminal;
  }

  public Account(long id, String title)
  {
    _mId = id;
    _mTitle = title;
  }

  public Account(long id, String login, String password, long terminal)
  {
    _mId = id;
    _mLogin = login;
    _mPasswordHash = password;
    _mTerminal = terminal;
  }

  public Account(String login, String password, long terminal)
  {
    _mId = -1;
    _mLogin = login;
    _mPasswordHash = password;
    _mTerminal = terminal;
  }

  public long getId()
  {
    return _mId;
  }

  public String getTitle()
  {
    return(_mTitle);
  }
  
  public void setTitle(String title)
  {
    _mTitle = title;
  }
  
  public String getLogin()
  {
    return(_mLogin);
  }
  
  public void setLogin(String login)
  {
    _mLogin = login;
  }
  
  public String getPassword()
  {
    return(_mPasswordHash);
  }
  
  public void setPassword(String password)
  {
    _mPasswordHash = password;
  }
  
  public long getTerminalId()
  {
    return(_mTerminal);
  }
  
  public void setTerminalId(long terminal)
  {
    _mTerminal = terminal;
  }
  
  public String toString()
  {
    if(TextUtils.isEmpty(_mTitle))
      return(_mLogin);
    return(_mTitle);
  }
}
