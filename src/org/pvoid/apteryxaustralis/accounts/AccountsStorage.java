package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;

public class AccountsStorage extends Storage<Account>
{
  private static final AccountsStorage _Storage = new AccountsStorage();
  
  private final static Comparator<Account> _Comparator = new Comparator<Account>()
  {
    @Override
    public int compare(Account object1, Account object2)
    {
      return (int)(object1.Id() - object2.Id());
    }
  };
  
  public static AccountsStorage Instance()
  {
    return(_Storage);
  }

  @Override
  protected String FileName()
  {
    return("accounts.data");
  }

  @Override
  protected Account EmptyItem(long id)
  {
    return(new Account(id));
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

  @Override
  protected Comparator<Account> comparator()
  {
    return _Comparator;
  }
}
