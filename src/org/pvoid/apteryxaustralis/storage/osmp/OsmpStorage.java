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

package org.pvoid.apteryxaustralis.storage.osmp;

import android.content.Context;
import android.widget.Toast;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.net.ErrorCodes;
import org.pvoid.apteryxaustralis.storage.IStorage;

import java.util.ArrayList;
import java.util.List;

public class OsmpStorage implements IStorage
{
  private Context _mContext;
  private Storage _mStorage;

  public OsmpStorage(Context context)
  {
    _mContext = context.getApplicationContext();
    _mStorage = new Storage(_mContext);
  }

  @Override
  public void getAccounts(List<Account> adapter)
  {
    _mStorage.getAccounts(adapter);
  }

  @Override
  public void deleteAccount(long id)
  {
    throw new RuntimeException("Not implemented!");
  }

  @Override
  public int addAccount(Account account)
  {
    ArrayList<Agent> agents = new ArrayList<Agent>();
    int result = OsmpRequest.checkAccount(account,agents);
    if(result==0)
    {
      _mStorage.addAccount(account.id,account.title,account.login,account.passwordHash,account.terminal);
      _mStorage.saveAgents(account.id,agents);
      return RES_OK;
    }
///////
    if(result>0)
      Toast.makeText(_mContext, ErrorCodes.Message(result),Toast.LENGTH_LONG).show();
///////
    return RES_ERR_INVALID_ACCOUNT;
  }

  @Override
  public Account getAccount(long id)
  {
    throw new RuntimeException("Not implemented!");
  }

  @Override
  public int updateAccount(Account account)
  {
    throw new RuntimeException("Not implemented!");
  }

  @Override
  public void getGroups(long accountId, List<Agent> agents)
  {
    _mStorage.getAgents(accountId, agents);
  }

  @Override
  public void getTerminals(long accountId, Agent group, List<Terminal> terminals)
  {
    throw new RuntimeException("Not implemented!");
  }

  @Override
  public boolean isEmpty()
  {
    return !_mStorage.hasAccounts();
  }
}
