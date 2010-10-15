package org.pvoid.apteryxaustralis.accounts;

import java.io.Serializable;

import org.pvoid.apteryxaustralis.Utils;

public class Account extends Preserved implements Serializable
{
  private static final long serialVersionUID = 799373172157184622L;
  public final String Title;
  public final String Login;
  public final String PasswordHash;
  public final String Terminal;
  
  public Account(long id, String title, String login,String password,String terminal)
  {
    super(id);
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
