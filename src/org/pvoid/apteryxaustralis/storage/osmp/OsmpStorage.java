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
import android.util.Log;
import android.widget.ArrayAdapter;
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Group;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.types.ITerminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OsmpStorage implements IStorage
{
  private Storage _mStorage;

  private final Comparator<Terminal> _mComparatorById = new Comparator<Terminal>()
  {
    @Override
    public int compare(Terminal o1, Terminal o2)
    {
      return (int) (o1.id() - o2.id());
    }
  };

  public OsmpStorage(Context context)
  {
    _mStorage = new Storage(context.getApplicationContext());
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
/////// Вытащим балансы
      OsmpRequest.getBalances(account,groups);
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
    ArrayList<Terminal> terminals = new ArrayList<Terminal>();
    _mStorage.getTerminals(0,terminals);
    int result = refresh(account);
    Log.d(OsmpStorage.class.getSimpleName(),"Result code: " + result);
    if(result==IStorage.RES_OK)
    {
      Log.d(OsmpStorage.class.getSimpleName(),"Sorting...");
      Collections.sort(terminals,_mComparatorById);
      ArrayList<Terminal> updatedTerminals = new ArrayList<Terminal>();
      _mStorage.getTerminals(0,updatedTerminals);
      for(Terminal updatedTerminal : updatedTerminals)
      {
        Log.d(OsmpStorage.class.getSimpleName(),"Looking for " + updatedTerminal.id());
        int index = Collections.binarySearch(terminals,updatedTerminal,_mComparatorById);
        if(index>-1)
        {
          Terminal terminal = terminals.get(index);
          Log.d(OsmpStorage.class.getSimpleName(),"Old state: " + terminal.State() + " new state: "+updatedTerminal.State());
          if(updatedTerminal.State()!=Terminal.STATE_OK && terminal.State()==Terminal.STATE_OK)
            return RES_OK_TERMINAL_ALARM;
        }
      }
    }

    return result;
  }

  @Override
  public void getGroups(long accountId, List<Group> groups)
  {
    _mStorage.getAgents(accountId, groups);
  }

  @Override
  public void getGroups(List<Group> groups)
  {
    _mStorage.getAgentsActive(groups);
  }

  @Override
  public void getTerminals(long accountId, Group group, ArrayAdapter<ITerminal> terminals)
  {
    ArrayList<Terminal> terminalsList = new ArrayList<Terminal>();
    _mStorage.getTerminals(group.id,terminalsList);
    int adapterIndex = 0;
    while(adapterIndex<terminals.getCount())
    {
//////// Ищем имеющийся в новых
      Terminal current = (Terminal)terminals.getItem(adapterIndex);
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
  public ITerminal getTerminal(long id)
  {
    return _mStorage.getTerminal(id);
  }

  @Override
  public int refresh(Account account)
  {
    ArrayList<Group> groups = new ArrayList<Group>();
    int result = OsmpRequest.checkAccount(account, groups);
    if(result==0)
    {
/////// Вытащим балансы
      OsmpRequest.getBalances(account,groups);
      _mStorage.saveAgents(account.id, groups);
/////// Вытащим сразу терминалы
      ArrayList<Terminal> terminals = new ArrayList<Terminal>();
      if(OsmpRequest.getTerminals(account,terminals)==0)
      {
        /*for(Terminal terminal : terminals)
        {
          terminal.State(Terminal.STATE_OK);
        }*/
        _mStorage.saveTerminals(account.id,terminals);
      }
      return RES_OK;
    }
///////
    if(result>0)
      return RES_ERR_CUSTOM_FIRST - result;
///////
    return result;
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
