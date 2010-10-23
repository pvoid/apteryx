package org.pvoid.apteryxaustralis.accounts;

import org.pvoid.apteryxaustralis.Utils;

public class Account extends Preserved
{
  private static final long serialVersionUID = 3074352127412133550L;
////////
  private String _Title;
  private String _Login;
  private String _PasswordHash;
  private long _Terminal;
  
  public Account(long id)
  {
    super(id);
  }
  
  public Account(long id, String title, String login,String password,long terminal)
  {
    super(id);
    _Title = title;
    _Login = login;
    _PasswordHash = password;
    _Terminal = terminal;
  }
  
  public String getTitle()
  {
    return(_Title);
  }
  
  public void setTitle(String title)
  {
    _Title = title;
  }
  
  public String getLogin()
  {
    return(_Login);
  }
  
  public void setLogin(String login)
  {
    _Login = login;
  }
  
  public String getPassword()
  {
    return(_PasswordHash);
  }
  
  public void setPassword(String password)
  {
    _PasswordHash = password;
  }
  
  public long getTerminalId()
  {
    return(_Terminal);
  }
  
  public void setTerminalId(long terminal)
  {
    _Terminal = terminal;
  }
  
  public String toString()
  {
    if(Utils.isEmptyString(_Title))
      return(_Login);
    return(_Title);      
  }

  @Override
  public <T extends Preserved> void Copy(T another)
  {
    Account item;
    try
    {
      item = (Account)another;
    }
    catch(ClassCastException e)
    {
      e.printStackTrace();
      return;
    }
    ///////
    _Login = item.getLogin();
    _PasswordHash = item.getPassword();
    _Terminal = item.getTerminalId();
  }
}
