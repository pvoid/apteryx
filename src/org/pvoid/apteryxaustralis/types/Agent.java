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

public class Agent
{
  private long _mId;
  private String _mName;
  private String _mPhone;
  private long _mAccountId;
  private long _mUpdateDate;
  private float _mBalance;
  private float _mOverdraft;

  public Agent(long id, String name, String phone)
  {
    _mId = id;
    _mName = name;
    _mPhone = phone;
  }

  public Agent(long id, String name)
  {
    _mId = id;
    _mName = name;
  }

  public Agent(long id, String name, long updateDate)
  {
    _mId = id;
    _mName = name;
    _mUpdateDate = updateDate;
  }

  public Agent(long id, String name, long updateDate, float balance, float overdraft)
  {
    _mId = id;
    _mName = name;
    _mUpdateDate = updateDate;
    _mBalance = balance;
    _mOverdraft = overdraft;
  }

  public Agent()
  {

  }

  public long getId()
  {
    return _mId;
  }

  public void setId(long id)
  {
    _mId = id;
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

  public long getUpdateDate()
  {
    return _mUpdateDate;
  }

  public void setUpdateDate(long updateDate)
  {
    _mUpdateDate = updateDate;
  }

  public float getBalance()
  {
    return _mBalance;
  }

  public void setBalance(float balance)
  {
    _mBalance = balance;
  }

  public float getOverdraft()
  {
    return _mOverdraft;
  }

  public void setOverdraft(float overdraft)
  {
    _mOverdraft = overdraft;
  }
}
