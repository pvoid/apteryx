package org.pvoid.apteryxaustralis.accounts;

import org.pvoid.apteryxaustralis.Utils;

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
    if(Utils.isEmptyString(_mTitle))
      return(_mLogin);
    return(_mTitle);
  }
}
