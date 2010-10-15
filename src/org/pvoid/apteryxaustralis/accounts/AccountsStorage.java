package org.pvoid.apteryxaustralis.accounts;

public class AccountsStorage extends Storage<Account>
{
  private static AccountsStorage _Storage = new AccountsStorage();
  
  public static AccountsStorage Instance()
  {
    return(_Storage);
  }
}
