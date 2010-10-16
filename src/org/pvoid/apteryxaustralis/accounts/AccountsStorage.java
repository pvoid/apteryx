package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;

import android.content.Context;

public class AccountsStorage extends Storage<Account>
{
  private static final AccountsStorage _Storage = new AccountsStorage();
  
  public static AccountsStorage Instance()
  {
    return(_Storage);
  }

  @Override
  protected String FileName()
  {
    return("accounts.data");
  }
  
  public void ConvertFromOld(Accounts accounts, Context context)
  {
    ArrayList<Account> accountsList = new ArrayList<Account>();
    accounts.GetAccounts(accountsList);
    for(Account account : accountsList)
    {
      AddUnique(account);
    }
    //////
    Serialize(context);
  }
}
