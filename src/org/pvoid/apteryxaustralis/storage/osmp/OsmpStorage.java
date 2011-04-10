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
import android.widget.ArrayAdapter;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;
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
    ArrayList<Group> groups = new ArrayList<Group>();
    int result = OsmpRequest.checkAccount(account, groups);
    if(result==0)
    {
      _mStorage.addAccount(account.id,account.title,account.login,account.passwordHash,account.terminal);
      _mStorage.saveAgents(account.id, groups);
/////// Вытащим сразу терминалы
      ArrayList<Terminal> terminals = new ArrayList<Terminal>();
      if(OsmpRequest.getTerminals(account,terminals)==0)
        _mStorage.saveTerminals(account.id,terminals);
      return RES_OK;
    }
///////
    if(result>0)
      return RES_ERR_CUSTOM_FIRST - result;
///////
    return result;
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
  public void getGroups(long accountId, List<Group> groups)
  {
    _mStorage.getAgents(accountId, groups);
  }

  @Override
  public void getTerminals(long accountId, Group group, ArrayAdapter<Terminal> terminals)
  {
    ArrayList<Terminal> terminalsList = new ArrayList<Terminal>();
    _mStorage.getTerminals(group.id,terminalsList);
    int adapterIndex = 0;
    while(adapterIndex<terminals.getCount())
    {
//////// Ищем имеющийся в новых
      Terminal current = terminals.getItem(adapterIndex);
      boolean found = false;
      for(Terminal terminal : terminalsList)
      {
        if(terminal.id() == current.id())
        {
          current.update(terminal);
          terminalsList.remove(terminal);
          found = true;
          break;
        }
      }
//////// если нашли увеличим индекс, если нет удалим текущий
      if(found)
        ++adapterIndex;
      else
        terminals.remove(current);
    }
//////// то что осталось в списке новое. добавим его.
    for(Terminal terminal : terminalsList)
      {
        terminals.add(terminal);
      }
  }

  @Override
  public boolean isEmpty()
  {
    return !_mStorage.hasAccounts();
  }

  @Override
  public int errorMessage(int errorCode)
  {
    return ErrorCodes.Message(errorCode);
  }
}
