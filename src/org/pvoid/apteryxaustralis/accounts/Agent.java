package org.pvoid.apteryxaustralis.accounts;

import android.os.Parcel;

public class Agent
{
  private long _mId;
  private String _mName;
  private String _mPhone;
  private long _mAccountId;

  public Agent(long id, String name, String phone)
  {
    _mId = id;
    _mName = name;
    _mPhone = phone;
  }

  public long getId()
  {
    return _mId;
  }

  public String getName()
  {
    return(_mName);
  }
  
  public void setName(String name)
  {
    _mName = name;
  }
  
  public String getPhone()
  {
    return(_mPhone);
  }
  
  public void setPhone(String phone)
  {
    _mPhone = phone;
  }
  
  public long getAccount()
  {
    return(_mAccountId);
  }
  
  public void setAccount(long accountId)
  {
    _mAccountId = accountId;
  }
  
  public String toString()
  {
    return(_mName);
  }
}
