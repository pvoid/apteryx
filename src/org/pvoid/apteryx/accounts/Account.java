package org.pvoid.apteryx.accounts;

import org.pvoid.apteryx.Utils;

public class Account
{
  public final Long Id;
  public final String Title;
  public final String Login;
  public final String PasswordHash;
  public final String Terminal;
  
  public Account(Long id, String title, String login,String password,String terminal)
  {
    Id = id;
    Title = title;
    Login = login;
    PasswordHash = password;
    Terminal = terminal;
  }
  public String toString()
  {
    if(Utils.isEmptyString(Title))
      return(Login);
    return(Title);      
  }
}
